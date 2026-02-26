package app;
	
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.*;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import comunicaciones.ConsolaMensajes;
import comunicaciones.GestorEnlaceOperativo;
import dominio.Blanco;
import dominio.PIF;
import dominio.ReporteFinMision;
import interfaz.DatosBlanco;
import interfaz.MetodoAtaqueYTiroPanel;
import interfaz.CorreccionesPanel;
import interfaz.PanelMapa;
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
    private ConsolaMensajes consolaMensajes;

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

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        pifPanel = new JPanel(new BorderLayout());
        pifPanel.setBackground(Color.BLACK);
        pifPanel.setBorder(FabricaComponentes.crearBordeTitulo(""));

        crearPanelDeBotones();
        crearCardLayout();
        crearBotonesDeNavegacion();
        crearPanelHistorial();

        inicializarAcciones();
        inicializarAccionesCorrecciones();

        add(pifPanel, BorderLayout.CENTER);

        actualizarBotones();
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
            indiceActual = 0; // índice de DATOS BLANCO
            cardLayout.show(pifCardPanel, "datos");
            actualizarBotones();
        });
    }

    public void mostrarMetodoYAtaque() {
        SwingUtilities.invokeLater(() -> {
            indiceActual = 1; // índice de METODO / ATAQUE
            cardLayout.show(pifCardPanel, "metodoTiro");
            actualizarBotones();
        });
    }

    private void crearCardLayout() {

        cardLayout = new CardLayout();
        pifCardPanel = new JPanel(cardLayout);
        pifCardPanel.setBackground(Color.BLACK);

        // PRIMERO crear el panel que contendrá el mapa
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
        btnVer.addActionListener(e -> mostrarPIFSeleccionado());

        JPanel cont = new JPanel(new BorderLayout());
        cont.setBackground(Color.BLACK);
        cont.add(btnVer, BorderLayout.NORTH);

        consolaMensajes = new ConsolaMensajes();
        cont.add(consolaMensajes, BorderLayout.SOUTH);

        panel.add(cont, BorderLayout.SOUTH);

        add(panel, BorderLayout.EAST);
    }

    private void inicializarAcciones() {

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
			            consolaMensajes.mostrarTx(msg);
			        }
		       });
        
        metodoYTiroPanel.getCorreccionesPanel().addPropertyChangeListener("ENVIAR_FUEGO", 
        		evt3 -> {
			        if (comunicacionIP != null) {
			            String msg = "FUEGO";
			            comunicacionIP.enviarATodos(msg);
			            consolaMensajes.mostrarTx(msg);
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
            consolaMensajes.agregarMensaje("[INFO] Nuevo PIF solicitado");
            volverASituacionTactica();
            metodoYTiroPanel.mostrarPanelPrincipal();
            metodoYTiroPanel.getPanelMisionDeFuego().setVisible(true);
        });

        corr.getBtnFin().addActionListener(e -> { 

            ReporteFinMision reporte = mostrarDialogoFinMision();

            if (reporte == null) {
                consolaMensajes.agregarMensaje("[CANCELADO] Fin de misión sin reporte.");
                return;
            }

            // Asociar al último PIF creado
            PIF pif = modeloHistorial.lastElement();
            pif.setReporteFin(reporte);
            
            generarPDFFinMision(pif, reporte);

            // Mensaje de red opcional
            String msg = "FIN_MISION|BLANCO=" + 
                          datosDeBlancoPanel.getBlancoActual().getNombre() +
                         "|EFECTO=" + reporte.getEfectoObservado() +
                         "|OBSERVACIONES=" + reporte.getObservaciones();

            if (comunicacionIP != null) {
                comunicacionIP.enviarATodos(msg);
            }

            metodoYTiroPanel.getCorreccionesPanel().reiniciarCuadricula();
            metodoYTiroPanel.getCorreccionesPanel().getLblUltima().setText("");
            
            consolaMensajes.mostrarTx(msg);

            JOptionPane.showMessageDialog(
                    this,
                    "Fin de misión registrado.\nReporte guardado.",
                    "Confirmado",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        
        corr.getBtnEnviar().addActionListener(e -> enviarCorreccion());
    }
    
    private void generarPDFFinMision(PIF pif, ReporteFinMision rep) {
        try {

        	CorreccionesPanel corr = metodoYTiroPanel.getCorreccionesPanel();
        	Map<String, String> historial = corr.getHistorialCorrecciones();
        	
        	String desktop = System.getProperty("user.home") + File.separator + "Desktop";
            String nombreArchivo = desktop + File.separator + "FinMision_" + pif.getId() + ".pdf";

            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(nombreArchivo));
            
            doc.open();

            Paragraph titulo = new Paragraph("REPORTE DE FIN DE MISIÓN");

            titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);

            doc.add(titulo);

            doc.add(new Paragraph("DATOS DEL PIF"));
            doc.add(Chunk.NEWLINE);

            String datosPifTxt = pif.mostrarDatosDePIF();
            if (datosPifTxt == null) datosPifTxt = "(Sin datos disponibles)";

            doc.add(new Paragraph(datosPifTxt));
            doc.add(Chunk.NEWLINE);


            doc.add(new Paragraph("REPORTE DE FIN DE MISIÓN"));
            doc.add(com.itextpdf.text.Chunk.NEWLINE);

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);

            agregarFilaPDF("Efecto Observado", rep.getEfectoObservado(), tabla);
            agregarFilaPDF("Dispersión", rep.getDispersion(), tabla);
            agregarFilaPDF("Daños Observados", rep.getDanos(), tabla);
            agregarFilaPDF("Movimiento", rep.getMovimiento(), tabla);
            agregarFilaPDF("Observaciones", rep.getObservaciones(), tabla);

            doc.add(tabla);
            
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph("HISTORIAL DE DISPAROS Y CORRECCIONES"));
            doc.add(Chunk.NEWLINE);

            if (historial.isEmpty()) {
                doc.add(new Paragraph("(No se registraron correcciones)"));
            } else {

                PdfPTable tablaHist = new PdfPTable(2);
                tablaHist.setWidthPercentage(100);
                tablaHist.setWidths(new float[]{1, 4});

                PdfPCell h1 = new PdfPCell(new Phrase("Disparo"));
                PdfPCell h2 = new PdfPCell(new Phrase("Detalle"));

                h1.setBackgroundColor(BaseColor.GRAY);
                h2.setBackgroundColor(BaseColor.GRAY);

                tablaHist.addCell(h1);
                tablaHist.addCell(h2);

                historial.forEach((id, texto) -> {
                    tablaHist.addCell(new PdfPCell(new Phrase(id)));
                    tablaHist.addCell(new PdfPCell(new Phrase(texto)));
                });

                doc.add(tablaHist);
            }
            
            doc.close();

            JOptionPane.showMessageDialog(
                    this,
                    "PDF generado:\n" + nombreArchivo,
                    "PDF Fin de Misión",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error generando PDF:\n" + ex.getMessage(),
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void agregarFilaPDF(String titulo, String valor,PdfPTable tabla) {

		PdfPCell c1 =
		new PdfPCell(new Phrase(titulo));
		
		PdfPCell c2 =
		new PdfPCell(new Phrase(valor));
		
		c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
		c1.setPadding(6);
		c2.setPadding(6);
		
		tabla.addCell(c1);
		tabla.addCell(c2);
	}
    
    private ReporteFinMision mostrarDialogoFinMision() {

        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Reporte de Fin de Misión",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dlg.setSize(750, 800); 
        dlg.setLocationRelativeTo(this);

        Font fTitulo = new Font("Segoe UI", Font.BOLD, 28);
        Font fLabel = new Font("Segoe UI", Font.BOLD, 20);
        Font fInput = new Font("Segoe UI", Font.PLAIN, 22);
        Color colorTextoLabel = new Color(160, 255, 160);
        int alturaComponentes = 60; 
        // Panel principal
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(20, 20, 20));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 70), 3), 
                BorderFactory.createEmptyBorder(30, 30, 30, 30) 
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        
        JComboBox<String> cbEfecto = FabricaComponentes.crearCombo(new String[]{"Neutralizado", "Suprimido", "Destruido"});
        cbEfecto.setFont(fInput);
        cbEfecto.setPreferredSize(new Dimension(0, alturaComponentes));

        JComboBox<String> cbDispersion = FabricaComponentes.crearCombo(new String[]{
                "Ajustado", "Dispersión baja", "Dispersión alta",
                "Corto", "Largo", "Izquierda", "Derecha"
        });
        cbDispersion.setFont(fInput);
        cbDispersion.setPreferredSize(new Dimension(0, alturaComponentes));

        JComboBox<String> cbDanos = FabricaComponentes.crearCombo(new String[]{"Destrucción parcial", "Destrucción total"});
        cbDanos.setFont(fInput);
        cbDanos.setPreferredSize(new Dimension(0, alturaComponentes));

        JTextField txtMovimiento = new JTextField();
        FabricaComponentes.configurarCampo(txtMovimiento);
        txtMovimiento.setFont(fInput);
        txtMovimiento.setPreferredSize(new Dimension(0, alturaComponentes));

        JTextArea txtObs = new JTextArea(5, 20);
        FabricaComponentes.configurarArea(txtObs);
        txtObs.setFont(fInput);
        JScrollPane scrollObs = new JScrollPane(txtObs);
        scrollObs.setBorder(txtObs.getBorder()); 
        scrollObs.setPreferredSize(new Dimension(0, 150)); 
        int y = 0;

        JLabel tituloSec = new JLabel("INFORME FINAL");
        tituloSec.setForeground(new Color(160, 255, 160));
        tituloSec.setFont(fTitulo);
        tituloSec.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 30, 10); 
        p.add(tituloSec, gbc);

        agregarFilaGigante(p, "Efecto Observado:", cbEfecto, gbc, y++, fLabel, colorTextoLabel);
        agregarFilaGigante(p, "Dispersión:", cbDispersion, gbc, y++, fLabel, colorTextoLabel);
        agregarFilaGigante(p, "Daños Observados:", cbDanos, gbc, y++, fLabel, colorTextoLabel);
        agregarFilaGigante(p, "Movimiento del blanco:", txtMovimiento, gbc, y++, fLabel, colorTextoLabel);

        JLabel lblObs = new JLabel("Observaciones:");
        lblObs.setFont(fLabel);
        lblObs.setForeground(colorTextoLabel);
        
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 5, 10); 
        p.add(lblObs, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(5, 10, 20, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0; 
        p.add(scrollObs, gbc);

        JButton ok = new JButton("CONFIRMAR");
        JButton cancel = new JButton("CANCELAR");

        Font fBoton = new Font("Segoe UI", Font.BOLD, 18);
        Dimension dimBoton = new Dimension(220, 65); 
        
        ok.setFont(fBoton);
        ok.setPreferredSize(dimBoton);
        ok.setBackground(new Color(40, 160, 40));
        ok.setForeground(Color.WHITE);
        ok.setFocusPainted(false);
        ok.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 220, 90), 2),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));

        cancel.setFont(fBoton);
        cancel.setPreferredSize(dimBoton);
        cancel.setBackground(new Color(140, 40, 40));
        cancel.setForeground(Color.WHITE);
        cancel.setFocusPainted(false);
        cancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 90, 90), 2),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));

        ok.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { ok.setBackground(new Color(55, 190, 55)); }
            public void mouseExited(MouseEvent evt) { ok.setBackground(new Color(40, 160, 40)); }
        });

        cancel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { cancel.setBackground(new Color(190, 50, 50)); }
            public void mouseExited(MouseEvent evt) { cancel.setBackground(new Color(140, 40, 40)); }
        });

        JPanel pb = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20)); // Más espacio entre botones
        pb.setBackground(new Color(20, 20, 20));
        pb.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        pb.add(ok);
        pb.add(cancel);

        final boolean[] confirmado = {false};
        ok.addActionListener(e -> { confirmado[0] = true; dlg.dispose(); });
        cancel.addActionListener(e -> dlg.dispose());

        dlg.add(p, BorderLayout.CENTER);
        dlg.add(pb, BorderLayout.SOUTH);
        dlg.setVisible(true);

        if (!confirmado[0]) return null;

        return new ReporteFinMision(
                cbEfecto.getSelectedItem().toString(),
                cbDispersion.getSelectedItem().toString(),
                cbDanos.getSelectedItem().toString(),
                txtMovimiento.getText(),
                txtObs.getText()
        );
    }

    private void agregarFilaGigante(JPanel p, String titulo, JComponent comp, GridBagConstraints gbc, int gridy, Font fLabel, Color color) {
        gbc.gridy = gridy;
        
        // Label
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(fLabel);
        lbl.setForeground(color);
        
        gbc.gridx = 0; 
        gbc.gridwidth = 1; 
        gbc.weightx = 0.3; // El label ocupa el 30% del ancho
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 10, 15, 20); // Separación derecha
        p.add(lbl, gbc);

        // Componente
        gbc.gridx = 1; 
        gbc.weightx = 0.7; // El componente ocupa el 70%
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 10);
        p.add(comp, gbc);
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

        consolaMensajes.mostrarTx(msg);
        
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
            consolaMensajes.mostrarTx(msg);
        }

        JOptionPane.showMessageDialog(this, "PIF registrado correctamente.", "Confirmado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarPIFSeleccionado() {
        PIF p = listaHistorial.getSelectedValue();
        if (p == null) return;

        Blanco b = p.getBlanco();

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.BLACK);
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font fTitulo = new Font("Arial", Font.BOLD, 18);
        Font fTexto = new Font("Consolas", Font.PLAIN, 15);

        JPanel panelBlanco = new JPanel();
        panelBlanco.setLayout(new BoxLayout(panelBlanco, BoxLayout.Y_AXIS));
        panelBlanco.setBackground(Color.BLACK);
        panelBlanco.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "DATOS DEL BLANCO",
                        0, 0, fTitulo, Color.WHITE
                )
        );

        panelBlanco.add(FabricaComponentes.crearLinea("Nombre: ", b.getNombre(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea("Naturaleza: ", b.getNaturaleza(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea("Coordenadas: ", b.getCoordenadas().toString(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea("Fecha/Hora PIF: ", p.getFechaHora().toString(), fTexto)); 
        panelBlanco.add(FabricaComponentes.crearLinea("Situación: ", String.valueOf(b.getSituacionMovimiento()), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea("Orientación: ", b.getOrientacion() + "°", fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea("Info adicional: ", b.getInformacionAdicional(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea("PIF ID: ", p.getId(), fTexto));

        contenido.add(panelBlanco);
        contenido.add(Box.createVerticalStrut(15));

        JPanel panelMetodo = new JPanel();
        panelMetodo.setLayout(new BoxLayout(panelMetodo, BoxLayout.Y_AXIS));
        panelMetodo.setBackground(Color.BLACK);
        panelMetodo.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "MÉTODO DE ATAQUE / TIRO Y CONTROL",
                        0, 0, fTitulo, Color.WHITE
                )
        );
        
        panelMetodo.add(FabricaComponentes.crearLinea("--- Misión y Ajustes ---", "", fTitulo));
        panelMetodo.add(FabricaComponentes.crearLinea("Misión (Modo): ", p.getModoMision(), fTexto)); 
        panelMetodo.add(FabricaComponentes.crearLinea("Reg. Sobre: ", p.getRegistroSobre(), fTexto));
        panelMetodo.add(FabricaComponentes.crearLinea("Barrera Frente: ", p.getBarreraFrente(), fTexto));
        panelMetodo.add(FabricaComponentes.crearLinea("Barrera Incl: ", p.getBarreraInclinacion(), fTexto));
        panelMetodo.add(Box.createVerticalStrut(8));
        		
        panelMetodo.add(FabricaComponentes.crearLinea("--- Método de Ataque ---", "", fTitulo));
        panelMetodo.add(FabricaComponentes.crearLinea("Efecto Deseado: ", p.getEfectoDeseado(), fTexto));
        panelMetodo.add(FabricaComponentes.crearLinea("Granada: ", p.getGranada(), fTexto)); 
        panelMetodo.add(FabricaComponentes.crearLinea("Espoleta: ", p.getEspoleta(), fTexto));
        panelMetodo.add(FabricaComponentes.crearLinea("Volumen: ", p.getVolumen(), fTexto));
        panelMetodo.add(FabricaComponentes.crearLinea("Haz: ", p.getHaz(), fTexto));
        panelMetodo.add(FabricaComponentes.crearLinea("Cercano: ", p.isCercano() ? "Sí" : "No", fTexto));
        panelMetodo.add(FabricaComponentes.crearLinea("Gran Ángulo: ", p.isGranAngulo() ? "Sí" : "No", fTexto));
        panelMetodo.add(Box.createVerticalStrut(8));

        panelMetodo.add(FabricaComponentes.crearLinea("--- Tiro y Control ---", "", fTitulo));
        panelMetodo.add(FabricaComponentes.crearLinea("Piezas: ", p.getPiezas(), fTexto));
        
        panelMetodo.add(FabricaComponentes.crearLinea("Rondas (Vol.): ", p.getVolumen(), fTexto)); 
        
        panelMetodo.add(FabricaComponentes.crearLinea("Sección: ", p.getSeccion(), fTexto));

        int piezas = 0; 
        try {
            piezas = Integer.parseInt(p.getPiezas().trim());
        } catch (NumberFormatException ignored) {
        }
        
        panelMetodo.add(FabricaComponentes.crearLinea("Modo Disparo: ", (piezas > 1 ? "RÁFAGA" : "DISPAROS"), fTexto));
        
        panelMetodo.add(FabricaComponentes.crearLinea("Modo Fuego: ", p.getModoFuego(), fTexto)); 
        
        panelMetodo.add(FabricaComponentes.crearLinea("FGO continuo: ", p.isFgoCont() ? "Sí" : "No", fTexto));
        
        panelMetodo.add(FabricaComponentes.crearLinea("TES: ", p.isTes() ? "Sí" : "No", fTexto));

        contenido.add(panelMetodo);

        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalle del PIF: " + p.getId());
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(contenido);

        JScrollBar barraVertical = scrollPane.getVerticalScrollBar();
        barraVertical.setPreferredSize(new Dimension(30, barraVertical.getPreferredSize().height));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        dialogo.getContentPane().add(scrollPane);
        dialogo.setSize(600, 800); 
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
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

    public ConsolaMensajes getConsolaMensajes() {
        return consolaMensajes;
    }

    public DatosBlanco getDatosDeBlancoPanel() {
        return datosDeBlancoPanel;
    }

    public LinkedList<Blanco> getListaDeBlancos() {
        return listaDeBlancos;
    }
}
