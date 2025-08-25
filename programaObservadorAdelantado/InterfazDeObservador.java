import java.awt.*;
import javax.swing.*;

public class InterfazDeObservador extends JPanel {
    private static final long serialVersionUID = 1L;

    //  Variables internas 
    private String idOAA;
    private coordenadas coordOAA;
    private coordenadas coordBlanco;
    private String natBlanco;
    private String tipoAz;
    private String misionFuego;

    // IDs válidos 
    private static final String[] IDS_VALIDOS = {"juarez", "lopez", "lamas"};

    //  Métodos de acceso 
    public void setID(String id){ idOAA = id; }
    public String getID(){ return idOAA; }
    public void setCoordOAA(coordenadas c){ coordOAA = c; }
    public coordenadas getCoordOAA(){ return coordOAA; }
    public void setCoordBlanco(coordenadas c){ coordBlanco = c; }
    public coordenadas getCoordBlanco(){ return coordBlanco; }
    public void setNatBlanco(String s){ natBlanco = s; }
    public String getNatBlanco(){ return natBlanco; }
    public void setTipoAz(String s){ tipoAz = s; }
    public String getTipoAz(){ return tipoAz; }
    public void setMisionFuego(String s){ misionFuego = s; }
    public String getMisionFuego(){ return misionFuego; }

    //  GUI 
    private CardLayout cardLayout;
    private JPanel cards;

    private JRadioButton modoRect, modoPolar;

    private JPanel panelBlancoRect, panelBlancoPolar, panelOaaRect, panelOaaPolar;

    private JTextField txtBlancoX, txtBlancoY, txtBlancoCota;
    private JTextField txtBlancoDir, txtBlancoDist, txtBlancoAng;
    private JTextField txtOaaX, txtOaaY, txtOaaCota;
    private JTextField txtOaaDir, txtOaaDist, txtOaaAng;

    private JComboBox<String> cbMagnitud, cbTipoBlanco, cbActividad, cbProteccion;

    private int panelActual = 0;

    private JButton[] botonesMenu;
    private JButton btnAtras, btnSiguiente;

    public InterfazDeObservador() {
        // Pide la ID antes de cargar la interfaz
        pedirID();

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        //  Menú superior 
        JPanel menuSuperior = new JPanel(new GridLayout(1,3));
        menuSuperior.setBackground(Color.DARK_GRAY);
        String[] secciones = {"LOCALIZACION","INSTRUCCION DE TIRO","CONTROL"};

        botonesMenu = new JButton[secciones.length];
        for (int i = 0; i < secciones.length; i++) {
            JButton btn = new JButton(secciones[i]);
            btn.setForeground(Color.WHITE);
            btn.setBackground(Color.GRAY);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            botonesMenu[i] = btn;

            final int idx = i;
            btn.addActionListener(e -> {
                panelActual = idx;
                switch (idx) {
                    case 0 -> cardLayout.show(cards, "LOCALIZACION");
                    case 1 -> cardLayout.show(cards, "INSTRUCCION");
                    case 2 -> cardLayout.show(cards, "CONTROL");
                }
                actualizarBotonesMenu();
                actualizarBotonesNavegacion();
            });

            menuSuperior.add(btn);
        }
        add(menuSuperior, BorderLayout.NORTH);

        //  Cards 
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // ----- Panel LOCALIZACION -----
        JPanel panelIzq = new JPanel();
        panelIzq.setBackground(Color.BLACK);
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));

        // MISION DE FUEGO
        JPanel panelMision = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMision.setBackground(Color.BLACK);
        panelMision.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "MISION DE FUEGO"
        ));
        ((javax.swing.border.TitledBorder)panelMision.getBorder()).setTitleColor(Color.WHITE);

        JRadioButton rbReglaje = new JRadioButton("Reglaje");
        JRadioButton rbEficacia = new JRadioButton("Eficacia");

        ButtonGroup grupoMision = new ButtonGroup();
        grupoMision.add(rbReglaje);
        grupoMision.add(rbEficacia);

        // estilos y tamaño igual que coordenadas
        rbReglaje.setFont(new Font("Arial", Font.BOLD, 20));
        rbEficacia.setFont(new Font("Arial", Font.BOLD, 20));
        rbReglaje.setForeground(Color.WHITE);
        rbEficacia.setForeground(Color.WHITE);
        rbReglaje.setBackground(Color.BLACK);
        rbEficacia.setBackground(Color.BLACK);

        rbReglaje.addActionListener(e -> setMisionFuego("Reglaje"));
        rbEficacia.addActionListener(e -> setMisionFuego("Eficacia"));

        panelMision.add(rbReglaje);
        panelMision.add(rbEficacia);
        panelIzq.add(panelMision);

        // Panel Coordenadas con borde separado
        JPanel panelCoord = new JPanel();
        panelCoord.setBackground(Color.BLACK);
        panelCoord.setLayout(new BoxLayout(panelCoord, BoxLayout.Y_AXIS));
        panelCoord.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "COORDENADAS"
        ));
        ((javax.swing.border.TitledBorder)panelCoord.getBorder()).setTitleColor(Color.WHITE);

        JPanel panelMetodo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMetodo.setBackground(Color.BLACK);
        modoRect = new JRadioButton("Rectangulares");
        modoPolar = new JRadioButton("Polares");
        modoRect.setFont(new Font("Arial", Font.BOLD, 20));
        modoPolar.setFont(new Font("Arial", Font.BOLD, 20));
        modoRect.setForeground(Color.WHITE);
        modoPolar.setForeground(Color.WHITE);
        modoRect.setBackground(Color.BLACK);
        modoPolar.setBackground(Color.BLACK);
        ButtonGroup grupoMetodo = new ButtonGroup();
        grupoMetodo.add(modoRect);
        grupoMetodo.add(modoPolar);
        panelMetodo.add(modoRect);
        panelMetodo.add(modoPolar);
        panelCoord.add(panelMetodo);

        // Blanco Rect
        panelBlancoRect = new JPanel(new GridLayout(1,3,10,10));
        panelBlancoRect.setBackground(Color.BLACK);
        panelBlancoRect.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "POSICION DEL BLANCO"
        ));
        ((javax.swing.border.TitledBorder)panelBlancoRect.getBorder()).setTitleColor(Color.WHITE);

        txtBlancoX = new JTextField(); estilizarCampo(txtBlancoX, "X");
        txtBlancoY = new JTextField(); estilizarCampo(txtBlancoY, "Y");
        txtBlancoCota = new JTextField(); estilizarCampo(txtBlancoCota, "Cota");
        panelBlancoRect.add(txtBlancoX); panelBlancoRect.add(txtBlancoY); panelBlancoRect.add(txtBlancoCota);

        // Blanco Polar
        panelBlancoPolar = new JPanel(new GridLayout(1,3,10,10));
        panelBlancoPolar.setBackground(Color.BLACK);
        panelBlancoPolar.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "POSICION DEL BLANCO"
        ));
        ((javax.swing.border.TitledBorder)panelBlancoPolar.getBorder()).setTitleColor(Color.WHITE);

        txtBlancoDir = new JTextField(); estilizarCampo(txtBlancoDir, "Dir");
        txtBlancoDist = new JTextField(); estilizarCampo(txtBlancoDist, "Dist");
        txtBlancoAng = new JTextField(); estilizarCampo(txtBlancoAng, "Ang");
        panelBlancoPolar.add(txtBlancoDir); panelBlancoPolar.add(txtBlancoDist); panelBlancoPolar.add(txtBlancoAng);

        // OAA Rect
        panelOaaRect = new JPanel(new GridLayout(1,3,10,10));
        panelOaaRect.setBackground(Color.BLACK);
        panelOaaRect.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "POSICION DEL OBSERVADOR"
        ));
        ((javax.swing.border.TitledBorder)panelOaaRect.getBorder()).setTitleColor(Color.WHITE);

        txtOaaX = new JTextField(); estilizarCampo(txtOaaX, "X");
        txtOaaY = new JTextField(); estilizarCampo(txtOaaY, "Y");
        txtOaaCota = new JTextField(); estilizarCampo(txtOaaCota, "Cota");
        panelOaaRect.add(txtOaaX); panelOaaRect.add(txtOaaY); panelOaaRect.add(txtOaaCota);

        // OAA Polar
        panelOaaPolar = new JPanel(new GridLayout(1,3,10,10));
        panelOaaPolar.setBackground(Color.BLACK);
        panelOaaPolar.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "POSICION DEL OBSERVADOR"));
        ((javax.swing.border.TitledBorder)panelOaaPolar.getBorder()).setTitleColor(Color.WHITE);

        txtOaaDir = new JTextField(); estilizarCampo(txtOaaDir, "Dir");
        txtOaaDist = new JTextField(); estilizarCampo(txtOaaDist, "Dist");
        txtOaaAng = new JTextField(); estilizarCampo(txtOaaAng, "Ang");
        panelOaaPolar.add(txtOaaDir); panelOaaPolar.add(txtOaaDist); panelOaaPolar.add(txtOaaAng);

        // Por defecto Rectangulares
        panelCoord.add(panelBlancoRect);
        panelCoord.add(panelOaaRect);

        // Listener radio buttons
        modoRect.addActionListener(e -> {
            panelCoord.remove(panelBlancoPolar);
            panelCoord.remove(panelOaaPolar);
            panelCoord.add(panelBlancoRect, 1);
            panelCoord.add(panelOaaRect, 2);
            panelCoord.revalidate(); panelCoord.repaint();
        });
        modoPolar.addActionListener(e -> {
            panelCoord.remove(panelBlancoRect);
            panelCoord.remove(panelOaaRect);
            panelCoord.add(panelBlancoPolar, 1);
            panelCoord.add(panelOaaPolar, 2);
            panelCoord.revalidate(); panelCoord.repaint();
        });

        panelIzq.add(panelCoord);

        // Panel mapa derecha
        JPanel panelMapa = new JPanel();
        panelMapa.setBackground(Color.DARK_GRAY);
        JLabel lblMapa = new JLabel("MAPA");
        lblMapa.setForeground(Color.WHITE);
        panelMapa.add(lblMapa);

        // Split
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzq, panelMapa);
        split.setDividerLocation(400);

        JPanel panelLocalizacion = new JPanel(new BorderLayout());
        panelLocalizacion.add(split, BorderLayout.CENTER);
        cards.add(panelLocalizacion, "LOCALIZACION");

        // ----- Panel INSTRUCCION DE TIRO -----
        JPanel panelInstruccion = new JPanel();
        panelInstruccion.setBackground(Color.BLACK);
        panelInstruccion.setLayout(new BoxLayout(panelInstruccion, BoxLayout.Y_AXIS));

        cbMagnitud = new JComboBox<>(new String[]{"SECCION","COMPAÑIA","BATALLON"});
        cbTipoBlanco = new JComboBox<>(new String[]{"PERSONAL","VEHICULO","PIEZA"});
        cbActividad = new JComboBox<>(new String[]{"OFENSIVO","DEFENSIVO"});
        cbProteccion = new JComboBox<>(new String[]{"SIN","ATRINCHERADO","FORTIFICADO"});

        estilizarCombo(cbMagnitud);
        estilizarCombo(cbTipoBlanco);
        estilizarCombo(cbActividad);
        estilizarCombo(cbProteccion);

        panelInstruccion.add(crearFilaCombo(cbMagnitud));
        panelInstruccion.add(crearFilaCombo(cbTipoBlanco));
        panelInstruccion.add(crearFilaCombo(cbActividad));
        panelInstruccion.add(crearFilaCombo(cbProteccion));

        cards.add(panelInstruccion, "INSTRUCCION");

        // ----- Panel CONTROL -----
        JPanel panelControl = new JPanel();
        panelControl.setBackground(Color.BLACK);
        JLabel lblCtrl = new JLabel("CONTROL (en construcción)");
        lblCtrl.setForeground(Color.WHITE);
        panelControl.add(lblCtrl);
        cards.add(panelControl, "CONTROL");

        add(cards, BorderLayout.CENTER);

        // ===== Botones ATRÁS y SIGUIENTE =====
        JPanel panelBotones = new JPanel(new GridLayout(1,2));
        btnAtras = new JButton("ATRÁS");
        btnAtras.setBackground(Color.red); btnAtras.setForeground(Color.WHITE);
        btnAtras.addActionListener(e -> retrocederPanel());

        btnSiguiente = new JButton("SIGUIENTE"); btnSiguiente.setForeground(Color.WHITE);
        btnSiguiente.setBackground(Color.green);
        btnSiguiente.addActionListener(e -> avanzarPanel());

        panelBotones.add(btnAtras);
        panelBotones.add(btnSiguiente);
        add(panelBotones, BorderLayout.SOUTH);

        cardLayout.show(cards, "LOCALIZACION");
        actualizarBotonesMenu();
        actualizarBotonesNavegacion();
    }

    //  Login de ID 
    private void pedirID() {
        while (true) {
            String idIngresado = JOptionPane.showInputDialog(
                null,
                "Ingrese su ID de observador:",
                "Autenticación requerida",
                JOptionPane.QUESTION_MESSAGE
            );

            if (idIngresado == null) {
                System.exit(0); // Si cancela, cerrar programa
            }

            for (String valido : IDS_VALIDOS) {
                if (idIngresado.equals(valido)) {
                    setID(idIngresado);
                    JOptionPane.showMessageDialog(null,
                        "Acceso concedido. Bienvenido " + idIngresado,
                        "Correcto",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
            }

            JOptionPane.showMessageDialog(null,
                "ID incorrecto. Intente de nuevo.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    //  Helpers
    private void actualizarBotonesMenu() {
        for (int i = 0; i < botonesMenu.length; i++) {
            botonesMenu[i].setBackground(i == panelActual ? Color.BLUE : Color.GRAY);
        }
    }

    private void actualizarBotonesNavegacion() {
        btnAtras.setVisible(panelActual > 0);
    }

    private void avanzarPanel() {
        if(panelActual == 0){
            panelActual++;
            cardLayout.show(cards, "INSTRUCCION");
        } else if(panelActual == 1){
            panelActual++;
            cardLayout.show(cards, "CONTROL");
        }
        actualizarBotonesMenu();
        actualizarBotonesNavegacion();
    }

    private void retrocederPanel() {
        if(panelActual > 0){
            panelActual--;
            if(panelActual == 0) cardLayout.show(cards, "LOCALIZACION");
            if(panelActual == 1) cardLayout.show(cards, "INSTRUCCION");
        }
        actualizarBotonesMenu();
        actualizarBotonesNavegacion();
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.BLACK);
        field.setText(placeholder);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if(field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if(field.getText().isEmpty()) {
                    field.setForeground(Color.BLACK);
                    field.setText(placeholder);
                }
            }
        });
    }

    private void estilizarCampo(JTextField field, String placeholder) {
        addPlaceholder(field, placeholder);
        field.setFont(new Font("Arial", Font.PLAIN, 18));
        field.setPreferredSize(new Dimension(80, 25));
        field.setMaximumSize(new Dimension(100, 25));
        field.setBackground(Color.DARK_GRAY);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setPreferredSize(new Dimension(150, 25));
        combo.setMaximumSize(new Dimension(160, 25));
        combo.setBackground(Color.DARK_GRAY);
        combo.setForeground(Color.WHITE);
    }

    private JPanel crearFilaCombo(JComboBox<String> combo) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fila.setBackground(Color.BLACK);
        fila.add(combo);
        return fila;
    }
}
