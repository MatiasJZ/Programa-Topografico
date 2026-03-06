package comunicaciones;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * ConsolaMensajes is a custom JPanel component that displays messages in a console-like interface.
 * 
 * It provides a styled message console with a header and a scrollable text area. The console
 * displays messages with different prefixes to indicate their type (STATE, TX, RX).
 * s
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
    private JTextPane area;
    private JScrollPane scroll;
    private final int ALTURA_ABIERTA = 200;

    public ConsolaMensajes() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));
        setPreferredSize(new Dimension(300, ALTURA_ABIERTA));

        header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(300, 28));
        header.setBackground(new Color(40, 40, 40));

        JLabel lblTitulo = new JLabel(" - REGISTRO DE RED (CHAT)", SwingConstants.LEFT); 
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 12));
        header.add(lblTitulo, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        area = new JTextPane();
        area.setEditable(false);
        area.setBackground(new Color(15, 15, 15));

        scroll = new JScrollPane(area);
        scroll.setVisible(true); 
        add(scroll, BorderLayout.CENTER);
    }

    public void agregarMensajeColor(String mensaje, Color color, boolean negrita) {
        StyledDocument doc = area.getStyledDocument();
        Style style = area.addStyle("ColorStyle", null);
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, negrita);
        StyleConstants.setFontFamily(style, "Consolas");
        StyleConstants.setFontSize(style, 13);
        
        try {
            doc.insertString(doc.getLength(), mensaje + "\n", style);
            area.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void agregarMensaje(String mensaje) {
        if (mensaje.startsWith("[TX")) {
            agregarMensajeColor(mensaje, new Color(0, 200, 255), true); 
        } else if (mensaje.startsWith("[RX")) {
            agregarMensajeColor(mensaje, new Color(255, 80, 80), true); 
        } else if (mensaje.startsWith("[ERROR")) {
            agregarMensajeColor(mensaje, Color.ORANGE, true); 
        } else {
            agregarMensajeColor(mensaje, Color.GREEN, false); 
        }
    }
    
    public void mostrarRx(String ipOrigen, String msg) {
        agregarMensajeColor("◄ [RX - " + ipOrigen + "] " + msg, new Color(255, 80, 80), true); }

    public void mostrarTx(String ipDestino, String msg) {
        agregarMensajeColor("► [TX - " + ipDestino + "] " + msg, new Color(0, 200, 255), true); 
    }

    public void mostrarSistema(String msg) {
        agregarMensajeColor("⚙ [SISTEMA] " + msg, new Color(150, 255, 150), false);
    }
}