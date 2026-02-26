package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import dominio.Blanco;
import dominio.Posicionable;
import dominio.Punto;

/**
 * Clase de utilidad para la creación y configuración de componentes Swing personalizados
 * con estilos y comportamientos específicos para la interfaz gráfica de usuario.
 * <p>
 * Esta clase no puede ser instanciada.
 * </p>
 * 
 * Métodos principales:
 * <ul>
 *   <li><b>crearEtiqueta</b>: Crea un JLabel con fuente y color personalizados.</li>
 *   <li><b>crearCampoTexto</b>: Crea un JTextField estilizado para entrada de texto.</li>
 *   <li><b>crearComboPuntosYBlancos</b>: Crea un JComboBox que contiene elementos de tipo Punto y Blanco.</li>
 *   <li><b>configurarBotonMilitar</b>: Aplica un estilo militar a un JButton, incluyendo color de acento y efecto hover.</li>
 *   <li><b>crearLinea2</b>: Crea un JPanel con dos etiquetas alineadas, útil para mostrar pares etiqueta-valor.</li>
 *   <li><b>configurarBotonEstilo</b>: Aplica un estilo general a un JButton con color de fondo personalizado.</li>
 *   <li><b>addPlaceholder</b>: Añade funcionalidad de placeholder a JTextField o JTextArea.</li>
 * </ul>
 * 
 * <b>Nota:</b> Todos los métodos son estáticos y están diseñados para facilitar la reutilización de componentes
 * con estilos consistentes en la aplicación.
 */
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

	public static void agrupar(JRadioButton a, JRadioButton b) {
        ButtonGroup g = new ButtonGroup();
        g.add(a);
        g.add(b);
    }
	
	public static JPanel crearGrupoRadio(String titulo, Consumer<JPanel> consumer) {
        JPanel p = crearFila();
        JLabel l = new JLabel(titulo);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.BOLD, 15));
        p.add(l);

        JPanel radios = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        radios.setBackground(new Color(60,60,60));

        consumer.accept(radios);
        p.add(radios);
        return p;
    }
	
	public static JPanel crearFila() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        p.setOpaque(false); // fondo transparente
        p.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 1)); // marco rectangular
        return p;
    }

    public static JPanel crearItem(String label, Component c) {
        JPanel p = crearFila();
        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.BOLD, 15));
        p.add(l);
        p.add(c);
        return p;
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
	
	public static TitledBorder crearBordeTitulo(String titulo) {
        TitledBorder b = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                titulo,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 20),
                Color.WHITE
        );
        return b;
    }
	
	public static JRadioButton crearRadio(String text, Icon icon) {
        JRadioButton rb = new JRadioButton(text);
        rb.setForeground(Color.WHITE);
        rb.setBackground(new Color(60,60,60));
        rb.setFont(new Font("Arial", Font.PLAIN, 14));
        rb.setIcon(icon);
        return rb;
    }
	
	public static JPanel crearBlindado(String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.BLACK);

        TitledBorder b = BorderFactory.createTitledBorder(
	        BorderFactory.createLineBorder(Color.GRAY, 2),
	        titulo,
	        TitledBorder.LEFT,
	        TitledBorder.TOP,
	        new Font("Arial", Font.BOLD, 17),
	        Color.WHITE
        );
        p.setBorder(b);
        return p;
    }
	
	public static void configurarArea(JTextArea a){
        a.setForeground(Color.WHITE);
        a.setBackground(new Color(60,60,60));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
    }
	
	public static JPanel crearLinea(String etiqueta, String valor, Font font) {
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linea.setBackground(Color.BLACK);

        JLabel l1 = new JLabel(etiqueta);
        l1.setForeground(Color.WHITE);
        l1.setFont(font);

        JLabel l2 = new JLabel(valor);
        l2.setForeground(new Color(180, 255, 180));
        l2.setFont(font);

        linea.add(l1);
        linea.add(l2);

        return linea;
    }
	
	public static void configurarCampo(JTextField t){
        t.setBackground(new Color(60,60,60));
        t.setForeground(Color.WHITE);
    }
	
	public static JComboBox<String> crearCombo(String[] items, Dimension d) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Arial", Font.PLAIN, 15));
        c.setPreferredSize(d);
        c.setBackground(Color.WHITE);
        centrarCombo(c);
        return c;
    }
	
	public static JComboBox<String> crearCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setBackground(new Color(60,60,60));
        c.setForeground(Color.WHITE);
        c.setFont(new Font("Arial", Font.PLAIN, 14));

        // Centrar texto en elementos
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        c.setRenderer(renderer);

        return c;
    }
	
	public static void centrarCombo(JComboBox<String> combo) {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        combo.setRenderer(renderer);
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
