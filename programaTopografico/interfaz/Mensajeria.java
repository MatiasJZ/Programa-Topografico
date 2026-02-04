package interfaz;

import java.awt.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import comunicaciones.ComunicacionIP;

public class Mensajeria extends JPanel {

    private static final long serialVersionUID = 6622634837519332429L;

    private ComunicacionIP com;
    private JTextArea txtMensaje;
    private JTextArea txtLogChat;

    public Mensajeria() {

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

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

        JButton btnEnviarLateral = new JButton("ENVIAR");
        btnEnviarLateral.setBackground(new Color(242, 37, 37));
        btnEnviarLateral.setForeground(Color.BLACK);
        btnEnviarLateral.setFont(new Font("Arial", Font.BOLD, 14));
        btnEnviarLateral.setPreferredSize(new Dimension(160, 40));
        // sin lógica por ahora
        panelFormularios.add(btnEnviarLateral);

        add(panelFormularios, BorderLayout.WEST);

        // PANEL CENTRAL DIVIDIDO EN DOS
        JPanel panelCentral = new JPanel(new GridLayout(4, 1));
        panelCentral.setBackground(Color.BLACK);
        add(panelCentral, BorderLayout.CENTER);

        // MITAD SUPERIOR — ESCRITURA
        JPanel panelEscritura = new JPanel(new BorderLayout());
        panelEscritura.setBorder(crearBordeTitulo("ESCRIBIR MENSAJE MANUAL"));
        panelEscritura.setBackground(Color.BLACK);

        txtMensaje = new JTextArea();
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setBackground(new Color(143, 140, 140));
        txtMensaje.setFont(new Font("Arial", Font.PLAIN, 24));

        JScrollPane scrollEscritura = new JScrollPane(txtMensaje);

        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 5, 5));
        panelBotones.setBackground(Color.BLACK);

        JButton btnEnviarChat = new JButton("ENVIAR");
        btnEnviarChat.setBackground(new Color(60, 130, 255));
        btnEnviarChat.setForeground(Color.WHITE);
        btnEnviarChat.setFont(new Font("Arial", Font.BOLD, 16));
        btnEnviarChat.addActionListener(e -> enviarChat());

        JButton btnEnviarArchivo = new JButton("📎 ARCHIVO");
        btnEnviarArchivo.setBackground(new Color(90, 90, 90));
        btnEnviarArchivo.setForeground(Color.WHITE);
        btnEnviarArchivo.setFont(new Font("Arial", Font.BOLD, 14));
        btnEnviarArchivo.addActionListener(e -> enviarArchivo());

        panelBotones.add(btnEnviarArchivo);
        panelBotones.add(btnEnviarChat);

        panelEscritura.add(panelBotones, BorderLayout.EAST);

        panelEscritura.add(scrollEscritura, BorderLayout.CENTER);

        panelCentral.add(panelEscritura);

        // MITAD INFERIOR — LOG MANUAL
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(crearBordeTitulo("LOG COMUNICACIONES MANUALES"));
        panelLog.setBackground(Color.BLACK);

        txtLogChat = new JTextArea();
        txtLogChat.setEditable(false);
        txtLogChat.setBackground(new Color(20, 20, 20));
        txtLogChat.setForeground(Color.GREEN);
        txtLogChat.setFont(new Font("Consolas", Font.PLAIN, 25));

        JScrollPane scrollLog = new JScrollPane(txtLogChat);
        panelLog.add(scrollLog, BorderLayout.CENTER);

        panelCentral.add(panelLog);
    }

    public void setComunicacion(ComunicacionIP c) {
        this.com = c;
    }
    
    private void enviarArchivo() {

        if (com == null) {
            agregarLog("[ERROR] Comunicacion IP no configurada.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo a enviar");

        int r = fc.showOpenDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();

        int conf = JOptionPane.showConfirmDialog(
                this,
                "Enviar archivo:\n" + f.getName() + "?",
                "Confirmar envío",
                JOptionPane.YES_NO_OPTION
        );

        if (conf != JOptionPane.YES_OPTION) return;

        agregarLog("[TX-FILE] " + f.getName());

        for (String ip : com.getDestinos()) {
            com.enviarArchivo(ip, f);
        }
    }


    private void enviarChat() {
        if (com == null) {
            agregarLog("[ERROR] Comunicacion IP no configurada.");
            return;
        }

        String msg = txtMensaje.getText().trim();
        if (msg.isEmpty()) return;

        com.enviarATodos("CHAT|" + msg);
        agregarLog("[TX] " + msg);
        txtMensaje.setText("");
    }

    public void recibirChat(String msg) {
        agregarLog("[RX] " + msg);
    }

    private void agregarLog(String texto) {
        txtLogChat.append(texto + "\n");
        txtLogChat.setCaretPosition(txtLogChat.getDocument().getLength());
    }
    
    private TitledBorder crearBordeTitulo(String titulo) {
        Font fuente = new Font("Arial", Font.BOLD, 18);
        TitledBorder borde = BorderFactory.createTitledBorder(titulo);
        borde.setTitleColor(Color.WHITE);
        borde.setTitleFont(fuente);
        return borde;
    }

    private void abrirDialogo(String nombreFormulario) {
        JDialog dialog = new JDialog((Frame) null, nombreFormulario, true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.add(new JLabel("Formulario: " + nombreFormulario, SwingConstants.CENTER));
        dialog.setVisible(true);
    }
}
