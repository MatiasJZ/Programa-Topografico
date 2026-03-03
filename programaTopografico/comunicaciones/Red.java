package comunicaciones;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.*;

/**
 * A JDialog for configuring network settings in a topographic application.
 * 
 * This dialog allows users to:
 * - Select a local network interface from available interfaces
 * - Configure the TCP port for server communication
 * - Manage a list of destination IP addresses
 * - Apply or cancel configuration changes
 * 
 * The UI features a dark theme with touch-friendly components and styled buttons.
 * All changes are applied through the {@link GestorEnlaceOperativo} communication manager.
 * 
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Network interface selection from active, non-virtual interfaces</li>
 *   <li>Dynamic list management for destination IP addresses</li>
 *   <li>Input validation for IP addresses and port numbers</li>
 *   <li>Dark-themed UI with Arial and Consolas fonts</li>
 *   <li>ESC key binding to close the dialog</li>
 * </ul>
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 * @see GestorEnlaceOperativo
 * @see NetworkInterfaceWrapper
 */
public class Red extends JDialog {

    private static final long serialVersionUID = 1218128306633571285L;
    private JComboBox<NetworkInterfaceWrapper> cbInterfaces;
    private JTextField txtPuerto;
    private DefaultListModel<String> modeloDestinos;
    private JList<String> listaDestinos;
    private JTextField txtNuevoDestino;

    private final GestorEnlaceOperativo comunicacion;

    private final Font fLabel = new Font("Arial", Font.PLAIN, 18);
    private final Font fInput = new Font("Consolas", Font.BOLD, 20);
    private final Font fBtn = new Font("Arial", Font.BOLD, 16);
    private final int ALTO_COMPONENTE = 50;

    public Red(Window owner, GestorEnlaceOperativo comunicacion) {
        super(owner, "Configuración de Red", ModalityType.APPLICATION_MODAL);
        this.comunicacion = comunicacion;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(30, 30, 30));
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(crearEtiqueta("Interfaz local:"), gbc);

        cbInterfaces = new JComboBox<>();
        cbInterfaces.setBackground(Color.WHITE);
        cbInterfaces.setFont(new Font("Arial", Font.BOLD, 16));
        cbInterfaces.setPreferredSize(new Dimension(300, ALTO_COMPONENTE));
        cargarInterfaces();
        
        gbc.gridx = 1; gbc.gridy = 0;
        panelForm.add(cbInterfaces, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(crearEtiqueta("Puerto TCP:"), gbc);

        txtPuerto = new JTextField();
        txtPuerto.setBackground(new Color(50, 50, 50));
        txtPuerto.setForeground(Color.CYAN);
        txtPuerto.setFont(fInput);
        txtPuerto.setHorizontalAlignment(JTextField.CENTER);
        txtPuerto.setPreferredSize(new Dimension(100, ALTO_COMPONENTE));
        
        gbc.gridx = 1; gbc.gridy = 1;
        panelForm.add(txtPuerto, gbc);

        int puertoActual = (comunicacion != null ? comunicacion.getPuerto() : 0);
        txtPuerto.setText(String.valueOf(puertoActual > 0 ? puertoActual : 10011));

        modeloDestinos = new DefaultListModel<>();
        listaDestinos = new JList<>(modeloDestinos);
        listaDestinos.setBackground(new Color(20, 20, 20));
        listaDestinos.setForeground(Color.WHITE);
        listaDestinos.setFont(fInput);
        listaDestinos.setFixedCellHeight(45);

        listaDestinos.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(fInput);
                label.setOpaque(true);
                if (isSelected) {
                    label.setBackground(new Color(60, 60, 60));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(new Color(20, 20, 20));
                    label.setForeground(Color.WHITE);
                }
                return label;
            }
        });

        JScrollPane scrollDestinos = new JScrollPane(listaDestinos);
        scrollDestinos.setPreferredSize(new Dimension(400, 200)); 

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panelForm.add(scrollDestinos, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (comunicacion != null && comunicacion.getDestinos() != null) {
            for (String ip : comunicacion.getDestinos()) {
                modeloDestinos.addElement(ip);
            }
        }

        JPanel panelDestinos = new JPanel(new BorderLayout(10, 10));
        panelDestinos.setBackground(new Color(30, 30, 30));
        panelDestinos.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        txtNuevoDestino = new JTextField("192.168.0.");
        txtNuevoDestino.setFont(fInput);
        txtNuevoDestino.setPreferredSize(new Dimension(180, ALTO_COMPONENTE));
        txtNuevoDestino.setHorizontalAlignment(JTextField.CENTER);
        panelDestinos.add(txtNuevoDestino, BorderLayout.CENTER);

        JPanel panelBtnDestinos = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBtnDestinos.setBackground(new Color(30, 30, 30));

        JButton btnAgregar = crearBotonVerde("AGREGAR (+)");
        btnAgregar.addActionListener(e -> agregarDestino());

        JButton btnQuitar = crearBotonRojo("QUITAR (-)");
        btnQuitar.addActionListener(e -> quitarDestino());

        panelBtnDestinos.add(btnAgregar);
        panelBtnDestinos.add(btnQuitar);
        panelDestinos.add(panelBtnDestinos, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(panelDestinos, gbc);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new GridLayout(1, 2, 15, 0));
        panelInferior.setBackground(new Color(30, 30, 30));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JButton btnCancelar = crearBotonRojo("CANCELAR");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 18));
        btnCancelar.setPreferredSize(new Dimension(0, 60));
        btnCancelar.addActionListener(e -> dispose());   

        JButton btnAplicar = crearBotonAzul("APLICAR CAMBIOS");
        btnAplicar.setFont(new Font("Arial", Font.BOLD, 18));
        btnAplicar.setPreferredSize(new Dimension(0, 60));
        btnAplicar.addActionListener(e -> aplicarCambios());

        panelInferior.add(btnCancelar);
        panelInferior.add(btnAplicar);

        add(panelInferior, BorderLayout.SOUTH);

        JRootPane rp = getRootPane();
        rp.registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        pack();
        setLocationRelativeTo(owner);

        if (comunicacion != null && comunicacion.getInterfazLocal() != null) {
            InetAddress actual = comunicacion.getInterfazLocal();
            for (int i = 0; i < cbInterfaces.getItemCount(); i++) {
                NetworkInterfaceWrapper w = cbInterfaces.getItemAt(i);
                if (w.getAddress().equals(actual)) {
                    cbInterfaces.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(fLabel);
        return lbl;
    }

    private JButton crearBotonVerde(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(new Color(70, 150, 70));
        b.setForeground(Color.WHITE);
        b.setFont(fBtn);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(150, ALTO_COMPONENTE));
        return b;
    }

    private JButton crearBotonRojo(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(new Color(150, 50, 50));
        b.setForeground(Color.WHITE);
        b.setFont(fBtn);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(150, ALTO_COMPONENTE));
        return b;
    }

    private JButton crearBotonAzul(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(new Color(40, 80, 180)); 
        b.setForeground(Color.WHITE);
        b.setFont(fBtn);
        b.setFocusPainted(false);
        return b;
    }

    private void cargarInterfaces() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                NetworkInterface ni = nets.nextElement();
                
                if (!ni.isUp() || ni.isVirtual()) continue;

                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    
                    if (addr.getAddress().length == 4 && !addr.isLoopbackAddress()) {
                        cbInterfaces.addItem(new NetworkInterfaceWrapper(ni, addr));
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las interfaces de red.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarDestino() {
        String ip = txtNuevoDestino.getText().trim();
        if (!ip.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
            JOptionPane.showMessageDialog(this, "IP inválida.");
            return;
        }
        if (!modeloDestinos.contains(ip)) {
            modeloDestinos.addElement(ip);
        }
        txtNuevoDestino.setText("192.168.0.");
    }

    private void quitarDestino() {
        int idx = listaDestinos.getSelectedIndex();
        if (idx >= 0) {
            modeloDestinos.remove(idx);
        }
    }

    private void aplicarCambios() {
        try {
            NetworkInterfaceWrapper wrap = (NetworkInterfaceWrapper) cbInterfaces.getSelectedItem();
            if (wrap == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una interfaz de red.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            InetAddress interfaz = wrap.getAddress();
            int puerto = Integer.parseInt(txtPuerto.getText().trim());

            ArrayList<String> destinos = new ArrayList<>();
            for (int i = 0; i < modeloDestinos.size(); i++) {
                destinos.add(modeloDestinos.get(i));
            }

            comunicacion.setInterfazLocal(interfaz);
            comunicacion.setPuerto(puerto);
            comunicacion.setDestinos(destinos);
            comunicacion.iniciarServidor();

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error aplicando configuración:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class NetworkInterfaceWrapper {
        private final NetworkInterface ni;
        private final InetAddress addr;
        
        NetworkInterfaceWrapper(NetworkInterface ni, InetAddress addr) {
            this.ni = ni;
            this.addr = addr;
        }
        
        InetAddress getAddress() { return addr; }
        
        @Override
        public String toString() {
            return ni.getDisplayName() + " [" + addr.getHostAddress() + "]";
        }
    }
}