package app;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import comunicaciones.ConsolaMensajes;
import gestores.GestorEnlaceOperativo;
import util.FabricaComponentes;

/**
 * La clase {@code Mensajeria} representa un panel de mensajería para una interfaz gráfica Swing,
 * permitiendo el envío y recepción de mensajes de chat, archivos y el acceso a formularios predefinidos.
 * 
 * <p>Incluye:
 * <ul>
 *   <li>Panel lateral con botones para abrir diferentes formularios y enviar mensajes.</li>
 *   <li>Área central para escribir mensajes manualmente y visualizar un log local de mensajes enviados y recibidos.</li>
 *   <li>Consola de mensajes global para mostrar transmisiones y estados generales.</li>
 * </ul>
 * 
 * <p>Permite la integración con un {@code GestorEnlaceOperativo} para gestionar la comunicación real,
 * y utiliza una instancia de {@code ConsolaMensajes} para mostrar mensajes globales.
 * 
 * <p>Principales funcionalidades:
 * <ul>
 *   <li>Enviar mensajes de chat a todos los destinos.</li>
 *   <li>Recibir y mostrar mensajes de chat y mensajes globales.</li>
 *   <li>Enviar archivos a un destino seleccionado.</li>
 *   <li>Acceso rápido a formularios específicos mediante botones laterales.</li>
 *   <li>Registro local y global de mensajes transmitidos y recibidos.</li>
 * </ul>
 * 
 * <p>Esta clase está diseñada para ser utilizada como parte de una interfaz de usuario más amplia,
 * proporcionando capacidades de mensajería y registro de eventos en aplicaciones topográficas o militares.
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */

public class Mensajeria extends JPanel {

    private static final long serialVersionUID = 6622634837519332429L;

    private GestorEnlaceOperativo com;
    private ConsolaMensajes consolaMensajes;

    private JTextArea txtMensaje;
    private JTextArea txtLogChat;
    protected String idOAA;

    private final Color COLOR_FONDO = new Color(18, 18, 18);
    
    public Mensajeria(String idOAA) {
        setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());
        
        this.idOAA = idOAA;

        consolaMensajes = new ConsolaMensajes();
        
        consolaMensajes.setBackground(new Color(10, 10, 10));
        consolaMensajes.setPreferredSize(new Dimension(0, 150)); 

        JPanel panelFormularios = new JPanel(new GridLayout(7, 1, 5, 5));
        panelFormularios.setBackground(COLOR_FONDO);
        
        panelFormularios.setBorder(new CompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder(
                        new LineBorder(Color.GRAY), "FORMULARIOS", 0, 0, new Font("Arial", Font.BOLD, 12), Color.GRAY)
        ));
        panelFormularios.setPreferredSize(new Dimension(180, 0));

        String[] formularios = {"CHIMENTO", "GRANADA", "APOYO", "FIRECAP", "DISREP", "SHELLREP"};
        for (String nombre : formularios) {
            JButton btn = new JButton(nombre);
            FabricaComponentes.estilizarBotonLateral(btn, new Color(60, 60, 60));
            btn.addActionListener(e -> abrirDialogo(nombre));
            panelFormularios.add(btn);
        }

        JButton btnEnviarLateral = new JButton("ENVIAR FORM");
        FabricaComponentes.estilizarBotonLateral(btnEnviarLateral, new Color(180, 40, 40));
        panelFormularios.add(btnEnviarLateral);

        add(panelFormularios, BorderLayout.WEST);
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(COLOR_FONDO);
        panelCentral.setBorder(new EmptyBorder(10, 0, 10, 10)); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.weighty = 1.0;
        
        JPanel panelLogContainer = new JPanel(new BorderLayout());
        panelLogContainer.setBackground(COLOR_FONDO);
        panelLogContainer.setBorder(FabricaComponentes.crearBordeTactico("REGISTRO VISUAL DE CHAT"));

        txtLogChat = new JTextArea();
        FabricaComponentes.configurarTextAreaLog(txtLogChat);

        JScrollPane scrollLog = new JScrollPane(txtLogChat);
        scrollLog.setBorder(null); 
        scrollLog.getViewport().setBackground(new Color(10, 10, 12));
        
        panelLogContainer.add(scrollLog, BorderLayout.CENTER);
        panelCentral.add(panelLogContainer, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.0; 
        gbc.insets = new Insets(10, 0, 0, 0); 

        JPanel panelInputContainer = new JPanel(new BorderLayout(10, 0));
        panelInputContainer.setBackground(COLOR_FONDO);
        panelInputContainer.setBorder(FabricaComponentes.crearBordeTactico("ENTRADA DE MENSAJE"));
        
        panelInputContainer.setPreferredSize(new Dimension(0, 200)); 

        txtMensaje = new JTextArea();
        FabricaComponentes.configurarInputChat(txtMensaje); 
        
        InputMap inputMap = txtMensaje.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = txtMensaje.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "enviar");
        inputMap.put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
        actionMap.put("enviar", new AbstractAction() {
			private static final long serialVersionUID = 375201203426850616L;

			@Override
            public void actionPerformed(ActionEvent e) {
                enviarChat();
            }
        });

        JScrollPane scrollInput = new JScrollPane(txtMensaje);
        scrollInput.setBorder(null);
        panelInputContainer.add(scrollInput, BorderLayout.CENTER);

        JPanel panelBotonesEnvio = new JPanel(new GridLayout(2, 1, 0, 5));
        panelBotonesEnvio.setBackground(COLOR_FONDO);
        panelBotonesEnvio.setPreferredSize(new Dimension(120, 0));

        Font fuenteEmoji = new Font("Segoe UI Emoji", Font.PLAIN, 24); 
        
        JButton btnAdjunto = new JButton();
        btnAdjunto.setText("<html><center><font size='6'>\u2795</font><br><font size='3'>FILE</font></center></html>");
        FabricaComponentes.estilizarBotonAccion(btnAdjunto, new Color(70, 70, 70));
       
        btnAdjunto.setFont(fuenteEmoji); 
        btnAdjunto.addActionListener(e -> enviarArchivo());

        JButton btnEnviar = new JButton();
        btnEnviar.setText("<html><center><font size='6'>\u2708</font><br><font size='3'>ENVIAR</font></center></html>");
        FabricaComponentes.estilizarBotonAccion(btnEnviar, new Color(60, 130, 255)); 

        btnEnviar.setFont(fuenteEmoji);        
        btnEnviar.addActionListener(e -> enviarChat());

        panelBotonesEnvio.add(btnAdjunto);
        panelBotonesEnvio.add(btnEnviar);

        panelInputContainer.add(panelBotonesEnvio, BorderLayout.EAST);
        panelCentral.add(panelInputContainer, gbc);

        add(panelCentral, BorderLayout.CENTER);

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
        
        if (msg.isEmpty()) {
            txtMensaje.setText("");
            txtMensaje.requestFocusInWindow();
            return;
        }

        if (com == null) {
            agregarLogLocal("[SISTEMA] ERROR: Enlace IP no activo.");
            return;
        }
        com.enviarATodos("CHAT|" + msg);
        
        // Uso del nuevo método de la consola de mensajes
        consolaMensajes.mostrarTx("TODOS", "CHAT: " + msg); 
        
        agregarLogLocal("["+idOAA+"] " + msg); 
        
        // Limpiar
        txtMensaje.setText("");
        txtMensaje.requestFocusInWindow();
    }
    
    public void recibirChat(String msg) {
        if (!msg.contains("|")) { 
            String origen = "DESCONOCIDO";
            String contenido = msg;
            
            if(msg.contains("||")) {
                String[] p = msg.split("\\|\\|", 2);
                origen = p[0];
                contenido = p[1];
            }
            
            if (consolaMensajes != null) consolaMensajes.mostrarRx(origen, contenido);
            agregarLogLocal("[RX - " + origen + "] " + contenido);
        }
    }

    public void recibirMensajeGlobal(String origen, String msg) {
        consolaMensajes.mostrarRx(origen, msg);
        agregarLogLocal("[SISTEMA - " + origen + "] " + msg);
    }

    private void agregarLogLocal(String texto) {
        txtLogChat.append(texto + "\n");
        txtLogChat.setCaretPosition(txtLogChat.getDocument().getLength());
    }

    private void enviarArchivo() {
        if (com == null) {
            agregarLogLocal("[SISTEMA] ERROR: Enlace IP no activo.");
            return;
        }
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        
        FileDialog fd = new FileDialog(parent, "SELECCIONAR ARCHIVO TÁCTICO", FileDialog.LOAD);
        fd.setVisible(true); 

        if (fd.getFile() != null) {
            File f = new File(fd.getDirectory() + fd.getFile());
            
            int opt = JOptionPane.showConfirmDialog(this, 
                    "¿Transmitir archivo: " + f.getName() + "?", 
                    "Confirmar Envío", JOptionPane.YES_NO_OPTION);
            
            if (opt == JOptionPane.YES_OPTION) {
                if (!com.getDestinos().isEmpty()) {
                    for(String ip : com.getDestinos()) {
                        com.enviarArchivo(ip, f);
                    }
                    if (consolaMensajes != null) consolaMensajes.mostrarTx("TODOS", "Archivo enviado: " + f.getName());
                    agregarLogLocal("[TX-FILE] " + f.getName());
                } else {
                    agregarLogLocal("[ERROR] No hay destinos conectados.");
                }
            }
        }
    }

    private void abrirDialogo(String nombre) {
        JOptionPane.showMessageDialog(this, "Formulario " + nombre + " en construcción.");
    }
}