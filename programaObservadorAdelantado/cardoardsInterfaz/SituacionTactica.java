import java.awt.*;
import javax.swing.*;

// Cardboard: Situación Táctica
public class SituacionTactica extends JPanel {
    public SituacionTactica() {
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("SITUACION TACTICA");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        add(titulo, BorderLayout.NORTH);
    }
}