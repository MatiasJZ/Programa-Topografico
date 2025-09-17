import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Locale;
import javax.swing.*;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

public class SituacionTactica extends JPanel {

    private DefaultListModel<Blanco> modeloLista;
    private JList<Blanco> listaUI;
    protected LinkedList<Blanco> listaDeBlancos;
    private JMapViewer mapa;

    // ====== Formato numérico locale-aware (Argentina) ======
    private static final Locale LOCALE_AR = new Locale("es", "AR");
    private static final DecimalFormatSymbols DFS_AR = DecimalFormatSymbols.getInstance(LOCALE_AR);
    static {
        DFS_AR.setDecimalSeparator(',');
        DFS_AR.setGroupingSeparator('.');
    }
    private static final DecimalFormat DF = new DecimalFormat("0.000000", DFS_AR);

    public SituacionTactica(LinkedList<Blanco> listaDeBlancos) {
        setSize(900, 600);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // ===== LISTA DE BLANCOS =====
        this.listaDeBlancos = listaDeBlancos;
        modeloLista = new DefaultListModel<>();
        listaUI = new JList<>(modeloLista);
        listaUI.setFont(new Font("Arial", Font.BOLD, 20));
        listaUI.setBackground(Color.BLACK);

        listaUI.setCellRenderer(new DefaultListCellRenderer() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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

        gbc.gridx = 0; gbc.gridy = 0; panelBotones.add(btnAgregar, gbc);
        gbc.gridx = 1; panelBotones.add(btnEliminar, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panelBotones.add(btnActualizar, gbc);

        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        // ===== MAPA =====
        mapa = new JMapViewer();
        mapa.setZoomControlsVisible(true);
        mapa.setAutoscrolls(true);
        mapa.setBackground(Color.DARK_GRAY);
        mapa.setDisplayPosition(new Coordinate(-38.92, -61.90),0);
        mapa.setTileSource(new LocalXYZSource());                          
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, mapa);
        splitPane.setDividerLocation(250);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // ===== ACCIONES =====
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar(null, null));
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
            @Override public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopup(e); }
            @Override public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopup(e); }
            private void mostrarPopup(MouseEvent e) {
                int idx = listaUI.locationToIndex(e.getPoint());
                if(idx>=0){
                    listaUI.setSelectedIndex(idx);
                    popupMenu.show(listaUI, e.getX(), e.getY());
                }
            }
        });

        itemEditar.addActionListener(e -> {
            int idx = listaUI.getSelectedIndex();
            if(idx >= 0) {
                Blanco b = listaDeBlancos.get(idx);
                coordRectangulares rect = (coordRectangulares) b.getCoordenadas();
                mostrarDialogoAgregar(b, rect);
            }
        });

        itemMedir.addActionListener(e -> {
            int idx = listaUI.getSelectedIndex();
            if(idx >= 0) {
                Blanco seleccionado = listaUI.getSelectedValue();
                mostrarDialogoMedir(seleccionado);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un blanco primero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        listaUI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt){
                if(evt.getClickCount()==2){
                    int idx = listaUI.locationToIndex(evt.getPoint());
                    if(idx>=0) {
                        Blanco b = listaDeBlancos.get(idx);
                        coordRectangulares rect = (coordRectangulares) b.getCoordenadas();
                        mostrarDialogoAgregar(b, rect);
                    }
                }
            }
        });

        mapa.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e){
                if(e.getButton()==java.awt.event.MouseEvent.BUTTON1){
                    Point punto = e.getPoint();
                    ICoordinate icoord = mapa.getPosition(punto);
                    double lat = icoord.getLat();
                    double lon = icoord.getLon();
                    coordRectangulares rect = latLonToCoordRectangulares(lat, lon);
                    mostrarDialogoAgregar(null, rect);
                }
            }
        });}

    private void mostrarDialogoMedir(Blanco blanco) {
        if (blanco == null || listaDeBlancos.size() < 2) {
            JOptionPane.showMessageDialog(this, "No hay suficientes blancos para medir.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Medir distancia", true);
        dialog.setSize(300, 170);
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
            Blanco segundo = (Blanco) comboSegundaMedida.getSelectedItem();
            if(segundo != null) {
                Coordinate p1 = rectToCoordinate((coordRectangulares) blanco.getCoordenadas());
                Coordinate p2 = rectToCoordinate((coordRectangulares) segundo.getCoordenadas());
                double distancia = new coordRectangulares(0,0,0).distanciaVincenty(p1.getLat(), p1.getLon(), p2.getLat(), p2.getLon());
                String distanciaStr = String.format("%.2f metros", distancia);
                JOptionPane.showMessageDialog(dialog,
                    "Distancia entre \"" + blanco.getNombre() + "\" y \"" + segundo.getNombre() + "\": " + distanciaStr,
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        dialog.getContentPane().setBackground(new Color(50,50,50));
        dialog.setVisible(true);
    }

    private void actualizarBlancosEnMapa(){
        mapa.removeAllMapMarkers(); // limpiar antes de volver a pintar
        for(Blanco b: listaDeBlancos){
            Coordinate c = coordRectangularesToLatLon(b.getCoordenadas());
            MapMarkerDot m = new MapMarkerDot(b.getNombre(), c);
            m.setBackColor(b.isAliado()?Color.BLUE:Color.RED);
            mapa.addMapMarker(m);
        }
        listaUI.repaint();
    }

    private void mostrarDialogoAgregar(Blanco blancoEditar, coordRectangulares coordInicial){
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, (blancoEditar==null?"Nuevo Blanco":"Editar Blanco"), true);
        dialog.setSize(700,400);
        dialog.setLocationRelativeTo(this);

        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panelDialog);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(); txtNombre.setPreferredSize(new Dimension(250,30));
        JTextField txtNaturaleza = new JTextField(); txtNaturaleza.setPreferredSize(new Dimension(250,30));
        addPlaceholder(txtNombre, blancoEditar!=null?blancoEditar.getNombre():"Nombre del Blanco");
        addPlaceholder(txtNaturaleza, blancoEditar!=null?blancoEditar.getNaturaleza():"Naturaleza");
        txtNombre.setBackground(new Color(70,70,70)); txtNombre.setForeground(Color.WHITE);
        txtNaturaleza.setBackground(new Color(70,70,70)); txtNaturaleza.setForeground(Color.WHITE);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFecha = new JTextField(blancoEditar!=null?blancoEditar.getFechaDeActualizacion():dtf.format(LocalDateTime.now()));
        txtFecha.setPreferredSize(new Dimension(250,30));
        txtFecha.setBackground(new Color(70,70,70)); txtFecha.setForeground(Color.WHITE);

        JLabel lblNombre = new JLabel("Nombre:"); lblNombre.setForeground(Color.WHITE);
        JLabel lblNaturaleza = new JLabel("Naturaleza:"); lblNaturaleza.setForeground(Color.WHITE);
        JLabel lblFecha = new JLabel("Fecha:"); lblFecha.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; panelDialog.add(lblNombre, gbc);
        gbc.gridx=1; panelDialog.add(txtNombre, gbc);
        gbc.gridx=0; gbc.gridy=1; panelDialog.add(lblNaturaleza, gbc);
        gbc.gridx=1; panelDialog.add(txtNaturaleza, gbc);
        gbc.gridx=0; gbc.gridy=2; panelDialog.add(lblFecha, gbc);
        gbc.gridx=1; panelDialog.add(txtFecha, gbc);

        JTextField campoX = new JTextField(); campoX.setPreferredSize(new Dimension(250,30));
        JTextField campoY = new JTextField(); campoY.setPreferredSize(new Dimension(250,30));
        JTextField campoZ = new JTextField(); campoZ.setPreferredSize(new Dimension(250,30));
        campoX.setBackground(new Color(70,70,70)); campoX.setForeground(Color.WHITE);
        campoY.setBackground(new Color(70,70,70)); campoY.setForeground(Color.WHITE);
        campoZ.setBackground(new Color(70,70,70)); campoZ.setForeground(Color.WHITE);

        if(blancoEditar!=null){
            coordRectangulares stored = (coordRectangulares) blancoEditar.getCoordenadas();
            coordRectangulares rect = isLonLat(stored) ? latLonToCoordRectangulares(stored.getY(), stored.getX()) : stored;
            campoX.setText(DF.format(rect.getX()));
            campoY.setText(DF.format(rect.getY()));
            campoZ.setText(DF.format(rect.getCota()));
        } else if(coordInicial!=null){
            campoX.setText(DF.format(coordInicial.getX()));
            campoY.setText(DF.format(coordInicial.getY()));
            campoZ.setText(DF.format(0));
        } else {
            campoX.setText(DF.format(0)); campoY.setText(DF.format(0)); campoZ.setText(DF.format(0));
        }

        JLabel lblCampoX = new JLabel("X (m)"); lblCampoX.setForeground(Color.WHITE);
        JLabel lblCampoY = new JLabel("Y (m)"); lblCampoY.setForeground(Color.WHITE);
        JLabel lblCampoZ = new JLabel("Cota"); lblCampoZ.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=3; panelDialog.add(lblCampoX, gbc);
        gbc.gridx=1; panelDialog.add(campoX, gbc);
        gbc.gridx=0; gbc.gridy=4; panelDialog.add(lblCampoY, gbc);
        gbc.gridx=1; panelDialog.add(campoY, gbc);
        gbc.gridx=0; gbc.gridy=5; panelDialog.add(lblCampoZ, gbc);
        gbc.gridx=1; panelDialog.add(campoZ, gbc);

        JCheckBox chkAliado = new JCheckBox("Aliado");
        chkAliado.setBackground(new Color(50,50,50));
        chkAliado.setForeground(Color.WHITE);
        if(blancoEditar!=null) chkAliado.setSelected(blancoEditar.isAliado());
        else chkAliado.setSelected(true);

        JLabel lblTipo = new JLabel("Tipo:"); lblTipo.setForeground(Color.WHITE);
        gbc.gridx=0; gbc.gridy=6; panelDialog.add(lblTipo, gbc);
        gbc.gridx=1; panelDialog.add(chkAliado, gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel panelBotonesDialog = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotonesDialog.setBackground(new Color(50,50,50));
        panelBotonesDialog.add(btnAceptar);
        panelBotonesDialog.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; panelDialog.add(panelBotonesDialog, gbc);

        btnAceptar.addActionListener(ev -> {
            try{
                String nombre = txtNombre.getText().trim();
                String naturaleza = txtNaturaleza.getText().trim();
                String fecha = dtf.format(LocalDateTime.now());
                if(nombre.isEmpty()||naturaleza.isEmpty()){
                    JOptionPane.showMessageDialog(dialog,"Complete todos los campos","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double x = parseDoubleLocale(campoX.getText());
                double y = parseDoubleLocale(campoY.getText());
                double z = parseDoubleLocale(campoZ.getText());
                coordRectangulares coords = new coordRectangulares(x,y,z);

                if(blancoEditar==null){
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
            } catch(ParseException ex){
                JOptionPane.showMessageDialog(dialog,
                    "Formato numérico inválido en X/Y/Z. Usá coma o punto, ej: 42028,404099",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }

    private static double parseDoubleLocale(String s) throws ParseException {
        if (s == null) return 0d;
        s = s.trim().replace(" ", "");
        try {
            Number n = DF.parse(s);
            return n.doubleValue();
        } catch (ParseException ex) {
            String alt = s.replace(',', '.');
            return Double.parseDouble(alt);
        }
    }

    private void addPlaceholder(JTextField field, String placeholder){
        Color placeholderColor = new Color(180,180,180);
        Color textColor = Color.WHITE;
        field.setForeground(placeholderColor);
        field.setText(placeholder);
        field.setBackground(new Color(70,70,70));
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

    // =====================
    // CONVERSIONES INTERNAS
    // =====================

    /** Heurística de compatibilidad: detecta si un coordRectangulares guarda lon/lat antiguos. */
    private static boolean isLonLat(coordRectangulares rect){
        return Math.abs(rect.getX()) <= 180 && Math.abs(rect.getY()) <= 90;
    }

    /** Convierte un coordRectangulares a Coordinate (lat/lon) manejando ambos formatos posibles. */
    private static Coordinate rectToCoordinate(coordRectangulares rect){
        if(isLonLat(rect)){
            // Formato antiguo: X=lon, Y=lat
            return new Coordinate(rect.getY(), rect.getX());
        }
        // Formato nuevo: UTM zona 21S
        return coordRectangularesToLatLon(rect);
    }

    // =====================
    // CONVERSIONES UTM <-> GEOGRÁFICAS
    // =====================

    public static Coordinate coordRectangularesToLatLon(coordenadas coord) {
        final double a = 6378137.0;
        final double f = 1 / 298.257223563;
        final double k0 = 0.9996;
        final double e = Math.sqrt(f * (2 - f));
        final double e1sq = e * e / (1 - e * e);

        final int zone = 21;
        final double lambda0 = Math.toRadians(-183.0 + zone * 6.0);
        double x=0;
        double y=0;
        if(coord instanceof coordRectangulares) {
            coordRectangulares c = (coordRectangulares) coord;
            x = c.getX() - 500000.0;
            y = c.getY() - 10000000.0; // Hemisferio sur
        }
        double M = y / k0;
        double mu = M / (a * (1 - Math.pow(e,2)/4 - 3*Math.pow(e,4)/64 - 5*Math.pow(e,6)/256));

        double phi1 = mu
            + (3*e/2 - 27*Math.pow(e,3)/32) * Math.sin(2*mu)
            + (21*Math.pow(e,2)/16 - 55*Math.pow(e,4)/32) * Math.sin(4*mu)
            + (151*Math.pow(e,3)/96) * Math.sin(6*mu)
            + (1097*Math.pow(e,4)/512) * Math.sin(8*mu);

        double N1 = a / Math.sqrt(1 - Math.pow(e*Math.sin(phi1),2));
        double T1 = Math.pow(Math.tan(phi1),2);
        double C1 = e1sq * Math.pow(Math.cos(phi1),2);
        double R1 = a * (1 - Math.pow(e,2)) / Math.pow(1 - Math.pow(e*Math.sin(phi1),2),1.5);
        double D = x / (N1 * k0);

        double lat = phi1 - (N1*Math.tan(phi1)/R1) * (
            Math.pow(D,2)/2
            - (5 + 3*T1 + 10+C1 - 4*Math.pow(C1,2) - 9*e1sq) * Math.pow(D,4)/24
            + (61 + 90*T1 + 298*C1 + 45*Math.pow(T1,2) - 252*e1sq - 3*Math.pow(C1,2)) * Math.pow(D,6)/720
        );
        lat = Math.toDegrees(lat);

        double lon = lambda0 + (
            D
            - (1 + 2*T1 + C1) * Math.pow(D,3)/6
            + (5 - 2*C1 + 28*T1 - 3*Math.pow(C1,2) + 8*e1sq + 24*Math.pow(T1,2)) * Math.pow(D,5)/120
        ) / Math.cos(phi1);
        lon = Math.toDegrees(lon);

        return new Coordinate(lat, lon);
    }

    public static coordRectangulares latLonToCoordRectangulares(double lat, double lon) {
        final double a = 6378137.0; // Semi-eje mayor (m)
        final double f = 1 / 298.257223563; // Achatamiento
        final double k0 = 0.9996; // factor de escala

        final double lambda0 = Math.toRadians(-183.0 + 21 * 6.0); // Meridiano central zona 21

        double phi = Math.toRadians(lat);
        double lambda = Math.toRadians(lon);
        double e = Math.sqrt(f * (2 - f));
        double N = a / Math.sqrt(1 - Math.pow(e * Math.sin(phi), 2));
        double T = Math.pow(Math.tan(phi), 2);
        double C = Math.pow(e, 2) / (1 - Math.pow(e, 2)) * Math.pow(Math.cos(phi), 2);
        double A = Math.cos(phi) * (lambda - lambda0);

        double M = a * (
            (1 - Math.pow(e, 2)/4 - 3*Math.pow(e, 4)/64 - 5*Math.pow(e, 6)/256) * phi
          - (3*Math.pow(e, 2)/8 + 3*Math.pow(e, 4)/32 + 45*Math.pow(e, 6)/1024) * Math.sin(2*phi)
          + (15*Math.pow(e, 4)/256 + 45*Math.pow(e, 6)/1024) * Math.sin(4*phi)
          - (35*Math.pow(e, 6)/3072) * Math.sin(6*phi)
        );

        double x = k0 * N * (A + (1 - T + C) * Math.pow(A, 3)/6
                + (5 - 18*T + T*T + 72*C - 58*Math.pow(e, 2)/(1 - Math.pow(e, 2))) * Math.pow(A, 5)/120)
                + 500000.0;

        double y = k0 * (M + N * Math.tan(phi) * (Math.pow(A, 2)/2
                + (5 - T + 9*C + 4*C*C) * Math.pow(A, 4)/24
                + (61 - 58*T + T*T + 600*C - 330*Math.pow(e, 2)/(1 - Math.pow(e, 2))) * Math.pow(A, 6)/720));
        y += 10000000.0; // Hemisferio sur

        return new coordRectangulares(x, y, 0);
    }
}
