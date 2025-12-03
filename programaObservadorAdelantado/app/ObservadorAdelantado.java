package app;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.LinkedList;

import javax.swing.*;

import dominio.Blanco;
import harris.GestorPuertoHarris;
import interfaz.Mensajeria;
import mensajes.ClienteMensajes;
import mensajes.ProcesadorMensajes;

public class ObservadorAdelantado extends JPanel{

    private static final long serialVersionUID = 1L;

    private String idOAA;
    private static final String[] IDS_VALIDOS = {"juarez"};
    private CardLayout cardLayout;
    private JPanel cards;
    private JButton[] botonesMenu;
    private int panelActual = 0;
    private GestorPuertoHarris gestorPuerto;
    private ClienteMensajes clienteMensajes;
    private ProcesadorMensajes procesadorMensajes;
	
    public static void main(String[] args) {
    	
    	LinkedList<Blanco> listaDeBlancos = new LinkedList<Blanco>();
        JFrame ventana = new JFrame("Sistema de Artillería de Reconocimiento y Gestión Operacional - OBSERVADOR");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setLocationRelativeTo(null);
        ImageIcon iconoOriginal = new ImageIcon(ObservadorAdelantado.class.getResource("/LOGOBIAC.png"));
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
        ventana.setIconImage(imgEscalada);
        
        ObservadorAdelantado panelObservador = new ObservadorAdelantado(listaDeBlancos);
        ventana.setContentPane(panelObservador);
        ventana.setVisible(true);
    }

    public ObservadorAdelantado(LinkedList<Blanco> listaDeBlancos) {
        // pide la contraseña antes de cargar la interfaz
        pedirID();

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        // menu superior con 3 botones
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

        // contenedor de cards
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        PedidoDeFuego pedidoDeFuego = new PedidoDeFuego(listaDeBlancos, getIdOAA());
        SituacionTactica situacionTactica = new SituacionTactica(listaDeBlancos,pedidoDeFuego);
        Mensajeria mensajeria = new Mensajeria();
        
        GestorPuertoHarris gestorPuerto = new GestorPuertoHarris();
	    boolean ok = gestorPuerto.abrir("COM3");
	
	    if(!ok){
	         JOptionPane.showMessageDialog(this,
	             "No se pudo abrir el puerto Harris (COM3).",
	             "ERROR",
	             JOptionPane.ERROR_MESSAGE
	         );
	     }
	    ProcesadorMensajes procesador = new ProcesadorMensajes(pedidoDeFuego.getConsolaMensajes(),situacionTactica,situacionTactica.getListaDeBlancos());
	
	    // Lanzar cliente que escucha todo el tiempo el puerto
	    ClienteMensajes cliente = new ClienteMensajes(gestorPuerto, procesador);
	
	    // Guardar referencias si querés usarlas después
	    this.setGestorPuerto(gestorPuerto);
	    this.setClienteMensajes(cliente);
	    this.setProcesadorMensajes(procesador);
        	
        // paneles con clases cardboard
        cards.add(situacionTactica,"SITUACION");
        cards.add(pedidoDeFuego, "PEDIDO");
        cards.add(mensajeria, "MENSAJERIA");

        add(cards, BorderLayout.CENTER);
        cardLayout.show(cards, "SITUACION");
        actualizarBotonesMenu();
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
        while (true) {
            String idIngresado = (String) JOptionPane.showInputDialog(null,"Ingresar ID de OAA:","Autenticación requerida",JOptionPane.PLAIN_MESSAGE, icono,null,null);
            if (idIngresado == null) {System.exit(0);}
            for (String valido : IDS_VALIDOS) {
                if (idIngresado.equals(valido)) {
                    setIdOAA(idIngresado);
                    return;
                }
            }
            JOptionPane.showMessageDialog(null,"ID incorrecto. Intente de nuevo.","Error",JOptionPane.ERROR_MESSAGE,icono); 
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

	public GestorPuertoHarris getGestorPuerto() {
		return gestorPuerto;
	}

	public void setGestorPuerto(GestorPuertoHarris gestorPuerto) {
		this.gestorPuerto = gestorPuerto;
	}

	public ClienteMensajes getClienteMensajes() {
		return clienteMensajes;
	}

	public void setClienteMensajes(ClienteMensajes clienteMensajes) {
		this.clienteMensajes = clienteMensajes;
	}

	public ProcesadorMensajes getProcesadorMensajes() {
		return procesadorMensajes;
	}

	public void setProcesadorMensajes(ProcesadorMensajes procesadorMensajes) {
		this.procesadorMensajes = procesadorMensajes;
	}
}
