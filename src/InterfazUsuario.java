import javax.swing.*;
import java.awt.*;

public class InterfazUsuario extends JFrame{
 
	private static final long serialVersionUID = 1L;
	private CardLayout cardLayout;
    private JPanel contenedor;
    
    public InterfazUsuario() {
        setTitle("INFANTERÍA DE MARINA BNPB");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        contenedor.add(new PantallaPrincipal(this), "principal");
        contenedor.add(new InterfazDeObservador(this), "observador");
        contenedor.add(new InterfazCDFdeBateria(this), "cdf");
        contenedor.add(new InterfazPieza(this), "pieza");
        contenedor.add(new InterfazDeGTop(this), "gtop");

        setContentPane(contenedor);
        cardLayout.show(contenedor, "principal");
        setVisible(true);
    }

    public void mostrarPantalla(String nombre) {
        cardLayout.show(contenedor, nombre);
    }

    public static void main(String[] args) {
        new InterfazUsuario();
    }
}
