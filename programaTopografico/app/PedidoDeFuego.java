package app;
	
import java.awt.*;
import java.time.LocalDateTime;
import java.util.LinkedList;

import javax.swing.*;

import dominio.Blanco;
import dominio.GeneradorPDF;
import dominio.PIF;
import gestores.GestorEnlaceOperativo;
import interfaces.DialogFactory;
import panelesSecundarios.CorreccionesPanel;
import panelesSecundarios.DatosBlanco;
import panelesSecundarios.Mensajeria;
import panelesSecundarios.MetodoAtaqueYTiroPanel;
import panelesSecundarios.PanelMapa;
import util.FabricaComponentes;
	
public class PedidoDeFuego extends JPanel {

	private static final long serialVersionUID = 1L;

    private LinkedList<Blanco> listaDeBlancos;

    private CardLayout cardLayout;
    private JPanel pifCardPanel;
    private DatosBlanco datosDeBlancoPanel;
    private MetodoAtaqueYTiroPanel metodoYTiroPanel;
    private JPanel pifPanel;
    private JPanel panelMapaObsHolder;
    private DefaultListModel<PIF> modeloHistorial;
    private JList<PIF> listaHistorial;
    private Mensajeria PanelMensajeria;
    private GeneradorPDF generadorPDF;
    private DialogFactory dialogFactory; 

    private GestorEnlaceOperativo comunicacionIP;

    private JButton btnDatos;
    private JButton btnMetodo;

    private final String[] ordenNavegable = {"datos", "metodoTiro"};
    private volatile int indiceActual = 0;
    private volatile boolean transicionEnCurso = false;

    private String idOAA;

    public PedidoDeFuego(LinkedList<Blanco> listaDeBlancos, String idOAA) {

        this.idOAA = idOAA;
        this.listaDeBlancos = listaDeBlancos;
        this.generadorPDF = new GeneradorPDF(this);

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        pifPanel = new JPanel(new BorderLayout());
        pifPanel.setBackground(Color.BLACK);
        pifPanel.setBorder(FabricaComponentes.crearBordeTitulo(""));

        crearPanelDeBotones();
        crearCardLayout();
        crearBotonesDeNavegacion();
        crearPanelHistorial();

        inicializarAccionesMT();
        inicializarAccionesCorrecciones();

        add(pifPanel, BorderLayout.CENTER);

        actualizarBotones();
    }

    public void setDialogFactory(DialogFactory dialogFactory) {
        this.dialogFactory = dialogFactory;
    }

    public void setComunicacionIP(GestorEnlaceOperativo com) {
        this.comunicacionIP = com;
    }
    
    public void recibirMTO(String epa, String angob, String tvolido) {
    	metodoYTiroPanel.getCorreccionesPanel().getEPA().setText(epa);
    	metodoYTiroPanel.getCorreccionesPanel().getAngOb().setText(angob);
    	metodoYTiroPanel.getCorreccionesPanel().getTVolido().setText(tvolido);
    }
    
    public void mostrarDatosDeBlanco() {
        SwingUtilities.invokeLater(() -> {
            indiceActual = 0; 
            cardLayout.show(pifCardPanel, "datos");
            actualizarBotones();
        });
    }

    public void mostrarMetodoYAtaque() {
        SwingUtilities.invokeLater(() -> {
            indiceActual = 1; 
            cardLayout.show(pifCardPanel, "metodoTiro");
            actualizarBotones();
        });
    }

    private void crearCardLayout() {

        cardLayout = new CardLayout();
        pifCardPanel = new JPanel(cardLayout);
        pifCardPanel.setBackground(Color.BLACK);

        panelMapaObsHolder = new JPanel(new BorderLayout());
        panelMapaObsHolder.setBackground(Color.BLACK);

        datosDeBlancoPanel = new DatosBlanco();

        metodoYTiroPanel = new MetodoAtaqueYTiroPanel(datosDeBlancoPanel);

        JPanel contSup = new JPanel(new BorderLayout());
        contSup.add(datosDeBlancoPanel, BorderLayout.CENTER);
        contSup.setPreferredSize(new Dimension(0, 240));
        contSup.setBackground(Color.BLACK);

        JPanel contTot = new JPanel(new BorderLayout());
        contTot.setBackground(Color.BLACK);
        contTot.add(contSup, BorderLayout.NORTH);
        contTot.add(panelMapaObsHolder, BorderLayout.CENTER);

        pifCardPanel.add(contTot, "datos");
        pifCardPanel.add(metodoYTiroPanel, "metodoTiro");

        cardLayout.show(pifCardPanel, "datos");
    }

    public void setMapaObservacion(PanelMapa mapaBase) {

        JPanel vistaSoloObs = mapaBase.crearVistaSoloObservacion();

        panelMapaObsHolder.removeAll();
        panelMapaObsHolder.add(vistaSoloObs, BorderLayout.CENTER);
        panelMapaObsHolder.revalidate();
        panelMapaObsHolder.repaint();
    }

    private void crearPanelDeBotones() {

        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBackground(Color.BLACK);

        panel.setPreferredSize(new Dimension(0, 30));
        
        btnDatos = new JButton("DATOS DEL BLANCO");
        btnMetodo = new JButton("MÉTODO Y TIRO");

        for (JButton b : new JButton[]{btnDatos, btnMetodo}) {
            b.setBackground(new Color(60, 60, 60));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        }

        panel.add(btnDatos);
        panel.add(btnMetodo);

        pifPanel.add(panel, BorderLayout.NORTH);
    }
    
    private void actualizarBotones() {
        Color activo = new Color(227, 7, 7);
        Color inactivo = new Color(60, 60, 60);

        btnDatos.setBackground(indiceActual == 0 ? activo : inactivo);
        btnMetodo.setBackground(indiceActual == 1 ? activo : inactivo);
    }

    private void crearPanelHistorial() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setBorder(FabricaComponentes.crearBordeTitulo("HISTORIAL DE PIF"));
        panel.setPreferredSize(new Dimension(300, 0));

        modeloHistorial = new DefaultListModel<>();
        listaHistorial = new JList<>(modeloHistorial);
        listaHistorial.setBackground(new Color(25, 25, 25));
        listaHistorial.setForeground(Color.WHITE);

        listaHistorial.setCellRenderer(new DefaultListCellRenderer() {

            private static final long serialVersionUID = -6684167244292177832L;

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                PIF pif = (PIF) value;
                Blanco b = pif.getBlanco();

                String naturaleza = b != null ? b.getNaturaleza().toUpperCase() : "";
                String fecha = pif.getFechaHora().toString().replace('T', ' ');

                String texto = String.format(
                        "<html><div style='margin:2px 0; line-height:130%%;'>"
                                + "<span style='font-size:12px; color:white;'>%s</span><br>"
                                + "<span style='font-size:11px;'>%s</span>"
                                + "</div></html>",
                        fecha, naturaleza
                );
                label.setText(texto);

                Color colorTexto;

                if (naturaleza.contains("HOSTIL")) colorTexto = new Color(255, 128, 128);
                else if (naturaleza.contains("ASUMIDO ENEMIGO")) colorTexto = new Color(255, 128, 128);
                else if (naturaleza.contains("ALIADO")) colorTexto = new Color(128, 224, 255);
                else if (naturaleza.contains("ASUMIDO AMIGO")) colorTexto = new Color(128, 224, 255);
                else if (naturaleza.contains("NEUTRO")) colorTexto = new Color(170, 255, 170);
                else if (naturaleza.contains("DESCONOCIDO")) colorTexto = new Color(255, 255, 128);
                else if (naturaleza.contains("PENDIENTE")) colorTexto = new Color(255, 255, 128);
                else colorTexto = Color.LIGHT_GRAY;

                label.setForeground(colorTexto);
                label.setBackground(isSelected ? new Color(40, 80, 120) : new Color(20, 20, 20));
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(80, 80, 80)),
                        BorderFactory.createEmptyBorder(4, 6, 4, 6)
                ));

                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(listaHistorial);
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnVer = new JButton("VER PIF SELECCIONADO");
        btnVer.setBackground(new Color(60, 60, 60));
        btnVer.setForeground(Color.WHITE);
        btnVer.setPreferredSize(new Dimension(0, 50));
        btnVer.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Uso de la DialogFactory para mostrar el detalle
        btnVer.addActionListener(e -> {
            PIF p = listaHistorial.getSelectedValue();
            if (p != null && dialogFactory != null) {
                dialogFactory.DetallePIFDialog(p);
            } else if (dialogFactory == null) {
                JOptionPane.showMessageDialog(this, "Error de Sistema: Factory no inicializada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel cont = new JPanel(new BorderLayout());
        cont.setBackground(Color.BLACK);
        cont.add(btnVer, BorderLayout.NORTH);

        panel.add(cont, BorderLayout.SOUTH);

        add(panel, BorderLayout.EAST);
    }

    private void inicializarAccionesMT() {

    	btnDatos.addActionListener(e -> {
    	    indiceActual = 0;
    	    cardLayout.show(pifCardPanel, "datos");
    	    actualizarBotones();
    	});

    	btnMetodo.addActionListener(e -> {
    	    indiceActual = 1;
    	    cardLayout.show(pifCardPanel, "metodoTiro");
    	    actualizarBotones();
    	});

        metodoYTiroPanel.addPropertyChangeListener("ENVIAR_PIF",
                evt1 -> registrarNuevoPIF());
        
        metodoYTiroPanel.addPropertyChangeListener("ENVIAR_FUEGO", 
        		evt2 -> {
			        if (comunicacionIP != null) {
			            String msg = "FUEGO";
			            comunicacionIP.enviarATodos(msg);
			            PanelMensajeria.getConsolaMensajes().mostrarTx(msg);
			        }
		       });
        
        metodoYTiroPanel.getCorreccionesPanel().addPropertyChangeListener("ENVIAR_FUEGO", 
        		evt3 -> {
			        if (comunicacionIP != null) {
			            String msg = "FUEGO";
			            comunicacionIP.enviarATodos(msg);
			            PanelMensajeria.getConsolaMensajes().mostrarTx(msg);
			        }
		       });
    }

    private void inicializarAccionesCorrecciones() {

        CorreccionesPanel corr = metodoYTiroPanel.getCorreccionesPanel();

        corr.getBtnVolver().addActionListener(e -> {
                metodoYTiroPanel.mostrarPanelPrincipal();
                metodoYTiroPanel.getPanelMisionDeFuego().setVisible(true);
        });

        corr.getBtnNuevoPIF().addActionListener(e -> {
        	PanelMensajeria.getConsolaMensajes().agregarMensaje("[INFO] Nuevo PIF solicitado");
            volverASituacionTactica();
            metodoYTiroPanel.mostrarPanelPrincipal();
            metodoYTiroPanel.getPanelMisionDeFuego().setVisible(true);
        });

        // Uso de la DialogFactory para el reporte de Fin de Misión
        corr.getBtnFin().addActionListener(e -> { 
            if (dialogFactory != null) {
                dialogFactory.ReporteFinMisionDialog(reporte -> {
                    if (reporte == null) {
                        PanelMensajeria.getConsolaMensajes().agregarMensaje("[CANCELADO] Fin de misión sin reporte.");
                        return;
                    }

                    // Asociar al último PIF creado
                    PIF pif = modeloHistorial.isEmpty() ? null : modeloHistorial.lastElement();
                    if (pif != null) {
                        pif.setReporteFin(reporte);
                        generadorPDF.generarPDF(this, pif, reporte);
                    }

                    String msg = "FIN_MISION|BLANCO=" + 
                                  datosDeBlancoPanel.getBlancoActual().getNombre() +
                                 "|EFECTO=" + reporte.getEfectoObservado() +
                                 "|OBSERVACIONES=" + reporte.getObservaciones();

                    if (comunicacionIP != null) {
                        comunicacionIP.enviarATodos(msg);
                    }

                    metodoYTiroPanel.getCorreccionesPanel().reiniciarCuadricula();
                    metodoYTiroPanel.getCorreccionesPanel().getLblUltima().setText("");
                    
                    PanelMensajeria.getConsolaMensajes().mostrarTx(msg);

                    JOptionPane.showMessageDialog(
                            this,
                            "Fin de misión registrado.\nReporte guardado.",
                            "Confirmado",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                });
            } else {
                JOptionPane.showMessageDialog(this, "Error de Sistema: Factory no inicializada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        corr.getBtnEnviar().addActionListener(e -> enviarCorreccion());
    }
    
    private void enviarCorreccion() {

        CorreccionesPanel corr = metodoYTiroPanel.getCorreccionesPanel();

        String dir = (String) corr.getCbDireccion().getSelectedItem();
        String alc = (String) corr.getCbAlcance().getSelectedItem();
        String alt = (String) corr.getCbAltura().getSelectedItem();

        int vDir = Integer.parseInt(corr.getTxtDirValor().getText());
        int vAlc = Integer.parseInt(corr.getTxtAlcValor().getText());
        int vAlt = Integer.parseInt(corr.getTxtAltValor().getText());

        String nombre = datosDeBlancoPanel.getBlancoActual().getNombre();

        String msg = "CORRECCION"
                + "|BLANCO=" + nombre
                + "|DIR=" + dir + ":" + vDir
                + "|ALC=" + alc + ":" + vAlc
                + "|ALT=" + alt + ":" + vAlt;

        if (comunicacionIP != null) {
            comunicacionIP.enviarATodos(msg);
        }

        PanelMensajeria.getConsolaMensajes().mostrarTx(msg);
        
        corr.getLblUltima().setText("ULTIMA CORRECCIÓN: " + dir + " " + vDir + " / " + alc + " " + vAlc + " / " + alt + " " + vAlt);
    }

    private void volverASituacionTactica() {

        Container parent = getParent();
        while (parent != null && !(parent instanceof ProgramaTopografico))
            parent = parent.getParent();

        if (parent instanceof ProgramaTopografico topo)
            topo.mostrarPanel("SITUACION");
    }

    private void registrarNuevoPIF() {

        Blanco b = datosDeBlancoPanel.getBlancoActual();
        if (b == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un blanco.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MetodoAtaqueYTiroPanel mt = metodoYTiroPanel;
        
        PIF nuevo = new PIF(idOAA, LocalDateTime.now(), b, mt.getModoMision(),
        		mt.getRegistroSobre(), mt.getBarreraFrente(), mt.getBarreraInclinacion(),
        		mt.getEfectoDeseado(), mt.getModoFuego(), mt.isCercano(), 
        		mt.isGranAngulo(), mt.getGranada(), mt.getEspoleta(), mt.getVolumen(), mt.getHaz(),
        		mt.getPiezas(), mt.getSeccion(), mt.isFgoSi(), mt.isTesSi(), mt.orden());

        modeloHistorial.addElement(nuevo);
        
        if (comunicacionIP != null) {
            String msg = "PIF"
                    + "|MISION=" + nuevo.getModoMision()
                    + "|BLANCO=" + nuevo.getBlanco().getNombre()
                    + "|NAT=" + nuevo.getBlanco().getNaturaleza()  
                    + "|FASE REGLAJE="+mt.getFaseReglaje()
                    + "|REGSOBRE=" + nuevo.getRegistroSobre()
                    + "|BARRFRENTE=" + nuevo.getBarreraFrente()
                    + "|BARRINC=" + nuevo.getBarreraInclinacion()            
                    + "|EFECTO=" + nuevo.getEfectoDeseado()
                    + "|MODO DE FUEGO=" + nuevo.getModoFuego()
                    + "|CERCANO=" + (nuevo.isCercano() ? "SI" : "NO")
                    + "|GRANANGULO=" + (nuevo.isGranAngulo() ? "SI" : "NO")
                    + "|GRANADA=" + nuevo.getGranada()
                    + "|ESPOLETA=" + nuevo.getEspoleta()
                    + "|VOLUMEN=" + nuevo.getVolumen()
                    + "|HAZ=" + nuevo.getHaz()
                    + "|PIEZAS=" + nuevo.getPiezas()
                    + "|SECCION=" + nuevo.getSeccion()
                    + "|FGOCONT=" + (nuevo.isFgoCont() ? "SI" : "NO")
                    + "|TES=" + (nuevo.isTes() ? "SI" : "NO")
            		+ "|ORDEN=" + nuevo.getOrden();

            comunicacionIP.enviarATodos(msg);
            PanelMensajeria.getConsolaMensajes().mostrarTx(msg);
        }

        JOptionPane.showMessageDialog(this, "PIF registrado correctamente.", "Confirmado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void crearBotonesDeNavegacion() {

        JButton prev = new JButton("<");
        prev.setBackground(new Color(28, 122, 33));
        prev.setForeground(Color.WHITE);

        JButton next = new JButton(">");
        next.setBackground(new Color(28, 122, 33));
        next.setForeground(Color.WHITE);

        prev.addActionListener(e -> moverSeguro(-1));
        next.addActionListener(e -> moverSeguro(+1));

        JPanel navegacion = new JPanel(new BorderLayout());
        navegacion.setBackground(Color.BLACK);

        navegacion.add(prev, BorderLayout.WEST);
        navegacion.add(pifCardPanel, BorderLayout.CENTER);
        navegacion.add(next, BorderLayout.EAST);

        pifPanel.add(navegacion, BorderLayout.CENTER);
    }

    private void moverSeguro(int delta) {
        if (transicionEnCurso) return;

        transicionEnCurso = true;

        int nuevo = indiceActual + delta;
        
        if (nuevo >= 0 && nuevo < ordenNavegable.length) {
            indiceActual = nuevo;
            cardLayout.show(pifCardPanel, ordenNavegable[nuevo]);
            actualizarBotones();
        }

        Timer t = new Timer(160, e -> transicionEnCurso = false);
        t.setRepeats(false);
        t.start();
    }
 
    public MetodoAtaqueYTiroPanel getMetodoYTiroPanel() {
    	return metodoYTiroPanel;
    }
    
    public void setPanelMensajeria(Mensajeria m) {
    	PanelMensajeria = m;
    }

    public DatosBlanco getDatosDeBlancoPanel() {
        return datosDeBlancoPanel;
    }

    public LinkedList<Blanco> getListaDeBlancos() {
        return listaDeBlancos;
    }
}