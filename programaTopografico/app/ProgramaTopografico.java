package app;

import comunicaciones.GestorEnlaceOperativo;
import comunicaciones.ProtocoloCallback;
import dominio.Blanco;
import interfaz.Mensajeria;
import java.awt.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import javax.swing.*;
import mensajes.ProcesadorMensajes;
import util.GestorSonido;

/**
 * ProgramaTopografico is the main GUI panel for a tactical topographic artillery system.
 * 
 * This class extends JPanel and serves as the primary user interface for the "Sistema de Artillería 
 * de Reconocimiento y Gestión Operacional - GRUPO TOPOGRAFICO" application. It manages a multi-panel 
 * interface using CardLayout to switch between different operational modules.
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Operator authentication via ID verification</li>
 *   <li>Multi-panel navigation using CardLayout (Tactical Situation and Messaging)</li>
 *   <li>IP communication management for message exchange</li>
 *   <li>Message processing and console logging</li>
 *   <li>Sound feedback for system events</li>
 * </ul>
 * 
 * <h2>Main Components:</h2>
 * <ul>
 *   <li>{@link SituacionTacticaTopografica} - Displays tactical situation and target management</li>
 *   <li>{@link Mensajeria} - Handles messaging and communication console</li>
 *   <li>{@link GestorEnlaceOperativo} - Manages IP-based operational communication</li>
 *   <li>{@link ProcesadorMensajes} - Processes incoming tactical messages</li>
 *   <li>{@link GestorSonido} - Provides audio feedback</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * The application is launched via the main method, which creates a maximized JFrame containing 
 * this panel. Upon initialization, users must authenticate with a valid operator ID to access 
 * the system. The interface provides a top navigation menu to switch between tactical situation 
 * and messaging panels.
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class ProgramaTopografico extends JPanel {

    private static final long serialVersionUID = 1L;

    private String idOAA;
    private static final String[] IDS_VALIDOS = {"juarez"};

    private CardLayout cardLayout;
    private JPanel cards;
    private JButton[] botonesMenu;
    private int panelActual = 0;

    private ProcesadorMensajes procesadorMensajes;
    private GestorSonido sonidos;

    private GestorEnlaceOperativo comunicacionIP;
    private Mensajeria mensajeriaPanel;
    private PedidoDeFuego pedidoDeFuego;
    private SituacionTacticaTopografica situacionTactica;

    public static void main(String[] args) {

        LinkedList<Blanco> listaDeBlancos = new LinkedList<>();
        JFrame ventana = new JFrame("Sistema de Artillería de Reconocimiento y Gestión Operacional - SAB");
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
        String[] secciones = {"SITUACION TACTICA","PEDIDO DE FUEGO","MENSAJERIA"};

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
        situacionTactica = new SituacionTacticaTopografica(listaDeBlancos,pedidoDeFuego, this); 
        mensajeriaPanel = new Mensajeria(idOAA);
        situacionTactica.setPanelMensajeria(mensajeriaPanel);
       
        cards.add(situacionTactica, "SITUACION");
        cards.add(pedidoDeFuego, "PEDIDO");
        cards.add(mensajeriaPanel, "MENSAJERIA");

        add(cards, BorderLayout.CENTER);
        cardLayout.show(cards, "SITUACION");

        actualizarBotonesMenu();

        // COMUNICACIÓN IP
        comunicacionIP = new GestorEnlaceOperativo();

        procesadorMensajes = new ProcesadorMensajes(situacionTactica,situacionTactica.getListaDeBlancos(),situacionTactica.getListaDePuntos());
        
        procesadorMensajes.setConsola(mensajeriaPanel.getConsolaMensajes());

        comunicacionIP.setCallback(new ProtocoloCallback() {
            @Override
            public void recibir(String mensaje) {
                mensajeriaPanel.getConsolaMensajes().agregarMensaje("[RX RAW] " + mensaje);

                if (mensaje.startsWith("CHAT|")) {
                    String cuerpo = mensaje.substring("CHAT|".length());
                    
                    mensajeriaPanel.recibirChat(cuerpo);
                    
                    procesadorMensajes.procesar(cuerpo);
                    return;
                }

                procesadorMensajes.procesar(mensaje);
            }

            @Override
            public void log(String texto) {
                mensajeriaPanel.getConsolaMensajes().agregarMensaje(texto);
            }
        });
        
        mensajeriaPanel.setComunicacion(comunicacionIP);
        
        try {
            LinkedList<String> ipsGuardadas = new LinkedList<>();
            
            ipsGuardadas.add("192.168.1.2");
            ipsGuardadas.add("192.168.2.2");

            comunicacionIP.setDestinos(ipsGuardadas);
            InetAddress ipLocal = null;
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements() && ipLocal == null) {
                NetworkInterface ni = nets.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr.getAddress().length == 4 && !addr.isLoopbackAddress()) { // Solo IPv4
                        ipLocal = addr;
                        break;
                    }
                }
            }

            if (ipLocal != null) {
                comunicacionIP.setInterfazLocal(ipLocal);
                comunicacionIP.setPuerto(10011); 
                comunicacionIP.iniciarServidor();
              
            } else {
                System.err.println(">> AUTO-START FALLIDO: No se detectó red.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        pedidoDeFuego.setComunicacionIP(comunicacionIP);
        pedidoDeFuego.setPanelMensajeria(mensajeriaPanel);
    }

    public GestorEnlaceOperativo getComunicacionIP() {
        return comunicacionIP;
    }

    public void mostrarPanel(String nombreCard) {
        switch (nombreCard) {
            case "SITUACION" -> panelActual = 0;
            case "MENSAJERIA" -> panelActual = 1;
        }
        cardLayout.show(cards, nombreCard);
        actualizarBotonesMenu();
    }

    private void pedirID() {
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/LOGOBIAC.png"));
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(100, 110, Image.SCALE_SMOOTH); // Un poco más grande para acompañar
        ImageIcon icono = new ImageIcon(imgEscalada);

        sonidos = new GestorSonido();

        while (true) {
            JPasswordField passwordField = new JPasswordField(10);
            passwordField.setEchoChar('●');
            passwordField.setFont(new Font("Arial", Font.BOLD, 30)); 
            passwordField.setHorizontalAlignment(JTextField.CENTER); 

            JPanel panelContenedor = new JPanel(new BorderLayout(0, 15));
            panelContenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel lblInstruccion = new JLabel("INGRESE IDENTIFICADOR DE OPERADOR:", SwingConstants.CENTER);
            lblInstruccion.setFont(new Font("Arial", Font.BOLD, 14));
            
            panelContenedor.add(lblInstruccion, BorderLayout.NORTH);
            panelContenedor.add(passwordField, BorderLayout.CENTER);

            UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 18));
            UIManager.put("Button.minimumSize", new Dimension(120, 50));

            int opcion = JOptionPane.showConfirmDialog(
                    null,
                    panelContenedor,
                    "SISTEMA DE AUTENTICACIÓN TÁCTICA",
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
            
            JLabel lblError = new JLabel("ID INCORRECTO. REINTENTE.");
            lblError.setFont(new Font("Arial", Font.BOLD, 16));
            
            JOptionPane.showMessageDialog(
                    null,
                    lblError,
                    "FALLO DE ACCESO",
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
