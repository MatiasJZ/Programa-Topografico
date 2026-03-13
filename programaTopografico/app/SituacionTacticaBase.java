package app;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import dominio.Blanco;
import dominio.CoordenadasRectangulares;
import gestores.GestorCoordenadas;
import gestores.GestorEnlaceOperativo;
import gestores.GestorSonido;
import interfaces.DesignacionProvider;
import interfaces.DialogFactory;
import paneles.PanelMapa;
import util.Configuracion;
import util.FabricaComponentes;
import util.FabricaDialogosTacticos;

/**
 * SituacionTacticaBase — Superclase abstracta común a los dos paneles tácticos del sistema SARGO.
 *
 * <p>Centraliza todo lo que comparten {@link SituacionTacticaTopografica} (SAB) y
 * {@link SituacionTacticaOA} (OA), eliminando ~400 líneas duplicadas:</p>
 * <ul>
 *   <li>Estado compartido: blancos, mapa, designación, sonidos, dialogFactory.</li>
 *   <li>{@link #pedirArchivoAMostrar()} — diálogo de carga de cartografía TIFF al inicio.</li>
 *   <li>{@link #cambiarDesignacionEnTiempoReal()} — diálogo de modificación de secuencia.</li>
 *   <li>{@link #configurarHerramientasMapa()} — cursor tool con tooltip de coordenadas.</li>
 *   <li>{@link #cambiarMapaEnTiempoReal()} — recarga de cartografía en caliente.</li>
 *   <li>{@link #agregarBlanco(Blanco)}, {@link #actualizarBlanco(Blanco)} — gestión de blancos.</li>
 *   <li>{@link #serializarBlanco(Blanco)} — formato de protocolo BLANCO|... compartido.</li>
 *   <li>Helpers de UI: {@link #boton}, {@link #lbl}, {@link #campo}, {@link #recortar}, {@link #nvl}.</li>
 * </ul>
 *
 * <p><b>Contrato para subclases (Template Method Pattern):</b></p>
 * <ul>
 *   <li>{@link #getComunicacion()} — devuelve el {@link GestorEnlaceOperativo} propio de cada módulo.</li>
 *   <li>{@link #onClickEnMapa(CoordenadasRectangulares, String, String)} — reacción al click en el mapa
 *       (OA solo marca Blanco; SAB ofrece Blanco + Punto).</li>
 *   <li>{@link #reemplazarMapaEnLayout(PanelMapa)} — reemplaza el componente mapa en el layout
 *       concreto de cada subclase.</li>
 *   <li>{@link #limpiarDatosMapa()} — limpia modelos adicionales al cambiar de cartografía
 *       (SAB también limpia puntos y poligonales). Por defecto no hace nada.</li>
 * </ul>
 *
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public abstract class SituacionTacticaBase extends JPanel implements DesignacionProvider {

    private static final long serialVersionUID = 1L;

    protected final LinkedList<Blanco> listaDeBlancos;
    protected DefaultListModel<Blanco> modeloListaBlancos = new DefaultListModel<>();
    protected JList<Blanco> listaUIBlancos;
    protected PanelMapa panelMapa;
    protected String rutaArchivoMapa;
    protected String designacionBlancoPrefijo;
    protected int designacionBlancoContador;
    protected JLabel tooltipLabel;
    protected final GestorSonido sonidos;
    protected final DialogFactory dialogFactory;

    protected SituacionTacticaBase(LinkedList<Blanco> listaDeBlancos) {
        this.listaDeBlancos = listaDeBlancos;
        this.sonidos = new GestorSonido();
        this.rutaArchivoMapa = Configuracion.get("ruta_mapa_defecto", "C:/Mapas/default.tif");
        this.designacionBlancoPrefijo = Configuracion.get("prefijo_blancos", "AF");
        this.designacionBlancoContador= Configuracion.getInt("contador_blancos_inicio", 6400);
        this.dialogFactory = new FabricaDialogosTacticos(this, sonidos);
    }

    //  Template Method Pattern
    protected abstract GestorEnlaceOperativo getComunicacion();

    protected abstract void onClickEnMapa(CoordenadasRectangulares coord, String xV, String yV);

    protected abstract void reemplazarMapaEnLayout(PanelMapa nuevoMapa);

    protected void limpiarDatosMapa() {}

    public void agregarBlanco(Blanco b) {
        if (b == null) return;
        if (!listaDeBlancos.contains(b))
        	listaDeBlancos.add(b);
        if (!modeloListaBlancos.contains(b)) 
        	modeloListaBlancos.addElement(b);
        panelMapa.agregarBlanco(b);
        listaUIBlancos.repaint();
    }

    public void actualizarBlanco(Blanco b) {
        if (b == null) return;
        int idx = modeloListaBlancos.indexOf(b);
        if (idx >= 0) 
        	modeloListaBlancos.set(idx, b);
        listaUIBlancos.repaint();
        panelMapa.eliminarBlanco(b);
        panelMapa.agregarBlanco(b);
    }

    public void enviarBlanco(Blanco b) {
        if (b == null) return;
        GestorEnlaceOperativo com = getComunicacion();
        if (com == null) {
            sonidos.clickError();
            JOptionPane.showMessageDialog(this,
                    "ERROR DE ENLACE: No hay conexión con el módulo de comunicaciones.",
                    "FALLO DE TRANSMISIÓN", JOptionPane.ERROR_MESSAGE);
            return;
        }
        com.enviarATodos(serializarBlanco(b));
    }

    protected void configurarHerramientasMapa() {
        panelMapa.getMapPane().setCursorTool(new CursorTool() {
            @Override public void onMousePressed(MapMouseEvent ev) {
                tooltipLabel.setVisible(true);
                actualizarTooltip(ev);
            }
            
            @Override public void onMouseDragged(MapMouseEvent ev) {
                actualizarTooltip(ev);
            }
            
            @Override public void onMouseReleased(MapMouseEvent ev) {
                tooltipLabel.setVisible(false);
                panelMapa.getMapPane().repaint();
                
                double x = ev.getWorldPos().getX();
                double y = ev.getWorldPos().getY();
                
                String xVisual = GestorCoordenadas.aVisualX(x, 2);
                String yVisual = GestorCoordenadas.aVisualY(y, 2);
                
                onClickEnMapa(
                        new CoordenadasRectangulares(x, y, 0),
                        xVisual,
                        yVisual
                );
            }
            
            private void actualizarTooltip(MapMouseEvent ev) {
                double x = ev.getWorldPos().getX();
                double y = ev.getWorldPos().getY();
                
                String xVisual = GestorCoordenadas.aVisualX(x, 2);
                String yVisual = GestorCoordenadas.aVisualY(y, 2);
                
                tooltipLabel.setText("DERECHAS: " + xVisual + " | ARRIBAS: " + yVisual);
                panelMapa.getMapPane().repaint();
            }
        });
    }
    
    public void cambiarMapaEnTiempoReal() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        FileDialog fd = new FileDialog(parent, "SELECCIONAR NUEVA CARTOGRAFÍA GeoTIFF", FileDialog.LOAD);
        fd.setFile("*.tif;*.tiff");
        fd.setDirectory(System.getProperty("user.home"));
        fd.setVisible(true);
        
        if (fd.getFile() == null) 
        	return;

        String nuevaRuta = (fd.getDirectory() + fd.getFile()).replace("\\", "/");

        listaDeBlancos.clear();
        modeloListaBlancos.clear();
        limpiarDatosMapa();

        panelMapa.dispose();
        PanelMapa nuevoMapa = new PanelMapa(nuevaRuta);
        nuevoMapa.getMapPane().add(tooltipLabel);
        this.panelMapa = nuevoMapa;

        reemplazarMapaEnLayout(nuevoMapa);
        configurarHerramientasMapa();
        revalidate();
        repaint();
    }

    public void cambiarDesignacionEnTiempoReal() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "MODIFICAR SECUENCIA OPERATIVA", true);
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
        Font fBtn = new Font("Arial", Font.BOLD, 20);

        JTextField txtPrefijo  = campo(designacionBlancoPrefijo, fCampo);
        JTextField txtContador = campo(String.valueOf(designacionBlancoContador), fCampo);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lbl("PREFIJO (LETRAS):",  fLbl), gbc);
        gbc.gridx = 1; panel.add(txtPrefijo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lbl("INICIO SECUENCIA:", fLbl), gbc);
        gbc.gridx = 1; panel.add(txtContador, gbc);

        JButton btnGuardar = boton("ACTUALIZAR", fBtn, new Dimension(0, 80), new Color(40, 100, 40),  Color.WHITE);
        JButton btnCancelar = boton("ABORTAR", fBtn, new Dimension(0, 80), new Color(100, 40, 40), Color.WHITE);

        JPanel panelBtns = new JPanel(new GridLayout(1, 2, 20, 0));
        panelBtns.setOpaque(false);
        panelBtns.add(btnGuardar);
        panelBtns.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 0, 0, 0);
        panel.add(panelBtns, gbc);

        btnGuardar.addActionListener(e -> {
            String prefijo = txtPrefijo.getText().trim().toUpperCase();
            if (!prefijo.matches("^[A-Z]+$")) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog,
                        "El prefijo solo admite letras A-Z.", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(dialog,
                        "El contador debe ser un número entero positivo.", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    protected void pedirArchivoAMostrar() {
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

        // Fila 0 — etiqueta ruta
        JLabel lblRuta = lbl("CARGA DE ARCHIVO RASTER (TIFF):", new Font("Consolas", Font.BOLD, 16));
        lblRuta.setForeground(new Color(180, 180, 180));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        panel.add(lblRuta, gbc);

        // Fila 1 — campo ruta + botón examinar
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

        // Fila 2 — etiqueta designación
        JLabel lblDesig = lbl("DESIGNACIÓN DE SECUENCIA DE BLANCOS:", new Font("Consolas", Font.BOLD, 16));
        lblDesig.setForeground(new Color(180, 180, 180));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        panel.add(lblDesig, gbc);

        // Fila 3 — campo designación
        JTextField txtDesig = new JTextField(designacionBlancoPrefijo + " " + designacionBlancoContador);
        txtDesig.setBackground(Color.BLACK); txtDesig.setForeground(new Color(0, 255, 0));
        txtDesig.setFont(new Font("Monospaced", Font.BOLD, 22));
        txtDesig.setHorizontalAlignment(SwingConstants.CENTER);
        txtDesig.setPreferredSize(new Dimension(300, 50));
        txtDesig.setBorder(BorderFactory.createLineBorder(verdeMilitar));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        panel.add(txtDesig, gbc);

        // Fila 4 — botones principales
        JButton btnIniCart = boton("INICIALIZAR CARTOGRAFÍA", new Font("Arial", Font.BOLD, 15),
                new Dimension(280, 55), azulTactico, Color.WHITE);
        btnIniCart.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        btnIniCart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnRutDef = boton("RUTA POR DEFECTO", new Font("Arial", Font.BOLD, 15),
                new Dimension(280, 55), grisOscuro, Color.WHITE);
        btnRutDef.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panelBtns.setOpaque(false);
        panelBtns.add(btnIniCart); panelBtns.add(btnRutDef);
        gbc.gridy = 4; gbc.gridwidth = 3;
        panel.add(panelBtns, gbc);
        
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
                errorDialog(dialog, icono, "ERROR: RUTA NO VÁLIDA", "FALLO DE SISTEMA"); return;
            }
            if (!ruta.toLowerCase().endsWith(".tif") && !ruta.toLowerCase().endsWith(".tiff")) {
                errorDialog(dialog, icono, "ERROR: EXTENSIÓN TIFF REQUERIDA", "FALLO DE SISTEMA"); return;
            }
            String[] partes = txtDesig.getText().trim().split(" ");
            if (partes.length != 2 || !partes[0].matches("^[A-Z]+$")) {
                errorDialog(dialog, icono, "ERROR: FORMATO DE DESIGNACIÓN INVÁLIDO\n(DEBE SER: [LETRAS] [NÚMEROS])", "FALLO DE DATOS"); return;
            }
            try {
                int cnt = Integer.parseInt(partes[1]);
                if (cnt < 1) throw new NumberFormatException();
                rutaArchivoMapa           = ruta.replace("\\", "/");
                designacionBlancoPrefijo  = partes[0].toUpperCase();
                designacionBlancoContador = cnt;
                dialog.dispose();
            } catch (NumberFormatException ex) {
                errorDialog(dialog, icono, "ERROR: EL CONTADOR DEBE SER UN ENTERO POSITIVO", "FALLO DE DATOS");
            }
        });

        btnRutDef.addActionListener(e -> {
            String[] partes = txtDesig.getText().trim().split(" ");
            if (partes.length != 2 || !partes[0].matches("^[A-Z]+$")) {
                errorDialog(dialog, icono, "ERROR: FORMATO DE DESIGNACIÓN INVÁLIDO\n(DEBE SER: [LETRAS] [NÚMEROS])", "FALLO DE DATOS"); return;
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
                errorDialog(dialog, icono, "ERROR: EL CONTADOR DEBE SER UN ENTERO POSITIVO", "FALLO DE DATOS");
            }
        });

        dialog.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                int r = JOptionPane.showConfirmDialog(dialog,
                        "¿DESEA ABORTAR LA OPERACIÓN Y SALIR?", "CONFIRMAR SALIDA",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icono);
                if (r == JOptionPane.YES_OPTION) System.exit(0);
            }
        });

        dialog.setVisible(true);
    }

    @Override public String getPrefijo() { return designacionBlancoPrefijo; }
    @Override public int  getContador() { return designacionBlancoContador; }
    @Override public void setPrefijo(String s) { designacionBlancoPrefijo  = s; }
    @Override public void setContador(int i) { designacionBlancoContador = i; }
    public void incrementarContador() { designacionBlancoContador++; }
    public LinkedList<Blanco> getListaBlancos() { return listaDeBlancos; }
    public JList<Blanco> getlistaUIBlancos() { return listaUIBlancos; }
    public DefaultListModel<Blanco> getModeloListaBlancos()   { return modeloListaBlancos; }
    public PanelMapa getPanelMapa() { return panelMapa; }
    public DialogFactory getDialogFactory() { return dialogFactory; }

    public double calcularAzimutEnMils(double x1, double y1, double x2, double y2) {
        double grados = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        if (grados < 0) grados += 360;
        return Math.round(grados * (6400.0 / 360.0));
    }

    protected String serializarBlanco(Blanco b) {
        String info = b.getInformacionAdicional();
        if (info == null || info.trim().isEmpty()) info = "Sin Informacion Adicional";
        return "BLANCO|NOMBRE=" + b.getNombre()
                + "|NAT="        + b.getNaturaleza()
                + "|FECHA="      + b.getFechaDeActualizacion()
                + "|ORI="        + (int) b.getOrientacion()
                + "|ENTIDAD="    + nvl(b.getUltEntidad(),    "DESCONOCIDO")
                + "|AFILIACION=" + nvl(b.getUltAfiliacion(), "DESCONOCIDO")
                + "|ECHELON="    + nvl(b.getUltEchelon(),    "Por Defecto")
                + "|INFO="       + info
                + "|SIMID="      + b.getSimID()
                + "|SIT="        + b.getSituacionMovimiento()
                + "|X="          + b.getCoordenadas().getX()
                + "|Y="          + b.getCoordenadas().getY()
                + "|Z="          + b.getCoordenadas().getCota();
    }

    protected JButton boton(String txt, Font f, Dimension d, Color bg, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(f);
        if (d  != null) b.setPreferredSize(d);
        if (bg != null) b.setBackground(bg);
        if (fg != null) b.setForeground(fg);
        b.setFocusPainted(false);
        return b;
    }

    protected JLabel lbl(String txt, Font f) {
        JLabel l = new JLabel(txt);
        l.setForeground(Color.WHITE);
        l.setFont(f);
        return l;
    }

    protected JTextField campo(String val, Font f) {
        JTextField tf = new JTextField(val);
        tf.setBackground(new Color(60, 60, 60));
        tf.setForeground(new Color(0, 255, 0));
        tf.setFont(f);
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setPreferredSize(new Dimension(250, 60));
        return tf;
    }
    protected static String recortar(String s) { return s.length() > 2 ? s.substring(2) : s; }

    protected static String nvl(String s, String def) { return s != null ? s : def; }

    private void errorDialog(JDialog dialog, ImageIcon icono, String msg, String titulo) {
        sonidos.clickError();
        JOptionPane.showMessageDialog(dialog, msg, titulo, JOptionPane.ERROR_MESSAGE, icono);
    }
    
}