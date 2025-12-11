package interfaz;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CuadriculaDeCorrecciones extends JPanel {

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

    // --- NUEVOS ATRIBUTOS para la visualización ---
    private PanelCuadricula panelCuadricula;
    private final int ESCALA_METROS_POR_CUADRICULA = 200;

    public CuadriculaDeCorrecciones() {

        setLayout(null);
        setBackground(new Color(0,0,0));

        Font font = new Font("Arial", Font.BOLD, 16);

        JLabel l1 = new JLabel("EN DIRECCIÓN");
        JLabel l2 = new JLabel("EN ALCANCE");
        JLabel l3 = new JLabel("EN ALTURA");
        JLabel l4 = new JLabel("EFICACIA");
        JLabel l5 = new JLabel("ULTIMA CORRECCIÓN:");

        for (JLabel l : new JLabel[]{l1,l2,l3,l4,l5}) {
            l.setForeground(Color.WHITE);
            l.setFont(font);
            add(l);
        }

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

        txtDirValor = new JTextField("10");
        txtAlcValor = new JTextField("10");
        txtAltValor = new JTextField("10");

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
        btnFinMision.setBackground(Color.ORANGE);
        btnEnviar.setBackground(new Color(220,180,0));
        

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
        
        // --- INTEGRACIÓN DEL PANEL DE CUADRÍCULA ---
        panelCuadricula = new PanelCuadricula(ESCALA_METROS_POR_CUADRICULA);
        panelCuadricula.setBounds(650, 40, 400, 400); 
        panelCuadricula.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(panelCuadricula);
    }

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
    
    // Métodos para interactuar con la cuadrícula (en metros)
    public void registrarImpactoUsuario(int x, int y) {
        // En el ejemplo anterior, el MouseListener ya llama a un método de píxeles,
        // pero este método existe para ser consistente con la clase.
        // Se podría añadir lógica de conversión aquí si el usuario solo da metros.
    }
    
    public void registrarCorreccionAutomatica(int xMetros, int yMetros) {
        panelCuadricula.addCorreccion(xMetros, yMetros);
    }
    
    public PanelCuadricula getPanelCuadricula() {
        return panelCuadricula;
    }
    

    // --- SUBCLASE PanelCuadricula ---
    class PanelCuadricula extends JPanel {
		private static final long serialVersionUID = 8790916219148017168L;
		private final int escala; // 200 metros por cuadrícula
        private final int TAM_PUNTO = 8;
        private Point impactoUsuario = null; 
        private List<Point> correcciones;

        public PanelCuadricula(int escala) {
            this.escala = escala;
            this.correcciones = new ArrayList<>();
            setBackground(Color.BLACK);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    addImpactoUsuarioPixels(e.getX(), e.getY());
                }
            });
        }
        
        public void addImpactoUsuarioPixels(int xPixel, int yPixel) {
            this.impactoUsuario = new Point(xPixel, yPixel);
            repaint();
        }
        
        public void reiniciarLista() {
        	int cont = 0;
        	while(!correcciones.isEmpty()) {
        		correcciones.remove(cont);
        		cont++;
        	}
        }
        
        public void addCorreccion(int xMetros, int yMetros) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // Asumiendo una cuadrícula de 4x4 (800m x 800m) a cada lado del centro
            int maxMetros = 800; 
            // Píxeles por unidad de metro
            double pixelesPorMetro = (double)(panelWidth / 2) / maxMetros; 
            
            int centroX = panelWidth / 2;
            int centroY = panelHeight / 2;
            
            // Conversión
            int xPixel = centroX + (int)(xMetros * pixelesPorMetro);
            int yPixel = centroY - (int)(yMetros * pixelesPorMetro); // Y invertido
            
            this.correcciones.add(new Point(xPixel, yPixel));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            int w = getWidth();
            int h = getHeight();
            int centroX = w / 2;
            int centroY = h / 2;
            
            // 1. Dibujar Cuadrícula (200m/cuadrícula)
            g2d.setColor(new Color(50, 50, 50));
            int cuadrantesLado = 4; // 4 cuadrículas de 200m a cada lado = 800m
            int numLineas = cuadrantesLado * 2;
            int paso = w / numLineas; 
            
            for (int i = 0; i < numLineas + 1; i++) {
                g2d.drawLine(i * paso, 0, i * paso, h);
                g2d.drawLine(0, i * paso, w, i * paso);
            }
            
            // 2. Dibujar Centro (Blanco)
            g2d.setColor(Color.WHITE);
            g2d.fillOval(centroX - TAM_PUNTO / 2, centroY - TAM_PUNTO / 2, TAM_PUNTO, TAM_PUNTO);
            
            // 3. Dibujar Impacto del Usuario (VERDE)
            if (impactoUsuario != null) {
                g2d.setColor(Color.GREEN);
                g2d.fillOval(
                    impactoUsuario.x - TAM_PUNTO / 2, 
                    impactoUsuario.y - TAM_PUNTO / 2, 
                    TAM_PUNTO, 
                    TAM_PUNTO
                );
            }
            
            // 4. Dibujar Correcciones Automáticas (ROJO)
            g2d.setColor(Color.RED);
            for (Point p : correcciones) {
                g2d.fillOval(p.x - TAM_PUNTO / 2, p.y - TAM_PUNTO / 2, TAM_PUNTO, TAM_PUNTO);
            }
        }
    }
}