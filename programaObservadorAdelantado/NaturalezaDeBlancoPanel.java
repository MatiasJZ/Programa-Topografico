import javax.swing.*;
import java.awt.*;

public class NaturalezaDeBlancoPanel extends JPanel {
    private JComboBox<String> cmbTipo, cmbMagnitud, cmbEstado;
    private JTextField cmbOtro;

    public NaturalezaDeBlancoPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST; // todo alineado a la izquierda
        gbc.fill = GridBagConstraints.NONE;   // no estirar
        gbc.weightx = 0;

        // Crear combos más cortos
        cmbTipo = crearCombo();
        cmbMagnitud = crearCombo();
        cmbEstado = crearCombo();
        cmbOtro = crearTextField();

        String[] labels = {"TIPO DE BLANCO", "MAGNITUD", "ESTADO", "OTRO:"};
        JComponent[] combos = {cmbTipo, cmbMagnitud, cmbEstado, cmbOtro};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Arial", Font.BOLD, 18));
            add(lbl, gbc);

            gbc.gridx = 1;
            add(combos[i], gbc);
        }
        
        setOpcionesTipo(new String[]{"INFANTERÍA", "MOTORIZADOS", "BLINDADOS", "PUESTO COMANDO", "FORTIFICACION"});
        setOpcionesMagnitud(new String[]{"SECCIÓN", "COMPAÑÍA", "BATALLÓN", "COMBOY MOTO.", "COMBOY BLIND.", "INDIVIDUO"});
        setOpcionesEstado(new String[]{"AL DESCUBIERTO", "A CUBIERTO", "ESTACIONADO", "EN MOVIMIENTO", "EN POS. FORT."});
        
    }

    private JComboBox<String> crearCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setPreferredSize(new Dimension(550, 35));
        combo.setBackground(new Color(0, 102, 153));
        combo.setForeground(Color.BLACK);
        combo.setFont(new Font("Arial", Font.BOLD, 16));
        return combo;
    }
    
    private JTextField crearTextField() {
    	JTextField t = new JTextField("ESCRIBA OTRA OPCION...");
    	t.setPreferredSize(new Dimension(550, 35));
        t.setBackground(new Color(0, 102, 153));
        t.setForeground(Color.BLACK);
        t.setFont(new Font("Arial", Font.BOLD, 16));
        t.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (t.getText().equals("ESCRIBA OTRA OPCION...")) {
                    t.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (t.getText().isEmpty()) {
                    t.setText("ESCRIBA OTRA OPCION...");
                }
            }
        });
        return t;
    }

    public String getTipoBlanco() { return (String) cmbTipo.getSelectedItem(); }
    public String getMagnitud() { return (String) cmbMagnitud.getSelectedItem(); }
    public String getEstado() { return (String) cmbEstado.getSelectedItem(); }
    public String getOtro() { return cmbOtro.getText(); }

    public void setOpcionesTipo(String[] opciones) { cmbTipo.setModel(new DefaultComboBoxModel<>(opciones)); }
    public void setOpcionesMagnitud(String[] opciones) { cmbMagnitud.setModel(new DefaultComboBoxModel<>(opciones)); }
    public void setOpcionesEstado(String[] opciones) { cmbEstado.setModel(new DefaultComboBoxModel<>(opciones)); }
}
