import java.awt.*;
import javax.swing.*;

public class InterfazPieza extends JPanel {
    
    private String idPieza;

    public InterfazPieza(InterfazUsuario padre) {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 55, 40)); // Verde militar
        JButton volver = new JButton("Volver");
        volver.addActionListener(e -> padre.mostrarPantalla("principal"));
        add(volver, BorderLayout.SOUTH);
    }

    public void setID(String id){
        idPieza = id;
    }

    public String getID(){
        return idPieza;
    }
}
