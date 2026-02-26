package comunicaciones;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * ConsolaMensajes is a custom JPanel component that displays messages in a console-like interface.
 * 
 * It provides a styled message console with a header and a scrollable text area. The console
 * displays messages with different prefixes to indicate their type (STATE, TX, RX).
 * 
 * The component features:
 * - A dark-themed header with title label
 * - A non-editable text area with green text on dark background
 * - Automatic scrolling to the latest message
 * - Methods to add messages with different prefixes (ESTADO, TX, RX)
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class ConsolaMensajes extends JPanel {
	
	private static final long serialVersionUID = 1L;
    private JPanel header;
    private JTextArea area;
    private JScrollPane scroll;
    private final int ALTURA_ABIERTA = 200;

    public ConsolaMensajes() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));
        
        setPreferredSize(new Dimension(300, ALTURA_ABIERTA));

        header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(300, 28));
        header.setBackground(new Color(40, 40, 40));

        JLabel lblTitulo = new JLabel(" - MENSAJES", SwingConstants.LEFT); 
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 12));
        header.add(lblTitulo, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // area de texto con scroll
        area = new JTextArea();
        area.setEditable(false);
        area.setBackground(new Color(15, 15, 15));
        area.setForeground(Color.GREEN);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));

        scroll = new JScrollPane(area);
        scroll.setVisible(true); 
        add(scroll, BorderLayout.CENTER);
    }

    public void agregarMensaje(String mensaje) {
        area.append(mensaje + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }
    
    public void mostrarEstado(String estado) {
        agregarMensaje("[ESTADO] " + estado);
    }

    public void mostrarTx(String msg) {
        agregarMensaje("[TX] " + msg);
    }

    public void mostrarRx(String msg) {
        agregarMensaje("[RX] " + msg);
    }
}