package interfaz;
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
    
    private MetodoAtaqueYTiroPanel metAtaqueYTiroPanel;

    private JTextField txtDirValor;
    private JTextField txtAlcValor;
    private JTextField txtAltValor;
    private JTextField txtEPA, txtAngOb, txtTVolido;
    private JLabel l4;

    private JCheckBox chkModoAutomatico;
    private JButton btnNuevoPIF;
    private JButton btnFinMision;
    private JButton btnEnviar;	
    private JButton btnVolver;
    private JButton btnFuego;
    private JButton btnHistorial;
    
    private Map<String,String> historial;
    
    private Timer timerFuego;
    private boolean fuegoEstado = false; 

    private JLabel lblUltima;
    
    private PanelCuadricula panelCuadricula;
    private int ESCALA_METROS_POR_CUADRICULA = 200;

    public CorreccionesPanel(Blanco blanco, MetodoAtaqueYTiroPanel m) {
    	
    	metAtaqueYTiroPanel = m;
    	
    	historial = new HashMap<String,String>();
    	
    	TitledBorder borde1 = BorderFactory.createTitledBorder("CORRECCIONES");
    	borde1.setTitleColor(Color.WHITE);
    	borde1.setTitleFont(new Font("Arial", Font.BOLD, 18));

    	this.setBorder(borde1);
    	
        setLayout(null);
        setBackground(new Color(0,0,0));
        
        btnZoomIn = new JButton("+");
        btnZoomOut = new JButton("-");

        btnZoomIn.setBounds(20, 560, 50, 30);
        btnZoomOut.setBounds(90, 560, 50, 30);

        for (JButton b : new JButton[]{btnZoomIn, btnZoomOut}) {
            b.setFont(new Font("Arial", Font.BOLD, 18));
            b.setFocusPainted(false);
            b.setBackground(new Color(60,60,60));
            b.setForeground(Color.WHITE);
            add(b);
        }
        
        JPanel panelParametros = new JPanel();
        TitledBorder borde2 = BorderFactory.createTitledBorder("M.T.O.");
    	borde2.setTitleColor(Color.WHITE);
    	borde2.setTitleFont(new Font("Arial", Font.BOLD, 18));
        panelParametros.setLayout(null);
        panelParametros.setBackground(Color.BLACK);
        panelParametros.setBorder(borde2);
        panelParametros.setBounds(730, 320, 350, 200); 
        add(panelParametros);

        Font fontParam = new Font("Arial", Font.BOLD, 14);
        int labelX = 20;
        int fieldX = 180;
        int widthLabel = 150;
        int widthField = 100;

        // 1) EPA
        JLabel lblEPA = new JLabel("EPA");
        lblEPA.setForeground(Color.WHITE);
        lblEPA.setFont(fontParam);
        lblEPA.setBounds(labelX, 30, widthLabel, 30);
        panelParametros.add(lblEPA);

        txtEPA = new JTextField("0");
        txtEPA.setBackground(new Color(40, 80, 120));
        txtEPA.setForeground(Color.WHITE);
        txtEPA.setHorizontalAlignment(SwingConstants.CENTER);
        txtEPA.setBounds(fieldX, 30, widthField, 30);
        panelParametros.add(txtEPA);

        // ANG. OB.
        JLabel lblAngOb = new JLabel("ANG. OB.");
        lblAngOb.setForeground(Color.WHITE);
        lblAngOb.setFont(fontParam);
        lblAngOb.setBounds(labelX, 90, widthLabel, 30);
        panelParametros.add(lblAngOb);

        txtAngOb = new JTextField("0");
        txtAngOb.setBackground(new Color(40, 80, 120));
        txtAngOb.setForeground(Color.WHITE);
        txtAngOb.setHorizontalAlignment(SwingConstants.CENTER);
        txtAngOb.setBounds(fieldX, 90, widthField, 30);
        panelParametros.add(txtAngOb);

        // T. VOLIDO
        JLabel lblTVolido = new JLabel("T. VOLIDO");
        lblTVolido.setForeground(Color.WHITE);
        lblTVolido.setFont(fontParam);
        lblTVolido.setBounds(labelX, 150, widthLabel, 30);
        panelParametros.add(lblTVolido);

        txtTVolido = new JTextField("0");
        txtTVolido.setBackground(new Color(40, 80, 120));
        txtTVolido.setForeground(Color.WHITE);
        txtTVolido.setHorizontalAlignment(SwingConstants.CENTER);
        txtTVolido.setBounds(fieldX, 150, widthField, 30);
        panelParametros.add(txtTVolido);

        Font font = new Font("Arial", Font.BOLD, 16);

        JLabel l1 = new JLabel("EN DIRECCIÓN");
        JLabel l2 = new JLabel("EN ALCANCE");
        JLabel l3 = new JLabel("EN ALTURA");
        l4 = new JLabel("EFICACIA");
        JLabel l5 = new JLabel("ULTIMA CORRECCIÓN:");
        JLabel l6 = new JLabel("MISION:");

        for (JLabel l : new JLabel[]{l1,l2,l3}) {
            l.setForeground(Color.WHITE);
            l.setFont(font);
            add(l);
        }
        
        l4.setForeground(Color.GREEN);
        l4.setFont(new Font("Arial", Font.BOLD, 22));
        add(l4);
        
        l6.setForeground(Color.WHITE);
        l6.setFont(new Font("Arial", Font.BOLD, 22));
        add(l6);
       
        l5.setForeground(Color.RED);
        l5.setFont(new Font("Arial", Font.BOLD, 22));
        add(l5);
        
        chkModoAutomatico = new JCheckBox("MODO AUTOMÁTICO");
        chkModoAutomatico.setFont(new Font("Arial", Font.BOLD, 16));
        chkModoAutomatico.setForeground(Color.WHITE);
        chkModoAutomatico.setBackground(Color.BLACK);
        chkModoAutomatico.setBounds(450, 620, 250, 40);
        chkModoAutomatico.setIcon(new CheckIconCustom(22));
        add(chkModoAutomatico);

        cbDireccion = new JComboBox<>(new String[]{"IZQUIERDA","DERECHA"});
        cbAlcance = new JComboBox<>(new String[]{"ALARGAR","ACORTAR"});
        cbAltura = new JComboBox<>(new String[]{"SUBIR","BAJAR"});

        JComboBox<?>[] combos = {cbDireccion,cbAlcance,cbAltura};
        for (JComboBox<?> cb : combos) {
            cb.setBackground(new Color(20,40,80));
            cb.setForeground(Color.WHITE);
            cb.setFont(font);
            cb.setFocusable(false);
            add(cb);
        }

        txtDirValor = new JTextField("0");
        txtAlcValor = new JTextField("0");
        txtAltValor = new JTextField("0");

        for (JTextField t : new JTextField[]{txtDirValor,txtAlcValor,txtAltValor}) {
            t.setBackground(new Color(40,80,120));
            t.setForeground(Color.WHITE);
            t.setHorizontalAlignment(SwingConstants.CENTER);
            t.setFont(font);
            add(t);
        }

        JLabel u1 = new JLabel("Mts");
        JLabel u2 = new JLabel("Mts");
        JLabel u3 = new JLabel("Mts");
        for (JLabel u : new JLabel[]{u1,u2,u3}) {
            u.setForeground(new Color(200,200,200));
            u.setFont(font);
            add(u);
        }

        btnNuevoPIF = new JButton("NUEVO PIF");
        btnFinMision = new JButton("FIN DE MISION");
        btnEnviar = new JButton("ENVIAR");
        btnHistorial = new JButton("HISTORIAL");

        JButton[] bs = {btnNuevoPIF,btnFinMision,btnEnviar};

        btnNuevoPIF.setBackground(new Color(200,255,200));
        btnFinMision.setBackground(Color.GRAY);
        btnEnviar.setBackground(Color.RED);
        
        timerFuego = new Timer(500, e -> {
            fuegoEstado = !fuegoEstado;

            if (fuegoEstado) {
                btnFuego.setBackground(new Color(255, 60, 60)); 
            } else 
                btnFuego.setBackground(new Color(180, 0, 0));
        });
        
        for (JButton b : bs) {
            b.setForeground(Color.BLACK);
            b.setFont(new Font("Arial", Font.BOLD, 18));
            b.setFocusPainted(false);
            add(b);
        }
        
        lblUltima = l5;
        
        int x1 = 710;
        int x2 = 830;
        int x3 = 990;
        int x4 = 1060;

        l1.setBounds(710, 30, 200, 28);
        cbDireccion.setBounds(830, 30, 150, 32);
        txtDirValor.setBounds(990, 30, 60, 32);
        u1.setBounds(1060, 30, 60, 32);

        l2.setBounds(x1, 70, 200, 28);
        cbAlcance.setBounds(x2, 70, 150, 32);
        txtAlcValor.setBounds(x3, 70, 60, 32);
        u2.setBounds(x4, 70, 60, 32);

        l3.setBounds(x1, 110, 200, 28);
        cbAltura.setBounds(x2, 110, 150, 32);
        txtAltValor.setBounds(x3, 110, 60, 32);
        u3.setBounds(x4, 110, 60, 32);

        l6.setBounds(x1, 160, 200, 28);
        l4.setBounds(810, 160, 200, 28);
        l5.setBounds(x1, 200, 500, 80);

        btnNuevoPIF.setBounds(710, 550, 180, 45);
        btnFinMision.setBounds(710, 605, 180, 45);
        btnEnviar.setBounds(900, 550, 180, 45);

        btnVolver = new JButton("VOLVER");
        btnVolver.setBounds(20, 620, 180, 40);
        btnVolver.setBackground(new Color(60,60,60));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Arial", Font.BOLD, 18));
        btnVolver.setFocusPainted(false);
        add(btnVolver);
        
        btnHistorial.setBounds(220, 620, 180, 40);
        btnHistorial.setBackground(new Color(60,60,60));
        btnHistorial.setForeground(Color.WHITE);
        btnHistorial.setFont(new Font("Arial", Font.BOLD, 18));
        btnHistorial.setFocusPainted(false);
        add(btnHistorial);
        
        btnFuego = new JButton("FUEGO"); 
        btnFuego.setBounds(900, 605, 180, 45);
        btnFuego.setBackground(new Color(200, 0, 0)); 
        btnFuego.setForeground(Color.WHITE);
        btnFuego.setFont(new Font("Arial", Font.BOLD, 18));
        btnFuego.setFocusPainted(false);
        btnFuego.setVisible(false); 
        add(btnFuego);
        
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
        
        String nombreBlanco = (blanco != null && blanco.getNombre() != null) ? blanco.getNombre() : "Blanco";
        panelCuadricula = new PanelCuadricula(ESCALA_METROS_POR_CUADRICULA, nombreBlanco);
        
        panelCuadricula.setBounds(10, 30, 680, 570); 
        
        panelCuadricula.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        add(panelCuadricula);


        panelCuadricula.setMetrosPorPunto(30);
        
        this.setComponentZOrder(panelCuadricula, getComponentCount() - 1);
        this.setComponentZOrder(btnZoomIn, 0);
        this.setComponentZOrder(btnZoomOut, 0);
    }
    
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
    
    public void actualizarBlanco(Blanco b) {
    	if (b != null && b.getNombre() != null) {
            panelCuadricula.setNombreBlanco(b.getNombre());
        }
    }
    
    public Map<String, String> getHistorialCorrecciones() {
        return new LinkedHashMap<>(historial);
    }
    public Timer getTimerFuego() { return timerFuego;	 }
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
    
    class PanelCuadricula extends JPanel {

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

                String disparo = "(" + par.disparoMetros.x + "m, " +
                                        par.disparoMetros.y + "m)";

                String correccion = (par.correccionMetros != null)
                        ? "(" + par.correccionMetros.x + "m, " +
                           par.correccionMetros.y + "m)"
                        : "(SIN CORRECCIÓN)";

                out.put(
                    String.valueOf(id),
                    disparo + " → CORRECCIÓN " + id + " " + correccion
                );
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

            int deltaX = -disparoXMetros;
            int deltaY = -disparoYMetros;

            contadorCorrecciones++;

            correcciones.add(new PuntoRotulado(
                    new Point(disparoXMetros + deltaX, disparoYMetros + deltaY),
                    "CORRECCIÓN " + contadorCorrecciones,
                    Color.RED
            ));

            ParDC par = historialInterno.get(contadorDisparos);
            if (par != null) {
                par.correccionMetros = new Point(deltaX, deltaY);
            }

            cbDireccion.setSelectedItem(deltaX >= 0 ? "DERECHA" : "IZQUIERDA");
            cbAlcance.setSelectedItem(deltaY >= 0 ? "ALARGAR" : "ACORTAR");
            txtDirValor.setText(String.valueOf(Math.abs(deltaX)));
            txtAlcValor.setText(String.valueOf(Math.abs(deltaY)));

            SwingUtilities.invokeLater(() -> btnEnviar.doClick());
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
