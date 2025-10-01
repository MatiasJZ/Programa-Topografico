import java.awt.Image;
import java.awt.Toolkit;
import java.util.LinkedList;

import javax.swing.*;

public class ObservadorAdelantado {

    public static void main(String[] args) {
    	LinkedList<Blanco> listaDeBlancos = new LinkedList<Blanco>();
        JFrame ventana = new JFrame("Sistema de Artillería de Reconocimiento y Gestión Operacional - OBSERVADOR");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setLocationRelativeTo(null);
        Image icon = Toolkit.getDefaultToolkit().getImage("C:/Users/54293/Desktop/Archivos SARGO/LOGOBIAC.png");
        ventana.setIconImage(icon);
        
        InterfazDeObservador panelObservador = new InterfazDeObservador(listaDeBlancos);
        ventana.setContentPane(panelObservador);
        ventana.setVisible(true);
    }
}
