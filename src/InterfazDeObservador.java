import java.awt.*;
import javax.swing.*;

public class InterfazDeObservador extends JPanel {
    private static final long serialVersionUID = 1L;

    // Campos de texto (puedes usarlos para setear datos)
    private JTextField txtIdOAA;
    private JTextField txtCoords;


    public InterfazDeObservador(InterfazUsuario padre) {
        setLayout(null);
        setBackground(new Color(40, 55, 40)); // Verde militar

        // ===== TÍTULO =====
        JLabel titulo = new JLabel("PANTALLA OAA", SwingConstants.CENTER);
        titulo.setBounds(200, 30, 600, 50);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setForeground(new Color(200, 190, 120));
        add(titulo);

        // ===== BOTÓN VOLVER =====
        JButton volver = new JButton("VOLVER");
        volver.setBounds(400, 400, 180, 50);
        volver.setFont(new Font("Arial", Font.BOLD, 18));
        volver.setBackground(new Color(50, 60, 40));
        volver.setForeground(new Color(190, 190, 120));
        volver.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        volver.setFocusPainted(false);
        volver.addActionListener(e -> padre.mostrarPantalla("principal"));
        add(volver);
    }

    private JTextField crearCampoTexto(int x, int y, int ancho, int alto) {
        JTextField campo = new JTextField();
        campo.setBounds(x, y, ancho, alto);
        campo.setFont(new Font("Arial", Font.PLAIN, 18));
        campo.setBackground(new Color(160, 150, 100));
        campo.setForeground(Color.BLACK);
        campo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        return campo;
    }
}
