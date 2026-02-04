package app;
import javax.swing.*;

public class PopupAlerta {

    public static void mostrar(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(null,
                mensaje,
                titulo,
                JOptionPane.WARNING_MESSAGE);
    }
}
