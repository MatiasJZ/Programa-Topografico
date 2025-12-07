package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConsolaMensajes extends JPanel {

    private static final long serialVersionUID = 1L;
    private boolean abierta = false;
    private JPanel header;
    private JTextArea area;
    private JScrollPane scroll;
    private final int ALTURA_CERRADA = 28;
    private final int ALTURA_ABIERTA = 200;

    public ConsolaMensajes() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));
        setPreferredSize(new Dimension(300, ALTURA_CERRADA));

        // barra superior (pestaña)
        header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(300, 28));
        header.setBackground(new Color(40, 40, 40));

        JLabel lblTitulo = new JLabel(" - MENSAJES", SwingConstants.LEFT);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 12));
        header.add(lblTitulo, BorderLayout.WEST);

        // click para desplegar o colapsar
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggle();
            }
        });

        add(header, BorderLayout.NORTH);

        // area de texto con scroll
        area = new JTextArea();
        area.setEditable(false);
        area.setBackground(new Color(15, 15, 15));
        area.setForeground(Color.GREEN);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));

        scroll = new JScrollPane(area);
        scroll.setVisible(false);
        add(scroll, BorderLayout.CENTER);
    }

    private void toggle() {
        abierta = !abierta;

        header.removeAll();
        JLabel lbl = new JLabel(
                abierta ? " - OCULTAR" : " - MENSAJES",
                SwingConstants.LEFT
        );
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Consolas", Font.BOLD, 12));
        header.add(lbl, BorderLayout.WEST);

        scroll.setVisible(abierta);
        setPreferredSize(new Dimension(getWidth(), abierta ? ALTURA_ABIERTA : ALTURA_CERRADA));

        revalidate();
        repaint();

        Window win = SwingUtilities.getWindowAncestor(this);
        if (win != null) win.pack();
    }

    public void agregarMensaje(String mensaje) {
        area.append(mensaje + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }

    // Helpers
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
