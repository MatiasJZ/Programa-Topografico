package panelesSecundarios;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dominio.Blanco;
import util.CheckIconCustom;

public class CorreccionesPanel extends JPanel {

    private static final long serialVersionUID = -271089343555480088L;
    private JComboBox<String> cbDireccion;
    private JComboBox<String> cbAlcance;
    private JComboBox<String> cbAltura;
    
    private JButton btnZoomIn;
    private JButton btnZoomOut;

    private JTextField txtDirValor;
    private JTextField txtAlcValor;
    private JTextField txtAltValor;
    private JTextField txtEPA, txtAngOb, txtTVolido;
    private JLabel l4;
    private JLabel lblAlertaImpacto;
    private Timer timerVolido;
    private int tiempoRetrocesoVolido;

    private JCheckBox chkModoAutomatico;
    private JButton btnNuevoPIF;
    private JButton btnFinMision;
    private JButton btnEnviar;  
    private JButton btnVolver;
    private JButton btnFuego;
    private JButton btnHistorial;
    
    private MetodoAtaqueYTiroPanel metAtaqueYTiroPanel;
    private Map<String,String> historial;
    
    private Timer timerFuego;
    private boolean fuegoEstado = false; 

    private JLabel lblUltima;
    
    private PanelCuadricula panelCuadricula;
    private int ESCALA_METROS_POR_CUADRICULA = 200;

    public CorreccionesPanel(Blanco blanco, MetodoAtaqueYTiroPanel m) {
        
        metAtaqueYTiroPanel = m;
        historial = new HashMap<>();
        
        TitledBorder borde1 = BorderFactory.createTitledBorder("CORRECCIONES");
        borde1.setTitleColor(Color.WHITE);
        borde1.setTitleFont(new Font("Arial", Font.BOLD, 18));
        this.setBorder(borde1);
        setBackground(Color.BLACK);
        
        // ---- 1. INICIALIZACIÓN DE COMPONENTES ----
        
        Font fontParam = new Font("Arial", Font.BOLD, 14);
        Font fontCombos = new Font("Arial", Font.BOLD, 16);
        Font fontButtons = new Font("Arial", Font.BOLD, 18);
        Color colorFondoTextos = new Color(40, 80, 120);
        Color colorFondoBotones = new Color(60, 60, 60);

        btnZoomIn = new JButton("+");
        btnZoomOut = new JButton("-");
        for (JButton b : new JButton[]{btnZoomIn, btnZoomOut}) {
            b.setFont(fontButtons);
            b.setFocusPainted(false);
            b.setBackground(colorFondoBotones);
            b.setForeground(Color.WHITE);
        }
        
        lblAlertaImpacto = new JLabel("IMPACTO: --");
        lblAlertaImpacto.setForeground(Color.DARK_GRAY);
        lblAlertaImpacto.setFont(new Font("Consolas", Font.BOLD, 26));
        lblAlertaImpacto.setHorizontalAlignment(SwingConstants.CENTER);
        lblAlertaImpacto.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        lblAlertaImpacto.setPreferredSize(new Dimension(350, 50));

        txtEPA = new JTextField("0");
        txtAngOb = new JTextField("0");
        txtTVolido = new JTextField("0");
        
        for (JTextField t : new JTextField[]{txtEPA, txtAngOb, txtTVolido}) {
            t.setBackground(colorFondoTextos);
            t.setForeground(Color.WHITE);
            t.setHorizontalAlignment(SwingConstants.CENTER);
            t.setFont(fontParam);
        }

        JLabel l1 = new JLabel("EN DIRECCIÓN");
        JLabel l2 = new JLabel("EN ALCANCE");
        JLabel l3 = new JLabel("EN ALTURA");
        l4 = new JLabel("EFICACIA");
        JLabel l6 = new JLabel("MISION:");

        for (JLabel l : new JLabel[]{l1, l2, l3}) {
            l.setForeground(Color.WHITE);
            l.setFont(fontCombos);
        }
        
        l4.setForeground(Color.GREEN);
        l4.setFont(new Font("Arial", Font.BOLD, 22));
        l6.setForeground(Color.WHITE);
        l6.setFont(new Font("Arial", Font.BOLD, 22));
        
        chkModoAutomatico = new JCheckBox("MODO AUTOMÁTICO");
        chkModoAutomatico.setFont(fontCombos);
        chkModoAutomatico.setForeground(Color.WHITE);
        chkModoAutomatico.setBackground(Color.BLACK);
        chkModoAutomatico.setIcon(new CheckIconCustom(22));

        cbDireccion = new JComboBox<>(new String[]{"IZQUIERDA","DERECHA"});
        cbAlcance = new JComboBox<>(new String[]{"ALARGAR","ACORTAR"});
        cbAltura = new JComboBox<>(new String[]{"SUBIR","BAJAR"});

        for (JComboBox<?> cb : new JComboBox<?>[]{cbDireccion, cbAlcance, cbAltura}) {
            cb.setBackground(new Color(20, 40, 80));
            cb.setForeground(Color.WHITE);
            cb.setFont(fontCombos);
            cb.setFocusable(false);
        }

        txtDirValor = new JTextField("0");
        txtAlcValor = new JTextField("0");
        txtAltValor = new JTextField("0");

        for (JTextField t : new JTextField[]{txtDirValor, txtAlcValor, txtAltValor}) {
            t.setBackground(colorFondoTextos);
            t.setForeground(Color.WHITE);
            t.setHorizontalAlignment(SwingConstants.CENTER);
            t.setFont(fontCombos);
            t.setPreferredSize(new Dimension(60, 32)); // Ancho fijo para mantener estética
        }

        JLabel u1 = new JLabel("Mts"); JLabel u2 = new JLabel("Mts"); JLabel u3 = new JLabel("Mts");
        for (JLabel u : new JLabel[]{u1, u2, u3}) {
            u.setForeground(new Color(200, 200, 200));
            u.setFont(fontCombos);
        }

        btnNuevoPIF = new JButton("NUEVO PIF");
        btnFinMision = new JButton("FIN DE MISION");
        btnEnviar = new JButton("ENVIAR");
        btnHistorial = new JButton("HISTORIAL");
        btnVolver = new JButton("VOLVER");
        btnFuego = new JButton("FUEGO"); 

        btnNuevoPIF.setBackground(new Color(200, 255, 200));
        btnFinMision.setBackground(Color.GRAY);
        btnEnviar.setBackground(Color.RED);
        btnVolver.setBackground(colorFondoBotones);
        btnVolver.setForeground(Color.WHITE);
        btnHistorial.setBackground(colorFondoBotones);
        btnHistorial.setForeground(Color.WHITE);
        btnFuego.setBackground(new Color(200, 0, 0)); 
        btnFuego.setForeground(Color.WHITE);
        btnFuego.setVisible(false); 

        for (JButton b : new JButton[]{btnNuevoPIF, btnFinMision, btnEnviar, btnHistorial, btnVolver, btnFuego}) {
            b.setFont(fontButtons);
            b.setFocusPainted(false);
            if(b == btnNuevoPIF || b == btnFinMision || b == btnEnviar) {
                 b.setForeground(Color.BLACK);
            }
        }
        
        lblUltima = new JLabel(); 
        lblUltima.setForeground(Color.WHITE);

        String nombreBlanco = (blanco != null && blanco.getNombre() != null) ? blanco.getNombre() : "Blanco";
        panelCuadricula = new PanelCuadricula(ESCALA_METROS_POR_CUADRICULA, nombreBlanco);
        panelCuadricula.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panelCuadricula.setMetrosPorPunto(30);

        // ---- 2. ENSAMBLAJE DE LAYOUTS (Reemplazando null layout) ----
        
        setLayout(new GridBagLayout());
        
        // LADO IZQUIERDO (Grilla + Botones Inferiores)
        JPanel pnlIzquierdo = new JPanel(new BorderLayout(0, 15));
        pnlIzquierdo.setOpaque(false);
        
        // Agregar botones de Zoom dentro de la cuadrícula usando GridBagLayout para anclarlos al sur-oeste
        panelCuadricula.setLayout(new GridBagLayout());
        GridBagConstraints gbcZoom = new GridBagConstraints();
        gbcZoom.anchor = GridBagConstraints.SOUTHWEST;
        gbcZoom.weightx = 1.0; gbcZoom.weighty = 1.0;
        gbcZoom.insets = new Insets(0, 10, 10, 0); // Margen
        JPanel pnlZoom = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlZoom.setOpaque(false);
        pnlZoom.add(btnZoomIn); pnlZoom.add(btnZoomOut);
        panelCuadricula.add(pnlZoom, gbcZoom);
        
        pnlIzquierdo.add(panelCuadricula, BorderLayout.CENTER);

        // Botones debajo de la grilla
        JPanel pnlIzquierdoAbajo = new JPanel(new GridBagLayout());
        pnlIzquierdoAbajo.setOpaque(false);
        GridBagConstraints gbcBtm = new GridBagConstraints();
        gbcBtm.insets = new Insets(0, 0, 0, 15);
        gbcBtm.fill = GridBagConstraints.VERTICAL;
        
        btnVolver.setPreferredSize(new Dimension(150, 40));
        btnHistorial.setPreferredSize(new Dimension(150, 40));
        
        gbcBtm.gridx = 0; pnlIzquierdoAbajo.add(btnVolver, gbcBtm);
        gbcBtm.gridx = 1; pnlIzquierdoAbajo.add(btnHistorial, gbcBtm);
        gbcBtm.gridx = 2; gbcBtm.weightx = 1.0; gbcBtm.anchor = GridBagConstraints.EAST;
        gbcBtm.insets = new Insets(0, 0, 0, 0);
        pnlIzquierdoAbajo.add(chkModoAutomatico, gbcBtm);
        
        pnlIzquierdo.add(pnlIzquierdoAbajo, BorderLayout.SOUTH);

        // LADO DERECHO (Controles)
        JPanel pnlDerecho = new JPanel(new GridBagLayout());
        pnlDerecho.setOpaque(false);
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.fill = GridBagConstraints.HORIZONTAL;
        gbcR.insets = new Insets(5, 20, 15, 10);
        gbcR.weightx = 1.0; gbcR.gridx = 0; gbcR.gridy = 0;
        
        // Controles de Corrección (Dirección, Alcance, Altura)
        JPanel pnlCombos = new JPanel(new GridBagLayout());
        pnlCombos.setOpaque(false);
        GridBagConstraints gbcC = new GridBagConstraints();
        gbcC.fill = GridBagConstraints.HORIZONTAL; gbcC.insets = new Insets(5, 5, 5, 5);
        
        // Fila 1
        gbcC.gridy = 0; gbcC.gridx = 0; pnlCombos.add(l1, gbcC);
        gbcC.gridx = 1; gbcC.weightx = 1.0; pnlCombos.add(cbDireccion, gbcC); gbcC.weightx = 0;
        gbcC.gridx = 2; pnlCombos.add(txtDirValor, gbcC);
        gbcC.gridx = 3; pnlCombos.add(u1, gbcC);
        // Fila 2
        gbcC.gridy = 1; gbcC.gridx = 0; pnlCombos.add(l2, gbcC);
        gbcC.gridx = 1; gbcC.weightx = 1.0; pnlCombos.add(cbAlcance, gbcC); gbcC.weightx = 0;
        gbcC.gridx = 2; pnlCombos.add(txtAlcValor, gbcC);
        gbcC.gridx = 3; pnlCombos.add(u2, gbcC);
        // Fila 3
        gbcC.gridy = 2; gbcC.gridx = 0; pnlCombos.add(l3, gbcC);
        gbcC.gridx = 1; gbcC.weightx = 1.0; pnlCombos.add(cbAltura, gbcC); gbcC.weightx = 0;
        gbcC.gridx = 2; pnlCombos.add(txtAltValor, gbcC);
        gbcC.gridx = 3; pnlCombos.add(u3, gbcC);

        pnlDerecho.add(pnlCombos, gbcR);
        
        // Misión
        gbcR.gridy++;
        JPanel pnlMision = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlMision.setOpaque(false);
        pnlMision.add(l6); pnlMision.add(l4);
        pnlDerecho.add(pnlMision, gbcR);
        
        // Impacto
        gbcR.gridy++;
        pnlDerecho.add(lblAlertaImpacto, gbcR);
        
        // Panel M.T.O.
        gbcR.gridy++;
        JPanel panelParametros = new JPanel(new GridBagLayout());
        panelParametros.setBackground(Color.BLACK);
        TitledBorder borde2 = BorderFactory.createTitledBorder("M.T.O.");
        borde2.setTitleColor(Color.WHITE);
        borde2.setTitleFont(new Font("Arial", Font.BOLD, 18));
        panelParametros.setBorder(borde2);
        
        GridBagConstraints gbcP = new GridBagConstraints();
        gbcP.fill = GridBagConstraints.HORIZONTAL;
        gbcP.insets = new Insets(10, 15, 10, 15);
        
        JLabel lblEPA = new JLabel("EPA"); lblEPA.setForeground(Color.WHITE); lblEPA.setFont(fontParam);
        JLabel lblAngOb = new JLabel("ANG. OB."); lblAngOb.setForeground(Color.WHITE); lblAngOb.setFont(fontParam);
        JLabel lblTVolido = new JLabel("T. VOLIDO"); lblTVolido.setForeground(Color.WHITE); lblTVolido.setFont(fontParam);
        
        gbcP.gridy = 0; gbcP.gridx = 0; gbcP.weightx = 0.6; panelParametros.add(lblEPA, gbcP);
        gbcP.gridx = 1; gbcP.weightx = 0.4; panelParametros.add(txtEPA, gbcP);
        gbcP.gridy = 1; gbcP.gridx = 0; panelParametros.add(lblAngOb, gbcP);
        gbcP.gridx = 1; panelParametros.add(txtAngOb, gbcP);
        gbcP.gridy = 2; gbcP.gridx = 0; panelParametros.add(lblTVolido, gbcP);
        gbcP.gridx = 1; panelParametros.add(txtTVolido, gbcP);
        
        pnlDerecho.add(panelParametros, gbcR);
        
        // Botones de Acción Derecho Inferior
        gbcR.gridy++;
        gbcR.weighty = 1.0; 
        gbcR.anchor = GridBagConstraints.SOUTH;
        JPanel pnlAcciones = new JPanel(new GridLayout(2, 2, 15, 15));
        pnlAcciones.setOpaque(false);
        btnNuevoPIF.setPreferredSize(new Dimension(160, 45));
        pnlAcciones.add(btnNuevoPIF);
        pnlAcciones.add(btnEnviar);
        pnlAcciones.add(btnFinMision);
        pnlAcciones.add(btnFuego);
        pnlDerecho.add(pnlAcciones, gbcR);

        // Combinar Todo
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.insets = new Insets(10, 10, 10, 10);
        
        gbcMain.gridx = 0; gbcMain.gridy = 0;
        gbcMain.weightx = 0.65; gbcMain.weighty = 1.0;
        add(pnlIzquierdo, gbcMain);
        
        gbcMain.gridx = 1;
        gbcMain.weightx = 0.35;
        add(pnlDerecho, gbcMain);

        // ---- 3. LISTENERS (Sin modificaciones de lógica) ----
        
        timerFuego = new Timer(500, e -> {
            fuegoEstado = !fuegoEstado;
            if (fuegoEstado) {
                btnFuego.setBackground(new Color(255, 60, 60));
            } else 
                btnFuego.setBackground(new Color(180, 0, 0));
        });
        
        btnFuego.addActionListener(a -> {
            firePropertyChange("ENVIAR_FUEGO", false, true);
            btnFuego.setVisible(false);
            timerFuego.stop();
        });
        
        btnHistorial.addActionListener(e -> {
            if (historial.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El historial está vacío.", "Historial", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder sb = new StringBuilder("HISTORIAL DE DISPAROS Y CORRECCIONES:\n\n");
            historial.forEach((key, value) -> {
                sb.append("DISPARO ").append(key).append(": ").append(value).append("\n");
            });

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(this, scrollPane, "Historial", JOptionPane.PLAIN_MESSAGE);
        });
        
        btnEnviar.addActionListener(e -> {
            try {
                int valorDir = Integer.parseInt(txtDirValor.getText().trim());
                int valorAlc = Integer.parseInt(txtAlcValor.getText().trim());

                int xMetros = cbDireccion.getSelectedItem().equals("DERECHA")
                        ? valorDir
                        : -valorDir;

                int yMetros = cbAlcance.getSelectedItem().equals("ALARGAR")
                        ? valorAlc
                        : -valorAlc;

                if (!chkModoAutomatico.isSelected()) {
                    panelCuadricula.registrarCorreccionManual(xMetros, yMetros);
                }

                historial.clear();
                historial.putAll(panelCuadricula.exportarHistorialTexto());

                lblUltima.setText(
                    "<html>ULTIMA CORRECCIÓN:<br>" +
                    cbDireccion.getSelectedItem() + " " + valorDir + "m, " +
                    cbAlcance.getSelectedItem() + " " + valorAlc + "m</html>"
                );

                if (metAtaqueYTiroPanel.orden().equals("A-MI-ORDEN")) {
                    btnFuego.setVisible(true);
                    timerFuego.start();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error en valores numéricos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        btnZoomIn.addActionListener(e ->
            panelCuadricula.zoomIn()
        );
        
        btnZoomOut.addActionListener(e ->
            panelCuadricula.zoomOut()
        );
        
        setPreferredSize(new Dimension(1100, 900)); 
    }
    
    // ----------- MÉTODOS Y CLASE INTERNA INTACTOS ------------

    public void setZoomHabilitado(boolean habilitado) {
        btnZoomIn.setEnabled(habilitado);
        btnZoomOut.setEnabled(habilitado);
    }
    
    public void registrarLabelDeModoDeMision(String s) {
        l4.setText(s);
    }
    
    public void registrarCorreccionAutomatica(int xMetros, int yMetros) {
        panelCuadricula.registrarCorreccionManual(xMetros, yMetros);
    }
    
    public void reiniciarCuadricula() {
        panelCuadricula.reiniciarCuadricula();
    }
    
    public void iniciarCuentaRegresivaVolido() {
        try {
            double tv = Double.parseDouble(txtTVolido.getText().trim().replace(",", "."));
            tiempoRetrocesoVolido = (int) Math.round(tv);

            if (tiempoRetrocesoVolido <= 0) return;

            if (timerVolido != null && timerVolido.isRunning()) {
                timerVolido.stop();
            }

            lblAlertaImpacto.setText("IMPACTO EN: " + tiempoRetrocesoVolido + "s");
            lblAlertaImpacto.setForeground(Color.ORANGE);
            lblAlertaImpacto.setBackground(Color.BLACK);
            lblAlertaImpacto.setOpaque(true);

            timerVolido = new Timer(1000, e -> {
            	tiempoRetrocesoVolido--;

                if (tiempoRetrocesoVolido == 5) {
                    
                    lblAlertaImpacto.setText("¡PIQUE! (5s)");
                    lblAlertaImpacto.setForeground(Color.WHITE);
                    lblAlertaImpacto.setBackground(new Color(220, 0, 0)); 

                } else if (tiempoRetrocesoVolido > 0) {
                    if (tiempoRetrocesoVolido > 5) {
                        lblAlertaImpacto.setText("PIQUE EN: " + tiempoRetrocesoVolido + "s");
                    } else {
                        lblAlertaImpacto.setText("¡PIQUE! (" + tiempoRetrocesoVolido + "s)");
                    }
                } else {
                    lblAlertaImpacto.setText("¡PIQUE!");
                    lblAlertaImpacto.setForeground(Color.BLACK);
                    lblAlertaImpacto.setBackground(new Color(0, 255, 100)); 
                    timerVolido.stop();
                    
                    Timer resetTimer = new Timer(3000, ev -> {
                        lblAlertaImpacto.setText("IMPACTO: --");
                        lblAlertaImpacto.setForeground(Color.DARK_GRAY);
                        lblAlertaImpacto.setBackground(Color.BLACK);
                    });
                    resetTimer.setRepeats(false);
                    resetTimer.start();
                }
            });
            timerVolido.start();

        } catch (NumberFormatException ex) {
            lblAlertaImpacto.setText("T.V. INVÁLIDO");
        }
    }
    
    public void actualizarBlanco(Blanco b) {
        if (b != null && b.getNombre() != null) {
            panelCuadricula.setNombreBlanco(b.getNombre());
        }
    }
    
    public Map<String, String> getHistorialCorrecciones() {
        return new LinkedHashMap<>(historial);
    }
    public Timer getTimerFuego() { return timerFuego;    }
    public JTextField getEPA() { return txtEPA; }
    public JTextField getAngOb() { return txtAngOb; }
    public JTextField getTVolido() { return txtTVolido; }
    public PanelCuadricula getPanelCuadricula() { return panelCuadricula; }
    public JButton getBtnEnviar() { return btnEnviar; }
    public JButton getBtnFuego() { return btnFuego; }
    public JButton getBtnFin() { return btnFinMision; }
    public JButton getBtnNuevoPIF() { return btnNuevoPIF; }
    public JButton getBtnVolver() { return btnVolver; }
    public JComboBox<String> getCbDireccion() { return cbDireccion; }
    public JComboBox<String> getCbAlcance() { return cbAlcance; }
    public JComboBox<String> getCbAltura() { return cbAltura; }
    public JTextField getTxtDirValor() { return txtDirValor; }
    public JTextField getTxtAlcValor() { return txtAlcValor; }
    public JTextField getTxtAltValor() { return txtAltValor; }
    public JLabel getLblUltima() { return lblUltima; }
    
    public class PanelCuadricula extends JPanel {

        private static final long serialVersionUID = 1L;
        private int metrosPorPunto = 200;      
        private static final int PUNTOS_SEMIEJE = 4; 
        @SuppressWarnings("unused")
        private static final int TAM_PUNTO = 10;

        private String nombreBlanco;
        @SuppressWarnings("unused")
        private Point disparoPixel;
        private int disparoXMetros;
        private int disparoYMetros;

        private static final int ZOOM_PASO = 50;
        private static final int ZOOM_MIN = 50;
        private static final int ZOOM_MAX = 1000;
        
        private int contadorDisparos = 0;
        private int contadorCorrecciones = 0;

        private List<PuntoRotulado> disparos = new ArrayList<>();
        private List<PuntoRotulado> correcciones = new ArrayList<>();
        private class ParDC {
            Point disparoMetros;
            Point correccionMetros;
        }

        private Map<Integer, ParDC> historialInterno = new HashMap<>();

        private class PuntoRotulado {
            Point metros;
            String label;
            Color color;

            PuntoRotulado(Point metros, String l, Color c) {
                this.metros = metros;
                this.label = l;
                this.color = c;
            }
        }

        public PanelCuadricula(int metrosInicial, String nombreBlanco) {
            this.metrosPorPunto = metrosInicial;
            this.nombreBlanco = nombreBlanco;
            setBackground(Color.BLACK);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    registrarDisparo(e.getX(), e.getY());
                }
            });
        }
        
        private int pasoPixel() {
            return (getWidth() / 2) / PUNTOS_SEMIEJE;
        }

        public void setMetrosPorPunto(int A) {
            if (A <= 0) return;
            this.metrosPorPunto = A;
            repaint();
        }
        
        public void zoomIn() {
            if (metrosPorPunto - ZOOM_PASO >= ZOOM_MIN) {
                metrosPorPunto -= ZOOM_PASO;
                repaint();
            }
        }

        public void zoomOut() {
            if (metrosPorPunto + ZOOM_PASO <= ZOOM_MAX) {
                metrosPorPunto += ZOOM_PASO;
                repaint();
            }
        }

        public Map<String, String> exportarHistorialTexto() {
            Map<String, String> out = new LinkedHashMap<>();
            historialInterno.forEach((id, par) -> {
                String disparo = "(" + par.disparoMetros.x + "m, " + par.disparoMetros.y + "m)";
                String correccion = (par.correccionMetros != null)
                        ? "(" + par.correccionMetros.x + "m, " + par.correccionMetros.y + "m)"
                        : "(SIN CORRECCIÓN)";
                out.put(String.valueOf(id), disparo + " → CORRECCIÓN " + id + " " + correccion);
            });
            return out;
        }
        
        private void registrarDisparo(int xPixel, int yPixel) {
            contadorDisparos++;
            disparoXMetros = pixelAMetrosX(xPixel);
            disparoYMetros = pixelAMetrosY(yPixel);

            disparos.add(new PuntoRotulado(
                    new Point(disparoXMetros, disparoYMetros),
                    "DISPARO " + contadorDisparos,
                    Color.GREEN
            ));

            ParDC par = new ParDC();
            par.disparoMetros = new Point(disparoXMetros, disparoYMetros);
            historialInterno.put(contadorDisparos, par);

            if (chkModoAutomatico.isSelected()) {
                ejecutarCorreccionAutomatica();
            }

            repaint();
        }
        
        public void setNombreBlanco(String nombre) {
            nombreBlanco = nombre;
        }

        private void ejecutarCorreccionAutomatica() {
            int deltaXObs = -disparoXMetros; 
            int deltaYObs = -disparoYMetros;

            double epa = obtenerValorNumerico(txtEPA.getText());
            double angObMils = obtenerValorNumerico(txtAngOb.getText());
            
            int correccionFinalDir = 0;
            int correccionFinalAlc = 0;

            if (angObMils > 0) {
                double angObRad = angObMils * (Math.PI / 3200.0);
                double deltaXBateria = (deltaXObs * Math.cos(angObRad)) - (deltaYObs * Math.sin(angObRad));
                double deltaYBateria = (deltaXObs * Math.sin(angObRad)) + (deltaYObs * Math.cos(angObRad));

                correccionFinalDir = (int) Math.round(deltaXBateria);
                correccionFinalAlc = (int) Math.round(deltaYBateria);
            } else {
                correccionFinalDir = deltaXObs;
                correccionFinalAlc = deltaYObs;
            }
            if (epa > 0) {
                if (Math.abs(correccionFinalAlc) < epa) {
                    correccionFinalAlc = 0; 
                }
            }
            contadorCorrecciones++;
            correcciones.add(new PuntoRotulado(
                    new Point(disparoXMetros + correccionFinalDir, disparoYMetros + correccionFinalAlc),
                    "CORRECCIÓN " + contadorCorrecciones,
                    Color.RED
            ));

            ParDC par = historialInterno.get(contadorDisparos);
            if (par != null) {
                par.correccionMetros = new Point(correccionFinalDir, correccionFinalAlc);
            }

            cbDireccion.setSelectedItem(correccionFinalDir >= 0 ? "DERECHA" : "IZQUIERDA");
            cbAlcance.setSelectedItem(correccionFinalAlc >= 0 ? "ALARGAR" : "ACORTAR");
            
            txtDirValor.setText(String.valueOf(Math.abs(correccionFinalDir)));
            txtAlcValor.setText(String.valueOf(Math.abs(correccionFinalAlc)));

            SwingUtilities.invokeLater(() -> btnEnviar.doClick());
        }

        private double obtenerValorNumerico(String texto) {
            try {
                if (texto == null || texto.trim().isEmpty()) return 0.0;
                return Double.parseDouble(texto.trim().replace(",", "."));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        public void registrarCorreccionManual(int deltaX, int deltaY) {
            contadorCorrecciones++;
            correcciones.add(new PuntoRotulado(
                    new Point(disparoXMetros + deltaX, disparoYMetros + deltaY),
                    "CORRECCIÓN " + contadorCorrecciones,
                    Color.RED
            ));

            ParDC par = historialInterno.get(contadorDisparos);
            if (par != null)
                par.correccionMetros = new Point(deltaX, deltaY);

            repaint();
        }

        @SuppressWarnings("unused")
        private int rangoMaxMetros() {
            return metrosPorPunto * PUNTOS_SEMIEJE;
        }

        private double pixelesPorMetro() {
            return pasoPixel() / (double) metrosPorPunto;
        }

        private int pixelAMetrosX(int px) {
            return (int) ((px - getWidth() / 2) / pixelesPorMetro());
        }

        private int pixelAMetrosY(int py) {
            return (int) ((getHeight() / 2 - py) / pixelesPorMetro());
        }
        
        public int getDisparoXMetros() {
            return disparoXMetros;
        }

        public int getDisparoYMetros() {
            return disparoYMetros;
        }

        private Point metrosAPixel(int mx, int my) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            return new Point(
                    cx + (int) (mx * pixelesPorMetro()),
                    cy - (int) (my * pixelesPorMetro())
            );
        }

        public void reiniciarCuadricula() {
            disparos.clear();
            correcciones.clear();
            historialInterno.clear();
            contadorDisparos = 0;
            contadorCorrecciones = 0;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;

            int paso = pasoPixel();

            g2.setColor(new Color(60, 60, 60));
            for (int i = -PUNTOS_SEMIEJE; i <= PUNTOS_SEMIEJE; i++) {

                int x = cx + i * paso;
                int y = cy + i * paso;

                g2.drawLine(x, 0, x, h);
                g2.drawLine(0, y, w, y);

                if (i != 0) {
                    String txt = (i * metrosPorPunto) + " m";
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawString(txt, x + 4, cy - 4);
                    g2.drawString(txt, cx + 4, y - 4);
                    g2.setColor(new Color(60, 60, 60));
                }
            }

            g2.setColor(Color.GRAY);
            g2.drawLine(cx, 0, cx, h);
            g2.drawLine(0, cy, w, cy);

            g2.setColor(Color.WHITE);
            g2.fillOval(cx - 5, cy - 5, 10, 10);
            g2.drawString(nombreBlanco, cx - 30, cy - 12);

            for (PuntoRotulado p : disparos) {
                Point px = metrosAPixel(p.metros.x, p.metros.y);
                g2.setColor(p.color);
                g2.fillOval(px.x - 5, px.y - 5, 10, 10);
                g2.drawString(p.label, px.x + 10, px.y);
            }

            for (PuntoRotulado p : correcciones) {
                Point px = metrosAPixel(p.metros.x, p.metros.y);
                g2.setColor(p.color);
                g2.fillOval(px.x - 5, px.y - 5, 10, 10);
                g2.drawString(p.label, px.x + 10, px.y);
            }
        }
    }
}