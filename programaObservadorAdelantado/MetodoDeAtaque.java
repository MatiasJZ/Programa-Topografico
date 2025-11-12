import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

class MetodoAtaquePanel extends JPanel {

    private JRadioButton rbCercanoSi, rbCercanoNo;
    private JRadioButton rbGranAnguloSi, rbGranAnguloNo;
    private JComboBox<String> comboGranada;
    private JComboBox<String> comboEspoleta;
    private JComboBox<String> comboHaz;
    private JTextField txtVolumen;
    private JRadioButton rbDisparos, rbRafaga;

    public MetodoAtaquePanel() {
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Arial", Font.BOLD, 22);
        Font fontControl = new Font("Arial", Font.PLAIN, 20);
        Font fontSmall = new Font("Arial", Font.PLAIN, 16);
        Color white = Color.WHITE;

        Dimension comboSize = new Dimension(250, 40);
        Border apartadoBorder = BorderFactory.createLineBorder(Color.GRAY, 2);

        Icon radioIcon = new RadioButtonGrande(28);

        // cercano
        JPanel panelCercano = crearPanelApartado("CERCANO:", fontLabel, white, apartadoBorder);
        panelCercano.setBackground(new Color(72,82,122));
        rbCercanoSi = crearRadio("Sí", fontControl, white, radioIcon);
        rbCercanoSi.setBackground(new Color(72,82,122));
        rbCercanoNo = crearRadio("No", fontControl, white, radioIcon);
        rbCercanoNo.setBackground(new Color(72,82,122));
        ButtonGroup grupoCercano = new ButtonGroup();
        grupoCercano.add(rbCercanoSi); grupoCercano.add(rbCercanoNo);
        panelCercano.add(rbCercanoSi); panelCercano.add(rbCercanoNo);
        gbc.gridx = 0; gbc.gridy = 0; add(panelCercano, gbc);

        // gran angulo
        JPanel panelGranAngulo = crearPanelApartado("GRAN ANGULO:", fontLabel, white, apartadoBorder);
        panelGranAngulo.setBackground(new Color(72,82,122));
        rbGranAnguloSi = crearRadio("Sí", fontControl, white, radioIcon);
        rbGranAnguloSi.setBackground(new Color(72,82,122));
        rbGranAnguloNo = crearRadio("No", fontControl, white, radioIcon);
        rbGranAnguloNo.setBackground(new Color(72,82,122));
        ButtonGroup grupoGranAngulo = new ButtonGroup();
        grupoGranAngulo.add(rbGranAnguloSi); grupoGranAngulo.add(rbGranAnguloNo);
        panelGranAngulo.add(rbGranAnguloSi); panelGranAngulo.add(rbGranAnguloNo);
        gbc.gridy = 1; add(panelGranAngulo, gbc);

        // granada
        JPanel panelGranada = crearPanelApartado("GRANADA:", fontLabel, white, apartadoBorder);
        panelGranada.setBackground(new Color(72,82,122));
        comboGranada = crearCombo(new String[]{"HE", "IL", "WP"}, fontControl, comboSize);
        panelGranada.add(comboGranada);
        gbc.gridy = 2; add(panelGranada, gbc);

        // espoleta
        JPanel panelEspoleta = crearPanelApartado("ESPOLETA:", fontLabel, white, apartadoBorder);
        panelEspoleta.setBackground(new Color(72,82,122));
        comboEspoleta = crearCombo(new String[]{"I", "VT", "CM"}, fontControl, comboSize);
        panelEspoleta.add(comboEspoleta);
        gbc.gridy = 3; add(panelEspoleta, gbc);

        // volumen
        JPanel panelVolumen = crearPanelApartado("VOLUMEN:", fontLabel, white, apartadoBorder);
        panelVolumen.setBackground(new Color(72,82,122));
        txtVolumen = new JTextField("         Número (1-50)");
        txtVolumen.setPreferredSize(comboSize);
        txtVolumen.setFont(fontControl);
        txtVolumen.setForeground(Color.GRAY);

        txtVolumen.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtVolumen.getText().equals("         Número (1-50)")) {
                    txtVolumen.setText("");
                    txtVolumen.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtVolumen.getText().isEmpty()) {
                    txtVolumen.setText("         Número (1-50)");
                    txtVolumen.setForeground(Color.GRAY);
                }
            }
        });

        txtVolumen.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    e.consume();
                }
            }
            public void keyReleased(KeyEvent e) {
                try {
                    if (!txtVolumen.getText().isEmpty() && !txtVolumen.getText().equals("Número")) {
                        int val = Integer.parseInt(txtVolumen.getText());
                        if (val < 1 || val > 50) {
                            txtVolumen.setForeground(Color.RED);
                        } else {
                            txtVolumen.setForeground(Color.BLACK);
                        }
                    }
                } catch (NumberFormatException ex) {
                    txtVolumen.setForeground(Color.RED);
                }
            }
        });

        rbDisparos = crearRadio("DISPAROS", fontSmall, white, null);
        rbDisparos.setBackground(new Color(72,82,122));
        rbRafaga = crearRadio("RÁFAGA", fontSmall, white, null);
        rbRafaga.setBackground(new Color(72,82,122));
        ButtonGroup grupoVolumen = new ButtonGroup();
        grupoVolumen.add(rbDisparos); grupoVolumen.add(rbRafaga);

        panelVolumen.add(txtVolumen);
        panelVolumen.add(rbDisparos);
        panelVolumen.add(rbRafaga);

        gbc.gridy = 4; add(panelVolumen, gbc);

        // haz
        JPanel panelHaz = crearPanelApartado("HAZ:", fontLabel, white, apartadoBorder);
        panelHaz.setBackground(new Color(72,82,122));
        comboHaz = crearCombo(new String[]{"PARALELO", "CONVERGENTE","ABIERTO","ESPECIAL","CIRCULAR"}, fontControl, comboSize);
        panelHaz.add(comboHaz);
        gbc.gridy = 5; add(panelHaz, gbc);
    }

    private JPanel crearPanelApartado(String titulo, Font fontLabel, Color color, Border border) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setBackground(Color.BLACK);
        panel.setBorder(border);
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(fontLabel);
        lbl.setForeground(color);
        panel.add(lbl);
        return panel;
    }
    
    public void mostrarPIF(PIF pif) {
        comboGranada.setSelectedItem(pif.getTipoMunicion());
        comboEspoleta.setSelectedItem(pif.getEspoleta());
        comboHaz.setSelectedItem(pif.getHaz());
    }

    private JRadioButton crearRadio(String text, Font font, Color color, Icon icon) {
        JRadioButton rb = new JRadioButton(text);
        rb.setBackground(Color.BLACK);
        rb.setForeground(color);
        rb.setFont(font);
        if (icon != null) rb.setIcon(icon);
        return rb;
    }

    private JComboBox<String> crearCombo(String[] items, Font font, Dimension size) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(font);
        combo.setPreferredSize(size);
        combo.setForeground(Color.BLACK);
        combo.setBackground(Color.WHITE);
        return combo;
    }

    public boolean isCercano() { return rbCercanoSi.isSelected(); }
    
    public boolean isGranAngulo() { return rbGranAnguloSi.isSelected(); }
    
    public String getGranada() { return (String) comboGranada.getSelectedItem(); }
    
    public String getEspoleta() { return (String) comboEspoleta.getSelectedItem(); }
    
    public String getHaz() { return (String) comboHaz.getSelectedItem(); }
    
    public boolean isDisparos() { return rbDisparos.isSelected(); }
    
    public boolean isRafaga() { return rbRafaga.isSelected(); }
    
    public int getVolumen() {
        try {
            String text = txtVolumen.getText();
            if (text.equals("Número")) return -1;
            int val = Integer.parseInt(text);
            if (val >= 1 && val <= 50) return val;
        } catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }
}
