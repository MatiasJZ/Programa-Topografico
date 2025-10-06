import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.LinkedList;

import javax.swing.*;

public class ObservadorAdelantado extends JPanel{

    private static final long serialVersionUID = 1L;

    private String idOAA;

    private static final String[] IDS_VALIDOS = {"juarez"};

    private CardLayout cardLayout;
    private JPanel cards;
    private JButton[] botonesMenu;
    private int panelActual = 0;
	
    public static void main(String[] args) {
    	LinkedList<Blanco> listaDeBlancos = new LinkedList<Blanco>();
        JFrame ventana = new JFrame("Sistema de Artillería de Reconocimiento y Gestión Operacional - OBSERVADOR");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setLocationRelativeTo(null);
        Image icon = Toolkit.getDefaultToolkit().getImage("C:/Users/54293/Desktop/Archivos SARGO/LOGOBIAC.png");
        ventana.setIconImage(icon);
        
        ObservadorAdelantado panelObservador = new ObservadorAdelantado(listaDeBlancos);
        ventana.setContentPane(panelObservador);
        ventana.setVisible(true);
    }

    public ObservadorAdelantado(LinkedList<Blanco> listaDeBlancos) {
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
        cards.add(new SituacionTactica(listaDeBlancos),"SITUACION");
        cards.add(new PedidoDeFuego(listaDeBlancos), "PEDIDO");
        cards.add(new Mensajeria(), "MENSAJERIA");

        add(cards, BorderLayout.CENTER);

        // Mostrar primera card
        cardLayout.show(cards, "SITUACION");
        actualizarBotonesMenu();
    }

    private void pedirID() {
        String rutaLogo = "C:/Users/54293/Desktop/Archivos SARGO/LOGOBIAC.png";
        ImageIcon iconoOriginal = new ImageIcon(rutaLogo);
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
        ImageIcon icono = new ImageIcon(imgEscalada);
        while (true) {
            String idIngresado = (String) JOptionPane.showInputDialog(null,"Ingresar ID de OAA:","Autenticación requerida",JOptionPane.PLAIN_MESSAGE, icono,null,null);
            if (idIngresado == null) {System.exit(0);}
            for (String valido : IDS_VALIDOS) {
                if (idIngresado.equals(valido)) {
                    setIdOAA(idIngresado);
                    JOptionPane.showMessageDialog(null,"Acceso concedido","BIAC",JOptionPane.INFORMATION_MESSAGE,icono);
                    return;
                }
            }
            JOptionPane.showMessageDialog(
                    null,
                    "ID incorrecto. Intente de nuevo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE,
                    icono); // idem aquí, para uniformidad
        }
    }

    private void actualizarBotonesMenu() {
        for (int i = 0; i < botonesMenu.length; i++) {
            botonesMenu[i].setBackground(i == panelActual ? Color.BLUE : Color.GRAY);
        }
    }

	public String getIdOAA() {
		return idOAA;
	}

	public void setIdOAA(String idOAA) {
		this.idOAA = idOAA;
	}
}
