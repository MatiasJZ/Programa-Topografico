import java.awt.*;
import javax.swing.*;

public class InterfazUsuario extends JFrame{
 
    private static final long serialVersionUID = 1L;
    private final CardLayout cardLayout;
    private final JPanel contenedor;

    // Guardamos referencias a cada pantalla
    private final InterfazDeObservador pantallaObservador;
    private final InterfazCDFdeBateria pantallaCDF;
    private final InterfazPieza pantallaPieza;
    private final InterfazDeGTop pantallaGTop;

    public InterfazUsuario() {
        setTitle("INFANTERÍA DE MARINA BNPB");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        // Inicializamos las pantallas
        pantallaObservador = new InterfazDeObservador(this);
        pantallaCDF = new InterfazCDFdeBateria(this);
        pantallaPieza = new InterfazPieza(this);
        pantallaGTop = new InterfazDeGTop(this);

        // Las agregamos al contenedor
        contenedor.add(new PantallaPrincipal(this), "principal");
        contenedor.add(pantallaObservador, "observador");
        contenedor.add(pantallaCDF, "cdf");
        contenedor.add(pantallaPieza, "pieza");
        contenedor.add(pantallaGTop, "gtop");

        setContentPane(contenedor);
        cardLayout.show(contenedor, "principal");
        setVisible(true);
    }

    public void mostrarPantalla(String nombre) {
        cardLayout.show(contenedor, nombre);
    }

    // 🔑 Método para que PantallaPrincipal pueda pedir una pantalla
    public JPanel getPantalla(String nombre){
        return switch (nombre) {
            case "observador" -> pantallaObservador;
            case "cdf" -> pantallaCDF;
            case "pieza" -> pantallaPieza;
            case "gtop" -> pantallaGTop;
            default -> null;
        };
    }

    public InterfazDeObservador getObservador() {
        return pantallaObservador;
    }

    public InterfazCDFdeBateria getCDFBateria() {
        return pantallaCDF;
    }

    public InterfazPieza getPieza() {
        return pantallaPieza;
    }

    public InterfazDeGTop getGT() {
        return pantallaGTop;
    }


    public static void main(String[] args) {
        new InterfazUsuario();
    }
}