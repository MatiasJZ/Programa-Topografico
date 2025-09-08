import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

public class SituacionTactica extends JPanel {

    private DefaultListModel<Blanco> modeloLista;
    private JList<Blanco> listaUI;
    protected ArrayList<Blanco> listaDeBlancos;
    private JMapViewer mapa;
    private JComboBox<String> comboFuentes;

    private Blanco blancoReferencia = null;

    public SituacionTactica(File mbtilesFile) {
        setSize(900, 600);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // ===== LISTA DE BLANCOS =====
        listaDeBlancos = new ArrayList<>();
        modeloLista = new DefaultListModel<>();
        listaUI = new JList<>(modeloLista);
        listaUI.setFont(new Font("Arial", Font.BOLD, 20));
        listaUI.setBackground(Color.BLACK);

        listaUI.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (value instanceof Blanco) {
                    Blanco b = (Blanco) value;
                    label.setForeground(b.isAliado() ? new Color(100, 150, 255) : new Color(255, 100, 100));
                }
                return label;
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaUI);
        scrollLista.setPreferredSize(new Dimension(250, 0));
        scrollLista.getViewport().setBackground(Color.BLACK);

        // ===== PANEL IZQUIERDO =====
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBackground(Color.BLACK);
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        // ===== BOTONES =====
        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setBackground(Color.BLACK);

        JButton btnAgregar = new JButton("AGREGAR");
        JButton btnEliminar = new JButton("ELIMINAR");
        JButton btnActualizar = new JButton("ACTUALIZAR");

        btnAgregar.setBackground(Color.green); btnAgregar.setForeground(Color.WHITE);
        btnEliminar.setBackground(Color.orange); btnEliminar.setForeground(Color.WHITE);
        btnActualizar.setBackground(Color.red); btnActualizar.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        // Primera fila: btnAgregar y btnEliminar
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelBotones.add(btnAgregar, gbc);

        gbc.gridx = 1;
        panelBotones.add(btnEliminar, gbc);

        // Segunda fila: btnActualizar centrado
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelBotones.add(btnActualizar, gbc);

        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        // ===== MAPA =====
        mapa = new JMapViewer();
        mapa.setZoomControlsVisible(true);
        mapa.setAutoscrolls(true);
        mapa.setBackground(Color.DARK_GRAY);

        comboFuentes = new JComboBox<>(new String[]{"Satélite","Político"});
        comboFuentes.setSelectedIndex(0);
        JPanel panelArriba = new JPanel();
        panelArriba.setBackground(Color.BLACK);
        panelArriba.add(comboFuentes);
        panelIzquierdo.add(panelArriba, BorderLayout.NORTH);

        Coordinate argentina = new Coordinate(-34.6, -58.4);
        int zoomInicial = 5;
        mapa.setTileSource(new SatelliteSource());
        mapa.setDisplayPosition(argentina, zoomInicial);

        if (mbtilesFile != null) {
            try {
                MBTilesTileSource tileSource = new MBTilesTileSource(mbtilesFile);
                mapa.setTileSource(tileSource);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error cargando MBTiles: "+ex.getMessage());
            }
        }

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, mapa);
        splitPane.setDividerLocation(250);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // ===== ACCIONES =====
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar(null,null));
        
        btnEliminar.addActionListener(e -> {
            int idx = listaUI.getSelectedIndex();
            if(idx>=0){
                listaDeBlancos.remove(idx);
                modeloLista.remove(idx);
                actualizarBlancosEnMapa();
            }
        });
        btnActualizar.addActionListener(e -> actualizarBlancosEnMapa());

        // ===== POPUP MENU PARA LISTA =====
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("EDITAR/MARCAR");
        JMenuItem itemMedir = new JMenuItem("MEDIR");
        popupMenu.add(itemEditar);
        popupMenu.add(itemMedir);

        listaUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopup(e); }

            private void mostrarPopup(MouseEvent e) {
                int idx = listaUI.locationToIndex(e.getPoint());
                if(idx>=0){
                    listaUI.setSelectedIndex(idx);
                    popupMenu.show(listaUI, e.getX(), e.getY());
                }
            }
        });

        // Acción EDITAR/MARCAR
        itemEditar.addActionListener(e -> {
            int idx = listaUI.getSelectedIndex();
            if(idx >= 0) mostrarDialogoAgregar(listaDeBlancos.get(idx), null);
        });

        // Acción MEDIR
        itemMedir.addActionListener(e -> {
            int idx = listaUI.getSelectedIndex();
            if(idx >= 0) {
                Blanco seleccionado = listaUI.getSelectedValue();
                mostrarDialogoMedir(seleccionado);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un blanco primero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Doble click sobre lista para editar
        listaUI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt){
                if(evt.getClickCount()==2){
                    int idx = listaUI.locationToIndex(evt.getPoint());
                    if(idx>=0) mostrarDialogoAgregar(listaDeBlancos.get(idx), null);
                }
            }
        });

        // Click izquierdo en el mapa para agregar
        mapa.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e){
                if(e.getButton()==java.awt.event.MouseEvent.BUTTON1){
                    Point punto = e.getPoint();
                    ICoordinate icoord = mapa.getPosition(punto);
                    double lat = icoord.getLat();
                    double lon = icoord.getLon();
                    Coordinate c = new Coordinate(lat,lon);
                    mostrarDialogoAgregar(null, c);
                }
            }
        });

        comboFuentes.addActionListener(ev -> {
            Coordinate centro = (Coordinate) mapa.getPosition(mapa.getWidth()/2, mapa.getHeight()/2);
            int zoom = mapa.getZoom();
            if("Satélite".equals(comboFuentes.getSelectedItem())) mapa.setTileSource(new SatelliteSource());
            else mapa.setTileSource(new OsmTileSource.Mapnik());
            mapa.setDisplayPosition(centro, zoom);
        });
    }

    private void mostrarDialogoMedir(Blanco blanco) {
        if (blanco == null || listaDeBlancos.size() < 2) {
            JOptionPane.showMessageDialog(this, "No hay suficientes blancos para medir.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Medir distancia", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Blanco> comboSegundaMedida = new JComboBox<>();
        for (Blanco b : listaDeBlancos) {
            if (!b.equals(blanco)) comboSegundaMedida.addItem(b);
        }

        JLabel lblSeleccion = new JLabel("Segundo blanco:");
        lblSeleccion.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(lblSeleccion, gbc);
        gbc.gridx = 1; dialog.add(comboSegundaMedida, gbc);

        JButton btnCalcular = new JButton("Calcular");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        dialog.add(btnCalcular, gbc);

        btnCalcular.addActionListener(ev -> {
            Blanco segundoBlanco = (Blanco) comboSegundaMedida.getSelectedItem();
            if(segundoBlanco != null) {
                double distancia = blanco.getCoordenadas().distanciaA(segundoBlanco.getCoordenadas());
                // Redondeamos a 2 decimales
                String distanciaStr = String.format("%.2f metros", distancia);
                JOptionPane.showMessageDialog(dialog,
                    "Distancia entre \"" + blanco.getNombre() + "\" y \"" + segundoBlanco.getNombre() + "\": " + distanciaStr,
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });


        dialog.getContentPane().setBackground(new Color(50,50,50));
        dialog.setVisible(true);
    }

    private void actualizarBlancosEnMapa(){
        mapa.removeAllMapMarkers();
        for(Blanco b: listaDeBlancos){
            coordRectangulares c = (coordRectangulares) b.getCoordenadas();
            Coordinate coord = convertirACoordenadaLatLon(c); // <-- conversión aquí
            MapMarkerDot m = new MapMarkerDot(b.getNombre(), coord);
            m.setBackColor(b.isAliado()?Color.BLUE:Color.RED);
            mapa.addMapMarker(m);
        }
        listaUI.repaint();
    }

    
    private Coordinate convertirACoordenadaLatLon(coordenadas c){
        double lat=0, lon=0;
        if(c instanceof coordRectangulares){
            coordRectangulares r = (coordRectangulares)c;
            lat = r.getY(); lon = r.getX();
        } else if(c instanceof coordPolares){
            coordPolares p = (coordPolares)c;
            double x = p.getDistancia()*Math.cos(Math.toRadians(p.getAnguloVertical()))*Math.sin(Math.toRadians(p.getDireccion()));
            double y = p.getDistancia()*Math.cos(Math.toRadians(p.getAnguloVertical()))*Math.cos(Math.toRadians(p.getDireccion()));
            lon = x; lat = y;
        }
        return new Coordinate(lat, lon);
    }
    
    public coordRectangulares convertirACoordenadaRectangular(ICoordinate coord) {
        // transforma lat/lon a metros aproximados en Web Mercator
        double R = 6378137; // radio de la Tierra en metros
        double x = Math.toRadians(coord.getLon()) * R;
        double y = Math.log(Math.tan(Math.PI/4 + Math.toRadians(coord.getLat())/2)) * R;
        return new coordRectangulares(x, y, 0);
    }
    
    private void mostrarDialogoAgregar(Blanco blancoEditar, Coordinate coordInicial){
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, (blancoEditar==null?"Nuevo Blanco":"Editar Blanco"), true);
        dialog.setSize(620,360);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal con fondo gris oscuro
        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panelDialog);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos de texto
        JTextField txtNombre = new JTextField();
        JTextField txtNaturaleza = new JTextField();
        addPlaceholder(txtNombre, blancoEditar!=null?blancoEditar.getNombre():"Nombre del Blanco");
        addPlaceholder(txtNaturaleza, blancoEditar!=null?blancoEditar.getNaturaleza():"Naturaleza");

        txtNombre.setBackground(new Color(70,70,70));
        txtNombre.setForeground(Color.WHITE);
        txtNaturaleza.setBackground(new Color(70,70,70));
        txtNaturaleza.setForeground(Color.WHITE);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFecha = new JTextField(blancoEditar!=null?blancoEditar.getFechaDeActualizacion():dtf.format(LocalDateTime.now()));
        txtFecha.setBackground(new Color(70,70,70));
        txtFecha.setForeground(Color.WHITE);

        // Labels con color blanco
        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblNaturaleza = new JLabel("Naturaleza:");
        JLabel lblFecha = new JLabel("Fecha:");
        lblNombre.setForeground(Color.WHITE);
        lblNaturaleza.setForeground(Color.WHITE);
        lblFecha.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; panelDialog.add(lblNombre, gbc);
        gbc.gridx=1; panelDialog.add(txtNombre, gbc);
        gbc.gridx=0; gbc.gridy=1; panelDialog.add(lblNaturaleza, gbc);
        gbc.gridx=1; panelDialog.add(txtNaturaleza, gbc);
        gbc.gridx=0; gbc.gridy=2; panelDialog.add(lblFecha, gbc);
        gbc.gridx=1; panelDialog.add(txtFecha, gbc);

        // Radio buttons
        JRadioButton rbRect = new JRadioButton("Rectangulares (Lat/Lon)");
        rbRect.setSelected(true);
        JRadioButton rbPol = null;
        ButtonGroup group = new ButtonGroup();
        group.add(rbRect);

        rbRect.setBackground(new Color(50,50,50));
        rbRect.setForeground(Color.WHITE);

        JLabel lblTipoCoord = new JLabel("Tipo de coordenadas:");
        lblTipoCoord.setForeground(Color.WHITE);
        gbc.gridx=0; gbc.gridy=3; panelDialog.add(lblTipoCoord, gbc);
        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radios.setBackground(new Color(50,50,50));
        radios.add(rbRect);

        if(blancoEditar!=null){ // solo al editar aparece la opción de polares
            rbPol = new JRadioButton("Marcar punto desde esta posición (polares)");
            rbPol.setBackground(new Color(50,50,50));
            rbPol.setForeground(Color.WHITE);
            group.add(rbPol);
            radios.add(rbPol);
        }
        gbc.gridx=1; panelDialog.add(radios, gbc);

        // Campos coordenadas
        JTextField campo1 = new JTextField();
        JTextField campo2 = new JTextField();
        JTextField campo3 = new JTextField();
        campo1.setBackground(new Color(70,70,70)); campo1.setForeground(Color.WHITE);
        campo2.setBackground(new Color(70,70,70)); campo2.setForeground(Color.WHITE);
        campo3.setBackground(new Color(70,70,70)); campo3.setForeground(Color.WHITE);

        // Rellenar según blanco existente o coordenada inicial
        if(blancoEditar!=null){
            Coordinate c = convertirACoordenadaLatLon(blancoEditar.getCoordenadas());
            campo1.setText(String.valueOf(c.getLon()));
            campo2.setText(String.valueOf(c.getLat()));
            campo3.setText("0");
        } else if(coordInicial!=null){
            campo1.setText(String.valueOf(coordInicial.getLon()));
            campo2.setText(String.valueOf(coordInicial.getLat()));
            campo3.setText("0");
        }

        JLabel lblCampo1 = new JLabel("X / Dirección (°)"); lblCampo1.setForeground(Color.WHITE);
        JLabel lblCampo2 = new JLabel("Y / Distancia (°)"); lblCampo2.setForeground(Color.WHITE);
        JLabel lblCampo3 = new JLabel("Cota / Áng Vertical (°)"); lblCampo3.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=4; panelDialog.add(lblCampo1, gbc);
        gbc.gridx=1; panelDialog.add(campo1, gbc);
        gbc.gridx=0; gbc.gridy=5; panelDialog.add(lblCampo2, gbc);
        gbc.gridx=1; panelDialog.add(campo2, gbc);
        gbc.gridx=0; gbc.gridy=6; panelDialog.add(lblCampo3, gbc);
        gbc.gridx=1; panelDialog.add(campo3, gbc);

        if(rbPol!=null){
            rbPol.addActionListener(ev -> {
                campo1.setText(""); campo2.setText(""); campo3.setText("");
                blancoReferencia = blancoEditar;
            });
        }
        rbRect.addActionListener(ev -> {
            if(coordInicial!=null){
                campo1.setText(String.valueOf(coordInicial.getLon()));
                campo2.setText(String.valueOf(coordInicial.getLat()));
                campo3.setText("0");
            }
            blancoReferencia = null;	
        });

        JCheckBox chkAliado = new JCheckBox("Aliado");
        chkAliado.setBackground(new Color(50,50,50));
        chkAliado.setForeground(Color.WHITE);
        if(blancoEditar!=null) chkAliado.setSelected(blancoEditar.isAliado());
        else chkAliado.setSelected(true);

        JLabel lblTipo = new JLabel("Tipo:"); lblTipo.setForeground(Color.WHITE);
        gbc.gridx=0; gbc.gridy=7; panelDialog.add(lblTipo, gbc);
        gbc.gridx=1; panelDialog.add(chkAliado, gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotonesDialog = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotonesDialog.setBackground(new Color(50,50,50));
        panelBotonesDialog.add(btnAceptar);
        panelBotonesDialog.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        panelDialog.add(panelBotonesDialog, gbc);

        // Acción aceptar
        JRadioButton finalRbPol = rbPol; // copia para usar en lambda
        btnAceptar.addActionListener(ev -> {
            try{
                String nombre = txtNombre.getText().trim();
                String naturaleza = txtNaturaleza.getText().trim();
                String fecha = dtf.format(LocalDateTime.now());
                if(nombre.isEmpty()||naturaleza.isEmpty()){
                    JOptionPane.showMessageDialog(dialog,"Complete todos los campos","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                coordenadas coords;
                if(rbRect.isSelected()){
                    double lat = Double.parseDouble(campo1.getText().trim());
                    double lon = Double.parseDouble(campo2.getText().trim());
                    double cota = Double.parseDouble(campo3.getText().trim());
                    coords = new coordRectangulares(lat, lon, cota);

                    if(blancoEditar==null){
                        // nuevo blanco
                        Blanco nuevo = new Blanco(nombre, coords, naturaleza, fecha, chkAliado.isSelected());
                        listaDeBlancos.add(nuevo);
                        modeloLista.addElement(nuevo);
                    } else {
                        // actualización
                        blancoEditar.setNombre(nombre);
                        blancoEditar.setNaturaleza(naturaleza);
                        blancoEditar.setFecha(fecha);
                        blancoEditar.setCoordenadas(coords);
                        blancoEditar.setAliado(chkAliado.isSelected());
                        listaUI.repaint();
                    }

                } else if(finalRbPol!=null && finalRbPol.isSelected()){
                    if(blancoReferencia==null){
                        JOptionPane.showMessageDialog(dialog,"Seleccione un blanco de referencia para coordenadas polares","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    double dir = Double.parseDouble(campo1.getText().trim());
                    double dist = Double.parseDouble(campo2.getText().trim());
                    double ang = Double.parseDouble(campo3.getText().trim());

                    Coordinate refCoord = convertirACoordenadaLatLon(blancoReferencia.getCoordenadas());
                    double x = refCoord.getLon() + dist*Math.cos(Math.toRadians(ang))*Math.sin(Math.toRadians(dir));
                    double y = refCoord.getLat() + dist*Math.cos(Math.toRadians(ang))*Math.cos(Math.toRadians(dir));
                    coords = new coordRectangulares(x,y,0);

                    // siempre un blanco nuevo
                    Blanco nuevo = new Blanco(nombre, coords, naturaleza, fecha, false);
                    listaDeBlancos.add(nuevo);
                    modeloLista.addElement(nuevo);
                }

                actualizarBlancosEnMapa();
                dialog.dispose();
            } catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(dialog,"Las coordenadas deben ser numéricas","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }

    
    private void addPlaceholder(JTextField field, String placeholder){
        Color placeholderColor = new Color(180,180,180); // gris claro para placeholder
        Color textColor = Color.WHITE; // texto normal en blanco
        field.setForeground(placeholderColor);
        field.setText(placeholder);
        field.setBackground(new Color(70,70,70)); // fondo gris oscuro consistente
        field.addFocusListener(new FocusAdapter(){
            @Override
            public void focusGained(FocusEvent e){
                if(field.getText().equals(placeholder)){
                    field.setText("");
                    field.setForeground(textColor);
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                if(field.getText().isEmpty()){
                    field.setForeground(placeholderColor);
                    field.setText(placeholder);
                }
            }
        });
    }
}