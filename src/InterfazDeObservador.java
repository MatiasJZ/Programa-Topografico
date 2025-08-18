import java.awt.*;
import javax.swing.*;

public class InterfazDeObservador extends JPanel {
    private static final long serialVersionUID = 1L;

    //VARIABLES QUE EL OAA COMUNICA AL CDF

    private String idOAA;
    private coordenadas coordOAA;
    private coordenadas coordBlanco;
    private String natBlanco; //NATURALEZA DEL BLANCO con un codigo de a lo sumo 5 caracteres
    private String tipoAz; //TIPO DE AZ PARA EL DiSPARO con un codigo de a lo sumo 2 caracteres
    
    //METODOS DE ACCESO A LAS VARIABLES QUE EL OAA ENVIA AL CDF

    public void setID(String id){
        idOAA = id;
    }
    public String getID(){
        return idOAA;
    }
    public void setCoordOAA(coordenadas c){
        coordOAA = c;
    }
    public coordenadas getCoordOAA(){
        return coordOAA;
    }
    public void setCoordBlanco(coordenadas c){
        coordBlanco = c;
    }
    public coordenadas getCoordBlanco(){
        return coordBlanco;
    }
    public void setNatBlanco(String s){
        natBlanco = s;
    }
    public String getNatBlanco(){
        return natBlanco;
    }
    public void setTipoAz(String s){
        tipoAz = s;
    }
    public String getTipoAz(){
        return tipoAz;
    }

    //CONSTRUCTOR DE LA INTERFAZ DEL OAA    

    public InterfazDeObservador(InterfazUsuario padre) {
        setLayout(null);
        setBackground(new Color(40, 55, 40)); // Verde militar

        // ===== TÍTULO =====
        JLabel titulo = new JLabel("PANTALLA OAA", SwingConstants.CENTER);
        titulo.setBounds(200, 30, 600, 50);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setForeground(new Color(200, 190, 120));
        add(titulo);

        // ===== BOTÓN VOLVER =====
        JButton volver = new JButton("VOLVER");
        volver.setBounds(400, 400, 180, 50);
        volver.setFont(new Font("Arial", Font.BOLD, 18));
        volver.setBackground(new Color(50, 60, 40));
        volver.setForeground(new Color(190, 190, 120));
        volver.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        volver.setFocusPainted(false);
        volver.addActionListener(e -> padre.mostrarPantalla("principal"));
        add(volver);
    }

    private JTextField crearCampoTexto(int x, int y, int ancho, int alto) {
        JTextField campo = new JTextField();
        campo.setBounds(x, y, ancho, alto);
        campo.setFont(new Font("Arial", Font.PLAIN, 18));
        campo.setBackground(new Color(160, 150, 100));
        campo.setForeground(Color.BLACK);
        campo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        return campo;
    }
    
}
