package app;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;

public class RenderizadorListas extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                  boolean isSelected, boolean cellHasFocus) {
        
        // El super ya se encarga de llamar a value.toString() automáticamente
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        // Estética común para todo el programa
        label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setBackground(isSelected ? new Color(50, 50, 50) : Color.BLACK);
        
        return label;
    }
}
