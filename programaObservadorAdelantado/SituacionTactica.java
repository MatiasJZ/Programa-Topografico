import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import javax.swing.*;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;

public class SituacionTactica extends JPanel {

    private static final long serialVersionUID = 1L;
    private DefaultListModel<Blanco> modeloListaBlancos;
    private JList<Blanco> listaUIBlancos;
    protected LinkedList<Blanco> listaDeBlancos;
    private PanelMapa panelMapa;
    private DefaultListModel<Punto> modeloListaPuntos;
    private JList<Punto> listaUIPuntos;
    protected LinkedList<Punto> listaDePuntos;

    public SituacionTactica(LinkedList<Blanco> listaDeBlancos) {
        setSize(900, 600);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        this.listaDeBlancos = listaDeBlancos;
        modeloListaBlancos = new DefaultListModel<>();
        listaUIBlancos = new JList<>(modeloListaBlancos);
        listaUIBlancos.setFont(new Font("Arial", Font.BOLD, 20));
        listaUIBlancos.setBackground(Color.BLACK);

        listaUIBlancos.setCellRenderer(new DefaultListCellRenderer() {
		private static final long serialVersionUID = 1L;
			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (value instanceof Blanco) {
                    Blanco b = (Blanco) value;
                    label.setText(b.getNombre());
                    label.setForeground(Color.WHITE);
                }
                return label;
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaUIBlancos);
        scrollLista.setPreferredSize(new Dimension(250, 0));
        scrollLista.getViewport().setBackground(Color.BLACK);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBackground(Color.BLACK);
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        listaDePuntos = new LinkedList<>();
        modeloListaPuntos = new DefaultListModel<>();
        listaUIPuntos = new JList<>(modeloListaPuntos);
        listaUIPuntos.setFont(new Font("Arial", Font.BOLD, 20));
        listaUIPuntos.setBackground(Color.BLACK);

        listaUIPuntos.setCellRenderer(new DefaultListCellRenderer() {
		private static final long serialVersionUID = 1L;
			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (value instanceof Punto) {
                    Punto p = (Punto) value;
                    label.setText(p.getNombre());
                    label.setForeground(new Color(200, 200, 200)); // gris claro
                }
                return label;
            }
        });

        // Título visual (igual estética)
        JLabel lblBlancos = new JLabel("BLANCOS", SwingConstants.CENTER);
        lblBlancos.setForeground(Color.WHITE);
        lblBlancos.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblPuntos = new JLabel("PUNTOS", SwingConstants.CENTER);
        lblPuntos.setForeground(Color.WHITE);
        lblPuntos.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panelListas = new JPanel();
        panelListas.setLayout(new BoxLayout(panelListas, BoxLayout.Y_AXIS));
        panelListas.setBackground(Color.BLACK);

        panelListas.add(lblBlancos);
        panelListas.add(new JScrollPane(listaUIBlancos));
        panelListas.add(Box.createVerticalStrut(10));
        panelListas.add(lblPuntos);
        panelListas.add(new JScrollPane(listaUIPuntos));

        // Usamos este panel de listas dentro del panel izquierdo existente
        panelIzquierdo.add(panelListas, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setBackground(Color.BLACK);

        JButton btnAgregar = new JButton("AGREGAR");
        JButton btnEliminar = new JButton("ELIMINAR");
        JButton btnActualizar = new JButton("ACTUALIZAR");
        JButton btnPIF = new JButton("PIF");

        btnAgregar.setBackground(new Color(0,120,255)); btnAgregar.setForeground(Color.WHITE);
        btnEliminar.setBackground(Color.ORANGE);        btnEliminar.setForeground(Color.WHITE);
        btnActualizar.setBackground(Color.RED);         btnActualizar.setForeground(Color.WHITE);
        btnPIF.setBackground(Color.GREEN);              btnPIF.setForeground(Color.WHITE);

        Dimension compacto = new Dimension(110, 32);
        Font fuente = new Font("Arial", Font.BOLD, 12);
        Insets padding = new Insets(4, 10, 4, 10);

        for (JButton b : new JButton[]{btnAgregar, btnEliminar, btnActualizar, btnPIF}) {
            b.setFont(fuente);
            b.setMargin(padding);
            b.setPreferredSize(compacto);
            b.setFocusPainted(false);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0; panelBotones.add(btnAgregar, gbc);
        gbc.gridx = 1; panelBotones.add(btnEliminar, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelBotones.add(btnActualizar, gbc);
        gbc.gridx = 1; panelBotones.add(btnPIF, gbc);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        // Mapa
        panelMapa = new PanelMapa("C:/Users/54293/Desktop/Archivos SARGO/mapaV1.TIF");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelMapa);
        splitPane.setDividerLocation(250);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // CLICK EN MAPA → menú: Blanco o Punto
        panelMapa.getMapPane().setCursorTool(new CursorTool() {
            @Override
            public void onMouseClicked(MapMouseEvent ev) {
                double x = ev.getWorldPos().getX();
                double y = ev.getWorldPos().getY();
                coordRectangulares coord = new coordRectangulares(x, y, 0);

                String[] opciones = {"Marcar Blanco", "Marcar Punto"};
                int seleccion = JOptionPane.showOptionDialog(
                        SituacionTactica.this,
                        "Seleccione qué desea marcar:",
                        "Marcación en mapa",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]);

                if (seleccion == 0) {
                    mostrarDialogoAgregarBlanco(coord);
                } else if (seleccion == 1) {
                    mostrarDialogoAgregarPunto(coord);
                }
            }
        });

        // Botón Agregar → elige tipo también
        btnAgregar.addActionListener(e -> {
            coordRectangulares coord = new coordRectangulares(0,0,0);
            String[] opciones = {"Marcar Blanco", "Marcar Punto"};
            int seleccion = JOptionPane.showOptionDialog(
                    this,
                    "Seleccione qué desea agregar:",
                    "Nueva marcación",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);

            if (seleccion == 0) mostrarDialogoAgregarBlanco(coord);
            else if (seleccion == 1) mostrarDialogoAgregarPunto(coord);
        });

        // Eliminar
        btnEliminar.addActionListener(e -> {
            Blanco selecB = listaUIBlancos.getSelectedValue();
            if (selecB != null) {
                listaDeBlancos.remove(selecB);
                modeloListaBlancos.removeElement(selecB);
                panelMapa.eliminarBlanco(selecB);
            }
            Punto selecP = listaUIPuntos.getSelectedValue();
            if(selecP != null) {
            	listaDePuntos.remove(selecP);
            	modeloListaPuntos.removeElement(selecP);
                panelMapa.eliminarPunto(selecP);
            }
        });

        // Actualizar
        btnActualizar.addActionListener(e -> actualizarBlancosEnMapa());

        // PIF
        btnPIF.addActionListener(e -> armarPIF(listaUIBlancos.getSelectedValue()));

        // MENU CONTEXTUAL
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("Editar Blanco");
        JMenuItem itemMarcarPolares = new JMenuItem("Marcar en Polares");
        JMenuItem itemMedir = new JMenuItem("Medir");

        popupMenu.add(itemEditar);
        popupMenu.add(itemMedir);
        popupMenu.add(itemMarcarPolares);

        itemMedir.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) mostrarDialogoMedir(bSel);
        });
        itemEditar.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) mostrarDialogoEditar(bSel);
        });
        itemMarcarPolares.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) mostrarDialogoPolares(bSel);
        });

        listaUIBlancos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopup(e); }
            private void mostrarPopup(MouseEvent e) {
                int idx = listaUIBlancos.locationToIndex(e.getPoint());
                if (idx >= 0) {
                	listaUIBlancos.setSelectedIndex(idx);
                    popupMenu.show(listaUIBlancos, e.getX(), e.getY());
                }
            }
        });
    }

    // ============================================================
    // === DIALOGOS DE AGREGAR ===================================
    // ============================================================

    private void mostrarDialogoAgregarBlanco(coordRectangulares coordInicial) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Nuevo Blanco", true);
        dialog.setSize(500, 330);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50,50,50));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField();
        JTextField txtNaturaleza = new JTextField();
        addPlaceholder(txtNombre, "Nombre del Blanco");
        addPlaceholder(txtNaturaleza, "Naturaleza (código ej. INF, ART_E, TNK...)");
        txtNombre.setBackground(new Color(70,70,70)); txtNaturaleza.setBackground(new Color(70,70,70));
        txtNombre.setForeground(Color.WHITE); txtNaturaleza.setForeground(Color.WHITE);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFecha = new JTextField(dtf.format(LocalDateTime.now()));
        txtFecha.setEditable(false);
        txtFecha.setBackground(new Color(70,70,70));
        txtFecha.setForeground(Color.WHITE);

        JTextField txtX = new JTextField(String.valueOf(coordInicial.getX()));
        JTextField txtY = new JTextField(String.valueOf(coordInicial.getY()));
        txtX.setEditable(false); txtY.setEditable(false);
        txtX.setBackground(new Color(70,70,70)); txtY.setBackground(new Color(70,70,70));
        txtX.setForeground(Color.WHITE); txtY.setForeground(Color.WHITE);

        JLabel[] labels = {
                new JLabel("Nombre:"), new JLabel("Naturaleza:"),
                new JLabel("Fecha de creación:"), new JLabel("X:"), new JLabel("Y:")
        };
        for (JLabel l : labels) l.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; panel.add(labels[0], gbc);
        gbc.gridx=1; panel.add(txtNombre, gbc);
        gbc.gridx=0; gbc.gridy=1; panel.add(labels[1], gbc);
        gbc.gridx=1; panel.add(txtNaturaleza, gbc);
        gbc.gridx=0; gbc.gridy=2; panel.add(labels[2], gbc);
        gbc.gridx=1; panel.add(txtFecha, gbc);
        gbc.gridx=0; gbc.gridy=3; panel.add(labels[3], gbc);
        gbc.gridx=1; panel.add(txtX, gbc);
        gbc.gridx=0; gbc.gridy=4; panel.add(labels[4], gbc);
        gbc.gridx=1; panel.add(txtY, gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel botones = new JPanel(new GridLayout(1,2,10,0));
        botones.setBackground(new Color(50,50,50));
        botones.add(btnAceptar); botones.add(btnCancelar);
        gbc.gridx=0; gbc.gridy=5; gbc.gridwidth=2; panel.add(botones, gbc);

        btnAceptar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String naturaleza = txtNaturaleza.getText().trim();
            if (nombre.isEmpty() || naturaleza.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            coordRectangulares coord = new coordRectangulares(coordInicial.getX(), coordInicial.getY(),0);
            String fecha = txtFecha.getText();
            Blanco nuevo = new Blanco(nombre, coord, naturaleza, fecha);

            // 🔹 Traducir naturaleza a SIDC MIL-STD-2525D
            String sidc = CodigosMilitares.obtenerSIDC(naturaleza);
            if (sidc != null) nuevo.setSimID(sidc);
            else JOptionPane.showMessageDialog(dialog, "Código de naturaleza no reconocido.", "Aviso", JOptionPane.WARNING_MESSAGE);

            listaDeBlancos.add(nuevo);
            modeloListaBlancos.addElement(nuevo);
            panelMapa.agregarBlanco(nuevo);
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void mostrarDialogoAgregarPunto(coordRectangulares coordInicial) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Nuevo Punto", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNombre = new JLabel("Nombre del Punto:"); lblNombre.setForeground(Color.WHITE);
        JTextField txtNombre = new JTextField(); addPlaceholder(txtNombre, "Nombre del Punto");
        txtNombre.setBackground(new Color(70,70,70)); txtNombre.setForeground(Color.WHITE);

        JLabel lblX = new JLabel("X:"); lblX.setForeground(Color.WHITE);
        JLabel lblY = new JLabel("Y:"); lblY.setForeground(Color.WHITE);
        JTextField txtX = new JTextField(String.valueOf(coordInicial.getX()));
        JTextField txtY = new JTextField(String.valueOf(coordInicial.getY()));
        txtX.setEditable(false); txtY.setEditable(false);
        txtX.setBackground(new Color(70,70,70)); txtY.setBackground(new Color(70,70,70));
        txtX.setForeground(Color.WHITE); txtY.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; panel.add(lblNombre, gbc);
        gbc.gridx=1; panel.add(txtNombre, gbc);
        gbc.gridx=0; gbc.gridy=1; panel.add(lblX, gbc);
        gbc.gridx=1; panel.add(txtX, gbc);
        gbc.gridx=0; gbc.gridy=2; panel.add(lblY, gbc);
        gbc.gridx=1; panel.add(txtY, gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel botones = new JPanel(new GridLayout(1,2,10,0));
        botones.setBackground(new Color(50,50,50));
        botones.add(btnAceptar); botones.add(btnCancelar);
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; panel.add(botones, gbc);

        btnAceptar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty() || nombre.equals("Nombre del Punto")) {
                JOptionPane.showMessageDialog(dialog, "Ingrese un nombre para el punto.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Punto nuevo = new Punto(coordInicial, nombre);
            panelMapa.agregarPunto(nuevo);
            listaDePuntos.add(nuevo);
            modeloListaPuntos.addElement(nuevo);
            dialog.dispose();
        });
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ============================================================
    // === DIALOGOS AUXILIARES ===================================
    // ============================================================

    private void mostrarDialogoPolares(Blanco referencia) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Marcar en Polares desde: " + referencia.getNombre(), true);
        dialog.setSize(600, 380);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Ingrese los datos del nuevo blanco:");
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; panel.add(lblTitulo, gbc);

        JLabel lblNombre = new JLabel("Nombre:"); lblNombre.setForeground(Color.WHITE);
        JLabel lblNaturaleza = new JLabel("Naturaleza:"); lblNaturaleza.setForeground(Color.WHITE);
        JLabel lblDir = new JLabel("Dirección (milésimos):"); lblDir.setForeground(Color.WHITE);
        JLabel lblDist = new JLabel("Distancia (m):"); lblDist.setForeground(Color.WHITE);
        JLabel lblAng = new JLabel("Ángulo vertical (milésimos):"); lblAng.setForeground(Color.WHITE);

        JTextField txtNombre = new JTextField();
        JTextField txtNaturaleza = new JTextField();
        JTextField txtDireccion = new JTextField();
        JTextField txtDistancia = new JTextField();
        JTextField txtAngulo = new JTextField("0");

        JTextField[] fields = {txtNombre, txtNaturaleza, txtDireccion, txtDistancia, txtAngulo};
        for (JTextField f : fields) {
            f.setPreferredSize(new Dimension(300,30));
            f.setBackground(new Color(70,70,70));
            f.setForeground(Color.WHITE);
        }

        gbc.gridwidth=1;
        gbc.gridx=0; gbc.gridy=1; panel.add(lblNombre, gbc);
        gbc.gridx=1; panel.add(txtNombre, gbc);
        gbc.gridx=0; gbc.gridy=2; panel.add(lblNaturaleza, gbc);
        gbc.gridx=1; panel.add(txtNaturaleza, gbc);
        gbc.gridx=0; gbc.gridy=3; panel.add(lblDir, gbc);
        gbc.gridx=1; panel.add(txtDireccion, gbc);
        gbc.gridx=0; gbc.gridy=4; panel.add(lblDist, gbc);
        gbc.gridx=1; panel.add(txtDistancia, gbc);
        gbc.gridx=0; gbc.gridy=5; panel.add(lblAng, gbc);
        gbc.gridx=1; panel.add(txtAngulo, gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel botones = new JPanel(new GridLayout(1,2,10,0));
        botones.setBackground(new Color(50,50,50));
        botones.add(btnAceptar); botones.add(btnCancelar);
        gbc.gridx=0; gbc.gridy=6; gbc.gridwidth=2; panel.add(botones, gbc);

        btnAceptar.addActionListener(e -> {
            try {
                double direccionMils = Double.parseDouble(txtDireccion.getText().trim());
                double distancia = Double.parseDouble(txtDistancia.getText().trim());
                double angVertMils = Double.parseDouble(txtAngulo.getText().trim());

                String nombre = txtNombre.getText().trim();
                String naturaleza = txtNaturaleza.getText().trim();

                if (nombre.isEmpty() || naturaleza.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                coordRectangulares refCoord = (coordRectangulares) referencia.getCoordenadas();
                coordPolares polar = new coordPolares(direccionMils, distancia, angVertMils, refCoord);
                coordRectangulares nuevasCoords = polar.toRectangulares();

                String fecha = LocalDateTime.now().toString();
                Blanco nuevo = new Blanco(nombre, nuevasCoords, naturaleza, fecha);
                listaDeBlancos.add(nuevo);
                modeloListaBlancos.addElement(nuevo);
                panelMapa.agregarBlanco(nuevo);

                JOptionPane.showMessageDialog(dialog,
                        String.format("Blanco '%s' marcado a %.2f m y %.1f mils desde '%s'.",
                                nombre, distancia, direccionMils, referencia.getNombre()),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Valores numéricos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ============================================================
    // === OTROS MÉTODOS EXISTENTES ===============================
    // ============================================================

    private void mostrarDialogoMedir(Blanco b1) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Medir distancia desde: " + b1.getNombre(), true);
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panelDialog);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Seleccione el blanco de destino:");
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelDialog.add(lblTitulo, gbc);

        JComboBox<Blanco> comboBlancos = new JComboBox<>();
        comboBlancos.setBackground(new Color(70, 70, 70));
        comboBlancos.setForeground(Color.WHITE);

        comboBlancos.setRenderer(new DefaultListCellRenderer() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Blanco) {
                    Blanco b = (Blanco) value;
                    setText(b.NombretoString()); // usar tu método
                }
                setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
                setForeground(Color.WHITE);
                return this;
            }
        });
        for (Blanco b : listaDeBlancos) {
            if (b != b1) comboBlancos.addItem(b);
        }
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panelDialog.add(comboBlancos, gbc);

        // botones
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setBackground(new Color(50, 50, 50));
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelDialog.add(panelBotones, gbc);

        // acción de los botones
        btnAceptar.addActionListener(e -> {
            Blanco b2 = (Blanco) comboBlancos.getSelectedItem();
            if (b2 == null) {
                JOptionPane.showMessageDialog(dialog, "Seleccione un blanco destino válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double distancia = b1.getCoordenadas().distanciaA(b2.getCoordenadas());
                String mensaje = String.format("Distancia entre %s y %s:\n%.2f metros",b1.getNombre(), b2.getNombre(), distancia);
                JOptionPane.showMessageDialog(dialog, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,"Error al calcular la distancia:\n" + ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(e -> dialog.dispose());

        JRootPane rootPane = dialog.getRootPane();
        rootPane.setDefaultButton(btnAceptar);
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                btnCancelar.doClick();
            }
        });
        dialog.setVisible(true);
    }

    
    private void mostrarDialogoEditar(Blanco b) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Editar Blanco", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panelDialog);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // campos
        JTextField txtNombre = new JTextField(b.getNombre());
        txtNombre.setPreferredSize(new Dimension(250, 30));
        JTextField txtNaturaleza = new JTextField(b.getNaturaleza());
        txtNaturaleza.setPreferredSize(new Dimension(250, 30));
        // coordenadas
        JTextField txtX = new JTextField(String.valueOf(b.getCoordenadas().getX()));
        JTextField txtY = new JTextField(String.valueOf(b.getCoordenadas().getY()));
        txtX.setPreferredSize(new Dimension(250, 30));
        txtY.setPreferredSize(new Dimension(250, 30));
        txtX.setEditable(false);
        txtY.setEditable(false);
        
        JTextField[] fields = {txtNombre, txtNaturaleza, txtX, txtY};
        for (JTextField f : fields) {
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
            f.setCaretColor(Color.WHITE);
        }

        // labels
        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblNaturaleza = new JLabel("Naturaleza:");
        JLabel lblTipo = new JLabel("Tipo (Aliado/Enemigo):");
        JLabel lblX = new JLabel("Coordenada X:");
        JLabel lblY = new JLabel("Coordenada Y:");
        JLabel[] labels = {lblNombre, lblNaturaleza, lblTipo, lblX, lblY};
        for (JLabel l : labels) l.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; panelDialog.add(lblNombre, gbc);
        gbc.gridx = 1; panelDialog.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy++; panelDialog.add(lblNaturaleza, gbc);
        gbc.gridx = 1; panelDialog.add(txtNaturaleza, gbc);

        gbc.gridx = 0; gbc.gridy++; panelDialog.add(lblX, gbc);
        gbc.gridx = 1; panelDialog.add(txtX, gbc);

        gbc.gridx = 0; gbc.gridy++; panelDialog.add(lblY, gbc);
        gbc.gridx = 1; panelDialog.add(txtY, gbc);

        // botones
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel panelBotonesDialog = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotonesDialog.setBackground(new Color(50, 50, 50));
        panelBotonesDialog.add(btnAceptar);
        panelBotonesDialog.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panelDialog.add(panelBotonesDialog, gbc);

        btnAceptar.addActionListener(e -> {
        	b.setNombre(txtNombre.getText());
            b.setNaturaleza(txtNaturaleza.getText());
            listaUIBlancos.repaint();
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.setMinimumSize(new Dimension(400, 350));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        
        JRootPane rootPane = dialog.getRootPane();
        rootPane.setDefaultButton(btnAceptar);
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                btnCancelar.doClick();
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void actualizarBlancosEnMapa() {
        for (Blanco b : listaDeBlancos) {
            panelMapa.agregarBlanco(b);
        }
        listaUIBlancos.repaint();
    }
    
    private void armarPIF(Blanco b) {
    	JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Pedido Inicial de Fuego", true);
        dialog.setSize(1000,600);
        dialog.setLocationRelativeTo(this);

        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panelDialog);
        
        /////
        /*proximos camiobios*/
        /////
        
        dialog.setVisible(true);
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
}
