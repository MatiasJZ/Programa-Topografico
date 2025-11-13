import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class MetodoAtaqueYTiroPanel extends JPanel {

    private JRadioButton rbCercanoSi, rbCercanoNo;
    private JRadioButton rbGranAnguloSi, rbGranAnguloNo;
    private JComboBox<String> comboGranada;
    private JComboBox<String> comboEspoleta;
    private JComboBox<String> comboHaz;
    private JTextField txtVolumen;
    private JRadioButton rbDisparos, rbRafaga;
    private JComboBox<String> comboPiezas;
    private JComboBox<String> comboSeccion;
    private JRadioButton rbPiqueSi, rbPiqueNo;
    private JRadioButton rbFgoSi, rbFgoNo;
    private JRadioButton rbTesSi, rbTesNo;
    private JTextField txtTot;
    private JRadioButton rbCuandoListo, rbAMiOrden;
    private EnviarListener enviarListener;

    public void setEnviarListener(EnviarListener l) { this.enviarListener = l; }

    public MetodoAtaqueYTiroPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.BLACK);

        contenido.add(crearMetodoAtaque2Columnas());
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(crearTiroYControl2Columnas());
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(crearBotonEnviar());

        add(contenido, BorderLayout.CENTER);
    }

    private JPanel crearMetodoAtaque2Columnas() {

        JPanel panel = crearBlindado("MÉTODO DE ATAQUE");
        JPanel grid = new JPanel(new GridLayout(4, 2, 15, 12));
        grid.setBackground(Color.BLACK);
        Icon icon = new RadioButtonGrande(22);
        Dimension comboSize = new Dimension(220, 30);
        
        // CERCANO
        grid.add(crearGrupoRadio("CERCANO:", rb -> {
            rbCercanoSi = crearRadio("Sí", icon);
            rbCercanoNo = crearRadio("No", icon);
            agrupar(rbCercanoSi, rbCercanoNo);
            rb.add(rbCercanoSi); rb.add(rbCercanoNo);
        }));

        // GRAN ANGULO
        grid.add(crearGrupoRadio("GRAN ÁNGULO:", rb -> {
            rbGranAnguloSi = crearRadio("Sí", icon);
            rbGranAnguloNo = crearRadio("No", icon);
            agrupar(rbGranAnguloSi, rbGranAnguloNo);
            rb.add(rbGranAnguloSi); rb.add(rbGranAnguloNo);
        }));

        // GRANADA
        comboGranada = crearCombo(new String[]{"HE","IL","WP"}, comboSize);
        grid.add(crearItem("GRANADA:", comboGranada));

        // ESPOLETA
        comboEspoleta = crearCombo(new String[]{"I","VT","CM"}, comboSize);
        grid.add(crearItem("ESPOLETA:", comboEspoleta));

        // VOLUMEN
        txtVolumen = new JTextField();
        txtVolumen.setHorizontalAlignment(SwingConstants.CENTER);
        txtVolumen.setPreferredSize(comboSize);
        txtVolumen.setFont(new Font("Arial",Font.PLAIN,15));
        txtVolumen.setForeground(Color.WHITE);
        txtVolumen.setBackground(new Color(60,60,60));
        grid.add(crearItem("VOLUMEN:", txtVolumen));

        // MODO DISPARO
        rbDisparos = crearRadio("DISPAROS", null);
        rbRafaga = crearRadio("RÁFAGA", null);
        agrupar(rbDisparos, rbRafaga);

        JPanel modoPanel = crearFila();
        modoPanel.add(rbDisparos);
        modoPanel.add(rbRafaga);
        grid.add(crearItem("MODO:", modoPanel));

        comboHaz = crearCombo(new String[]{"PARALELO","CONVERGENTE","ABIERTO","ESPECIAL","CIRCULAR"},comboSize);
        grid.add(crearItem("HAZ:", comboHaz));
        panel.add(grid, BorderLayout.CENTER);
        
        return panel;
    }

    private void centrarCombo(JComboBox<String> combo) {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        combo.setRenderer(renderer);
    }
    
    private JPanel crearTiroYControl2Columnas() {

        JPanel panel = crearBlindado("TIRO Y CONTROL");
        JPanel grid = new JPanel(new GridLayout(4, 2, 15, 12));
        grid.setBackground(Color.BLACK);
        Icon icon = new RadioButtonGrande(22);
        Dimension comboSize = new Dimension(220, 30);
        // PIEZAS
        comboPiezas = crearCombo(new String[]{"1","2","3","4","5","6"}, comboSize);
        grid.add(crearItem("PIEZAS:", comboPiezas));
        // SECCIÓN
        comboSeccion = crearCombo(new String[]{"IZQUIERDA","DERECHA"}, comboSize);
        grid.add(crearItem("SECCIÓN:", comboSeccion));
        // PIQUE
        grid.add(crearGrupoRadio("PIQUE:", rb -> {
            rbPiqueSi = crearRadio("Sí", icon);
            rbPiqueNo = crearRadio("No", icon);
            agrupar(rbPiqueSi, rbPiqueNo);
            rb.add(rbPiqueSi); rb.add(rbPiqueNo);
        }));
        // FGO CONT
        grid.add(crearGrupoRadio("FGO CONT:", rb -> {
            rbFgoSi = crearRadio("Sí", icon);
            rbFgoNo = crearRadio("No", icon);
            agrupar(rbFgoSi, rbFgoNo);
            rb.add(rbFgoSi); rb.add(rbFgoNo);
        }));
        // TES
        grid.add(crearGrupoRadio("TES:", rb -> {
            rbTesSi = crearRadio("Sí", icon);
            rbTesNo = crearRadio("No", icon);
            agrupar(rbTesSi, rbTesNo);
            rb.add(rbTesSi); rb.add(rbTesNo);
        }));
        // TOT
        txtTot = new JTextField("seg");
        txtTot.setHorizontalAlignment(SwingConstants.CENTER);
        txtTot.setPreferredSize(new Dimension(90, 30));
        txtTot.setForeground(Color.GRAY);
        txtTot.setBackground(new Color(60,60,60));

        txtTot.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e){
                if(txtTot.getText().equals("seg")){
                    txtTot.setText("");
                    txtTot.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e){
                if(txtTot.getText().isEmpty()){
                    txtTot.setText("seg");
                    txtTot.setForeground(Color.GRAY);
                }
            }
        });
        grid.add(crearItem("TOT:", txtTot));
        panel.add(grid, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel crearBotonEnviar() {

        // radiobutton de modo de fuego
        rbCuandoListo = crearRadio("CUANDO LISTO", null);
        rbAMiOrden    = crearRadio("A MI ORDEN", null);
        agrupar(rbCuandoListo, rbAMiOrden);

        JPanel radiosModo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        radiosModo.setBackground(Color.BLACK);
        radiosModo.add(rbCuandoListo);
        radiosModo.add(rbAMiOrden);

        // boton enviar
        JButton btn = new JButton("ENVIAR PIF");
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setBackground(new Color(140, 30, 30));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);

        btn.addActionListener(e -> {
            if (enviarListener != null)
                enviarListener.onEnviar();
        });
        JPanel total = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        total.setBackground(Color.BLACK);

        total.add(radiosModo);
        total.add(btn);

        return total;
    }

    private JPanel crearBlindado(String titulo){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.BLACK);

        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY,2),
            titulo,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial",Font.BOLD,17),
            Color.WHITE
        );
        p.setBorder(b);
        return p;
    }

    private JPanel crearFila(){
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        p.setBackground(new Color(60,60,60));
        return p;
    }

    private JPanel crearItem(String label, Component c){
        JPanel p = crearFila();
        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial",Font.BOLD,15));
        p.add(l);
        p.add(c);
        return p;
    }

    private JPanel crearGrupoRadio(String titulo, java.util.function.Consumer<JPanel> consumer){
        JPanel p = crearFila();
        JLabel l = new JLabel(titulo);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial",Font.BOLD,15));
        p.add(l);

        JPanel radios = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
        radios.setBackground(new Color(60,60,60));

        consumer.accept(radios);
        p.add(radios);
        return p;
    }

    private JComboBox<String> crearCombo(String[] items, Dimension d){
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Arial",Font.PLAIN,15));
        c.setPreferredSize(d);
        c.setBackground(Color.WHITE);
        centrarCombo(c);
        return c;
    }

    private JRadioButton crearRadio(String text, Icon icon){
        JRadioButton rb = new JRadioButton(text);
        rb.setForeground(Color.WHITE);
        rb.setBackground(new Color(60,60,60));
        rb.setFont(new Font("Arial",Font.PLAIN,14));
        rb.setIcon(icon);
        return rb;
    }

    private void agrupar(JRadioButton a, JRadioButton b){
        ButtonGroup g = new ButtonGroup();
        g.add(a); g.add(b);
    }

    public boolean isRafaga(){ return rbRafaga.isSelected(); }
    
    public boolean isDisparos(){ return rbDisparos.isSelected(); }
    
    public boolean isCercano(){ return rbCercanoSi.isSelected(); }
    
    public boolean isGranAngulo(){ return rbGranAnguloSi.isSelected(); }

    public String getGranada(){ return (String) comboGranada.getSelectedItem(); }
    
    public String getEspoleta(){ return (String) comboEspoleta.getSelectedItem(); }
    
    public String getHaz(){ return (String) comboHaz.getSelectedItem(); }

    public int getVolumen(){
        try{ return Integer.parseInt(txtVolumen.getText()); }
        catch(Exception e){ return -1; }
    }

    public String getPiezas(){ return (String) comboPiezas.getSelectedItem(); }
    
    public String getSeccion(){ return (String) comboSeccion.getSelectedItem(); }

    public boolean isPiqueSi(){ return rbPiqueSi.isSelected(); }
    
    public boolean isFgoSi(){ return rbFgoSi.isSelected(); }
    
    public boolean isTesSi(){ return rbTesSi.isSelected(); }

    public boolean isCuandoListo(){ return rbCuandoListo.isSelected(); }
    
    public boolean isAMiOrden(){ return rbAMiOrden.isSelected(); }

    public String getTot(){ return txtTot.getText(); }

    public void mostrarPIF(PIF p){}
}
