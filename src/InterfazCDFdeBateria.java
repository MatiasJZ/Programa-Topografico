import javax.swing.*;
import java.awt.*;

public class InterfazCDFdeBateria extends JPanel {
    public InterfazCDFdeBateria(InterfazUsuario padre) {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 55, 40)); // Verde militar
        JButton volver = new JButton("Volver");
        volver.addActionListener(e -> padre.mostrarPantalla("principal"));
        add(volver, BorderLayout.SOUTH);
    }
}
