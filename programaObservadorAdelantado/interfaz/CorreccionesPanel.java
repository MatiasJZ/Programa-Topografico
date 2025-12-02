package interfaz;
import javax.swing.*;
import java.awt.*;

public class CorreccionesPanel extends JPanel {

	private static final long serialVersionUID = -271089343555480088L;
	private JComboBox<String> cbDireccion;
    private JComboBox<String> cbAlcance;
    private JComboBox<String> cbAltura;

    private JTextField txtDirValor;
    private JTextField txtAlcValor;
    private JTextField txtAltValor;

    private JButton btnNuevoPIF;
    private JButton btnFinMision;
    private JButton btnEnviar;
    private JButton btnVolver;

    private JLabel lblUltima;

    public CorreccionesPanel() {

        setLayout(null);
        setBackground(new Color(0,0,0));

        Font font = new Font("Arial", Font.BOLD, 16);

        JLabel l1 = new JLabel("EN DIRECCIÓN");
        JLabel l2 = new JLabel("EN ALCANCE");
        JLabel l3 = new JLabel("EN ALTURA");
        JLabel l4 = new JLabel("EFICACIA");
        JLabel l5 = new JLabel("ULTIMA CORRECCIÓN:");

        for (JLabel l : new JLabel[]{l1,l2,l3,l4,l5}) {
            l.setForeground(Color.WHITE);
            l.setFont(font);
            add(l);
        }

        cbDireccion = new JComboBox<>(new String[]{"IZQUIERDA","DERECHA"});
        cbAlcance = new JComboBox<>(new String[]{"ALARGAR","ACORTAR"});
        cbAltura = new JComboBox<>(new String[]{"SUBIR","BAJAR"});

        JComboBox<?>[] combos = {cbDireccion,cbAlcance,cbAltura};
        for (JComboBox<?> cb : combos) {
            cb.setBackground(new Color(20,40,80));
            cb.setForeground(Color.WHITE);
            cb.setFont(font);
            cb.setFocusable(false);
            add(cb);
        }

        txtDirValor = new JTextField("10");
        txtAlcValor = new JTextField("10");
        txtAltValor = new JTextField("10");

        for (JTextField t : new JTextField[]{txtDirValor,txtAlcValor,txtAltValor}) {
            t.setBackground(new Color(40,80,120));
            t.setForeground(Color.WHITE);
            t.setHorizontalAlignment(SwingConstants.CENTER);
            t.setFont(font);
            add(t);
        }

        JLabel u1 = new JLabel("Mts");
        JLabel u2 = new JLabel("Mts");
        JLabel u3 = new JLabel("Mts");
        for (JLabel u : new JLabel[]{u1,u2,u3}) {
            u.setForeground(new Color(200,200,200));
            u.setFont(font);
            add(u);
        }

        btnNuevoPIF = new JButton("NUEVO PIF");
        btnFinMision = new JButton("FIN DE MISION");
        btnEnviar = new JButton("ENVIAR");

        JButton[] bs = {btnNuevoPIF,btnFinMision,btnEnviar};

        btnNuevoPIF.setBackground(new Color(200,255,200));
        btnFinMision.setBackground(Color.ORANGE);
        btnEnviar.setBackground(new Color(220,180,0));
        

        for (JButton b : bs) {
            b.setForeground(Color.BLACK);
            b.setFont(new Font("Arial", Font.BOLD, 18));
            b.setFocusPainted(false);
            add(b);
        }

        lblUltima = l5;

        int x1 = 40;
        int x2 = 260;
        int x3 = 460;
        int x4 = 550;

        l1.setBounds(x1, 40, 200, 28);
        cbDireccion.setBounds(x2, 40, 180, 32);
        txtDirValor.setBounds(x3, 40, 60, 32);
        u1.setBounds(x4, 40, 60, 32);

        l2.setBounds(x1, 100, 200, 28);
        cbAlcance.setBounds(x2, 100, 180, 32);
        txtAlcValor.setBounds(x3, 100, 60, 32);
        u2.setBounds(x4, 100, 60, 32);

        l3.setBounds(x1, 160, 200, 28);
        cbAltura.setBounds(x2, 160, 180, 32);
        txtAltValor.setBounds(x3, 160, 60, 32);
        u3.setBounds(x4, 160, 60, 32);

        l4.setBounds(x1, 220, 200, 28);
        l5.setBounds(x1, 260, 500, 28);

        btnNuevoPIF.setBounds(40, 340, 180, 45);
        btnFinMision.setBounds(240, 340, 180, 45);
        btnEnviar.setBounds(440, 340, 180, 45);

        btnVolver = new JButton("VOLVER");
        btnVolver.setBounds(40, 400, 180, 40);
        btnVolver.setBackground(new Color(60,60,60));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Arial", Font.BOLD, 18));
        btnVolver.setFocusPainted(false);
        add(btnVolver);
    }

    public JButton getBtnEnviar() { return btnEnviar; }
    public JButton getBtnFin() { return btnFinMision; }
    public JButton getBtnNuevoPIF() { return btnNuevoPIF; }
    public JButton getBtnVolver() { return btnVolver; }

    public JComboBox<String> getCbDireccion() { return cbDireccion; }
    public JComboBox<String> getCbAlcance() { return cbAlcance; }
    public JComboBox<String> getCbAltura() { return cbAltura; }

    public JTextField getTxtDirValor() { return txtDirValor; }
    public JTextField getTxtAlcValor() { return txtAlcValor; }
    public JTextField getTxtAltValor() { return txtAltValor; }

    public JLabel getLblUltima() { return lblUltima; }
}
