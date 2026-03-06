package comunicaciones;

import javax.swing.*;

import gestores.GestorEnlaceOperativo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * DispatcherNotificacionesTacticas
 * * A utility class for displaying tactical notification dialogs with a distinctive
 * dark theme and red flashing border animation.
 * * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class DispatcherNotificacionesTacticas {

    /**
     * Displays a modal dialog with a tactical notification.
     * * @param titulo  the title text displayed at the top of the dialog
     * @param mensaje the main message content displayed in the text area
     */
    public static void mostrar(String titulo, String mensaje) {
        mostrar(titulo, mensaje, null, null);
    }

    public static void mostrar(String titulo, String mensaje, String ipRemota, GestorEnlaceOperativo comunicacionIP) {
        
        JDialog dialog = new JDialog((Frame) null, "Notificación Táctica", true);
        dialog.setUndecorated(true); 
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(null); 

        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(Color.BLACK);
        panelPrincipal.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 5, 20);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblHeader = new JLabel(">> " + titulo.toUpperCase() + " <<");
        lblHeader.setFont(new Font("Monospaced", Font.BOLD, 22));
        lblHeader.setForeground(Color.RED);
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 0;
        panelPrincipal.add(lblHeader, gbc);

        JSeparator separador = new JSeparator();
        separador.setForeground(Color.RED);
        separador.setBackground(Color.BLACK);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 15, 0); 
        panelPrincipal.add(separador, gbc);

        JTextArea txtMensaje = new JTextArea(mensaje);
        txtMensaje.setFont(new Font("Monospaced", Font.PLAIN, 16));
        txtMensaje.setForeground(Color.WHITE);
        txtMensaje.setBackground(Color.BLACK);
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setEditable(false);
        txtMensaje.setHighlighter(null); 

        JScrollPane scroll = new JScrollPane(txtMensaje);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.BLACK);
        
        gbc.gridy = 2;
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 30, 0, 30); 
        panelPrincipal.add(scroll, gbc);

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnOk = new JButton("[ CONFIRMAR LECTURA ]");
        btnOk.setFont(new Font("Monospaced", Font.BOLD, 16));
        btnOk.setBackground(Color.BLACK);
        btnOk.setForeground(Color.RED);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        btnOk.setPreferredSize(new Dimension(0, 50));
        
        btnOk.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnOk.setBackground(new Color(40, 0, 0));
            }
            public void mouseExited(MouseEvent evt) {
                btnOk.setBackground(Color.BLACK);
            }
        });

        btnOk.addActionListener(e -> {
            dialog.dispose();
            if (comunicacionIP != null && ipRemota != null && !ipRemota.isEmpty()) {
                String miIp = "DESCONOCIDA";
                if (comunicacionIP.getInterfazLocal() != null) {
                    miIp = comunicacionIP.getInterfazLocal().getHostAddress();
                }
                String acuse = "ACK|MSG=Operador en IP " + miIp + " confirma la lectura de: " + titulo;
                comunicacionIP.enviar(ipRemota, acuse);
            }
        });
        
        gbc.gridy = 4; 
        gbc.insets = new Insets(20, 20, 20, 20);
        panelPrincipal.add(btnOk, gbc);

        Timer timerTitileo = new Timer(400, new ActionListener() {
            boolean toggle = true;
            @Override
            public void actionPerformed(ActionEvent e) {            
                if (!dialog.isVisible()) {
                    ((Timer)e.getSource()).stop();
                    return;
                }

                if (toggle) {
                    panelPrincipal.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    lblHeader.setForeground(Color.RED);
                    btnOk.setForeground(Color.RED);
                    btnOk.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                } else {
                    Color rojoOscuro = new Color(80, 0, 0);
                    panelPrincipal.setBorder(BorderFactory.createLineBorder(rojoOscuro, 3));
                    lblHeader.setForeground(rojoOscuro);
                    btnOk.setForeground(rojoOscuro);
                    btnOk.setBorder(BorderFactory.createLineBorder(rojoOscuro, 1));
                }
                toggle = !toggle;
            }
        });

        dialog.add(panelPrincipal);
        
        timerTitileo.start();
        dialog.setVisible(true);
        
        timerTitileo.stop(); 
    }
}