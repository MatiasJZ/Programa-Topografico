package app;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.*;

import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;

import comunicaciones.Red;
import dominio.Blanco;
import dominio.Posicionable;
import dominio.Punto;
import dominio.RegistroCalculos;
import dominio.RenderizadorListas;
import dominio.CoordenadasRectangulares;
import dominio.GeneradorPDF;
import dominio.Linea;
import dominio.Poligonal;
import interfaz.Mensajeria;
import interfaz.PanelMapa;
import util.Configuracion;
import util.DesignacionProvider;
import util.DialogFactory;
import util.FabricaComponentes;
import util.FabricaDialogosTacticos;
import util.GestorSonido;

/**
 * SituacionTacticaTopografica es un panel principal para la gestión táctica topográfica en una aplicación Swing.
 * 
 * <p>
 * Esta clase proporciona una interfaz gráfica avanzada para la visualización, edición y transmisión de blancos,
 * puntos y poligonales sobre un mapa raster (GeoTIFF), integrando herramientas de cálculo topográfico, generación
 * de informes PDF, y comunicación IP para la transmisión de datos tácticos.
 * </p>
 * 
 * <h2>Características principales:</h2>
 * <ul>
 *   <li>Visualización y gestión de listas de blancos y poligonales.</li>
 *   <li>Integración con un panel de mapa interactivo (PanelMapa) para marcar y editar elementos geográficos.</li>
 *   <li>Herramientas topográficas: triangulación, radiación, intersección, trilateración, nivelación, etc.</li>
 *   <li>Generación de informes PDF de los cálculos realizados.</li>
 *   <li>Comunicación IP para envío de blancos y puntos a través de un protocolo definido.</li>
 *   <li>Personalización de la designación de blancos (prefijo y contador).</li>
 *   <li>Soporte para cambio dinámico de cartografía raster.</li>
 *   <li>PopUp contextuales para acciones rápidas sobre listas.</li>
 *   <li>Interfaz optimizada para uso táctil y operación en campo.</li>
 * </ul>
 * 
 * <h2>Componentes principales:</h2>
 * <ul>
 *   <li>Listas de blancos y poligonales con renderizado personalizado.</li>
 *   <li>Panel de herramientas topográficas y HUD flotante.</li>
 *   <li>Panel de botones de acción rápida (agregar, eliminar, refrescar, configuración, PDF, herramientas).</li>
 *   <li>Integración con gestor de sonido y mensajería para feedback al usuario.</li>
 *   <li>Soporte para edición y marcado de elementos mediante diálogos modales (DialogFactory).</li>
 * </ul>
 * 
 * <h2>Interacción:</h2>
 * <ul>
 *   <li>Permite agregar, editar y eliminar blancos y puntos tanto desde la lista como desde el mapa.</li>
 *   <li>Permite marcar nuevos elementos directamente sobre el mapa mediante arrastre y selección contextual.</li>
 *   <li>Permite modificar en tiempo real la cartografía y la secuencia de designación de blancos.</li>
 *   <li>Permite enviar información táctica a través de la red IP integrada.</li>
 * </ul>
 * 
 * <h2>Dependencias:</h2>
 * <ul>
 *   <li>PanelMapa, Blanco, Punto, Poligonal, GestorSonido, Mensajeria, DialogFactory, RenderizadorListas, etc.</li>
 *   <li>Librerías externas: iText para generación de PDF.</li>
 * </ul>
 * 
 * <h2>Implementa:</h2>
 * <ul>
 *   <li>DesignacionProvider: para gestión de prefijo y contador de designación de blancos.</li>
 * </ul>
 * 
 * <h2>Uso:</h2>
 * <pre>
 * SituacionTacticaTopografica panel = new SituacionTacticaTopografica(listaDeBlancos, observador);
 * JFrame frame = new JFrame();
 * frame.add(panel);
 * frame.setVisible(true);
 * </pre>
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */

public class SituacionTacticaTopografica extends JPanel implements DesignacionProvider {

    private static final long serialVersionUID = 789462013392544798L;
    private DefaultListModel<Blanco> modeloListaBlancos;
    private JList<Blanco> listaUIBlancos;
    private LinkedList<Blanco> listaDeBlancos;
    private LinkedList<Punto> listaDePuntos;
    private Map<Posicionable, Posicionable> mapeoDeVertices;
    private PanelMapa panelMapa;
    private LinkedList<Poligonal> listaDePoligonales;
    private DefaultListModel<Poligonal> modeloListaPoligonales;
    private JList<Poligonal> listaUIPoligonales;
    private GestorSonido sonidos;
    private ProgramaTopografico main;
    private String rutaArchivoMapa;
    private String designacionBlancoPrefijo; 
    private int designacionBlancoContador;
    private JPanel panelGlobalTopografico;
    private JLabel tooltipLabel;
    private Mensajeria mensajeria;
    private PedidoDeFuego panelPIF;
    private RenderizadorListas RenderListas;
    private JPopupMenu popupMenu;    
    private JPopupMenu popupMenuPunto;
    private JSplitPane splitPaneMapa;
    private DialogFactory dialogFactory;
    private GeneradorPDF generadorDoc;
    
    public SituacionTacticaTopografica(LinkedList<Blanco> listaDeBlancos, PedidoDeFuego pif, ProgramaTopografico main) { 
        
        // Settings iniciales
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        // Inicializacion de atributos
        this.main = main;
        this.listaDeBlancos = listaDeBlancos;
        this.listaDePuntos = new LinkedList<>();
        mapeoDeVertices = new HashMap<Posicionable,Posicionable>();
        this.RenderListas = new RenderizadorListas();
        
        this.rutaArchivoMapa = Configuracion.get("ruta_mapa_defecto", "C:/Mapas/default.tif");
        this.designacionBlancoPrefijo = Configuracion.get("prefijo_blancos", "AF");
        this.designacionBlancoContador = Configuracion.getInt("contador_blancos_inicio", 6400);
        
        this.modeloListaBlancos = new DefaultListModel<>();     
        this.listaUIBlancos = new JList<>(modeloListaBlancos); 
        listaUIBlancos.setFont(new Font("Arial", Font.BOLD, 20)); 
        listaUIBlancos.setBackground(Color.BLACK); 
        listaUIBlancos.setFixedCellHeight(50);  
        this.listaUIBlancos.setCellRenderer(RenderListas);  
        
        this.listaDePoligonales = new LinkedList<>();
        this.modeloListaPoligonales = new DefaultListModel<>();     
        listaUIPoligonales = new JList<>(modeloListaPoligonales); 
        listaUIPoligonales.setFont(new Font("Arial", Font.BOLD, 20)); 
        listaUIPoligonales.setBackground(Color.BLACK); 
        listaUIPoligonales.setFixedCellHeight(40);  
        listaUIPoligonales.setCellRenderer(RenderListas);
        
        panelPIF = pif;
        sonidos = new GestorSonido();
        this.dialogFactory = new FabricaDialogosTacticos(this, sonidos);  
        generadorDoc = new GeneradorPDF(this);
        
        // 1. PANEL IZQUIERDO (Listas)
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBackground(Color.BLACK); 
        
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
        
        gbcList.gridx = 0; gbcList.gridy = 0;
        gbcList.weightx = 1; gbcList.weighty = 0;
        panelListas.add(lblBlancos, gbcList);
        
        gbcList.gridy = 1; gbcList.weighty = 0.75;
        JScrollPane scrollBlancos = new JScrollPane(listaUIBlancos);
        scrollBlancos.getViewport().setBackground(Color.BLACK);
        panelListas.add(scrollBlancos, gbcList);
        
        gbcList.gridy = 2; gbcList.weighty = 0;
        panelListas.add(lblPuntos, gbcList);
        
        gbcList.gridy = 3; gbcList.weighty = 0.25;
        JScrollPane scrollPoligonales = new JScrollPane(listaUIPoligonales);
        scrollPoligonales.getViewport().setBackground(Color.BLACK);
        panelListas.add(scrollPoligonales, gbcList);
        
        panelIzquierdo.add(panelListas, BorderLayout.CENTER);
        
        // 2. COMPONENTES FLOTANTES
        JButton btnAgregar = new JButton("\u2795 AGREGAR");     
        JButton btnEliminar = new JButton("\u274C ELIMINAR");    
        JButton btnRefrescar = new JButton("\u21BB REFRESCAR");
        JButton btnConfigIP = new JButton("HARRIS"); 
        JButton btnHerramientas = new JButton("\u2692 HERRAM.");   
        JButton btnGenPdf = new JButton("GENERAR PDF");
        
        Font fuenteEmoji = new Font("Segoe UI Emoji", Font.BOLD, 16);
        Dimension dimPequeña = new Dimension(135, 45);
        Dimension dimAncha = new Dimension(280, 45);

        for (JButton b : new JButton[]{btnAgregar, btnEliminar, btnRefrescar}) {
            b.setFont(fuenteEmoji);
            b.setPreferredSize(dimPequeña);
            b.setFocusPainted(false);
        }

        Color azulOscuro = new Color(60, 60, 120);
        Color azulClaro = new Color(129, 129, 204);
        for (JButton b : new JButton[]{btnConfigIP, btnHerramientas, btnGenPdf}) {
            b.setBackground(azulOscuro);
            b.setForeground(Color.WHITE);
            b.setFont(fuenteEmoji);
            b.setFocusPainted(false);
            
            if (b == btnGenPdf || b == btnHerramientas) {
                b.setPreferredSize(dimAncha);
            } else {
                b.setPreferredSize(dimPequeña);
            }
        }
        btnHerramientas.setBackground(azulClaro);

        // Panel HUD Transparente 
        JPanel hud = new JPanel(new GridBagLayout());
        hud.setBackground(new Color(0, 0, 0, 170));  
        hud.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        hud.setPreferredSize(new Dimension(340, 280)); 
        hud.setMinimumSize(new Dimension(340, 280)); 

        GridBagConstraints h = new GridBagConstraints();
        h.insets = new Insets(8, 8, 8, 8);
        h.fill = GridBagConstraints.BOTH; 
        h.weightx = 1.0; h.weighty = 1.0;

        h.gridy = 0; h.gridx = 0; h.gridwidth = 1; hud.add(btnAgregar, h);
        h.gridx = 1; hud.add(btnEliminar, h);
        h.gridy = 1; h.gridx = 0; hud.add(btnRefrescar, h);
        h.gridx = 1; hud.add(btnConfigIP, h);
        h.gridy = 2; h.gridx = 0; h.gridwidth = 2; hud.add(btnHerramientas, h);
        h.gridy = 3; hud.add(btnGenPdf, h);

        // Botón Ajustes
        JButton btnConfig = new JButton("\u2699 AJUSTES");
        btnConfig.setFont(fuenteEmoji);
        btnConfig.setBackground(Color.DARK_GRAY);
        btnConfig.setForeground(Color.WHITE);
        btnConfig.setPreferredSize(new Dimension(150, 80));
        btnConfig.setMinimumSize(new Dimension(150, 80)); 
        btnConfig.setFocusPainted(false);
        
        // Panel Global Topografico
        panelGlobalTopografico = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelGlobalTopografico.setBackground(Color.BLACK);  
        panelGlobalTopografico.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panelGlobalTopografico.setPreferredSize(new Dimension(1050, 145)); 
        panelGlobalTopografico.setVisible(true); 
        
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
                    case "TRIANG": 
                        dialogFactory.TriangulacionDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            agregarPunto(resultado);
                            RegistroCalculos.guardar("TRIANGULACIÓN", informe);
                        });
                        break;
                    case "RAD": 
                        dialogFactory.RadiacionDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            agregarPunto(resultado);
                            RegistroCalculos.guardar("RADIACIÓN", informe);
                        });
                        break;
                    case "INT-INV-3P": 
                        dialogFactory.InterseccionInversa3PDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            agregarPunto(resultado);
                            RegistroCalculos.guardar("INTERSECCIÓN INVERSA 3P", informe);
                            JOptionPane.showMessageDialog(this, "Posición propia determinada con éxito.");
                        });
                        break;
                    case "MESA-P": 
                        dialogFactory.MesaPlottingDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            agregarPunto(resultado);
                            RegistroCalculos.guardar("MESA PLOTTING", informe);
                            JOptionPane.showMessageDialog(this, "Intersección de Plotting graficada con éxito.");
                        });
                        break;
                    case "TRILAT": 
                        dialogFactory.TrilateracionDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            agregarPunto(resultado);
                            RegistroCalculos.guardar("TRILATERACIÓN", informe);
                            JOptionPane.showMessageDialog(this, "Trilateración calculada y graficada con éxito.");
                        });
                        break;
                    case "INT-INV-2P": 
                        dialogFactory.InterseccionInversa2PDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            agregarPunto(resultado);
                            RegistroCalculos.guardar("INTERSECCIÓN INVERSA 2P", informe);
                            JOptionPane.showMessageDialog(this, "Posición propia (2P) determinada con éxito.");
                        });
                        break;
                    case "INT-D-M": 
                        dialogFactory.InterseccionDirectaMDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            agregarPunto(resultado);
                            RegistroCalculos.guardar("INTERSECCIÓN DIRECTA", informe);
                            JOptionPane.showMessageDialog(this, "Objetivo ubicado por Intersección Directa.");
                        });
                        break;
                    case "ANG-B": 
                        dialogFactory.AnguloBaseDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            agregarPunto(resultado);
                            RegistroCalculos.guardar("ÁNGULO BASE", informe);
                            JOptionPane.showMessageDialog(this, "Objetivo por Ángulo Base graficado con éxito.");
                        });
                        break;
                    case "ACT-MAG": 
                        dialogFactory.ActualizacionMagneticaDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            if (resultado != null) {
                                agregarPunto(resultado);
                            }
                            RegistroCalculos.guardar("ACTUALIZACIÓN MAGNÉTICA", informe);
                            JOptionPane.showMessageDialog(this, "Declinación actualizada con éxito. Revisa el informe.");
                        });
                        break;
                    case "REG-C-M": 
                        dialogFactory.RegistroCoordModDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            agregarPunto(resultado);
                            RegistroCalculos.guardar("COORDENADAS MODIFICADAS", informe);
                            JOptionPane.showMessageDialog(this, "Coordenadas modificadas y registradas con éxito.");
                        });
                        break;
                    case "NIVEL-T": 
                        dialogFactory.NivelTrigonometricoDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            agregarPunto(resultado);
                            RegistroCalculos.guardar("NIVELACIÓN TRIGONOMÉTRICA", informe);
                            JOptionPane.showMessageDialog(this, "Nivelación trigonométrica calculada con éxito.");
                        });
                        break;
                    case "REG-PPAL": 
                        dialogFactory.RegistroPPALDialog(listaDePuntos, listaDeBlancos, (resultado, informe) -> {
                            RegistroCalculos.guardar("ENCABEZADO DE EXPORTACIÓN", informe);
                            JOptionPane.showMessageDialog(this, "Registro PPAL guardado con éxito.");
                            JOptionPane.showMessageDialog(this, "Registro PDF exportado correctamente.");
                        });
                        break;
                }
            });
        }

        JButton PIFbtn = new JButton("PIF"); botoneraPanelTopo.addLast(PIFbtn);
        PIFbtn.setBackground(new Color(180, 40, 40));
        PIFbtn.setForeground(Color.WHITE);
        PIFbtn.setFont(new Font("Arial", Font.BOLD, 12)); 
        PIFbtn.setPreferredSize(new Dimension(98, 60)); 
        PIFbtn.setFocusPainted(false);
        panelGlobalTopografico.add(PIFbtn);

        // MAPA
        pedirArchivoAMostrar();
        panelMapa = new PanelMapa(rutaArchivoMapa);

        // 3. ESTRUCTURA DE OVERLAY 
        JPanel mapWrapper = new JPanel() {
			private static final long serialVersionUID = -8649016075576678370L;

			@Override
            public boolean isOptimizedDrawingEnabled() {
                return false; 
            }
        };
        mapWrapper.setLayout(new OverlayLayout(mapWrapper));

        JPanel overlayPanel = new JPanel(new GridBagLayout());
        overlayPanel.setOpaque(false);

        GridBagConstraints gbcOverlay = new GridBagConstraints();

        // Fila 0: Panel Topográfico
        gbcOverlay.gridx = 0; gbcOverlay.gridy = 0;
        gbcOverlay.gridwidth = 2; 
        gbcOverlay.weightx = 1.0; gbcOverlay.weighty = 0.0;
        gbcOverlay.anchor = GridBagConstraints.NORTH;
        gbcOverlay.fill = GridBagConstraints.NONE;
        gbcOverlay.insets = new Insets(20, 0, 0, 0); 
        overlayPanel.add(panelGlobalTopografico, gbcOverlay);

        gbcOverlay.gridx = 0; gbcOverlay.gridy = 1;
        gbcOverlay.gridwidth = 2;
        gbcOverlay.weightx = 1.0; gbcOverlay.weighty = 1.0;
        gbcOverlay.fill = GridBagConstraints.BOTH;
        gbcOverlay.insets = new Insets(0, 0, 0, 0);
        overlayPanel.add(Box.createGlue(), gbcOverlay);

        // Fila 2 - Botón Ajustes
        gbcOverlay.gridx = 0; gbcOverlay.gridy = 2;
        gbcOverlay.gridwidth = 1;
        gbcOverlay.weightx = 0.5; gbcOverlay.weighty = 0.0;
        gbcOverlay.anchor = GridBagConstraints.SOUTHWEST;
        gbcOverlay.fill = GridBagConstraints.NONE;
        gbcOverlay.insets = new Insets(0, 20, 20, 0);
        overlayPanel.add(btnConfig, gbcOverlay);

        // Fila 2 - HUD 
        gbcOverlay.gridx = 1; gbcOverlay.gridy = 2;
        gbcOverlay.gridwidth = 1;
        gbcOverlay.weightx = 0.5; gbcOverlay.weighty = 0.0;
        gbcOverlay.anchor = GridBagConstraints.SOUTHEAST;
        gbcOverlay.fill = GridBagConstraints.NONE;
        gbcOverlay.insets = new Insets(0, 0, 20, 20);
        overlayPanel.add(hud, gbcOverlay);

        mapWrapper.add(overlayPanel); 
        mapWrapper.add(panelMapa);

        // 4. SPLIT PANE
        splitPaneMapa = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, mapWrapper);
        splitPaneMapa.setDividerLocation(250);
        splitPaneMapa.setContinuousLayout(true);
        add(splitPaneMapa, BorderLayout.CENTER);

        // Tooltip del mapa
        tooltipLabel = new JLabel("");
        tooltipLabel.setOpaque(true);
        tooltipLabel.setBackground(new Color(255, 255, 255, 220)); 
        tooltipLabel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        tooltipLabel.setSize(320, 70);
        tooltipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tooltipLabel.setFont(new Font("Arial", Font.BOLD, 15));
        tooltipLabel.setVisible(false);
        panelMapa.getMapPane().add(tooltipLabel);

        // ACCIONES PRINCIPALES
        btnGenPdf.addActionListener(e -> {
            if (RegistroCalculos.getBitacora().isEmpty()) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(this, 
                    "ERROR: No hay cálculos registrados para exportar.\nRealice al menos una operación topográfica.", 
                    "SISTEMA DE REGISTRO", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                generadorDoc.generarPDF();
            }
        });

        btnHerramientas.addActionListener(e -> {
            if(!panelGlobalTopografico.isVisible()) {
                panelGlobalTopografico.setVisible(true);
                btnHerramientas.setBackground(azulClaro);
            } else {
                panelGlobalTopografico.setVisible(false);
                btnHerramientas.setBackground(azulOscuro);
            }
            overlayPanel.revalidate();
            overlayPanel.repaint();
        });

        btnConfigIP.addActionListener(e -> {
            Red dlg = new Red(SwingUtilities.getWindowAncestor(this),main.getComunicacionIP());
            dlg.setVisible(true);
        });

        btnAgregar.addActionListener(e -> {
            CoordenadasRectangulares coord = new CoordenadasRectangulares(0,0,0);
            dialogFactory.AgregarBlancoDialog(coord, nuevoBlanco -> {
                agregarBlanco(nuevoBlanco); 
                String sugerido = designacionBlancoPrefijo + " " + designacionBlancoContador;
                if (nuevoBlanco.getNombre().equals(sugerido)) {
                    designacionBlancoContador++;
                }
            });
        });

        btnEliminar.addActionListener(e -> {
            Blanco selecB = listaUIBlancos.getSelectedValue();
            Poligonal selecP = listaUIPoligonales.getSelectedValue(); 

            boolean borrarBlanco = false;
            boolean borrarPoligonal = false;

            if (selecB != null && selecP != null) {
                Object[] opciones = {"Eliminar Blanco", "Eliminar Linea/Punto", "Cancelar"};
                int eleccion = JOptionPane.showOptionDialog(this,
                        "Tiene seleccionado un Blanco y una Poligonal al mismo tiempo.\n¿Cuál de los dos desea eliminar?",
                        "Selección Múltiple",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, opciones, opciones[0]);

                if (eleccion == 0) borrarBlanco = true;
                else if (eleccion == 1) borrarPoligonal = true;
                else return;
            } 
            else if (selecB != null) borrarBlanco = true;
            else if (selecP != null) borrarPoligonal = true;
            else {
                sonidos.clickError();
                JOptionPane.showMessageDialog(this, "Seleccione un elemento para eliminar.", "SISTEMA", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (borrarBlanco) {
                listaDeBlancos.remove(selecB);
                modeloListaBlancos.removeElement(selecB);
                panelMapa.eliminarBlanco(selecB);
                mapeoDeVertices.remove(selecB); 
            }
            if (borrarPoligonal) {
                listaDePoligonales.remove(selecP);
                modeloListaPoligonales.removeElement(selecP);
                panelMapa.eliminarPoligonal(selecP);
                listaDePuntos.removeIf(p -> p.getName().equals(selecP.getName()));

                if (selecP instanceof Punto) {
                    mapeoDeVertices.remove((Posicionable) selecP);
                    mapeoDeVertices.values().removeIf(val -> val.equals(selecP));
                } 
                else if (selecP instanceof Linea) {
                    Linea linea = (Linea) selecP;
                    Posicionable keyToRemove = null;

                    for (Map.Entry<Posicionable, Posicionable> entry : mapeoDeVertices.entrySet()) {
                        Posicionable origen = entry.getKey();
                        Posicionable destino = entry.getValue();

                        boolean mismoOrigen = origen.getCoordenadas().equals(linea.getC1()); 
                        boolean mismoDestino = destino.getCoordenadas().equals(linea.getC2());

                        if (mismoOrigen && mismoDestino) {
                            keyToRemove = origen;
                            break; 
                        }
                    }
                    if (keyToRemove != null) {
                        mapeoDeVertices.remove(keyToRemove);
                    }
                }
            }

            listaUIBlancos.clearSelection();
            listaUIPoligonales.clearSelection();
            panelMapa.repaint();
        });

        btnRefrescar.addActionListener(e -> panelMapa.refrescar());
        
        btnConfig.addActionListener(e -> dialogFactory.ConfiguracionDialog());

        PIFbtn.addActionListener(e -> {
            armarPIF(listaUIBlancos.getSelectedValue());        
            panelPIF.mostrarDatosDeBlanco();
            panelPIF.getMetodoYTiroPanel().mostrarPanelPrincipal();
        });

        configurarHerramientasMapa();
        configurarPopUpMenus();
        
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
                if (!e.isPopupTrigger()) return;
                int idx = listaUIPoligonales.locationToIndex(e.getPoint());
                if (idx != -1 && listaUIPoligonales.getCellBounds(idx, idx).contains(e.getPoint())) {
                    Poligonal elemento = modeloListaPoligonales.getElementAt(idx);
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
        
        panelPIF.setMapaObservacion(panelMapa);
    }
    
    private void configurarPopUpMenus() {  	
    	this.popupMenu = new JPopupMenu();
        popupMenu.setPreferredSize(new Dimension(260,220));
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
            Posicionable bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) {
                dialogFactory.MedirDialog(bSel);
            }
        });
        itemEditar.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) {
                dialogFactory.EditarBlancoDialog(bSel, blancoEditado -> {
                    listaUIBlancos.repaint();
                    panelMapa.eliminarBlanco(bSel);
                    panelMapa.agregarBlanco(blancoEditado);
                });
            }
        });
        itemMarcarPolares.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) {
                dialogFactory.AgregarEnPolaresDialog(bSel, nuevoBlanco -> {                
                    agregarBlanco(nuevoBlanco);
                    String sugerido = getPrefijo() + " " + getContador();
                    if (nuevoBlanco.getNombre().equals(sugerido)) {
                        designacionBlancoContador++;
                    }
                });
            }
        });
        itemInfoBlanco.addActionListener(e -> {
            Blanco bSel = listaUIBlancos.getSelectedValue();
            if (bSel != null) {
                dialogFactory.InfoBlancoDialog(bSel);
            }
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
        JMenuItem itemCerrarP = new JMenuItem("Cerrar Poligonal");
        itemCerrarP.setBackground(new Color(45, 45, 85)); 
        itemCerrarP.setForeground(Color.WHITE);
        itemCerrarP.setFont(new Font("Arial", Font.BOLD, 15));

        popupMenuPunto.add(itemMedirP);
        popupMenuPunto.add(itemInfoP);
        popupMenuPunto.add(itemEnviarPunto);
        popupMenuPunto.add(itemCerrarP);

        itemMedirP.addActionListener(e -> {
            Posicionable bSel = (Posicionable) listaUIPoligonales.getSelectedValue();
            if (bSel != null) {
                dialogFactory.MedirDialog(bSel);
            }
        });
        itemInfoP.addActionListener(e -> {
            Posicionable bSel = (Posicionable) listaUIPoligonales.getSelectedValue();
            if (bSel != null) {
                dialogFactory.InfoPuntoDialog(bSel);
            }
        });
        itemEnviarPunto.addActionListener(e -> {
        	Posicionable bSel = (Posicionable) listaUIPoligonales.getSelectedValue();
            if (bSel != null) enviarPunto(bSel);
        });
        itemCerrarP.addActionListener(e -> {
            Posicionable selec = (Posicionable) listaUIPoligonales.getSelectedValue();
            if (selec != null && selec.soportaCierrePoligonal()) {
                dialogFactory.CierrePoligonalDialog((Punto) selec, (resultado, informe) -> {
                    RegistroCalculos.guardar("CIERRE DE POLIGONAL", informe);
                    JOptionPane.showMessageDialog(this, "Control de precisión registrado con éxito.");
                });	
            }
        });
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

    public void enviarPunto(Posicionable p) {
        if (p == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("PUNTO|");
        sb.append("NOMBRE=").append(p.getNombre()).append("|");
        sb.append("X=").append(p.getCoordenadas().getX()).append("|");
        sb.append("Y=").append(p.getCoordenadas().getY());

        String mensajeFinal = sb.toString();

        if (main != null && main.getComunicacionIP() != null) {
        	main.getComunicacionIP().enviarATodos(mensajeFinal);
            mensajeria.getConsolaMensajes().agregarMensaje("[TX] Punto " + p.getNombre() + " transmitido.");
        } else {
            sonidos.clickError();
            JOptionPane.showMessageDialog(this, 
                "FALLO DE COMUNICACIONES:\nNo se detectó el enlace IP para el envío.", 
                "SISTEMA", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
        
    public void enviarBlanco(Blanco b) {
        if (b == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("BLANCO|");
        sb.append("NOMBRE=").append(b.getNombre()).append("|");
        sb.append("NAT=").append(b.getNaturaleza()).append("|");
        sb.append("FECHA=").append(b.getFechaDeActualizacion()).append("|");
        sb.append("ORI=").append((int)b.getOrientacion()).append("|");
        
        sb.append("ENTIDAD=").append(b.getUltEntidad() != null ? b.getUltEntidad() : "DESCONOCIDO").append("|");
        sb.append("AFILIACION=").append(b.getUltAfiliacion() != null ? b.getUltAfiliacion() : "DESCONOCIDO").append("|");
        sb.append("ECHELON=").append(b.getUltEchelon() != null ? b.getUltEchelon() : "Por Defecto").append("|");

        String info = b.getInformacionAdicional();
        if (info == null || info.trim().isEmpty()) {
            info = "Sin Informacion Adicional";
        }
        sb.append("INFO=").append(info).append("|");
        
        sb.append("SIMID=").append(b.getSimID()).append("|");
        sb.append("SIT=").append(b.getSituacionMovimiento()).append("|");
        sb.append("X=").append(b.getCoordenadas().getX()).append("|");
        sb.append("Y=").append(b.getCoordenadas().getY()).append("|");
        sb.append("Z=").append(b.getCoordenadas().getCota());

        String mensajeFinal = sb.toString();

        if (main != null && main.getComunicacionIP() != null) {
        	main.getComunicacionIP().enviarATodos(mensajeFinal);
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

                String xVisual = String.format("%.0f", x);
                String yVisual = String.format("%.0f", y);
                
                if (xVisual.length() > 2) xVisual = xVisual.substring(2);
                if (yVisual.length() > 2) yVisual = yVisual.substring(2);

                SeleccionDeMarcado(coord, xVisual, yVisual);
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

    private void SeleccionDeMarcado(CoordenadasRectangulares coord, String xV, String yV) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Selección de Marcador", true);
        dialog.setSize(600, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        JLabel lblInfo = new JLabel("<html><center><font size='4'>COORDENADAS</font><br>"
                + "<font color='black' size='6'>DERECHAS: " + xV + " | ARRIBAS: " + yV + "</font></center></html>");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblInfo, gbc);

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

        btnBlanco.addActionListener(e -> {
            dialog.dispose();
            dialogFactory.AgregarBlancoDialog(coord, nuevoBlanco -> {
                agregarBlanco(nuevoBlanco); 
                String sugerido = getPrefijo() + " " + getContador();
                if (nuevoBlanco.getNombre().equals(sugerido)) {
                    designacionBlancoContador++;
                }
            });
        });

        btnPunto.addActionListener(e -> {
            dialog.dispose();
            dialogFactory.AgregarPuntoDialog(coord, nuevoPunto -> {
                agregarPunto(nuevoPunto);  
            });
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    public void cambiarMapaEnTiempoReal() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        FileDialog fd = new FileDialog(parentFrame, "SELECCIONAR NUEVA CARTOGRAFÍA GeoTIFF", FileDialog.LOAD);
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
            nuevoMapa.getMapPane().add(tooltipLabel);
            
            if (splitPaneMapa != null) {
                JPanel mapWrapper = (JPanel) splitPaneMapa.getRightComponent();
                mapWrapper.remove(this.panelMapa); 
                
                this.panelMapa = nuevoMapa;
                
                mapWrapper.add(this.panelMapa);
                
                panelPIF.setMapaObservacion(panelMapa);
                configurarHerramientasMapa();
            }

            revalidate();
            repaint();
        }
    }
    
    public void cambiarDesignacionEnTiempoReal() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "MODIFICAR SECUENCIA OPERATIVA", true);
        dialog.setSize(600, 450); 
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fuenteEtiqueta = new Font("Arial", Font.BOLD, 22);
        Font fuenteCampo = new Font("Monospaced", Font.BOLD, 24);
        Font fuenteBoton = new Font("Arial", Font.BOLD, 20);

        JLabel lblPrefijo = new JLabel("PREFIJO (LETRAS):");
        lblPrefijo.setForeground(Color.WHITE);
        lblPrefijo.setFont(fuenteEtiqueta);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblPrefijo, gbc);

        JTextField txtPrefijo = new JTextField(designacionBlancoPrefijo);
        txtPrefijo.setBackground(new Color(60, 60, 60));
        txtPrefijo.setForeground(new Color(0, 255, 0)); 
        txtPrefijo.setFont(fuenteCampo);
        txtPrefijo.setHorizontalAlignment(JTextField.CENTER);
        txtPrefijo.setPreferredSize(new Dimension(250, 60));
        gbc.gridx = 1;
        panel.add(txtPrefijo, gbc);

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

        JButton btnGuardar = new JButton("ACTUALIZAR");
        JButton btnCancelar = new JButton("ABORTAR");

        for (JButton b : new JButton[]{btnGuardar, btnCancelar}) {
            b.setFont(fuenteBoton);
            b.setPreferredSize(new Dimension(0, 80)); 
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
        dialog.setSize(800, 450); 
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setIconImage(imgEscalada);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(grisFondo);
        panel.setBorder(BorderFactory.createLineBorder(verdeMilitar, 2));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20); 
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
        txtDesignacion.setForeground(new Color(0, 255, 0)); 
        txtDesignacion.setFont(new Font("Monospaced", Font.BOLD, 22)); 
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
            b.setPreferredSize(new Dimension(280, 55)); 
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

        btnExaminar.addActionListener(e -> {
            FileDialog fd = new FileDialog(dialog, "SELECCIONAR ARCHIVO CARTOGRÁFICO TIFF", FileDialog.LOAD);
            fd.setFile("*.tif;*.tiff");
            fd.setDirectory("C:\\");
            fd.setVisible(true);

            if (fd.getFile() != null) {
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
    
    public void agregarPunto(Punto p) {
        if (p == null) return;
        
        boolean esNuevo = false; 
        
        if (!listaDePuntos.contains(p)) {
            listaDePuntos.add(p);
            esNuevo = true;
        }
       
        if (!listaDePoligonales.contains(p)) {
            listaDePoligonales.add(p);
            esNuevo = true;
        }
        
        if (!modeloListaPoligonales.contains(p)) {
            modeloListaPoligonales.addElement(p);
            esNuevo = true;
        }
        
        if (esNuevo) {
            panelMapa.agregarPoligonal(p);
        }
        
        listaUIPoligonales.repaint();
    }

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
    
    @SuppressWarnings("unused")
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
    
    public DefaultListModel<Blanco> getModeloListaBlancos(){
        return modeloListaBlancos;
    }
    
    public DefaultListModel<Poligonal> getModeloListaPoligonales(){
        return modeloListaPoligonales;
    }
    
    public LinkedList<Blanco> getListaBlancos(){
        return listaDeBlancos;
    }
    
    public LinkedList<Punto> getListaPuntos(){
        return listaDePuntos;
    }
    
    public LinkedList<Poligonal> getListaPoligonales(){
        return listaDePoligonales;
    }
    
    public PanelMapa getPanelMapa() {
        return panelMapa;
    }
    
    public JList<Blanco> getlistaUIBlancos(){
        return listaUIBlancos;
    }
    
    public JList<Poligonal> getlistaUIPoligonales(){
        return listaUIPoligonales;
    }

    @Override
    public String getPrefijo() {
        return this.designacionBlancoPrefijo;
    }

    @Override
    public int getContador() {
        return this.designacionBlancoContador;
    }
    
    public Map<Posicionable,Posicionable> getMapeoVertices(){
        return mapeoDeVertices;
    }
    
    @Override
    public void setPrefijo(String s) {
        this.designacionBlancoPrefijo = s;
    }

    @Override
    public void setContador(int i) {
        this.designacionBlancoContador = i;
    }
    
    public DialogFactory getDialogFactory() {
        return dialogFactory;
    }
}