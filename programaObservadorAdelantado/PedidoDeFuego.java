import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.TitledBorder;

class PedidoDeFuego extends JPanel {

	private static final long serialVersionUID = 1L;

	private LinkedList<Blanco> listaDeBlancos;

    // Referencias al CardLayout y a los paneles
    private CardLayout cardLayout;
    private JPanel pifCardPanel;
    private MetodoAtaquePanel metodoAtaquePanel;
    private LocalizacionDeBlancoPanel localizacionDeBlancoPanel;
    private NaturalezaDeBlancoPanel naturalezaDeBlancoPanel;
    private TiroYControlPanel tiroYControlPanel;
    private JPanel correccionesPanel, pifPanel;

    // Botones superiores
    private JButton btnLocBlanco;
    private JButton btnNatBlanco;
    private JButton btnMetodo;
    private JButton btnTiro;

    private final String[] ordenNavegable = {"localizacion", "naturaleza", "metodo", "tiro"};
    private int indiceActual = 0;
    
    public PedidoDeFuego(LinkedList<Blanco> listaDeBlancos) {

        this.listaDeBlancos=listaDeBlancos;

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // PANEL PIF 
        pifPanel = new JPanel(new BorderLayout());
        pifPanel.setBackground(Color.BLACK);
        pifPanel.setBorder(crearBordeTitulo(""));

        // Panel de botones superiores
        JPanel botonesPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        botonesPanel.setBackground(Color.BLACK);

        btnLocBlanco = new JButton("LOCALIZACION BLANCO");
        btnNatBlanco = new JButton("NATURALEZA BLANCO");
        btnMetodo = new JButton("METODO DE ATAQUE");
        btnTiro = new JButton("TIRO Y CONTROL");

        for (JButton b : new JButton[]{btnLocBlanco, btnNatBlanco, btnMetodo, btnTiro}) {
            b.setBackground(new Color(60, 60, 60));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        }

        botonesPanel.add(btnLocBlanco);
        botonesPanel.add(btnNatBlanco);
        botonesPanel.add(btnMetodo);
        botonesPanel.add(btnTiro);

        pifPanel.add(botonesPanel, BorderLayout.NORTH);

        // CARDLAYOUT PARA EL CONTENIDO DE PIF 
        cardLayout = new CardLayout();
        pifCardPanel = new JPanel(cardLayout);
        pifCardPanel.setBackground(Color.BLACK);

        // Vista inicial vacía o de bienvenida
        JPanel vistaInicial = new JPanel();
        vistaInicial.setBackground(Color.BLACK);
        vistaInicial.add(new JLabel("Seleccione una opción arriba", JLabel.CENTER));
        ((JLabel) vistaInicial.getComponent(0)).setForeground(Color.WHITE);

        // Paneles específicos
        metodoAtaquePanel = new MetodoAtaquePanel();
        localizacionDeBlancoPanel = new LocalizacionDeBlancoPanel(listaDeBlancos);
        naturalezaDeBlancoPanel = new NaturalezaDeBlancoPanel();
        tiroYControlPanel = new TiroYControlPanel();

        // PANEL CORRECCIONES
        correccionesPanel = new CorreccionesPanel(this);

        // Registrar vistas en el CardLayout
        pifCardPanel.add(vistaInicial, "inicio");
        pifCardPanel.add(localizacionDeBlancoPanel, "localizacion");
        pifCardPanel.add(naturalezaDeBlancoPanel, "naturaleza");
        pifCardPanel.add(metodoAtaquePanel, "metodo");
        pifCardPanel.add(tiroYControlPanel, "tiro");
        pifCardPanel.add(correccionesPanel, "correcciones");

        cardLayout.show(pifCardPanel, "localizacion");
        resaltarSegunCard("localizacion");

        pifPanel.add(pifCardPanel, BorderLayout.CENTER);

        // PANEL HISTORIAL
        JPanel historialPanel = new JPanel(new BorderLayout());
        historialPanel.setBackground(Color.BLACK);
        historialPanel.setBorder(crearBordeTitulo("HISTORIAL"));
        historialPanel.setPreferredSize(new Dimension(200, 0));

        add(pifPanel, BorderLayout.CENTER);
        add(historialPanel, BorderLayout.EAST);

        inicializarAcciones();
        
        crearBotonesDeNavegacion();	
    }

    private void inicializarAcciones() {
    	// acciones de botones superiores
        btnLocBlanco.addActionListener(e -> {
            indiceActual = 0;
            cardLayout.show(pifCardPanel, "localizacion");
            resaltarSegunCard("localizacion");
        });

        btnNatBlanco.addActionListener(e -> {
            indiceActual = 1;
            cardLayout.show(pifCardPanel, "naturaleza");
            resaltarSegunCard("naturaleza");
        });

        btnMetodo.addActionListener(e -> {
            indiceActual = 2;
            cardLayout.show(pifCardPanel, "metodo");
            resaltarSegunCard("metodo");
        });

        btnTiro.addActionListener(e -> {
            indiceActual = 3;
            cardLayout.show(pifCardPanel, "tiro");
            resaltarSegunCard("tiro");
        });

        // conexion con TiroYControlPanel 
        tiroYControlPanel.setEnviarListener(() -> {
            cardLayout.show(pifCardPanel, "correcciones");
        });
    }
    
    @SuppressWarnings("serial")
	private void crearBotonesDeNavegacion() {
        JButton btnPrev = new JButton("<");
        btnPrev.setFont(new Font("Arial", Font.BOLD, 24));
        btnPrev.setBackground(new Color(0, 100, 0));
        btnPrev.setForeground(Color.WHITE);
        btnPrev.setFocusPainted(false);

        JButton btnNext = new JButton(">");
        btnNext.setFont(new Font("Arial", Font.BOLD, 24));
        btnNext.setBackground(new Color(0, 100, 0));
        btnNext.setForeground(Color.WHITE);
        btnNext.setFocusPainted(false);

        JPanel centerWithNav = new JPanel(new BorderLayout());
        centerWithNav.setBackground(Color.BLACK);

        centerWithNav.add(btnPrev, BorderLayout.WEST);
        centerWithNav.add(pifCardPanel, BorderLayout.CENTER);
        centerWithNav.add(btnNext, BorderLayout.EAST);

        pifPanel.add(centerWithNav, BorderLayout.CENTER);

        btnPrev.addActionListener(e -> {
            if (indiceActual > 0) {
                indiceActual--;
                String card = ordenNavegable[indiceActual];
                cardLayout.show(pifCardPanel, card);
                resaltarSegunCard(card);
            }
        });

        btnNext.addActionListener(e -> {
            if (indiceActual < ordenNavegable.length - 1) {
                indiceActual++;
                String card = ordenNavegable[indiceActual];
                cardLayout.show(pifCardPanel, card);
                resaltarSegunCard(card);
            }
        });
        // Atajos con flechas
        InputMap inputMap = pifPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = pifPanel.getActionMap();

        // Flecha izquierda → botón Prev
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "irPrev");
        actionMap.put("irPrev", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnPrev.doClick();
            }
        });
        // Flecha derecha → botón Next
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "irNext");
        actionMap.put("irNext", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnNext.doClick();
            }
        });
    }

    private void resaltarSegunCard(String cardName) {
        switch (cardName) {
            case "localizacion":
                resaltarBoton(btnLocBlanco, btnNatBlanco, btnMetodo, btnTiro);
                break;
            case "naturaleza":
                resaltarBoton(btnNatBlanco, btnLocBlanco, btnMetodo, btnTiro);
                break;
            case "metodo":
                resaltarBoton(btnMetodo, btnLocBlanco, btnNatBlanco, btnTiro);
                break;
            case "tiro":
                resaltarBoton(btnTiro, btnLocBlanco, btnNatBlanco, btnMetodo);
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

    private void resaltarBoton(JButton seleccionado, JButton... otros) {
        Color resaltado = new Color(0, 100, 0);
        Color normal = new Color(60, 60, 60);
        seleccionado.setBackground(resaltado);
        for (JButton b : otros) {
            b.setBackground(normal);
        }
    }

    public void mostrarInicio() {
        cardLayout.show(pifCardPanel, "inicio");
    }
    
    // Métodos para acceder a los paneles
    public MetodoAtaquePanel getMetodoAtaquePanel() { return metodoAtaquePanel; }
    public LocalizacionDeBlancoPanel getLocalizacionDeBlancoPanel() { return localizacionDeBlancoPanel; }
    public NaturalezaDeBlancoPanel getNaturalezaDeBlancoPanel() { return naturalezaDeBlancoPanel; }
    public TiroYControlPanel getTiroYControlPanel() { return tiroYControlPanel; }

	public LinkedList<Blanco> getListaDeBlancos() {
		return listaDeBlancos;
	}
}
