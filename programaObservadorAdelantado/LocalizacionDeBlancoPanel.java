import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class LocalizacionDeBlancoPanel extends JPanel {
    private JButton btnPOL, btnCOOR, btnRefBlancos;

    // Campos para POL
    private JTextField txtPIF, txtAngDir, txtDistancia, txtSubirBajar, txtAngVertical;
    private JComboBox<String> cmbPtoReferencia;

    // Campos para COOR
    private JTextField txtPIFCoor, txtDerechas, txtArribas, txtCota;

    // Panel dinámico
    private JPanel panelCampos;

    // Lista de blancos (referencia compartida)
    private LinkedList<Blanco> blancosDisponibles;

    public LocalizacionDeBlancoPanel(LinkedList<Blanco> blancosDisponibles) {
        this.blancosDisponibles = blancosDisponibles;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Panel botones principales
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 10, 10));
        panelBotones.setBackground(Color.BLACK);

        btnPOL = new JButton("POL");
        btnCOOR = new JButton("COOR");

        estilizarBoton(btnPOL, Color.GRAY);
        estilizarBoton(btnCOOR, Color.GRAY);

        panelBotones.add(btnPOL);
        panelBotones.add(btnCOOR);
        add(panelBotones, BorderLayout.WEST);

        // Panel central
        panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBackground(Color.BLACK);
        add(panelCampos, BorderLayout.CENTER);

        // Inicializar combo y botón recargar
        cmbPtoReferencia = new JComboBox<>();
        recargarModeloBlancos();

        btnRefBlancos = new JButton("Recargar blancos");
        btnRefBlancos.setBackground(new Color(100, 149, 237));
        btnRefBlancos.setForeground(Color.WHITE);
        btnRefBlancos.setFont(new Font("Arial", Font.BOLD, 16));
        btnRefBlancos.setFocusPainted(false);
        btnRefBlancos.addActionListener(e -> recargarModeloBlancos());

        // Listeners principales
        btnPOL.addActionListener(e -> {
            seleccionarBoton(btnPOL, btnCOOR);
            mostrarCamposPOL();
        });

        btnCOOR.addActionListener(e -> {
            seleccionarBoton(btnCOOR, btnPOL);
            mostrarCamposCOOR();
        });
    }

    private void estilizarBoton(JButton btn, Color baseColor) {
        btn.setBackground(baseColor);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setFocusPainted(false);
    }

    private void estilizarCampo(JComponent comp) {
        comp.setFont(new Font("Arial", Font.PLAIN, 18));
        if (comp instanceof JTextField) {
            ((JTextField) comp).setPreferredSize(new Dimension(250, 35));
        } else if (comp instanceof JComboBox) {
            ((JComboBox<?>) comp).setPreferredSize(new Dimension(250, 35));
        }
    }

    private void seleccionarBoton(JButton activo, JButton inactivo) {
        activo.setBackground(Color.YELLOW);
        inactivo.setBackground(Color.GRAY);
    }

    private void mostrarCamposPOL() {
        panelCampos.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtPIF = new JTextField();
        txtAngDir = new JTextField();
        txtDistancia = new JTextField();
        txtSubirBajar = new JTextField();
        txtAngVertical = new JTextField();

        estilizarCampo(txtPIF);
        estilizarCampo(txtAngDir);
        estilizarCampo(txtDistancia);
        estilizarCampo(txtSubirBajar);
        estilizarCampo(txtAngVertical);
        estilizarCampo(cmbPtoReferencia);

        String[] labels = {"PIF N°:", "ANG. DIR.:", "DISTANCIA:", "SUBIR/BAJAR:", "ANG. VERTICAL:", "PTO. REFERENCIA:"};
        JComponent[] campos = {txtPIF, txtAngDir, txtDistancia, txtSubirBajar, txtAngVertical, cmbPtoReferencia};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Arial", Font.BOLD, 18));
            panelCampos.add(lbl, gbc);

            gbc.gridx = 1;
            panelCampos.add(campos[i], gbc);

            if (i > 0 && i < 5) { 
                gbc.gridx = 2;
                JLabel lblMils = new JLabel("mils");
                lblMils.setForeground(Color.WHITE);
                lblMils.setFont(new Font("Arial", Font.BOLD, 16));
                panelCampos.add(lblMils, gbc);
            }
        }

        gbc.gridx = 0;
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(btnRefBlancos, gbc);

        refrescar();
    }

    private void mostrarCamposCOOR() {
        panelCampos.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtPIFCoor = new JTextField();
        txtDerechas = new JTextField();
        txtArribas = new JTextField();
        txtCota = new JTextField();

        estilizarCampo(txtPIFCoor);
        estilizarCampo(txtDerechas);
        estilizarCampo(txtArribas);
        estilizarCampo(txtCota);

        String[] labels = {"PIF N°:", "DERECHAS:", "ARRIBAS:", "COTA:"};
        JComponent[] campos = {txtPIFCoor, txtDerechas, txtArribas, txtCota};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Arial", Font.BOLD, 18));
            panelCampos.add(lbl, gbc);

            gbc.gridx = 1;
            panelCampos.add(campos[i], gbc);
        }

        refrescar();
    }

    private void refrescar() {
        panelCampos.revalidate();
        panelCampos.repaint();
    }

    /** Recarga el modelo del combo con la lista actual de blancos */
    private void recargarModeloBlancos() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        if (blancosDisponibles != null) {
            for (Blanco b : blancosDisponibles) {
                if (b != null) model.addElement(b.getNombre());
            }
        }
        cmbPtoReferencia.setModel(model);
    }

    // Getters de POL
    public String getPIF() { return txtPIF != null ? txtPIF.getText() : null; }
    public String getAngDir() { return txtAngDir != null ? txtAngDir.getText() : null; }
    public String getDistancia() { return txtDistancia != null ? txtDistancia.getText() : null; }
    public String getSubirBajar() { return txtSubirBajar != null ? txtSubirBajar.getText() : null; }
    public String getAngVertical() { return txtAngVertical != null ? txtAngVertical.getText() : null; }
    public String getPtoReferenciaPOL() { return cmbPtoReferencia != null ? (String)cmbPtoReferencia.getSelectedItem() : null; }

    // Getters de COOR
    public String getPIFCoor() { return txtPIFCoor != null ? txtPIFCoor.getText() : null; }
    public String getDerechas() { return txtDerechas != null ? txtDerechas.getText() : null; }
    public String getArribas() { return txtArribas != null ? txtArribas.getText() : null; }
    public String getCota() { return txtCota != null ? txtCota.getText() : null; }
}
