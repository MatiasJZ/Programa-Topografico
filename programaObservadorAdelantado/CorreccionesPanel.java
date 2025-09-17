import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class CorreccionesPanel extends JPanel {

    private JComboBox<String> comboDireccion, comboAlcance, comboAltura;
    private JTextField txtDireccion, txtAlcance, txtAltura;
    private JRadioButton rbEficaciaSi, rbEficaciaNo;
    private JButton btnNuevoPif, btnFinMision, btnEnviar;

    private PedidoDeFuego pedidoDeFuego; // referencia al contenedor principal

    public CorreccionesPanel(PedidoDeFuego pedidoDeFuego) {
        this.pedidoDeFuego = pedidoDeFuego;

        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fontLabel = new Font("Arial", Font.BOLD, 18);
        Font fontCombo = new Font("Arial", Font.PLAIN, 16);

        // ===== EN DIRECCIÓN =====
        gbc.gridx = 0; gbc.gridy = 0;
        add(crearLabel("EN DIRECCIÓN"), gbc);

        comboDireccion = new JComboBox<>(new String[]{"IZQUIERDA", "DERECHA"});
        comboDireccion.setFont(fontCombo);
        gbc.gridx = 1;
        add(comboDireccion, gbc);

        txtDireccion = new JTextField(5);
        txtDireccion.setFont(fontCombo);
        gbc.gridx = 2;
        add(txtDireccion, gbc);

        JLabel lblMts1 = crearLabel("Mts");
        gbc.gridx = 3;
        add(lblMts1, gbc);

        // ===== EN ALCANCE =====
        gbc.gridx = 0; gbc.gridy = 1;
        add(crearLabel("EN ALCANCE"), gbc);

        comboAlcance = new JComboBox<>(new String[]{"ALARGAR", "ACORTAR"});
        comboAlcance.setFont(fontCombo);
        gbc.gridx = 1;
        add(comboAlcance, gbc);

        txtAlcance = new JTextField(5);
        txtAlcance.setFont(fontCombo);
        gbc.gridx = 2;
        add(txtAlcance, gbc);

        JLabel lblMts2 = crearLabel("Mts");
        gbc.gridx = 3;
        add(lblMts2, gbc);

        // ===== EN ALTURA =====
        gbc.gridx = 0; gbc.gridy = 2;
        add(crearLabel("EN ALTURA"), gbc);

        comboAltura = new JComboBox<>(new String[]{"SUBIR", "BAJAR"});
        comboAltura.setFont(fontCombo);
        gbc.gridx = 1;
        add(comboAltura, gbc);

        txtAltura = new JTextField(5);
        txtAltura.setFont(fontCombo);
        gbc.gridx = 2;
        add(txtAltura, gbc);

        JLabel lblMts3 = crearLabel("Mts");
        gbc.gridx = 3;
        add(lblMts3, gbc);

        // ===== EFICACIA =====
        gbc.gridx = 0; gbc.gridy = 3;
        add(crearLabel("EFICACIA"), gbc);

        rbEficaciaSi = new JRadioButton("SI");
        rbEficaciaNo = new JRadioButton("NO");

        for (JRadioButton rb : new JRadioButton[]{rbEficaciaSi, rbEficaciaNo}) {
            rb.setBackground(Color.BLACK);
            rb.setForeground(Color.WHITE);
            rb.setFont(fontCombo);
        }

        ButtonGroup eficaciaGroup = new ButtonGroup();
        eficaciaGroup.add(rbEficaciaSi);
        eficaciaGroup.add(rbEficaciaNo);

        JPanel panelEficacia = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEficacia.setBackground(Color.BLACK);
        panelEficacia.add(rbEficaciaSi);
        panelEficacia.add(rbEficaciaNo);

        gbc.gridx = 1; gbc.gridwidth = 2;
        add(panelEficacia, gbc);

        // ===== BOTONES ABAJO =====
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(Color.BLACK);

        btnNuevoPif = new JButton("NUEVO PIF");
        btnFinMision = new JButton("FIN DE MISION");
        btnEnviar = new JButton("ENVIAR");

        for (JButton b : new JButton[]{btnNuevoPif, btnFinMision, btnEnviar}) {
            b.setPreferredSize(new Dimension(140, 40));
            b.setFont(new Font("Arial", Font.BOLD, 14));
            b.setFocusPainted(false);
        }

        btnNuevoPif.setBackground(new Color(180, 255, 180)); // verde claro
        btnFinMision.setBackground(new Color(255, 200, 0)); // amarillo
        btnEnviar.setBackground(new Color(171, 50, 50));    // rojo

        // Acción de NUEVO PIF
        btnNuevoPif.addActionListener(e -> reiniciarPedido());

        panelBotones.add(btnNuevoPif);
        panelBotones.add(btnFinMision);
        panelBotones.add(btnEnviar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        add(panelBotones, gbc);
    }

    private JLabel crearLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        return lbl;
    }

    // ==== Métodos de acceso ====
    public String getDireccion() { return (String) comboDireccion.getSelectedItem(); }
    public String getValorDireccion() { return txtDireccion.getText(); }

    public String getAlcance() { return (String) comboAlcance.getSelectedItem(); }
    public String getValorAlcance() { return txtAlcance.getText(); }

    public String getAltura() { return (String) comboAltura.getSelectedItem(); }
    public String getValorAltura() { return txtAltura.getText(); }

    public boolean isEficaciaSi() { return rbEficaciaSi.isSelected(); }
    public boolean isEficaciaNo() { return rbEficaciaNo.isSelected(); }

    // ==== Acción de reinicio ====
    private void reiniciarPedido() {
        // limpiar campos
        comboDireccion.setSelectedIndex(0);
        txtDireccion.setText("");
        comboAlcance.setSelectedIndex(0);
        txtAlcance.setText("");
        comboAltura.setSelectedIndex(0);
        txtAltura.setText("");
        rbEficaciaSi.setSelected(false);
        rbEficaciaNo.setSelected(false);

        // volver al inicio del CardLayout
        if (pedidoDeFuego != null) {
            pedidoDeFuego.mostrarInicio();
        }
    }
}
