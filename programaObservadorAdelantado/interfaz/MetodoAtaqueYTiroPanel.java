package interfaz;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.*;

import dominio.Blanco;
import dominio.PIF;

public class MetodoAtaqueYTiroPanel extends JPanel {

    private static final long serialVersionUID = -3752662630854874010L;

    // Componentes principales
    private JRadioButton rbCercanoSi, rbCercanoNo;
    private JRadioButton rbGranAnguloSi, rbGranAnguloNo;
    private JComboBox<String> comboGranada;
    private JComboBox<String> comboEspoleta;
    private JComboBox<String> comboHaz;
    private JTextField txtVolumen;
    private JRadioButton rbDisparos, rbRafaga;
    private JComboBox<String> comboPiezas;
    private JComboBox<String> comboSeccion;
    private JComboBox<String> comboEfectoDeseado;
    private JRadioButton rbFgoSi, rbFgoNo;
    private JRadioButton rbTesSi, rbTesNo;
    private JRadioButton rbCuandoListo, rbAMiOrden;
    private JRadioButton rbMetodo;
    private JRadioButton rbTiro;
    private JRadioButton rbEficacia, rbReglare, rbSupresion, rbSupresionInmediata;
    private JTextField txtRegistroSobre, txtBarreraInclinacion, txtBarreraFrente;
    private final Icon iconoRB = new RadioButtonGrande(22);
    	
    // Correcciones
    private CardLayout cardCorrecciones;
    private JPanel panelCard;
    private JPanel panelPrincipal;
    private CorreccionesPanel panelCorrecciones;
    private JPanel panelMisionDeFuego;

    private JButton btnCorrecciones;
    private JButton btnEnviar;
    private JButton btnFuego;

    private Timer timerFuego;
    private boolean fuegoEstado = false;
    
    private CardLayout cardAlternable;
    private JPanel panelAlternable;
    private JPanel panelMetodoAtaque;
    private JPanel panelTiroControl;
    private Blanco blancoSeleccionado;
    private DatosBlanco datos;
    
    public MetodoAtaqueYTiroPanel(DatosBlanco datos) {

    	this.datos = datos;
    	blancoSeleccionado = null;
    	
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(Color.BLACK);

        panelMetodoAtaque = crearMetodoAtaque();
        panelTiroControl = crearTiroYControl();
        
        cardAlternable = new CardLayout();
        panelAlternable = new JPanel(cardAlternable);
        panelAlternable.setBackground(Color.BLACK);
        
        panelAlternable.add(panelMetodoAtaque, "ataque");
        panelAlternable.add(panelTiroControl, "tiro");

        cardAlternable.show(panelAlternable, "ataque");  

        panelPrincipal.add(crearSelectorMetodoTiro());
        panelPrincipal.add(Box.createVerticalStrut(0));
        panelPrincipal.add(panelAlternable);
        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(crearBotonEnviar());

        panelCorrecciones = new CorreccionesPanel(blancoSeleccionado);

        cardCorrecciones = new CardLayout();
        panelCard = new JPanel(cardCorrecciones);
        panelCard.setBackground(Color.BLACK);
        panelAlternable.setPreferredSize(new Dimension(1000, 300));

        panelCard.add(panelPrincipal, "principal");
        panelCard.add(panelCorrecciones, "correcciones");
        add(panelCard, BorderLayout.CENTER);
        
        panelMisionDeFuego = crearPanelMisionDeFuego(); 
        add(panelMisionDeFuego, BorderLayout.NORTH);
        
        panelCorrecciones.getBtnVolver().addActionListener(
            e -> cardCorrecciones.show(panelCard, "principal")
        );
        
        timerFuego = new Timer(500, e -> {
            fuegoEstado = !fuegoEstado;

            if (fuegoEstado) {
                btnFuego.setBackground(new Color(255, 60, 60)); // Rojo más brillante
            } else {
                btnFuego.setBackground(new Color(180, 0, 0));   // Rojo más oscuro
            }
        });
    }

    public void actualizar() {
    	panelCorrecciones.reiniciarCuadricula();
    	String nombreBlanco = datos.getBlancoActual().getNombre();
    	if(nombreBlanco != null)
    		txtRegistroSobre.setText(datos.getBlancoActual().getNombre());
    }
    
    private JPanel crearPanelMisionDeFuego() {
        
    	// Panel principal (Misión)
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.BLACK);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblTitulo = new JLabel("MISIÓN", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        
        JSplitPane splitMision = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitMision.setResizeWeight(0.62); 
        splitMision.setBorder(null);
        splitMision.setBackground(Color.BLACK);
        
        JPanel panelSwitches = new JPanel(new GridBagLayout()); 
        panelSwitches.setBackground(Color.BLACK);
        panelSwitches.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "MISION",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 15), Color.WHITE
        ));

        GridBagConstraints gbcSwitch = new GridBagConstraints();
        gbcSwitch.insets = new Insets(5, 5, 5, 5);
        gbcSwitch.anchor = GridBagConstraints.WEST;
        
        // Crear Radios y agruparlos
        ButtonGroup grupoMision = new ButtonGroup();
        Font fSwitch = new Font("Arial", Font.BOLD, 14);

        rbEficacia = crearRadio("EFICACIA", iconoRB);
        rbReglare = crearRadio("REGLARE", iconoRB);
        rbSupresion = crearRadio("SUPRESIÓN", iconoRB);
        rbSupresionInmediata = crearRadio("SUPRESIÓN INMEDIATA", iconoRB);
        
        // Configuramos fuente y grupo
        JRadioButton[] radios = {rbEficacia, rbReglare, rbSupresion, rbSupresionInmediata};
        for (JRadioButton rb : radios) {
            rb.setFont(fSwitch);
            rb.setBackground(Color.BLACK);
            grupoMision.add(rb);
        }
        rbEficacia.setSelected(true);
        
        // Posicionamiento en 2x2
        gbcSwitch.gridx = 0; gbcSwitch.gridy = 0; panelSwitches.add(rbEficacia, gbcSwitch);
        gbcSwitch.gridx = 0; gbcSwitch.gridy = 1; panelSwitches.add(rbReglare, gbcSwitch);
        gbcSwitch.gridx = 1; gbcSwitch.gridy = 0; panelSwitches.add(rbSupresion, gbcSwitch);
        gbcSwitch.gridx = 1; gbcSwitch.gridy = 1; panelSwitches.add(rbSupresionInmediata, gbcSwitch);

        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBackground(Color.BLACK);
        panelCampos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 15), Color.WHITE
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 
        
        // Estilos de JTextField
        Dimension fieldSize = new Dimension(100, 26);
        Font fLabel = new Font("Arial", Font.BOLD, 13);
        
        // Inicializar campos
        txtRegistroSobre = new JTextField();
        txtBarreraFrente = new JTextField();
        txtBarreraInclinacion = new JTextField(); 
        
        for (JTextField field : new JTextField[]{txtRegistroSobre, txtBarreraFrente, txtBarreraInclinacion}) {
            field.setPreferredSize(fieldSize);
            field.setBackground(new Color(70, 70, 70));
            field.setForeground(Color.WHITE);
            field.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        JLabel lblRegistro = new JLabel("REGISTRO", SwingConstants.LEFT);
        lblRegistro.setForeground(Color.CYAN); 
        lblRegistro.setFont(fLabel);

        JLabel lblSobre = new JLabel("SOBRE:", SwingConstants.RIGHT);
        lblSobre.setForeground(Color.WHITE);
        lblSobre.setFont(fLabel);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(lblRegistro, gbc);
        gbc.gridwidth = 1; 
        
        gbc.gridx = 0; gbc.gridy = 1; 
        gbc.weightx = 0;
        panelCampos.add(lblSobre, gbc);
        
        gbc.gridx = 1; 
        gbc.weightx = 1.0;
        panelCampos.add(txtRegistroSobre, gbc);
        
        JLabel lblBarrera = new JLabel("BARRERA", SwingConstants.LEFT);
        lblBarrera.setForeground(Color.CYAN); 
        lblBarrera.setFont(fLabel);
        
        JLabel lblFrente = new JLabel("FRENTE:", SwingConstants.RIGHT);
        lblFrente.setForeground(Color.WHITE);
        lblFrente.setFont(fLabel);

        JLabel lblInclinacion = new JLabel("INCLINACIÓN:", SwingConstants.RIGHT);
        lblInclinacion.setForeground(Color.WHITE);
        lblInclinacion.setFont(fLabel);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(lblBarrera, gbc);
        gbc.gridwidth = 1; 

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        panelCampos.add(lblFrente, gbc);
        
        gbc.gridx = 1; 
        gbc.weightx = 1.0;
        panelCampos.add(txtBarreraFrente, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0;
        panelCampos.add(lblInclinacion, gbc);
        
        gbc.gridx = 1; 
        gbc.weightx = 1.0;
        panelCampos.add(txtBarreraInclinacion, gbc); 
        
        splitMision.setLeftComponent(panelSwitches);
        splitMision.setRightComponent(panelCampos);
        
        p.add(splitMision, BorderLayout.CENTER);
        
        return p;
    }
    
    private JPanel crearSelectorMetodoTiro() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        p.setBackground(Color.BLACK);

        rbMetodo = crearRadio("METODO DE ATAQUE", iconoRB);
        rbMetodo.setForeground(Color.WHITE);
        rbMetodo.setBackground(Color.BLACK);
        rbMetodo.setFont(new Font("Arial", Font.BOLD, 15));

        rbTiro = crearRadio("TIRO Y CONTROL", iconoRB);
        rbTiro.setForeground(Color.WHITE);
        rbTiro.setBackground(Color.BLACK);
        rbTiro.setFont(new Font("Arial", Font.BOLD, 15));

        ButtonGroup g = new ButtonGroup();
        g.add(rbMetodo);
        g.add(rbTiro);
        rbMetodo.setSelected(true); // por defecto

        rbMetodo.addActionListener(e -> cardAlternable.show(panelAlternable, "ataque"));
        rbTiro.addActionListener(e -> cardAlternable.show(panelAlternable, "tiro"));

        p.add(rbMetodo);
        p.add(rbTiro);

        return p;
    }

    private void notificarEnvioPIF() {
        firePropertyChange("ENVIAR_PIF", false, true);
        panelCorrecciones.actualizarBlanco(datos.getBlancoActual());
    }

    public void setConexionDisponible(boolean ok) {
        btnEnviar.setEnabled(ok);
        btnEnviar.setBackground(ok ? new Color(140,30,30) : new Color(70,20,20));
    }

    private JPanel crearMetodoAtaque() {
        JPanel panel = crearBlindado("MÉTODO DE ATAQUE");
        
        JPanel grid = new JPanel(new GridLayout(5, 2, 15, 12)); 
        grid.setBackground(Color.BLACK);
        
        Dimension comboSize = new Dimension(220, 30);
        String[] efectos = {"OBSERVAR", "DESTRUIR", "NEUTRALIZAR", "SUPRIMIR"};
        comboEfectoDeseado = crearCombo(efectos, comboSize);
        grid.add(crearItem("EFECTO DESEADO:", comboEfectoDeseado));
        
        rbDisparos = crearRadio("DISPAROS", iconoRB);
        rbRafaga = crearRadio("RÁFAGA", iconoRB);
        agrupar(rbDisparos, rbRafaga);

        Icon icon = null;
        
        JPanel modoPanel = crearFila();
        modoPanel.add(rbDisparos);
        modoPanel.add(rbRafaga);
        grid.add(crearItem("MODO:", modoPanel)); 

        // CERCANO 
        grid.add(crearGrupoRadio("CERCANO:", rb -> {
            rbCercanoSi = crearRadio("Sí", iconoRB);
            rbCercanoNo = crearRadio("No", iconoRB);
            agrupar(rbCercanoSi, rbCercanoNo);
            rb.add(rbCercanoSi);
            rb.add(rbCercanoNo);
        }));

        // GRAN ÁNGULO
        grid.add(crearGrupoRadio("GRAN ÁNGULO:", rb -> {
            rbGranAnguloSi = crearRadio("Sí", iconoRB);
            rbGranAnguloNo = crearRadio("No", iconoRB);
            agrupar(rbGranAnguloSi, rbGranAnguloNo);
            rb.add(rbGranAnguloSi);
            rb.add(rbGranAnguloNo);
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
        txtVolumen.setFont(new Font("Arial", Font.PLAIN, 15));
        txtVolumen.setForeground(Color.WHITE);
        txtVolumen.setBackground(new Color(60,60,60));
        grid.add(crearItem("VOLUMEN:", txtVolumen));

        comboHaz = crearCombo(new String[]{"PARALELO","CONVERGENTE","ABIERTO","ESPECIAL","CIRCULAR"}, comboSize);
        grid.add(crearItem("HAZ:", comboHaz));

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTiroYControl() {

        JPanel panel = crearBlindado("TIRO Y CONTROL");
        JPanel grid = new JPanel(new GridLayout(4, 2, 15, 12));
        grid.setBackground(Color.BLACK);
        Icon icon = new RadioButtonGrande(22);
        Dimension comboSize = new Dimension(220, 30);

        comboPiezas = crearCombo(new String[]{"1","2","3","4","5","6"}, comboSize);
        grid.add(crearItem("PIEZAS:", comboPiezas));

        comboSeccion = crearCombo(new String[]{"IZQUIERDA","DERECHA"}, comboSize);
        grid.add(crearItem("SECCIÓN:", comboSeccion));

        // FGO CONTINUO
        grid.add(crearGrupoRadio("FGO CONT:", rb -> {
            rbFgoSi = crearRadio("Sí", iconoRB);
            rbFgoNo = crearRadio("No", iconoRB);
            agrupar(rbFgoSi, rbFgoNo);
            rb.add(rbFgoSi);
            rb.add(rbFgoNo);
        }));

        // TES
        grid.add(crearGrupoRadio("TES:", rb -> {
            rbTesSi = crearRadio("Sí", iconoRB);
            rbTesNo = crearRadio("No", iconoRB);
            agrupar(rbTesSi, rbTesNo);
            rb.add(rbTesSi);
            rb.add(rbTesNo);
        }));

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBotonEnviar() {

        JPanel total = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
        total.setBackground(Color.BLACK);
        rbCuandoListo = crearRadio("CUANDO LISTO", iconoRB);
        rbAMiOrden = crearRadio("A MI ORDEN", iconoRB);
        agrupar(rbCuandoListo, rbAMiOrden);

        JPanel radiosModo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        radiosModo.setBackground(Color.BLACK);
        radiosModo.add(rbCuandoListo);
        radiosModo.add(rbAMiOrden);

        // Botón enviar
        btnEnviar = new JButton("ENVIAR PIF");
        btnEnviar.setPreferredSize(new Dimension(180, 40));
        btnEnviar.setBackground(new Color(140, 30, 30));
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFont(new Font("Arial", Font.BOLD, 16));
        btnEnviar.setFocusPainted(false);

        btnFuego = new JButton("FUEGO"); 
        btnFuego.setPreferredSize(new Dimension(180, 40));
        btnFuego.setBackground(new Color(200, 0, 0)); 
        btnFuego.setForeground(Color.WHITE);
        btnFuego.setFont(new Font("Arial", Font.BOLD, 18));
        btnFuego.setFocusPainted(false);
        btnFuego.setVisible(false); 
        btnFuego.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btnFuego.addActionListener(a -> {
            timerFuego.stop(); 
            btnFuego.setVisible(false);
            btnFuego.setBackground(new Color(200,0,0));
            cardCorrecciones.show(panelCard, "correcciones");
            panelMisionDeFuego.setVisible(false);
            firePropertyChange("ENVIAR_FUEGO", false, true);
            MetodoAtaqueYTiroPanel.this.revalidate();
            MetodoAtaqueYTiroPanel.this.repaint();
        });
        
        btnEnviar.addActionListener(e -> {
            notificarEnvioPIF();
            
            btnFuego.setVisible(false); 
            panelCorrecciones.reiniciarCuadricula();
            
            if (rbAMiOrden.isSelected()) {
                btnFuego.setVisible(true);
                timerFuego.start();      
            } else {
                timerFuego.stop();       
                btnFuego.setBackground(new Color(200,0,0)); 
            }
            
            if (rbCuandoListo.isSelected()) {
                cardCorrecciones.show(panelCard, "correcciones");
                panelMisionDeFuego.setVisible(false);
            }
          
            total.revalidate();
            total.repaint();
        });
        
        // Botón correcciones
        btnCorrecciones = new JButton("CORRECCIONES");
        btnCorrecciones.setPreferredSize(new Dimension(180, 40));
        btnCorrecciones.setFocusPainted(false);
        btnCorrecciones.setFont(new Font("Arial", Font.BOLD, 15));
        btnCorrecciones.setForeground(Color.WHITE);
        btnCorrecciones.setBackground(new Color(20, 40, 90));
        btnCorrecciones.setBorder(BorderFactory.createLineBorder(new Color(80,80,160)));

        btnCorrecciones.addActionListener(
            e -> {
            	cardCorrecciones.show(panelCard, "correcciones");           
            	panelMisionDeFuego.setVisible(false);
    	});

        total.add(radiosModo);
        total.add(btnEnviar);
        total.add(btnCorrecciones);
        total.add(btnFuego);

        return total;
    }

    private JPanel crearBlindado(String titulo) {
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

    private JPanel crearFila() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        p.setOpaque(false); // fondo transparente
        p.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 1)); // marco rectangular
        return p;
    }

    private JPanel crearItem(String label, Component c) {
        JPanel p = crearFila();
        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.BOLD, 15));
        p.add(l);
        p.add(c);
        return p;
    }

    private JPanel crearGrupoRadio(String titulo, Consumer<JPanel> consumer) {
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

    private JComboBox<String> crearCombo(String[] items, Dimension d) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Arial", Font.PLAIN, 15));
        c.setPreferredSize(d);
        c.setBackground(Color.WHITE);
        centrarCombo(c);
        return c;
    }

    private void centrarCombo(JComboBox<String> combo) {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        combo.setRenderer(renderer);
    }

    private JRadioButton crearRadio(String text, Icon icon) {
        JRadioButton rb = new JRadioButton(text);
        rb.setForeground(Color.WHITE);
        rb.setBackground(new Color(60,60,60));
        rb.setFont(new Font("Arial", Font.PLAIN, 14));
        rb.setIcon(icon);
        return rb;
    }

    private void agrupar(JRadioButton a, JRadioButton b) {
        ButtonGroup g = new ButtonGroup();
        g.add(a);
        g.add(b);
    }
    
    public String getModoMision() {
        if (rbEficacia.isSelected()) return "EFICACIA";
        if (rbReglare.isSelected()) return "REGLARE";
        if (rbSupresion.isSelected()) return "SUPRESION";
        if (rbSupresionInmediata.isSelected()) return "SUPRESION INMEDIATA";
        return "";
    }

    public String getRegistroSobre() {
        return txtRegistroSobre.getText().trim();
    }
    
    public void setRegistroSobre(String s) {
    	txtRegistroSobre.setText(s);
    }

    public String getBarreraFrente() {
        return txtBarreraFrente.getText().trim();
    }

    public String getBarreraInclinacion() {
        return txtBarreraInclinacion.getText().trim();
    }

    public String getEfectoDeseado() {
        return (String) comboEfectoDeseado.getSelectedItem();
    }
    
    public JPanel getPanelMisionDeFuego() {
    	return panelMisionDeFuego;
    }
    
    public String getModoFuego() {
    	if(rbDisparos.isSelected())
    		return "DISPAROS";
    	else 
    		return "RAFAGAS";
    }
    public boolean isCercano() { return rbCercanoSi.isSelected(); }
    public boolean isGranAngulo() { return rbGranAnguloSi.isSelected(); }
    public String getGranada() { return (String) comboGranada.getSelectedItem(); }
    public String getEspoleta() { return (String) comboEspoleta.getSelectedItem(); }
    public String getHaz() { return (String) comboHaz.getSelectedItem(); }
    
    public String getVolumen() {
        return txtVolumen.getText();
    }

    public String getPiezas() { return (String) comboPiezas.getSelectedItem(); }
    public String getSeccion() { return (String) comboSeccion.getSelectedItem(); }
    public boolean isFgoSi() { return rbFgoSi.isSelected(); }
    public boolean isTesSi() { return rbTesSi.isSelected(); }
    public boolean isCuandoListo() { return rbCuandoListo.isSelected(); }
    public boolean isAMiOrden() { return rbAMiOrden.isSelected(); }
    public void mostrarPanelCorrecciones() {
        cardCorrecciones.show(panelCard, "correcciones");
    }

    public void mostrarPanelPrincipal() {
        cardCorrecciones.show(panelCard, "principal");
    }

    public CorreccionesPanel getCorreccionesPanel() {
        return panelCorrecciones;
    }

    public void mostrarPIF(PIF p) {}
}