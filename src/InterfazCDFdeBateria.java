import java.awt.*;
import javax.swing.*;

public class InterfazCDFdeBateria extends JPanel {

    private String idCDF;

    public InterfazCDFdeBateria(InterfazUsuario padre) {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 55, 40)); // Verde militar
        JButton volver = new JButton("Volver");
        volver.addActionListener(e -> padre.mostrarPantalla("principal"));
        add(volver, BorderLayout.SOUTH);
    }

    public void setID(String id){
        idCDF = id;
    }

    public String getID(){
        return idCDF;
    }
}
