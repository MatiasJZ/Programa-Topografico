import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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

// Panel principal para manejar la situación táctica en un mapa
public class SituacionTactica extends JPanel {

    // Modelo de lista y componente JList para mostrar los blancos
    private DefaultListModel<Blanco> modeloLista;
    private JList<Blanco> listaUI;
    // Lista de objetos Blanco
    protected ArrayList<Blanco> listaDeBlancos;
    // Componente de mapa de OpenStreetMap
    private JMapViewer mapa;
    // ComboBox para elegir fuente de mapa
    private JComboBox<String> comboFuentes;

    // Blanco de referencia para calcular coordenadas polares relativas
    private Blanco blancoReferencia = null;

    // Constructor principal, recibe opcionalmente un archivo MBTiles para el mapa
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

        // Personalización del renderer de la lista para colorear aliados/enemigos
        listaUI.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (value instanceof Blanco) {
                    Blanco b = (Blanco) value;
                    // Color azul para aliados, rojo para enemigos
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

        // ===== BOTONES DE CONTROL =====
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        panelBotones.setBackground(Color.BLACK);
        JButton btnAgregar = new JButton("AGREGAR");
        JButton btnEditar = new JButton("EDITAR");
        JButton btnEliminar = new JButton("ELIMINAR");
        JButton btnActualizar = new JButton("ACTUALIZAR");

        // Colores de botones para diferenciar acciones
        btnAgregar.setBackground(Color.green); btnAgregar.setForeground(Color.WHITE);
        btnEditar.setBackground(Color.blue); btnEditar.setForeground(Color.WHITE);
        btnEliminar.setBackground(Color.orange); btnEliminar.setForeground(Color.WHITE);
        btnActualizar.setBackground(Color.red); btnActualizar.setForeground(Color.WHITE);

        panelBotones.add(btnAgregar); panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar); panelBotones.add(btnActualizar);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        // ===== MAPA =====
        mapa = new JMapViewer();
        mapa.setZoomControlsVisible(true);
        mapa.setAutoscrolls(true);
        mapa.setBackground(Color.DARK_GRAY);

        // Selector de fuente de mapa
        comboFuentes = new JComboBox<>(new String[]{"Satélite","Político"});
        comboFuentes.setSelectedIndex(0);
        JPanel panelArriba = new JPanel();
        panelArriba.setBackground(Color.BLACK);
        panelArriba.add(comboFuentes);
        panelIzquierdo.add(panelArriba, BorderLayout.NORTH);

        // Posición inicial centrada en Argentina
        Coordinate argentina = new Coordinate(-34.6, -58.4);
        int zoomInicial = 5;
        mapa.setTileSource(new SatelliteSource());
        mapa.setDisplayPosition(argentina, zoomInicial);

        // Si se pasa un archivo MBTiles, se utiliza como fuente del mapa
        if (mbtilesFile != null) {
            try {
                MBTilesTileSource tileSource = new MBTilesTileSource(mbtilesFile);
                mapa.setTileSource(tileSource);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error cargando MBTiles: "+ex.getMessage());
            }
        }

        // SplitPane para separar lista de blancos y mapa
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, mapa);
        splitPane.setDividerLocation(250);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // ===== ACCIONES DE LOS BOTONES =====
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar(null,null));
        btnEditar.addActionListener(e -> {
            int idx = listaUI.getSelectedIndex();
            if(idx>=0) mostrarDialogoAgregar(listaDeBlancos.get(idx), null);
            else JOptionPane.showMessageDialog(this,"Seleccione un blanco para editar.");
        });
        btnEliminar.addActionListener(e -> {
            int idx = listaUI.getSelectedIndex();
            if(idx>=0){
                listaDeBlancos.remove(idx);
                modeloLista.remove(idx);
                actualizarBlancosEnMapa();
            }
        });
        btnActualizar.addActionListener(e -> actualizarBlancosEnMapa());

        // Doble click en la lista abre diálogo de edición
        listaUI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt){
                if(evt.getClickCount()==2){
                    int idx = listaUI.locationToIndex(evt.getPoint());
                    if(idx>=0) mostrarDialogoAgregar(listaDeBlancos.get(idx), null);
                }
            }
        });

        // Click en el mapa para agregar nuevo blanco
        mapa.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e){
                if(e.getButton()==java.awt.event.MouseEvent.BUTTON1){
                    Point punto = e.getPoint();
                    ICoordinate icoord = mapa.getPosition(punto);
                    Coordinate coord = new Coordinate(icoord.getLat(), icoord.getLon());
                    mostrarDialogoAgregar(null, coord);
                }
            }
        });

        // Cambiar fuente de mapa sin perder la posición actual
        comboFuentes.addActionListener(ev -> {
            Coordinate centro = (Coordinate) mapa.getPosition(mapa.getWidth()/2, mapa.getHeight()/2);
            int zoom = mapa.getZoom();
            if("Satélite".equals(comboFuentes.getSelectedItem())) mapa.setTileSource(new SatelliteSource());
            else mapa.setTileSource(new OsmTileSource.Mapnik());
            mapa.setDisplayPosition(centro, zoom);
        });
    }

    // Actualiza todos los marcadores en el mapa según la lista de blancos
    private void actualizarBlancosEnMapa(){
        mapa.removeAllMapMarkers();
        for(Blanco b: listaDeBlancos){
            Coordinate coord = convertirACoordenadaLatLon(b.getCoordenadas());
            MapMarkerDot m = new MapMarkerDot(b.getNombre(), coord);
            m.setBackColor(b.isAliado()?Color.BLUE:Color.RED);
            mapa.addMapMarker(m);
        }
        listaUI.repaint();
    }

    // Convierte coordenadas internas a lat/lon para el mapa
    private Coordinate convertirACoordenadaLatLon(coordenadas c){
        double lat=0, lon=0;
        if(c instanceof coordRectangulares){
            coordRectangulares r = (coordRectangulares)c;
            lat = r.getY(); lon = r.getX();
        } else if(c instanceof coordPolares){
            coordPolares p = (coordPolares)c;
            // Dirección medida desde el norte absoluto
            double x = p.getDistancia()*Math.cos(Math.toRadians(p.getAnguloVertical()))*Math.sin(Math.toRadians(p.getDireccion()));
            double y = p.getDistancia()*Math.cos(Math.toRadians(p.getAnguloVertical()))*Math.cos(Math.toRadians(p.getDireccion()));
            lon = x; lat = y;
        }
        return new Coordinate(lat, lon);
    }

    // Diálogo para agregar o editar un blanco
    private void mostrarDialogoAgregar(Blanco blancoEditar, Coordinate coordInicial){
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame,(blancoEditar==null?"Nuevo Blanco":"Editar Blanco"),true);
        dialog.setSize(520,460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos de texto y placeholders
        JTextField txtNombre = new JTextField();
        JTextField txtNaturaleza = new JTextField();
        addPlaceholder(txtNombre, blancoEditar!=null?blancoEditar.getNombre():"Nombre del Blanco");
        addPlaceholder(txtNaturaleza, blancoEditar!=null?blancoEditar.getNaturaleza():"Naturaleza");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFecha = new JTextField(blancoEditar!=null?blancoEditar.getFechaDeActualizacion():dtf.format(LocalDateTime.now()));

        gbc.gridx=0; gbc.gridy=0; dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx=1; dialog.add(txtNombre, gbc);
        gbc.gridx=0; gbc.gridy=1; dialog.add(new JLabel("Naturaleza:"), gbc);
        gbc.gridx=1; dialog.add(txtNaturaleza, gbc);
        gbc.gridx=0; gbc.gridy=2; dialog.add(new JLabel("Fecha:"), gbc);
        gbc.gridx=1; dialog.add(txtFecha, gbc);

        // Radio buttons para elegir tipo de coordenadas
        JRadioButton rbRect = new JRadioButton("Rectangulares");
        JRadioButton rbPol = new JRadioButton("Polares");
        ButtonGroup group = new ButtonGroup();
        group.add(rbRect); group.add(rbPol);
        if(blancoEditar!=null && blancoEditar.getCoordenadas() instanceof coordPolares) rbPol.setSelected(true);
        else rbRect.setSelected(true);

        gbc.gridx=0; gbc.gridy=3; dialog.add(new JLabel("Tipo de coordenadas:"), gbc);
        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radios.add(rbRect); radios.add(rbPol);
        gbc.gridx=1; dialog.add(radios, gbc);

        // Campos para coordenadas (x, y, cota / dirección, distancia, ángulo vertical)
        JTextField campo1 = new JTextField();
        JTextField campo2 = new JTextField();
        JTextField campo3 = new JTextField();

        // Si se edita un blanco existente, rellenar los campos
        if(blancoEditar!=null){
            coordenadas c = blancoEditar.getCoordenadas();
            if(c instanceof coordRectangulares){
                coordRectangulares r = (coordRectangulares)c;
                campo1.setText(String.valueOf(r.getX()));
                campo2.setText(String.valueOf(r.getY()));
                campo3.setText(String.valueOf(r.getCota()));
            }
        } else if(coordInicial!=null && rbRect.isSelected()){
            campo1.setText(String.valueOf(coordInicial.getLon()));
            campo2.setText(String.valueOf(coordInicial.getLat()));
            campo3.setText("0");
        }

        gbc.gridx=0; gbc.gridy=4; dialog.add(new JLabel("X / Dirección:"), gbc);
        gbc.gridx=1; dialog.add(campo1, gbc);
        gbc.gridx=0; gbc.gridy=5; dialog.add(new JLabel("Y / Distancia:"), gbc);
        gbc.gridx=1; dialog.add(campo2, gbc);
        gbc.gridx=0; gbc.gridy=6; dialog.add(new JLabel("COTA / Ángulo Vertical:"), gbc);
        gbc.gridx=1; dialog.add(campo3, gbc);

        // Cambia comportamiento al elegir coordenadas polares o rectangulares
        rbPol.addActionListener(ev -> {
            campo1.setText(""); campo2.setText(""); campo3.setText("");
            if(blancoEditar!=null) blancoReferencia = blancoEditar;
        });
        rbRect.addActionListener(ev -> {
            if(coordInicial!=null){
                campo1.setText(String.valueOf(coordInicial.getLon()));
                campo2.setText(String.valueOf(coordInicial.getLat()));
                campo3.setText("0");
            }
            blancoReferencia = null;
        });

        // Checkbox para indicar si es aliado
        JCheckBox chkAliado = new JCheckBox("Aliado");
        if(blancoEditar!=null) chkAliado.setSelected(blancoEditar.isAliado());
        else chkAliado.setSelected(true);

        gbc.gridx=0; gbc.gridy=7; dialog.add(new JLabel("Tipo:"), gbc);
        gbc.gridx=1; dialog.add(chkAliado, gbc);

        // Botones aceptar y cancelar
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        gbc.gridx=0; gbc.gridy=8; dialog.add(btnAceptar, gbc);
        gbc.gridx=1; gbc.gridy=8; dialog.add(btnCancelar, gbc);

        // Acción de aceptar: valida campos, crea o actualiza blanco y actualiza mapa
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
                    double x = Double.parseDouble(campo1.getText().trim());
                    double y = Double.parseDouble(campo2.getText().trim());
                    double cota = Double.parseDouble(campo3.getText().trim());
                    coords = new coordRectangulares(x,y,cota);
                } else {
                    if(blancoReferencia==null){
                        JOptionPane.showMessageDialog(dialog,"Seleccione un blanco de referencia para coordenadas polares","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    double dir = Double.parseDouble(campo1.getText().trim());
                    double dist = Double.parseDouble(campo2.getText().trim());
                    double ang = Double.parseDouble(campo3.getText().trim());
                    
                    // Coordenadas polares: se calculan relativas al blanco de referencia
                    // con dirección medida desde el norte absoluto, y se asignan como enemigos
                    Coordinate refCoord = convertirACoordenadaLatLon(blancoReferencia.getCoordenadas());
                    double x = refCoord.getLon() + dist*Math.cos(Math.toRadians(ang))*Math.sin(Math.toRadians(dir));
                    double y = refCoord.getLat() + dist*Math.cos(Math.toRadians(ang))*Math.cos(Math.toRadians(dir));
                    coords = new coordRectangulares(x,y,0);
                    chkAliado.setSelected(false);
                }

                // Crear nuevo blanco o actualizar existente
                if(blancoEditar==null || rbPol.isSelected()){
                    Blanco nuevo = new Blanco(nombre, coords, naturaleza, fecha, chkAliado.isSelected());
                    listaDeBlancos.add(nuevo);
                    modeloLista.addElement(nuevo);
                } else {
                    blancoEditar.setNombre(nombre);
                    blancoEditar.setNaturaleza(naturaleza);
                    blancoEditar.setFecha(fecha);
                    blancoEditar.setCoordenadas(coords);
                    blancoEditar.setAliado(chkAliado.isSelected());
                    listaUI.repaint();
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

    // Añade un placeholder a un JTextField con cambio de color al enfocar
    private void addPlaceholder(JTextField field, String placeholder){
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter(){
            @Override
            public void focusGained(FocusEvent e){
                if(field.getText().equals(placeholder)){
                    field.setText(""); field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                if(field.getText().isEmpty()){
                    field.setForeground(Color.GRAY); field.setText(placeholder);
                }
            }
        });
    }
}
