import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.TitledBorder;

class PedidoDeFuego extends JPanel {

    private LinkedList<Blanco> listaDeBlancos;

    // Referencias al CardLayout y a los paneles
    private CardLayout cardLayout;
    private JPanel pifCardPanel;
    private MetodoAtaquePanel metodoAtaquePanel;
    private LocalizacionDeBlancoPanel localizacionDeBlancoPanel;
    private NaturalezaDeBlancoPanel naturalezaDeBlancoPanel;
    private TiroYControlPanel tiroYControlPanel;
    private JPanel correccionesPanel;

    public PedidoDeFuego(LinkedList<Blanco> listaDeBlancos) {

        this.setListaDeBlancos(listaDeBlancos);

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // ====== PANEL PIF ======
        JPanel pifPanel = new JPanel(new BorderLayout());
        pifPanel.setBackground(Color.BLACK);
        pifPanel.setBorder(crearBordeTitulo(""));

        // Panel de botones superiores
        JPanel botonesPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        botonesPanel.setBackground(Color.BLACK);

        JButton btnLocBlanco = new JButton("LOCALIZACION BLANCO");
        JButton btnNatBlanco = new JButton("NATURALEZA BLANCO");
        JButton btnMetodo = new JButton("METODO DE ATAQUE");
        JButton btnTiro = new JButton("TIRO Y CONTROL");

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

        // ====== CARDLAYOUT PARA EL CONTENIDO DE PIF ======
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

        // ====== PANEL CORRECCIONES ======
        correccionesPanel = new CorreccionesPanel(this);

        // Registrar vistas en el CardLayout
        pifCardPanel.add(vistaInicial, "inicio");
        pifCardPanel.add(localizacionDeBlancoPanel, "localizacion");
        pifCardPanel.add(naturalezaDeBlancoPanel, "naturaleza");
        pifCardPanel.add(metodoAtaquePanel, "metodo");
        pifCardPanel.add(tiroYControlPanel, "tiro");
        pifCardPanel.add(correccionesPanel, "correcciones");

        // Mostrar LOCALIZACION BLANCO por defecto
        cardLayout.show(pifCardPanel, "localizacion");
        resaltarBoton(btnLocBlanco, btnNatBlanco, btnMetodo, btnTiro);

        pifPanel.add(pifCardPanel, BorderLayout.CENTER);

        // ====== PANEL HISTORIAL ======
        JPanel historialPanel = new JPanel(new BorderLayout());
        historialPanel.setBackground(Color.BLACK);
        historialPanel.setBorder(crearBordeTitulo("HISTORIAL"));
        historialPanel.setPreferredSize(new Dimension(200, 0));

        add(pifPanel, BorderLayout.CENTER);
        add(historialPanel, BorderLayout.EAST);

        // ====== ACCIONES DE BOTONES (con resaltado) ======
        btnLocBlanco.addActionListener(e -> {
            cardLayout.show(pifCardPanel, "localizacion");
            resaltarBoton(btnLocBlanco, btnNatBlanco, btnMetodo, btnTiro);
        });

        btnNatBlanco.addActionListener(e -> {
            cardLayout.show(pifCardPanel, "naturaleza");
            resaltarBoton(btnNatBlanco, btnLocBlanco, btnMetodo, btnTiro);
        });

        btnMetodo.addActionListener(e -> {
            cardLayout.show(pifCardPanel, "metodo");
            resaltarBoton(btnMetodo, btnLocBlanco, btnNatBlanco, btnTiro);
        });

        btnTiro.addActionListener(e -> {
            cardLayout.show(pifCardPanel, "tiro");
            resaltarBoton(btnTiro, btnLocBlanco, btnNatBlanco, btnMetodo);
        });

        // ====== CONEXIÓN CON TiroYControlPanel ======
        tiroYControlPanel.setEnviarListener(() -> {
            cardLayout.show(pifCardPanel, "correcciones");
        });
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

	public void setListaDeBlancos(LinkedList<Blanco> listaDeBlancos) {
		this.listaDeBlancos = listaDeBlancos;
	}
}
