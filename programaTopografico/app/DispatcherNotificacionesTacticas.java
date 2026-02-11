package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DispatcherNotificacionesTacticas {

    public static void mostrar(String titulo, String mensaje) {
        JDialog dialog = new JDialog((Frame) null, titulo, true);
        dialog.setSize(500, 600); 
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true); // Sin bordes de Windows

        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(Color.BLACK);
        // Borde inicial grueso
        panelPrincipal.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ETIQUETA DE SISTEMA (TITULO)
        JLabel lblHeader = new JLabel(titulo.toUpperCase());
        lblHeader.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblHeader.setForeground(Color.RED);
        gbc.gridy = 0;
        panelPrincipal.add(lblHeader, gbc);

        // SEPARADOR VISUAL
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.RED);
        gbc.gridy = 1;
        panelPrincipal.add(sep, gbc);

        // CUERPO DEL MENSAJE (Identificado explícitamente)
        JLabel lblTag = new JLabel("CONTENIDO DEL MENSAJE:");
        lblTag.setFont(new Font("Monospaced", Font.PLAIN, 18));
        lblTag.setForeground(Color.GRAY);
        gbc.gridy = 2;
        panelPrincipal.add(lblTag, gbc);

        JTextArea txtCuerpo = new JTextArea(mensaje);
        txtCuerpo.setFont(new Font("Monospaced", Font.BOLD, 24));
        txtCuerpo.setForeground(Color.WHITE);
        txtCuerpo.setBackground(Color.BLACK);
        txtCuerpo.setEditable(false);
        txtCuerpo.setLineWrap(true);
        txtCuerpo.setWrapStyleWord(true);
        gbc.gridy = 3;
        panelPrincipal.add(txtCuerpo, gbc);

        // BOTÓN DE CIERRE MINIMALISTA
        JButton btnOk = new JButton("[ CONFIRMAR LECTURA ]");
        btnOk.setFont(new Font("Monospaced", Font.BOLD, 16));
        btnOk.setBackground(Color.BLACK);
        btnOk.setForeground(Color.RED);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        btnOk.setPreferredSize(new Dimension(0, 50));
        btnOk.addActionListener(e -> dialog.dispose());
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 20, 20, 20);
        panelPrincipal.add(btnOk, gbc);

        // LÓGICA DE TITILEO ROJO
        Timer timerTitileo = new Timer(400, new ActionListener() {
            boolean toggle = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toggle) {
                    panelPrincipal.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    lblHeader.setForeground(Color.RED);
                    btnOk.setForeground(Color.RED);
                } else {
                    panelPrincipal.setBorder(BorderFactory.createLineBorder(new Color(60, 0, 0), 3));
                    lblHeader.setForeground(new Color(60, 0, 0));
                    btnOk.setForeground(new Color(60, 0, 0));
                }
                toggle = !toggle;
            }
        });

        dialog.add(panelPrincipal);
        timerTitileo.start();
        dialog.setVisible(true);
        timerTitileo.stop(); // Limpieza al cerrar
    }
}