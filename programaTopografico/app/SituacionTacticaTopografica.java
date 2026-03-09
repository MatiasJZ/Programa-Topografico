package app;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.*;

import comunicaciones.Red;
import dominio.Blanco;
import dominio.CoordenadasRectangulares;
import dominio.GeneradorPDF;
import dominio.Linea;
import dominio.Punto;
import dominio.RegistroCalculos;
import gestores.GestorEnlaceOperativo;
import gestores.GestorPopupMenus;
import interfaces.Poligonal;
import interfaces.Posicionable;
import paneles.PanelHerramientasTopograficas;
import paneles.PanelListasTacticas;
import paneles.PanelMapa;

/**
 * SituacionTacticaTopografica — Panel principal táctico topográfico.
 *
 * <p>Extiende {@link SituacionTacticaBase} y delega toda la lógica común
 * (blancos, mapa, designación, tooltip, diálogos) en la superclase.
 * Solo mantiene lo específico del módulo SAB:</p>
 * <ul>
 *   <li>Listas de {@link Punto} y {@link Poligonal}, con su modelo y mapeo de vértices.</li>
 *   <li>Layout con {@code OverlayLayout} (mapWrapper) dentro de un {@link JSplitPane}.</li>
 *   <li>Click en mapa: ofrece "Marcar Blanco" y "Marcar Punto" (a diferencia del OA).</li>
 *   <li>Barra de herramientas topográficas ({@link PanelHerramientasTopograficas}).</li>
 *   <li>Generación de PDF ({@link GeneradorPDF}).</li>
 *   <li>Enlace operativo obtenido de {@link ProgramaTopografico}.</li>
 * </ul>
 *
 * <p>Eliminadas ~300 líneas duplicadas respecto a la versión anterior:
 * {@code pedirArchivoAMostrar()}, {@code cambiarDesignacionEnTiempoReal()},
 * {@code cambiarMapaEnTiempoReal()}, {@code configurarHerramientasMapa()},
 * {@code agregarBlanco()}, {@code actualizarBlanco()}, {@code enviarBlanco()},
 * helpers de UI y serialización de protocolo.</p>
 *
 * @author [Matias Leonel Juarez]
 * @version 3.0
 */

public class SituacionTacticaTopografica extends SituacionTacticaBase {

    private static final long serialVersionUID = 789462013392544798L;

    private final LinkedList<Punto> listaDePuntos = new LinkedList<>();
    private final LinkedList<Poligonal> listaDePoligonales = new LinkedList<>();
    private final Map<Posicionable, Posicionable> mapeoDeVertices = new HashMap<>();
    private final DefaultListModel<Poligonal> modeloListaPoligonales;
    private final JList<Poligonal> listaUIPoligonales;

    private final ProgramaTopografico main;
    private final GeneradorPDF generadorDoc;
    private final PedidoDeFuego panelPIF;
    private Mensajeria mensajeria;

    private JSplitPane splitPaneMapa;
    private JPanel overlayPanel;
    private JPanel panelHerramientas;

    public SituacionTacticaTopografica(LinkedList<Blanco> listaDeBlancos,PedidoDeFuego pif,ProgramaTopografico main) {
    	
        super(listaDeBlancos);

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        this.main = main;
        this.panelPIF = pif;
        this.generadorDoc = new GeneradorPDF(this);

        // 1. Sub-panel de listas
        PanelListasTacticas panelListas = new PanelListasTacticas();
        modeloListaBlancos = panelListas.getModeloBlancos(); 
        modeloListaPoligonales = panelListas.getModeloPoligonales();
        listaUIBlancos = panelListas.getListaUIBlancos();  
        listaUIPoligonales = panelListas.getListaUIPoligonales();

        // 2. Herramientas topográficas
        panelHerramientas = new PanelHerramientasTopograficas(dialogFactory, listaDePuntos, listaDeBlancos, this, this::ejecutarPIF);

        // 3. HUD flotante
        Color azulOscuro = new Color(60, 60, 120);
        Color azulClaro = new Color(129, 129, 204);
        Font fuenteEmoji = new Font("Segoe UI Emoji", Font.BOLD, 16);
        Dimension dimPeq = new Dimension(135, 45);
        Dimension dimAncha = new Dimension(280, 45);

        JButton btnAgregar = boton("➕ AGREGAR", fuenteEmoji, dimPeq, null, null);
        JButton btnEliminar = boton("❌ ELIMINAR", fuenteEmoji, dimPeq, null, null);
        JButton btnRefrescar = boton("↻ REFRESCAR", fuenteEmoji, dimPeq, null, null);
        JButton btnConfigIP = boton("HARRIS", fuenteEmoji, dimPeq, azulOscuro, Color.WHITE);
        JButton btnHerramientas = boton("⚒ HERRAM.", fuenteEmoji, dimAncha, azulClaro, Color.WHITE);
        JButton btnGenPdf = boton("GENERAR PDF", fuenteEmoji, dimAncha, azulOscuro, Color.WHITE);

        JPanel hud = new JPanel(new GridBagLayout());
        hud.setBackground(new Color(0, 0, 0, 170));
        hud.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        hud.setPreferredSize(new Dimension(340, 280));
        hud.setMinimumSize(new Dimension(340, 280));

        GridBagConstraints h = new GridBagConstraints();
        h.insets = new Insets(8, 8, 8, 8);
        h.fill = GridBagConstraints.BOTH;
        h.weightx = h.weighty = 1.0;

        h.gridy = 0; h.gridx = 0; h.gridwidth = 1; hud.add(btnAgregar,h);
        h.gridx = 1; hud.add(btnEliminar,h);
        h.gridy = 1; h.gridx = 0; hud.add(btnRefrescar,h);
        h.gridx = 1; hud.add(btnConfigIP,h);
        h.gridy = 2; h.gridx = 0; h.gridwidth = 2; hud.add(btnHerramientas,h);
        h.gridy = 3; hud.add(btnGenPdf,h);

        JButton btnConfig = boton("⚙ AJUSTES", fuenteEmoji, new Dimension(150, 80), Color.DARK_GRAY, Color.WHITE);

        // 4. Mapa (pedirArchivoAMostrar() está en la superclase)
        pedirArchivoAMostrar();
        panelMapa = new PanelMapa(rutaArchivoMapa);

        // 5. Overlay (mapWrapper contiene: overlayPanel encima + panelMapa debajo)
        JPanel mapWrapper = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override public boolean isOptimizedDrawingEnabled() { return false; }
        };
        mapWrapper.setLayout(new OverlayLayout(mapWrapper));

        overlayPanel = new JPanel(new GridBagLayout());
        overlayPanel.setOpaque(false);

        GridBagConstraints ov = new GridBagConstraints();
        ov.gridx = 0; ov.gridy = 0; ov.gridwidth = 2;
        ov.weightx = 1.0; ov.weighty = 0.0;
        ov.anchor = GridBagConstraints.NORTH; ov.fill = GridBagConstraints.NONE;
        ov.insets = new Insets(20, 0, 0, 0);
        overlayPanel.add(panelHerramientas, ov);

        ov.gridy = 1; ov.weighty = 1.0; ov.fill = GridBagConstraints.BOTH;
        ov.insets = new Insets(0, 0, 0, 0);
        overlayPanel.add(Box.createGlue(), ov);

        ov.gridy = 2; ov.gridwidth = 1; ov.weightx = 0.5; ov.weighty = 0.0;
        ov.anchor = GridBagConstraints.SOUTHWEST; ov.fill = GridBagConstraints.NONE;
        ov.insets = new Insets(0, 20, 20, 0);
        overlayPanel.add(btnConfig, ov);

        ov.gridx = 1; ov.anchor = GridBagConstraints.SOUTHEAST;
        ov.insets = new Insets(0, 0, 20, 20);
        overlayPanel.add(hud, ov);

        mapWrapper.add(overlayPanel);
        mapWrapper.add(panelMapa);

        // 6. Tooltip de coordenadas (campo tooltipLabel heredado de la superclase)
        tooltipLabel = new JLabel("");
        tooltipLabel.setOpaque(true);
        tooltipLabel.setBackground(new Color(255, 255, 255, 220));
        tooltipLabel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        tooltipLabel.setSize(320, 70);
        tooltipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tooltipLabel.setFont(new Font("Arial", Font.BOLD, 15));
        tooltipLabel.setVisible(false);
        panelMapa.getMapPane().add(tooltipLabel);

        // 7. Split pane
        splitPaneMapa = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelListas, mapWrapper);
        splitPaneMapa.setDividerLocation(250);
        splitPaneMapa.setContinuousLayout(true);
        add(splitPaneMapa, BorderLayout.CENTER);

        // 8. Popup menus
        GestorPopupMenus gestorPopup = new GestorPopupMenus(listaUIBlancos, listaUIPoligonales, dialogFactory, this);
        JPopupMenu popupBlancos     = gestorPopup.getPopupBlancos();
        JPopupMenu popupPoligonales = gestorPopup.getPopupPoligonales();

        // 9. Mouse listeners para popups
        listaUIBlancos.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { chkPopupBlancos(e); }
            @Override public void mouseReleased(MouseEvent e) { chkPopupBlancos(e); }
            private void chkPopupBlancos(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                int idx = listaUIBlancos.locationToIndex(e.getPoint());
                if (idx != -1 && listaUIBlancos.getCellBounds(idx, idx).contains(e.getPoint())) {
                    listaUIBlancos.setSelectedIndex(idx);
                    listaUIBlancos.requestFocusInWindow();
                    popupBlancos.show(e.getComponent(), e.getX(), e.getY());
                } else { listaUIBlancos.clearSelection(); }
            }
        });

        listaUIPoligonales.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { chkPopupPoligonales(e); }
            @Override public void mouseReleased(MouseEvent e) { chkPopupPoligonales(e); }
            private void chkPopupPoligonales(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                int idx = listaUIPoligonales.locationToIndex(e.getPoint());
                if (idx != -1 && listaUIPoligonales.getCellBounds(idx, idx).contains(e.getPoint())) {
                    if (modeloListaPoligonales.getElementAt(idx).tienePopUpMenu()) {
                        listaUIPoligonales.setSelectedIndex(idx);
                        listaUIPoligonales.requestFocusInWindow();
                        popupPoligonales.show(e.getComponent(), e.getX(), e.getY());
                    }
                } else { listaUIPoligonales.clearSelection(); }
            }
        });

        // 10. Listeners HUD
        btnGenPdf.addActionListener(e -> {
            if (RegistroCalculos.getBitacora().isEmpty()) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(this,
                        "ERROR: No hay cálculos registrados para exportar.\nRealice al menos una operación topográfica.",
                        "SISTEMA DE REGISTRO", JOptionPane.WARNING_MESSAGE);
            } else { generadorDoc.generarPDF(); }
        });

        btnHerramientas.addActionListener(e -> {
            boolean visible = !panelHerramientas.isVisible();
            panelHerramientas.setVisible(visible);
            btnHerramientas.setBackground(visible ? azulClaro : azulOscuro);
            overlayPanel.revalidate();
            overlayPanel.repaint();
        });

        btnConfigIP.addActionListener(e -> {
            Red dlg = new Red(SwingUtilities.getWindowAncestor(this), main.getComunicacionIP());
            dlg.setVisible(true);
        });

        btnAgregar.addActionListener(e ->
            dialogFactory.AgregarBlancoDialog(new CoordenadasRectangulares(0, 0, 0), nuevo -> {
                agregarBlanco(nuevo);
                if (nuevo.getNombre().equals(designacionBlancoPrefijo + " " + designacionBlancoContador))
                    designacionBlancoContador++;
            })
        );

        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnRefrescar.addActionListener(e -> panelMapa.refrescar());
        btnConfig.addActionListener(e -> dialogFactory.ConfiguracionDialog());

        // 11. Herramientas de mapa (método heredado de la superclase)
        configurarHerramientasMapa();
        panelPIF.setMapaObservacion(panelMapa);
    }

    @Override
    protected GestorEnlaceOperativo getComunicacion() {
        return (main != null) ? main.getComunicacionIP() : null;
    }

    @Override
    protected void onClickEnMapa(CoordenadasRectangulares coord, String xV, String yV) {
        seleccionDeMarcado(coord, xV, yV);
    }

    @Override
    protected void reemplazarMapaEnLayout(PanelMapa nuevoMapa) {
        if (splitPaneMapa == null) return;
        JPanel mapWrapper = (JPanel) splitPaneMapa.getRightComponent();
        mapWrapper.remove(panelMapa);       
        mapWrapper.add(nuevoMapa);
        panelPIF.setMapaObservacion(nuevoMapa);
    }

    @Override
    protected void limpiarDatosMapa() {
        listaDePuntos.clear();
        listaDePoligonales.clear();
        modeloListaPoligonales.clear();
    }

    public void agregarPunto(Punto p) {
        if (p == null) return;
        boolean esNuevo = false;
        if (!listaDePuntos.contains(p)) { listaDePuntos.add(p);                esNuevo = true; }
        if (!listaDePoligonales.contains(p)) { listaDePoligonales.add(p);            esNuevo = true; }
        if (!modeloListaPoligonales.contains(p)) { modeloListaPoligonales.addElement(p); esNuevo = true; }
        if (esNuevo) panelMapa.agregarPoligonal(p);
        listaUIPoligonales.repaint();
    }

    public void actualizarPunto(Punto p) {
        if (p == null) return;
        int idx = modeloListaPoligonales.indexOf(p);
        if (idx >= 0) modeloListaPoligonales.set(idx, p);
        listaUIPoligonales.repaint();
        panelMapa.eliminarPoligonal(p);
        panelMapa.agregarPoligonal(p);
    }

    public void enviarPunto(Posicionable p) {
        if (p == null) return;
        GestorEnlaceOperativo com = getComunicacion();
        String msg = "PUNTO|NOMBRE=" + p.getNombre()
                + "|X=" + p.getCoordenadas().getX()
                + "|Y=" + p.getCoordenadas().getY();
        if (com != null) {
            com.enviarATodos(msg);
            if (mensajeria != null)
                mensajeria.getConsolaMensajes().agregarMensaje("[TX] Punto " + p.getNombre() + " transmitido.");
        } else {
            sonidos.clickError();
            JOptionPane.showMessageDialog(this,
                    "FALLO DE COMUNICACIONES:\nNo se detectó el enlace IP para el envío.",
                    "SISTEMA", JOptionPane.ERROR_MESSAGE);
        }
    }

    public LinkedList<Blanco> getListaDeBlancos() { return listaDeBlancos; }
    public LinkedList<Punto> getListaDePuntos() { return listaDePuntos; }
    public LinkedList<Punto> getListaPuntos() { return listaDePuntos; }
    public LinkedList<Poligonal> getListaPoligonales() { return listaDePoligonales; }
    public JList<Poligonal> getlistaUIPoligonales(){ return listaUIPoligonales; }
    public DefaultListModel<Poligonal> getModeloListaPoligonales() { return modeloListaPoligonales; }
    public Map<Posicionable, Posicionable> getMapeoVertices() { return mapeoDeVertices; }
    public void setPanelMensajeria(Mensajeria m) { this.mensajeria = m; }

    private void ejecutarPIF() {
        armarPIF(listaUIBlancos.getSelectedValue());
        panelPIF.mostrarDatosDeBlanco();
        panelPIF.getMetodoYTiroPanel().mostrarPanelPrincipal();
    }

    private void armarPIF(Blanco b) {
        panelPIF.getDatosDeBlancoPanel().setDatosBlanco(b);
        panelPIF.getMetodoYTiroPanel().actualizar();
        Container parent = this.getParent();
        while (parent != null && !(parent instanceof ProgramaTopografico)) parent = parent.getParent();
        if (parent instanceof ProgramaTopografico obs) obs.mostrarPanel("PEDIDO");
    }

    private void eliminarSeleccionado() {
        Blanco    selB = listaUIBlancos.getSelectedValue();
        Poligonal selP = listaUIPoligonales.getSelectedValue();
        boolean borrarB = false, borrarP = false;

        if (selB != null && selP != null) {
            Object[] ops = {"Eliminar Blanco", "Eliminar Linea/Punto", "Cancelar"};
            int elec = JOptionPane.showOptionDialog(this,
                    "Tiene seleccionado un Blanco y una Poligonal.\n¿Cuál desea eliminar?",
                    "Selección Múltiple", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, ops, ops[0]);
            if      (elec == 0) borrarB = true;
            else if (elec == 1) borrarP = true;
            else return;
        } else if (selB != null) { borrarB = true; }
          else if (selP != null) { borrarP = true; }
          else {
            sonidos.clickError();
            JOptionPane.showMessageDialog(this, "Seleccione un elemento para eliminar.",
                    "SISTEMA", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (borrarB) {
            listaDeBlancos.remove(selB);
            modeloListaBlancos.removeElement(selB);
            panelMapa.eliminarBlanco(selB);
            mapeoDeVertices.remove(selB);
        }
        if (borrarP) {
            listaDePoligonales.remove(selP);
            modeloListaPoligonales.removeElement(selP);
            panelMapa.eliminarPoligonal(selP);
            listaDePuntos.removeIf(p -> p.getName().equals(selP.getName()));
            if (selP instanceof Punto pt) {
                mapeoDeVertices.remove((Posicionable) pt);
                mapeoDeVertices.values().removeIf(v -> v.equals(pt));
            } else if (selP instanceof Linea linea) {
                Posicionable key = null;
                for (Map.Entry<Posicionable, Posicionable> entry : mapeoDeVertices.entrySet()) {
                    if (entry.getKey().getCoordenadas().equals(linea.getC1())
                     && entry.getValue().getCoordenadas().equals(linea.getC2())) {
                        key = entry.getKey(); break;
                    }
                }
                if (key != null) mapeoDeVertices.remove(key);
            }
        }
        listaUIBlancos.clearSelection();
        listaUIPoligonales.clearSelection();
        panelMapa.repaint();
    }

    private void seleccionDeMarcado(CoordenadasRectangulares coord, String xV, String yV) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Selección de Marcador", true);
        dialog.setSize(600, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill   = GridBagConstraints.BOTH;

        JLabel lblInfo = new JLabel("<html><center><font size='4'>COORDENADAS</font><br>"
                + "<font color='black' size='6'>DERECHAS: " + xV + " | ARRIBAS: " + yV + "</font></center></html>");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblInfo, gbc);

        Font fb      = new Font("Arial", Font.BOLD, 18);
        JButton btnBlanco = boton("Marcar Blanco", fb, new Dimension(200, 80), null, null);
        JButton btnPunto  = boton("Marcar Punto",  fb, new Dimension(200, 80), null, null);

        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0; panel.add(btnBlanco, gbc);
        gbc.gridx = 1; panel.add(btnPunto,  gbc);

        btnBlanco.addActionListener(e -> {
            dialog.dispose();
            dialogFactory.AgregarBlancoDialog(coord, nuevo -> {
                agregarBlanco(nuevo);
                if (nuevo.getNombre().equals(getPrefijo() + " " + getContador()))
                    designacionBlancoContador++;
            });
        });

        btnPunto.addActionListener(e -> {
            dialog.dispose();
            dialogFactory.AgregarPuntoDialog(coord, this::agregarPunto);
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }
}