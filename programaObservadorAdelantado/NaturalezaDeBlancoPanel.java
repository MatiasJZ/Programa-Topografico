import javax.swing.*;
import java.awt.*;

public class NaturalezaDeBlancoPanel extends JPanel {
    private JComboBox<String> cmbTipo, cmbMagnitud, cmbEstado, cmbOtro;

    public NaturalezaDeBlancoPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 15);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Crear combos
        cmbTipo = crearCombo();
        cmbMagnitud = crearCombo();
        cmbEstado = crearCombo();
        cmbOtro = crearCombo();

        // Labels
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
            gbc.weightx = 1.0;
            add(combos[i], gbc);
        }
    }

    private JComboBox<String> crearCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setPreferredSize(new Dimension(250, 40));
        combo.setBackground(new Color(0, 102, 153)); // azul estilo imagen
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("Arial", Font.BOLD, 16));
        return combo;
    }

    // Getters para acceder a los valores seleccionados
    public String getTipoBlanco() { return (String) cmbTipo.getSelectedItem(); }
    public String getMagnitud() { return (String) cmbMagnitud.getSelectedItem(); }
    public String getEstado() { return (String) cmbEstado.getSelectedItem(); }
    public String getOtro() { return (String) cmbOtro.getSelectedItem(); }

    // Métodos para setear opciones desde afuera
    public void setOpcionesTipo(String[] opciones) { cmbTipo.setModel(new DefaultComboBoxModel<>(opciones)); }
    public void setOpcionesMagnitud(String[] opciones) { cmbMagnitud.setModel(new DefaultComboBoxModel<>(opciones)); }
    public void setOpcionesEstado(String[] opciones) { cmbEstado.setModel(new DefaultComboBoxModel<>(opciones)); }
    public void setOpcionesOtro(String[] opciones) { cmbOtro.setModel(new DefaultComboBoxModel<>(opciones)); }
}
