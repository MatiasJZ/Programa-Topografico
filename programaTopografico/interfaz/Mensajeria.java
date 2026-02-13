package interfaz;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import comunicaciones.GestorEnlaceOperativo;
import app.ConsolaMensajes;

public class Mensajeria extends JPanel {

    private static final long serialVersionUID = 6622634837519332429L;

    private GestorEnlaceOperativo com;
    private JTextArea txtMensaje;
    private JTextArea txtLogChat;
    private ConsolaMensajes consolaMensajes; 
    
    public Mensajeria() {
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // Inicializamos la consola interna
        consolaMensajes = new ConsolaMensajes();

        // PANEL LATERAL DE FORMULARIOS
        JPanel panelFormularios = new JPanel(new GridLayout(7, 1, 5, 5));
        panelFormularios.setBackground(Color.BLACK);
        panelFormularios.setBorder(crearBordeTitulo("FORMULARIOS"));
        panelFormularios.setPreferredSize(new Dimension(180, 0));

        String[] formularios = {"CHIMENTO", "GRANADA", "APOYO", "FIRECAP", "DISREP", "SHELLREP"};
        for (String nombre : formularios) {
            JButton btn = new JButton(nombre);
            styleMilitarBtn(btn);
            btn.addActionListener(e -> abrirDialogo(nombre));
            panelFormularios.add(btn);
        }

        JButton btnEnviarLateral = new JButton("ENVIAR");
        btnEnviarLateral.setBackground(new Color(242, 37, 37));
        btnEnviarLateral.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormularios.add(btnEnviarLateral);

        add(panelFormularios, BorderLayout.WEST);

        // PANEL CENTRAL (Escritura + Log Manual)
        JPanel panelCentral = new JPanel(new GridLayout(2, 1));
        panelCentral.setBackground(Color.BLACK);

        // Mitad Superior: Escritura
        JPanel panelEscritura = new JPanel(new BorderLayout());
        panelEscritura.setBorder(crearBordeTitulo("ESCRIBIR MENSAJE MANUAL"));
        panelEscritura.setBackground(Color.BLACK);

        txtMensaje = new JTextArea();
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setBackground(new Color(143, 140, 140));
        txtMensaje.setFont(new Font("Arial", Font.PLAIN, 24));
        
        panelEscritura.add(new JScrollPane(txtMensaje), BorderLayout.CENTER);

        JPanel panelBotonesEnvio = new JPanel(new GridLayout(2, 1, 5, 5));
        panelBotonesEnvio.setBackground(Color.BLACK);
        
        JButton btnArchivo = new JButton("ENVIAR ARCHIVO");
        styleMilitarBtn(btnArchivo);
        btnArchivo.addActionListener(e -> enviarArchivo());
        
        JButton btnEnviar = new JButton("ENVIAR CHAT");
        btnEnviar.setBackground(new Color(60, 130, 255));
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.addActionListener(e -> enviarChat());

        panelBotonesEnvio.add(btnArchivo);
        panelBotonesEnvio.add(btnEnviar);
        panelEscritura.add(panelBotonesEnvio, BorderLayout.EAST);

        // Mitad Inferior: Log Manual propio de esta pestaña
        txtLogChat = new JTextArea();
        txtLogChat.setEditable(false);
        txtLogChat.setBackground(new Color(20, 20, 20));
        txtLogChat.setForeground(Color.GREEN);
        txtLogChat.setFont(new Font("Consolas", Font.PLAIN, 20));
        
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(crearBordeTitulo("LOG MANUAL"));
        panelLog.add(new JScrollPane(txtLogChat), BorderLayout.CENTER);

        panelCentral.add(panelEscritura);
        panelCentral.add(panelLog);

        add(panelCentral, BorderLayout.CENTER);
        
        // La consola de mensajes la añadimos al sur o la dejamos disponible para el JSplitPane principal
        consolaMensajes.setPreferredSize(new Dimension(0, 150));
        add(consolaMensajes, BorderLayout.SOUTH);
    }

    public ConsolaMensajes getConsolaMensajes() {
        return consolaMensajes;
    }

    public void setComunicacion(GestorEnlaceOperativo c) {
        this.com = c;
    }

    private void enviarChat() {
        String msg = txtMensaje.getText().trim();
        if (msg.isEmpty() || com == null) return;

        com.enviarATodos("CHAT|" + msg);
        consolaMensajes.mostrarTx(msg); // Se muestra en la consola global
        agregarLogLocal("[TX] " + msg); // Se muestra en el log de esta pestaña
        txtMensaje.setText("");
    }
    
    public void recibirChat(String msg) {
        if (!msg.contains("|")) {
            if (consolaMensajes != null) consolaMensajes.mostrarRx(msg);
            agregarLogLocal("[RX] " + msg);
        }
    }

    public void recibirMensajeGlobal(String origen, String msg) {
        consolaMensajes.mostrarRx(origen + ": " + msg);
        agregarLogLocal("[RX de " + origen + "] " + msg);
    }

    private void agregarLogLocal(String texto) {
        txtLogChat.append(texto + "\n");
        txtLogChat.setCaretPosition(txtLogChat.getDocument().getLength());
    }

    private void styleMilitarBtn(JButton b) {
        b.setBackground(new Color(60, 60, 60));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private TitledBorder crearBordeTitulo(String titulo) {
        TitledBorder borde = BorderFactory.createTitledBorder(titulo);
        borde.setTitleColor(Color.WHITE);
        borde.setTitleFont(new Font("Arial", Font.BOLD, 16));
        return borde;
    }

    private void enviarArchivo() {
        if (com == null) return;
        FileDialog fd = new FileDialog((Frame)null, "Enviar Archivo", FileDialog.LOAD);
        fd.setVisible(true);
        if (fd.getFile() != null) {
            File f = new File(fd.getDirectory() + fd.getFile());
            com.enviarArchivo(com.getDestinos().get(0), f); // Ejemplo
            consolaMensajes.mostrarEstado("Archivo enviado: " + f.getName());
        }
    }

    private void abrirDialogo(String nombre) {
        consolaMensajes.mostrarEstado("Abriendo formulario: " + nombre);
    }
}