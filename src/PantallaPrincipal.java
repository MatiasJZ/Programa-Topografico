import java.awt.*;      // Importa clases para gráficos y componentes GUI
import javax.swing.*;   // Importa clases de Swing para ventanas y paneles

// Clase que representa la pantalla principal del sistema
class PantallaPrincipal extends JPanel {
    private static final long serialVersionUID = 1L; // ID de versión para serialización

    // Constructor de la pantalla principal, recibe la ventana padre
    public PantallaPrincipal(InterfazUsuario padre) {
        setLayout(null); // Se usa layout absoluto para posicionar componentes manualmente
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1000, 600)); // Tamaño preferido del panel

        // Label principal con título
        JLabel titulo = new JLabel("BATALLON DE ARTILLERIA DE CAMPAÑA N°1", SwingConstants.CENTER);
        titulo.setBounds(150, 200, 700, 60); // Posición y tamaño
        titulo.setFont(new Font("Arial", Font.BOLD, 20)); // Fuente y tamaño
        titulo.setForeground(new Color(200, 200, 120)); // Color del texto
        titulo.setOpaque(true); // Permite establecer color de fondo
        titulo.setBackground(new Color(30, 50, 30)); // Fondo del label
        titulo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Borde negro
        add(titulo); // Agrega el label al panel

        // Configuración de botones
        int yBoton = 450; // Posición vertical
        int ancho = 180;  // Ancho de los botones
        int espacio = 56; // Espacio entre botones

        // Calcula posición horizontal de cada botón
        int[] posicionesX = {
            espacio,
            espacio * 2 + ancho,
            espacio * 3 + ancho * 2,
            espacio * 4 + ancho * 3
        };

        // Crear botón "Observador Adelantado" y asignar acción
        JButton btn1 = crearBoton("Observador Adelantado", posicionesX[0], yBoton);
        btn1.addActionListener(e -> pedirIdYMostrar(padre, "observador")); // Muestra la pantalla correspondiente
        add(btn1);

        // Crear botón "CDF de Batería"
        JButton btn2 = crearBoton("CDF de Batería", posicionesX[1], yBoton);
        btn2.addActionListener(e -> pedirIdYMostrar(padre, "cdf"));
        add(btn2);

        // Crear botón "Pieza"
        JButton btn3 = crearBoton("Pieza", posicionesX[2], yBoton);
        btn3.addActionListener(e -> pedirIdYMostrar(padre, "pieza"));
        add(btn3);

        // Crear botón "Grupo Topográfico"
        JButton btn4 = crearBoton("Grupo Topográfico", posicionesX[3], yBoton);
        btn4.addActionListener(e -> pedirIdYMostrar(padre, "gtop"));
        add(btn4);

        // Label de marca de agua "BIAC"
        JLabel marcaAgua = new JLabel("COIN/BRIM");
        marcaAgua.setBounds(20, 20, 150, 30); // Posición y tamaño
        marcaAgua.setFont(new Font("Arial", Font.BOLD, 20)); // Fuente y tamaño
        marcaAgua.setForeground(new Color(100, 120, 100)); // Color del texto
        add(marcaAgua); // Agrega la marca al panel
    }

    // Método auxiliar para crear botones con estilo uniforme
    private JButton crearBoton(String texto, int x, int y) {
        JButton boton = new JButton(texto);
        boton.setBounds(x, y, 180, 60); // Posición y tamaño
        boton.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente del texto
        boton.setBackground(new Color(50, 60, 40)); // Fondo del botón
        boton.setForeground(new Color(190, 190, 120)); // Color del texto
        boton.setBorder(BorderFactory.createLineBorder(new Color(144, 238, 144), 3)); // Borde negro
        boton.setFocusPainted(false); // Quita el borde de enfoque al hacer click
        return boton;
    }

    // Método que pide el ID al usuario y muestra la pantalla correspondiente
    private void pedirIdYMostrar(InterfazUsuario padre, String nombrePantalla) {
        // Muestra un cuadro de diálogo para ingresar el ID
        String id = JOptionPane.showInputDialog(this, "Ingrese su ID: ", "Autenticación",
                JOptionPane.QUESTION_MESSAGE);

        if (id != null && !id.trim().isEmpty()) { // Si se ingresó un ID válido
            JPanel panel = padre.getPantalla(nombrePantalla); // Obtiene la pantalla correspondiente

            // Según el tipo de panel, se asigna el ID
            switch (panel) {
                case InterfazDeObservador interfazDeObservador -> interfazDeObservador.setID(id);
                case InterfazCDFdeBateria interfazCDFdeBateria -> interfazCDFdeBateria.setID(id);
                case InterfazPieza interfazPieza -> interfazPieza.setID(id);
                default -> {
                    // No hace nada si el panel no coincide
                }
            }
            padre.mostrarPantalla(nombrePantalla); // Muestra la pantalla en la interfaz principal
        }
    }
}
