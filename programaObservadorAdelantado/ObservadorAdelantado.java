import javax.swing.*;

public class ObservadorAdelantado {

    public static void main(String[] args) {
        JFrame ventana = new JFrame("SARGO (Sistema de Artillería de Reconocimiento y Gestión Operacional) - OBSERVADOR");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1000, 600);
        ventana.setLocationRelativeTo(null);

        // Crear e insertar la interfaz del observador
        InterfazDeObservador panelObservador = new InterfazDeObservador();
        ventana.setContentPane(panelObservador);
        ventana.setVisible(true);
    }
}
