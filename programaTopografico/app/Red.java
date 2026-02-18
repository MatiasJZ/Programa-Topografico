package app;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.*;

import comunicaciones.GestorEnlaceOperativo;

public class Red extends JDialog {

	private static final long serialVersionUID = 1218128306633571285L;
	private JComboBox<NetworkInterfaceWrapper> cbInterfaces;
    private JTextField txtPuerto;
    private DefaultListModel<String> modeloDestinos;
    private JList<String> listaDestinos;
    private JTextField txtNuevoDestino;

    private final GestorEnlaceOperativo comunicacion;

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
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ENTRADA ETHERNET LOCAL
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblInterfaz = crearEtiqueta("Interfaz local:");
        panelForm.add(lblInterfaz, gbc);

        cbInterfaces = new JComboBox<>();
        cbInterfaces.setBackground(Color.WHITE);
        cargarInterfaces();
        gbc.gridx = 1; gbc.gridy = 0;
        panelForm.add(cbInterfaces, gbc);

        // PUERTO TCP
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPuerto = crearEtiqueta("Puerto TCP:");
        panelForm.add(lblPuerto, gbc);

        txtPuerto = new JTextField();
        txtPuerto.setBackground(new Color(50, 50, 50));
        txtPuerto.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 1;
        panelForm.add(txtPuerto, gbc);

        // valor inicial de puerto (si ComunicacionIP ya tiene uno)
        int puertoActual = (comunicacion != null ? comunicacion.getPuerto() : 0);
        if (puertoActual > 0) {
            txtPuerto.setText(String.valueOf(puertoActual));
        } else {
            txtPuerto.setText("5056");
        }

        // LISTA DE DESTINATARIOS
        modeloDestinos = new DefaultListModel<>();
        listaDestinos = new JList<>(modeloDestinos);
        listaDestinos.setBackground(new Color(20, 20, 20));
        listaDestinos.setForeground(Color.WHITE);

        listaDestinos.setFont(new Font("Consolas", Font.BOLD, 18));
        listaDestinos.setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = -294481990100120184L;

			@Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Consolas", Font.BOLD, 18));
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
        scrollDestinos.setPreferredSize(new Dimension(380, 130));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panelForm.add(scrollDestinos, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // rellenar lista con lo que ya tenga ComunicacionIP
        if (comunicacion != null) {
            List<String> yaConfigurados = comunicacion.getDestinos();
            if (yaConfigurados != null) {
                for (String ip : yaConfigurados) {
                    modeloDestinos.addElement(ip);
                }
            }
        }

        JPanel panelDestinos = new JPanel(new BorderLayout(5, 5));
        panelDestinos.setBackground(new Color(30, 30, 30));
        panelDestinos.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        txtNuevoDestino = new JTextField("192.168.0.");
        txtNuevoDestino.setPreferredSize(new Dimension(140, 28));
        panelDestinos.add(txtNuevoDestino, BorderLayout.CENTER);

        JPanel panelBtnDestinos = new JPanel(new GridLayout(1, 2, 6, 0));
        panelBtnDestinos.setBackground(new Color(30, 30, 30));

        JButton btnAgregar = crearBotonVerde("Agregar destino");
        btnAgregar.addActionListener(e -> agregarDestino());

        JButton btnQuitar = crearBotonRojo("Quitar seleccionado");
        btnQuitar.addActionListener(e -> quitarDestino());

        panelBtnDestinos.add(btnAgregar);
        panelBtnDestinos.add(btnQuitar);
        panelDestinos.add(panelBtnDestinos, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(panelDestinos, gbc);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelInferior.setBackground(new Color(30, 30, 30));

        Dimension tamBotonPrincipal = new Dimension(110, 32);

        JButton btnCancelar = crearBotonRojo("Cancelar");
        btnCancelar.setPreferredSize(tamBotonPrincipal);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancelar.addActionListener(e -> dispose());   

        JButton btnAplicar = crearBotonAzul("Aplicar");
        btnAplicar.setPreferredSize(tamBotonPrincipal);
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
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        return lbl;
    }

    private JButton crearBotonVerde(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(new Color(70, 150, 70));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    private JButton crearBotonRojo(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(new Color(150, 50, 50));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFocusPainted(false);
        return b;
    }

    private JButton crearBotonAzul(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(new Color(40, 80, 150));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 14));
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
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar las interfaces de red.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
            NetworkInterfaceWrapper wrap =
                    (NetworkInterfaceWrapper) cbInterfaces.getSelectedItem();
            if (wrap == null) {
                JOptionPane.showMessageDialog(this,
                        "Debe seleccionar una interfaz de red.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this,
                    "Error aplicando configuración:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
	    return ni.getDisplayName() + " - " + addr.getHostAddress();
	    }
	}
}
