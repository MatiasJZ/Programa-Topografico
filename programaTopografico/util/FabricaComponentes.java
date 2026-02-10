package util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import dominio.Blanco;
import dominio.Posicionable;
import dominio.Punto;

public class FabricaComponentes {

	private FabricaComponentes() {
	    throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada.");
	}

	public static JLabel crearEtiqueta(String texto, Font fuente) {
        JLabel label = new JLabel(texto);
        label.setFont(fuente);
        label.setForeground(new Color(180, 180, 180));
        return label;
    }

	public static JTextField crearCampoTexto(Font fuente) {
        JTextField tf = new JTextField();
        tf.setFont(fuente);
        tf.setBackground(new Color(50, 50, 50));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        tf.setPreferredSize(new Dimension(200, 45));
        return tf;
    }
    
    public static JComboBox<Posicionable> crearComboPuntosYBlancos(Font fuente, LinkedList<Punto> l1,LinkedList<Blanco> l2) {
        JComboBox<Posicionable> cb = new JComboBox<Posicionable>();
        for(Punto p : l1) {
        	cb.addItem(p);
        }
        for(Blanco b : l2) {
        	cb.addItem(b);
        }
        cb.setFont(fuente);
        cb.setBackground(new Color(50, 50, 50));
        cb.setForeground(Color.WHITE);
        cb.setPreferredSize(new Dimension(200, 45));
        return cb;
    }
    
    public static void configurarBotonMilitar(JButton btn, Font font, Color accentColor) {
        btn.setFont(font);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(45, 45, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Borde lateral de color (el detalle estético)
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Efecto de hover
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(new Color(60, 60, 60));
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(new Color(45, 45, 45));
            }
        });
    }
    
    public static JPanel crearLinea2(String etiqueta, String valor, Font f) {
        // Usamos FlowLayout con alineación a la izquierda
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linea.setBackground(Color.BLACK);

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(f);
        lblEtiqueta.setForeground(Color.LIGHT_GRAY);
        
        // AUMENTADO: De 180 a 280 para evitar que el texto pise al valor
        // La altura sube a 30 para acomodar el tamaño de la fuente de 21px
        lblEtiqueta.setPreferredSize(new Dimension(280, 30)); 

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(f);
        lblValor.setForeground(Color.WHITE);

        linea.add(lblEtiqueta);
        linea.add(lblValor);
        return linea;
    }

    public static void configurarBotonEstilo(JButton btn, Color colorFondo) {
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(0, 65));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
    }
    
    public static void addPlaceholder(JTextField field, String placeholder){
    	
        Color placeholderColor = new Color(180,180,180);
        Color textColor = Color.WHITE;
        field.setForeground(placeholderColor);
        field.setText(placeholder);
        field.setBackground(new Color(70,70,70));
        field.addFocusListener(new FocusAdapter(){
            @Override
            public void focusGained(FocusEvent e){
                if(field.getText().equals(placeholder)){
                    field.setText("");
                    field.setForeground(textColor);
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                if(field.getText().isEmpty()){
                    field.setForeground(placeholderColor);
                    field.setText(placeholder);
                }
            }
        });
    }
    
    public static void addPlaceholder(JTextArea field, String placeholder){
    	
        Color placeholderColor = new Color(180,180,180);
        Color textColor = Color.WHITE;
        field.setForeground(placeholderColor);
        field.setText(placeholder);
        field.setBackground(new Color(70,70,70));
        field.addFocusListener(new FocusAdapter(){
            @Override
            public void focusGained(FocusEvent e){
                if(field.getText().equals(placeholder)){
                    field.setText("");
                    field.setForeground(textColor);
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                if(field.getText().isEmpty()){
                    field.setForeground(placeholderColor);
                    field.setText(placeholder);
                }
            }
        });
    }
}
