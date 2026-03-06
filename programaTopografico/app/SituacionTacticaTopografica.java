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
import dominio.Punto;
import dominio.GeneradorPDF;
import dominio.Linea;
import dominio.CoordenadasRectangulares;
import dominio.RegistroCalculos;
import gestores.GestorPopupMenus;
import gestores.GestorSonido;
import interfaces.DesignacionProvider;
import interfaces.DialogFactory;
import interfaces.Poligonal;
import interfaces.Posicionable;
import paneles.PanelHerramientasTopograficas;
import paneles.PanelListasTacticas;
import paneles.PanelMapa;
import util.Configuracion;
import util.FabricaComponentes;
import util.FabricaDialogosTacticos;

/**
 * SituacionTacticaTopografica — Panel principal táctico topográfico.
 *
 * <p>Actúa como <b>coordinador</b>: ensambla los sub-paneles especializados
 * y expone la API pública que usa el resto del sistema.
 * La lógica de presentación está delegada en:
 * <ul>
 *   <li>{@link PanelListasTacticas} — listas de blancos y poligonales.</li>
 *   <li>{@link PanelHerramientasTopograficas} — barra de cálculo topográfico.</li>
 *   <li>{@link GestorPopupMenus} — menús contextuales de las listas.</li>
 * </ul>
 *
 * @author [Matias Leonel Juarez]
 * @version 2.0
 */
public class SituacionTacticaTopografica extends JPanel implements DesignacionProvider {

    private static final long serialVersionUID = 789462013392544798L;

    private final LinkedList<Blanco> listaDeBlancos;
    private final LinkedList<Punto> listaDePuntos = new LinkedList<>();
    private final LinkedList<Poligonal> listaDePoligonales = new LinkedList<>();
    private final Map<Posicionable, Posicionable> mapeoDeVertices = new HashMap<>();

    private String designacionBlancoPrefijo;
    private int designacionBlancoContador;
    private String rutaArchivoMapa;

    private final ProgramaTopografico main;
    private final GestorSonido sonidos;
    private final DialogFactory dialogFactory;
    private final GeneradorPDF generadorDoc;
    private Mensajeria mensajeria;
    private final PedidoDeFuego panelPIF;

    private final PanelListasTacticas panelListas;
    private PanelMapa panelMapa;
    private JPanel panelHerramientas;
    private JSplitPane splitPaneMapa;
    private JLabel tooltipLabel;

    private final DefaultListModel<Blanco> modeloListaBlancos;
    private final DefaultListModel<Poligonal> modeloListaPoligonales;
    private final JList<Blanco> listaUIBlancos;
    private final JList<Poligonal> listaUIPoligonales;

    public SituacionTacticaTopografica(LinkedList<Blanco> listaDeBlancos,
                                       PedidoDeFuego pif,
                                       ProgramaTopografico main) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        this.main           = main;
        this.listaDeBlancos = listaDeBlancos;
        this.panelPIF       = pif;
        this.sonidos        = new GestorSonido();
        this.dialogFactory  = new FabricaDialogosTacticos(this, sonidos);
        this.generadorDoc   = new GeneradorPDF(this);

        this.rutaArchivoMapa           = Configuracion.get("ruta_mapa_defecto", "C:/Mapas/default.tif");
        this.designacionBlancoPrefijo  = Configuracion.get("prefijo_blancos", "AF");
        this.designacionBlancoContador = Configuracion.getInt("contador_blancos_inicio", 6400);

        // 1. Sub-panel de listas
        panelListas            = new PanelListasTacticas();
        modeloListaBlancos     = panelListas.getModeloBlancos();
        modeloListaPoligonales = panelListas.getModeloPoligonales();
        listaUIBlancos         = panelListas.getListaUIBlancos();
        listaUIPoligonales     = panelListas.getListaUIPoligonales();

        // 2. Herramientas topográficas
        panelHerramientas = new PanelHerramientasTopograficas(
                dialogFactory, listaDePuntos, listaDeBlancos, this, this::ejecutarPIF);

        // 3. HUD flotante
        Color azulOscuro  = new Color(60, 60, 120);
        Color azulClaro   = new Color(129, 129, 204);
        Font  fuenteEmoji = new Font("Segoe UI Emoji", Font.BOLD, 16);
        Dimension dimPeq  = new Dimension(135, 45);
        Dimension dimAncha = new Dimension(280, 45);

        JButton btnAgregar      = boton("➕ AGREGAR",   fuenteEmoji, dimPeq,   null,       null);
        JButton btnEliminar     = boton("❌ ELIMINAR",  fuenteEmoji, dimPeq,   null,       null);
        JButton btnRefrescar    = boton("↻ REFRESCAR", fuenteEmoji, dimPeq,   null,       null);
        JButton btnConfigIP     = boton("HARRIS",       fuenteEmoji, dimPeq,   azulOscuro, Color.WHITE);
        JButton btnHerramientas = boton("⚒ HERRAM.",   fuenteEmoji, dimAncha, azulClaro,  Color.WHITE);
        JButton btnGenPdf       = boton("GENERAR PDF",  fuenteEmoji, dimAncha, azulOscuro, Color.WHITE);

        JPanel hud = new JPanel(new GridBagLayout());
        hud.setBackground(new Color(0, 0, 0, 170));
        hud.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        hud.setPreferredSize(new Dimension(340, 280));
        hud.setMinimumSize(new Dimension(340, 280));

        GridBagConstraints h = new GridBagConstraints();
        h.insets = new Insets(8, 8, 8, 8);
        h.fill = GridBagConstraints.BOTH;
        h.weightx = h.weighty = 1.0;

        h.gridy = 0; h.gridx = 0; h.gridwidth = 1; hud.add(btnAgregar, h);
        h.gridx = 1;                                hud.add(btnEliminar, h);
        h.gridy = 1; h.gridx = 0;                  hud.add(btnRefrescar, h);
        h.gridx = 1;                                hud.add(btnConfigIP, h);
        h.gridy = 2; h.gridx = 0; h.gridwidth = 2; hud.add(btnHerramientas, h);
        h.gridy = 3;                                hud.add(btnGenPdf, h);

        JButton btnConfig = boton("⚙ AJUSTES", fuenteEmoji, new Dimension(150, 80), Color.DARK_GRAY, Color.WHITE);

        // 4. Mapa
        pedirArchivoAMostrar();
        panelMapa = new PanelMapa(rutaArchivoMapa);

        // 5. Overlay
        JPanel mapWrapper = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override public boolean isOptimizedDrawingEnabled() { return false; }
        };
        mapWrapper.setLayout(new OverlayLayout(mapWrapper));

        JPanel overlayPanel = new JPanel(new GridBagLayout());
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

        // 6. Tooltip
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
        GestorPopupMenus gestorPopup = new GestorPopupMenus(
                listaUIBlancos, listaUIPoligonales, dialogFactory, this);
        JPopupMenu popupBlancos     = gestorPopup.getPopupBlancos();
        JPopupMenu popupPoligonales = gestorPopup.getPopupPoligonales();

        // 9. Mouse listeners para popups
        listaUIBlancos.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { chkPopup(e); }
            @Override public void mouseReleased(MouseEvent e) { chkPopup(e); }
            private void chkPopup(MouseEvent e) {
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
            @Override public void mousePressed(MouseEvent e)  { chkPopup(e); }
            @Override public void mouseReleased(MouseEvent e) { chkPopup(e); }
            private void chkPopup(MouseEvent e) {
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
            overlayPanel.revalidate(); overlayPanel.repaint();
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

        // 11. Herramientas de mapa
        configurarHerramientasMapa();
        panelPIF.setMapaObservacion(panelMapa);
    }

    public void agregarBlanco(Blanco b) {
        if (b == null) return;
        if (!listaDeBlancos.contains(b))     listaDeBlancos.add(b);
        if (!modeloListaBlancos.contains(b)) modeloListaBlancos.addElement(b);
        panelMapa.agregarBlanco(b);
        listaUIBlancos.repaint();
    }

    public void actualizarBlanco(Blanco b) {
        if (b == null) return;
        int idx = modeloListaBlancos.indexOf(b);
        if (idx >= 0) modeloListaBlancos.set(idx, b);
        listaUIBlancos.repaint();
        panelMapa.eliminarBlanco(b);
        panelMapa.agregarBlanco(b);
    }

    public void enviarBlanco(Blanco b) {
        if (b == null) return;
        String info = b.getInformacionAdicional();
        if (info == null || info.trim().isEmpty()) info = "Sin Informacion Adicional";
        String msg = "BLANCO|NOMBRE=" + b.getNombre() + "|NAT=" + b.getNaturaleza()
                + "|FECHA=" + b.getFechaDeActualizacion() + "|ORI=" + (int) b.getOrientacion()
                + "|ENTIDAD="    + nvl(b.getUltEntidad(),    "DESCONOCIDO")
                + "|AFILIACION=" + nvl(b.getUltAfiliacion(), "DESCONOCIDO")
                + "|ECHELON="    + nvl(b.getUltEchelon(),    "Por Defecto")
                + "|INFO=" + info + "|SIMID=" + b.getSimID()
                + "|SIT=" + b.getSituacionMovimiento()
                + "|X=" + b.getCoordenadas().getX()
                + "|Y=" + b.getCoordenadas().getY()
                + "|Z=" + b.getCoordenadas().getCota();
        enviar(msg, "FALLO DE TRANSMISIÓN", "ERROR DE ENLACE: No hay conexión con el módulo de comunicaciones.");
    }

    public void agregarPunto(Punto p) {
        if (p == null) return;
        boolean esNuevo = false;
        if (!listaDePuntos.contains(p))          { listaDePuntos.add(p);                esNuevo = true; }
        if (!listaDePoligonales.contains(p))     { listaDePoligonales.add(p);            esNuevo = true; }
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
        String msg = "PUNTO|NOMBRE=" + p.getNombre()
                + "|X=" + p.getCoordenadas().getX()
                + "|Y=" + p.getCoordenadas().getY();
        if (enviar(msg, "SISTEMA", "FALLO DE COMUNICACIONES:\nNo se detectó el enlace IP para el envío."))
            if (mensajeria != null)
                mensajeria.getConsolaMensajes().agregarMensaje("[TX] Punto " + p.getNombre() + " transmitido.");
    }

    public void cambiarMapaEnTiempoReal() {
        JFrame pf = (JFrame) SwingUtilities.getWindowAncestor(this);
        FileDialog fd = new FileDialog(pf, "SELECCIONAR NUEVA CARTOGRAFÍA GeoTIFF", FileDialog.LOAD);
        fd.setFile("*.tif;*.tiff");
        fd.setDirectory(System.getProperty("user.home"));
        fd.setVisible(true);
        if (fd.getFile() == null) return;

        String nuevaRuta = (fd.getDirectory() + fd.getFile()).replace("\\", "/");
        listaDeBlancos.clear(); listaDePuntos.clear(); listaDePoligonales.clear();
        modeloListaBlancos.clear(); modeloListaPoligonales.clear();

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
        revalidate(); repaint();
    }

    public void cambiarDesignacionEnTiempoReal() {
        JFrame pf = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(pf, "MODIFICAR SECUENCIA OPERATIVA", true);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLbl = new Font("Arial", Font.BOLD, 22);
        Font fCampo = new Font("Monospaced", Font.BOLD, 24);
        Font fBtn   = new Font("Arial", Font.BOLD, 20);

        JTextField txtPrefijo  = campo(designacionBlancoPrefijo,             fCampo);
        JTextField txtContador = campo(String.valueOf(designacionBlancoContador), fCampo);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lbl("PREFIJO (LETRAS):",  fLbl), gbc);
        gbc.gridx = 1;                panel.add(txtPrefijo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lbl("INICIO SECUENCIA:", fLbl), gbc);
        gbc.gridx = 1;                panel.add(txtContador, gbc);

        JButton btnGuardar  = boton("ACTUALIZAR", fBtn, new Dimension(0, 80), new Color(40, 100, 40), Color.WHITE);
        JButton btnCancelar = boton("ABORTAR",    fBtn, new Dimension(0, 80), new Color(100, 40, 40), Color.WHITE);
        JPanel panelBtns = new JPanel(new GridLayout(1, 2, 20, 0));
        panelBtns.setOpaque(false);
        panelBtns.add(btnGuardar); panelBtns.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 0, 0, 0);
        panel.add(panelBtns, gbc);

        btnGuardar.addActionListener(e -> {
            String prefijo = txtPrefijo.getText().trim().toUpperCase();
            if (!prefijo.matches("^[A-Z]+$")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "El prefijo solo admite letras A-Z.", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int cnt = Integer.parseInt(txtContador.getText().trim());
                if (cnt < 0) throw new NumberFormatException();
                designacionBlancoPrefijo  = prefijo;
                designacionBlancoContador = cnt;
                dialog.dispose();
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "El contador debe ser un número entero positivo.", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    @Override public String getPrefijo() { return designacionBlancoPrefijo; }
    @Override public int    getContador() { return designacionBlancoContador; }
    @Override public void   setPrefijo(String s) { designacionBlancoPrefijo  = s; }
    @Override public void   setContador(int i) { designacionBlancoContador = i; }
    public void incrementarContador() { designacionBlancoContador++; }

    public LinkedList<Blanco> getListaDeBlancos() { return listaDeBlancos; }
    public LinkedList<Blanco> getListaBlancos() { return listaDeBlancos; }
    public LinkedList<Punto> getListaDePuntos() { return listaDePuntos; }
    public LinkedList<Punto> getListaPuntos() { return listaDePuntos; }
    public LinkedList<Poligonal> getListaPoligonales() { return listaDePoligonales; }
    public PanelMapa getPanelMapa() { return panelMapa; }
    public JList<Blanco> getlistaUIBlancos() { return listaUIBlancos; }
    public JList<Poligonal> getlistaUIPoligonales() { return listaUIPoligonales; }
    public DefaultListModel<Blanco> getModeloListaBlancos() { return modeloListaBlancos; }
    public DefaultListModel<Poligonal> getModeloListaPoligonales(){ return modeloListaPoligonales; }
    public Map<Posicionable,Posicionable> getMapeoVertices() { return mapeoDeVertices; }
    public DialogFactory getDialogFactory() { return dialogFactory; }
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
        Blanco   selB = listaUIBlancos.getSelectedValue();
        Poligonal selP = listaUIPoligonales.getSelectedValue();
        boolean borrarB = false, borrarP = false;

        if (selB != null && selP != null) {
            Object[] ops = {"Eliminar Blanco", "Eliminar Linea/Punto", "Cancelar"};
            int elec = JOptionPane.showOptionDialog(this,
                    "Tiene seleccionado un Blanco y una Poligonal.\n¿Cuál desea eliminar?",
                    "Selección Múltiple", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, ops, ops[0]);
            if (elec == 0) borrarB = true;
            else if (elec == 1) borrarP = true;
            else return;
        } else if (selB != null) { borrarB = true; }
          else if (selP != null) { borrarP = true; }
          else {
            sonidos.clickError();
            JOptionPane.showMessageDialog(this, "Seleccione un elemento para eliminar.", "SISTEMA", JOptionPane.WARNING_MESSAGE);
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

    private void configurarHerramientasMapa() {
        panelMapa.getMapPane().setCursorTool(new CursorTool() {
            @Override public void onMousePressed(MapMouseEvent ev) {
                tooltipLabel.setVisible(true); actualizarTooltip(ev);
            }
            @Override public void onMouseDragged(MapMouseEvent ev) { actualizarTooltip(ev); }
            @Override public void onMouseReleased(MapMouseEvent ev) {
                tooltipLabel.setVisible(false);
                panelMapa.getMapPane().repaint();
                double x = ev.getWorldPos().getX(), y = ev.getWorldPos().getY();
                seleccionDeMarcado(new CoordenadasRectangulares(x, y, 0),
                        recortar(String.format("%.0f", x)), recortar(String.format("%.0f", y)));
            }
            private void actualizarTooltip(MapMouseEvent ev) {
                double x = ev.getWorldPos().getX(), y = ev.getWorldPos().getY();
                tooltipLabel.setText("DERECHAS: " + recortar(String.format("%.0f", x))
                        + " | ARRIBAS: " + recortar(String.format("%.0f", y)));
                panelMapa.getMapPane().repaint();
            }
        });
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
        gbc.fill = GridBagConstraints.BOTH;

        JLabel lblInfo = new JLabel("<html><center><font size='4'>COORDENADAS</font><br>"
                + "<font color='black' size='6'>DERECHAS: " + xV + " | ARRIBAS: " + yV + "</font></center></html>");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblInfo, gbc);

        Font fb = new Font("Arial", Font.BOLD, 18);
        JButton btnBlanco = boton("Marcar Blanco", fb, new Dimension(200, 80), null, null);
        JButton btnPunto  = boton("Marcar Punto",  fb, new Dimension(200, 80), null, null);

        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0; panel.add(btnBlanco, gbc);
        gbc.gridx = 1; panel.add(btnPunto, gbc);

        btnBlanco.addActionListener(e -> {
            dialog.dispose();
            dialogFactory.AgregarBlancoDialog(coord, nuevo -> {
                agregarBlanco(nuevo);
                if (nuevo.getNombre().equals(getPrefijo() + " " + getContador())) designacionBlancoContador++;
            });
        });
        btnPunto.addActionListener(e -> { dialog.dispose(); dialogFactory.AgregarPuntoDialog(coord, this::agregarPunto); });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void pedirArchivoAMostrar() {
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/LOGOBIAC.png"));
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(100, 120, Image.SCALE_SMOOTH);
        ImageIcon icono = new ImageIcon(imgEscalada);

        Color grisFondo    = new Color(25, 25, 25);
        Color grisOscuro   = new Color(45, 45, 45);
        Color verdeMilitar = new Color(60, 140, 60);
        Color azulTactico  = new Color(40, 70, 120);

        JFrame pf = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(pf, "SISTEMA - CONFIGURACIÓN DE CARTOGRAFÍA", true);
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

        String placeholder = "Ejemplo: C:/DATOS/OPERATIVOS/MAPA.TIF";
        JTextField txtRuta = new JTextField();
        FabricaComponentes.addPlaceholder(txtRuta, placeholder);
        txtRuta.setFont(new Font("Monospaced", Font.PLAIN, 15));
        txtRuta.setBackground(Color.BLACK); txtRuta.setForeground(Color.WHITE);
        txtRuta.setCaretColor(verdeMilitar); txtRuta.setPreferredSize(new Dimension(550, 40));
        txtRuta.setBorder(BorderFactory.createLineBorder(grisOscuro));

        JButton btnExaminar = boton("EXPLORAR...", new Font("Arial", Font.BOLD, 12),
                new Dimension(120, 40), verdeMilitar, Color.WHITE);

        gbc.gridy = 1; gbc.gridwidth = 2; gbc.gridx = 0; panel.add(txtRuta, gbc);
        gbc.gridx = 2; gbc.gridwidth = 1; panel.add(btnExaminar, gbc);

        JLabel lblDesig = new JLabel("DESIGNACIÓN DE SECUENCIA DE BLANCOS:");
        lblDesig.setForeground(new Color(180, 180, 180));
        lblDesig.setFont(new Font("Consolas", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; panel.add(lblDesig, gbc);

        JTextField txtDesig = new JTextField(designacionBlancoPrefijo + " " + designacionBlancoContador);
        txtDesig.setBackground(Color.BLACK); txtDesig.setForeground(new Color(0, 255, 0));
        txtDesig.setFont(new Font("Monospaced", Font.BOLD, 22));
        txtDesig.setHorizontalAlignment(SwingConstants.CENTER);
        txtDesig.setPreferredSize(new Dimension(300, 50));
        txtDesig.setBorder(BorderFactory.createLineBorder(verdeMilitar));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3; panel.add(txtDesig, gbc);

        JButton btnIniCart = boton("INICIALIZAR CARTOGRAFÍA", new Font("Arial", Font.BOLD, 15),
                new Dimension(280, 55), azulTactico, Color.WHITE);
        btnIniCart.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        JButton btnRutDef = boton("RUTA POR DEFECTO", new Font("Arial", Font.BOLD, 15),
                new Dimension(280, 55), grisOscuro, Color.WHITE);

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panelBtns.setOpaque(false);
        panelBtns.add(btnIniCart); panelBtns.add(btnRutDef);
        gbc.gridy = 4; gbc.gridwidth = 3; panel.add(panelBtns, gbc);

        btnExaminar.addActionListener(e -> {
            FileDialog fd = new FileDialog(dialog, "SELECCIONAR ARCHIVO CARTOGRÁFICO TIFF", FileDialog.LOAD);
            fd.setFile("*.tif;*.tiff");
            fd.setDirectory(System.getProperty("user.home"));
            fd.setVisible(true);
            if (fd.getFile() != null) {
                txtRuta.setText((fd.getDirectory() + fd.getFile()).replace("\\", "/"));
                txtRuta.setForeground(Color.WHITE);
            }
        });

        btnIniCart.addActionListener(e -> {
            String ruta = txtRuta.getText().trim();
            if (ruta.isEmpty() || ruta.equals(placeholder)) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "ERROR: RUTA NO VÁLIDA", "FALLO DE SISTEMA", JOptionPane.ERROR_MESSAGE, icono);
                return;
            }
            if (!ruta.toLowerCase().endsWith(".tif") && !ruta.toLowerCase().endsWith(".tiff")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "ERROR: EXTENSIÓN TIFF REQUERIDA", "FALLO DE SISTEMA", JOptionPane.ERROR_MESSAGE, icono);
                return;
            }
            String[] partes = txtDesig.getText().trim().split(" ");
            if (partes.length != 2 || !partes[0].matches("^[A-Z]+$")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "ERROR: FORMATO DE DESIGNACIÓN INVÁLIDO\n(DEBE SER: [LETRAS] [NÚMEROS])", "FALLO DE DATOS", JOptionPane.ERROR_MESSAGE, icono);
                return;
            }
            try {
                int cnt = Integer.parseInt(partes[1]);
                if (cnt < 1) throw new NumberFormatException();
                rutaArchivoMapa           = ruta.replace("\\", "/");
                designacionBlancoPrefijo  = partes[0].toUpperCase();
                designacionBlancoContador = cnt;
                dialog.dispose();
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "ERROR: EL CONTADOR DEBE SER UN ENTERO POSITIVO", "FALLO DE DATOS", JOptionPane.ERROR_MESSAGE, icono);
            }
        });

        btnRutDef.addActionListener(e -> {
            String[] partes = txtDesig.getText().trim().split(" ");
            if (partes.length != 2 || !partes[0].matches("^[A-Z]+$")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "ERROR: FORMATO DE DESIGNACIÓN INVÁLIDO\n(DEBE SER: [LETRAS] [NÚMEROS])", "FALLO DE DATOS", JOptionPane.ERROR_MESSAGE, icono);
                return;
            }
            try {
                int cnt = Integer.parseInt(partes[1]);
                if (cnt < 1) throw new NumberFormatException();
                designacionBlancoPrefijo  = partes[0].toUpperCase();
                designacionBlancoContador = cnt;
                JOptionPane.showMessageDialog(dialog,
                        "SISTEMA: CARGANDO RUTA PREESTABLECIDA\n" + rutaArchivoMapa
                        + "\nDESIGNACIÓN: " + designacionBlancoPrefijo + " " + designacionBlancoContador,
                        "AVISO TÁCTICO", JOptionPane.INFORMATION_MESSAGE, icono);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "ERROR: EL CONTADOR DEBE SER UN ENTERO POSITIVO", "FALLO DE DATOS", JOptionPane.ERROR_MESSAGE, icono);
            }
        });

        dialog.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                int r = JOptionPane.showConfirmDialog(dialog, "¿DESEA ABORTAR LA OPERACIÓN Y SALIR?",
                        "CONFIRMAR SALIDA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icono);
                if (r == JOptionPane.YES_OPTION) System.exit(0);
            }
        });

        dialog.setVisible(true);
    }

    private boolean enviar(String msg, String tituloError, String textoError) {
        if (main != null && main.getComunicacionIP() != null) {
            main.getComunicacionIP().enviarATodos(msg);
            return true;
        }
        sonidos.clickError();
        JOptionPane.showMessageDialog(this, textoError, tituloError, JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private static String recortar(String s) { return s.length() > 2 ? s.substring(2) : s; }
    
    private static String nvl(String s, String def) { return s == null ? def : s; }

    private JButton boton(String txt, Font f, Dimension d, Color bg, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(f);
        if (d  != null) b.setPreferredSize(d);
        if (bg != null) b.setBackground(bg);
        if (fg != null) b.setForeground(fg);
        b.setFocusPainted(false);
        return b;
    }

    private JLabel lbl(String txt, Font f) {
        JLabel l = new JLabel(txt);
        l.setForeground(Color.WHITE);
        l.setFont(f);
        return l;
    }

    private JTextField campo(String val, Font f) {
        JTextField tf = new JTextField(val);
        tf.setBackground(new Color(60, 60, 60));
        tf.setForeground(new Color(0, 255, 0));
        tf.setFont(f);
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setPreferredSize(new Dimension(250, 60));
        return tf;
    }
}