package app;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.*;

import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.locationtech.jts.geom.Coordinate;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import dominio.Blanco;
import dominio.GestorCodigosSIDC;
import dominio.Linea;
import dominio.Posicionable;
import dominio.Punto;
import dominio.SituacionMovimiento;
import dominio.Vertice;
import dominio.CoordenadasPolares;
import dominio.CoordenadasRectangulares;
import dominio.Poligonal;
import interfaz.Mensajeria;
import interfaz.PanelMapa;
import util.FabricaComponentes;
import util.GestorSonido;
	
public class SituacionTacticaTopografica extends JPanel {

	private static final long serialVersionUID = 789462013392544798L;
	private DefaultListModel<Blanco> modeloListaBlancos;
    private JList<Blanco> listaUIBlancos;
    protected LinkedList<Blanco> listaDeBlancos;
    protected LinkedList<Punto> listaDePuntos;
    private PanelMapa panelMapa;
    protected LinkedList<Poligonal> listaDePoligonales;
    private DefaultListModel<Poligonal> modeloListaPoligonales;
    private JList<Poligonal> listaUIPoligonales;
    protected String rutaArchivoMapa = "C:/Users/54293/Desktop/Archivos SARGO/mapaV1.TIF";
    private GestorSonido sonidos;
    private ProgramaTopografico observador;
    protected String designacionBlancoPrefijo = "AF"; // Prefijo de designación 
    protected int designacionBlancoContador = 6400;	// Contador de designación
    private JPanel panelGlobalTopografico;
    protected JLabel tooltipLabel;
    private Mensajeria mensajeria;
    private RenderizadorListas RenderListas;
    private JPopupMenu popupMenu;      // Menú para Blancos
    private JPopupMenu popupMenuPunto;

    @SuppressWarnings("deprecation")
	public SituacionTacticaTopografica(LinkedList<Blanco> listaDeBlancos,ProgramaTopografico obs) { 
    	
    	//	Settings iniciales de Tamaño y Aspecto
    	setSize(900, 600);
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		//
    			
		//	Inicializacion de todos los atributos de clase y sus caracteristicas
		this.observador = obs;
		
		this.listaDeBlancos = listaDeBlancos;
		
		this.listaDePuntos = new LinkedList<>();
		
		this.RenderListas = new RenderizadorListas();
		
		this.modeloListaBlancos = new DefaultListModel<>();		
		this.listaUIBlancos = new JList<>(modeloListaBlancos); listaUIBlancos.setFont(new Font("Arial", Font.BOLD, 20)); listaUIBlancos.setBackground(Color.BLACK); listaUIBlancos.setFixedCellHeight(50);	
		this.listaUIBlancos.setCellRenderer(RenderListas);	
		
		this.listaDePoligonales = new LinkedList<>();
		
		this.modeloListaPoligonales = new DefaultListModel<>();		
		listaUIPoligonales = new JList<>(modeloListaPoligonales); listaUIPoligonales.setFont(new Font("Arial", Font.BOLD, 20)); listaUIPoligonales.setBackground(Color.BLACK); listaUIPoligonales.setFixedCellHeight(40);	
		listaUIPoligonales.setCellRenderer(RenderListas);
		
		sonidos = new GestorSonido();
		//		
		
		//	Declaracion de elementos visuales que maneja la clase
		JScrollPane scrollLista = new JScrollPane(listaUIBlancos);
		scrollLista.setPreferredSize(new Dimension(250, 0)); scrollLista.getViewport().setBackground(Color.BLACK);
		
		JPanel panelIzquierdo = new JPanel(new BorderLayout());
		panelIzquierdo.setBackground(Color.BLACK); panelIzquierdo.add(scrollLista, BorderLayout.CENTER);
		
		JLabel lblBlancos = new JLabel("BLANCOS", SwingConstants.CENTER);
		lblBlancos.setForeground(Color.GRAY); lblBlancos.setFont(new Font("Arial", Font.BOLD, 18));
		
		JLabel lblPuntos = new JLabel("POLIGONALES", SwingConstants.CENTER);
		lblPuntos.setForeground(Color.GRAY); lblPuntos.setFont(new Font("Arial", Font.BOLD, 18));
		
		JPanel panelListas = new JPanel(new GridBagLayout()); panelListas.setBackground(Color.BLACK);
		
		GridBagConstraints gbcList = new GridBagConstraints(); gbcList.fill = GridBagConstraints.BOTH; gbcList.insets = new Insets(2, 0, 2, 0);
		
		gbcList.gridx = 0; gbcList.gridy = 0;
		gbcList.weightx = 1; gbcList.weighty = 0;
		panelListas.add(lblBlancos, gbcList);
		
		gbcList.gridy = 1;gbcList.weighty = 0.66;
		panelListas.add(new JScrollPane(listaUIBlancos), gbcList);
		
		gbcList.gridy = 2;gbcList.weighty = 0;
		panelListas.add(lblPuntos, gbcList);
		
		gbcList.gridy = 3;gbcList.weighty = 0.33;
		panelListas.add(new JScrollPane(listaUIPoligonales), gbcList);
		
		panelIzquierdo.add(panelListas, BorderLayout.CENTER);
		
		//	Panel de Botones Inferiores Derechos
		JPanel panelBotones = new JPanel(new GridBagLayout()); panelBotones.setBackground(Color.BLACK);
		
		JButton btnAgregar = new JButton("\u2795 AGREGAR");     
		JButton btnEliminar = new JButton("\u274C ELIMINAR");    
		JButton btnActualizar = new JButton("\u21BB REFRESCAR");
		JButton btnConfigIP = new JButton("HARRIS"); 
		JButton btnHerramientas = new JButton("\u2692 HERRAM.");   
		JButton btnGenPdf = new JButton("GENERAR PDF");
		
		//	Configuración de Fuentes y Dimensiones comunes
		Font fuenteEmoji = new Font("Segoe UI Emoji", Font.BOLD, 16);
		Dimension dimPequeña = new Dimension(135, 45);
		Dimension dimAncha = new Dimension(280, 45);

		// GRUPO A: Botones Grises (Superiores)
		for (JButton b : new JButton[]{btnAgregar, btnEliminar, btnActualizar}) {
		    b.setFont(fuenteEmoji);
		    b.setPreferredSize(dimPequeña);
		    b.setFocusPainted(false);
		}

		// GRUPO B: Botones Azules (Harris, Herramientas, PIF Rápido)
		Color azulOscuro = new Color(60, 60, 120);
		Color azulClaro = new Color(129,129,204);
		for (JButton b : new JButton[]{btnConfigIP, btnHerramientas, btnGenPdf}) {
		    b.setBackground(azulOscuro);
		    b.setForeground(Color.WHITE);
		    b.setFont(fuenteEmoji);
		    b.setFocusPainted(false);
		    
		    // Asignar ancho según el botón
		    if (b == btnGenPdf) {
		        b.setPreferredSize(dimAncha);
		    } else {
		        b.setPreferredSize(dimPequeña);
		    }
		}
		
		btnHerramientas.setBackground(azulClaro);

		//	ActionListeners de los botones del panel global inferior derecho
		btnGenPdf.addActionListener(e -> {
		    if (RegistroCalculos.getBitacora().isEmpty()) {
		        sonidos.clickError();
		        JOptionPane.showMessageDialog(this, 
		            "ERROR: No hay cálculos registrados para exportar.\nRealice al menos una operación topográfica.", 
		            "SISTEMA DE REGISTRO", 
		            JOptionPane.WARNING_MESSAGE);
		    } else {
		        generarInformePDF();
		    }
		});

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
		    Red dlg = new Red(
		        SwingUtilities.getWindowAncestor(this),
		        observador.getComunicacionIP()
		    );
		    dlg.setVisible(true);
		});
		
		btnAgregar.addActionListener(e -> {
        	
            CoordenadasRectangulares coord = new CoordenadasRectangulares(0,0,0);
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

        btnEliminar.addActionListener(e -> {
            Blanco selecB = listaUIBlancos.getSelectedValue();
            Poligonal selecP = listaUIPoligonales.getSelectedValue();

            // Verifico qué hay para borrar (gracias a la selección exclusiva, solo uno será distinto de null)
            if (selecB != null) {
                listaDeBlancos.remove(selecB);
                modeloListaBlancos.removeElement(selecB);
                panelMapa.eliminarBlanco(selecB);
            } else if (selecP != null) {
                listaDePoligonales.remove(selecP);
                modeloListaPoligonales.removeElement(selecP);
                panelMapa.eliminarPoligonal(selecP);
                for(Punto p : listaDePuntos) {
                	if(selecP.getName().equals(p.getName()))
                		listaDePuntos.remove(p);
                }
            } else {
                sonidos.clickError();
                JOptionPane.showMessageDialog(this, "Seleccione un elemento para eliminar.", "SISTEMA", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Limpio cualquier rastro de selección después de borrar
            listaUIBlancos.clearSelection();
            listaUIPoligonales.clearSelection();
        });

        // actualizar
        btnActualizar.addActionListener(e -> {
        	actualizarBlancosEnMapa();
        });
        //
		
		Dimension dimAnchaTactico = new Dimension(290, 70);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 6, 8, 6); gbc.fill = GridBagConstraints.BOTH; 
		gbc.weightx = 1.0; gbc.weighty = 1.0;

		// Fila 0
		gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
		panelBotones.add(btnAgregar, gbc);
		gbc.gridx = 1;
		panelBotones.add(btnEliminar, gbc);

		// Fila 1
		gbc.gridx = 0; gbc.gridy = 1;
		panelBotones.add(btnActualizar, gbc);
		gbc.gridx = 1;
		panelBotones.add(btnConfigIP, gbc);

		// Fila 2: Los botones anchos
		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
		btnGenPdf.setPreferredSize(dimAnchaTactico);
		panelBotones.add(btnGenPdf, gbc); 

		// Fila 3
		gbc.gridx = 0; gbc.gridy = 3;
		btnHerramientas.setPreferredSize(dimAnchaTactico);
		panelBotones.add(btnHerramientas, gbc);
		
		panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);
		//
		
		// Pedido de Archivo TIFF a mostrar en el PANEL DEL MAPA
		pedirArchivoAMostrar();
		panelMapa = new PanelMapa(rutaArchivoMapa);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelMapa);
		splitPane.setDividerLocation(250);
		splitPane.setContinuousLayout(true);
		//

		JLayeredPane layered = new JLayeredPane();
		layered.setLayout(null);
		splitPane.setBounds(0, 0, getWidth(), getHeight());
				
		//	Boton de AJUSTES y su ActionListener
		JButton btnConfig = new JButton("\u2699 AJUSTES");
		btnConfig.setFont(fuenteEmoji);
		btnConfig.setBackground(Color.DARK_GRAY);
		btnConfig.setForeground(Color.WHITE);
		btnConfig.setSize(150, 80);
		btnConfig.setFocusPainted(false);
		
		btnConfig.addActionListener(e -> {
			dialogoConfiguracion();
		});
		//
		
		// Panel Global de Herramientas Topograficas
		panelGlobalTopografico = new JPanel(new GridBagLayout());
		panelGlobalTopografico.setBackground(Color.BLACK);  
		panelGlobalTopografico.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		panelGlobalTopografico.setSize(1145, 135);
		panelGlobalTopografico.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JPanel hud = new JPanel(new GridBagLayout());
		hud.setBackground(new Color(0, 0, 0, 170));  
		hud.setBorder(BorderFactory.createLineBorder(Color.WHITE));

		hud.setSize(320, 300);

		GridBagConstraints h = new GridBagConstraints();
		h.insets = new Insets(10, 10, 10, 10);
		h.fill = GridBagConstraints.BOTH; 
		h.weightx = 1.0;h.weighty = 1.0;

		// Configuración de botones en el HUD
		h.gridy = 0; h.gridx = 0; h.gridwidth = 1;
		hud.add(btnAgregar, h);
		h.gridx = 1;
		hud.add(btnEliminar, h);

		h.gridy = 1; h.gridx = 0;
		hud.add(btnActualizar, h);
		h.gridx = 1;
		hud.add(btnConfigIP, h);

		h.gridy = 2; h.gridx = 0; h.gridwidth = 2;
		hud.add(btnHerramientas, h);

		h.gridy = 3;
		hud.add(btnGenPdf, h);

		layered.add(splitPane, JLayeredPane.DEFAULT_LAYER);
		layered.add(hud, JLayeredPane.PALETTE_LAYER);
		layered.add(panelGlobalTopografico, JLayeredPane.PALETTE_LAYER);
		layered.add(btnConfig,JLayeredPane.PALETTE_LAYER);
		//
		
		//	Seteo de ubicacion de cada panel flotante y boton de AJUSTES
		this.addComponentListener(new ComponentAdapter() {
		    @Override
		    public void componentResized(ComponentEvent e) {

		        splitPane.setBounds(0, 0, getWidth(), getHeight());

		        hud.setLocation(getWidth() - hud.getWidth() - 20,getHeight() - hud.getHeight() - 20);

		        panelGlobalTopografico.setLocation(370, 30);
		        
		        btnConfig.setLocation(270, getHeight() - 90);
		    }
		});

		add(layered, BorderLayout.CENTER);
		//
		
		//	Botonera del panel topografico 
		LinkedList<JButton> botoneraPanelTopo = new LinkedList<JButton>();
		 
		JButton triangulacion = new JButton("TRIANG"); botoneraPanelTopo.addLast(triangulacion);
		JButton radiacion = new JButton("RAD"); botoneraPanelTopo.addLast(radiacion);
		JButton trilateracion = new JButton("TRILAT"); botoneraPanelTopo.addLast(trilateracion);
		JButton intInv3P = new JButton("INT-INV-3P"); botoneraPanelTopo.addLast(intInv3P);
		JButton intInv2P = new JButton("INT-INV-2P"); botoneraPanelTopo.addLast(intInv2P);
		JButton intDirMult = new JButton("INT-D-M"); botoneraPanelTopo.addLast(intDirMult);
		JButton mesaPolotting = new JButton("MESA-P"); botoneraPanelTopo.addLast(mesaPolotting);
		JButton anguloBase = new JButton("ANG-B"); botoneraPanelTopo.addLast(anguloBase);
		JButton actMag = new JButton("ACT-MAG"); botoneraPanelTopo.addLast(actMag);
		JButton nivelTrigo = new JButton("NIVEL-T"); botoneraPanelTopo.addLast(nivelTrigo);
		JButton registroPPAL = new JButton("REG-PPAL"); botoneraPanelTopo.addLast(registroPPAL);
		JButton registroCoordMod = new JButton("REG-C-M"); botoneraPanelTopo.addLast(registroCoordMod);
		
		//	Seteo de configuracion visual de cada boton y su action listener
		for(JButton b : botoneraPanelTopo) {
			
			b.setBackground(Color.DARK_GRAY);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Arial", Font.BOLD, 10)); 
            b.setPreferredSize(new Dimension(98, 60)); 
            b.setFocusPainted(false);
			panelGlobalTopografico.add(b);
		    
		    b.addActionListener(e -> {
		        String comando = b.getText();
		        switch(comando) {
		           case "TRIANG": abrirDialogoTriangulacion(); break;
		           case "RAD": abrirDialogoRadiacion(); break;
		           // case "TRILAT": abrirDialogoTrilateracion(); break;
		           case "INT-INV-3P": abrirDialogoInterseccionInversa3P(); break;
		           // case "INT-INV-2P": abrirDialogoInterseccionInversa2P(); break;
		           // case "INT-D-M": abrirDialogoInterseccionDirecta(); break;
		           // case "POLIGONAL": abrirDialogoPoligonal(); break;
		           // case "MESA-P": abrirDialogoMesaPlotting(); break;
		           // case "ANG-B": abrirDialogoAnguloBase(); break;
		           // case "ACT-MAG": abrirDialogoActualizacionMagnetica(); break;
		           // case "REG-C-M": abrirDialogoRegistroCoordMod(); break;
		           // case "NIVEL-T": abrirDialogoNivelacionTrigo(); break;
		           // case "REG-PPAL": exportarRegistroPDF(); break; // El botón de exportación
		        }
		    });
		}
		//

		//	Configuración de la etiqueta que muestra las coordenadas con el arrastre del click 
		tooltipLabel = new JLabel("");
		tooltipLabel.setOpaque(true);
		tooltipLabel.setBackground(new Color(255, 255, 255, 220)); 
		tooltipLabel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
		tooltipLabel.setSize(320, 70);
		tooltipLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tooltipLabel.setFont(new Font("Arial", Font.BOLD, 15));
		tooltipLabel.setVisible(false);
		//
		
		//	Se le añade al panel del mapa la etiqueta anterior
		panelMapa.getMapPane().add(tooltipLabel);

		//	Se configuran los controles de Zoom y Arrastre
		configurarHerramientasMapa();
		//
   
		//	Se configuran los PopUpMenu de las listas BLANCOS y POLIGONALES
		ConfigurarPopUpMenus();
		
        listaUIBlancos.addMouseListener(new MouseAdapter() {

        	@Override public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopup(e); }
            @Override public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) mostrarPopup(e); }

            private void mostrarPopup(MouseEvent e) {
                int idx = listaUIBlancos.locationToIndex(e.getPoint());
                
                if (idx != -1 && listaUIBlancos.getCellBounds(idx, idx).contains(e.getPoint())) {
                    listaUIBlancos.setSelectedIndex(idx);
                    listaUIBlancos.requestFocusInWindow();
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                } else {
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
                    Poligonal elemento = modeloListaPoligonales.getElementAt(idx);
                    
                    // Solo si es un Punto mostramos el menú
                    if (elemento.tienePopUpMenu()) {
                        listaUIPoligonales.setSelectedIndex(idx);
                        listaUIPoligonales.requestFocusInWindow();
                        popupMenuPunto.show(e.getComponent(), e.getX(), e.getY());
                    }
                } else {
                    listaUIPoligonales.clearSelection();
                }
            }
        });
    }
    
    private void ConfigurarPopUpMenus() {
    	
    	this.popupMenu = new JPopupMenu();
        popupMenu.setPreferredSize(new Dimension(250,200));
        JMenuItem itemEditar = new JMenuItem("Editar Blanco Seleccionado");
        itemEditar.setBackground(Color.BLACK);
        itemEditar.setForeground(Color.WHITE);
        itemEditar.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemMarcarPolares = new JMenuItem("Marcar Nuevo Blanco en Polares");
        itemMarcarPolares.setBackground(Color.BLACK);itemMarcarPolares.setForeground(Color.WHITE);itemMarcarPolares.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemMedir = new JMenuItem("Marcar Medicion");
        itemMedir.setBackground(Color.BLACK);
        itemMedir.setForeground(Color.WHITE);
        itemMedir.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemInfoBlanco = new JMenuItem("Informacion del Blanco");
        itemInfoBlanco.setBackground(Color.BLACK);
        itemInfoBlanco.setForeground(Color.WHITE);
        itemInfoBlanco.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemEnviarBlanco = new JMenuItem("Enviar");
        itemEnviarBlanco.setBackground(Color.BLACK);
        itemEnviarBlanco.setForeground(Color.WHITE);
        itemEnviarBlanco.setFont(new Font("Arial", Font.BOLD, 15));

        popupMenu.add(itemEditar);
        popupMenu.add(itemMedir);
        popupMenu.add(itemMarcarPolares);
        popupMenu.add(itemInfoBlanco);
        popupMenu.add(itemEnviarBlanco);

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
        itemEnviarBlanco.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) enviarBlanco(bSel); 
        });
        
        this.popupMenuPunto = new JPopupMenu();
        popupMenuPunto.setPreferredSize(new Dimension(250,220));     
        JMenuItem itemMedirP = new JMenuItem("Marcar Medicion");
        itemMedirP.setBackground(Color.BLACK);
        itemMedirP.setForeground(Color.WHITE);
        itemMedirP.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemInfoP = new JMenuItem("Informacion del Punto");
        itemInfoP.setBackground(Color.BLACK);
        itemInfoP.setForeground(Color.WHITE);
        itemInfoP.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemEnviarPunto = new JMenuItem("Enviar");
        itemEnviarPunto.setBackground(Color.BLACK);
        itemEnviarPunto.setForeground(Color.WHITE);
        itemEnviarPunto.setFont(new Font("Arial", Font.BOLD, 15));
        JMenuItem itemCerrarP = new JMenuItem("Cerrar Poligonal (Cálculo de Error)");
        itemCerrarP.setBackground(new Color(45, 45, 85)); // Azul oscuro táctico
        itemCerrarP.setForeground(Color.WHITE);
        itemCerrarP.setFont(new Font("Arial", Font.BOLD, 15));

        popupMenuPunto.add(itemMedirP);
        popupMenuPunto.add(itemInfoP);
        popupMenuPunto.add(itemEnviarPunto);
        popupMenuPunto.add(itemCerrarP);

        itemMedirP.addActionListener(e -> {
            Posicionable bSel = (Posicionable) listaUIPoligonales.getSelectedValue();
            if (bSel != null) dialogoMedir(bSel);
        });
        itemInfoP.addActionListener(e -> {
        	Posicionable bSel = (Posicionable) listaUIPoligonales.getSelectedValue();
            if (bSel != null) dialogoInfoPunto(bSel);
        });
        itemEnviarPunto.addActionListener(e -> {
        	Posicionable bSel = (Posicionable) listaUIPoligonales.getSelectedValue();
            if (bSel != null) enviarPunto(bSel);
        });
        itemCerrarP.addActionListener(e -> {
            Posicionable selec = (Posicionable) listaUIPoligonales.getSelectedValue();
            if (selec != null) {
                selec.ejecutarCierrePoligonal(this);
            }
        });
    }

    @SuppressWarnings("unused")
	private void dialogoCierreControlado(Punto puntoCalculado) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "CONTROL DE PRECISIÓN AUTOMÁTICO", true);
        dialog.setSize(600, 450);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(new Color(40, 40, 40));
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Etiqueta de información dinámica
        JLabel lblInfo = new JLabel("<html><center>Seleccione el punto de origen real para comparar</center></html>");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(lblInfo, gbc);

        // 2. Combo de puntos reales (excluyendo el seleccionado)
        JComboBox<Punto> comboPuntosReales = new JComboBox<>();
        for (Punto p : listaDePuntos) {
            if (!p.equals(puntoCalculado)) comboPuntosReales.addItem(p);
        }
        comboPuntosReales.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 1; 
        dialog.add(comboPuntosReales, gbc);

        // 3. Campo de Distancia (Se actualizará solo)
        gbc.gridwidth = 1; gbc.gridy = 2;
        dialog.add(new JLabel("Distancia Poligonal (m):") {

			private static final long serialVersionUID = 1L;

		{ setForeground(Color.GRAY); }}, gbc);
        
        JTextField txtDistManual = new JTextField("0.00");
        txtDistManual.setFont(new Font("Monospaced", Font.BOLD, 20));
        txtDistManual.setBackground(new Color(60, 60, 60));
        txtDistManual.setForeground(Color.GREEN);
        gbc.gridx = 1;
        dialog.add(txtDistManual, gbc);

        /*comboPuntosReales.addActionListener(e -> {
            Punto real = (Punto) comboPuntosReales.getSelectedItem();
            if (real != null) {
                // Ejecutamos el rastreo por el grafo
                double distCamino = calcularDistanciaRecorridaRecursiva((Vertice)puntoCalculado, (Vertice)real);
                
                if (distCamino > 0) {
                    // Caso ideal: Se encontró la ruta en el mapeo
                    txtDistManual.setText(String.format("%.2f", distCamino).replace(",", "."));
                    txtDistManual.setForeground(Color.GREEN); // Feedback visual de éxito
                } else {
                    // Caso Fallido: Los puntos no están conectados en el grafo
                    double distDirecta = puntoCalculado.getCoordenadas().distanciaA(real.getCoordenadas());
                    txtDistManual.setText(String.format("%.2f", distDirecta).replace(",", "."));
                    txtDistManual.setForeground(Color.ORANGE); // Advertencia: distancia geométrica simple
                }
            }
        });*/

        // 4. Botón de Acción
        JButton btnCalcular = new JButton("GENERAR INFORME DE PRECISIÓN");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(45, 90, 45));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 10, 15);
        dialog.add(btnCalcular, gbc);

        btnCalcular.addActionListener(ev -> {
            try {
                Punto real = (Punto) comboPuntosReales.getSelectedItem();
                double distFinal = Double.parseDouble(txtDistManual.getText());

                // Llamada al motor matemático
                String informe = CalculadorTopografico.obtenerInformeError(
                    real.getCoordenadas(), 
                    puntoCalculado.getCoordenadas(), 
                    distFinal
                );

                RegistroCalculos.guardar("CIERRE DE POLIGONAL: " + puntoCalculado.getNombre(), informe);
                JOptionPane.showMessageDialog(dialog, informe, "INFORME TÉCNICO", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: Formato de distancia inválido.");
            }
        });

        // Disparar carga inicial
        if (comboPuntosReales.getItemCount() > 0) {
            comboPuntosReales.setSelectedIndex(0);
        }

        dialog.setVisible(true);
    }
        
    private void abrirDialogoRadiacion() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE RADIACIÓN (PUNTO Y DISTANCIA)", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fCombo = new Font("Arial", Font.PLAIN, 18);
        Font fInput = new Font("Monospaced", Font.BOLD, 22);

        // 1. Estación de Origen
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN DE ORIGEN", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboOrigen = FabricaComponentes.crearComboPuntosYBlancos(fCombo, listaDePuntos, listaDeBlancos);
        formPanel.add(comboOrigen, gbc);

        // 2. Azimut (Dirección)
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("AZIMUT (mils)", fLabel), gbc);
        JTextField txtAzimut = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAzimut, gbc);

        // 3. Distancia
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(FabricaComponentes.crearEtiqueta("DISTANCIA (m)", fLabel), gbc);
        JTextField txtDistancia = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtDistancia, gbc);

        // 4. Nombre del Objetivo
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(FabricaComponentes.crearEtiqueta("ID OBJETIVO", fLabel), gbc);
        JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
        txtNombre.setText("RAD-" + (listaDePuntos.size() + 1));
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        JButton btnCalcular = new JButton("CALCULAR");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(45, 85, 45));
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        btnCalcular.addActionListener(e -> {
            try {
                Posicionable origen = (Posicionable) comboOrigen.getSelectedItem();
                double azimut = Double.parseDouble(txtAzimut.getText());
                double distancia = Double.parseDouble(txtDistancia.getText());
                String id = txtNombre.getText().trim();

                // Usamos una lógica similar a la de triangulación pero para Radiación
                // Podés agregar este método a tu CalculadorTopografico
                CoordenadasRectangulares res = CalculadorTopografico.radiacion(origen, azimut, distancia);
                
                Punto ptoNuevo = new Punto(res, id);
                agregarPunto(ptoNuevo); // Usamos tu método de SituacionTacticaTopo

                // REGISTRO PARA EL PDF
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("ESTACIÓN: %s (X: %.2f, Y: %.2f)\n", origen.getNombre(), origen.getCoordenadas().getX(), origen.getCoordenadas().getY()));
                sb.append(String.format("DATOS MEDIDOS: Azimut: %.0f mils | Distancia: %.2f m\n", azimut, distancia));
                sb.append(String.format("RESULTADO: %s en (X: %.3f, Y: %.3f)", id, res.getX(), res.getY()));
                
                RegistroCalculos.guardar("RADIACIÓN", sb.toString());
                
                dialog.dispose();
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    private void abrirDialogoInterseccionInversa3P() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE INTERSECCIÓN INVERSA (3 PUNTOS)", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 13);
        Font fCombo = new Font("Arial", Font.PLAIN, 16);
        Font fInput = new Font("Monospaced", Font.BOLD, 20);

        // SELECCIÓN DE PUNTOS CONOCIDOS
        // Punto Izquierda (P1)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("PUNTO IZQUIERDA (P1)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboP1 = FabricaComponentes.crearComboPuntosYBlancos(fCombo, listaDePuntos, listaDeBlancos);
        formPanel.add(comboP1, gbc);

        // Punto Central (P2)
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("PUNTO CENTRAL (P2)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboP2 = FabricaComponentes.crearComboPuntosYBlancos(fCombo, listaDePuntos, listaDeBlancos);
        formPanel.add(comboP2, gbc);

        // Punto Derecha (P3)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(FabricaComponentes.crearEtiqueta("PUNTO DERECHA (P3)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboP3 = FabricaComponentes.crearComboPuntosYBlancos(fCombo, listaDePuntos, listaDeBlancos);
        formPanel.add(comboP3, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(FabricaComponentes.crearEtiqueta("ANG. ALFA P1-P2 (mils)", fLabel), gbc);
        JTextField txtAlfa = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAlfa, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(FabricaComponentes.crearEtiqueta("ANG. BETA P2-P3 (mils)", fLabel), gbc);
        JTextField txtBeta = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtBeta, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(FabricaComponentes.crearEtiqueta("ID POSICIÓN PROPIA", fLabel), gbc);
        JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
        txtNombre.setText("POS-PROPIA-" + (listaDePuntos.size() + 1));
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton btnCalcular = new JButton("DETERMINAR POSICIÓN");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(40, 70, 120)); // Azul táctico
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(80, 40, 40));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        btnCalcular.addActionListener(e -> {
            try {
                Posicionable p1 = (Posicionable) comboP1.getSelectedItem();
                Posicionable p2 = (Posicionable) comboP2.getSelectedItem();
                Posicionable p3 = (Posicionable) comboP3.getSelectedItem();
                
                if (p1 == p2 || p2 == p3 || p1 == p3) throw new Exception("Seleccione tres puntos distintos.");

                double alfa = Double.parseDouble(txtAlfa.getText());
                double beta = Double.parseDouble(txtBeta.getText());
                
                // Llamaremos a la calculadora (Próximo paso)
                CoordenadasRectangulares res = CalculadorTopografico.interseccionInversa3P(p1, p2, p3, alfa, beta);
                
                Punto miPosicion = new Punto(res, txtNombre.getText().trim());
                agregarPunto(miPosicion);

                // Registro detallado para el PDF
                StringBuilder sb = new StringBuilder();
                sb.append("MÉTODO: INTERSECCIÓN INVERSA (POTENOT)\n");
                sb.append(String.format("REFERENCIAS: %s, %s, %s\n", p1.getNombre(), p2.getNombre(), p3.getNombre()));
                sb.append(String.format("ÁNGULOS OBS: α=%.0f mils, β=%.0f mils\n", alfa, beta));
                sb.append(String.format("POSICIÓN CALCULADA: X: %.3f | Y: %.3f", res.getX(), res.getY()));
                
                RegistroCalculos.guardar("INTERSECCIÓN INVERSA 3P", sb.toString());
                
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Posición propia determinada con éxito.");
                
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error en el cálculo: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    public void enviarPunto(Posicionable p) {
        if (p == null) return;

        // Construcción del datagrama según el protocolo requerido
        StringBuilder sb = new StringBuilder();
        sb.append("PUNTO|");
        sb.append("NOMBRE=").append(p.getNombre()).append("|");
        sb.append("X=").append(p.getCoordenadas().getX()).append("|");
        sb.append("Y=").append(p.getCoordenadas().getY());

        String mensajeFinal = sb.toString();

        // Verificación de enlace y envío
        if (observador != null && observador.getComunicacionIP() != null) {
            // Usamos el método enviarATodos que ya tienes implementado en el gestor Harris/IP
            observador.getComunicacionIP().enviarATodos(mensajeFinal);
            
            // Log de auditoría en consola
            System.out.println("TX Táctica (PUNTO): " + mensajeFinal);
            
            // Feedback visual rápido (opcional, podrías usar un Snackbar o similar)
            mensajeria.getConsolaMensajes().agregarMensaje("[TX] Punto " + p.getNombre() + " transmitido.");
        } else {
            sonidos.clickError();
            JOptionPane.showMessageDialog(this, 
                "FALLO DE COMUNICACIONES:\nNo se detectó el enlace IP para el envío.", 
                "SISTEMA", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generarInformePDF() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        FileDialog fd = new FileDialog(parentFrame, "GUARDAR REGISTRO TOPOGRÁFICO", FileDialog.SAVE);
        fd.setFile("Reporte_Topografico.pdf");
        fd.setVisible(true);

        if (fd.getFile() != null) {
            String rutaCompleta = fd.getDirectory() + fd.getFile();
            if (!rutaCompleta.toLowerCase().endsWith(".pdf")) rutaCompleta += ".pdf";

            // Definimos las fuentes de iText correctamente
            com.itextpdf.text.Font fuenteCabeceraTabla = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
            com.itextpdf.text.Font fuenteTexto = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);

            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, new FileOutputStream(rutaCompleta));
                documento.open();

                // Encabezado
                Paragraph titulo = new Paragraph("BATALLÓN DE ARTILLERÍA DE CAMPAÑA N°1\nSECCIÓN ADQUISICIÓN DE BLANCOS");
                titulo.setAlignment(Element.ALIGN_CENTER);
                documento.add(titulo);
                
                documento.add(new Paragraph("\nFECHA DE OPERACIÓN: " + LocalDateTime.now().toString()));
                documento.add(new Paragraph("------------------------------------------------------------------------------------------"));

                Map<String, String> datos = RegistroCalculos.getBitacora();

                String[] funcionesPDF = {
                    "TRIANGULACIÓN", "RADIACIÓN", "TRILATERACIÓN", 
                    "INTERSECCIÓN INVERSA 3P", "INTERSECCIÓN INVERSA 2P", 
                    "INTERSECCIÓN DIRECTA", "POLIGONAL", "MESA PLOTTING", 
                    "ÁNGULO BASE", "ACTUALIZACIÓN MAGNÉTICA", 
                    "REGISTRO COORD. MODIFICADAS", "NIVELACIÓN TRIGONOMÉTRICA",
                    "MEDICIÓN AYD"
                };

                for (String funcion : funcionesPDF) {
                    if (datos.containsKey(funcion)) {
                        PdfPTable tabla = new PdfPTable(1);
                        tabla.setWidthPercentage(100);
                        tabla.setSpacingBefore(10f);

                        // Celda de título de la función
                        PdfPCell celdaTitulo = new PdfPCell(new Phrase(funcion, fuenteCabeceraTabla));
                        celdaTitulo.setBackgroundColor(new BaseColor(192, 192, 192));
                        celdaTitulo.setPadding(5);
                        tabla.addCell(celdaTitulo);
                        
                        // Celda de contenido del cálculo
                        PdfPCell celdaContenido = new PdfPCell(new Phrase(datos.get(funcion), fuenteTexto));
                        celdaContenido.setPadding(10);
                        tabla.addCell(celdaContenido);

                        documento.add(tabla);
                    }
                }

                documento.close();
                JOptionPane.showMessageDialog(this, "Informe PDF generado con éxito.");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al generar PDF: " + ex.getMessage());
            }
        }
    }
    
    private void abrirDialogoTriangulacion() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE TRIANGULACIÓN TÁCTICA", true);
        dialog.setSize(650, 600); // Tamaño más contenido y equilibrado
        dialog.setLocationRelativeTo(this);

        // Panel Principal con un degradado o color sólido oscuro
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));

        // SECCIÓN DE FORMULARIO 
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fuentes
        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fCombo = new Font("Arial", Font.PLAIN, 18);
        Font fInput = new Font("Monospaced", Font.BOLD, 22);

        // 1. Grupo Estaciones (Línea Base)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN A (Izquierda)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboA;
        comboA = FabricaComponentes.crearComboPuntosYBlancos(fCombo,listaDePuntos,listaDeBlancos);

        formPanel.add(comboA, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN B (Derecha)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboB = FabricaComponentes.crearComboPuntosYBlancos(fCombo,listaDePuntos,listaDeBlancos);
        formPanel.add(comboB, gbc);

        // Separador
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // 2. Grupo Mediciones (Ángulos)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(FabricaComponentes.crearEtiqueta("ANG. ALFA (mils)", fLabel), gbc);
        JTextField txtAngA = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAngA, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(FabricaComponentes.crearEtiqueta("ANG. BETA (mils)", fLabel), gbc);
        JTextField txtAngB = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAngB, gbc);

        // 3. Grupo Resultado
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(FabricaComponentes.crearEtiqueta("ID OBJETIVO", fLabel), gbc);
        JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
        txtNombre.setText("EST-" + (listaDePuntos.size() + 1));
        txtNombre.setForeground(new Color(255, 200, 0)); // Color distintivo
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        // --- PANEL DE BOTONES (Inferior) ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        JButton btnCalcular = new JButton("CALCULAR");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(45, 85, 45));
        
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        // ACCIONES
        btnCalcular.addActionListener(e -> {
            try {
                Posicionable pA = (Posicionable) comboA.getSelectedItem();
                Posicionable pB = (Posicionable) comboB.getSelectedItem();
                
                if (pA == null || pB == null) throw new Exception("Seleccione ambas estaciones.");
                if (pA.equals(pB)) throw new Exception("Las estaciones deben ser distintas.");

                double alfa = Double.parseDouble(txtAngA.getText());
                double beta = Double.parseDouble(txtAngB.getText());
                
                double distLB = pA.getCoordenadas().distanciaA(pB.getCoordenadas());
                CoordenadasRectangulares res = CalculadorTopografico.triangulacion(pA, pB, alfa, beta);
                String id = txtNombre.getText().trim();

                Punto ptoNuevo = new Punto(res, id);

                listaDePuntos.add(ptoNuevo);
                
                listaDePoligonales.add(ptoNuevo);
                
                modeloListaPoligonales.addElement(ptoNuevo);
                
                panelMapa.agregarPoligonal(ptoNuevo);

                StringBuilder sb = new StringBuilder();
                sb.append("DATOS DE ESTACIONES:\n");
                sb.append(String.format(" - ETR A: %s (X: %.2f, Y: %.2f)\n", pA.getNombre(), pA.getCoordenadas().getX(), pA.getCoordenadas().getY()));
                sb.append(String.format(" - ETR B: %s (X: %.2f, Y: %.2f)\n", pB.getNombre(), pB.getCoordenadas().getX(), pB.getCoordenadas().getY()));
                sb.append(String.format(" - LÍNEA BASE (LB): %.2f m\n\n", distLB));
                sb.append("MEDICIONES DE CAMPO:\n");
                sb.append(String.format(" - ÁNGULO ALFA (α): %.0f mils\n", alfa));
                sb.append(String.format(" - ÁNGULO BETA (β): %.0f mils\n\n", beta));
                sb.append("RESULTADO POSICIONAMIENTO:\n");
                sb.append(String.format(" - OBJETIVO: %s\n", id));
                sb.append(String.format(" - COORDENADAS: X: %.3f | Y: %.3f", res.getX(), res.getY()));

                RegistroCalculos.guardar("TRIANGULACIÓN", sb.toString());
                
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: Los ángulos deben ser valores numéricos.");
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    public void enviarBlanco(Blanco b) {
        if (b == null) return;

        // Construyo el mensaje siguiendo el protocolo técnico al pie de la letra
        StringBuilder sb = new StringBuilder();
        sb.append("BLANCO|");
        sb.append("NOMBRE=").append(b.getNombre()).append("|");
        sb.append("NAT=").append(b.getNaturaleza()).append("|");
        sb.append("FECHA=").append(b.getFechaDeActualizacion()).append("|");
        sb.append("ORI=").append((int)b.getOrientacion()).append("|");
        
        // Verifico que la info adicional no sea nula o vacía para cumplir el estándar
        String info = b.getInformacionAdicional();
        if (info == null || info.trim().isEmpty()) {
            info = "Sin Informacion Adicional";
        }
        sb.append("INFO=").append(info).append("|");
        
        sb.append("SIMID=").append(b.getSimID()).append("|");
        sb.append("SIT=").append(b.getSituacionMovimiento()).append("|");
        sb.append("X=").append(b.getCoordenadas().getX()).append("|");
        sb.append("Y=").append(b.getCoordenadas().getY());

        String mensajeFinal = sb.toString();

        // Envío el paquete a través del observador de comunicaciones IP
        // Asumo que observador tiene acceso al gestor de red (Harris/IP)
        if (observador != null && observador.getComunicacionIP() != null) {
            observador.getComunicacionIP().enviarATodos(mensajeFinal);
            
            // Registro la salida en mi consola táctica para que el operador sepa que salió
            System.out.println("Transmisión Táctica Saliente: " + mensajeFinal);
        } else {
            sonidos.clickError();
            JOptionPane.showMessageDialog(this, 
                "ERROR DE ENLACE: No hay conexión con el módulo de comunicaciones.", 
                "FALLO DE TRANSMISIÓN", 
                JOptionPane.ERROR_MESSAGE);
        }
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
                tooltipLabel.setVisible(false);
                panelMapa.getMapPane().repaint();

                double x = ev.getWorldPos().getX();
                double y = ev.getWorldPos().getY();
                CoordenadasRectangulares coord = new CoordenadasRectangulares(x, y, 0);

                // MÁSCARA VISUAL: Desde el 3er dígito, sin decimales
                String xVisual = String.format("%.0f", x);
                String yVisual = String.format("%.0f", y);
                
                if (xVisual.length() > 2) xVisual = xVisual.substring(2);
                if (yVisual.length() > 2) yVisual = yVisual.substring(2);

                mostrarDialogoSeleccionNormal(coord, xVisual, yVisual);
            }

            private void actualizarTooltip(MapMouseEvent ev) {
                double x = ev.getWorldPos().getX();
                double y = ev.getWorldPos().getY();
                
                String xV = String.format("%.0f", x);
                String yV = String.format("%.0f", y);
                if (xV.length() > 2) xV = xV.substring(2);
                if (yV.length() > 2) yV = yV.substring(2);

                tooltipLabel.setText("DERECHAS: " + xV + " | ARRIBAS: " + yV);
                panelMapa.getMapPane().repaint();
            }
        });
    }

    private void mostrarDialogoSeleccionNormal(CoordenadasRectangulares coord, String xV, String yV) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Selección de Marcador", true);
        dialog.setSize(600, 300);
        dialog.setLocationRelativeTo(this);
        
        // Panel con colores normales (System Default)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Etiquetas de coordenadas
        JLabel lblInfo = new JLabel("<html><center><font size='4'>COORDENADAS</font><br>"
                + "<font color='black' size='6'>DERECHAS: " + xV + " | ARRIBAS: " + yV + "</font></center></html>");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblInfo, gbc);

        // Botones con tamaño para Tablet
        JButton btnBlanco = new JButton("Marcar Blanco");
        JButton btnPunto = new JButton("Marcar Punto");
        
        Font fontBoton = new Font("Arial", Font.BOLD, 18);
        for (JButton b : new JButton[]{btnBlanco, btnPunto}) {
            b.setFont(fontBoton);
            b.setPreferredSize(new Dimension(200, 80));
            b.setFocusPainted(false);
        }

        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0; panel.add(btnBlanco, gbc);
        gbc.gridx = 1; panel.add(btnPunto, gbc);

        // Listeners
        btnBlanco.addActionListener(e -> {
            dialog.dispose();
            dialogoAgregarBlanco(coord);
        });

        btnPunto.addActionListener(e -> {
            dialog.dispose();
            dialogoAgregarPunto(coord);
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void cambiarMapaEnTiempoReal() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        FileDialog fd = new FileDialog(parentFrame, "SELECCIONAR NUEVA CARTOGRAFÍA GeoTIFF", FileDialog.LOAD);
        
        // Filtro para archivos TIFF
        fd.setFile("*.tif;*.tiff");
        fd.setDirectory("C:\\");
        fd.setVisible(true);

        if (fd.getFile() != null) {
            String nuevaRuta = (fd.getDirectory() + fd.getFile()).replace("\\", "/");

            listaDeBlancos.clear();
            listaDePuntos.clear();
            listaDePoligonales.clear();
            modeloListaBlancos.clear();
            modeloListaPoligonales.clear();

            panelMapa.dispose();
            PanelMapa nuevoMapa = new PanelMapa(nuevaRuta);

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

            nuevoMapa.getMapPane().add(tooltipLabel);
            
            if (split != null) {
                this.panelMapa = nuevoMapa;
                split.setRightComponent(panelMapa);
                
                configurarHerramientasMapa();
            }

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
        FabricaComponentes.configurarBotonMilitar(btnMapa, new Font("Arial", Font.BOLD, 22), new Color(130, 40, 40)); 
        btnMapa.setPreferredSize(new Dimension(0, 150)); // Altura táctica para tablet
        btnMapa.addActionListener(e -> {
            dialog.dispose();
            cambiarMapaEnTiempoReal();
        });

        JButton btnDesig = new JButton("<html><center><font size='6'>MODIFICAR DESIGNACIÓN</font><br>"
                                     + "<font size='5' color='#BBBBBB'>Prefijo y contador de blancos</font></center></html>");
        FabricaComponentes.configurarBotonMilitar(btnDesig, new Font("Arial", Font.BOLD, 22), new Color(40, 70, 130));
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
        FabricaComponentes.addPlaceholder(txtRuta, placeholder); 
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

        JButton btnIniCart = new JButton("INICIALIZAR CARTOGRAFÍA");
        JButton btnRutDef = new JButton("RUTA POR DEFECTO");

        for (JButton b : new JButton[]{btnIniCart, btnRutDef}) {
            b.setFocusPainted(false);
            b.setFont(new Font("Arial", Font.BOLD, 15));
            b.setForeground(Color.WHITE);
            b.setPreferredSize(new Dimension(280, 55)); // Botones grandes
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        btnIniCart.setBackground(azulTactico);
        btnRutDef.setBackground(grisOscuro);
        btnIniCart.setBorder(BorderFactory.createLineBorder(Color.CYAN));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panelBotones.setOpaque(false);
        panelBotones.add(btnIniCart);
        panelBotones.add(btnRutDef);

        gbc.gridy = 4; gbc.gridwidth = 3;
        panel.add(panelBotones, gbc);

        // Acción examinar
        btnExaminar.addActionListener(e -> {
            // Uso FileDialog de AWT para invocar el explorador real de Windows
            FileDialog fd = new FileDialog(dialog, "SELECCIONAR ARCHIVO CARTOGRÁFICO TIFF", FileDialog.LOAD);
            
            // Filtrado de extensiones para Windows
            fd.setFile("*.tif;*.tiff");
            
            // Abrir en la unidad C o la última usada
            fd.setDirectory("C:\\");
            
            fd.setVisible(true);

            if (fd.getFile() != null) {
                // Construyo la ruta completa
                String rutaSeleccionada = fd.getDirectory() + fd.getFile();
                
                txtRuta.setText(rutaSeleccionada.replace("\\", "/"));
                txtRuta.setForeground(Color.WHITE);
            }
        });

        btnIniCart.addActionListener(e -> {
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

        btnRutDef.addActionListener(e -> {
            String designacion = txtDesignacion.getText().trim();
            String[] partes = designacion.split(" ");
            
            if (partes.length != 2 || !partes[0].matches("^[A-Z]+$")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, 
                    "ERROR: FORMATO DE DESIGNACIÓN INVÁLIDO\n(DEBE SER: [LETRAS] [NÚMEROS])", 
                    "FALLO DE DATOS", JOptionPane.ERROR_MESSAGE, icono);
                return; 
            }

            try {
                int contador = Integer.parseInt(partes[1]);
                if (contador < 1) throw new NumberFormatException();
                
                designacionBlancoPrefijo = partes[0].toUpperCase();
                designacionBlancoContador = contador;

                JOptionPane.showMessageDialog(dialog, 
                    "SISTEMA: CARGANDO RUTA PREESTABLECIDA\n" + rutaArchivoMapa + 
                    "\nDESIGNACIÓN: " + designacionBlancoPrefijo + " " + designacionBlancoContador, 
                    "AVISO TÁCTICO", JOptionPane.INFORMATION_MESSAGE, icono);
                
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, 
                    "ERROR: EL CONTADOR DEBE SER UN ENTERO POSITIVO", 
                    "FALLO DE DATOS", JOptionPane.ERROR_MESSAGE, icono);
            }
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

        panelBlanco.add(FabricaComponentes.crearLinea2("Nombre: ", b.getNombre(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("Fecha Creación: ", b.getFechaDeActualizacion(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("SIM ID (SIDC): ", b.getSimID(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("Orientación: ", String.format("%.2f mils", b.getOrientacion()), fTexto));
        panelBlanco.add(Box.createVerticalStrut(separacion));
        
        panelBlanco.add(FabricaComponentes.crearLinea2("Coordenadas: ", "", fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - DERECHAS (X): ", String.format("%.6f", b.getCoordenadas().getX()), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - ARRIBAS (Y): ", String.format("%.6f", b.getCoordenadas().getY()), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - COTA (Z): ", String.format("%.2f m", b.getCoordenadas().getCota()), fTexto)); 
        panelBlanco.add(Box.createVerticalStrut(separacion));

        panelBlanco.add(FabricaComponentes.crearLinea2("Naturaleza: ", b.getNaturaleza(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - Tipo (Entidad): ", b.getUltEntidad(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - Afiliación: ", b.getUltAfiliacion(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - Magnitud : ", b.getUltEchelon(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - Situación Mov.: ", String.valueOf(b.getSituacionMovimiento()), fTexto));

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
        panelPunto.add(FabricaComponentes.crearLinea2("Nombre: ", p.getNombre(), fTexto));
        panelPunto.add(Box.createVerticalStrut(15));
        
        // Coordenadas con formato de alta precisión
        panelPunto.add(FabricaComponentes.crearLinea2("Coordenadas: ", "", fTexto));
        panelPunto.add(FabricaComponentes.crearLinea2("  - DERECHAS (X): ", String.format("%.6f", p.getCoordenadas().getX()), fTexto));
        panelPunto.add(FabricaComponentes.crearLinea2("  - ARRIBAS (Y): ", String.format("%.6f", p.getCoordenadas().getY()), fTexto));
        
        // Cota (Z)
        panelPunto.add(FabricaComponentes.crearLinea2("  - COTA (Z): ", String.format("%.2f m", p.getCoordenadas().getCota()), fTexto)); 

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
    private void dialogoAgregarBlanco(CoordenadasRectangulares coordInicial) {

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
        FabricaComponentes.addPlaceholder(txtOrient, "mils");
        txtOrient.setPreferredSize(new Dimension(120, 40)); // Dimensiones aumentadas
        txtOrient.setBackground(new Color(70, 70, 70));
        txtOrient.setForeground(Color.WHITE);
        txtOrient.setFont(fuenteMedia);

        JTextField txtNombre = new JTextField();
        FabricaComponentes.addPlaceholder(txtNombre, designacionBlancoPrefijo +" "+ designacionBlancoContador);
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
        FabricaComponentes.addPlaceholder(txtInfo, "Información adicional necesaria");
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

            CoordenadasRectangulares coord = new CoordenadasRectangulares(x, y, cota);
            Blanco nuevo = new Blanco(nombre, coord, naturaleza, txtFecha.getText());

            nuevo.setUltAfiliacion(afiliacion);
            nuevo.setUltEchelon(echelon);
            nuevo.setUltEntidad(entidad);
            nuevo.setSimID(GestorCodigosSIDC.obtenerSIDC(naturaleza));
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

    private void dialogoAgregarPunto(CoordenadasRectangulares coordInicial) {
    	
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Nuevo Punto", true);
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
        FabricaComponentes.addPlaceholder(txtNombre, "Nombre del Punto");
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
            b.setPreferredSize(new Dimension(0, 70)); 
            b.setFocusPainted(false);
        }

        JPanel botones = new JPanel(new GridLayout(1, 2, 20, 0));
        botones.setBackground(new Color(50, 50, 50));
        botones.add(btnAceptar); 
        botones.add(btnCancelar);
        
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; 
        gbc.insets = new Insets(30, 12, 10, 12); 
        panel.add(botones, gbc);

        // ACCIONES
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
            listaDePuntos.add(nuevo); 
            
            modeloListaPoligonales.addElement(nuevo);
            
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
        FabricaComponentes.addPlaceholder(txtInfo, "Información adicional necesaria");
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

                CoordenadasRectangulares refCoord = (CoordenadasRectangulares) referencia.getCoordenadas();
                CoordenadasPolares polar = new CoordenadasPolares(direccion, distancia, angulo, refCoord);
                CoordenadasRectangulares nuevas = polar.toRectangulares();

                String nombre = txtNombre.getText().trim();

                String entidad = (String) cbEntidad.getSelectedItem();
                String afiliacion = (String) cbAfiliacion.getSelectedItem();
                String echelon = (String) cbEchelon.getSelectedItem();

                String naturaleza = entidad + "_" + afiliacion;
                if (!echelon.equals("Por Defecto"))
                    naturaleza += "_" + echelon.toUpperCase();

                Blanco nuevo = new Blanco(nombre, nuevas, naturaleza, LocalDateTime.now().toString());
                nuevo.setSimID(GestorCodigosSIDC.obtenerSIDC(naturaleza));
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

                JOptionPane.showMessageDialog(dialog, resultado, "Resultado de Medición", JOptionPane.INFORMATION_MESSAGE);

                Coordinate c1 = new Coordinate();
                c1.setX(origen.getCoordenadas().getX());
                c1.setY(origen.getCoordenadas().getY());
                Coordinate c2 = new Coordinate();
                c2.setX(destino.getCoordenadas().getX());
                c2.setY(destino.getCoordenadas().getY());
                
                String nombreLinea = origen.getNombre() + "→" + destino.getNombre();
                Linea nuevaLinea = new Linea(nombreLinea, c1,c2, distancia, azimutMils);
                
                listaDePoligonales.add(nuevaLinea);
                modeloListaPoligonales.addElement(nuevaLinea);
                panelMapa.agregarPoligonal(nuevaLinea);
                
                String medicionData = String.format("Origen: %s -> Destino: %s | Dist: %.2f m | Az: %.0f mil", 
                        origen.getNombre(), destino.getNombre(), distancia, azimutMils);

				RegistroCalculos.guardar("MEDICIÓN AYD", medicionData);
                
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                sonidos.clickError();
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    public void agregarPunto(Punto p) {
        if (p == null) return;
        
        if (!listaDePuntos.contains(p)) {
            listaDePuntos.add(p);
        }
        
        // Como los puntos son parte de la visualización de poligonales:
        if (!listaDePoligonales.contains(p)) {
            listaDePoligonales.add(p);
        }
        
        if (!modeloListaPoligonales.contains(p)) {
            modeloListaPoligonales.addElement(p);
        }
        
        panelMapa.agregarPoligonal(p);
        listaUIPoligonales.repaint();
    }

    /**
     * Actualizo un punto existente.
     * Es vital para cuando recibo correcciones de coordenadas vía Harris (IP)
     * para una estación topográfica (ETR) ya establecida.
     */
    public void actualizarPunto(Punto p) {
        if (p == null) return;

        int idx = modeloListaPoligonales.indexOf(p);
        if (idx >= 0) {
            modeloListaPoligonales.set(idx, p);
        }

        listaUIPoligonales.repaint();
        panelMapa.eliminarPoligonal(p);
        panelMapa.agregarPoligonal(p);
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
                b.setSimID(GestorCodigosSIDC.obtenerSIDC(naturaleza));

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
        
    public LinkedList<Blanco> getListaDeBlancos(){
    	return listaDeBlancos;
    }
    
    public LinkedList<Punto> getListaDePuntos(){
    	return listaDePuntos;
    }

	public void setPanelMensajeria(Mensajeria mensajeriaPanel) {
		mensajeria = mensajeriaPanel;
	}
}
