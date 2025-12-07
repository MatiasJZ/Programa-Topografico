package app;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.locationtech.jts.geom.Coordinate;

import dominio.Blanco;
import dominio.CodigosMilitares;
import dominio.Linea;
import dominio.Punto;
import dominio.SituacionMovimiento;
import dominio.coordPolares;
import dominio.coordRectangulares;
import dominio.poligonal;
import interfaz.PanelMapa;
import util.SoundManager;

public class SituacionTactica extends JPanel {

	private static final long serialVersionUID = 789462013392544798L;
	private DefaultListModel<Blanco> modeloListaBlancos;
    private JList<Blanco> listaUIBlancos;
    protected LinkedList<Blanco> listaDeBlancos;
    private PanelMapa panelMapa;
    protected LinkedList<poligonal> listaDePoligonales;
    private DefaultListModel<poligonal> modeloListaPoligonales;
    private JList<poligonal> listaUIPoligonales;
    protected String rutaArchivoMapa = "C:/Users/54293/Desktop/Archivos SARGO/mapaV1.TIF";
    protected PedidoDeFuego panelPIF;
    private SoundManager sonidos;
    private ObservadorAdelantado observador;

    public SituacionTactica(LinkedList<Blanco> listaDeBlancos,PedidoDeFuego pif,ObservadorAdelantado obs) { 

		this.observador = obs;
		
		setSize(900, 600);
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		
		this.listaDeBlancos = listaDeBlancos;
		modeloListaBlancos = new DefaultListModel<>();
		listaUIBlancos = new JList<>(modeloListaBlancos);
		listaUIBlancos.setFont(new Font("Arial", Font.BOLD, 20));
		listaUIBlancos.setBackground(Color.BLACK);
		
		panelPIF = pif;
		sonidos = new SoundManager();
		
		listaUIBlancos.setCellRenderer(new DefaultListCellRenderer() {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index,
		                                          boolean isSelected, boolean cellHasFocus) {
		
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index,
		                                                           isSelected, cellHasFocus);
		label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		if (value instanceof Blanco b) {
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
		
		listaDePoligonales = new LinkedList<>();
		modeloListaPoligonales = new DefaultListModel<>();
		listaUIPoligonales = new JList<>(modeloListaPoligonales);
		listaUIPoligonales.setFont(new Font("Arial", Font.BOLD, 20));
		listaUIPoligonales.setBackground(Color.BLACK);
		
		listaUIPoligonales.setCellRenderer(new DefaultListCellRenderer() {
		private static final long serialVersionUID = -3139532018963310447L;
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value,
		                                          int index, boolean isSelected, boolean cellHasFocus) {
		
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index,
		                                                           isSelected, cellHasFocus);
		
		if (value instanceof Punto pt)
		    label.setText(pt.getNombre());
		else if (value instanceof Linea ln)
		    label.setText(ln.getName() + " (" + String.format("%.3f m", ln.getDistancia()) + ")");
		
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		return label;
		}
		});
		
		JLabel lblBlancos = new JLabel("BLANCOS", SwingConstants.CENTER);
		lblBlancos.setForeground(Color.GRAY);
		lblBlancos.setFont(new Font("Arial", Font.BOLD, 18));
		
		JLabel lblPuntos = new JLabel("POLIGONALES", SwingConstants.CENTER);
		lblPuntos.setForeground(Color.GRAY);
		lblPuntos.setFont(new Font("Arial", Font.BOLD, 18));
		
		JPanel panelListas = new JPanel(new GridBagLayout());
		panelListas.setBackground(Color.BLACK);
		
		GridBagConstraints gbcList = new GridBagConstraints();
		gbcList.fill = GridBagConstraints.BOTH;
		gbcList.insets = new Insets(2, 0, 2, 0);
		
		gbcList.gridx = 0;
		gbcList.gridy = 0;
		gbcList.weightx = 1;
		gbcList.weighty = 0;
		panelListas.add(lblBlancos, gbcList);
		
		gbcList.gridy = 1;
		gbcList.weighty = 0.66;
		panelListas.add(new JScrollPane(listaUIBlancos), gbcList);
		
		gbcList.gridy = 2;
		gbcList.weighty = 0;
		panelListas.add(lblPuntos, gbcList);
		
		gbcList.gridy = 3;
		gbcList.weighty = 0.33;
		panelListas.add(new JScrollPane(listaUIPoligonales), gbcList);
		
		panelIzquierdo.add(panelListas, BorderLayout.CENTER);
		
		// ---------------------------
		// BOTONERA IZQUIERDA
		// ---------------------------
		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setBackground(Color.BLACK);
		
		JButton btnAgregar = new JButton("AGREGAR");
		JButton btnEliminar = new JButton("ELIMINAR");
		JButton btnActualizar = new JButton("REFRESCAR");
		JButton btnPIF = new JButton("GENERAR PIF");
		
		JButton btnConfigIP = new JButton("CONFIGURAR IP");  // <-- NUEVO BOTÓN
		btnConfigIP.setBackground(new Color(60, 60, 120));
		btnConfigIP.setForeground(Color.WHITE);
		btnConfigIP.setFont(new Font("Arial", Font.BOLD, 12));
		btnConfigIP.setPreferredSize(new Dimension(110, 32));
		btnConfigIP.setFocusPainted(false);
		
		// Acción: abrir el diálogo
		btnConfigIP.addActionListener(e -> {
		DialogoConfigRed dlg =
		new DialogoConfigRed(SwingUtilities.getWindowAncestor(this),
		                     observador.getComunicacionIP());
		dlg.setVisible(true);
		});
		
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
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0; panelBotones.add(btnAgregar, gbc);
		gbc.gridx = 1; panelBotones.add(btnEliminar, gbc);
		gbc.gridx = 0; gbc.gridy = 1; panelBotones.add(btnActualizar, gbc);
		gbc.gridx = 1; panelBotones.add(btnPIF, gbc);
		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panelBotones.add(btnConfigIP, gbc); // <-- NUEVO
		
		panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);
		
		// MAPA
		pedirArchivoAMostrar();
		panelMapa = new PanelMapa(rutaArchivoMapa);
		panelPIF.setMapaObservacion(panelMapa);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelMapa);
		splitPane.setDividerLocation(250);
		splitPane.setContinuousLayout(true);
		add(splitPane, BorderLayout.CENTER);

        // click en mapa: Blanco o Punto
        panelMapa.getMapPane().setCursorTool(new CursorTool() {
            @Override
            public void onMouseClicked(MapMouseEvent ev) {
                double x = ev.getWorldPos().getX();
                double y = ev.getWorldPos().getY();
                coordRectangulares coord = new coordRectangulares(x, y, 0);

                String[] opciones = {"Marcar Blanco", "Marcar Punto"};
                Image img = new ImageIcon(getClass().getResource("/imagenPIN.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Icon icono = new ImageIcon(img);
                int seleccion = JOptionPane.showOptionDialog(SituacionTactica.this,
                        "Seleccione qué desea marcar:",null,
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        icono,
                        opciones,
                        opciones[0]);

                if (seleccion == 0) {
                    mostrarDialogoAgregarBlanco(coord);
                } else if (seleccion == 1) {
                    mostrarDialogoAgregarPunto(coord);
                }
            }
        });

        btnAgregar.addActionListener(e -> {
        	
        	sonidos.popUpSonido();
            coordRectangulares coord = new coordRectangulares(0,0,0);
            String[] opciones = {"Marcar Blanco", "Marcar Punto"};
            Image img = new ImageIcon(getClass().getResource("/imagenPIN.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            Icon icono = new ImageIcon(img);
            int seleccion = JOptionPane.showOptionDialog(
                    this,
                    "Seleccione qué desea agregar:",
                    null,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    icono,
                    opciones,
                    opciones[0]);

            if (seleccion == 0) mostrarDialogoAgregarBlanco(coord);
            else if (seleccion == 1) mostrarDialogoAgregarPunto(coord);
        });

        // eliminar
        btnEliminar.addActionListener(e -> {
        	
        	sonidos.popUpSonido();
            Blanco selecB = listaUIBlancos.getSelectedValue();
            poligonal selecP = listaUIPoligonales.getSelectedValue();

            // 1) Nada seleccionado
            if (selecB == null && selecP == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione un elemento para eliminar.",
                        "Nada seleccionado",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            // 2) Solo BLANCO seleccionado
            if (selecB != null && selecP == null) {
                listaDeBlancos.remove(selecB);
                modeloListaBlancos.removeElement(selecB);
                panelMapa.eliminarBlanco(selecB);
                listaUIBlancos.clearSelection();
                return;
            }
            // 3) Solo POLIGONAL seleccionada
            if (selecP != null && selecB == null) {
                listaDePoligonales.remove(selecP);
                modeloListaPoligonales.removeElement(selecP);
                panelMapa.eliminarPoligonal(selecP);
                listaUIPoligonales.clearSelection();
                return;
            }
            // 4) Ambas seleccionadas → Preguntar al usuario
            String[] opciones = {"Eliminar Blanco", "Eliminar Poligonal", "Cancelar"};
            int resp = JOptionPane.showOptionDialog(
                    this,
                    "Hay un blanco y una poligonal seleccionados.\n¿Qué desea eliminar?",
                    "Confirme eliminación",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[2]
            );
            if (resp == 0 && selecB != null) {
                listaDeBlancos.remove(selecB);
                modeloListaBlancos.removeElement(selecB);
                panelMapa.eliminarBlanco(selecB);
            } 
            else if (resp == 1 && selecP != null) {
                listaDePoligonales.remove(selecP);
                modeloListaPoligonales.removeElement(selecP);
                panelMapa.eliminarPoligonal(selecP);
            }
            listaUIBlancos.clearSelection();
            listaUIPoligonales.clearSelection();
        });

        // actualizar
        btnActualizar.addActionListener(e -> {
        sonidos.popUpSonido();	
        actualizarBlancosEnMapa();
        });

        // PIF
        btnPIF.addActionListener(e -> {
        sonidos.popUpSonido();
        armarPIF(listaUIBlancos.getSelectedValue());
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("Editar Blanco Seleccionado");
        itemEditar.setBackground(Color.BLACK);
        itemEditar.setForeground(Color.WHITE);
        JMenuItem itemMarcarPolares = new JMenuItem("Marcar Nuevo Blanco en Polares");
        itemMarcarPolares.setBackground(Color.BLACK);
        itemMarcarPolares.setForeground(Color.WHITE);
        JMenuItem itemMedir = new JMenuItem("Marcar Medicion");
        itemMedir.setBackground(Color.BLACK);
        itemMedir.setForeground(Color.WHITE);

        popupMenu.add(itemEditar);
        popupMenu.add(itemMedir);
        popupMenu.add(itemMarcarPolares);

        itemMedir.addActionListener(e -> {
        	sonidos.popUpSonido();
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) mostrarDialogoMedir(bSel);
        });
        itemEditar.addActionListener(e -> {
        	sonidos.popUpSonido();
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) mostrarDialogoEditar(bSel);
        });
        itemMarcarPolares.addActionListener(e -> {
        	sonidos.popUpSonido();
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
                	sonidos.popUpSonido();
                    popupMenu.show(listaUIBlancos, e.getX(), e.getY());
                }
            }
        });
    }

    private void pedirArchivoAMostrar() {

        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/LOGOBIAC.png"));
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
        ImageIcon icono = new ImageIcon(imgEscalada);

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Subir Mapa", true);
        dialog.setSize(650, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setIconImage(imgEscalada);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(40, 40, 40));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblRuta = new JLabel("Ingrese la ruta del archivo TIFF:");
        lblRuta.setForeground(Color.WHITE);
        lblRuta.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        panel.add(lblRuta, gbc);

        JTextField txtRuta = new JTextField();
        Color placeholderColor = new Color(180,180,180);
        Color textColor = Color.WHITE;
        String placeholder = "Ejemplo: C:/Usuarios/Usuario/Mapa.tif";
        txtRuta.setText(placeholder);
        txtRuta.setForeground(placeholderColor);
        txtRuta.setBackground(new Color(70,70,70));
        txtRuta.setCaretColor(Color.WHITE);
        txtRuta.setPreferredSize(new Dimension(500, 28));

        txtRuta.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtRuta.getText().equals(placeholder)) {
                    txtRuta.setText("");
                    txtRuta.setForeground(textColor);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtRuta.getText().isEmpty()) {
                    txtRuta.setForeground(placeholderColor);
                    txtRuta.setText(placeholder);
                }
            }
        });

        JButton btnExaminar = new JButton("Examinar");
        btnExaminar.setBackground(new Color(60, 60, 60));
        btnExaminar.setForeground(Color.WHITE);
        btnExaminar.setFocusPainted(false);
        btnExaminar.setFont(new Font("Arial", Font.BOLD, 12));

        gbc.gridy = 1; gbc.gridwidth = 2; gbc.gridx = 0;
        panel.add(txtRuta, gbc);
        gbc.gridx = 2; gbc.gridwidth = 1;
        panel.add(btnExaminar, gbc);

        JTextField txtInvisible = new JTextField();
        txtInvisible.setPreferredSize(new Dimension(1, 1));
        txtInvisible.setOpaque(false);
        txtInvisible.setBorder(null);
        txtInvisible.setFocusable(true);
        txtInvisible.setEditable(false);
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(txtInvisible, gbc);

        JButton btnAceptar = new JButton("Ingresar Ruta");
        JButton btnCancelar = new JButton("Usar ruta por defecto");
        for (JButton b : new JButton[]{btnAceptar, btnCancelar}) {
            b.setFocusPainted(false);
            b.setFont(new Font("Arial", Font.BOLD, 13));
            b.setBackground(new Color(70, 70, 70));
            b.setForeground(Color.WHITE);
            b.setPreferredSize(new Dimension(180, 32));
        }
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(40, 40, 40));
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        panel.add(panelBotones, gbc);
        // acción examinar
        btnExaminar.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Seleccionar archivo TIFF");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Archivos TIFF (*.tif, *.tiff)", "tif", "tiff")
            );

            int result = chooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                String rutaSeleccionada = chooser.getSelectedFile().getAbsolutePath();
                txtRuta.setForeground(textColor);
                txtRuta.setText(rutaSeleccionada.replace("\\", "/"));
            }
        });
        btnAceptar.addActionListener(e -> {
            String rutaIngresada = txtRuta.getText().trim();
            if (rutaIngresada.isEmpty() || rutaIngresada.equals(placeholder)) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, 
                    "Debe ingresar una ruta válida.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE, 
                    icono);
                return;
            }
            if (!rutaIngresada.toLowerCase().endsWith(".tif") && 
                !rutaIngresada.toLowerCase().endsWith(".tiff")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, 
                    "Solo se admiten archivos con extensión .TIF o .TIFF.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE,
                    icono);
                return;
            }
            rutaArchivoMapa = rutaIngresada.replace("\\", "/");
            dialog.dispose();
        });
        btnCancelar.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog,
                "Se usará la ruta por defecto:\n" + rutaArchivoMapa,
                "Ruta por defecto",
                JOptionPane.INFORMATION_MESSAGE,
                icono);
            dialog.dispose();
        });
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int resp = JOptionPane.showConfirmDialog(
                    dialog,
                    "¿Desea salir del programa?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    icono
                );
                if (resp == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        dialog.setVisible(true);
    }

    @SuppressWarnings("serial")
	private void mostrarDialogoAgregarBlanco(coordRectangulares coordInicial) {
    	
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Nuevo Blanco", true);
        dialog.setSize(660, 580);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblOrient = new JLabel("Orientación:");
        lblOrient.setForeground(Color.WHITE);
        JTextField txtOrient = new JTextField();
        addPlaceholder(txtOrient,"mils");
        txtOrient.setPreferredSize(new Dimension(80, 28));
        txtOrient.setBackground(new Color(70, 70, 70));
        txtOrient.setForeground(Color.WHITE);

        JTextField txtNombre = new JTextField();
        addPlaceholder(txtNombre, "Nombre del Blanco");
        txtNombre.setBackground(new Color(70, 70, 70));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setPreferredSize(new Dimension(240, 28));

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelNombre.setBackground(new Color(50, 50, 50));
        panelNombre.add(lblOrient);
        panelNombre.add(txtOrient);
        panelNombre.add(txtNombre);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFecha = new JTextField(dtf.format(LocalDateTime.now()));
        txtFecha.setEditable(false);
        txtFecha.setBackground(new Color(70, 70, 70));
        txtFecha.setForeground(Color.WHITE);

        JTextField txtX = new JTextField(String.valueOf(coordInicial.getX()));
        JTextField txtY = new JTextField(String.valueOf(coordInicial.getY()));
        for (JTextField f : new JTextField[]{txtX, txtY}) {
            f.setEditable(true);
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
            f.setPreferredSize(new Dimension(300, 26));
        }

        String[] entidades = {"INFANTERIA", "INFANTERIA-MOTORIZADA", "INFANTERIA-ANFIBIA", "INFANTERIA-MECANIZADA", "INFANTERIA-FORTIFICADA", "INFANTERIA-RECONOCIMIENTO", "INFANTERIA-REC-MOTORIZADA", "ANTITANQUE", "ANTITANQUE-BLINDADO", "ANTITANQUE-MOTORIZADO", "ARTILLERIA", "ARTILLERIA-AUTOPROPULSADA", "ARTILLERIA-ADQ-BLANCOS", "DEFENSA-AEREA", "MORTERO", "MORTERO-MOTORIZADO", "MORTERO-ACORAZADO", "INGENIEROS", "COMUNICACIONES", "GUERRA-ELECTRONICA", "COMANDO-Y-CONTROL", "GRUPO-LOGISTICO/APOYO", "OBSERVADOR", "OBSERVADOR-ARTILLERIA", "DRON-TERRESTRE", "INSTALACION-MEDICA"};
        String[] afiliaciones = {"ALIADO", "HOSTIL", "NEUTRO", "DESCONOCIDO", "ASUMIDO-ENEMIGO", "PENDIENTE", "ASUMIDO-AMIGO"};
        String[] escalafones = {"Por Defecto", "PELOTON", "COMPANIA", "BRIGADA", "DIVISION"};

        JComboBox<String> cbEntidad = new JComboBox<>(entidades);
        JComboBox<String> cbAfiliacion = new JComboBox<>(afiliaciones);
        JComboBox<String> cbEchelon = new JComboBox<>(escalafones);

        for (JComboBox<?> cb : new JComboBox[]{cbEntidad, cbAfiliacion, cbEchelon}) {
            cb.setPreferredSize(new Dimension(220, 26));
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
        }

        ListCellRenderer<String> guionRenderer = (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.replace("-", " "));
            label.setOpaque(true);
            label.setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
            label.setForeground(Color.WHITE);
            return label;
        };
        cbEntidad.setRenderer(guionRenderer);
        cbAfiliacion.setRenderer(guionRenderer);

        JPanel panelNaturaleza = new JPanel(new GridBagLayout());
        panelNaturaleza.setBackground(new Color(50, 50, 50));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(2, 4, 2, 4);
        g2.anchor = GridBagConstraints.WEST;

        JLabel lblEnt = new JLabel("Tipo:"); lblEnt.setForeground(Color.WHITE);
        JLabel lblAfi = new JLabel("Afiliación:"); lblAfi.setForeground(Color.WHITE);
        JLabel lblEsc = new JLabel("Magnitud:"); lblEsc.setForeground(Color.WHITE);

        g2.gridx = 0; g2.gridy = 0; panelNaturaleza.add(lblEnt, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEntidad, g2);
        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(lblAfi, g2);
        g2.gridx = 1; panelNaturaleza.add(cbAfiliacion, g2);
        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(lblEsc, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEchelon, g2);

        JLabel lblSituacion = new JLabel("Situación de movimiento:");
        lblSituacion.setForeground(Color.WHITE);
        JComboBox<SituacionMovimiento> cbSituacion = new JComboBox<>(SituacionMovimiento.values());
        cbSituacion.setBackground(new Color(70, 70, 70));
        cbSituacion.setForeground(Color.WHITE);
        cbSituacion.setSelectedItem(SituacionMovimiento.DESCONOCIDO);

        JLabel lblInfo = new JLabel("Información adicional:");
        lblInfo.setForeground(Color.WHITE);
        JTextArea txtInfo = new JTextArea();
        addPlaceholder(txtInfo, "Información adicional necesaria");
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(new Color(70, 70, 70));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setCaretColor(Color.WHITE);
        txtInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        txtInfo.setPreferredSize(new Dimension(240, 120));

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(panelNombre, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(panelNaturaleza, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Fecha de creación:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(txtFecha, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("DERECHAS:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(txtX, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("ARRIBAS:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(txtY, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblSituacion, gbc);
        gbc.gridx = 1; panel.add(cbSituacion, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblInfo, gbc);
        gbc.gridx = 1; panel.add(new JScrollPane(txtInfo), gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
        botones.setBackground(new Color(50, 50, 50));
        botones.add(btnAceptar);
        botones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(botones, gbc);

        btnAceptar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty() || nombre.equals("Nombre del Blanco")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Ingrese un nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String entidad = (String) cbEntidad.getSelectedItem();
            String afiliacion = (String) cbAfiliacion.getSelectedItem();
            String echelon = (String) cbEchelon.getSelectedItem();
            String naturaleza = entidad + "_" + afiliacion;
        
            if (!echelon.equals("Por Defecto")) naturaleza += "_" + echelon.toUpperCase();

            coordRectangulares coord = new coordRectangulares(coordInicial.getX(), coordInicial.getY(), 0);
            Blanco nuevo = new Blanco(nombre, coord, naturaleza, txtFecha.getText());
            
            nuevo.setUltAfiliacion(afiliacion);
            nuevo.setUltEchelon(echelon);
            nuevo.setUltEntidad(entidad);
            
            nuevo.setSimID(CodigosMilitares.obtenerSIDC(naturaleza));
            nuevo.setSituacionMovimiento((SituacionMovimiento) cbSituacion.getSelectedItem());
            try {
                nuevo.setOrientacion(Double.parseDouble(txtOrient.getText().trim()));
            } catch (Exception ex) {
                nuevo.setOrientacion(0);
            }
            String info = txtInfo.getText().trim();
            if (info.equals("Información adicional necesaria")) info = "";
            nuevo.setInformacionAdicional(info);
            
            sonidos.blancoAgregado();
            
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

        JLabel lblX = new JLabel("DERECHAS:"); lblX.setForeground(Color.WHITE);
        JLabel lblY = new JLabel("ARRIBAS:"); lblY.setForeground(Color.WHITE);
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
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Ingrese un nombre para el punto.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Punto nuevo = new Punto(coordInicial, nombre);
            
            sonidos.blancoAgregado();
            
            panelMapa.agregarPoligonal(nuevo);
            listaDePoligonales.add(nuevo);
            modeloListaPoligonales.addElement(nuevo);
            dialog.dispose();
        });
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    @SuppressWarnings("serial")
	private void mostrarDialogoPolares(Blanco referencia) {
    	
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Marcar en Polares desde: " + referencia.getNombre(), true);
        dialog.setSize(740, 540);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Ingrese los datos del nuevo blanco:");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 15f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        JLabel lblOrient = new JLabel("Orientación:");
        lblOrient.setForeground(Color.WHITE);
        JTextField txtOrient = new JTextField("0");
        txtOrient.setPreferredSize(new Dimension(80, 26));
        txtOrient.setBackground(new Color(70, 70, 70));
        txtOrient.setForeground(Color.WHITE);

        JTextField txtNombre = new JTextField();
        txtNombre.setPreferredSize(new Dimension(180, 26));
        txtNombre.setBackground(new Color(70, 70, 70));
        txtNombre.setForeground(Color.WHITE);

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelNombre.setBackground(new Color(50, 50, 50));
        panelNombre.add(lblOrient);
        panelNombre.add(txtOrient);
        panelNombre.add(txtNombre);
        
        JLabel lblDir = new JLabel("Dirección (milésimos):");
        JLabel lblDist = new JLabel("Distancia (m):");
        JLabel lblAng = new JLabel("Cota (milésimos):");
        for (JLabel l : new JLabel[]{lblDir, lblDist, lblAng}) l.setForeground(Color.WHITE);
        JTextField txtDir = new JTextField();
        JTextField txtDist = new JTextField();
        JTextField txtAng = new JTextField("0");
        for (JTextField t : new JTextField[]{txtDir, txtDist, txtAng}) {
            t.setPreferredSize(new Dimension(160, 26));
            t.setBackground(new Color(70, 70, 70));
            t.setForeground(Color.WHITE);
        }

        String[] entidades = {"INFANTERIA", "INFANTERIA-MOTORIZADA", "INFANTERIA-ANFIBIA", "INFANTERIA-MECANIZADA", "INFANTERIA-FORTIFICADA", "INFANTERIA-RECONOCIMIENTO", "INFANTERIA-REC-MOTORIZADA", "ANTITANQUE", "ANTITANQUE-BLINDADO", "ANTITANQUE-MOTORIZADO", "ARTILLERIA", "ARTILLERIA-AUTOPROPULSADA", "ARTILLERIA-ADQ-BLANCOS", "DEFENSA-AEREA", "MORTERO", "MORTERO-MOTORIZADO", "MORTERO-ACORAZADO", "INGENIEROS", "COMUNICACIONES", "GUERRA-ELECTRONICA", "COMANDO-Y-CONTROL", "GRUPO-LOGISTICO/APOYO", "OBSERVADOR", "OBSERVADOR-ARTILLERIA", "DRON-TERRESTRE", "INSTALACION-MEDICA"};
        String[] afiliaciones = {"ALIADO", "HOSTIL", "NEUTRO", "DESCONOCIDO", "ASUMIDO-ENEMIGO", "PENDIENTE", "ASUMIDO-AMIGO"};
        String[] escalafones = {"Por Defecto", "PELOTON", "COMPANIA", "BRIGADA", "DIVISION"};

        JComboBox<String> cbEntidad = new JComboBox<>(entidades);
        JComboBox<String> cbAfiliacion = new JComboBox<>(afiliaciones);
        JComboBox<String> cbEchelon = new JComboBox<>(escalafones);
        for (JComboBox<?> cb : new JComboBox[]{cbEntidad, cbAfiliacion, cbEchelon}) {
            cb.setPreferredSize(new Dimension(220, 26));
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
        }

        JPanel panelNaturaleza = new JPanel(new GridBagLayout());
        panelNaturaleza.setBackground(new Color(50, 50, 50));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(2, 4, 2, 4);
        g2.anchor = GridBagConstraints.WEST;
        g2.gridx = 0; g2.gridy = 0; panelNaturaleza.add(new JLabel("Tipo:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEntidad, g2);
        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(new JLabel("Afiliación:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1; panelNaturaleza.add(cbAfiliacion, g2);
        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(new JLabel("Magnitud:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEchelon, g2);
        
        JLabel lblSituacion = new JLabel("Situación de movimiento:");
        lblSituacion.setForeground(Color.WHITE);
        JComboBox<SituacionMovimiento> cbSituacion = new JComboBox<>(SituacionMovimiento.values());
        cbSituacion.setBackground(new Color(70, 70, 70));
        cbSituacion.setForeground(Color.WHITE);
        cbSituacion.setSelectedItem(SituacionMovimiento.DESCONOCIDO);

        JLabel lblInfo = new JLabel("Información adicional:");
        lblInfo.setForeground(Color.WHITE);
        JTextArea txtInfo = new JTextArea();
        addPlaceholder(txtInfo, "Información adicional necesaria");
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(new Color(70, 70, 70));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setCaretColor(Color.WHITE);
        txtInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        txtInfo.setPreferredSize(new Dimension(240, 120));

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(panelNombre, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(panelNaturaleza, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblDir, gbc);
        gbc.gridx = 1; panel.add(txtDir, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblDist, gbc);
        gbc.gridx = 1; panel.add(txtDist, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblAng, gbc);
        gbc.gridx = 1; panel.add(txtAng, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblSituacion, gbc);
        gbc.gridx = 1; panel.add(cbSituacion, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblInfo, gbc);
        gbc.gridx = 1; panel.add(new JScrollPane(txtInfo), gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
        botones.setBackground(new Color(50, 50, 50));
        botones.add(btnAceptar);
        botones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(botones, gbc);

        btnAceptar.addActionListener(e -> {
            try {
                double direccion = Double.parseDouble(txtDir.getText().trim());
                double distancia = Double.parseDouble(txtDist.getText().trim());
                double angulo = Double.parseDouble(txtAng.getText().trim());
                coordRectangulares refCoord = (coordRectangulares) referencia.getCoordenadas();
                coordPolares polar = new coordPolares(direccion, distancia, angulo, refCoord);
                coordRectangulares nuevas = polar.toRectangulares();
                String nombre = txtNombre.getText().trim();
                String entidad = (String) cbEntidad.getSelectedItem();
                String afiliacion = (String) cbAfiliacion.getSelectedItem();
                String echelon = (String) cbEchelon.getSelectedItem();
                String naturaleza = entidad + "_" + afiliacion;
               
                if (!echelon.equals("Por Defecto")) naturaleza += "_" + echelon.toUpperCase();

                Blanco nuevo = new Blanco(nombre, nuevas, naturaleza, LocalDateTime.now().toString());
                nuevo.setSimID(CodigosMilitares.obtenerSIDC(naturaleza));
                nuevo.setSituacionMovimiento((SituacionMovimiento) cbSituacion.getSelectedItem());
                nuevo.setOrientacion(Double.parseDouble(txtOrient.getText().trim()));
                nuevo.setUltEntidad(entidad);
                nuevo.setUltAfiliacion(afiliacion);
                nuevo.setUltEchelon(echelon);

                String info = txtInfo.getText().trim();
                if (info.equals("Información adicional necesaria")) info = "";
                nuevo.setInformacionAdicional(info);
                listaDeBlancos.add(nuevo);
                modeloListaBlancos.addElement(nuevo);
                panelMapa.agregarBlanco(nuevo);
                dialog.dispose();
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Datos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    @SuppressWarnings("serial")
	private void mostrarDialogoMedir(Blanco b) {
    	
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Medir distancia desde: " + b.getNombre(), true);
        dialog.setSize(300, 200);
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
			private static final long serialVersionUID = 1L;

			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Blanco) {
                    Blanco b = (Blanco) value;
                    setText(b.NombretoString());
                }
                setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
                setForeground(Color.WHITE);
                return this;
            }
        });
        for (Blanco a : listaDeBlancos) {
            if (a != b) comboBlancos.addItem(a);
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
            	sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Seleccione un blanco destino válido.", "Error", JOptionPane.ERROR_MESSAGE);               
                return;
            }

            try {
                double distancia = b.getCoordenadas().distanciaA(b2.getCoordenadas());
                JOptionPane.showMessageDialog(
                        dialog,
                        String.format("Distancia entre %s y %s:\n%.0f metros",
                                b.getNombre(),
                                b2.getNombre(),
                                distancia),
                        "Resultado",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dialog.dispose();
                Coordinate c1 = new Coordinate(b.getCoordenadas().getX(),b.getCoordenadas().getY());
                Coordinate c2 = new Coordinate(b2.getCoordenadas().getX(),b2.getCoordenadas().getY());
                String nombreLinea = "Medición: " + b.getNombre() + " → " + b2.getNombre();
                Linea nuevaLinea = new Linea(nombreLinea, c1, c2, distancia);
                listaDePoligonales.add(nuevaLinea);
                modeloListaPoligonales.addElement(nuevaLinea);
                panelMapa.agregarPoligonal(nuevaLinea);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Error al calcular la distancia:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                sonidos.clickError();
            }
        });
        btnCancelar.addActionListener(e -> dialog.dispose());

        JRootPane rootPane = dialog.getRootPane();
        rootPane.setDefaultButton(btnAceptar);
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {

			@Override
            public void actionPerformed(ActionEvent e) {
                btnCancelar.doClick();
            }
        });
        dialog.setVisible(true);
    }
    
    @SuppressWarnings("serial")
	private void mostrarDialogoEditar(Blanco b) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Editar Blanco", true);
        dialog.setSize(660, 580);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblOrient = new JLabel("Orientación:");
        lblOrient.setForeground(Color.WHITE);
        JTextField txtOrient = new JTextField(String.valueOf(b.getOrientacion()));
        txtOrient.setPreferredSize(new Dimension(80, 28));
        txtOrient.setBackground(new Color(70, 70, 70));
        txtOrient.setForeground(Color.WHITE);

        JTextField txtNombre = new JTextField(b.getNombre());
        txtNombre.setBackground(new Color(70, 70, 70));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setPreferredSize(new Dimension(240, 28));

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelNombre.setBackground(new Color(50, 50, 50));
        panelNombre.add(lblOrient);
        panelNombre.add(txtOrient);
        panelNombre.add(txtNombre);
        
        JTextField txtX = new JTextField(String.valueOf(b.getCoordenadas().getX()));
        JTextField txtY = new JTextField(String.valueOf(b.getCoordenadas().getY()));
        for (JTextField f : new JTextField[]{txtX, txtY}) {
            f.setPreferredSize(new Dimension(300, 28));
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
            f.setEditable(false);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFechaAct = new JTextField(dtf.format(LocalDateTime.now()));
        txtFechaAct.setEditable(false);
        txtFechaAct.setBackground(new Color(70, 70, 70));
        txtFechaAct.setForeground(Color.WHITE);
        txtFechaAct.setPreferredSize(new Dimension(300, 28));
        
        String[] entidades = {"INFANTERIA", "INFANTERIA-MOTORIZADA", "INFANTERIA-ANFIBIA", "INFANTERIA-MECANIZADA", "INFANTERIA-FORTIFICADA", "INFANTERIA-RECONOCIMIENTO", "INFANTERIA-REC-MOTORIZADA", "ANTITANQUE", "ANTITANQUE-BLINDADO", "ANTITANQUE-MOTORIZADO", "ARTILLERIA", "ARTILLERIA-AUTOPROPULSADA", "ARTILLERIA-ADQ-BLANCOS", "DEFENSA-AEREA", "MORTERO", "MORTERO-MOTORIZADO", "MORTERO-ACORAZADO", "INGENIEROS", "COMUNICACIONES", "GUERRA-ELECTRONICA", "COMANDO-Y-CONTROL", "GRUPO-LOGISTICO/APOYO", "OBSERVADOR", "OBSERVADOR-ARTILLERIA", "DRON-TERRESTRE", "INSTALACION-MEDICA"};
        String[] afiliaciones = {"ALIADO", "HOSTIL", "NEUTRO", "DESCONOCIDO", "ASUMIDO-ENEMIGO", "PENDIENTE", "ASUMIDO-AMIGO"};
        String[] escalafones = {"Por Defecto", "PELOTON", "COMPANIA", "BRIGADA", "DIVISION"};

        JComboBox<String> cbEntidad = new JComboBox<>(entidades);
        cbEntidad.setSelectedItem(b.getUltEntidad());
        
        JComboBox<String> cbAfiliacion = new JComboBox<>(afiliaciones);
        cbAfiliacion.setSelectedItem(b.getUltAfiliacion());
        
        JComboBox<String> cbEchelon = new JComboBox<>(escalafones);
        cbEchelon.setSelectedItem(b.getUltEchelon());
        
        for (JComboBox<?> cb : new JComboBox[]{cbEntidad, cbAfiliacion, cbEchelon}) {
            cb.setPreferredSize(new Dimension(220, 26));
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
        }

        ListCellRenderer<String> guionRenderer = (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(
                value == null ? "" : value.replace("-", " ")
            );
            label.setOpaque(true);
            label.setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
            label.setForeground(Color.WHITE);

            return label;
        };
        
        cbEntidad.setRenderer(guionRenderer);
        cbAfiliacion.setRenderer(guionRenderer);
        
        JPanel panelNaturaleza = new JPanel(new GridBagLayout());
        panelNaturaleza.setBackground(new Color(50, 50, 50));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(2, 4, 2, 4);
        g2.anchor = GridBagConstraints.WEST;
        
        g2.gridx = 0; g2.gridy = 0; panelNaturaleza.add(new JLabel("Tipo:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEntidad, g2);
        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(new JLabel("Afiliación:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1; panelNaturaleza.add(cbAfiliacion, g2);
        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(new JLabel("Magnitud:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEchelon, g2);
        
        JLabel lblSituacion = new JLabel("Situación de movimiento:");
        lblSituacion.setForeground(Color.WHITE);
        JComboBox<SituacionMovimiento> cbSituacion = new JComboBox<>(SituacionMovimiento.values());
        cbSituacion.setBackground(new Color(70, 70, 70));
        cbSituacion.setForeground(Color.WHITE);
        cbSituacion.setSelectedItem(b.getSituacionMovimiento());

        JLabel lblInfo = new JLabel("Información adicional:");
        lblInfo.setForeground(Color.WHITE);
        JTextArea txtInfo = new JTextArea(b.getInformacionAdicional() != null && !b.getInformacionAdicional().isEmpty() ? b.getInformacionAdicional() : "Información adicional necesaria");
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(new Color(70, 70, 70));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setCaretColor(Color.WHITE);
        txtInfo.setPreferredSize(new Dimension(240, 120));
        txtInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(panelNombre, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(panelNaturaleza, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("DERECHAS:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(txtX, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("ARRIBAS:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(txtY, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Fecha actualización:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(txtFechaAct, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblSituacion, gbc);
        gbc.gridx = 1; panel.add(cbSituacion, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblInfo, gbc);
        gbc.gridx = 1; panel.add(new JScrollPane(txtInfo), gbc);
        
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setBackground(new Color(50, 50, 50));
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        btnAceptar.addActionListener(e -> {
            try {
                String entidad = (String) cbEntidad.getSelectedItem();
                String afiliacion = (String) cbAfiliacion.getSelectedItem();
                String echelon = (String) cbEchelon.getSelectedItem();

                String naturaleza = entidad + "_" + afiliacion;
                if (!echelon.equals("Por Defecto")) naturaleza += "_" + echelon.toUpperCase();

                b.setNombre(txtNombre.getText().trim());
                b.setNaturaleza(naturaleza);
                b.setFecha(txtFechaAct.getText());
                b.setSituacionMovimiento((SituacionMovimiento) cbSituacion.getSelectedItem());

                String info = txtInfo.getText().trim();
                if (info.equals("Información adicional necesaria")) info = "";
                b.setInformacionAdicional(info);

                try {
                    b.setOrientacion(Double.parseDouble(txtOrient.getText().trim()));
                } catch (Exception ex) {
                    b.setOrientacion(0);
                }

                // Guardar selección del editor, si las usás después
                b.setUltEntidad(entidad);
                b.setUltAfiliacion(afiliacion);
                b.setUltEchelon(echelon);
                b.setSimID(CodigosMilitares.obtenerSIDC(naturaleza));

                // ACTUALIZAR LISTA UI Y MAPA
                listaUIBlancos.repaint();
                panelMapa.eliminarBlanco(b);
                
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(150);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    panelMapa.agregarBlanco(b);
                                    panelMapa.repaint();
                                }
                            });
                        } catch (InterruptedException e) {}
                    }
                }).start();

                dialog.dispose();

            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error al guardar cambios:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
    
    public void actualizarBlanco(Blanco b) {
        if (b == null) return;

        int idx = modeloListaBlancos.indexOf(b);
        if (idx >= 0) modeloListaBlancos.set(idx, b);

        listaUIBlancos.repaint();
        panelMapa.eliminarBlanco(b);
        panelMapa.agregarBlanco(b);
    }	
    
    public void agregarBlanco(Blanco b) {
        if (b == null) return;
        
        if (!listaDeBlancos.contains(b)) {
            listaDeBlancos.add(b);
        }
        if (!modeloListaBlancos.contains(b)) {
            modeloListaBlancos.addElement(b);
        }
        panelMapa.agregarBlanco(b);
        listaUIBlancos.repaint();
    }
    
    private void actualizarBlancosEnMapa() {
        panelMapa.repaint();
    }
    
    private void armarPIF(Blanco b) {
        panelPIF.getDatosDeBlancoPanel().setDatosBlanco(b);

        Container parent = this.getParent();
        while (parent != null && !(parent instanceof ObservadorAdelantado)) {
            parent = parent.getParent();
        }
        if (parent instanceof ObservadorAdelantado obs) {
            obs.mostrarPanel("PEDIDO");
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
    
    private void addPlaceholder(JTextArea field, String placeholder){
    	
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
    
    public LinkedList<Blanco> getListaDeBlancos(){
    	return listaDeBlancos;
    }
}
