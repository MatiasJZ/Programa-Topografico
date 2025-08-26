import java.awt.*;
import javax.swing.*;

class Mensajeria extends JPanel {
    public Mensajeria() {
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("MENSAJERIA");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        add(titulo, BorderLayout.NORTH);
    }
}