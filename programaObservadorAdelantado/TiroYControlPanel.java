import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class TiroYControlPanel extends JPanel {

    private JButton btnEnviar, btnCuandoListo, btnAMiOrden;

    // === Controles declarados como atributos para poder accederlos ===
    private JComboBox<String> comboPiezas;
    private JComboBox<String> comboSeccion;
    private JRadioButton rbPiqueSi, rbPiqueNo;
    private JRadioButton rbFgoSi, rbFgoNo;
    private JRadioButton rbTesSi, rbTesNo;
    private JTextField txtTot;

    public TiroYControlPanel() {
        setBackground(Color.BLACK);
        setLayout(new GridLayout(2, 2, 5, 5)); // 2x2 celdas

        // ====== PANEL TYC (ARRIBA IZQUIERDA) ======
        JPanel panelTYC1 = crearPanelTYC();
        panelTYC1.setLayout(new GridBagLayout());
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.insets = new Insets(5, 5, 5, 5);
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.anchor = GridBagConstraints.WEST;

        comboPiezas = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6"});
        comboPiezas.setFont(new Font("Arial", Font.PLAIN, 16));
        comboPiezas.setPreferredSize(new Dimension(80, 28));

        comboSeccion = new JComboBox<>(new String[]{"IZQUIERDA", "DERECHA"});
        comboSeccion.setFont(new Font("Arial", Font.PLAIN, 16));
        comboSeccion.setPreferredSize(new Dimension(120, 28));

        // Fila PIEZAS
        gbc1.gridx = 0; gbc1.gridy = 0;
        panelTYC1.add(crearLabelConfig("PIEZAS"), gbc1);
        gbc1.gridx = 1;
        panelTYC1.add(comboPiezas, gbc1);

        // Fila SECCION
        gbc1.gridx = 0; gbc1.gridy = 1;
        panelTYC1.add(crearLabelConfig("SECCION"), gbc1);
        gbc1.gridx = 1;
        panelTYC1.add(comboSeccion, gbc1);

        // Fila PIQUE
        gbc1.gridx = 0; gbc1.gridy = 2; gbc1.gridwidth = 2;
        panelTYC1.add(crearRadioGroupAlineado("PIQUE:", true), gbc1);

        // ====== PANEL TYC (ABAJO IZQUIERDA) ======
        JPanel panelTYC2 = crearPanelTYC();
        panelTYC2.setLayout(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(10, 10, 10, 10);
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.anchor = GridBagConstraints.WEST;

        // FGO CONT.
        gbc2.gridx = 0; gbc2.gridy = 0;
        panelTYC2.add(crearRadioGroupAlineado("FGO CONT:", false), gbc2);

        // TES
        gbc2.gridy = 1;
        panelTYC2.add(crearRadioGroupTes(), gbc2);

        // TOT
        gbc2.gridy = 2;
        JPanel totPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        totPanel.setBackground(Color.BLACK);

        JLabel lblTot = new JLabel(" TOT: ");
        lblTot.setForeground(Color.WHITE);
        lblTot.setFont(new Font("Arial", Font.BOLD, 18));
        
        txtTot = new JTextField(" seg");
        txtTot.setForeground(Color.GRAY);
        txtTot.setFont(new Font("Arial", Font.PLAIN, 18));
        txtTot.setPreferredSize(new Dimension(40, 28));

        txtTot.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtTot.getText().equals(" seg")) {
                    txtTot.setText("");
                    txtTot.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtTot.getText().isEmpty()) {
                    txtTot.setText(" seg");
                    txtTot.setForeground(Color.GRAY);
                }
            }
        });

        totPanel.add(lblTot, BorderLayout.WEST);
        totPanel.add(txtTot, BorderLayout.CENTER);
        panelTYC2.add(totPanel, gbc2);

        // ====== PANEL ACCIONES (ABAJO DERECHA) ======
        JPanel panelAcciones = new JPanel(new GridBagLayout());
        panelAcciones.setBackground(Color.BLACK);
        panelAcciones.setBorder(crearBordeTitulo("Acciones"));

        GridBagConstraints ga = new GridBagConstraints();
        ga.insets = new Insets(8, 8, 8, 8);
        ga.fill = GridBagConstraints.NONE;

        Dimension btnSize = new Dimension(150, 50);

        btnCuandoListo = new JButton("CUANDO LISTO");
        btnAMiOrden   = new JButton("A MI ORDEN");
        btnEnviar     = new JButton("ENVIAR");

        for (JButton b : new JButton[]{btnCuandoListo, btnAMiOrden, btnEnviar}) {
            b.setBackground(new Color(60, 60, 60));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setPreferredSize(btnSize);
            b.setFont(new Font("Arial", Font.BOLD, 14));
        }

        btnCuandoListo.setBackground(new Color(179,179,93));
        btnAMiOrden.setBackground(new Color(179,179,93));
        btnEnviar.setBackground(new Color(171,50,50));

        ga.gridx = 0; ga.gridy = 0; panelAcciones.add(btnCuandoListo, ga);
        ga.gridx = 1; ga.gridy = 0; panelAcciones.add(btnAMiOrden, ga);
        ga.gridx = 0; ga.gridy = 1; ga.gridwidth = 2; panelAcciones.add(btnEnviar, ga);

        // ====== AGREGAR CELDAS ======
        add(panelTYC1);    // arriba izquierda
        JPanel filler = new JPanel();
        filler.setBackground(Color.BLACK);
        add(filler);
        add(panelTYC2);    // abajo izquierda	
        add(panelAcciones);// abajo derecha
    }

    // Método para crear JLabel con estilo
    private JLabel crearLabelConfig(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        return lbl;
    }

    // Radio group PIQUE y FGO
    private JPanel crearRadioGroupAlineado(String titulo, boolean esPique) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(72,82,122));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lbl = new JLabel(titulo);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lbl, gbc);

        JRadioButton rbSi = new JRadioButton("Sí");
        JRadioButton rbNo = new JRadioButton("No");

        rbSi.setBackground(new Color(72,82,122));
        rbNo.setBackground(new Color(72,82,122));
        rbSi.setForeground(Color.WHITE);
        rbNo.setForeground(Color.WHITE);

        rbSi.setFont(new Font("Arial", Font.PLAIN, 18));
        rbNo.setFont(new Font("Arial", Font.PLAIN, 18));

        Icon bigIcon = new RadioButtonGrande(24);
        rbSi.setIcon(bigIcon);
        rbNo.setIcon(bigIcon);

        ButtonGroup group = new ButtonGroup();
        group.add(rbSi);
        group.add(rbNo);

        gbc.gridx = 1; panel.add(rbSi, gbc);
        gbc.gridx = 2; panel.add(rbNo, gbc);

        if (esPique) {
            rbPiqueSi = rbSi;
            rbPiqueNo = rbNo;
        } else {
            rbFgoSi = rbSi;
            rbFgoNo = rbNo;
        }

        return panel;
    }

    // Radio group TES
    private JPanel crearRadioGroupTes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(72,82,122));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lbl = new JLabel("TES:           ");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lbl, gbc);

        rbTesSi = new JRadioButton("Sí");
        rbTesNo = new JRadioButton("No");

        rbTesSi.setBackground(new Color(72,82,122));
        rbTesNo.setBackground(new Color(72,82,122));
        rbTesSi.setForeground(Color.WHITE);
        rbTesNo.setForeground(Color.WHITE);

        rbTesSi.setFont(new Font("Arial", Font.PLAIN, 18));
        rbTesNo.setFont(new Font("Arial", Font.PLAIN, 18));

        Icon bigIcon = new RadioButtonGrande(24);
        rbTesSi.setIcon(bigIcon);
        rbTesNo.setIcon(bigIcon);

        ButtonGroup group = new ButtonGroup();
        group.add(rbTesSi);
        group.add(rbTesNo);

        gbc.gridx = 1; panel.add(rbTesSi, gbc);
        gbc.gridx = 2; panel.add(rbTesNo, gbc);

        return panel;
    }

    private JPanel crearPanelTYC() {
        JPanel p = new JPanel();
        p.setBackground(Color.BLACK);
        p.setBorder(crearBordeTitulo(""));
        return p;
    }

    private TitledBorder crearBordeTitulo(String titulo) {
        Font fuente = new Font("Arial", Font.BOLD, 18);
        TitledBorder borde = BorderFactory.createTitledBorder(titulo);
        borde.setTitleColor(Color.WHITE);
        borde.setTitleFont(fuente);
        return borde;
    }

    // ==== Métodos de acceso públicos ====
    public String getPiezas() { return (String) comboPiezas.getSelectedItem(); }
    public String getSeccion() { return (String) comboSeccion.getSelectedItem(); }
    public boolean isPiqueSi() { return rbPiqueSi != null && rbPiqueSi.isSelected(); }
    public boolean isPiqueNo() { return rbPiqueNo != null && rbPiqueNo.isSelected(); }
    public boolean isFgoSi() { return rbFgoSi != null && rbFgoSi.isSelected(); }
    public boolean isFgoNo() { return rbFgoNo != null && rbFgoNo.isSelected(); }
    public boolean isTesSi() { return rbTesSi != null && rbTesSi.isSelected(); }
    public boolean isTesNo() { return rbTesNo != null && rbTesNo.isSelected(); }
    public String getTot() { return txtTot.getText().equals("seg") ? "" : txtTot.getText(); }
}
