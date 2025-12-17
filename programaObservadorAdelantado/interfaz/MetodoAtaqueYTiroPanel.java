package interfaz;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
    private JRadioButton rbDisparosAtaque, rbRafagaAtaque, rbDisparosReglaje, rbRafagaReglaje;
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
    private JRadioButton rbReglajeSelector;
	private JPanel panelFases;
	private JToggleButton btnFasePercusion;
	private JToggleButton btnFaseTiempo;
	private JToggleButton btnBarrera;


    private JButton btnCorrecciones;
    private JButton btnEnviar;
    
    private CardLayout cardAlternable;
    private JPanel panelAlternable;
    private JPanel panelMetodoAtaque;
    private JPanel panelTiroControl;
    private JPanel panelReglaje;
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
        panelReglaje = crearReglaje();

        cardAlternable = new CardLayout();
        panelAlternable = new JPanel(cardAlternable);
        panelAlternable.setBackground(Color.BLACK);

        panelAlternable.add(panelMetodoAtaque, "ataque");
        panelAlternable.add(panelTiroControl, "tiro");
        panelAlternable.add(panelReglaje, "reglaje");

        cardAlternable.show(panelAlternable, "ataque");

        panelPrincipal.add(crearSelectorMetodoTiro());
        panelPrincipal.add(Box.createVerticalStrut(5));
        panelPrincipal.add(panelAlternable);
        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(crearBotonEnviar());

        panelCorrecciones = new CorreccionesPanel(blancoSeleccionado, this);

        cardCorrecciones = new CardLayout();
        panelCard = new JPanel(cardCorrecciones);
        panelCard.setBackground(Color.BLACK);

        panelCard.add(panelPrincipal, "principal");
        panelCard.add(panelCorrecciones, "correcciones");

        add(panelCard, BorderLayout.CENTER);

        panelMisionDeFuego = crearPanelMisionDeFuego();
        add(panelMisionDeFuego, BorderLayout.NORTH);

        panelCorrecciones.getBtnVolver().addActionListener(
            e -> cardCorrecciones.show(panelCard, "principal")
        );
    }

    public void actualizar() {
    	panelCorrecciones.reiniciarCuadricula();
    	String nombreBlanco = datos.getBlancoActual().getNombre();
    	if(nombreBlanco != null)
    		txtRegistroSobre.setText(datos.getBlancoActual().getNombre());
    }
    
    private JPanel crearReglaje() {

        JPanel panel = crearBlindado("REGLAJE");

        JPanel grid = new JPanel(new GridLayout(4, 2, 15, 12));
        grid.setBackground(Color.BLACK);

        Dimension comboSize = new Dimension(220, 30);
        
        rbDisparosReglaje = crearRadio("DISPAROS", iconoRB);
        rbRafagaReglaje  = crearRadio("RÁFAGA", iconoRB);
        agrupar(rbDisparosReglaje, rbRafagaReglaje);

        // MODO DE FUEGO
        JPanel modoPanel = crearFila();
        modoPanel.add(rbDisparosReglaje);
        modoPanel.add(rbRafagaReglaje);
        grid.add(crearItem("MODO DE FUEGO:", modoPanel));

        // VOLUMEN
        JTextField volumenReglaje = new JTextField();
        volumenReglaje.setHorizontalAlignment(SwingConstants.CENTER);
        volumenReglaje.setPreferredSize(comboSize);
        volumenReglaje.setFont(new Font("Arial", Font.PLAIN, 15));
        volumenReglaje.setForeground(Color.WHITE);
        volumenReglaje.setBackground(new Color(60,60,60));

        // sincroniza con el volumen principal
        volumenReglaje.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { txtVolumen.setText(volumenReglaje.getText()); }
            public void removeUpdate(DocumentEvent e) { txtVolumen.setText(volumenReglaje.getText()); }
            public void changedUpdate(DocumentEvent e) {}
        });

        grid.add(crearItem("VOLUMEN:", volumenReglaje));

        // ESPOLETA
        JComboBox<String> comboEspoletaReglaje =
                crearCombo(new String[]{"I","VT","CM","DELAY"}, comboSize);

        comboEspoletaReglaje.addActionListener(e ->
            comboEspoleta.setSelectedItem(comboEspoletaReglaje.getSelectedItem())
        );

        grid.add(crearItem("ESPOLETA:", comboEspoletaReglaje));

        // GRANADA
        JComboBox<String> comboGranadaReglaje =
                crearCombo(new String[]{"HE","IL","WP","HUMO"}, comboSize);

        comboGranadaReglaje.addActionListener(e ->
            comboGranada.setSelectedItem(comboGranadaReglaje.getSelectedItem())
        );

        grid.add(crearItem("GRANADA:", comboGranadaReglaje));

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel crearPanelMisionDeFuego() {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.BLACK);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80,80,80), 2),
            BorderFactory.createEmptyBorder(10,10,10,10)
        ));

        JSplitPane splitMision = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitMision.setResizeWeight(0.74);
        splitMision.setBorder(null);
        splitMision.setBackground(Color.BLACK);

        // PANEL IZQUIERDO 

        JPanel panelSwitches = new JPanel(new GridBagLayout());
        panelSwitches.setBackground(Color.BLACK);
        panelSwitches.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "MISIÓN",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 15),
            Color.WHITE
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 30);

        ButtonGroup grupo = new ButtonGroup();

        rbEficacia = crearRadio("EFICACIA", iconoRB);
        rbReglare = crearRadio("REGLARE", iconoRB);
        rbSupresion = crearRadio("SUPRESIÓN", iconoRB);
        rbSupresionInmediata = crearRadio("SUPRESIÓN INMEDIATA", iconoRB);

        for (JRadioButton rb : new JRadioButton[]{ rbEficacia, rbReglare, rbSupresion, rbSupresionInmediata }) {
            rb.setFont(new Font("Arial", Font.BOLD, 14));
            rb.setBackground(Color.BLACK);
            grupo.add(rb);
        }

        rbEficacia.setSelected(true);

        gbc.gridx = 0; gbc.gridy = 0; panelSwitches.add(rbEficacia, gbc);
        gbc.gridy = 1; panelSwitches.add(rbReglare, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panelSwitches.add(rbSupresion, gbc);
        gbc.gridy = 1; panelSwitches.add(rbSupresionInmediata, gbc);

        // PANEL FASES

        panelFases = new JPanel(new GridBagLayout());
        panelFases.setBackground(Color.BLACK);
        panelFases.setVisible(false);

        GridBagConstraints gbcF = new GridBagConstraints();
        gbcF.insets = new Insets(5, 5, 5, 5);

        btnFasePercusion = new JToggleButton("FASE PERCUSIÓN");
        btnFaseTiempo = new JToggleButton("FASE TIEMPO");
        btnBarrera = new JToggleButton("BARRERA");

        ButtonGroup grupoFases = new ButtonGroup();
        JToggleButton[] fases = { btnFasePercusion, btnFaseTiempo, btnBarrera };

        Color colorNormal = new Color(60, 60, 60);
        Color colorSeleccionado = new Color(70, 120, 70); 

        ActionListener listenerColorFase = e -> {
            for (JToggleButton b : fases) {
                if (b.isSelected()) {
                    b.setBackground(colorSeleccionado);
                } else {
                    b.setBackground(colorNormal);
                }
            }
        };

        for (JToggleButton b : fases) {
            b.setFont(new Font("Arial", Font.BOLD, 13));
            b.setForeground(Color.GRAY);
            b.setBackground(colorNormal);
            b.setPreferredSize(new Dimension(140,30));
            b.setOpaque(true);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createLineBorder(new Color(120,120,120), 1));
            grupoFases.add(b);
            b.addActionListener(listenerColorFase);
        }

        // Fila superior
        gbcF.gridx = 0; gbcF.gridy = 0;
        panelFases.add(btnFasePercusion, gbcF);

        gbcF.gridx = 1;
        panelFases.add(btnFaseTiempo, gbcF);

        // Fila inferior (centrada)
        gbcF.gridx = 0;
        gbcF.gridy = 1;
        gbcF.gridwidth = 2;
        gbcF.anchor = GridBagConstraints.CENTER;
        panelFases.add(btnBarrera, gbcF);

        // Ubicación del bloque de fases
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(0, 10, 0, 10);
        panelSwitches.add(panelFases, gbc);

        // LÓGICA DE VISIBILIDAD 

        ActionListener ocultarReglaje = e -> {
            panelFases.setVisible(false);
            rbReglajeSelector.setEnabled(false);
            rbMetodo.setSelected(true);
            cardAlternable.show(panelAlternable, "ataque");
        };

        rbEficacia.addActionListener(ocultarReglaje);
        rbSupresion.addActionListener(ocultarReglaje);
        rbSupresionInmediata.addActionListener(ocultarReglaje);

        // PANEL DERECHO (REGISTRO / BARRERA)

        JPanel panelCampos = new JPanel(new GridBagLayout()); 
        panelCampos.setBackground(Color.BLACK); panelCampos.setBorder(BorderFactory.createTitledBorder( 
        		BorderFactory.createLineBorder(Color.GRAY), "", TitledBorder.LEFT, TitledBorder.TOP, new 
        		Font("Arial", Font.BOLD, 15), Color.WHITE ));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5,5,5,5);
        gbc2.fill = GridBagConstraints.HORIZONTAL; 
        gbc2.weightx = 1.0;
        
        Dimension fieldSize = new Dimension(100, 26); 
        Font fLabel = new Font("Arial", Font.BOLD, 13);
        
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
        
        gbc2.gridx = 0; 
        gbc2.gridy = 0; 
        gbc2.gridwidth = 2; 
        gbc2.anchor = GridBagConstraints.WEST; 
        panelCampos.add(lblRegistro, gbc2); 
        gbc2.gridwidth = 1; 
        gbc2.gridx = 0; 
        gbc2.gridy = 1; 
        gbc2.weightx = 0; 
        panelCampos.add(lblSobre, gbc2); 
        gbc2.gridx = 1; 
        gbc2.weightx = 1.0; 
        panelCampos.add(txtRegistroSobre, gbc2);
        
        JLabel lblBarrera = new JLabel("BARRERA", SwingConstants.LEFT); 
        lblBarrera.setForeground(Color.CYAN); 
        lblBarrera.setFont(fLabel); 
        JLabel lblFrente = new JLabel("FRENTE:", SwingConstants.RIGHT); 
        lblFrente.setForeground(Color.WHITE); 
        lblFrente.setFont(fLabel);
        
        JLabel lblInclinacion = new JLabel("INCLINACIÓN:", SwingConstants.RIGHT); 
        lblInclinacion.setForeground(Color.WHITE); 
        lblInclinacion.setFont(fLabel); 
        gbc2.gridx = 0; 
        gbc2.gridy = 2; 
        gbc2.gridwidth = 2; 
        gbc2.anchor = GridBagConstraints.WEST; 
        panelCampos.add(lblBarrera, gbc2); 
        gbc2.gridwidth = 1;
        gbc2.gridx = 0; 
        gbc2.gridy = 3; 
        gbc2.weightx = 0; 
        panelCampos.add(lblFrente, gbc2); 
        gbc2.gridx = 1; 
        gbc2.weightx = 1.0;
        panelCampos.add(txtBarreraFrente, gbc2); 
        gbc2.gridx = 0; 
        gbc2.gridy = 4; 
        gbc2.weightx = 0; 
        panelCampos.add(lblInclinacion, gbc2); 
        gbc2.gridx = 1; 
        gbc2.weightx = 1.0;
        panelCampos.add(txtBarreraInclinacion, gbc2);
        
        splitMision.setLeftComponent(panelSwitches); 
        splitMision.setRightComponent(panelCampos); 
        p.add(splitMision, BorderLayout.CENTER); 
        
        rbReglare.addActionListener(e -> {
            panelFases.setVisible(true);
            rbReglajeSelector.setEnabled(true);
            rbReglajeSelector.setSelected(true);
            cardAlternable.show(panelAlternable, "reglaje");
            panelCorrecciones.registrarLabelDeModoDeMision("REGLARE");
            panelCorrecciones.getPanelCuadricula().setMetrosPorPunto(200);
            panelCorrecciones.reiniciarCuadricula();
            panelCorrecciones.setZoomHabilitado(true);
        });
        
        rbEficacia.addActionListener(a-> { 
        	panelCorrecciones.registrarLabelDeModoDeMision("EFICACIA");
            panelCorrecciones.getPanelCuadricula().setMetrosPorPunto(30);
            panelCorrecciones.reiniciarCuadricula();
            panelCorrecciones.setZoomHabilitado(true);
        });
        
        rbSupresion.addActionListener(a-> { 
        	panelCorrecciones.registrarLabelDeModoDeMision("SUPRESION");
            panelCorrecciones.getPanelCuadricula().setMetrosPorPunto(30);
            panelCorrecciones.reiniciarCuadricula();
            panelCorrecciones.setZoomHabilitado(true);
        });
        
        rbSupresionInmediata.addActionListener(a-> { 
        	panelCorrecciones.registrarLabelDeModoDeMision("SUPRESION I.");
            panelCorrecciones.getPanelCuadricula().setMetrosPorPunto(30);
            panelCorrecciones.reiniciarCuadricula();
            panelCorrecciones.setZoomHabilitado(true);
        });
        
        txtBarreraFrente.addActionListener( e -> { 
        	if(!txtBarreraFrente.getText().isBlank()) { 
        		comboHaz.setSelectedItem("-"); 
        	} else comboHaz.setSelectedItem("PARALELO"); 
        }); 
        
        txtBarreraInclinacion.addActionListener( e -> { 
        	if(!txtBarreraInclinacion.getText().isBlank()) { 
        		comboHaz.setSelectedItem("-"); 
        	} else comboHaz.setSelectedItem("PARALELO"); 
        });
        
        return p;
    }

    private JPanel crearSelectorMetodoTiro() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        p.setBackground(Color.BLACK);

        rbMetodo = crearRadio("MÉTODO DE ATAQUE", iconoRB);
        rbTiro = crearRadio("TIRO Y CONTROL", iconoRB);
        rbReglajeSelector = crearRadio("REGLAJE", iconoRB);

        Font f = new Font("Arial", Font.BOLD, 15);

        for (JRadioButton rb : new JRadioButton[]{rbMetodo, rbTiro, rbReglajeSelector}) {
            rb.setFont(f);
            rb.setBackground(Color.BLACK);
            rb.setForeground(Color.WHITE);
        }

        ButtonGroup g = new ButtonGroup();
        g.add(rbMetodo);
        g.add(rbTiro);
        g.add(rbReglajeSelector);

        rbMetodo.setSelected(true);
        rbReglajeSelector.setEnabled(false);

        rbMetodo.addActionListener(e ->
            cardAlternable.show(panelAlternable, "ataque")
        );

        rbTiro.addActionListener(e ->
            cardAlternable.show(panelAlternable, "tiro")
        );

        rbReglajeSelector.addActionListener(e ->
            cardAlternable.show(panelAlternable, "reglaje")
        );

        p.add(rbMetodo);
        p.add(rbTiro);
        p.add(rbReglajeSelector);

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
        
        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 12));
        grid.setBackground(Color.BLACK);
        
        Dimension comboSize = new Dimension(220, 30);
        String[] efectos = { "DESTRUIR", "NEUTRALIZAR", "SUPRIMIR"};
        comboEfectoDeseado = crearCombo(efectos, comboSize);
        grid.add(crearItem("EFECTO DESEADO:", comboEfectoDeseado));
        
        rbDisparosAtaque = crearRadio("DISPAROS", iconoRB);
        rbRafagaAtaque = crearRadio("RÁFAGA", iconoRB);
        agrupar(rbDisparosAtaque, rbRafagaAtaque);
        
        JPanel modoPanel = crearFila();
        modoPanel.add(rbDisparosAtaque);
        modoPanel.add(rbRafagaAtaque);
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
        comboGranada = crearCombo(new String[]{"HE","IL","WP", "HUMO"}, comboSize);
        grid.add(crearItem("GRANADA:", comboGranada));

        // ESPOLETA
        comboEspoleta = crearCombo(new String[]{"I","VT","CM", "DELAY"}, comboSize);
        grid.add(crearItem("ESPOLETA:", comboEspoleta));

        // VOLUMEN 
        txtVolumen = new JTextField();
        txtVolumen.setHorizontalAlignment(SwingConstants.CENTER);
        txtVolumen.setPreferredSize(comboSize);
        txtVolumen.setFont(new Font("Arial", Font.PLAIN, 15));
        txtVolumen.setForeground(Color.WHITE);
        txtVolumen.setBackground(new Color(60,60,60));
        grid.add(crearItem("VOLUMEN:", txtVolumen));

        comboHaz = crearCombo(new String[]{"PARALELO","CONVERGENTE","ABIERTO","ESPECIAL","CIRCULAR","E - LINEAL","E - CIRCULAR",
        		"E - RECTANGULAR","E - IRREGULAR","-"}, comboSize);
        grid.add(crearItem("HAZ:", comboHaz));
        
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTiroYControl() {

        JPanel panel = crearBlindado("TIRO Y CONTROL");
        JPanel grid = new JPanel(new GridLayout(4, 2, 15, 12));
        grid.setBackground(Color.BLACK);
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
        
        btnEnviar.addActionListener(e -> {
            notificarEnvioPIF();
            
            panelCorrecciones.reiniciarCuadricula();
            
            if (rbAMiOrden.isSelected()) {
                panelCorrecciones.getBtnFuego().setVisible(true);
                panelCorrecciones.getTimerFuego().start();
                cardCorrecciones.show(panelCard, "correcciones");
                panelMisionDeFuego.setVisible(false);
            } else {
                panelCorrecciones.getBtnFuego().setBackground(new Color(200,0,0)); 
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
    
    public String getFaseReglaje() {
    	if(btnFasePercusion.isSelected()) return "PERCUSION";
	    	else {
	    		if(btnFaseTiempo.isSelected()) return "TIEMPO";    		
	    			else {
	    				if(btnBarrera.isSelected()) return "BARRERA";    			
	    					else return "-";
	    				 }
	    		 }    	                                  
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
    	if(rbDisparosAtaque.isSelected() || rbDisparosReglaje.isSelected())
    		return "DISPAROS";
    	else {
    		if(rbRafagaAtaque.isSelected() || rbRafagaReglaje.isSelected())
    			return "RAFAGAS";
    		else return "-";
    	}
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
    
    public String orden() {
    	if(rbAMiOrden.isSelected())
    		return "A-MI-ORDEN";
    	if(rbCuandoListo.isSelected())
    		return "CUANDO-LISTO";
    	else
    		return "SIN-ORDEN-DE-FUEGO";
    }
    
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