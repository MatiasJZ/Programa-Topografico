import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.TitledBorder;

class PedidoDeFuego extends JPanel {

    private LinkedList<Blanco> listaDeBlancos;

    // Referencias al CardLayout y al panel que lo usa
    private CardLayout cardLayout;
    private JPanel pifCardPanel;
    private MetodoAtaquePanel metodoAtaquePanel;

    public PedidoDeFuego(LinkedList<Blanco> listaDeBlancos) {

        this.listaDeBlancos = listaDeBlancos;

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // ====== PANEL PIF ======
        JPanel pifPanel = new JPanel(new BorderLayout());
        pifPanel.setBackground(Color.BLACK);
        pifPanel.setBorder(crearBordeTitulo("PIF"));

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

        // Panel Metodo de Ataque
        metodoAtaquePanel = new MetodoAtaquePanel();

        // Registrar vistas en el CardLayout
        pifCardPanel.add(vistaInicial, "inicio");
        pifCardPanel.add(metodoAtaquePanel, "metodo");

        // Mostrar vista inicial por defecto
        cardLayout.show(pifCardPanel, "inicio");

        pifPanel.add(pifCardPanel, BorderLayout.CENTER);

        // ====== PANEL HISTORIAL ======
        JPanel historialPanel = new JPanel(new BorderLayout());
        historialPanel.setBackground(Color.BLACK);
        historialPanel.setBorder(crearBordeTitulo("HISTORIAL"));
        historialPanel.setPreferredSize(new Dimension(200, 0));

        // ====== AGREGAR AL LAYOUT PRINCIPAL ======
        add(pifPanel, BorderLayout.CENTER);
        add(historialPanel, BorderLayout.EAST);

        // ====== ACCIONES DE BOTONES (con resaltado) ======
        btnLocBlanco.addActionListener(e -> {
            cardLayout.show(pifCardPanel, "inicio");
            resaltarBoton(btnLocBlanco, btnNatBlanco, btnMetodo, btnTiro);
        });

        btnNatBlanco.addActionListener(e -> {
            cardLayout.show(pifCardPanel, "inicio");
            resaltarBoton(btnNatBlanco, btnLocBlanco, btnMetodo, btnTiro);
        });

        btnMetodo.addActionListener(e -> {
            cardLayout.show(pifCardPanel, "metodo");
            resaltarBoton(btnMetodo, btnLocBlanco, btnNatBlanco, btnTiro);
        });

        btnTiro.addActionListener(e -> {
            cardLayout.show(pifCardPanel, "inicio");
            resaltarBoton(btnTiro, btnLocBlanco, btnNatBlanco, btnMetodo);
        });
    }

    // Método para crear bordes con títulos grandes y blancos
    private TitledBorder crearBordeTitulo(String titulo) {
        Font fuente = new Font("Arial", Font.BOLD, 20);
        TitledBorder borde = BorderFactory.createTitledBorder(titulo);
        borde.setTitleColor(Color.WHITE);
        borde.setTitleFont(fuente);
        return borde;
    }

    // Método para resaltar el botón seleccionado
    private void resaltarBoton(JButton seleccionado, JButton... otros) {
    	Color resaltado = new Color(0, 100, 0); // amarillo tenue
        Color normal = new Color(60, 60, 60);      // gris oscuro

        seleccionado.setBackground(resaltado);
        for (JButton b : otros) {
            b.setBackground(normal);
        }
    }

    // ====== MÉTODOS PARA ACCEDER A LOS DATOS DE METODO DE ATAQUE ======
    public MetodoAtaquePanel getMetodoAtaquePanel() {
        return metodoAtaquePanel;
    }
}
