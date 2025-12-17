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
import interfaz.MetodoAtaqueYTiroPanel;
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
    protected String designacionBlancoPrefijo = "AF"; // Prefijo de designación 
    protected int designacionBlancoContador = 6400;	// Contador de designación

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
		
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index,isSelected, cellHasFocus);
		
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
		
		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setBackground(Color.BLACK);
		
		JButton btnAgregar = new JButton("AGREGAR");
		JButton btnEliminar = new JButton("ELIMINAR");
		JButton btnActualizar = new JButton("REFRESCAR");
		JButton btnPIF = new JButton("GENERAR PIF");
		JButton btnConfigIP = new JButton("CONFIGURAR HARRIS");  
		JButton btnPifRapido = new JButton("PIF RAPIDO");
		
		for (JButton b : new JButton[]{btnConfigIP, btnPifRapido}) {
			b.setBackground(new Color(60, 60, 120));
			b.setForeground(Color.WHITE);
			b.setFont(new Font("Arial", Font.BOLD, 12));
			b.setPreferredSize(new Dimension(190, 32));
			b.setFocusPainted(false);
		}
		
		btnPifRapido.addActionListener(e -> { dialogoPIFRapido();
		});
		
		// abre el diálogo
		btnConfigIP.addActionListener(e -> {
		DialogoConfigRed dlg =
		new DialogoConfigRed(SwingUtilities.getWindowAncestor(this),
		                     observador.getComunicacionIP());
		dlg.setVisible(true);
		});
		
		Dimension compacto = new Dimension(120, 32);
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
		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panelBotones.add(btnConfigIP, gbc);
		gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panelBotones.add(btnPifRapido,gbc);
		
		panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);
		
		// MAPA
		pedirArchivoAMostrar();
		panelMapa = new PanelMapa(rutaArchivoMapa);
		panelPIF.setMapaObservacion(panelMapa);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelMapa);
		splitPane.setDividerLocation(250);
		splitPane.setContinuousLayout(true);

		JLayeredPane layered = new JLayeredPane();
		layered.setLayout(null);

		// El split ocupa todo el panel
		splitPane.setBounds(0, 0, getWidth(), getHeight());
		layered.add(splitPane, JLayeredPane.DEFAULT_LAYER);

		// HUD flotante inferior derecha
		JPanel hud = new JPanel(new GridBagLayout());
		hud.setBackground(new Color(0, 0, 0, 170));  
		hud.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		hud.setSize(280, 200);

		GridBagConstraints h = new GridBagConstraints();
		h.insets = new Insets(5, 5, 5, 5);

		h.gridx = 0; h.gridy = 0; hud.add(btnAgregar, h);
		h.gridx = 1; hud.add(btnEliminar, h);

		h.gridx = 0; h.gridy = 1; hud.add(btnActualizar, h);
		h.gridx = 1; hud.add(btnPIF, h);

		h.gridx = 0; h.gridy = 2; h.gridwidth = 2;
		hud.add(btnConfigIP, h);
		
		h.gridx = 0; h.gridy = 3; h.gridwidth = 2;
		hud.add(btnPifRapido,h);

		// Posicion inicial abajo derecha
		hud.setLocation(getWidth() - hud.getWidth() - 20,getHeight() - hud.getHeight() - 20);

		layered.add(hud, JLayeredPane.PALETTE_LAYER);

		this.addComponentListener(new ComponentAdapter() {
		    @Override
		    public void componentResized(ComponentEvent e) {

		        splitPane.setBounds(0, 0, getWidth(), getHeight());

		        hud.setLocation(
		                getWidth() - hud.getWidth() - 20,
		                getHeight() - hud.getHeight() - 20
		        );
		    }
		});

		add(layered, BorderLayout.CENTER);

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
                    dialogoAgregarBlanco(coord);
                } else if (seleccion == 1) {
                    dialogoAgregarPunto(coord);
                }
            }
        });

        btnAgregar.addActionListener(e -> {
        	
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

            if (seleccion == 0) dialogoAgregarBlanco(coord);
            else if (seleccion == 1) dialogoAgregarPunto(coord);
        });

        // eliminar
        btnEliminar.addActionListener(e -> {
        	
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
        actualizarBlancosEnMapa();
        });

        // PIF
        btnPIF.addActionListener(e -> {
        armarPIF(listaUIBlancos.getSelectedValue());
        
        });

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setPreferredSize(new Dimension(250,160));
        JMenuItem itemEditar = new JMenuItem("Editar Blanco Seleccionado");
        itemEditar.setBackground(Color.BLACK);
        itemEditar.setForeground(Color.WHITE);
        itemEditar.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemMarcarPolares = new JMenuItem("Marcar Nuevo Blanco en Polares");
        itemMarcarPolares.setBackground(Color.BLACK);
        itemMarcarPolares.setForeground(Color.WHITE);
        itemMarcarPolares.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemMedir = new JMenuItem("Marcar Medicion");
        itemMedir.setBackground(Color.BLACK);
        itemMedir.setForeground(Color.WHITE);
        itemMedir.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemInfoBlanco = new JMenuItem("Informacion del Blanco");
        itemInfoBlanco.setBackground(Color.BLACK);
        itemInfoBlanco.setForeground(Color.WHITE);
        itemInfoBlanco.setFont(new Font("Arial", Font.BOLD, 15));

        popupMenu.add(itemEditar);
        popupMenu.add(itemMedir);
        popupMenu.add(itemMarcarPolares);
        popupMenu.add(itemInfoBlanco);

        itemMedir.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) dialogoMedir(bSel);
        });
        itemEditar.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) dialogoEditar(bSel);
        });
        itemMarcarPolares.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) dialogoPolares(bSel);
        });
        itemInfoBlanco.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) dialogoInfoBlanco(bSel);
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

    private void pedirArchivoAMostrar() {

        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/LOGOBIAC.png"));
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
        ImageIcon icono = new ImageIcon(imgEscalada);

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Subir Mapa y Configuración", true); // Título actualizado
        dialog.setSize(650, 280); // Aumentado el alto de 220 a 280
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
        
        JLabel lblDesignacion = new JLabel("Designación de Blancos (Ej: AA 1000):");
        lblDesignacion.setForeground(Color.WHITE);
        lblDesignacion.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Inicializar con los valores globales por defecto
        JTextField txtDesignacion = new JTextField(designacionBlancoPrefijo + " " + designacionBlancoContador);
        txtDesignacion.setBackground(new Color(70, 70, 70));
        txtDesignacion.setForeground(Color.WHITE);
        txtDesignacion.setCaretColor(Color.WHITE);
        txtDesignacion.setPreferredSize(new Dimension(240, 28));
        txtDesignacion.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        panel.add(lblDesignacion, gbc);

        gbc.gridx = 0; gbc.gridy = 3; 
        panel.add(txtDesignacion, gbc);

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
        
        // La fila 4 contiene los botones
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
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
        
        // ACCIÓN ACEPTAR PRINCIPAL
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

            String designacion = txtDesignacion.getText().trim();
            String[] partes = designacion.split(" ");
            
            if (partes.length != 2) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, 
                    "La Designación de Blancos debe tener el formato: [LETRAS MAYÚSCULAS] [NÚMEROS], separados por un espacio.", 
                    "Error de Formato", 
                    JOptionPane.ERROR_MESSAGE,
                    icono);
                return;
            }
            
            String prefijo = partes[0];
            String contadorStr = partes[1];
            
            if (!prefijo.matches("^[A-Z]+$")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, 
                    "El prefijo de la Designación de Blancos debe contener solo letras mayúsculas.", 
                    "Error de Formato", 
                    JOptionPane.ERROR_MESSAGE,
                    icono);
                return;
            }
            
            int contador = 0;
            try {
                // Validar que el contador sea un número entero
                contador = Integer.parseInt(contadorStr);
                if (contador < 1) {
                    throw new NumberFormatException(); // Obliga a mostrar el error
                }
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, 
                    "El contador de la Designación de Blancos debe ser un número entero positivo.", 
                    "Error de Formato", 
                    JOptionPane.ERROR_MESSAGE,
                    icono);
                return;
            }
            
            rutaArchivoMapa = rutaIngresada.replace("\\", "/");
            designacionBlancoPrefijo = prefijo;
            designacionBlancoContador = contador;
            
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

    private void dialogoPIFRapido() {
    	JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "PIF RAPIDO", true);
        dialog.setSize(500, 540);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        //Proxima implementacion de PIF rapido

        dialog.setVisible(true);
    }
    
    private void dialogoInfoBlanco(Blanco b) {

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this); 
        JDialog dialogo = new JDialog(parentFrame, "Detalle del Blanco: " + b.getNombre(), true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.BLACK);
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font fTitulo = new Font("Arial", Font.BOLD, 18);
        Font fTexto = new Font("Consolas", Font.PLAIN, 15);

        JPanel panelBlanco = new JPanel();
        panelBlanco.setLayout(new BoxLayout(panelBlanco, BoxLayout.Y_AXIS));
        panelBlanco.setBackground(Color.BLACK);
        panelBlanco.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "INFORMACIÓN DEL BLANCO",
                        0, 0, fTitulo, Color.WHITE
                )
        );

        panelBlanco.add(crearLinea("Nombre: ", b.getNombre(), fTexto));
        panelBlanco.add(crearLinea("Fecha Creación: ", b.getFechaDeActualizacion(), fTexto));
        panelBlanco.add(crearLinea("SIM ID (SIDC): ", b.getSimID(), fTexto));
        panelBlanco.add(crearLinea("Orientación: ", String.format("%.2f mils", b.getOrientacion()), fTexto));
        panelBlanco.add(Box.createVerticalStrut(8));
        
        panelBlanco.add(crearLinea("Coordenadas: ", "", fTexto));
        panelBlanco.add(crearLinea("  - DERECHAS (X): ", String.format("%.6f", b.getCoordenadas().getX()), fTexto));
        panelBlanco.add(crearLinea("  - ARRIBAS (Y): ", String.format("%.6f", b.getCoordenadas().getY()), fTexto));
        
        panelBlanco.add(crearLinea("  - COTA (Z): ", String.format("%.2f m", b.getCoordenadas().getCota()), fTexto)); 
        panelBlanco.add(Box.createVerticalStrut(8));

        panelBlanco.add(crearLinea("Naturaleza: ", b.getNaturaleza(), fTexto));
        panelBlanco.add(crearLinea("  - Tipo (Entidad): ", b.getUltEntidad(), fTexto));
        panelBlanco.add(crearLinea("  - Afiliación: ", b.getUltAfiliacion(), fTexto));
        panelBlanco.add(crearLinea("  - Magnitud : ", b.getUltEchelon(), fTexto));
        panelBlanco.add(crearLinea("  - Situación Movimiento: ", String.valueOf(b.getSituacionMovimiento()), fTexto));

        contenido.add(panelBlanco);
        contenido.add(Box.createVerticalStrut(15));
        
        JPanel panelInfoAdicional = new JPanel();
        panelInfoAdicional.setLayout(new BoxLayout(panelInfoAdicional, BoxLayout.Y_AXIS));
        panelInfoAdicional.setBackground(Color.BLACK);
        panelInfoAdicional.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "DETALLES ADICIONALES",
                        0, 0, fTitulo, Color.WHITE
                )
        );
        
        String info = b.getInformacionAdicional();
        if (info == null || info.trim().isEmpty()) {
            info = "Ninguna información adicional registrada.";
        }
        
        // Usamos un JTextArea para manejar información larga y garantizar que se vea todo
        JTextArea txtInfo = new JTextArea(info);
        txtInfo.setFont(fTexto);
        txtInfo.setBackground(new Color(50, 50, 50));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setEditable(false);
        txtInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        scrollInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        panelInfoAdicional.add(scrollInfo);
        contenido.add(panelInfoAdicional);

        dialogo.getContentPane().add(new JScrollPane(contenido));
        dialogo.setSize(600, 550); // Tamaño ajustado para mostrar toda la info sin ser demasiado grande
        dialogo.setLocationRelativeTo(parentFrame);
        dialogo.setVisible(true);
    }
    
    private JPanel crearLinea(String etiqueta, String valor, Font f) {
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linea.setBackground(Color.BLACK);

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(f);
        lblEtiqueta.setForeground(Color.LIGHT_GRAY);
        lblEtiqueta.setPreferredSize(new Dimension(180, 20)); // Ancho fijo para alineación

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(f);
        lblValor.setForeground(Color.WHITE);

        linea.add(lblEtiqueta);
        linea.add(lblValor);
        return linea;
    }
    
    @SuppressWarnings("serial")
    private void dialogoAgregarBlanco(coordRectangulares coordInicial) {

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Nuevo Blanco", true);
        dialog.setSize(620, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ORIENTACIÓN + NOMBRE
        JLabel lblOrient = new JLabel("Orientación:");
        lblOrient.setForeground(Color.WHITE);

        JTextField txtOrient = new JTextField();
        addPlaceholder(txtOrient, "mils");
        txtOrient.setPreferredSize(new Dimension(80, 28));
        txtOrient.setBackground(new Color(70, 70, 70));
        txtOrient.setForeground(Color.WHITE);

        JTextField txtNombre = new JTextField();
        addPlaceholder(txtNombre, designacionBlancoPrefijo +" "+ designacionBlancoContador);
        txtNombre.setBackground(new Color(70, 70, 70));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setPreferredSize(new Dimension(240, 28));

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelNombre.setBackground(new Color(50, 50, 50));
        panelNombre.add(lblOrient);
        panelNombre.add(txtOrient);
        panelNombre.add(txtNombre);

        // FECHA
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFecha = new JTextField(dtf.format(LocalDateTime.now()));
        txtFecha.setEditable(false);
        txtFecha.setBackground(new Color(70, 70, 70));
        txtFecha.setForeground(Color.WHITE);

        // COORDENADAS: DERECHAS (X) y ARRIBAS (Y)
        JTextField txtX = new JTextField(String.valueOf(coordInicial.getX()));
        JTextField txtY = new JTextField(String.valueOf(coordInicial.getY()));
        JTextField txtCota = new JTextField(String.valueOf(coordInicial.getCota())); 
        txtCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Estilos para X, Y, COTA
        for (JTextField f : new JTextField[]{txtX, txtY, txtCota}) {
            f.setEditable(true);
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
            f.setPreferredSize(new Dimension(100, 26)); 
        }

        // Estilo específico para COTA
        txtCota.setPreferredSize(new Dimension(80, 26)); 
        
        JPanel panelCoordenadas = new JPanel(new GridBagLayout());
        panelCoordenadas.setBackground(new Color(50, 50, 50));
        GridBagConstraints gCoord = new GridBagConstraints();
        gCoord.insets = new Insets(0, 0, 2, 6); // Separación sutil
        gCoord.fill = GridBagConstraints.HORIZONTAL;

        // Etiqueta COTA 
        JLabel lblCota = new JLabel("COTA");
        lblCota.setForeground(Color.WHITE);
        lblCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Posicionamiento de COTA
        gCoord.gridx = 2;
        gCoord.gridy = 0;
        gCoord.weightx = 0;
        panelCoordenadas.add(lblCota, gCoord);

        gCoord.gridx = 2; 
        gCoord.gridy = 1; 
        gCoord.insets = new Insets(0, 0, 0, 6); 
        panelCoordenadas.add(txtCota, gCoord);
        
        // Posicionamiento de DERECHAS (X) y ARRIBAS (Y)
        gCoord.gridx = 0; // Columna 0
        gCoord.gridy = 0; // Primera fila
        gCoord.gridwidth = 2; // Ocupa espacio de la columna 0 y 1 para ajustarse a la izquierda
        gCoord.weightx = 1.0; // Se expande
        gCoord.insets = new Insets(0, 0, 2, 4); 
        panelCoordenadas.add(txtX, gCoord);

        gCoord.gridx = 0;
        gCoord.gridy = 1; 
        gCoord.insets = new Insets(0, 0, 0, 4);
        panelCoordenadas.add(txtY, gCoord);

        // COMBOS
        String[] entidades = {
            "INFANTERIA", "INFANTERIA-MOTORIZADA", "INFANTERIA-ANFIBIA",
            "INFANTERIA-MECANIZADA", "INFANTERIA-FORTIFICADA",
            "INFANTERIA-RECONOCIMIENTO", "INFANTERIA-REC-MOTORIZADA",
            "ANTITANQUE", "ANTITANQUE-BLINDADO", "ANTITANQUE-MOTORIZADO",
            "ARTILLERIA", "ARTILLERIA-AUTOPROPULSADA", "ARTILLERIA-ADQ-BLANCOS",
            "DEFENSA-AEREA", "MORTERO", "MORTERO-MOTORIZADO", "MORTERO-ACORAZADO",
            "INGENIEROS", "COMUNICACIONES", "GUERRA-ELECTRONICA",
            "COMANDO-Y-CONTROL", "GRUPO-LOGISTICO/APOYO",
            "OBSERVADOR", "OBSERVADOR-ARTILLERIA",
            "DRON-TERRESTRE", "INSTALACION-MEDICA"
        };

        String[] afiliaciones = {
            "HOSTIL", "ALIADO", "NEUTRO", "DESCONOCIDO",
            "ASUMIDO-ENEMIGO", "PENDIENTE", "ASUMIDO-AMIGO"
        };

        String[] escalafones = {
            "Por Defecto", "PELOTON", "COMPANIA", "GRUPO", "SECCION", "BATALLON"
        };

        JComboBox<String> cbEntidad = new JComboBox<>(entidades);
        JComboBox<String> cbAfiliacion = new JComboBox<>(afiliaciones);
        JComboBox<String> cbEchelon = new JComboBox<>(escalafones);
        JComboBox<SituacionMovimiento> cbEstado = new JComboBox<>(SituacionMovimiento.values());

        for (JComboBox<?> cb : new JComboBox[]{cbEntidad, cbAfiliacion, cbEchelon, cbEstado}) {
            cb.setPreferredSize(new Dimension(220, 26));
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
        }

        cbEstado.setSelectedItem(SituacionMovimiento.FIJO);

        // Renderer para reemplazar guión por espacio
        ListCellRenderer<String> guionRenderer = (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.replace("-", " "));
            label.setOpaque(true);
            label.setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
            label.setForeground(Color.WHITE);
            return label;
        };

        cbEntidad.setRenderer(guionRenderer);
        cbAfiliacion.setRenderer(guionRenderer);

        // PANEL NATURALEZA 
        JPanel panelNaturaleza = new JPanel(new GridBagLayout());
        panelNaturaleza.setBackground(new Color(50, 50, 50));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(2, 4, 2, 4);
        g2.anchor = GridBagConstraints.WEST;

        JLabel lblEnt = new JLabel("Tipo:"); lblEnt.setForeground(Color.WHITE);
        JLabel lblAfi = new JLabel("Afiliación:"); lblAfi.setForeground(Color.WHITE);
        JLabel lblEsc = new JLabel("Magnitud:"); lblEsc.setForeground(Color.WHITE);
        JLabel lblSit = new JLabel("Estado:"); lblSit.setForeground(Color.WHITE);

        g2.gridx = 0; g2.gridy = 0; panelNaturaleza.add(lblEnt, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEntidad, g2);

        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(lblAfi, g2);
        g2.gridx = 1; panelNaturaleza.add(cbAfiliacion, g2);

        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(lblEsc, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEchelon, g2);

        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(lblSit, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEstado, g2);

        // INFO ADICIONAL
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

        // GRILLA PRINCIPAL
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(panelNombre, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(panelNaturaleza, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Fecha de creación:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1; panel.add(txtFecha, gbc);

       
        JPanel panelCoordLabels = new JPanel(new GridLayout(2, 1, 0, 4)); // 2 filas, 1 columna, con separación
        panelCoordLabels.setBackground(new Color(50, 50, 50));
        
        JLabel lblX = new JLabel("DERECHAS:"); lblX.setForeground(Color.WHITE); 
        lblX.setVerticalAlignment(SwingConstants.BOTTOM); 
        
        JLabel lblY = new JLabel("ARRIBAS:"); lblY.setForeground(Color.WHITE);
        lblY.setVerticalAlignment(SwingConstants.TOP); 

        panelCoordLabels.add(lblX);
        panelCoordLabels.add(lblY);
        
        gbc.gridx = 0; 
        gbc.gridy++; 
        gbc.gridheight = 2; // Ocupa dos filas
        gbc.fill = GridBagConstraints.BOTH; 
        panel.add(panelCoordLabels, gbc);
        
        gbc.gridx = 1; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelCoordenadas, gbc); 
        
        gbc.gridheight = 1; 
        gbc.gridy += 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; panel.add(lblInfo, gbc);
        gbc.gridx = 1; panel.add(new JScrollPane(txtInfo), gbc);

        // BOTONES
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
        botones.setBackground(new Color(50, 50, 50));
        botones.add(btnAceptar);
        botones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(botones, gbc);

        // ACCIÓN BOTÓN ACEPTAR
        btnAceptar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Ingrese un nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if(nombre.equals(designacionBlancoPrefijo +" "+ designacionBlancoContador)) {
            	designacionBlancoContador++;
            }

            String entidad = (String) cbEntidad.getSelectedItem();
            String afiliacion = (String) cbAfiliacion.getSelectedItem();
            String echelon = (String) cbEchelon.getSelectedItem();
            String naturaleza = entidad + "_" + afiliacion;

            if (!echelon.equals("Por Defecto"))
                naturaleza += "_" + echelon.toUpperCase();
            
            double x = 0;
            double y = 0;
            double cota = 0;
            
            try {
                x = Double.parseDouble(txtX.getText().trim());
                y = Double.parseDouble(txtY.getText().trim());
                cota = Double.parseDouble(txtCota.getText().trim());
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Las coordenadas (DERECHAS, ARRIBAS, COTA) deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }

            coordRectangulares coord = new coordRectangulares(x, y, cota);
            Blanco nuevo = new Blanco(nombre, coord, naturaleza, txtFecha.getText());

            nuevo.setUltAfiliacion(afiliacion);
            nuevo.setUltEchelon(echelon);
            nuevo.setUltEntidad(entidad);
            nuevo.setSimID(CodigosMilitares.obtenerSIDC(naturaleza));
            nuevo.setSituacionMovimiento( (SituacionMovimiento) cbEstado.getSelectedItem());

            try {
                nuevo.setOrientacion(Double.parseDouble(txtOrient.getText().trim()));
            } catch (Exception ex) {
                nuevo.setOrientacion(0);
            }

            String info = txtInfo.getText().trim();
            if (info.equals("Información adicional necesaria")) info = "";
            nuevo.setInformacionAdicional(info);

            modeloListaBlancos.addElement(nuevo);
            panelMapa.agregarBlanco(nuevo);
            listaDeBlancos.add(nuevo);

            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void dialogoAgregarPunto(coordRectangulares coordInicial) {
    	
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
            
            panelMapa.agregarPoligonal(nuevo);
            listaDePoligonales.add(nuevo);
            modeloListaPoligonales.addElement(nuevo);
            dialog.dispose();
        });
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    @SuppressWarnings("serial")
    private void dialogoPolares(Blanco referencia) {

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Marcado en Polares ", true);
        dialog.setSize(600, 540);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TÍTULO
        JLabel lblTitulo = new JLabel("BLANCO DE REFERENCIA: " + referencia.getNombre());
        lblTitulo.setForeground(Color.RED);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 15f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        gbc.gridwidth = 1;

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

        // POLARES (DIR, DISTANCIA, ANGULO)
        JLabel lblDir = new JLabel("Dirección (mil):");
        JLabel lblDist = new JLabel("Distancia (m):");
        JLabel lblAng = new JLabel("Cota (mil):");
        for (JLabel l : new JLabel[]{lblDir, lblDist, lblAng}) l.setForeground(Color.WHITE);

        JTextField txtDir = new JTextField();
        JTextField txtDist = new JTextField();
        JTextField txtAng = new JTextField("0");

        for (JTextField t : new JTextField[]{txtDir, txtDist, txtAng}) {
            t.setPreferredSize(new Dimension(160, 26));
            t.setBackground(new Color(70, 70, 70));
            t.setForeground(Color.WHITE);
        }

        // COMBOS
        String[] entidades = {
            "INFANTERIA", "INFANTERIA-MOTORIZADA", "INFANTERIA-ANFIBIA",
            "INFANTERIA-MECANIZADA", "INFANTERIA-FORTIFICADA",
            "INFANTERIA-RECONOCIMIENTO", "INFANTERIA-REC-MOTORIZADA",
            "ANTITANQUE", "ANTITANQUE-BLINDADO", "ANTITANQUE-MOTORIZADO",
            "ARTILLERIA", "ARTILLERIA-AUTOPROPULSADA", "ARTILLERIA-ADQ-BLANCOS",
            "DEFENSA-AEREA", "MORTERO", "MORTERO-MOTORIZADO", "MORTERO-ACORAZADO",
            "INGENIEROS", "COMUNICACIONES", "GUERRA-ELECTRONICA",
            "COMANDO-Y-CONTROL", "GRUPO-LOGISTICO/APOYO",
            "OBSERVADOR", "OBSERVADOR-ARTILLERIA",
            "DRON-TERRESTRE", "INSTALACION-MEDICA"
        };

        String[] afiliaciones = {
            "HOSTIL", "ALIADO", "NEUTRO", "DESCONOCIDO",
            "ASUMIDO-ENEMIGO", "PENDIENTE", "ASUMIDO-AMIGO"
        };

        String[] escalafones = {
                "Por Defecto", "PELOTON", "COMPANIA", "GRUPO", "SECCION", "BATALLON"
        };

        JComboBox<String> cbEntidad = new JComboBox<>(entidades);
        JComboBox<String> cbAfiliacion = new JComboBox<>(afiliaciones);
        JComboBox<String> cbEchelon = new JComboBox<>(escalafones);
        JComboBox<SituacionMovimiento> cbSituacion = new JComboBox<>(SituacionMovimiento.values());

        for (JComboBox<?> cb : new JComboBox[]{cbEntidad, cbAfiliacion, cbEchelon, cbSituacion}) {
            cb.setPreferredSize(new Dimension(220, 26));
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
        }

        cbSituacion.setSelectedItem(SituacionMovimiento.FIJO);

        JLabel lblSituacion = new JLabel("Estado:");
        lblSituacion.setForeground(Color.WHITE);

        // PANEL NATURALEZA (Tipo + Afiliación + Magnitud + Estado)
        JPanel panelNaturaleza = new JPanel(new GridBagLayout());
        panelNaturaleza.setBackground(new Color(50, 50, 50));

        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(2, 4, 2, 4);
        g2.anchor = GridBagConstraints.WEST;

        g2.gridx = 0; g2.gridy = 0;
        panelNaturaleza.add(new JLabel("Tipo:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEntidad, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(new JLabel("Afiliación:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbAfiliacion, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(new JLabel("Magnitud:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEchelon, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(lblSituacion, g2);

        g2.gridx = 1;
        panelNaturaleza.add(cbSituacion, g2);

        // INFORMACIÓN ADICIONAL
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

        // GRILLA PRINCIPAL
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1;
        panel.add(panelNombre, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1;
        panel.add(panelNaturaleza, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(lblDir, gbc);
        gbc.gridx = 1; panel.add(txtDir, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(lblDist, gbc);
        gbc.gridx = 1; panel.add(txtDist, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(lblAng, gbc);
        gbc.gridx = 1; panel.add(txtAng, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(lblInfo, gbc);
        gbc.gridx = 1; panel.add(new JScrollPane(txtInfo), gbc);

        // BOTONES
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
        botones.setBackground(new Color(50, 50, 50));
        botones.add(btnAceptar);
        botones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(botones, gbc);

        // ACCIÓN ACEPTAR
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
                if (!echelon.equals("Por Defecto"))
                    naturaleza += "_" + echelon.toUpperCase();

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

    private double calcularAzimutEnMils(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double radianes = Math.atan2(dx, dy);

        double grados = Math.toDegrees(radianes);

        if (grados < 0) {
            grados += 360;
        }
        double mils = grados * (6400.0 / 360.0);
        return Math.round(mils);
    }
    
    @SuppressWarnings("serial")
	private void dialogoMedir(Blanco b) {
    	
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
            	double x1 = b.getCoordenadas().getX();
                double y1 = b.getCoordenadas().getY();
                double x2 = b2.getCoordenadas().getX();
                double y2 = b2.getCoordenadas().getY();
                
                double distancia = b.getCoordenadas().distanciaA(b2.getCoordenadas());
                
                double azimutMils = calcularAzimutEnMils(x1, y1, x2, y2);
                
                String resultado = String.format(
                        "Distancia entre %s y %s:\n" +
                        "%.0f metros\n\n" +
                        "AD:\n" +
                        "%.0f milésimos",
                        b.getNombre(),
                        b2.getNombre(),
                        distancia,
                        azimutMils
                );

                JOptionPane.showMessageDialog(
                        dialog,
                        resultado,
                        "Resultado de Medición",
                        JOptionPane.INFORMATION_MESSAGE
                );
                
                dialog.dispose();
                Coordinate c1 = new Coordinate(b.getCoordenadas().getX(),b.getCoordenadas().getY());
                Coordinate c2 = new Coordinate(b2.getCoordenadas().getX(),b2.getCoordenadas().getY());
                String nombreLinea = "Medición: " + b.getNombre() + " → " + b2.getNombre();
                Linea nuevaLinea = new Linea(nombreLinea, c1, c2, distancia,azimutMils);
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
    private void dialogoEditar(Blanco b) {

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
        txtNombre.setPreferredSize(new Dimension(240, 28));
        txtNombre.setBackground(new Color(70, 70, 70));
        txtNombre.setForeground(Color.WHITE);

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelNombre.setBackground(new Color(50, 50, 50));
        panelNombre.add(lblOrient);
        panelNombre.add(txtOrient);
        panelNombre.add(txtNombre);

        // COORDENADAS (solo lectura X, Y - editable COTA)
        JTextField txtX = new JTextField(String.valueOf(b.getCoordenadas().getX()));
        JTextField txtY = new JTextField(String.valueOf(b.getCoordenadas().getY()));
        
        JTextField txtCota = new JTextField(String.valueOf(b.getCoordenadas().getCota()));
        txtCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (JTextField f : new JTextField[]{txtX, txtY}) {
            f.setEditable(false);
            f.setPreferredSize(new Dimension(200, 26));
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
        }

        txtCota.setEditable(true);
        txtCota.setPreferredSize(new Dimension(80, 26)); 
        txtCota.setBackground(new Color(70, 70, 70));
        txtCota.setForeground(Color.WHITE);


        // PANEL DE COORDENADAS (Incluye TXT_X, TXT_Y, LBL_COTA, TXT_COTA)
        JPanel panelCoordenadas = new JPanel(new GridBagLayout());
        panelCoordenadas.setBackground(new Color(50, 50, 50));
        GridBagConstraints gCoord = new GridBagConstraints();
        gCoord.insets = new Insets(0, 0, 2, 6); 
        gCoord.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCota = new JLabel("COTA");
        lblCota.setForeground(Color.WHITE);
        lblCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        gCoord.gridx = 2; gCoord.gridy = 0; gCoord.weightx = 0; 
        panelCoordenadas.add(lblCota, gCoord);

        gCoord.gridx = 2; gCoord.gridy = 1; gCoord.insets = new Insets(0, 0, 0, 6);
        panelCoordenadas.add(txtCota, gCoord);
        
        gCoord.gridx = 0; gCoord.gridy = 0; gCoord.gridwidth = 2; 
        gCoord.weightx = 1.0; 
        gCoord.insets = new Insets(0, 0, 2, 4); 
        panelCoordenadas.add(txtX, gCoord);

        gCoord.gridx = 0; gCoord.gridy = 1; 
        gCoord.insets = new Insets(0, 0, 0, 4);
        panelCoordenadas.add(txtY, gCoord);


        // FECHA ACTUALIZACIÓN
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFechaAct = new JTextField(dtf.format(LocalDateTime.now()));
        txtFechaAct.setEditable(false);
        txtFechaAct.setPreferredSize(new Dimension(300, 28));
        txtFechaAct.setBackground(new Color(70, 70, 70));
        txtFechaAct.setForeground(Color.WHITE);

        // COMBOS
        String[] entidades = {
            "INFANTERIA", "INFANTERIA-MOTORIZADA", "INFANTERIA-ANFIBIA",
            "INFANTERIA-MECANIZADA", "INFANTERIA-FORTIFICADA",
            "INFANTERIA-RECONOCIMIENTO", "INFANTERIA-REC-MOTORIZADA",
            "ANTITANQUE", "ANTITANQUE-BLINDADO", "ANTITANQUE-MOTORIZADO",
            "ARTILLERIA", "ARTILLERIA-AUTOPROPULSADA", "ARTILLERIA-ADQ-BLANCOS",
            "DEFENSA-AEREA", "MORTERO", "MORTERO-MOTORIZADO", "MORTERO-ACORAZADO",
            "INGENIEROS", "COMUNICACIONES", "GUERRA-ELECTRONICA",
            "COMANDO-Y-CONTROL", "GRUPO-LOGISTICO/APOYO",
            "OBSERVADOR", "OBSERVADOR-ARTILLERIA",
            "DRON-TERRESTRE", "INSTALACION-MEDICA"
        };

        String[] afiliaciones = {
            "ALIADO", "HOSTIL", "NEUTRO", "DESCONOCIDO",
            "ASUMIDO-ENEMIGO", "PENDIENTE", "ASUMIDO-AMIGO"
        };

        String[] escalafones = {
                "Por Defecto", "PELOTON", "COMPANIA", "GRUPO", "SECCION", "BATALLON"
        };

        JComboBox<String> cbEntidad = new JComboBox<>(entidades);
        JComboBox<String> cbAfiliacion = new JComboBox<>(afiliaciones);
        JComboBox<String> cbEchelon = new JComboBox<>(escalafones);

        cbEntidad.setSelectedItem(b.getUltEntidad());
        cbAfiliacion.setSelectedItem(b.getUltAfiliacion());
        cbEchelon.setSelectedItem(b.getUltEchelon());

        for (JComboBox<?> cb : new JComboBox[]{cbEntidad, cbAfiliacion, cbEchelon}) {
            cb.setPreferredSize(new Dimension(220, 26));
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
        }

        ListCellRenderer<String> guionRenderer = (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? "" : value.replace("-", " "));
            label.setOpaque(true);
            label.setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
            label.setForeground(Color.WHITE);
            return label;
        };
        cbEntidad.setRenderer(guionRenderer);
        cbAfiliacion.setRenderer(guionRenderer);

        // SITUACIÓN
        JLabel lblSituacion = new JLabel("Estado:");
        lblSituacion.setForeground(Color.WHITE);

        JComboBox<SituacionMovimiento> cbEstado = new JComboBox<>(SituacionMovimiento.values());
        cbEstado.setPreferredSize(new Dimension(220, 26));
        cbEstado.setBackground(new Color(70, 70, 70));
        cbEstado.setForeground(Color.WHITE);
        cbEstado.setSelectedItem(b.getSituacionMovimiento());

        // PANEL NATURALEZA
        JPanel panelNaturaleza = new JPanel(new GridBagLayout());
        panelNaturaleza.setBackground(new Color(50, 50, 50));

        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(2, 4, 2, 4);
        g2.anchor = GridBagConstraints.WEST;

        g2.gridx = 0; g2.gridy = 0;
        panelNaturaleza.add(new JLabel("Tipo:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEntidad, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(new JLabel("Afiliación:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbAfiliacion, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(new JLabel("Magnitud:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEchelon, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(lblSituacion, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEstado, g2);

        // INFO ADICIONAL
        JLabel lblInfo = new JLabel("Información adicional:");
        lblInfo.setForeground(Color.WHITE);

        JTextArea txtInfo = new JTextArea(
            b.getInformacionAdicional() != null && !b.getInformacionAdicional().isEmpty()
                ? b.getInformacionAdicional()
                : "Información adicional necesaria"
        );
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(new Color(70, 70, 70));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setCaretColor(Color.WHITE);
        txtInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        txtInfo.setPreferredSize(new Dimension(240, 120));

        // ARMADO GRILLA PRINCIPAL
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1;
        panel.add(panelNombre, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1;
        panel.add(panelNaturaleza, gbc);

        // Integración del Panel de Coordenadas (DERECHAS, ARRIBAS, COTA)
        JPanel panelCoordLabels = new JPanel(new GridLayout(2, 1, 0, 4)); 
        panelCoordLabels.setBackground(new Color(50, 50, 50));
        
        JLabel lblX = new JLabel("DERECHAS:"); lblX.setForeground(Color.WHITE); 
        lblX.setVerticalAlignment(SwingConstants.BOTTOM); 
        
        JLabel lblY = new JLabel("ARRIBAS:"); lblY.setForeground(Color.WHITE);
        lblY.setVerticalAlignment(SwingConstants.TOP); 

        panelCoordLabels.add(lblX);
        panelCoordLabels.add(lblY);
        
        gbc.gridx = 0; 
        gbc.gridy++; 
        gbc.gridheight = 2; 
        gbc.fill = GridBagConstraints.BOTH; 
        panel.add(panelCoordLabels, gbc);
        
        gbc.gridx = 1; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelCoordenadas, gbc); 
        
        gbc.gridheight = 1; 
        gbc.gridy += 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; 
        panel.add(new JLabel("Fecha actualización:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1;
        panel.add(txtFechaAct, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(lblInfo, gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(txtInfo), gbc);

        // BOTONES
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setBackground(new Color(50, 50, 50));
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        // ACCIÓN ACEPTAR
        btnAceptar.addActionListener(e -> {
            try {
                String entidad = (String) cbEntidad.getSelectedItem();
                String afiliacion = (String) cbAfiliacion.getSelectedItem();
                String echelon = (String) cbEchelon.getSelectedItem();

                String naturaleza = entidad + "_" + afiliacion;
                if (!echelon.equals("Por Defecto"))
                    naturaleza += "_" + echelon.toUpperCase();
                
                double nuevaCota;
                try {
                    nuevaCota = Double.parseDouble(txtCota.getText().trim());
                    b.getCoordenadas().setCota(nuevaCota); 
                } catch (NumberFormatException ex) {
                    sonidos.clickError();
                    JOptionPane.showMessageDialog(dialog, "La COTA debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                b.setNombre(txtNombre.getText().trim());
                b.setNaturaleza(naturaleza);
                b.setFecha(txtFechaAct.getText());
                b.setSituacionMovimiento((SituacionMovimiento) cbEstado.getSelectedItem());

                String info = txtInfo.getText().trim();
                if (info.equals("Información adicional necesaria")) info = "";
                b.setInformacionAdicional(info);

                try {
                    b.setOrientacion(Double.parseDouble(txtOrient.getText().trim()));
                } catch (Exception ex) {
                    b.setOrientacion(0);
                }

                b.setUltEntidad(entidad);
                b.setUltAfiliacion(afiliacion);
                b.setUltEchelon(echelon);
                b.setSimID(CodigosMilitares.obtenerSIDC(naturaleza));

                listaUIBlancos.repaint();
                panelMapa.eliminarBlanco(b);

                new Thread(() -> {
                    try {
                        Thread.sleep(150);
                        SwingUtilities.invokeLater(() -> {
                            panelMapa.agregarBlanco(b);
                            panelMapa.repaint();
                        });
                    } catch (InterruptedException ignored) {}
                }).start();

                dialog.dispose();

            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog,
                    "Error al guardar cambios:\n" + ex.getMessage(),
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
        panelPIF.getMetodoYTiroPanel().actualizar();
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

	public MetodoAtaqueYTiroPanel getMetodoAtaqueYTiroPanel() {
		return observador.getPedidoDeFuego().getMetodoYTiroPanel();
	}
}
