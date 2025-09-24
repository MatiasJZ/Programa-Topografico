import java.util.LinkedList;

import javax.swing.*;

public class ObservadorAdelantado {

    public static void main(String[] args) {
    	LinkedList<Blanco> listaDeBlancos = new LinkedList<Blanco>();
        JFrame ventana = new JFrame("SARGO (Sistema de Artillería de Reconocimiento y Gestión Operacional) - OBSERVADOR");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setLocationRelativeTo(null);

        InterfazDeObservador panelObservador = new InterfazDeObservador(listaDeBlancos);
        ventana.setContentPane(panelObservador);
        ventana.setVisible(true);
    }
}
