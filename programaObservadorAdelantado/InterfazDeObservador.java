import java.awt.*;
import javax.swing.*;

public class InterfazDeObservador extends JPanel {
    private static final long serialVersionUID = 1L;

    // ===== Variables internas =====
    private String idOAA;
    private coordenadas coordOAA;
    private coordenadas coordBlanco;
    private String natBlanco;
    private String tipoAz;

    // ===== Métodos de acceso =====
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

    // ===== GUI =====
    private CardLayout cardLayout;
    private JPanel cards;

    private JRadioButton modoRect, modoPolar;

    // Paneles de coordenadas
    private JPanel panelBlancoRect, panelBlancoPolar, panelOaaRect, panelOaaPolar;

    // Campos texto
    private JTextField txtBlancoX, txtBlancoY, txtBlancoCota;
    private JTextField txtBlancoDir, txtBlancoDist, txtBlancoAng;
    private JTextField txtOaaX, txtOaaY, txtOaaCota;
    private JTextField txtOaaDir, txtOaaDist, txtOaaAng;

    // NATURALEZA
    private JComboBox<String> cbMagnitud, cbTipoBlanco, cbActividad, cbProteccion;

    private int panelActual = 0;

    // Botones del menú superior
    private JButton[] botonesMenu;

    // Botones de navegación
    private JButton btnAtras, btnSiguiente;

    public InterfazDeObservador() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // ===== Menú superior =====
        JPanel menuSuperior = new JPanel(new GridLayout(1,4));
        menuSuperior.setBackground(Color.DARK_GRAY);
        String[] secciones = {"LOCALIZACIÓN","NATURALEZA","TIRO A EFECTUAR","CONTROL"};

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
                    case 1 -> cardLayout.show(cards, "NATURALEZA");
                }
                actualizarBotonesMenu();
                actualizarBotonesNavegacion();
            });

            menuSuperior.add(btn);
        }
        add(menuSuperior, BorderLayout.NORTH);

        // ===== Cards =====
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // ----- Panel LOCALIZACION -----
        JPanel panelIzq = new JPanel();
        panelIzq.setBackground(Color.BLACK);
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));

        // Método de coordenadas
        JPanel panelMetodo = new JPanel(new FlowLayout());
        panelMetodo.setBackground(Color.BLACK);
        modoRect = new JRadioButton("Rectangulares");
        modoPolar = new JRadioButton("Polares");
        modoRect.setFont(new Font("Arial", Font.BOLD, 16));
        modoPolar.setFont(new Font("Arial", Font.BOLD, 16));
        modoRect.setForeground(Color.BLACK);
        modoPolar.setForeground(Color.BLACK);
        ButtonGroup grupoMetodo = new ButtonGroup();
        grupoMetodo.add(modoRect);
        grupoMetodo.add(modoPolar);
        panelMetodo.add(modoRect);
        panelMetodo.add(modoPolar);
        panelIzq.add(panelMetodo);

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
            BorderFactory.createLineBorder(Color.GRAY), "POSICION DEL OBSERVADOR"
        ));
        ((javax.swing.border.TitledBorder)panelOaaPolar.getBorder()).setTitleColor(Color.WHITE);

        txtOaaDir = new JTextField(); estilizarCampo(txtOaaDir, "Dir");
        txtOaaDist = new JTextField(); estilizarCampo(txtOaaDist, "Dist");
        txtOaaAng = new JTextField(); estilizarCampo(txtOaaAng, "Ang");
        panelOaaPolar.add(txtOaaDir); panelOaaPolar.add(txtOaaDist); panelOaaPolar.add(txtOaaAng);

        // Al inicio solo muestro Rect
        panelIzq.add(panelBlancoRect);
        panelIzq.add(panelOaaRect);

        // Listener radio buttons
        modoRect.addActionListener(e -> {
            panelIzq.remove(panelBlancoPolar);
            panelIzq.remove(panelOaaPolar);
            panelIzq.add(panelBlancoRect, 1);
            panelIzq.add(panelOaaRect, 2);
            panelIzq.revalidate(); panelIzq.repaint();
        });
        modoPolar.addActionListener(e -> {
            panelIzq.remove(panelBlancoRect);
            panelIzq.remove(panelOaaRect);
            panelIzq.add(panelBlancoPolar, 1);
            panelIzq.add(panelOaaPolar, 2);
            panelIzq.revalidate(); panelIzq.repaint();
        });

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

        // ----- Panel NATURALEZA -----
        JPanel panelNaturaleza = new JPanel();
        panelNaturaleza.setBackground(Color.BLACK);
        panelNaturaleza.setLayout(new BoxLayout(panelNaturaleza, BoxLayout.Y_AXIS));

        cbMagnitud = new JComboBox<>(new String[]{"SECCION","COMPAÑIA","BATALLON"});
        cbTipoBlanco = new JComboBox<>(new String[]{"PERSONAL","VEHICULO","PIEZA"});
        cbActividad = new JComboBox<>(new String[]{"OFENSIVO","DEFENSIVO"});
        cbProteccion = new JComboBox<>(new String[]{"SIN","ATRINCHERADO","FORTIFICADO"});

        estilizarCombo(cbMagnitud);
        estilizarCombo(cbTipoBlanco);
        estilizarCombo(cbActividad);
        estilizarCombo(cbProteccion);

        panelNaturaleza.add(crearFilaCombo(cbMagnitud));
        panelNaturaleza.add(crearFilaCombo(cbTipoBlanco));
        panelNaturaleza.add(crearFilaCombo(cbActividad));
        panelNaturaleza.add(crearFilaCombo(cbProteccion));

        cards.add(panelNaturaleza, "NATURALEZA");

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

    // ===== Helpers =====
    private void actualizarBotonesMenu() {
        for (int i = 0; i < botonesMenu.length; i++) {
            if (i == panelActual) {
                botonesMenu[i].setBackground(Color.BLUE);
            } else {
                botonesMenu[i].setBackground(Color.GRAY);
            }
        }
    }

    private void actualizarBotonesNavegacion() {
        btnAtras.setVisible(panelActual > 0);
    }

    private void avanzarPanel() {
        if(panelActual == 0){
            panelActual++;
            cardLayout.show(cards, "NATURALEZA");
        } else if(panelActual == 1){
            panelActual++;
        }
        actualizarBotonesMenu();
        actualizarBotonesNavegacion();
    }

    private void retrocederPanel() {
        if(panelActual > 0){
            panelActual--;
            if(panelActual == 0) cardLayout.show(cards, "LOCALIZACION");
            if(panelActual == 1) cardLayout.show(cards, "NATURALEZA");
        }
        actualizarBotonesMenu();
        actualizarBotonesNavegacion();
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.BLACK); // ahora negro en vez de gris
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
                    field.setForeground(Color.BLACK); // vuelve negro si está vacío
                    field.setText(placeholder);
                }
            }
        });
    }

    private void estilizarCampo(JTextField field, String placeholder) {
        addPlaceholder(field, placeholder);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
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
