package app;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PanelConfigRed extends JPanel {

    private static final long serialVersionUID = 1L;

    private JComboBox<String> comboInterfaces;
    private InetAddress[] interfacesIPs;

    private JTextField txtPuerto;
    private JTextField txtNuevaIP;
    private DefaultListModel<String> modeloDestinos;
    private JList<String> listaDestinos;
    private JButton btnAgregarIP;
    private JButton btnEliminarIP;
    private JButton btnAplicar;

    public PanelConfigRed() {

        setLayout(new BorderLayout(5, 5));
        setBackground(Color.BLACK);
        TitledBorder b = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "CONFIGURACIÓN RED (HARRIS IP)",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                Color.WHITE
        );
        setBorder(b);

        // ================== PARTE SUPERIOR: interfaz + puerto ==================
        JPanel panelSup = new JPanel(new GridLayout(2, 2, 5, 5));
        panelSup.setBackground(Color.BLACK);

        comboInterfaces = new JComboBox<>();
        interfacesIPs = cargarInterfaces();

        txtPuerto = new JTextField("5056");

        JLabel lblInt = new JLabel("Interfaz local:");
        lblInt.setForeground(Color.WHITE);
        JLabel lblPuerto = new JLabel("Puerto TCP:");
        lblPuerto.setForeground(Color.WHITE);

        panelSup.add(lblInt);
        panelSup.add(comboInterfaces);
        panelSup.add(lblPuerto);
        panelSup.add(txtPuerto);

        add(panelSup, BorderLayout.NORTH);

        // ================== PARTE CENTRAL: lista de destinos ===================
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        panelCentro.setBackground(Color.BLACK);

        modeloDestinos = new DefaultListModel<>();
        listaDestinos = new JList<>(modeloDestinos);
        listaDestinos.setBackground(new Color(20, 20, 20));
        listaDestinos.setForeground(Color.WHITE);
        listaDestinos.setFont(new Font("Consolas", Font.PLAIN, 13));

        panelCentro.add(new JScrollPane(listaDestinos), BorderLayout.CENTER);

        // ================== PARTE INFERIOR: agregar / quitar ==================
        JPanel panelIP = new JPanel(new BorderLayout(5, 5));
        panelIP.setBackground(Color.BLACK);

        txtNuevaIP = new JTextField("192.168.105.20");

        btnAgregarIP = new JButton("Agregar destino");
        btnAgregarIP.setBackground(new Color(40, 120, 40));
        btnAgregarIP.setForeground(Color.WHITE);

        btnEliminarIP = new JButton("Quitar seleccionado");
        btnEliminarIP.setBackground(new Color(120, 40, 40));
        btnEliminarIP.setForeground(Color.WHITE);

        JPanel botones = new JPanel(new GridLayout(1, 2, 5, 5));
        botones.setBackground(Color.BLACK);
        botones.add(btnAgregarIP);
        botones.add(btnEliminarIP);

        panelIP.add(txtNuevaIP, BorderLayout.CENTER);
        panelIP.add(botones, BorderLayout.EAST);

        panelCentro.add(panelIP, BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);

        // ================== BOTÓN APLICAR ==================
        btnAplicar = new JButton("APLICAR CONFIGURACIÓN");
        btnAplicar.setBackground(new Color(30, 60, 130));
        btnAplicar.setForeground(Color.WHITE);
        btnAplicar.setFont(new Font("Arial", Font.BOLD, 14));

        add(btnAplicar, BorderLayout.SOUTH);

        // ================== LÓGICA BOTONES ==================
        btnAgregarIP.addActionListener(e -> {
            String ip = txtNuevaIP.getText().trim();
            if (!ip.isEmpty() && !modeloDestinos.contains(ip)) {
                modeloDestinos.addElement(ip);
            }
        });

        btnEliminarIP.addActionListener(e -> {
            int idx = listaDestinos.getSelectedIndex();
            if (idx >= 0) modeloDestinos.remove(idx);
        });
    }

    private InetAddress[] cargarInterfaces() {

        java.util.List<InetAddress> ips = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

            while (nets.hasMoreElements()) {
                NetworkInterface ni = nets.nextElement();

                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual())
                    continue;

                for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                    InetAddress addr = ia.getAddress();

                    if (addr instanceof Inet4Address) {
                        comboInterfaces.addItem(ni.getDisplayName() + " - " + addr.getHostAddress());
                        ips.add(addr);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ips.toArray(new InetAddress[0]);
    }

    public InetAddress getInterfazSeleccionada() {
        int idx = comboInterfaces.getSelectedIndex();
        if (idx < 0 || interfacesIPs.length == 0) return null;
        return interfacesIPs[idx];
    }

    public int getPuerto() {
        try {
            return Integer.parseInt(txtPuerto.getText().trim());
        } catch (Exception e) {
            return 5056;
        }
    }

    public List<String> getDestinos() {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < modeloDestinos.getSize(); i++) {
            res.add(modeloDestinos.getElementAt(i));
        }
        return res;
    }

    public void setOnAplicar(Runnable r) {
        btnAplicar.addActionListener(e -> r.run());
    }
}
