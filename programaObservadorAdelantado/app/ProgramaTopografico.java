package app;

import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

import comunicaciones.ComunicacionIP;
import comunicaciones.ProtocoloCallback;
import dominio.Blanco;
import interfaz.Mensajeria;
import mensajes.ProcesadorMensajes;
import util.SoundManager;

public class ProgramaTopografico extends JPanel {

    private static final long serialVersionUID = 1L;

    private String idOAA;
    private static final String[] IDS_VALIDOS = {"juarez"};

    private CardLayout cardLayout;
    private JPanel cards;
    private JButton[] botonesMenu;
    private int panelActual = 0;

    private ProcesadorMensajes procesadorMensajes;
    private SoundManager sonidos;

    private ComunicacionIP comunicacionIP;
    private Mensajeria mensajeriaPanel;
    private PedidoDeFuego pedidoDeFuego;
    private SituacionTactica situacionTactica;

    public static void main(String[] args) {

        LinkedList<Blanco> listaDeBlancos = new LinkedList<>();
        JFrame ventana = new JFrame("Sistema de Artillería de Reconocimiento y Gestión Operacional - GRUPO TOPOGRAFICO");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setLocationRelativeTo(null);

        ImageIcon iconoOriginal = new ImageIcon(ProgramaTopografico.class.getResource("/LOGOBIAC.png"));
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
        ventana.setIconImage(imgEscalada);

        ProgramaTopografico panelObservador = new ProgramaTopografico(listaDeBlancos);
        ventana.setContentPane(panelObservador);
        ventana.setVisible(true);
    }

    public ProgramaTopografico(LinkedList<Blanco> listaDeBlancos) {

        pedirID();

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JPanel menuSuperior = new JPanel(new GridLayout(1, 3));
        menuSuperior.setPreferredSize(new Dimension(0, 40));
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

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        pedidoDeFuego = new PedidoDeFuego(listaDeBlancos, getIdOAA());
        situacionTactica = new SituacionTactica(listaDeBlancos, pedidoDeFuego, this); 
        mensajeriaPanel = new Mensajeria();

        cards.add(situacionTactica, "SITUACION");
        cards.add(pedidoDeFuego, "PEDIDO");
        cards.add(mensajeriaPanel, "MENSAJERIA");

        add(cards, BorderLayout.CENTER);
        cardLayout.show(cards, "SITUACION");

        actualizarBotonesMenu();

        // COMUNICACIÓN IP
        comunicacionIP = new ComunicacionIP();

        procesadorMensajes = new ProcesadorMensajes(pedidoDeFuego,situacionTactica,situacionTactica.getListaDeBlancos());

        comunicacionIP.setCallback(new ProtocoloCallback() {
            @Override
            public void recibir(String mensaje) {

                if (mensaje.startsWith("CHAT|")) {
                    String cuerpo = mensaje.substring("CHAT|".length());
                    mensajeriaPanel.recibirChat(cuerpo);
                    pedidoDeFuego.getConsolaMensajes().agregarMensaje("[CHAT RX] " + cuerpo);
                    return;
                }

                procesadorMensajes.procesar(mensaje);
            }

            @Override
            public void log(String texto) {
                pedidoDeFuego.getConsolaMensajes().agregarMensaje(texto);
            }
        });

        mensajeriaPanel.setComunicacion(comunicacionIP);
        pedidoDeFuego.setComunicacionIP(comunicacionIP);
    }

    public ComunicacionIP getComunicacionIP() {
        return comunicacionIP;
    }

    public void mostrarPanel(String nombreCard) {
        switch (nombreCard) {
            case "SITUACION" -> panelActual = 0;
            case "PEDIDO" -> panelActual = 1;
            case "MENSAJERIA" -> panelActual = 2;
        }
        cardLayout.show(cards, nombreCard);
        actualizarBotonesMenu();
    }

    private void pedirID() {
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/LOGOBIAC.png"));
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(80, 90, Image.SCALE_SMOOTH);
        ImageIcon icono = new ImageIcon(imgEscalada);

        sonidos = new SoundManager();

        while (true) {

            JPasswordField passwordField = new JPasswordField(15);
            passwordField.setEchoChar('*');
            passwordField.setFont(new Font("Arial", Font.BOLD, 15));           

            int opcion = JOptionPane.showConfirmDialog(
                    null,
                    passwordField,
                    "Autenticación requerida",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    icono
            );

            if (opcion != JOptionPane.OK_OPTION) {
                System.exit(0);
            }

            String idIngresado = new String(passwordField.getPassword());

            for (String valido : IDS_VALIDOS) {
                if (idIngresado.equals(valido)) {
                    this.idOAA = idIngresado;
                    return;
                }
            }

            sonidos.ingresoError();
            JOptionPane.showMessageDialog(
                    null,
                    "ID incorrecto. Intente de nuevo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE,
                    icono
            );
        }
    }

    private void actualizarBotonesMenu() {
        for (int i = 0; i < botonesMenu.length; i++) {
            botonesMenu[i].setBackground(i == panelActual ? Color.BLUE : Color.GRAY);
        }
    }
    
    public PedidoDeFuego getPedidoDeFuego() {
    	return pedidoDeFuego;
    }

    public String getIdOAA() {
        return idOAA;
    }
}
