import java.awt.*;
import javax.swing.*;

public class InterfazCDFdeBateria extends JPanel {

    private String idCDF;

    public InterfazCDFdeBateria() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 55, 40)); // Verde militar
    }

    public void setID(String id){
        idCDF = id;
    }

    public String getID(){
        return idCDF;
    }
}
