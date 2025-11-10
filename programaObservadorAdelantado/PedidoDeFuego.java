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
    private MetodoAtaquePanel metodoAtaquePanel;
    private TiroYControlPanel tiroYControlPanel;
    private JPanel correccionesPanel, pifPanel;

    // botones superiores
    private JButton btnDatos;
    private JButton btnMetodo;
    private JButton btnTiro;

    // navegación
    private final String[] ordenNavegable = {"datos", "metodo", "tiro"};
    private volatile int indiceActual = 0; 
    private volatile boolean transicionEnCurso = false;

    public PedidoDeFuego(LinkedList<Blanco> listaDeBlancos) {
        this.listaDeBlancos = listaDeBlancos;

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // panel principal
        pifPanel = new JPanel(new BorderLayout());
        pifPanel.setBackground(Color.BLACK);
        pifPanel.setBorder(crearBordeTitulo(""));

        crearPanelDeBotones();
        crearCardLayout();
        crearBotonesDeNavegacion();
        crearPanelHistorial();

        inicializarAcciones();
    }

    private void crearPanelDeBotones() {
        JPanel botonesPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        botonesPanel.setBackground(Color.BLACK);

        btnDatos = new JButton("DATOS DEL BLANCO");
        btnMetodo = new JButton("MÉTODO DE ATAQUE");
        btnTiro = new JButton("TIRO Y CONTROL");

        for (JButton b : new JButton[]{btnDatos, btnMetodo, btnTiro}) {
            b.setBackground(new Color(60, 60, 60));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        }

        botonesPanel.add(btnDatos);
        botonesPanel.add(btnMetodo);
        botonesPanel.add(btnTiro);
        pifPanel.add(botonesPanel, BorderLayout.NORTH);
    }

    private void crearCardLayout() {
        cardLayout = new CardLayout();
        pifCardPanel = new JPanel(cardLayout);
        pifCardPanel.setBackground(Color.BLACK);

        JLabel lblInicio = new JLabel("Seleccione una opción arriba", JLabel.CENTER);
        lblInicio.setForeground(Color.WHITE);

        datosDeBlancoPanel = new DatosBlanco();
        metodoAtaquePanel = new MetodoAtaquePanel();
        tiroYControlPanel = new TiroYControlPanel();

        pifCardPanel.add(datosDeBlancoPanel, "datos");
        pifCardPanel.add(metodoAtaquePanel, "metodo");
        pifCardPanel.add(tiroYControlPanel, "tiro");

        cardLayout.show(pifCardPanel, "datos");
        resaltarSegunCard("datos");

        pifPanel.add(pifCardPanel, BorderLayout.CENTER);
        add(pifPanel, BorderLayout.CENTER);
    }

    private void crearPanelHistorial() {
        JPanel historialPanel = new JPanel(new BorderLayout());
        historialPanel.setBackground(Color.BLACK);
        historialPanel.setBorder(crearBordeTitulo("HISTORIAL"));
        historialPanel.setPreferredSize(new Dimension(200, 0));
        add(historialPanel, BorderLayout.EAST);
    }

    private void inicializarAcciones() {
        btnDatos.addActionListener(e -> mostrarPanelSeguro("datos", 0));
        btnMetodo.addActionListener(e -> mostrarPanelSeguro("metodo", 1));
        btnTiro.addActionListener(e -> mostrarPanelSeguro("tiro", 2));

        tiroYControlPanel.setEnviarListener(() -> {
            mostrarPanelSeguro("correcciones", -1);
        });
    }

    private void mostrarPanelSeguro(String card, int nuevoIndice) {
        if (transicionEnCurso) return; // evita flood por clics múltiples
        transicionEnCurso = true;

        Runnable cambio = () -> {
            try {
                if (nuevoIndice >= 0 && nuevoIndice < ordenNavegable.length)
                    indiceActual = nuevoIndice;

                cardLayout.show(pifCardPanel, card);
                resaltarSegunCard(card);
            } finally {
                Timer t = new Timer(150, evt -> transicionEnCurso = false);
                t.setRepeats(false);
                t.start();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            cambio.run();
        } else {
            SwingUtilities.invokeLater(cambio);
        }
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

        // atajos con teclado
        InputMap inputMap = pifPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = pifPanel.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "prev");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "next");
        actionMap.put("prev", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { btnPrev.doClick(); }
        });
        actionMap.put("next", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { btnNext.doClick(); }
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
        btnTiro.setBackground(normal);

        switch (cardName) {
            case "datos": btnDatos.setBackground(resaltado); break;
            case "metodo": btnMetodo.setBackground(resaltado); break;
            case "tiro": btnTiro.setBackground(resaltado); break;
        }
    }

    private TitledBorder crearBordeTitulo(String titulo) {
        Font fuente = new Font("Arial", Font.BOLD, 20);
        TitledBorder borde = BorderFactory.createTitledBorder(titulo);
        borde.setTitleColor(Color.WHITE);
        borde.setTitleFont(fuente);
        return borde;
    }

    public MetodoAtaquePanel getMetodoAtaquePanel() { return metodoAtaquePanel; }
    
    public TiroYControlPanel getTiroYControlPanel() { return tiroYControlPanel; }
    
    public DatosBlanco getDatosDeBlancoPanel() { return datosDeBlancoPanel; }
    
    public LinkedList<Blanco> getListaDeBlancos() { return listaDeBlancos; }
}
