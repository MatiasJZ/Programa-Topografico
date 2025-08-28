import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

public class SituacionTactica extends JPanel {

    private DefaultListModel<String> modeloLista;
    private JList<String> listaUI;
    protected ArrayList<Blanco> listaDeBlancos;

    public SituacionTactica() {

        setSize(900, 600);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK); // fondo negro del panel principal

        //  LISTA DE BLANCOS 
        listaDeBlancos = new ArrayList<>();
        modeloLista = new DefaultListModel<>();
        listaUI = new JList<>(modeloLista);

        // FUENTE TEXTO DE LOS BLANCOS  
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        listaUI.setFont(new Font("Arial", Font.BOLD, 25));
        listaUI.setBackground(Color.BLACK);
        listaUI.setCellRenderer(renderer);
        listaUI.setForeground(Color.GREEN); // texto visible en fondo negro

        JScrollPane scrollLista = new JScrollPane(listaUI);
        scrollLista.setPreferredSize(new Dimension(250, 0));
        scrollLista.getViewport().setBackground(Color.BLACK);

        // Panel lateral (lista + botones)
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBackground(Color.BLACK);

        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        //  BOTONES 
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.BLACK);

        JButton btnAgregar = new JButton("AGREGAR"); 
        JButton btnEliminar = new JButton("ELIMINAR");

        // estilo de botones oscuros
        btnAgregar.setBackground(Color.green);
        btnAgregar.setForeground(Color.WHITE);
        btnEliminar.setBackground(Color.orange);
        btnEliminar.setForeground(Color.WHITE);

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);

        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        add(panelIzquierdo, BorderLayout.WEST);

        // Panel del mapa 
        JPanel panelMapa = new JPanel();
        panelMapa.setBackground(Color.DARK_GRAY);
        panelMapa.setLayout(new BorderLayout());
        panelMapa.add(new JLabel("Panel de mapa", SwingConstants.CENTER), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelMapa);
        splitPane.setDividerLocation(250);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // Acción de AGREGAR
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar());

        // Acción de ELIMINAR
        btnEliminar.addActionListener(e -> {
            int index = listaUI.getSelectedIndex();
            if (index >= 0) {
                listaDeBlancos.remove(index);
                modeloLista.remove(index);
            }
        });
    }

    // ================== DIÁLOGO PARA AGREGAR ==================
    private void mostrarDialogoAgregar() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Nuevo Blanco", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(Color.BLACK);  
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //  CAMPOS GENERALES 
        JTextField txtNombre = new JTextField();
        JTextField txtNaturaleza = new JTextField();
        JTextField txtFecha = new JTextField(java.time.LocalDate.now().toString()); // hoy por defecto

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; dialog.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Naturaleza:"), gbc);
        gbc.gridx = 1; dialog.add(txtNaturaleza, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; dialog.add(txtFecha, gbc);

        //  OPCIÓN DE TIPO DE COORDENADAS 
        JRadioButton rbRect = new JRadioButton("Rectangulares");
        JRadioButton rbPol = new JRadioButton("Polares");
        ButtonGroup group = new ButtonGroup();
        group.add(rbRect);
        group.add(rbPol);
        rbRect.setSelected(true);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Tipo de coordenadas:"), gbc);
        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radios.add(rbRect);
        radios.add(rbPol);
        gbc.gridx = 1; dialog.add(radios, gbc);

        //  CAMPOS DE COORDENADAS
        JTextField campo1 = new JTextField("X");
        JTextField campo2 = new JTextField("Y");
        JTextField campo3 = new JTextField("Cota");

        // función placeholder
        java.util.function.BiConsumer<JTextField, String> setPlaceholder = (tf, placeholder) -> {
            tf.setText(placeholder);
            tf.setForeground(Color.GRAY);
            tf.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (tf.getText().equals(placeholder)) {
                        tf.setText("");
                        tf.setForeground(Color.BLACK);
                    }
                }
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (tf.getText().isEmpty()) {
                        tf.setText(placeholder);
                        tf.setForeground(Color.GRAY);
                    }
                }
            });
        };

        setPlaceholder.accept(campo1, "X");
        setPlaceholder.accept(campo2, "Y");
        setPlaceholder.accept(campo3, "Cota");

        gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Coord 1:"), gbc);
        gbc.gridx = 1; dialog.add(campo1, gbc);
        gbc.gridx = 0; gbc.gridy = 5; dialog.add(new JLabel("Coord 2:"), gbc);
        gbc.gridx = 1; dialog.add(campo2, gbc);
        gbc.gridx = 0; gbc.gridy = 6; dialog.add(new JLabel("Coord 3:"), gbc);
        gbc.gridx = 1; dialog.add(campo3, gbc);

        // Cambiar placeholders correctamente
        rbRect.addActionListener(ev -> {
            setPlaceholder.accept(campo1, "X");
            setPlaceholder.accept(campo2, "Y");
            setPlaceholder.accept(campo3, "Cota");
        });
        rbPol.addActionListener(ev -> {
            setPlaceholder.accept(campo1, "Dirección");
            setPlaceholder.accept(campo2, "Distancia");
            setPlaceholder.accept(campo3, "Ángulo vertical");
        });

        //  BOTONES 
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        gbc.gridx = 0; gbc.gridy = 7; dialog.add(btnAceptar, gbc);
        gbc.gridx = 1; dialog.add(btnCancelar, gbc);

        btnAceptar.addActionListener(ev -> {
            try {
                String nombre = txtNombre.getText().trim();
                String naturaleza = txtNaturaleza.getText().trim();
                String fecha = txtFecha.getText().trim();

                if (nombre.isEmpty() || naturaleza.isEmpty() || fecha.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Complete todos los campos generales",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double v1 = Double.parseDouble(campo1.getText().trim());
                double v2 = Double.parseDouble(campo2.getText().trim());
                double v3 = Double.parseDouble(campo3.getText().trim());

                coordenadas coords;
                if (rbRect.isSelected()) {
                    coords = new coordRectangulares(v1, v2, v3);
                } else {
                    coords = new coordPolares(v1, v2, v3);
                }

                Blanco nuevo = new Blanco(nombre, coords, naturaleza, fecha);
                listaDeBlancos.add(nuevo);
                modeloLista.addElement(nuevo.getNombre()); //mostrarlo en la lista

                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Las coordenadas deben ser numéricas",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }
}
