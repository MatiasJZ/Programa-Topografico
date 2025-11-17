package interfaz;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Mensajeria extends JPanel {

    public Mensajeria() {
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // panel formularios
        JPanel panelFormularios = new JPanel(new GridLayout(7, 1, 5, 5));
        panelFormularios.setBackground(Color.BLACK);
        panelFormularios.setBorder(crearBordeTitulo("FORMULARIOS"));
        panelFormularios.setPreferredSize(new Dimension(180, 0)); 

        String[] formularios = {"CHIMENTO", "GRANADA", "APOYO", "FIRECAP", "DISREP", "SHELLREP"};

        for (String nombre : formularios) {
            JButton btn = new JButton(nombre);
            btn.setBackground(new Color(60, 60, 60));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(160, 40)); 

            btn.addActionListener(e -> abrirDialogo(nombre));

            panelFormularios.add(btn);
        }

        // boton enviar
        JButton btnEnviar = new JButton("ENVIAR");
        btnEnviar.setBackground(new Color(242, 37, 37));
        btnEnviar.setForeground(Color.BLACK);
        btnEnviar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEnviar.setPreferredSize(new Dimension(160, 40));
        panelFormularios.add(btnEnviar);

        add(panelFormularios, BorderLayout.WEST);

        // panel tactical chat
        JPanel panelChat = new JPanel(new BorderLayout());
        panelChat.setBackground(Color.BLACK);
        panelChat.setBorder(crearBordeTitulo("TACTICAL CHAT"));

        JPanel panelPara = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelPara.setBackground(Color.BLACK);
        JLabel lblPara = new JLabel("PARA:");
        lblPara.setForeground(Color.WHITE);
        lblPara.setFont(new Font("Arial", Font.BOLD, 16));

        JComboBox<String> comboPara = new JComboBox<>(new String[]{"CDF"});
        comboPara.setPreferredSize(new Dimension(200, 28));

        panelPara.add(lblPara);
        panelPara.add(comboPara);
        panelChat.add(panelPara, BorderLayout.NORTH);

        // area de texto para mensajes 
        JTextArea txtMensaje = new JTextArea();
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setBackground(new Color(143,140,140));
        txtMensaje.setFont(new Font("Arial", Font.PLAIN, 30));

        JScrollPane scroll = new JScrollPane(txtMensaje);
        panelChat.add(scroll, BorderLayout.CENTER);

        add(panelChat, BorderLayout.CENTER);
    }

    // metodo para abrir un JDialog de cada formulario
    private void abrirDialogo(String nombreFormulario) {
        JDialog dialog = new JDialog((Frame) null, nombreFormulario, true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.add(new JLabel("Formulario: " + nombreFormulario, SwingConstants.CENTER));
        dialog.setVisible(true);
    }

    // metodo para crear bordes con títulos grandes y blancos
    private TitledBorder crearBordeTitulo(String titulo) {
        Font fuente = new Font("Arial", Font.BOLD, 18);
        TitledBorder borde = BorderFactory.createTitledBorder(titulo);
        borde.setTitleColor(Color.WHITE);
        borde.setTitleFont(fuente);
        return borde;
    }
}
