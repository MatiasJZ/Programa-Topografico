import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

public class SituacionTactica extends JPanel {

    private DefaultListModel<String> modeloLista;
    private JList<String> listaUI;
    protected ArrayList<Blanco> listaDeBlancos;
    private JMapViewer mapa;
    private JComboBox<String> comboFuentes;

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
        listaUI.setForeground(Color.GREEN);
        listaUI.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                label.setHorizontalAlignment(SwingConstants.CENTER);
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

        // ===== PANEL BOTONES =====
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        panelBotones.setBackground(Color.BLACK);
        JButton btnAgregar = new JButton("AGREGAR");
        JButton btnEditar = new JButton("EDITAR");
        JButton btnEliminar = new JButton("ELIMINAR");
        JButton btnActualizar = new JButton("ACTUALIZAR");
        btnAgregar.setBackground(Color.green); btnAgregar.setForeground(Color.WHITE);
        btnEditar.setBackground(Color.blue); btnEditar.setForeground(Color.WHITE);
        btnEliminar.setBackground(Color.orange); btnEliminar.setForeground(Color.WHITE);
        btnActualizar.setBackground(Color.red); btnActualizar.setForeground(Color.WHITE);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        // ===== PANEL MAPA =====
        mapa = new JMapViewer();
        mapa.setZoomControlsVisible(true);
        mapa.setAutoscrolls(true);
        mapa.setBackground(Color.DARK_GRAY);

        // JComboBox para fuentes (Satélite y Político)
        comboFuentes = new JComboBox<>(new String[]{"Satélite", "Político"});
        comboFuentes.setSelectedIndex(0);
        JPanel panelArriba = new JPanel();
        panelArriba.setBackground(Color.BLACK);
        panelArriba.add(comboFuentes);
        panelIzquierdo.add(panelArriba, BorderLayout.NORTH);

        // Coordenadas de Buenos Aires
        Coordinate argentina = new Coordinate(-34.6, -58.4);
        int zoomInicial = 5;

        // Inicializar mapa con Satélite
        mapa.setTileSource(new SatelliteSource());
        mapa.setDisplayPosition(argentina, zoomInicial);

        // Cargar MBTiles si existe
        if (mbtilesFile != null) {
            try {
                MBTilesTileSource tileSource = new MBTilesTileSource(mbtilesFile);
                mapa.setTileSource(tileSource);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error cargando MBTiles: " + ex.getMessage());
            }
        }

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, mapa);
        splitPane.setDividerLocation(250);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // ===== ACCIONES DE BOTONES =====
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar(null, null));
        btnEditar.addActionListener(e -> {
            int index = listaUI.getSelectedIndex();
            if (index >= 0) mostrarDialogoAgregar(listaDeBlancos.get(index), null);
            else JOptionPane.showMessageDialog(this, "Seleccione un blanco para editar.");
        });
        btnEliminar.addActionListener(e -> {
            int index = listaUI.getSelectedIndex();
            if (index >= 0) {
                listaDeBlancos.remove(index);
                modeloLista.remove(index);
                actualizarBlancosEnMapa();
            }
        });
        btnActualizar.addActionListener(e -> actualizarBlancosEnMapa());

        // Doble clic en lista = editar
        listaUI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = listaUI.locationToIndex(evt.getPoint());
                    if (index >= 0) mostrarDialogoAgregar(listaDeBlancos.get(index), null);
                }
            }
        });

        // Click en mapa = agregar blanco
        mapa.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    Point punto = e.getPoint();
                    ICoordinate icoord = mapa.getPosition(punto);
                    Coordinate coord = new Coordinate(icoord.getLat(), icoord.getLon());
                    mostrarDialogoAgregar(null, coord);
                }
            }
        });

        // Cambio de fuente de mapa
        comboFuentes.addActionListener(e -> {
            // Guardar centro y zoom
            Coordinate centro = (Coordinate) mapa.getPosition(mapa.getWidth()/2, mapa.getHeight()/2);
            int zoom = mapa.getZoom();

            String seleccion = (String) comboFuentes.getSelectedItem();
            if ("Satélite".equals(seleccion)) {
                mapa.setTileSource(new SatelliteSource());
            } else if ("Político".equals(seleccion)) {
                mapa.setTileSource(new OsmTileSource.Mapnik());
            }

            // Restaurar centro y zoom
            mapa.setDisplayPosition(centro, zoom);
        });
    }

    // ================== ACTUALIZAR BLANCOS EN EL MAPA ==================
    private void actualizarBlancosEnMapa() {
        mapa.removeAllMapMarkers();
        for (Blanco b : listaDeBlancos) {
            Coordinate coord = convertirACoordenadaLatLon(b.getCoordenadas());
            MapMarkerDot marcador = new MapMarkerDot(b.getNombre(), coord);
            marcador.setBackColor(Color.red); 
            mapa.addMapMarker(marcador);
        }
    }

    // ================== CONVERTIR COORDENADAS ==================
    private Coordinate convertirACoordenadaLatLon(coordenadas c) {
        double lat = 0, lon = 0;
        if (c instanceof coordRectangulares) {
            coordRectangulares r = (coordRectangulares) c;
            lat = r.getY(); lon = r.getX();
        } else if (c instanceof coordPolares) {
            coordPolares p = (coordPolares) c;
            double x = p.getDistancia() * Math.cos(Math.toRadians(p.getAnguloVertical())) *
                       Math.cos(Math.toRadians(p.getDireccion()));
            double y = p.getDistancia() * Math.cos(Math.toRadians(p.getAnguloVertical())) *
                       Math.sin(Math.toRadians(p.getDireccion()));
            lat = y; lon = x;
        }
        return new Coordinate(lat, lon);
    }

    // ================== DIÁLOGO AGREGAR/EDITAR BLANCO ==================
    private void mostrarDialogoAgregar(Blanco blancoEditar, Coordinate coordInicial) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, (blancoEditar == null ? "Nuevo Blanco" : "Editar Blanco"), true);
        dialog.setSize(480, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(blancoEditar != null ? blancoEditar.getNombre() : (coordInicial != null ? "Blanco en mapa" : ""));
        JTextField txtNaturaleza = new JTextField(blancoEditar != null ? blancoEditar.getNaturaleza() : "");
        JTextField txtFecha = new JTextField(blancoEditar != null ? blancoEditar.getFechaDeActualizacion() : java.time.LocalDate.now().toString());

        gbc.gridx=0; gbc.gridy=0; dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx=1; dialog.add(txtNombre, gbc);
        gbc.gridx=0; gbc.gridy=1; dialog.add(new JLabel("Naturaleza:"), gbc);
        gbc.gridx=1; dialog.add(txtNaturaleza, gbc);
        gbc.gridx=0; gbc.gridy=2; dialog.add(new JLabel("Fecha:"), gbc);
        gbc.gridx=1; dialog.add(txtFecha, gbc);

        JRadioButton rbRect = new JRadioButton("Rectangulares");
        JRadioButton rbPol = new JRadioButton("Polares");
        ButtonGroup group = new ButtonGroup();
        group.add(rbRect); group.add(rbPol);
        if (blancoEditar != null && blancoEditar.getCoordenadas() instanceof coordPolares) rbPol.setSelected(true);
        else rbRect.setSelected(true);

        gbc.gridx=0; gbc.gridy=3; dialog.add(new JLabel("Tipo de coordenadas:"), gbc);
        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radios.add(rbRect); radios.add(rbPol);
        gbc.gridx=1; dialog.add(radios, gbc);

        JTextField campo1 = new JTextField();
        JTextField campo2 = new JTextField();
        JTextField campo3 = new JTextField();

        if (blancoEditar != null) {
            coordenadas c = blancoEditar.getCoordenadas();
            if (c instanceof coordRectangulares) {
                coordRectangulares r = (coordRectangulares) c;
                campo1.setText(String.valueOf(r.getX()));
                campo2.setText(String.valueOf(r.getY()));
                campo3.setText(String.valueOf(r.getCota()));
            } else if (c instanceof coordPolares) {
                coordPolares p = (coordPolares) c;
                campo1.setText(String.valueOf(p.getDireccion()));
                campo2.setText(String.valueOf(p.getDistancia()));
                campo3.setText(String.valueOf(p.getAnguloVertical()));
            }
        } else if (coordInicial != null) {
            campo1.setText(String.valueOf(coordInicial.getLon()));
            campo2.setText(String.valueOf(coordInicial.getLat()));
            campo3.setText(String.valueOf(0));
            rbRect.setSelected(true);
        }

        gbc.gridx=0; gbc.gridy=4; dialog.add(new JLabel("X:"), gbc);
        gbc.gridx=1; dialog.add(campo1, gbc);
        gbc.gridx=0; gbc.gridy=5; dialog.add(new JLabel("Y:"), gbc);
        gbc.gridx=1; dialog.add(campo2, gbc);
        gbc.gridx=0; gbc.gridy=6; dialog.add(new JLabel("COTA:"), gbc);
        gbc.gridx=1; dialog.add(campo3, gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        gbc.gridx = 0; gbc.gridy = 7;
        dialog.add(btnAceptar, gbc);
        gbc.gridx = 1;
        dialog.add(btnCancelar, gbc);

        btnAceptar.addActionListener(ev -> {
            try {
                String nombre = txtNombre.getText().trim();
                String naturaleza = txtNaturaleza.getText().trim();
                String fecha = txtFecha.getText().trim();
                if(nombre.isEmpty()||naturaleza.isEmpty()||fecha.isEmpty()){
                    JOptionPane.showMessageDialog(dialog,"Complete todos los campos","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double v1 = Double.parseDouble(campo1.getText().trim());
                double v2 = Double.parseDouble(campo2.getText().trim());
                double v3 = Double.parseDouble(campo3.getText().trim());

                coordenadas coords = rbRect.isSelected()? new coordRectangulares(v1,v2,v3) : new coordPolares(v1,v2,v3);

                if (blancoEditar == null) {
                    Blanco nuevo = new Blanco(nombre,coords,naturaleza,fecha);
                    listaDeBlancos.add(nuevo);
                    modeloLista.addElement(nuevo.getNombre());
                } else {
                    blancoEditar.setNombre(nombre);
                    blancoEditar.setNaturaleza(naturaleza);
                    blancoEditar.setFecha(fecha);
                    blancoEditar.setCoordenadas(coords);
                    int idx = listaDeBlancos.indexOf(blancoEditar);
                    if (idx >= 0) modeloLista.set(idx, nombre);
                }
                actualizarBlancosEnMapa();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,"Las coordenadas deben ser numéricas","Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(ev -> dialog.dispose());
        dialog.setVisible(true);
    }
}
