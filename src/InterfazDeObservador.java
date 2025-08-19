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
    private JTextField txtBlancoX, txtBlancoY, txtBlancoCota;
    private JTextField txtBlancoDir, txtBlancoDist, txtBlancoAng;
    private JTextField txtOaaX, txtOaaY, txtOaaCota;
    private JTextField txtOaaDir, txtOaaDist, txtOaaAng;

    // NATURALEZA
    private JComboBox<String> cbMagnitud, cbTipoBlanco, cbActividad, cbProteccion;
    private JTextField txtFrente, txtProfundidad;

    private int panelActual = 0;

    // Botones del menú superior
    private JButton[] botonesMenu;

    public InterfazDeObservador(InterfazUsuario padre) {
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
                    // Puedes agregar los otros paneles si existen
                }
                actualizarBotonesMenu();
            });

            menuSuperior.add(btn);
        }
        add(menuSuperior, BorderLayout.NORTH);

        // ===== Cards =====
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // ----- Panel LOCALIZACION -----
        JPanel panelLocalizacion = new JPanel();
        panelLocalizacion.setBackground(Color.BLACK);
        panelLocalizacion.setLayout(new BoxLayout(panelLocalizacion, BoxLayout.Y_AXIS));

        // Método de coordenadas
        JPanel panelMetodo = new JPanel(new FlowLayout());
        panelMetodo.setBackground(Color.BLACK);
        modoRect = new JRadioButton("Coordenadas (Rectangulares)");
        modoPolar = new JRadioButton("Polar");
        modoRect.setFont(new Font("Arial", Font.BOLD, 18));
        modoPolar.setFont(new Font("Arial", Font.BOLD, 18));
        modoRect.setForeground(Color.black);
        modoPolar.setForeground(Color.black);
        ButtonGroup grupoMetodo = new ButtonGroup();
        grupoMetodo.add(modoRect);
        grupoMetodo.add(modoPolar);
        panelMetodo.add(modoRect);
        panelMetodo.add(modoPolar);
        panelLocalizacion.add(panelMetodo);

        // Panel Blanco
        JPanel panelBlanco = new JPanel(new GridLayout(2,3,10,10));
        panelBlanco.setBackground(Color.BLACK);
        panelBlanco.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Posicion del Blanco",
            0,0,new Font("Arial",Font.BOLD,16), Color.WHITE));
        txtBlancoX = new JTextField(); addPlaceholder(txtBlancoX, "X");
        txtBlancoY = new JTextField(); addPlaceholder(txtBlancoY, "Y");
        txtBlancoCota = new JTextField(); addPlaceholder(txtBlancoCota, "Cota");
        txtBlancoDir = new JTextField(); addPlaceholder(txtBlancoDir, "Dirección");
        txtBlancoDist = new JTextField(); addPlaceholder(txtBlancoDist, "Distancia");
        txtBlancoAng = new JTextField(); addPlaceholder(txtBlancoAng, "Ángulo V");
        panelBlanco.add(txtBlancoX); panelBlanco.add(txtBlancoY); panelBlanco.add(txtBlancoCota);
        panelBlanco.add(txtBlancoDir); panelBlanco.add(txtBlancoDist); panelBlanco.add(txtBlancoAng);
        panelLocalizacion.add(panelBlanco);

        // Panel OAA
        JPanel panelOaa = new JPanel(new GridLayout(2,3,10,10));
        panelOaa.setBackground(Color.BLACK);
        panelOaa.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Posicion del Observador Adelantado",
            0,0,new Font("Arial",Font.BOLD,16), Color.WHITE));
        txtOaaX = new JTextField(); addPlaceholder(txtOaaX, "X");
        txtOaaY = new JTextField(); addPlaceholder(txtOaaY, "Y");
        txtOaaCota = new JTextField(); addPlaceholder(txtOaaCota, "Cota");
        txtOaaDir = new JTextField(); addPlaceholder(txtOaaDir, "Dirección");
        txtOaaDist = new JTextField(); addPlaceholder(txtOaaDist, "Distancia");
        txtOaaAng = new JTextField(); addPlaceholder(txtOaaAng, "Ángulo V");
        panelOaa.add(txtOaaX); panelOaa.add(txtOaaY); panelOaa.add(txtOaaCota);
        panelOaa.add(txtOaaDir); panelOaa.add(txtOaaDist); panelOaa.add(txtOaaAng);
        panelLocalizacion.add(panelOaa);

        cards.add(panelLocalizacion, "LOCALIZACION");

        // ----- Panel NATURALEZA -----
        JPanel panelNaturaleza = new JPanel();
        panelNaturaleza.setBackground(Color.BLACK);
        panelNaturaleza.setLayout(new BoxLayout(panelNaturaleza, BoxLayout.Y_AXIS));

        JPanel panelDim = new JPanel(new GridBagLayout());
        panelDim.setBackground(Color.BLACK);
        panelDim.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Naturaleza del Blanco",
            0,0,new Font("Arial",Font.BOLD,18), Color.WHITE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        JLabel lblMagnitud = new JLabel("Magnitud:");
        lblMagnitud.setFont(labelFont);
        lblMagnitud.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0;
        panelDim.add(lblMagnitud, gbc);

        cbMagnitud = new JComboBox<>(new String[]{"SECCION","COMPAÑIA","BATALLON","BRIGADA"});
        cbMagnitud.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 0;
        panelDim.add(cbMagnitud, gbc);

        JLabel lblTipoBlanco = new JLabel("Tipo de Blanco:");
        lblTipoBlanco.setFont(labelFont);
        lblTipoBlanco.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1;
        panelDim.add(lblTipoBlanco, gbc);

        cbTipoBlanco = new JComboBox<>(new String[]{"PERSONAL_DESPLEGADO","VEHICULO","PIEZA_ARTILLERIA"});
        cbTipoBlanco.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 1;
        panelDim.add(cbTipoBlanco, gbc);

        JLabel lblActividad = new JLabel("Actividad:");
        lblActividad.setFont(labelFont);
        lblActividad.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 2;
        panelDim.add(lblActividad, gbc);

        cbActividad = new JComboBox<>(new String[]{"OFENSIVO","DEFENSIVO"});
        cbActividad.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 2;
        panelDim.add(cbActividad, gbc);

        JLabel lblProteccion = new JLabel("Protección:");
        lblProteccion.setFont(labelFont);
        lblProteccion.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3;
        panelDim.add(lblProteccion, gbc);

        cbProteccion = new JComboBox<>(new String[]{"SIN_PROTECCION","ATRINCHERADO","FORTIFICADO"});
        cbProteccion.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 3;
        panelDim.add(cbProteccion, gbc);

        panelNaturaleza.add(panelDim);

        cards.add(panelNaturaleza, "NATURALEZA");

        add(cards, BorderLayout.CENTER);

        // ===== Botones ATRÁS y SIGUIENTE =====
        JPanel panelBotones = new JPanel(new GridLayout(1,2));
        JButton btnAtras = new JButton("ATRÁS");
        JButton btnSiguiente = new JButton("SIGUIENTE");

        btnAtras.setBackground(new Color(128,0,0));
        btnAtras.setForeground(Color.WHITE);
        btnAtras.setFont(new Font("Arial", Font.BOLD, 20));

        btnSiguiente.setBackground(new Color(0,128,0));
        btnSiguiente.setForeground(Color.WHITE);
        btnSiguiente.setFont(new Font("Arial", Font.BOLD, 20));

        btnAtras.addActionListener(e -> retrocederPanel());
        btnSiguiente.addActionListener(e -> avanzarPanel());

        panelBotones.add(btnAtras);
        panelBotones.add(btnSiguiente);

        add(panelBotones, BorderLayout.SOUTH);

        cardLayout.show(cards, "LOCALIZACION");
        actualizarBotonesMenu();
    }

    // ===== Actualiza colores de los botones del menú superior =====
    private void actualizarBotonesMenu() {
        for (int i = 0; i < botonesMenu.length; i++) {
            if (i == panelActual) {
                botonesMenu[i].setBackground(Color.BLUE); // Activo
                botonesMenu[i].setForeground(Color.WHITE);
            } else {
                botonesMenu[i].setBackground(Color.GRAY); // Inactivo
                botonesMenu[i].setForeground(Color.WHITE);
            }
        }
    }

    private void avanzarPanel() {
        if(panelActual == 0){
            guardarDatosLocalizacion();
            panelActual++;
            cardLayout.show(cards, "NATURALEZA");
            actualizarBotonesMenu();
        } else if(panelActual == 1){
            guardarDatosNaturaleza();
            panelActual++;
            actualizarBotonesMenu();
        }
    }

    private void retrocederPanel() {
        if(panelActual == 1){
            panelActual--;
            cardLayout.show(cards, "LOCALIZACION");
            actualizarBotonesMenu();
        } else if(panelActual == 2){
            panelActual--;
            cardLayout.show(cards, "NATURALEZA");
            actualizarBotonesMenu();
        }
    }

    private void guardarDatosLocalizacion() {
        try {
            if (modoRect.isSelected()) {
                coordRectangulares blanco = new coordRectangulares();
                blanco.setX(Double.parseDouble(txtBlancoX.getText()));
                blanco.setY(Double.parseDouble(txtBlancoY.getText()));
                blanco.setCota(Double.parseDouble(txtBlancoCota.getText()));
                setCoordBlanco(blanco);

                coordRectangulares oaa = new coordRectangulares();
                oaa.setX(Double.parseDouble(txtOaaX.getText()));
                oaa.setY(Double.parseDouble(txtOaaY.getText()));
                oaa.setCota(Double.parseDouble(txtOaaCota.getText()));
                setCoordOAA(oaa);
            } else if (modoPolar.isSelected()) {
                coordPolares blanco = new coordPolares();
                blanco.setDireccion(Double.parseDouble(txtBlancoDir.getText()));
                blanco.setDistancia(Double.parseDouble(txtBlancoDist.getText()));
                blanco.setAnguloVertical(Double.parseDouble(txtBlancoAng.getText()));
                setCoordBlanco(blanco);

                coordPolares oaa = new coordPolares();
                oaa.setDireccion(Double.parseDouble(txtOaaDir.getText()));
                oaa.setDistancia(Double.parseDouble(txtOaaDist.getText()));
                oaa.setAnguloVertical(Double.parseDouble(txtOaaAng.getText()));
                setCoordOAA(oaa);
            }
            JOptionPane.showMessageDialog(this, "Datos de LOCALIZACION guardados", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Error: revise los datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarDatosNaturaleza() {
        natBlanco = cbMagnitud.getSelectedItem().toString();
        tipoAz = cbTipoBlanco.getSelectedItem().toString();
        JOptionPane.showMessageDialog(this, "Datos de NATURALEZA guardados", "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== Método auxiliar para Placeholders =====
    private void addPlaceholder(JTextField field, String placeholder) {
        field.setBackground(Color.BLACK);   // Fondo negro
        field.setForeground(Color.GRAY);    // Texto inicial gris
        field.setCaretColor(Color.WHITE);   // Cursor blanco
        field.setText(placeholder);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if(field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if(field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
}

