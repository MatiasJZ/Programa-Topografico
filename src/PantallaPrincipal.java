import javax.swing.*;
import java.awt.*;

class PantallaPrincipal extends JPanel {
	private static final long serialVersionUID = 1L;

    public PantallaPrincipal(InterfazUsuario padre) {
        setLayout(null);
        setBackground(new Color(40, 55, 40)); // Verde militar
        setPreferredSize(new Dimension(1000, 600));

        JLabel titulo = new JLabel("INFANTERÍA DE MARINA BASE NAVAL PUERTO BELGRANO", SwingConstants.CENTER);
        titulo.setBounds(150, 200, 700, 60);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(200, 200, 120));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(30, 50, 30));
        titulo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(titulo);

        // Botones bien centrados y alineados
        int yBoton = 450;
        int ancho = 180;
        int alto = 60;
        int espacio = 56;

        int[] posicionesX = {
            espacio,
            espacio * 2 + ancho,
            espacio * 3 + ancho * 2,
            espacio * 4 + ancho * 3
        };

        JButton btn1 = crearBoton("Observador Adelantado", posicionesX[0], yBoton);
        btn1.addActionListener(e -> padre.mostrarPantalla("observador"));
        add(btn1);

        JButton btn2 = crearBoton("CDF de Batería", posicionesX[1], yBoton);
        btn2.addActionListener(e -> padre.mostrarPantalla("cdf"));
        add(btn2);

        JButton btn3 = crearBoton("Pieza", posicionesX[2], yBoton);
        btn3.addActionListener(e -> padre.mostrarPantalla("pieza"));
        add(btn3);

        JButton btn4 = crearBoton("Grupo Topográfico", posicionesX[3], yBoton);
        btn4.addActionListener(e -> padre.mostrarPantalla("gtop"));
        add(btn4);

        // Marca de agua BIAC
        JLabel marcaAgua = new JLabel("BIAC");
        marcaAgua.setBounds(20, 20, 100, 30);
        marcaAgua.setFont(new Font("Arial", Font.BOLD, 20));
        marcaAgua.setForeground(new Color(100, 120, 100));
        add(marcaAgua);

    }

    private JButton crearBoton(String texto, int x, int y) {
        JButton boton = new JButton(texto);
        boton.setBounds(x, y, 180, 60);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setBackground(new Color(50, 60, 40));
        boton.setForeground(new Color(190, 190, 120));
        boton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        boton.setFocusPainted(false);
        return boton;
    }
}
