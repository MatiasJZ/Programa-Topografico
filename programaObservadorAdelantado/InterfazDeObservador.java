import java.awt.*;
import javax.swing.*;

public class InterfazDeObservador extends JPanel {
    private static final long serialVersionUID = 1L;

    private String idOAA;

    // IDs válidos 
    private static final String[] IDS_VALIDOS = {"juarez", "lopez", "lamas"};

    // GUI
    private CardLayout cardLayout;
    private JPanel cards;
    private JButton[] botonesMenu;
    private int panelActual = 0;

    public InterfazDeObservador() {
        // Pide la ID antes de cargar la interfaz
        pedirID();

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Menú superior con 3 botones
        JPanel menuSuperior = new JPanel(new GridLayout(1, 3));
        menuSuperior.setBackground(Color.DARK_GRAY);
        String[] secciones = {"SITUACION TACTICA", "PEDIDO DE FUEGO", "MENSAJERIA"};

        botonesMenu = new JButton[secciones.length];
        for (int i = 0; i < secciones.length; i++) {
            JButton btn = new JButton(secciones[i]);
            btn.setForeground(Color.WHITE);
            btn.setBackground(Color.GRAY);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            botonesMenu[i] = btn;

            final int idx = i;
            btn.addActionListener(e -> {
                panelActual = idx;
                switch (idx) {
                    case 0 -> cardLayout.show(cards, "SITUACION");
                    case 1 -> cardLayout.show(cards, "PEDIDO");
                    case 2 -> cardLayout.show(cards, "MENSAJERIA");
                }
                actualizarBotonesMenu();
            });

            menuSuperior.add(btn);
        }
        add(menuSuperior, BorderLayout.NORTH);

        // Contenedor de cards
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // Paneles con clases cardboard
        cards.add(new SituacionTactica(), "SITUACION");
        cards.add(new PedidoDeFuego(), "PEDIDO");
        cards.add(new Mensajeria(), "MENSAJERIA");
// 

        add(cards, BorderLayout.CENTER);

        // Mostrar primera card
        cardLayout.show(cards, "SITUACION");
        actualizarBotonesMenu();
    }

    // Login de ID 
    private void pedirID() {
        while (true) {
            String idIngresado = JOptionPane.showInputDialog(
                null,"Ingrese su ID de observador:",
                "Autenticación requerida",JOptionPane.QUESTION_MESSAGE);

            if (idIngresado == null) {
                System.exit(0); // Si cancela, cerrar programa
            }

            for (String valido : IDS_VALIDOS) {
                if (idIngresado.equals(valido)) {
                    idOAA = idIngresado;
                    JOptionPane.showMessageDialog(null,
                        "Acceso concedido. Bienvenido " + idIngresado,
                        "Correcto",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
            }
            JOptionPane.showMessageDialog(null,
                "ID incorrecto. Intente de nuevo.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarBotonesMenu() {
        for (int i = 0; i < botonesMenu.length; i++) {
            botonesMenu[i].setBackground(i == panelActual ? Color.BLUE : Color.GRAY);
        }
    }
}
