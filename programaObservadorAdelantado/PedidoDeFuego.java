import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.TitledBorder;

class PedidoDeFuego extends JPanel {

    private static final long serialVersionUID = 1L;

    private LinkedList<Blanco> listaDeBlancos;

    private CardLayout cardLayout;
    private JPanel pifCardPanel;
    private DatosBlanco datosDeBlancoPanel;
    private MetodoAtaqueYTiroPanel metodoYTiroPanel;
    private JPanel pifPanel;
    private JPanel panelMapaObsHolder;
    private JPanel panelSuperior;
    private JPanel panelInferior;
    private DefaultListModel<PIF> modeloHistorial;
    private JList<PIF> listaHistorial;
    // botones superiores
    private JButton btnDatos;
    private JButton btnMetodo;
    private final String[] ordenNavegable = {"datos", "metodoTiro"};
    private volatile int indiceActual = 0;
    private volatile boolean transicionEnCurso = false;
    private String idOAA;

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
    }

    private void crearCardLayout() {
        cardLayout = new CardLayout();
        pifCardPanel = new JPanel(cardLayout);
        pifCardPanel.setBackground(Color.BLACK);

        datosDeBlancoPanel = new DatosBlanco();
        metodoYTiroPanel = new MetodoAtaqueYTiroPanel();

        // parte superior con datos del blanco
        panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.BLACK);
        panelSuperior.add(datosDeBlancoPanel, BorderLayout.CENTER);
        panelSuperior.setPreferredSize(new Dimension(0, 240));

        // visor de mapa
        panelMapaObsHolder = new JPanel(new BorderLayout());
        panelMapaObsHolder.setBackground(Color.BLACK);

        panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(Color.BLACK);
        panelInferior.add(panelMapaObsHolder, BorderLayout.CENTER);

        // contenedor vertical
        JPanel contenedorVertical = new JPanel(new BorderLayout());
        contenedorVertical.setBackground(Color.BLACK);
        contenedorVertical.add(panelSuperior, BorderLayout.NORTH);
        contenedorVertical.add(panelInferior, BorderLayout.CENTER);

        // agregar cards
        pifCardPanel.add(contenedorVertical, "datos");
        pifCardPanel.add(metodoYTiroPanel, "metodoTiro");

        cardLayout.show(pifCardPanel, "datos");
        resaltarSegunCard("datos");

        pifPanel.add(pifCardPanel, BorderLayout.CENTER);
        add(pifPanel, BorderLayout.CENTER);
    }

    public void setMapaObservacion(PanelMapa mapaBase) {
        JPanel vistaSoloObs = mapaBase.crearVistaSoloObservacion();
        panelMapaObsHolder.removeAll();
        panelMapaObsHolder.add(vistaSoloObs, BorderLayout.CENTER);
        panelMapaObsHolder.revalidate();
        panelMapaObsHolder.repaint();
    }

    private void crearPanelDeBotones() {
        JPanel botonesPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        botonesPanel.setBackground(Color.BLACK);

        btnDatos = new JButton("DATOS DEL BLANCO");
        btnMetodo = new JButton("MÉTODO Y TIRO");

        for (JButton b : new JButton[]{btnDatos, btnMetodo}) {
            b.setBackground(new Color(60, 60, 60));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        }

        botonesPanel.add(btnDatos);
        botonesPanel.add(btnMetodo);
        pifPanel.add(botonesPanel, BorderLayout.NORTH);
    }

    private void crearPanelHistorial() {
        JPanel historialPanel = new JPanel(new BorderLayout());
        historialPanel.setBackground(Color.BLACK);
        historialPanel.setBorder(crearBordeTitulo("HISTORIAL DE PIFs"));
        historialPanel.setPreferredSize(new Dimension(300, 0));

        modeloHistorial = new DefaultListModel<>();
        listaHistorial = new JList<>(modeloHistorial);
        listaHistorial.setBackground(new Color(25, 25, 25));
        listaHistorial.setForeground(Color.WHITE);
        listaHistorial.setSelectionBackground(new Color(70, 70, 70));
        listaHistorial.setFont(new Font("Consolas", Font.PLAIN, 14));

        listaHistorial.setCellRenderer(new DefaultListCellRenderer() {
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
        scroll.setBorder(BorderFactory.createEmptyBorder());
        historialPanel.add(scroll, BorderLayout.CENTER);

        JButton btnVer = new JButton("Ver PIF");
        btnVer.setBackground(new Color(60, 60, 60));
        btnVer.setForeground(Color.WHITE);
        btnVer.setFocusPainted(false);
        btnVer.addActionListener(e -> mostrarPIFSeleccionado());
        historialPanel.add(btnVer, BorderLayout.SOUTH);

        add(historialPanel, BorderLayout.EAST);
    }

    private void inicializarAcciones() {
        btnDatos.addActionListener(e -> mostrarPanelSeguro("datos", 0));
        btnMetodo.addActionListener(e -> mostrarPanelSeguro("metodoTiro", 1));
        // accion enviar
        metodoYTiroPanel.setEnviarListener(() -> registrarNuevoPIF());
    }

    private void registrarNuevoPIF() {

        Blanco b = datosDeBlancoPanel.getBlancoActual();
        if (b == null) {
            JOptionPane.showMessageDialog(this, "No hay blanco seleccionado.","Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        MetodoAtaqueYTiroPanel mt = metodoYTiroPanel;
        int rondas = mt.isRafaga() ? 5 : 1;

        PIF nuevo = new PIF(null, null,idOAA,b,"EPSG:9265",b.getNaturaleza(),mt.getGranada() + "-" + mt.getEspoleta(),
            Integer.parseInt(mt.getPiezas()),rondas,mt.getGranada(),mt.getEspoleta(),"Carga estándar");
        
        nuevo.setModoFuego(mt.isCuandoListo() ? "CUANDO LISTO" : "A MI ORDEN");
        nuevo.setFuegoContinuo(mt.isFgoSi());
        nuevo.setTes(mt.isTesSi());
        nuevo.setTotSegundos(mt.getTot());
        nuevo.setSeccion(mt.getSeccion());

        modeloHistorial.addElement(nuevo);

        JOptionPane.showMessageDialog(this, "PIF registrado correctamente.",
                "Confirmado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarPIFSeleccionado() {
        PIF p = listaHistorial.getSelectedValue();
        if (p == null) return;

        datosDeBlancoPanel.setDatosBlanco(p.getBlanco());
        metodoYTiroPanel.mostrarPIF(p);

        cardLayout.show(pifCardPanel, "datos");
        resaltarSegunCard("datos");
    }

    private void mostrarPanelSeguro(String card, int nuevoIndice) {
        if (transicionEnCurso) return;
        transicionEnCurso = true;

        Runnable cambio = () -> {
            try {
                indiceActual = nuevoIndice;
                cardLayout.show(pifCardPanel, card);
                resaltarSegunCard(card);
            } finally {
                Timer t = new Timer(150, evt -> transicionEnCurso = false);
                t.setRepeats(false);
                t.start();
            }
        };
        SwingUtilities.invokeLater(cambio);
    }

    private void crearBotonesDeNavegacion() {
        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        configurarBotonNavegacion(btnPrev);
        configurarBotonNavegacion(btnNext);

        JPanel centerWithNav = new JPanel(new BorderLayout());
        centerWithNav.setBackground(Color.BLACK);
        centerWithNav.add(btnPrev, BorderLayout.WEST);
        centerWithNav.add(pifCardPanel, BorderLayout.CENTER);
        centerWithNav.add(btnNext, BorderLayout.EAST);

        pifPanel.add(centerWithNav, BorderLayout.CENTER);

        btnPrev.addActionListener(e -> moverSeguro(-1));
        btnNext.addActionListener(e -> moverSeguro(+1));

        // atajos de teclado
        InputMap inputMap = pifPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = pifPanel.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "prev");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "next");
        actionMap.put("prev", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { btnPrev.doClick(); }
        });
        actionMap.put("next", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { btnNext.doClick(); }
        });
    }

    private void moverSeguro(int delta) {
        if (transicionEnCurso) return;
        transicionEnCurso = true;

        Runnable cambio = () -> {
            try {
                int nuevo = indiceActual + delta;
                if (nuevo >= 0 && nuevo < ordenNavegable.length) {
                    indiceActual = nuevo;
                    String card = ordenNavegable[indiceActual];
                    cardLayout.show(pifCardPanel, card);
                    resaltarSegunCard(card);
                }
            } finally {
                Timer t = new Timer(150, evt -> transicionEnCurso = false);
                t.setRepeats(false);
                t.start();
            }
        };
        SwingUtilities.invokeLater(cambio);
    }

    private void configurarBotonNavegacion(JButton b) {
        b.setFont(new Font("Arial", Font.BOLD, 24));
        b.setBackground(new Color(0, 100, 0));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    private void resaltarSegunCard(String cardName) {
        Color resaltado = new Color(0, 100, 0);
        Color normal = new Color(60, 60, 60);

        btnDatos.setBackground(normal);
        btnMetodo.setBackground(normal);

        switch (cardName) {
            case "datos":
                btnDatos.setBackground(resaltado);
                break;
            case "metodoTiro":
                btnMetodo.setBackground(resaltado);
                break;
        }
    }

    private TitledBorder crearBordeTitulo(String titulo) {
        Font fuente = new Font("Arial", Font.BOLD, 20);
        TitledBorder borde = BorderFactory.createTitledBorder(titulo);
        borde.setTitleColor(Color.WHITE);
        borde.setTitleFont(fuente);
        return borde;
    }

    public DatosBlanco getDatosDeBlancoPanel() { return datosDeBlancoPanel; }
    public LinkedList<Blanco> getListaDeBlancos() { return listaDeBlancos; }
}
