package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * DispatcherNotificacionesTacticas
 * 
 * A utility class for displaying tactical notification dialogs with a distinctive
 * dark theme and red flashing border animation.
 * 
 * This class provides a static method to show modal dialog windows with a tactical
 * aesthetic, featuring a black background, red accents, and a blinking border effect.
 * The dialogs are undecorated (no system window borders) and centered on screen.
 * 
 * Visual Features:
 * - Black background with red 3-pixel border
 * - Red flashing animation on the border and header text (400ms interval)
 * - Monospaced font typography for a technical appearance
 * - Non-editable text area for message content
 * - Confirmation button to close the dialog
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 * @since [Date]
 */
public class DispatcherNotificacionesTacticas {

    /**
     * Displays a modal dialog with a tactical notification.
     * 
     * Creates and shows a centered, undecorated dialog window containing:
     * - A title header (displayed in uppercase, red color)
     * - A visual separator line
     * - A content label and message text area
     * - A confirmation button to dismiss the dialog
     * - A red flashing animation that cycles every 400 milliseconds
     * 
     * The dialog is modal, blocking interaction with other windows until closed.
     * The message text wraps automatically and is displayed in a non-editable format.
     * 
     * @param titulo the title text displayed at the top of the dialog (converted to uppercase)
     * @param mensaje the main message content displayed in the text area
     */
    public static void mostrar(String titulo, String mensaje) {
        // Implementation...
    }
}
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