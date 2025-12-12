package interfaz;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import dominio.Blanco;

public class CorreccionesPanel extends JPanel {

	private static final long serialVersionUID = -271089343555480088L;
	private JComboBox<String> cbDireccion;
    private JComboBox<String> cbAlcance;
    private JComboBox<String> cbAltura;

    private JTextField txtDirValor;
    private JTextField txtAlcValor;
    private JTextField txtAltValor;

    private JButton btnNuevoPIF;
    private JButton btnFinMision;
    private JButton btnEnviar;	
    private JButton btnVolver;

    private JLabel lblUltima;
    
    private PanelCuadricula panelCuadricula;
    private final int ESCALA_METROS_POR_CUADRICULA = 200;

    public CorreccionesPanel(Blanco blanco) {	
    	
    	TitledBorder borde = BorderFactory.createTitledBorder("CORRECCIONES");
    	borde.setTitleColor(Color.WHITE);
    	borde.setTitleFont(new Font("Arial", Font.BOLD, 18));

    	this.setBorder(borde);
    	
        setLayout(null);
        setBackground(new Color(0,0,0));

        Font font = new Font("Arial", Font.BOLD, 16);

        JLabel l1 = new JLabel("EN DIRECCIÓN");
        JLabel l2 = new JLabel("EN ALCANCE");
        JLabel l3 = new JLabel("EN ALTURA");
        JLabel l4 = new JLabel("EFICACIA");
        JLabel l5 = new JLabel("ULTIMA CORRECCIÓN:");

        for (JLabel l : new JLabel[]{l1,l2,l3,l4}) {
            l.setForeground(Color.WHITE);
            l.setFont(font);
            add(l);
        }
        
        l5.setForeground(Color.RED);
        l5.setFont(font);
        add(l5);

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

        JButton[] bs = {btnNuevoPIF,btnFinMision,btnEnviar};

        btnNuevoPIF.setBackground(new Color(200,255,200));
        btnFinMision.setBackground(Color.GRAY);
        btnEnviar.setBackground(Color.RED);
        

        for (JButton b : bs) {
            b.setForeground(Color.BLACK);
            b.setFont(new Font("Arial", Font.BOLD, 18));
            b.setFocusPainted(false);
            add(b);
        }
        
        lblUltima = l5;
        
        int x1 = 40;
        int x2 = 260;
        int x3 = 460;
        int x4 = 550;

        l1.setBounds(x1, 40, 200, 28);
        cbDireccion.setBounds(x2, 40, 180, 32);
        txtDirValor.setBounds(x3, 40, 60, 32);
        u1.setBounds(x4, 40, 60, 32);

        l2.setBounds(x1, 100, 200, 28);
        cbAlcance.setBounds(x2, 100, 180, 32);
        txtAlcValor.setBounds(x3, 100, 60, 32);
        u2.setBounds(x4, 100, 60, 32);

        l3.setBounds(x1, 160, 200, 28);
        cbAltura.setBounds(x2, 160, 180, 32);
        txtAltValor.setBounds(x3, 160, 60, 32);
        u3.setBounds(x4, 160, 60, 32);

        l4.setBounds(x1, 220, 200, 28);
        l5.setBounds(x1, 260, 500, 28);

        btnNuevoPIF.setBounds(40, 340, 180, 45);
        btnFinMision.setBounds(240, 340, 180, 45);
        btnEnviar.setBounds(440, 340, 180, 45);

        btnVolver = new JButton("VOLVER");
        btnVolver.setBounds(40, 400, 180, 40);
        btnVolver.setBackground(new Color(60,60,60));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Arial", Font.BOLD, 18));
        btnVolver.setFocusPainted(false);
        add(btnVolver);
        
        btnEnviar.addActionListener(e -> {
            int xMetros = 0; 
            int yMetros = 0; 
            
            try {
                int valorDir = Integer.parseInt(txtDirValor.getText().trim());
                int valorAlc = Integer.parseInt(txtAlcValor.getText().trim());

                String direccion = (String) cbDireccion.getSelectedItem();
                if ("DERECHA".equals(direccion)) {
                    xMetros = valorDir;	
                } else if ("IZQUIERDA".equals(direccion)) {
                    xMetros = -valorDir;	
                }

                String alcance = (String) cbAlcance.getSelectedItem();
                if ("ALARGAR".equals(alcance)) {
                    yMetros = valorAlc;	
                } else if ("ACORTAR".equals(alcance)) {
                    yMetros = -valorAlc;	
                }
                
                registrarCorreccionAutomatica(xMetros, yMetros);
                
                lblUltima.setText("ULTIMA CORRECCIÓN: " + 
                                  cbDireccion.getSelectedItem() + " " + valorDir + "m, " + 
                                  cbAlcance.getSelectedItem() + " " + valorAlc + "m.");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error: Los valores de corrección deben ser números válidos.", 
                    "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        setPreferredSize(new Dimension(1100, 500));	
        String nombreBlanco = (blanco != null && blanco.getNombre() != null) ? blanco.getNombre() : "Blanco";
        panelCuadricula = new PanelCuadricula(ESCALA_METROS_POR_CUADRICULA, nombreBlanco);
        panelCuadricula.setBounds(650, 40, 400, 400);	
        panelCuadricula.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(panelCuadricula);
    }
    
    public void registrarCorreccionAutomatica(int xMetros, int yMetros) {
        panelCuadricula.addCorreccion(xMetros, yMetros);
    }
    
    public void reiniciarCuadricula() {
    	panelCuadricula.reiniciarCuadricula();
    }
    
    public void actualizarBlanco(Blanco b) {
    	if (b != null && b.getNombre() != null) {
            panelCuadricula.setNombreBlanco(b.getNombre());
        }
    }
    
    public PanelCuadricula getPanelCuadricula() { return panelCuadricula; }
    public JButton getBtnEnviar() { return btnEnviar; }
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
    
    private class PanelCuadricula extends JPanel {

		private static final long serialVersionUID = 7531765244066433113L;

		private class CorrectionData {
            Point pixelLocation;
            String label;

            public CorrectionData(Point pixelLocation, String label) {
                this.pixelLocation = pixelLocation;
                this.label = label;
            }
        }

        private final int escala;	
        private final int TAM_PUNTO = 10;
        private String nombreBlanco;	
        private Point impactoUsuario = null;	
        private List<CorrectionData> correcciones;	
        private final int CUADRANTES_LADO = 4;
        
        private int xImpactoMetros = 0; 
        private int yImpactoMetros = 0; 
        
        private int xCurrentMetros = 0; 
        private int yCurrentMetros = 0; 

        public PanelCuadricula(int escala, String nombreBlanco) {
            this.escala = escala;
            this.nombreBlanco = nombreBlanco;
            this.correcciones = new ArrayList<>();
            setBackground(Color.BLACK);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    addImpactoUsuarioPixels(e.getX(), e.getY());
                }
            });
        }
        
        public void setNombreBlanco(String g) {
        	this.nombreBlanco = g;
        	repaint();
        }
        
        public void addImpactoUsuarioPixels(int xPixel, int yPixel) {
            this.impactoUsuario = new Point(xPixel, yPixel);
            
            int panelWidth = getWidth();
            final int RANGO_TOTAL_METROS = 1600; 
            double pixelesPorMetro = (double)panelWidth / RANGO_TOTAL_METROS;
            int centroX = panelWidth / 2;
            int centroY = getHeight() / 2;

            this.xImpactoMetros = (int)((xPixel - centroX) / pixelesPorMetro);
            this.yImpactoMetros = (int)((centroY - yPixel) / pixelesPorMetro);
            
            this.xCurrentMetros = this.xImpactoMetros;
            this.yCurrentMetros = this.yImpactoMetros;
            
            repaint();
        }
        
        public void addCorreccion(int xCorrection, int yCorrection) {
                    
            int xNewMetros = this.xCurrentMetros + xCorrection;
            int yNewMetros = this.yCurrentMetros + yCorrection;

            int panelWidth = getWidth();
            int panelHeight = getHeight();
            final int RANGO_TOTAL_METROS = 1600;
            double pixelesPorMetro = (double)panelWidth / RANGO_TOTAL_METROS;
            
            int centroX = panelWidth / 2;
            int centroY = panelHeight / 2;
            
            long xOffset = Math.round(xNewMetros * pixelesPorMetro);
            long yOffset = Math.round(yNewMetros * pixelesPorMetro);

            int xPixel = centroX + (int)xOffset;
            int yPixel = centroY - (int)yOffset; 
            
            if (xPixel >= 0 && xPixel < panelWidth && yPixel >= 0 && yPixel < panelHeight) {
                String label = "C" + (this.correcciones.size() + 1);
                this.correcciones.add(new CorrectionData(new Point(xPixel, yPixel), label));
                
                this.xCurrentMetros = xNewMetros;
                this.yCurrentMetros = yNewMetros;
            }
            repaint();
        }

        public void reiniciarCuadricula() {
            this.correcciones.clear();
            this.impactoUsuario = null; 
            this.xImpactoMetros = 0;
            this.yImpactoMetros = 0;
            
            this.xCurrentMetros = 0;
            this.yCurrentMetros = 0;

            repaint(); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int centroX = w / 2;
            int centroY = h / 2;
            
            int numLineas = CUADRANTES_LADO * 2;
            int paso = w / numLineas;	
            
            g2d.setColor(new Color(50, 50, 50));
            for (int i = 0; i <= numLineas; i++) {
                g2d.drawLine(i * paso, 0, i * paso, h);
                g2d.drawLine(0, i * paso, w, i * paso);	
            }
            
            g2d.setColor(new Color(100, 100, 100));
            g2d.drawLine(centroX, 0, centroX, h);
            g2d.drawLine(0, centroY, w, centroY);
            
            g2d.setColor(new Color(150, 150, 150));
            g2d.setFont(new Font("Consolas", Font.PLAIN, 10));
            
            for (int i = 1; i <= CUADRANTES_LADO; i++) {
                int metros = i * escala;
                String label = String.valueOf(metros);
                
                g2d.drawString(label, centroX + (i * paso) - 15, centroY + 12);
                g2d.drawString("-" + label, centroX - (i * paso) + 5, centroY + 12);

                g2d.drawString(label, centroX + 5, centroY - (i * paso) + 12);
                g2d.drawString("-" + label, centroX + 5, centroY + (i * paso) - 2);
            }

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(
                nombreBlanco,	
                centroX - (g2d.getFontMetrics().stringWidth(nombreBlanco) / 2),	
                centroY - 10
            );
            
            g2d.fillOval(centroX - TAM_PUNTO / 2, centroY - TAM_PUNTO / 2, TAM_PUNTO, TAM_PUNTO);
            
            if (impactoUsuario != null) {
                g2d.setColor(Color.GREEN);
                g2d.fillOval(
                    impactoUsuario.x - TAM_PUNTO / 2,	
                    impactoUsuario.y - TAM_PUNTO / 2,	
                    TAM_PUNTO,	
                    TAM_PUNTO
                );
            }
            
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            for (CorrectionData data : correcciones) {
                g2d.fillOval(data.pixelLocation.x - TAM_PUNTO / 2, data.pixelLocation.y - TAM_PUNTO / 2, TAM_PUNTO, TAM_PUNTO);
                g2d.drawString(data.label, data.pixelLocation.x + TAM_PUNTO, data.pixelLocation.y + TAM_PUNTO / 2);
            }
        }
    }
}