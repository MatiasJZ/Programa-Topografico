import java.awt.*;
import javax.swing.*;

class PedidoDeFuego extends JPanel {
    public PedidoDeFuego() {
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("PEDIDO DE FUEGO");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        add(titulo, BorderLayout.NORTH);
    }
}
