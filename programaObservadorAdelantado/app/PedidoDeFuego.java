package app;
import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import dominio.Blanco;
import dominio.PIF;
import harris.GestorPuertoHarris;
import interfaz.DatosBlanco;
import interfaz.MetodoAtaqueYTiroPanel;
import interfaz.CorreccionesPanel;
import interfaz.PanelMapa;

class PedidoDeFuego extends JPanel {

    private static final long serialVersionUID = 1L;

    private LinkedList<Blanco> listaDeBlancos;

    private CardLayout cardLayout;
    private JPanel pifCardPanel;
    private DatosBlanco datosDeBlancoPanel;
    private MetodoAtaqueYTiroPanel metodoYTiroPanel;
    private JPanel pifPanel;
    private JPanel panelMapaObsHolder;
    private DefaultListModel<PIF> modeloHistorial;
    private JList<PIF> listaHistorial;
    private ConsolaMensajes consolaMensajes;

    private GestorPuertoHarris gestorPuerto;

    private JButton btnDatos;
    private JButton btnMetodo;

    private final String[] ordenNavegable = {"datos", "metodoTiro"};
    private volatile int indiceActual = 0;
    private volatile boolean transicionEnCurso = false;

    private String idOAA;

    public void setGestorPuerto(GestorPuertoHarris gp) {
        this.gestorPuerto = gp;
    }

    public PedidoDeFuego(LinkedList<Blanco> listaDeBlancos, String idOAA) {
        this.idOAA = idOAA;
        this.listaDeBlancos = listaDeBlancos;

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        pifPanel = new JPanel(new BorderLayout());
        pifPanel.setBackground(Color.BLACK);
        pifPanel.setBorder(crearBordeTitulo(""));

        crearPanelDeBotones();
        crearCardLayout();
        crearBotonesDeNavegacion();
        crearPanelHistorial();

        inicializarAcciones();
        inicializarAccionesCorrecciones();

        add(pifPanel, BorderLayout.CENTER);
    }

    private void crearCardLayout() {

        cardLayout = new CardLayout();
        pifCardPanel = new JPanel(cardLayout);
        pifCardPanel.setBackground(Color.BLACK);

        datosDeBlancoPanel = new DatosBlanco();
        metodoYTiroPanel = new MetodoAtaqueYTiroPanel();

        panelMapaObsHolder = new JPanel(new BorderLayout());
        panelMapaObsHolder.setBackground(Color.BLACK);

        JPanel contSup = new JPanel(new BorderLayout());
        contSup.add(datosDeBlancoPanel, BorderLayout.CENTER);
        contSup.setPreferredSize(new Dimension(0, 240));
        contSup.setBackground(Color.BLACK);

        JPanel contTot = new JPanel(new BorderLayout());
        contTot.setBackground(Color.BLACK);
        contTot.add(contSup, BorderLayout.NORTH);
        contTot.add(panelMapaObsHolder, BorderLayout.CENTER);

        pifCardPanel.add(contTot, "datos");
        pifCardPanel.add(metodoYTiroPanel, "metodoTiro");

        cardLayout.show(pifCardPanel, "datos");
    }

    public void setMapaObservacion(PanelMapa mapaBase) {
        JPanel vistaSoloObs = mapaBase.crearVistaSoloObservacion();
        panelMapaObsHolder.removeAll();
        panelMapaObsHolder.add(vistaSoloObs, BorderLayout.CENTER);
        panelMapaObsHolder.revalidate();
        panelMapaObsHolder.repaint();
    }

    private void crearPanelDeBotones() {

        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBackground(Color.BLACK);

        btnDatos = new JButton("DATOS DEL BLANCO");
        btnMetodo = new JButton("MÉTODO Y TIRO");

        for (JButton b : new JButton[]{btnDatos, btnMetodo}) {
            b.setBackground(new Color(60, 60, 60));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        }

        panel.add(btnDatos);
        panel.add(btnMetodo);

        pifPanel.add(panel, BorderLayout.NORTH);
    }

    private void crearPanelHistorial() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setBorder(crearBordeTitulo("HISTORIAL DE PIFs"));
        panel.setPreferredSize(new Dimension(300, 0));

        modeloHistorial = new DefaultListModel<>();
        listaHistorial = new JList<>(modeloHistorial);
        listaHistorial.setBackground(new Color(25, 25, 25));
        listaHistorial.setForeground(Color.WHITE);

        listaHistorial.setCellRenderer(new DefaultListCellRenderer() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -6684167244292177832L;

			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                PIF pif = (PIF) value;
                Blanco b = pif.getBlanco();

                String naturaleza = b != null ? b.getNaturaleza().toUpperCase() : "";
                String fecha = pif.getFechaHora().toString().replace('T', ' ');

                String texto = String.format("<html>"+ "<div style='margin:2px 0; line-height:130%%;'>"+ "<span style='font-size:12px; color:white;'>%s</span><br>"
                  + "<span style='font-size:11px;'>%s</span>"+ "</div></html>",fecha,naturaleza);
                label.setText(texto);

                Color colorTexto;

                if (naturaleza.contains("HOSTIL")) colorTexto = new Color(255,128,128);
                else if (naturaleza.contains("ASUMIDO ENEMIGO")) colorTexto = new Color(255,128,128);
                else if (naturaleza.contains("ALIADO")) colorTexto = new Color(128,224,255);
                else if (naturaleza.contains("ASUMIDO AMIGO")) colorTexto = new Color(128,224,255);
                else if (naturaleza.contains("NEUTRO")) colorTexto = new Color(170,255,170);
                else if (naturaleza.contains("DESCONOCIDO")) colorTexto = new Color(255,255,128);
                else if (naturaleza.contains("PENDIENTE")) colorTexto = new Color(255,255,128);
                else colorTexto = Color.LIGHT_GRAY;

                label.setForeground(colorTexto);
                label.setBackground(isSelected ? new Color(40, 80, 120) : new Color(20, 20, 20));
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(80, 80, 80)),
                    BorderFactory.createEmptyBorder(4, 6, 4, 6)
                ));

                return label;
            }
        });
        
        JScrollPane scroll = new JScrollPane(listaHistorial);
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnVer = new JButton("Ver PIF");
        btnVer.setBackground(new Color(60, 60, 60));
        btnVer.setForeground(Color.WHITE);
        btnVer.addActionListener(e -> mostrarPIFSeleccionado());

        JPanel cont = new JPanel(new BorderLayout());
        cont.setBackground(Color.BLACK);
        cont.add(btnVer, BorderLayout.NORTH);

        consolaMensajes = new ConsolaMensajes();
        cont.add(consolaMensajes, BorderLayout.SOUTH);

        panel.add(cont, BorderLayout.SOUTH);

        add(panel, BorderLayout.EAST);
    }

    private void inicializarAcciones() {

        btnDatos.addActionListener(e -> {
            indiceActual = 0;
            cardLayout.show(pifCardPanel, "datos");
        });

        btnMetodo.addActionListener(e -> {
            indiceActual = 1;
            cardLayout.show(pifCardPanel, "metodoTiro");
        });

        metodoYTiroPanel.setEnviarListener(() -> registrarNuevoPIF());
    }

    private void inicializarAccionesCorrecciones() {

        CorreccionesPanel corr = metodoYTiroPanel.getCorreccionesPanel();

        corr.getBtnVolver().addActionListener(e ->
                metodoYTiroPanel.mostrarPanelPrincipal()
        );

        corr.getBtnNuevoPIF().addActionListener(e -> {
            consolaMensajes.agregarMensaje("[INFO] Nuevo PIF solicitado");
            volverASituacionTactica();
        });

        corr.getBtnFin().addActionListener(e -> {
            String msg = "FIN_MISION|BLANCO=" +
                    datosDeBlancoPanel.getBlancoActual().getNombre();

            if (gestorPuerto != null && gestorPuerto.estaAbierto())
                gestorPuerto.enviar(msg);

            consolaMensajes.agregarMensaje("[TX] " + msg);
        });

        corr.getBtnEnviar().addActionListener(e -> enviarCorreccion());
    }

    private void enviarCorreccion() {

        CorreccionesPanel corr = metodoYTiroPanel.getCorreccionesPanel();

        String dir = (String) corr.getCbDireccion().getSelectedItem();
        String alc = (String) corr.getCbAlcance().getSelectedItem();
        String alt = (String) corr.getCbAltura().getSelectedItem();

        int vDir = Integer.parseInt(corr.getTxtDirValor().getText());
        int vAlc = Integer.parseInt(corr.getTxtAlcValor().getText());
        int vAlt = Integer.parseInt(corr.getTxtAltValor().getText());

        String nombre = datosDeBlancoPanel.getBlancoActual().getNombre();

        String msg = "CORRECCION"
                + "|BLANCO=" + nombre
                + "|DIR=" + dir + ":" + vDir
                + "|ALC=" + alc + ":" + vAlc
                + "|ALT=" + alt + ":" + vAlt;

        if (gestorPuerto != null && gestorPuerto.estaAbierto())
            gestorPuerto.enviar(msg);

        consolaMensajes.agregarMensaje("[TX] " + msg);

        corr.getLblUltima().setText("ULTIMA CORRECCIÓN: " +
                dir + " " + vDir + " / " +
                alc + " " + vAlc + " / " +
                alt + " " + vAlt);
    }

    private void volverASituacionTactica() {

        Container parent = getParent();
        while (parent != null && !(parent instanceof ObservadorAdelantado))
            parent = parent.getParent();

        if (parent instanceof ObservadorAdelantado obs)
            obs.mostrarPanel("SITUACION");
    }

    private void registrarNuevoPIF() {

        Blanco b = datosDeBlancoPanel.getBlancoActual();
        if (b == null) {
            JOptionPane.showMessageDialog(this,"Seleccione un blanco.","Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MetodoAtaqueYTiroPanel mt = metodoYTiroPanel;

        int rondas = mt.isRafaga() ? 5 : 1;

        PIF nuevo = new PIF(
                null, null, idOAA, b,
                "EPSG:9265",
                b.getNaturaleza(),
                mt.getGranada() + "-" + mt.getEspoleta(),
                Integer.parseInt(mt.getPiezas()),
                rondas,
                mt.getGranada(),
                mt.getEspoleta(),
                "Carga estándar"
        );

        nuevo.setModoFuego(mt.isCuandoListo() ? "CUANDO LISTO" : "A MI ORDEN");
        nuevo.setFuegoContinuo(mt.isFgoSi());
        nuevo.setTes(mt.isTesSi());
        nuevo.setTotSegundos(mt.getTot());
        nuevo.setSeccion(mt.getSeccion());

        modeloHistorial.addElement(nuevo);

        if (gestorPuerto != null && gestorPuerto.estaAbierto()) {

            String msg = "PIF"
                    + "|BLANCO=" + b.getNombre()
                    + "|NAT=" + b.getNaturaleza()
                    + "|PIEZAS=" + nuevo.getPiezas()
                    + "|RONDAS=" + nuevo.getRondas()
                    + "|MODO=" + nuevo.getModoFuego()
                    + "|MUNI=" + nuevo.getTipoMunicion() + "-" + nuevo.getEspoleta()
                    + "|TOT=" + nuevo.getTotSegundos()
                    + "|SECCION=" + nuevo.getSeccion();

            gestorPuerto.enviar(msg);
            consolaMensajes.agregarMensaje("[TX] " + msg);
        }

        JOptionPane.showMessageDialog(this,"PIF registrado correctamente.","Confirmado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarPIFSeleccionado() {
        PIF p = listaHistorial.getSelectedValue();
        if (p == null) return;

        Blanco b = p.getBlanco();

        // ===== CUERPO DEL DIALOGO =====
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.BLACK);
        contenido.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        Font fTitulo = new Font("Arial", Font.BOLD, 18);
        Font fTexto = new Font("Consolas", Font.PLAIN, 15);

        // ---------- DATOS DEL BLANCO ----------
        JPanel panelBlanco = new JPanel();
        panelBlanco.setLayout(new BoxLayout(panelBlanco, BoxLayout.Y_AXIS));
        panelBlanco.setBackground(Color.BLACK);
        panelBlanco.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "DATOS DEL BLANCO",
                0, 0, fTitulo, Color.WHITE
            )
        );

        panelBlanco.add(crearLinea("Nombre: ", b.getNombre(), fTexto));
        panelBlanco.add(crearLinea("Naturaleza: ", b.getNaturaleza(), fTexto));
        panelBlanco.add(crearLinea("Coordenadas: ", b.getCoordenadas().toString(), fTexto));
        panelBlanco.add(crearLinea("Fecha: ", b.getFechaDeActualizacion(), fTexto));
        panelBlanco.add(crearLinea("Situación: ", String.valueOf(b.getSituacionMovimiento()), fTexto));
        panelBlanco.add(crearLinea("Orientación: ", b.getOrientacion() + "°", fTexto));
        panelBlanco.add(crearLinea("Info adicional: ", b.getInformacionAdicional(), fTexto));
        panelBlanco.add(crearLinea("SIM ID: ", b.getSimID(), fTexto));

        contenido.add(panelBlanco);
        contenido.add(Box.createVerticalStrut(15));

        // ---------- DATOS DE MÉTODO Y TIRO ----------
        JPanel panelMetodo = new JPanel();
        panelMetodo.setLayout(new BoxLayout(panelMetodo, BoxLayout.Y_AXIS));
        panelMetodo.setBackground(Color.BLACK);
        panelMetodo.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "MÉTODO DE ATAQUE / TIRO Y CONTROL",
                0, 0, fTitulo, Color.WHITE
            )
        );

        panelMetodo.add(crearLinea("Carga: ", p.getCarga(), fTexto));
        panelMetodo.add(crearLinea("Espoleta: ", p.getEspoleta(), fTexto));
        panelMetodo.add(crearLinea("Piezas: ", String.valueOf(p.getPiezas()), fTexto));
        panelMetodo.add(crearLinea("Rondas: ", String.valueOf(p.getRondas()), fTexto));
        panelMetodo.add(crearLinea("Sección: ", p.getSeccion(), fTexto));
        panelMetodo.add(crearLinea("TOT: ", p.getTotSegundos(), fTexto));
        panelMetodo.add(crearLinea("Modo fuego: ", p.getModoFuego(), fTexto));
        panelMetodo.add(crearLinea("FGO continuo: ", p.isFuegoContinuo() ? "Sí" : "No", fTexto));
        panelMetodo.add(crearLinea("TES: ", p.isTes() ? "Sí" : "No", fTexto));

        contenido.add(panelMetodo);

        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalle del PIF");
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.getContentPane().add(new JScrollPane(contenido));
        dialogo.setSize(500, 650);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    // ===== FACTORY DE LÍNEAS =====
    private JPanel crearLinea(String etiqueta, String valor, Font font) {
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linea.setBackground(Color.BLACK);

        JLabel l1 = new JLabel(etiqueta);
        l1.setForeground(Color.WHITE);
        l1.setFont(font);

        JLabel l2 = new JLabel(valor);
        l2.setForeground(new Color(180, 255, 180));
        l2.setFont(font);

        linea.add(l1);
        linea.add(l2);

        return linea;
    }

    private void crearBotonesDeNavegacion() {

        JButton prev = new JButton("<");
        prev.setBackground(new Color(28,122,33));
        prev.setForeground(Color.WHITE);

        JButton next = new JButton(">");
        next.setBackground(new Color(28,122,33));
        next.setForeground(Color.WHITE);

        prev.addActionListener(e -> moverSeguro(-1));
        next.addActionListener(e -> moverSeguro(+1));

        JPanel navegacion = new JPanel(new BorderLayout());
        navegacion.setBackground(Color.BLACK);

        navegacion.add(prev, BorderLayout.WEST);
        navegacion.add(pifCardPanel, BorderLayout.CENTER);
        navegacion.add(next, BorderLayout.EAST);

        // AGREGARLO SOLO UNA VEZ
        pifPanel.add(navegacion, BorderLayout.CENTER);
    }

    private void moverSeguro(int delta) {
        if (transicionEnCurso) return;

        transicionEnCurso = true;

        int nuevo = indiceActual + delta;
        if (nuevo >= 0 && nuevo < ordenNavegable.length) {
            indiceActual = nuevo;
            cardLayout.show(pifCardPanel, ordenNavegable[nuevo]);
        }

        Timer t = new Timer(160, e -> transicionEnCurso = false);
        t.setRepeats(false);
        t.start();
    }

    private TitledBorder crearBordeTitulo(String titulo) {
        TitledBorder b = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                titulo,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 20),
                Color.WHITE
        );
        return b;
    }

    public ConsolaMensajes getConsolaMensajes() {
        return consolaMensajes;
    }

    public DatosBlanco getDatosDeBlancoPanel() {
        return datosDeBlancoPanel;
    }

    public LinkedList<Blanco> getListaDeBlancos() {
        return listaDeBlancos;
    }
}
