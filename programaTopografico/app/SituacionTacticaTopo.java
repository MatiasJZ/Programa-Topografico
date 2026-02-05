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
import dominio.Posicionable;
import dominio.Punto;
import dominio.SituacionMovimiento;
import dominio.coordPolares;
import dominio.coordRectangulares;
import dominio.poligonal;
import interfaz.MetodoAtaqueYTiroPanel;
import interfaz.PanelMapa;
import util.SoundManager;

public class SituacionTacticaTopo extends JPanel {

	private static final long serialVersionUID = 789462013392544798L;
	private DefaultListModel<Blanco> modeloListaBlancos;
    private JList<Blanco> listaUIBlancos;
    protected LinkedList<Blanco> listaDeBlancos;
    protected LinkedList<Punto> listaDePuntos;
    private PanelMapa panelMapa;
    protected LinkedList<poligonal> listaDePoligonales;
    private DefaultListModel<poligonal> modeloListaPoligonales;
    private JList<poligonal> listaUIPoligonales;
    protected String rutaArchivoMapa = "C:/Users/54293/Desktop/Archivos SARGO/mapaV1.TIF";
    protected PedidoDeFuego panelPIF;
    private SoundManager sonidos;
    private ProgramaTopografico observador;
    protected String designacionBlancoPrefijo = "AF"; // Prefijo de designación 
    protected int designacionBlancoContador = 6400;	// Contador de designación
    private JPanel panelGlobalTopografico;
    protected JLabel tooltipLabel;

    public SituacionTacticaTopo(LinkedList<Blanco> listaDeBlancos,PedidoDeFuego pif,ProgramaTopografico obs) { 

		this.observador = obs;
		
		setSize(900, 600);
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		
		listaDePuntos = new LinkedList<>();
		
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
		
		listaUIBlancos.setFixedCellHeight(50);
		
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
		
		label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		return label;
		}
		});
		
		listaUIPoligonales.setFixedCellHeight(40);
		
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
		
		JButton btnAgregar = new JButton("\u2795 AGREGAR");     
		JButton btnEliminar = new JButton("\u274C ELIMINAR");    
		JButton btnActualizar = new JButton("\u21BB REFRESCAR");
		JButton btnPIF = new JButton("GENERAR PIF");
		JButton btnConfigIP = new JButton("HARRIS"); 
		JButton btnHerramientas = new JButton("\u2692 HERRAM.");   
		JButton btnPifRapido = new JButton("PIF RAPIDO");
		
		// 2. Configuración de Fuentes y Dimensiones comunes
		Font fuenteEmoji = new Font("Segoe UI Emoji", Font.BOLD, 16);
		Dimension dimPequeña = new Dimension(135, 45);
		Dimension dimAncha = new Dimension(280, 45);

		// GRUPO A: Botones Grises (Superiores)
		for (JButton b : new JButton[]{btnAgregar, btnEliminar, btnActualizar, btnPIF}) {
		    b.setFont(fuenteEmoji);
		    b.setPreferredSize(dimPequeña);
		    b.setFocusPainted(false);
		}

		// GRUPO B: Botones Azules (Harris, Herramientas, PIF Rápido)
		Color azulOscuro = new Color(60, 60, 120);
		Color azulClaro = new Color(129,129,204);
		for (JButton b : new JButton[]{btnConfigIP, btnHerramientas, btnPifRapido}) {
		    b.setBackground(azulOscuro);
		    b.setForeground(Color.WHITE);
		    b.setFont(fuenteEmoji);
		    b.setFocusPainted(false);
		    
		    // Asignar ancho según el botón
		    if (b == btnPifRapido) {
		        b.setPreferredSize(dimAncha);
		    } else {
		        b.setPreferredSize(dimPequeña);
		    }
		}
		
		btnHerramientas.setBackground(azulClaro);

		// 3. Listeners (Acciones)
		btnPifRapido.addActionListener(e -> dialogoPIFRapido());

		btnHerramientas.addActionListener(e -> {
		    if(!panelGlobalTopografico.isShowing()) {
		        panelGlobalTopografico.show();
		        btnHerramientas.setBackground(azulClaro);
		    }
		    else {
		    	panelGlobalTopografico.hide();
		    	btnHerramientas.setBackground(azulOscuro);
		    }
		});

		btnConfigIP.addActionListener(e -> {
		    DialogoConfigRed dlg = new DialogoConfigRed(
		        SwingUtilities.getWindowAncestor(this),
		        observador.getComunicacionIP()
		    );
		    dlg.setVisible(true);
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 6, 6, 6);
		
		gbc.gridx = 0; gbc.gridy = 0; panelBotones.add(btnAgregar, gbc);
		gbc.gridx = 1; panelBotones.add(btnEliminar, gbc);
		gbc.gridx = 0; gbc.gridy = 1; panelBotones.add(btnActualizar, gbc);
		gbc.gridx = 1; panelBotones.add(btnPIF, gbc);
		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panelBotones.add(btnConfigIP, gbc);
		gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panelBotones.add(btnPifRapido,gbc); 
		gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panelBotones.add(btnHerramientas,gbc);
		
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
		
		JButton btnConfig = new JButton("\u2699 AJUSTES");
		btnConfig.setFont(fuenteEmoji);
		btnConfig.setBackground(Color.DARK_GRAY);
		btnConfig.setForeground(Color.WHITE);
		btnConfig.setSize(150, 80);
		btnConfig.setFocusPainted(false);
		
		btnConfig.addActionListener(e -> {
			dialogoConfiguracion();
		});
		
		// Panel flotante de Herramientas Topograficas
		panelGlobalTopografico = new JPanel(new GridBagLayout());
		panelGlobalTopografico.setBackground(Color.BLACK);  
		panelGlobalTopografico.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		panelGlobalTopografico.setSize(1145, 135);
		panelGlobalTopografico.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JPanel hud = new JPanel(new GridBagLayout());
		hud.setBackground(new Color(0, 0, 0, 170));  
		hud.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		hud.setSize(300, 230);

		GridBagConstraints h = new GridBagConstraints();
		h.insets = new Insets(5, 5, 5, 5);

		h.gridx = 0; h.gridy = 0; hud.add(btnAgregar, h);
		h.gridx = 1; hud.add(btnEliminar, h);

		h.gridx = 0; h.gridy = 1; hud.add(btnActualizar, h);
		h.gridx = 1; hud.add(btnPIF, h);

		h.gridx = 0; h.gridy = 2; h.gridwidth = 1;
		hud.add(btnConfigIP, h);
		
		h.gridx = 1; h.gridy = 2; h.gridwidth = 1;
		hud.add(btnHerramientas,h);
		
		h.gridx = 0; h.gridy = 3; h.gridwidth = 2;
		hud.add(btnPifRapido,h);

		layered.add(splitPane, JLayeredPane.DEFAULT_LAYER);
		layered.add(hud, JLayeredPane.PALETTE_LAYER);
		layered.add(panelGlobalTopografico, JLayeredPane.PALETTE_LAYER);
		layered.add(btnConfig,JLayeredPane.PALETTE_LAYER);

		this.addComponentListener(new ComponentAdapter() {
		    @Override
		    public void componentResized(ComponentEvent e) {

		        splitPane.setBounds(0, 0, getWidth(), getHeight());

		        hud.setLocation(getWidth() - hud.getWidth() - 20,getHeight() - hud.getHeight() - 20);
		        
		        int x = 370;
		        int y = 30;

		        panelGlobalTopografico.setLocation(x, y);
		        
		        btnConfig.setLocation(270, getHeight() - 90);
		    }
		});

		add(layered, BorderLayout.CENTER);
		
		// Botonera del panel topografico 
		
		LinkedList<JButton> botoneraPanelTopo = new LinkedList<JButton>();
		 
		JButton triangulacion = new JButton("TRIANG"); botoneraPanelTopo.addLast(triangulacion);
		JButton radiacion = new JButton("RAD"); botoneraPanelTopo.addLast(radiacion);
		JButton trilateracion = new JButton("TRILAT"); botoneraPanelTopo.addLast(trilateracion);
		JButton intInv3P = new JButton("INT-INV-3P"); botoneraPanelTopo.addLast(intInv3P);
		JButton intInv2P = new JButton("INT-INV-2P"); botoneraPanelTopo.addLast(intInv2P);
		JButton intDirMult = new JButton("INT-D-M"); botoneraPanelTopo.addLast(intDirMult);
		JButton poligonal = new JButton("POLIGONAL"); botoneraPanelTopo.addLast(poligonal);
		JButton mesaPolotting = new JButton("MESA-P"); botoneraPanelTopo.addLast(mesaPolotting);
		JButton anguloBase = new JButton("ANG-B"); botoneraPanelTopo.addLast(anguloBase);
		JButton actMag = new JButton("ACT-MAG"); botoneraPanelTopo.addLast(actMag);
		JButton registroCoordMod = new JButton("REG-C-M"); botoneraPanelTopo.addLast(registroCoordMod);
		JButton nivelTrigo = new JButton("NIVEL-T"); botoneraPanelTopo.addLast(nivelTrigo);
		JButton registroPPAL = new JButton("REG-PPAL"); botoneraPanelTopo.addLast(registroPPAL);
		
		for(JButton b : botoneraPanelTopo) {
			b.setBackground(Color.DARK_GRAY);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Arial", Font.BOLD, 10)); 
            b.setPreferredSize(new Dimension(98, 60)); 
            b.setFocusPainted(false);
			panelGlobalTopografico.add(b);
		}

		// 1. Configuración de la etiqueta (fuera del listener)
		tooltipLabel = new JLabel("");
		tooltipLabel.setOpaque(true);
		tooltipLabel.setBackground(new Color(255, 255, 255, 220)); 
		tooltipLabel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
		tooltipLabel.setSize(320, 70);
		tooltipLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tooltipLabel.setFont(new Font("Arial", Font.BOLD, 15));
		tooltipLabel.setVisible(false);
		panelMapa.getMapPane().add(tooltipLabel);

		configurarHerramientasMapa();

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
        	panelPIF.mostrarDatosDeBlanco();
        	panelPIF.getMetodoYTiroPanel().mostrarPanelPrincipal();
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
        
        JPopupMenu popupMenuPoligonal = new JPopupMenu();
        popupMenuPoligonal.setPreferredSize(new Dimension(250,160));     
        JMenuItem itemMedirP = new JMenuItem("Marcar Medicion");
        itemMedirP.setBackground(Color.BLACK);
        itemMedirP.setForeground(Color.WHITE);
        itemMedirP.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemInfoP = new JMenuItem("Informacion del Punto");
        itemInfoP.setBackground(Color.BLACK);
        itemInfoP.setForeground(Color.WHITE);
        itemInfoP.setFont(new Font("Arial", Font.BOLD, 15));

        popupMenuPoligonal.add(itemMedirP);
        popupMenuPoligonal.add(itemInfoP);

        itemMedirP.addActionListener(e -> {
            Posicionable bSel = (Posicionable) listaUIPoligonales.getSelectedValue();
            if (bSel != null) dialogoMedir(bSel);
        });
        itemInfoP.addActionListener(e -> {
        	Posicionable bSel = (Posicionable) listaUIPoligonales.getSelectedValue();
            if (bSel != null) dialogoInfoPunto(bSel);
        });

        listaUIBlancos.addMouseListener(new MouseAdapter() {

        	@Override public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopup(e); }
            @Override public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopup(e); }

            private void mostrarPopup(MouseEvent e) {
                int idx = listaUIBlancos.locationToIndex(e.getPoint());
                
                // Verificamos si el índice es válido Y si el punto está dentro de la celda real
                if (idx != -1 && listaUIBlancos.getCellBounds(idx, idx).contains(e.getPoint())) {
                    listaUIBlancos.setSelectedIndex(idx);
                    listaUIBlancos.requestFocusInWindow();
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                } else {
                    // Si hizo clic en el vacío, limpiamos selección y NO mostramos nada
                    listaUIBlancos.clearSelection();
                }
            }
        });
        
        listaUIPoligonales.addMouseListener(new MouseAdapter() {

        	@Override public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopupPoligonal(e); }
            @Override public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopupPoligonal(e); }

            private void mostrarPopupPoligonal(MouseEvent e) {
                // Si no es un evento de popup (clic derecho), salimos
                if (!e.isPopupTrigger()) return;

                int idx = listaUIPoligonales.locationToIndex(e.getPoint());
                
                if (idx != -1 && listaUIPoligonales.getCellBounds(idx, idx).contains(e.getPoint())) {
                    Object elemento = modeloListaPoligonales.getElementAt(idx);
                    
                    // Solo si es un Punto mostramos el menú
                    if (elemento instanceof Punto) {
                        listaUIPoligonales.setSelectedIndex(idx);
                        listaUIPoligonales.requestFocusInWindow();
                        popupMenuPoligonal.show(e.getComponent(), e.getX(), e.getY());
                    }
                } else {
                    listaUIPoligonales.clearSelection();
                }
            }
        });
    }
    
    private void configurarHerramientasMapa() {

    	panelMapa.getMapPane().setCursorTool(new CursorTool() {
		    
		    @Override
		    public void onMousePressed(MapMouseEvent ev) {
		        tooltipLabel.setVisible(true);
		        actualizarTooltip(ev);
		    }
		    @Override
		    public void onMouseDragged(MapMouseEvent ev) {
		        actualizarTooltip(ev);
		    }
		    @Override
		    public void onMouseReleased(MapMouseEvent ev) {
		        // Oculto el tooltip visual
		        tooltipLabel.setVisible(false);
		        panelMapa.getMapPane().repaint();

		        // Extraigo las coordenadas finales del evento
		        double x = ev.getWorldPos().getX();
		        double y = ev.getWorldPos().getY();
		        coordRectangulares coord = new coordRectangulares(x, y, 0);

		        // Disparo la lógica de selección (Blanco o Punto)
		        String[] opciones = {"Marcar Blanco", "Marcar Punto"};
		        Image imgRaw = new ImageIcon(getClass().getResource("/imagenPIN.png")).getImage();
		        Icon icono = new ImageIcon(imgRaw.getScaledInstance(32, 32, Image.SCALE_SMOOTH));

		        int seleccion = JOptionPane.showOptionDialog(SituacionTacticaTopo.this,
		                "Coordenadas: " + String.format("%.2f, %.2f", x, y) + "\n¿Qué desea marcar?", 
		                "Nuevo Marcador",
		                JOptionPane.DEFAULT_OPTION,
		                JOptionPane.QUESTION_MESSAGE,    ///////////////////////////////////////////////////// MODIFICAR TAMAÑO DE TODO EL DIALOGO
		                icono,                               
		                opciones,
		                opciones[0]);

		        if (seleccion == 0) {
		            dialogoAgregarBlanco(coord);
		        } else if (seleccion == 1) {
		            dialogoAgregarPunto(coord);
		        }
		    }

		    private void actualizarTooltip(MapMouseEvent ev) {
		        double x = ev.getWorldPos().getX();
		        double y = ev.getWorldPos().getY();
		        
		        tooltipLabel.setText(String.format("X: ( %.5f )  Y: ( %.5f )", x, y));
		        
		        panelMapa.getMapPane().repaint();
		    }	
		});
    }
    
    private void cambiarMapaEnTiempoReal() {
        // 1. Invocación del explorador nativo de Windows
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        FileDialog fd = new FileDialog(parentFrame, "SELECCIONAR NUEVA CARTOGRAFÍA GeoTIFF", FileDialog.LOAD);
        
        // Filtro para archivos TIFF
        fd.setFile("*.tif;*.tiff");
        fd.setDirectory("C:\\"); // Directorio inicial sugerido
        fd.setVisible(true);

        // 2. Verificamos si el usuario seleccionó un archivo o canceló
        if (fd.getFile() != null) {
            String nuevaRuta = (fd.getDirectory() + fd.getFile()).replace("\\", "/");

            // --- REINICIO DE BASE DE DATOS OPERATIVA ---
            listaDeBlancos.clear();
            listaDePuntos.clear();
            listaDePoligonales.clear();
            modeloListaBlancos.clear();
            modeloListaPoligonales.clear();

            // --- REEMPLAZO DINÁMICO DEL PANEL DE MAPA ---
            panelMapa.dispose();
            PanelMapa nuevoMapa = new PanelMapa(nuevaRuta);

            // Buscamos el JSplitPane dentro de la jerarquía de la UI para inyectar el nuevo mapa
            JSplitPane split = null;
            for (Component comp : getComponents()) {
                if (comp instanceof JLayeredPane lp) {
                    for (Component sub : lp.getComponents()) {
                        if (sub instanceof JSplitPane sp) {
                            split = sp;
                            break;
                        }
                    }
                }
            }

            if (split != null) {
                this.panelMapa = nuevoMapa;
                split.setRightComponent(panelMapa);
                
                // Re-vinculación con el panel de herramientas de tiro/observación
                panelPIF.setMapaObservacion(panelMapa);
                configurarHerramientasMapa();
            }

            // Refresco de la interfaz gráfica
            revalidate();
            repaint();
            
            System.out.println("Sistema: Nueva cartografía cargada exitosamente -> " + nuevaRuta);
        }
    }
    
    private void cambiarDesignacionEnTiempoReal() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "MODIFICAR SECUENCIA OPERATIVA", true);
        // Tamaño aumentado para uso táctil
        dialog.setSize(600, 450); 
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // FUENTES ESCALADAS
        Font fuenteEtiqueta = new Font("Arial", Font.BOLD, 22);
        Font fuenteCampo = new Font("Monospaced", Font.BOLD, 24);
        Font fuenteBoton = new Font("Arial", Font.BOLD, 20);

        // ETIQUETA PREFIJO
        JLabel lblPrefijo = new JLabel("PREFIJO (LETRAS):");
        lblPrefijo.setForeground(Color.WHITE);
        lblPrefijo.setFont(fuenteEtiqueta);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblPrefijo, gbc);

        JTextField txtPrefijo = new JTextField(designacionBlancoPrefijo);
        txtPrefijo.setBackground(new Color(60, 60, 60));
        txtPrefijo.setForeground(new Color(0, 255, 0)); // Verde neón para visibilidad
        txtPrefijo.setFont(fuenteCampo);
        txtPrefijo.setHorizontalAlignment(JTextField.CENTER);
        txtPrefijo.setPreferredSize(new Dimension(250, 60)); // Altura táctica
        gbc.gridx = 1;
        panel.add(txtPrefijo, gbc);

        // ETIQUETA CONTADOR
        JLabel lblContador = new JLabel("INICIO SECUENCIA:");
        lblContador.setForeground(Color.WHITE);
        lblContador.setFont(fuenteEtiqueta);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblContador, gbc);

        JTextField txtContador = new JTextField(String.valueOf(designacionBlancoContador));
        txtContador.setBackground(new Color(60, 60, 60));
        txtContador.setForeground(new Color(0, 255, 0));
        txtContador.setFont(fuenteCampo);
        txtContador.setHorizontalAlignment(JTextField.CENTER);
        txtContador.setPreferredSize(new Dimension(250, 60));
        gbc.gridx = 1;
        panel.add(txtContador, gbc);

        // BOTONES DE ACCIÓN
        JButton btnGuardar = new JButton("ACTUALIZAR");
        JButton btnCancelar = new JButton("ABORTAR");

        for (JButton b : new JButton[]{btnGuardar, btnCancelar}) {
            b.setFont(fuenteBoton);
            b.setPreferredSize(new Dimension(0, 80)); // Botón alto para dedos
            b.setFocusPainted(false);
        }
        btnGuardar.setBackground(new Color(40, 100, 40));
        btnGuardar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(100, 40, 40));
        btnCancelar.setForeground(Color.WHITE);

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 20, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 0, 0, 0);
        panel.add(panelBotones, gbc);

        // LOGICA DE ACCIÓN (Respetando funcionamiento original)
        btnGuardar.addActionListener(e -> {
            String prefijo = txtPrefijo.getText().trim().toUpperCase();
            String contadorStr = txtContador.getText().trim();

            if (!prefijo.matches("^[A-Z]+$")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "El prefijo solo admite letras A-Z.", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int contador = Integer.parseInt(contadorStr);
                if (contador < 0) throw new NumberFormatException();

                // Actualización de variables globales
                designacionBlancoPrefijo = prefijo;
                designacionBlancoContador = contador;

                dialog.dispose();
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "El contador debe ser un número entero positivo.", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
    
    private void dialogoConfiguracion() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "SISTEMA - CONFIGURACIÓN TÁCTICA", true);
        // Tamaño aumentado para facilitar la interacción en tablet
        dialog.setSize(750, 600); 
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Panel principal con fondo oscuro y borde refinado
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 20));
        panelPrincipal.setBackground(new Color(30, 30, 30));
        panelPrincipal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 3), // Borde más grueso
            BorderFactory.createEmptyBorder(30, 30, 30, 30) // Más aire interno
        ));

        JLabel lblHeader = new JLabel("UNIDAD DE CONTROL DE DATOS");
        lblHeader.setForeground(new Color(200, 200, 200));
        lblHeader.setFont(new Font("Consolas", Font.BOLD, 24)); // Fuente de encabezado más grande
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblHeader, BorderLayout.NORTH);

        // Contenedor de botones (Tarjetas) con mayor separación
        JPanel panelContenido = new JPanel(new GridLayout(2, 1, 25, 25));
        panelContenido.setOpaque(false);

        // Fuentes escaladas para el contenido de los botones
        // Nota: El uso de HTML en botones ignora parcialmente el setFont externo para el texto interno, 
        // por lo que ajustamos el tamaño de fuente directamente en las etiquetas HTML.
        
        JButton btnMapa = new JButton("<html><center><font size='6'>ACTUALIZAR CARTOGRAFÍA</font><br>"
                                    + "<font size='5' color='#BBBBBB'>Cargar nuevo archivo GeoTIFF</font></center></html>");
        configurarBotonMilitar(btnMapa, new Font("Arial", Font.BOLD, 22), new Color(130, 40, 40)); 
        btnMapa.setPreferredSize(new Dimension(0, 150)); // Altura táctica para tablet
        btnMapa.addActionListener(e -> {
            dialog.dispose();
            cambiarMapaEnTiempoReal();
        });

        JButton btnDesig = new JButton("<html><center><font size='6'>MODIFICAR DESIGNACIÓN</font><br>"
                                     + "<font size='5' color='#BBBBBB'>Prefijo y contador de blancos</font></center></html>");
        configurarBotonMilitar(btnDesig, new Font("Arial", Font.BOLD, 22), new Color(40, 70, 130));
        btnDesig.setPreferredSize(new Dimension(0, 150));
        btnDesig.addActionListener(e -> {
            dialog.dispose();
            cambiarDesignacionEnTiempoReal();
        });

        panelContenido.add(btnMapa);
        panelContenido.add(btnDesig);
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);

        dialog.add(panelPrincipal);
        dialog.setVisible(true);
    }

    private void configurarBotonMilitar(JButton btn, Font font, Color accentColor) {
        btn.setFont(font);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(45, 45, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Borde lateral de color (el detalle estético)
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Efecto de hover
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(new Color(60, 60, 60));
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(new Color(45, 45, 45));
            }
        });
    }

    private void pedirArchivoAMostrar() {
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/LOGOBIAC.png"));
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(100, 120, Image.SCALE_SMOOTH);
        ImageIcon icono = new ImageIcon(imgEscalada);

        Color grisFondo = new Color(25, 25, 25);
        Color grisOscuro = new Color(45, 45, 45);
        Color verdeMilitar = new Color(60, 140, 60);
        Color azulTactico = new Color(40, 70, 120);

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "SISTEMA - CONFIGURACIÓN DE CARTOGRAFÍA", true);
        dialog.setSize(800, 450); // Tamaño aumentado
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setIconImage(imgEscalada);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(grisFondo);
        panel.setBorder(BorderFactory.createLineBorder(verdeMilitar, 2));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20); // Márgenes internos amplios
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblRuta = new JLabel("CARGA DE ARCHIVO RASTER (TIFF):");
        lblRuta.setForeground(new Color(180, 180, 180));
        lblRuta.setFont(new Font("Consolas", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        panel.add(lblRuta, gbc);

        JTextField txtRuta = new JTextField();
        String placeholder = "Ejemplo: C:/DATOS/OPERATIVOS/MAPA.TIF";
        addPlaceholder(txtRuta, placeholder); // Usa tu método existente
        txtRuta.setFont(new Font("Monospaced", Font.PLAIN, 15));
        txtRuta.setBackground(Color.BLACK);
        txtRuta.setForeground(Color.WHITE);
        txtRuta.setCaretColor(verdeMilitar);
        txtRuta.setPreferredSize(new Dimension(550, 40));
        txtRuta.setBorder(BorderFactory.createLineBorder(grisOscuro));

        JButton btnExaminar = new JButton("EXPLORAR...");
        btnExaminar.setBackground(verdeMilitar);
        btnExaminar.setForeground(Color.WHITE);
        btnExaminar.setFocusPainted(false);
        btnExaminar.setFont(new Font("Arial", Font.BOLD, 12));
        btnExaminar.setPreferredSize(new Dimension(120, 40));

        gbc.gridy = 1; gbc.gridwidth = 2; gbc.gridx = 0;
        panel.add(txtRuta, gbc);
        gbc.gridx = 2; gbc.gridwidth = 1;
        panel.add(btnExaminar, gbc);

        // --- SECCIÓN DESIGNACIÓN ---
        JLabel lblDesignacion = new JLabel("DESIGNACIÓN DE SECUENCIA DE BLANCOS:");
        lblDesignacion.setForeground(new Color(180, 180, 180));
        lblDesignacion.setFont(new Font("Consolas", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        panel.add(lblDesignacion, gbc);

        JTextField txtDesignacion = new JTextField(designacionBlancoPrefijo + " " + designacionBlancoContador);
        txtDesignacion.setBackground(Color.BLACK);
        txtDesignacion.setForeground(new Color(0, 255, 0)); // Color fósforo clásico
        txtDesignacion.setFont(new Font("Monospaced", Font.BOLD, 22)); // Letra muy grande
        txtDesignacion.setHorizontalAlignment(SwingConstants.CENTER);
        txtDesignacion.setPreferredSize(new Dimension(300, 50));
        txtDesignacion.setBorder(BorderFactory.createLineBorder(verdeMilitar));

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        panel.add(txtDesignacion, gbc);

        // --- SECCIÓN BOTONES DE ACCIÓN ---
        JButton btnAceptar = new JButton("INICIALIZAR CARTOGRAFÍA");
        JButton btnCancelar = new JButton("RUTA POR DEFECTO");

        for (JButton b : new JButton[]{btnAceptar, btnCancelar}) {
            b.setFocusPainted(false);
            b.setFont(new Font("Arial", Font.BOLD, 15));
            b.setForeground(Color.WHITE);
            b.setPreferredSize(new Dimension(280, 55)); // Botones grandes
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        btnAceptar.setBackground(azulTactico);
        btnCancelar.setBackground(grisOscuro);
        btnAceptar.setBorder(BorderFactory.createLineBorder(Color.CYAN));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panelBotones.setOpaque(false);
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        gbc.gridy = 4; gbc.gridwidth = 3;
        panel.add(panelBotones, gbc);

        // Acción examinar
        btnExaminar.addActionListener(e -> {
            // Usamos FileDialog de AWT para invocar el explorador real de Windows
            FileDialog fd = new FileDialog(dialog, "SELECCIONAR ARCHIVO CARTOGRÁFICO TIFF", FileDialog.LOAD);
            
            // Filtrado de extensiones para Windows
            fd.setFile("*.tif;*.tiff");
            
            // Abrir en la unidad C o la última usada
            fd.setDirectory("C:\\");
            
            fd.setVisible(true);

            // Verificamos si el usuario seleccionó un archivo o canceló
            if (fd.getFile() != null) {
                // Construimos la ruta completa
                String rutaSeleccionada = fd.getDirectory() + fd.getFile();
                
                // Actualizamos el campo de texto con el formato de barras que prefieres
                txtRuta.setText(rutaSeleccionada.replace("\\", "/"));
                txtRuta.setForeground(Color.WHITE);
            }
        });

        btnAceptar.addActionListener(e -> {
            String rutaIngresada = txtRuta.getText().trim();
            if (rutaIngresada.isEmpty() || rutaIngresada.equals(placeholder)) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "ERROR: RUTA NO VÁLIDA", "FALLO DE SISTEMA", JOptionPane.ERROR_MESSAGE, icono);
                return;
            }
            if (!rutaIngresada.toLowerCase().endsWith(".tif") && !rutaIngresada.toLowerCase().endsWith(".tiff")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "ERROR: EXTENSIÓN TIFF REQUERIDA", "FALLO DE SISTEMA", JOptionPane.ERROR_MESSAGE, icono);
                return;
            }

            String designacion = txtDesignacion.getText().trim();
            String[] partes = designacion.split(" ");
            if (partes.length != 2 || !partes[0].matches("^[A-Z]+$")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "ERROR: FORMATO DE DESIGNACIÓN INVÁLIDO\n(DEBE SER: [LETRAS] [NÚMEROS])", "FALLO DE DATOS", JOptionPane.ERROR_MESSAGE, icono);
                return;
            }

            try {
                int contador = Integer.parseInt(partes[1]);
                if (contador < 1) throw new NumberFormatException();
                
                rutaArchivoMapa = rutaIngresada.replace("\\", "/");
                designacionBlancoPrefijo = partes[0].toUpperCase();
                designacionBlancoContador = contador;
                dialog.dispose();
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "ERROR: EL CONTADOR DEBE SER UN ENTERO POSITIVO", "FALLO DE DATOS", JOptionPane.ERROR_MESSAGE, icono);
            }
        });

        btnCancelar.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "SISTEMA: UTILIZANDO PARÁMETROS PREESTABLECIDOS\n" + rutaArchivoMapa, "AVISO TÁCTICO", JOptionPane.INFORMATION_MESSAGE, icono);
            dialog.dispose();
        });

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int resp = JOptionPane.showConfirmDialog(dialog, "¿DESEA ABORTAR LA OPERACIÓN Y SALIR?", "CONFIRMAR SALIDA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icono);
                if (resp == JOptionPane.YES_OPTION) System.exit(0);
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

        // 1. ESCALADO DE FUENTES (+40%)
        Font fTitulo = new Font("Arial", Font.BOLD, 25);   
        Font fTexto = new Font("Consolas", Font.PLAIN, 21); 

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.BLACK);
        contenido.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // PANEL DE INFORMACIÓN PRINCIPAL
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

        int separacion = 12;

        panelBlanco.add(crearLinea2("Nombre: ", b.getNombre(), fTexto));
        panelBlanco.add(crearLinea2("Fecha Creación: ", b.getFechaDeActualizacion(), fTexto));
        panelBlanco.add(crearLinea2("SIM ID (SIDC): ", b.getSimID(), fTexto));
        panelBlanco.add(crearLinea2("Orientación: ", String.format("%.2f mils", b.getOrientacion()), fTexto));
        panelBlanco.add(Box.createVerticalStrut(separacion));
        
        panelBlanco.add(crearLinea2("Coordenadas: ", "", fTexto));
        panelBlanco.add(crearLinea2("  - DERECHAS (X): ", String.format("%.6f", b.getCoordenadas().getX()), fTexto));
        panelBlanco.add(crearLinea2("  - ARRIBAS (Y): ", String.format("%.6f", b.getCoordenadas().getY()), fTexto));
        panelBlanco.add(crearLinea2("  - COTA (Z): ", String.format("%.2f m", b.getCoordenadas().getCota()), fTexto)); 
        panelBlanco.add(Box.createVerticalStrut(separacion));

        panelBlanco.add(crearLinea2("Naturaleza: ", b.getNaturaleza(), fTexto));
        panelBlanco.add(crearLinea2("  - Tipo (Entidad): ", b.getUltEntidad(), fTexto));
        panelBlanco.add(crearLinea2("  - Afiliación: ", b.getUltAfiliacion(), fTexto));
        panelBlanco.add(crearLinea2("  - Magnitud : ", b.getUltEchelon(), fTexto));
        panelBlanco.add(crearLinea2("  - Situación Mov.: ", String.valueOf(b.getSituacionMovimiento()), fTexto));

        contenido.add(panelBlanco);
        contenido.add(Box.createVerticalStrut(20));
        
        // PANEL DE INFORMACIÓN ADICIONAL
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
        
        String info = (b.getInformacionAdicional() == null || b.getInformacionAdicional().trim().isEmpty()) 
                      ? "Ninguna información adicional registrada." : b.getInformacionAdicional();
        
        JTextArea txtInfo = new JTextArea(info);
        txtInfo.setFont(fTexto);
        txtInfo.setBackground(new Color(50, 50, 50));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setEditable(false);
        txtInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); 
        scrollInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        // Barra deslizadora interna ancha
        scrollInfo.getVerticalScrollBar().setPreferredSize(new Dimension(30, 0));

        panelInfoAdicional.add(scrollInfo);
        contenido.add(panelInfoAdicional);

        // 3. SCROLL PRINCIPAL Y BARRA DESLIZADORA GENERAL (TÁCTICA)
        JScrollPane scrollPrincipal = new JScrollPane(contenido);
        // Barra de desplazamiento general muy ancha para facilitar el uso
        scrollPrincipal.getVerticalScrollBar().setPreferredSize(new Dimension(40, 0)); 
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(20); // Scroll más fluido

        dialogo.getContentPane().add(scrollPrincipal);
        
        // Aumentamos el tamaño para evitar que los datos largos se corten a la derecha
        dialogo.setSize(1000, 800); 
        dialogo.setLocationRelativeTo(parentFrame);
        dialogo.setVisible(true);
    }
    
    private JPanel crearLinea2(String etiqueta, String valor, Font f) {
        // Usamos FlowLayout con alineación a la izquierda
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linea.setBackground(Color.BLACK);

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(f);
        lblEtiqueta.setForeground(Color.LIGHT_GRAY);
        
        // AUMENTADO: De 180 a 280 para evitar que el texto pise al valor
        // La altura sube a 30 para acomodar el tamaño de la fuente de 21px
        lblEtiqueta.setPreferredSize(new Dimension(280, 30)); 

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(f);
        lblValor.setForeground(Color.WHITE);

        linea.add(lblEtiqueta);
        linea.add(lblValor);
        return linea;
    }
    
    private void dialogoInfoPunto(Posicionable p) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this); 
        JDialog dialogo = new JDialog(parentFrame, "Detalle del Punto: " + p.getNombre(), true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 1. ESCALADO DE FUENTES (+40%)
        Font fTitulo = new Font("Arial", Font.BOLD, 25);   
        Font fTexto = new Font("Consolas", Font.PLAIN, 21); 

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.BLACK);
        // Margen más amplio para acompañar el tamaño de letra
        contenido.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // PANEL DE INFORMACIÓN BÁSICA
        JPanel panelPunto = new JPanel();
        panelPunto.setLayout(new BoxLayout(panelPunto, BoxLayout.Y_AXIS));
        panelPunto.setBackground(Color.BLACK);
        panelPunto.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "DATOS DEL PUNTO",
                        0, 0, fTitulo, Color.WHITE
                )
        );

        // Nombre y separadores escalados
        panelPunto.add(crearLinea2("Nombre: ", p.getNombre(), fTexto));
        panelPunto.add(Box.createVerticalStrut(15));
        
        // Coordenadas con formato de alta precisión
        panelPunto.add(crearLinea2("Coordenadas: ", "", fTexto));
        panelPunto.add(crearLinea2("  - DERECHAS (X): ", String.format("%.6f", p.getCoordenadas().getX()), fTexto));
        panelPunto.add(crearLinea2("  - ARRIBAS (Y): ", String.format("%.6f", p.getCoordenadas().getY()), fTexto));
        
        // Cota (Z)
        panelPunto.add(crearLinea2("  - COTA (Z): ", String.format("%.2f m", p.getCoordenadas().getCota()), fTexto)); 

        contenido.add(panelPunto);

        // 2. USO DE SCROLL TÁCTICO (Previsión por si la resolución es baja o el nombre es largo)
        JScrollPane scrollPrincipal = new JScrollPane(contenido);
        scrollPrincipal.getVerticalScrollBar().setPreferredSize(new Dimension(40, 0)); 
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(20);

        dialogo.getContentPane().add(scrollPrincipal);
        
        // 3. TAMAÑO AJUSTADO PARA FUENTES GRANDES
        // Ancho suficiente para que las coordenadas no se corten
        dialogo.setSize(850, 450); 
        dialogo.setLocationRelativeTo(parentFrame);
        dialogo.setVisible(true);
    }
    
    @SuppressWarnings("serial")
    private void dialogoAgregarBlanco(coordRectangulares coordInicial) {

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Nuevo Blanco", true);
        // Tamaño aumentado para acomodar componentes más grandes
        dialog.setSize(850, 750); 
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); // Insets aumentados para mejor espaciado
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // DEFINICIÓN DE FUENTES ESCALADAS
        Font fuenteGrande = new Font("Arial", Font.BOLD, 18);
        Font fuenteMedia = new Font("Arial", Font.PLAIN, 16);

        // ORIENTACIÓN + NOMBRE
        JLabel lblOrient = new JLabel("Orientación:");
        lblOrient.setForeground(Color.WHITE);
        lblOrient.setFont(fuenteGrande);

        JTextField txtOrient = new JTextField();
        addPlaceholder(txtOrient, "mils");
        txtOrient.setPreferredSize(new Dimension(120, 40)); // Dimensiones aumentadas
        txtOrient.setBackground(new Color(70, 70, 70));
        txtOrient.setForeground(Color.WHITE);
        txtOrient.setFont(fuenteMedia);

        JTextField txtNombre = new JTextField();
        addPlaceholder(txtNombre, designacionBlancoPrefijo +" "+ designacionBlancoContador);
        txtNombre.setBackground(new Color(70, 70, 70));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setPreferredSize(new Dimension(300, 40)); // Dimensiones aumentadas
        txtNombre.setFont(fuenteMedia);

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
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
        txtFecha.setFont(fuenteMedia);
        txtFecha.setPreferredSize(new Dimension(0, 40));

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
            f.setFont(fuenteMedia);
            f.setPreferredSize(new Dimension(150, 35)); // Aumentado
        }

        txtCota.setPreferredSize(new Dimension(120, 35)); 
        
        JPanel panelCoordenadas = new JPanel(new GridBagLayout());
        panelCoordenadas.setBackground(new Color(50, 50, 50));
        GridBagConstraints gCoord = new GridBagConstraints();
        gCoord.insets = new Insets(2, 2, 4, 8); 
        gCoord.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCota = new JLabel("COTA");
        lblCota.setForeground(Color.WHITE);
        lblCota.setFont(fuenteGrande);
        lblCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        gCoord.gridx = 2; gCoord.gridy = 0; gCoord.weightx = 0;
        panelCoordenadas.add(lblCota, gCoord);

        gCoord.gridx = 2; gCoord.gridy = 1; gCoord.insets = new Insets(0, 0, 0, 8); 
        panelCoordenadas.add(txtCota, gCoord);
        
        gCoord.gridx = 0; gCoord.gridy = 0; gCoord.gridwidth = 2; gCoord.weightx = 1.0;
        gCoord.insets = new Insets(0, 0, 4, 6); 
        panelCoordenadas.add(txtX, gCoord);

        gCoord.gridx = 0; gCoord.gridy = 1; gCoord.insets = new Insets(0, 0, 0, 6);
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
            cb.setPreferredSize(new Dimension(350, 45)); // Tamaño aumentado
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
            cb.setFont(fuenteMedia);
        }

        cbEstado.setSelectedItem(SituacionMovimiento.FIJO);

        ListCellRenderer<String> guionRenderer = (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.replace("-", " "));
            label.setOpaque(true);
            label.setFont(fuenteMedia); // Fuente en lista
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
        g2.insets = new Insets(5, 5, 5, 5);
        g2.anchor = GridBagConstraints.WEST;

        JLabel lblEnt = new JLabel("Tipo:"); lblEnt.setForeground(Color.WHITE); lblEnt.setFont(fuenteGrande);
        JLabel lblAfi = new JLabel("Afiliación:"); lblAfi.setForeground(Color.WHITE); lblAfi.setFont(fuenteGrande);
        JLabel lblEsc = new JLabel("Magnitud:"); lblEsc.setForeground(Color.WHITE); lblEsc.setFont(fuenteGrande);
        JLabel lblSit = new JLabel("Estado:"); lblSit.setForeground(Color.WHITE); lblSit.setFont(fuenteGrande);

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
        lblInfo.setFont(fuenteGrande);

        JTextArea txtInfo = new JTextArea();
        addPlaceholder(txtInfo, "Información adicional necesaria");
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(new Color(70, 70, 70));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setCaretColor(Color.WHITE);
        txtInfo.setFont(fuenteMedia);
        txtInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        txtInfo.setPreferredSize(new Dimension(350, 150));

        // GRILLA PRINCIPAL
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1; panel.add(panelNombre, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1; panel.add(panelNaturaleza, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Fecha de creación:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1; panel.add(txtFecha, gbc);

        JPanel panelCoordLabels = new JPanel(new GridLayout(2, 1, 0, 10)); 
        panelCoordLabels.setBackground(new Color(50, 50, 50));
        
        JLabel lblX = new JLabel("DERECHAS:"); lblX.setForeground(Color.WHITE); lblX.setFont(fuenteGrande);
        lblX.setVerticalAlignment(SwingConstants.BOTTOM); 
        
        JLabel lblY = new JLabel("ARRIBAS:"); lblY.setForeground(Color.WHITE); lblY.setFont(fuenteGrande);
        lblY.setVerticalAlignment(SwingConstants.TOP); 

        panelCoordLabels.add(lblX);
        panelCoordLabels.add(lblY);
        
        gbc.gridx = 0; gbc.gridy++; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH; 
        panel.add(panelCoordLabels, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelCoordenadas, gbc); 
        
        gbc.gridheight = 1; gbc.gridy += 2; gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; panel.add(lblInfo, gbc);
        gbc.gridx = 1; panel.add(new JScrollPane(txtInfo), gbc);

        // BOTONES
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel botones = new JPanel(new GridLayout(1, 2, 20, 0));
        botones.setBackground(new Color(50, 50, 50));
        for(JButton b : new JButton[]{btnAceptar, btnCancelar}) {
            b.setFont(fuenteGrande);
            b.setPreferredSize(new Dimension(0, 60)); // Botones mucho más altos
            b.setFocusPainted(false);
        }
        botones.add(btnAceptar);
        botones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(botones, gbc);

        // ACCIÓN BOTÓN ACEPTAR (Lógica intacta)
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
        // Tamaño aumentado significativamente para el uso táctico en tablet
        dialog.setSize(650, 480); 
        dialog.setLocationRelativeTo(this);

        // DEFINICIÓN DE FUENTES TÁCTICAS
        Font fuenteGrande = new Font("Arial", Font.BOLD, 22);
        Font fuenteMedia = new Font("Arial", Font.PLAIN, 18);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12); // Espaciado amplio para dedos
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ETIQUETAS Y CAMPOS
        JLabel lblNombre = new JLabel("Nombre del Punto:"); 
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(fuenteGrande);

        JTextField txtNombre = new JTextField(); 
        addPlaceholder(txtNombre, "Nombre del Punto");
        txtNombre.setBackground(new Color(70,70,70)); 
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setFont(fuenteMedia);
        txtNombre.setPreferredSize(new Dimension(300, 55)); // Altura optimizada para tablet

        JLabel lblX = new JLabel("DERECHAS:"); 
        lblX.setForeground(Color.WHITE);
        lblX.setFont(fuenteGrande);

        JLabel lblY = new JLabel("ARRIBAS:"); 
        lblY.setForeground(Color.WHITE);
        lblY.setFont(fuenteGrande);

        JTextField txtX = new JTextField(String.valueOf(coordInicial.getX()));
        JTextField txtY = new JTextField(String.valueOf(coordInicial.getY()));
        
        // Estilo común para campos de coordenadas
        for (JTextField f : new JTextField[]{txtX, txtY}) {
            f.setEditable(false);
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
            f.setFont(fuenteMedia);
            f.setPreferredSize(new Dimension(300, 50));
        }

        // ORGANIZACIÓN EN EL PANEL
        gbc.gridx=0; gbc.gridy=0; panel.add(lblNombre, gbc);
        gbc.gridx=1; panel.add(txtNombre, gbc);
        
        gbc.gridx=0; gbc.gridy=1; panel.add(lblX, gbc);
        gbc.gridx=1; panel.add(txtX, gbc);
        
        gbc.gridx=0; gbc.gridy=2; panel.add(lblY, gbc);
        gbc.gridx=1; panel.add(txtY, gbc);

        // BOTONES GRANDES PARA INTERACCIÓN TÁCTIL
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        
        for (JButton b : new JButton[]{btnAceptar, btnCancelar}) {
            b.setFont(fuenteGrande);
            b.setPreferredSize(new Dimension(0, 70)); // Botones muy altos para fácil pulsación
            b.setFocusPainted(false);
        }

        JPanel botones = new JPanel(new GridLayout(1, 2, 20, 0));
        botones.setBackground(new Color(50, 50, 50));
        botones.add(btnAceptar); 
        botones.add(btnCancelar);
        
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; 
        gbc.insets = new Insets(30, 12, 10, 12); // Más separación arriba de los botones
        panel.add(botones, gbc);

        // ACCIONES
        btnAceptar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty() || nombre.equals("Nombre del Punto")) {
                sonidos.clickError();
                // El JOptionPane también heredará el LookAndFeel si lo configuraste globalmente
                JOptionPane.showMessageDialog(dialog, "Ingrese un nombre para el punto.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Punto nuevo = new Punto(coordInicial, nombre);
            
            panelMapa.agregarPoligonal(nuevo);
            listaDePoligonales.add(nuevo);
            listaDePuntos.add(nuevo); 
            
            modeloListaPoligonales.addElement(nuevo);
            
            System.out.println("Punto guardado: " + nuevo.getNombre() + ". Total puntos: " + listaDePuntos.size());
            
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    @SuppressWarnings("serial")
    private void dialogoPolares(Blanco referencia) { /// FALTA AGRANDARLO

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Marcado en Polares ", true);
        dialog.setSize(800, 740);
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
    
    private void dialogoMedir(Posicionable origen) {
    	
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Medir distancia desde: " + origen.getNombre(), true);
        // Tamaño aumentado para legibilidad táctica
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        // FUENTES ESCALADAS
        Font fuenteTitulo = new Font("Arial", Font.BOLD, 18);
        Font fuenteComponente = new Font("Arial", Font.PLAIN, 20);

        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        panelDialog.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialog.setContentPane(panelDialog);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Seleccione el destino (Blanco o Punto):");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(fuenteTitulo); 	
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelDialog.add(lblTitulo, gbc);

        JComboBox<Posicionable> comboDestinos = new JComboBox<>();
        comboDestinos.setBackground(new Color(70, 70, 70));
        comboDestinos.setForeground(Color.WHITE);
        comboDestinos.setFont(fuenteComponente);
        // Altura del combo aumentada para facilitar el toque
        comboDestinos.setPreferredSize(new Dimension(0, 60));

        comboDestinos.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Posicionable p) {
                    String tipo = (p instanceof Blanco) ? "[B] " : "[P] ";
                    setText(tipo + p.getNombre());
                }
                // Fuente más grande dentro de la lista desplegable
                setFont(fuenteComponente);
                setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
                setForeground(Color.WHITE);
                // Padding interno para que los elementos no estén pegados
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
        
        if (listaDeBlancos != null) {
            for (Blanco b : listaDeBlancos) {
                if (!b.equals(origen)) comboDestinos.addItem(b);
            }
        }
        
        if (listaDePuntos != null) {
            for (Punto p : listaDePuntos) {
                    comboDestinos.addItem(p);
            }
        }
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panelDialog.add(comboDestinos, gbc);

        // BOTONES GIGANTES PARA TABLET
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 20, 0));
        panelBotones.setBackground(new Color(50, 50, 50));
        
        for (JButton b : new JButton[]{btnAceptar, btnCancelar}) {
            b.setFont(fuenteTitulo);
            b.setPreferredSize(new Dimension(0, 80));
            b.setFocusPainted(false);
        }
        
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        panelDialog.add(panelBotones, gbc);

        // ACCIÓN DE LOS BOTONES (Lógica intacta)
        btnAceptar.addActionListener(e -> {
            Posicionable destino = (Posicionable) comboDestinos.getSelectedItem();
            if (destino == null) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Seleccione un destino válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double x1 = origen.getCoordenadas().getX();
                double y1 = origen.getCoordenadas().getY();
                double x2 = destino.getCoordenadas().getX();
                double y2 = destino.getCoordenadas().getY();

                double distancia = origen.getCoordenadas().distanciaA(destino.getCoordenadas());
                double azimutMils = calcularAzimutEnMils(x1, y1, x2, y2);

                String resultado = String.format(
                        "Distancia entre %s y %s:\n%.0f metros\n\nAD: %.0f milésimos",
                        origen.getNombre(), destino.getNombre(), distancia, azimutMils
                );

                // El diálogo de resultado también debería heredar el estilo si configuraste UIManager
                JOptionPane.showMessageDialog(dialog, resultado, "Resultado de Medición", JOptionPane.INFORMATION_MESSAGE);

                Coordinate c1 = new Coordinate();
                c1.setX(origen.getCoordenadas().getX());
                c1.setY(origen.getCoordenadas().getY());
                Coordinate c2 = new Coordinate();
                c2.setX(destino.getCoordenadas().getX());
                c2.setY(destino.getCoordenadas().getY());
                String nombreLinea = "Medición: " + origen.getNombre() + " → " + destino.getNombre();
                Linea nuevaLinea = new Linea(nombreLinea, c1,c2, distancia, azimutMils);
                
                listaDePoligonales.add(nuevaLinea);
                modeloListaPoligonales.addElement(nuevaLinea);
                panelMapa.agregarPoligonal(nuevaLinea);
                
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                sonidos.clickError();
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    @SuppressWarnings("serial")
    private void dialogoEditar(Blanco b) {

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Editar Blanco", true);
        // Tamaño aumentado para acomodar el escalado de componentes
        dialog.setSize(850, 850); 
        dialog.setLocationRelativeTo(this);

        // DEFINICIÓN DE FUENTES ESCALADAS
        Font fuenteGrande = new Font("Arial", Font.BOLD, 18);
        Font fuenteMedia = new Font("Arial", Font.PLAIN, 16);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ORIENTACIÓN + NOMBRE
        JLabel lblOrient = new JLabel("Orientación:");
        lblOrient.setForeground(Color.WHITE);
        lblOrient.setFont(fuenteGrande);

        JTextField txtOrient = new JTextField(String.valueOf(b.getOrientacion()));
        txtOrient.setPreferredSize(new Dimension(120, 45)); // Aumentado
        txtOrient.setBackground(new Color(70, 70, 70));
        txtOrient.setForeground(Color.WHITE);
        txtOrient.setFont(fuenteMedia);

        JTextField txtNombre = new JTextField(b.getNombre());
        txtNombre.setPreferredSize(new Dimension(300, 45)); // Aumentado
        txtNombre.setBackground(new Color(70, 70, 70));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setFont(fuenteMedia);

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelNombre.setBackground(new Color(50, 50, 50));
        panelNombre.add(lblOrient);
        panelNombre.add(txtOrient);
        panelNombre.add(txtNombre);

        // COORDENADAS (solo lectura X, Y - editable COTA)
        JTextField txtX = new JTextField(String.valueOf(b.getCoordenadas().getX()));
        JTextField txtY = new JTextField(String.valueOf(b.getCoordenadas().getY()));
        JTextField txtCota = new JTextField(String.valueOf(b.getCoordenadas().getCota()));
        txtCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (JTextField f : new JTextField[]{txtX, txtY, txtCota}) {
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
            f.setFont(fuenteMedia);
            f.setPreferredSize(new Dimension(200, 40)); 
        }
        txtX.setEditable(false);
        txtY.setEditable(false);
        txtCota.setEditable(true);
        txtCota.setPreferredSize(new Dimension(120, 40)); 

        // PANEL DE COORDENADAS
        JPanel panelCoordenadas = new JPanel(new GridBagLayout());
        panelCoordenadas.setBackground(new Color(50, 50, 50));
        GridBagConstraints gCoord = new GridBagConstraints();
        gCoord.insets = new Insets(0, 0, 5, 10); 
        gCoord.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCota = new JLabel("COTA");
        lblCota.setForeground(Color.WHITE);
        lblCota.setFont(fuenteGrande);
        lblCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        gCoord.gridx = 2; gCoord.gridy = 0; gCoord.weightx = 0; 
        panelCoordenadas.add(lblCota, gCoord);

        gCoord.gridx = 2; gCoord.gridy = 1; gCoord.insets = new Insets(0, 0, 0, 10);
        panelCoordenadas.add(txtCota, gCoord);
        
        gCoord.gridx = 0; gCoord.gridy = 0; gCoord.gridwidth = 2; 
        gCoord.weightx = 1.0; 
        gCoord.insets = new Insets(0, 0, 5, 8); 
        panelCoordenadas.add(txtX, gCoord);

        gCoord.gridx = 0; gCoord.gridy = 1; 
        gCoord.insets = new Insets(0, 0, 0, 8);
        panelCoordenadas.add(txtY, gCoord);

        // FECHA ACTUALIZACIÓN
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFechaAct = new JTextField(dtf.format(LocalDateTime.now()));
        txtFechaAct.setEditable(false);
        txtFechaAct.setPreferredSize(new Dimension(350, 45));
        txtFechaAct.setBackground(new Color(70, 70, 70));
        txtFechaAct.setForeground(Color.WHITE);
        txtFechaAct.setFont(fuenteMedia);

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
            cb.setPreferredSize(new Dimension(350, 50));
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
            cb.setFont(fuenteMedia);
        }

        ListCellRenderer<String> guionRenderer = (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? "" : value.replace("-", " "));
            label.setOpaque(true);
            label.setFont(fuenteMedia);
            label.setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
            label.setForeground(Color.WHITE);
            return label;
        };
        cbEntidad.setRenderer(guionRenderer);
        cbAfiliacion.setRenderer(guionRenderer);

        // SITUACIÓN
        JLabel lblSituacion = new JLabel("Estado:");
        lblSituacion.setForeground(Color.WHITE);
        lblSituacion.setFont(fuenteGrande);

        JComboBox<SituacionMovimiento> cbEstado = new JComboBox<>(SituacionMovimiento.values());
        cbEstado.setPreferredSize(new Dimension(350, 50));
        cbEstado.setBackground(new Color(70, 70, 70));
        cbEstado.setForeground(Color.WHITE);
        cbEstado.setFont(fuenteMedia);
        cbEstado.setSelectedItem(b.getSituacionMovimiento());

        // PANEL NATURALEZA
        JPanel panelNaturaleza = new JPanel(new GridBagLayout());
        panelNaturaleza.setBackground(new Color(50, 50, 50));

        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(5, 5, 5, 5);
        g2.anchor = GridBagConstraints.WEST;

        g2.gridx = 0; g2.gridy = 0;
        panelNaturaleza.add(new JLabel("Tipo:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEntidad, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(new JLabel("Afiliación:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbAfiliacion, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(new JLabel("Magnitud:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEchelon, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(lblSituacion, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEstado, g2);

        // INFO ADICIONAL
        JLabel lblInfo = new JLabel("Información adicional:");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(fuenteGrande);

        JTextArea txtInfo = new JTextArea(
            b.getInformacionAdicional() != null && !b.getInformacionAdicional().isEmpty()
                ? b.getInformacionAdicional() : "Información adicional necesaria"
        );
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(new Color(70, 70, 70));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setCaretColor(Color.WHITE);
        txtInfo.setFont(fuenteMedia);
        txtInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        txtInfo.setPreferredSize(new Dimension(350, 150));

        // ARMADO GRILLA PRINCIPAL
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1;
        panel.add(panelNombre, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1;
        panel.add(panelNaturaleza, gbc);

        JPanel panelCoordLabels = new JPanel(new GridLayout(2, 1, 0, 8)); 
        panelCoordLabels.setBackground(new Color(50, 50, 50));
        
        JLabel lblX = new JLabel("DERECHAS:"); lblX.setForeground(Color.WHITE); lblX.setFont(fuenteGrande);
        lblX.setVerticalAlignment(SwingConstants.BOTTOM); 
        
        JLabel lblY = new JLabel("ARRIBAS:"); lblY.setForeground(Color.WHITE); lblY.setFont(fuenteGrande);
        lblY.setVerticalAlignment(SwingConstants.TOP); 

        panelCoordLabels.add(lblX);
        panelCoordLabels.add(lblY);
        
        gbc.gridx = 0; gbc.gridy++; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH; 
        panel.add(panelCoordLabels, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelCoordenadas, gbc); 
        
        gbc.gridheight = 1; gbc.gridy += 2; gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; 
        panel.add(new JLabel("Fecha act.:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1;
        panel.add(txtFechaAct, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(lblInfo, gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(txtInfo) {{ 
            setPreferredSize(new Dimension(400, 160)); 
            getVerticalScrollBar().setPreferredSize(new Dimension(35, 0));
        }}, gbc);

        // BOTONES
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 20, 0));
        panelBotones.setBackground(new Color(50, 50, 50));
        for (JButton bBtn : new JButton[]{btnAceptar, btnCancelar}) {
            bBtn.setFont(fuenteGrande);
            bBtn.setPreferredSize(new Dimension(0, 70));
            bBtn.setFocusPainted(false);
        }
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
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
                    JOptionPane.showMessageDialog(dialog, "La COTA debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                b.setNombre(txtNombre.getText().trim());
                b.setNaturaleza(naturaleza);
                b.setFecha(txtFechaAct.getText());
                b.setSituacionMovimiento((SituacionMovimiento) cbEstado.getSelectedItem());

                String infoAd = txtInfo.getText().trim();
                if (infoAd.equals("Información adicional necesaria")) infoAd = "";
                b.setInformacionAdicional(infoAd);

                try { b.setOrientacion(Double.parseDouble(txtOrient.getText().trim())); } 
                catch (Exception ex) { b.setOrientacion(0); }

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
                JOptionPane.showMessageDialog(dialog, "Error al guardar cambios:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        while (parent != null && !(parent instanceof ProgramaTopografico)) {
            parent = parent.getParent();
        }
        if (parent instanceof ProgramaTopografico obs) {
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
