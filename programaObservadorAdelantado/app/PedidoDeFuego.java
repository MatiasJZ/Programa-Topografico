package app;
	
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import comunicaciones.ComunicacionIP;
import dominio.Blanco;
import dominio.PIF;
import dominio.ReporteFinMision;
import interfaz.DatosBlanco;
import interfaz.MetodoAtaqueYTiroPanel;
import interfaz.CorreccionesPanel;
import interfaz.PanelMapa;
	
class PedidoDeFuego extends JPanel {

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

    private ComunicacionIP comunicacionIP;

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
        pifPanel.setBorder(crearBordeTitulo(""));

        crearPanelDeBotones();
        crearCardLayout();
        crearBotonesDeNavegacion();
        crearPanelHistorial();

        inicializarAcciones();
        inicializarAccionesCorrecciones();

        add(pifPanel, BorderLayout.CENTER);

        actualizarBotones();
    }

    public void setComunicacionIP(ComunicacionIP com) {
        this.comunicacionIP = com;
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
        panel.setBorder(crearBordeTitulo("HISTORIAL DE PIFs"));
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

        JButton btnVer = new JButton("Ver PIF");
        btnVer.setBackground(new Color(60, 60, 60));
        btnVer.setForeground(Color.WHITE);
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
                evt1z -> registrarNuevoPIF());
        
        metodoYTiroPanel.addPropertyChangeListener("ENVIAR_FUEGO", 
        		evt2 -> {
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

            // Mostrar diálogo para cargar el reporte
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
            agregarFilaPDF("Recomendación", rep.getRecomendacion(), tabla);
            agregarFilaPDF("Observaciones", rep.getObservaciones(), tabla);

            doc.add(tabla);
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
    
    private void agregarFilaPDF(String titulo, String valor,com.itextpdf.text.pdf.PdfPTable tabla) {

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

        dlg.setSize(500, 520);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.BLACK);
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;

        JComboBox<String> cbEfecto = crearCombo(new String[]{
                "Sin efecto", "Efecto ligero", "Efecto moderado",
                "Efecto intenso", "Neutralizado", "Suprimido", "Destruido"
        });

        JComboBox<String> cbDispersion = crearCombo(new String[]{
                "Ajustado", "Dispersión baja", "Dispersión alta",
                "Corto", "Largo", "Izquierda", "Derecha"
        });

        JComboBox<String> cbDanos = crearCombo(new String[]{
                "Sin daño", "Daño ligero", "Daño moderado",
                "Daño severo", "Destrucción parcial", "Destrucción total"
        });

        JTextField txtMovimiento = new JTextField();
        configurarCampo(txtMovimiento);

        JComboBox<String> cbRecomendacion = crearCombo(new String[]{
                "Cese fuego", "Continuar fuego", "Cambiar munición",
                "Nuevo PIF recomendado"
        });

        JTextArea txtObs = new JTextArea(4,20);
        configurarArea(txtObs);

        // --- Agregar filas ---
        agregarFila(p, "Efecto Observado:", cbEfecto, gbc, y++);
        agregarFila(p, "Dispersión:", cbDispersion, gbc, y++);
        agregarFila(p, "Daños Observados:", cbDanos, gbc, y++);
        agregarFila(p, "Movimiento del blanco:", txtMovimiento, gbc, y++);
        agregarFila(p, "Recomendación:", cbRecomendacion, gbc, y++);
        agregarArea(p, "Observaciones:", txtObs, gbc, y++);

        // --- Botones ---
        JButton ok = new JButton("Confirmar");
        JButton cancel = new JButton("Cancelar");
        ok.setBackground(new Color(0,140,0));
        ok.setForeground(Color.WHITE);
        cancel.setBackground(new Color(140,0,0));
        cancel.setForeground(Color.WHITE);

        JPanel pb = new JPanel();
        pb.setBackground(Color.BLACK);
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
                cbRecomendacion.getSelectedItem().toString(),
                txtObs.getText()
        );
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

        corr.getLblUltima().setText("ULTIMA CORRECCIÓN: " + dir + " " + vDir
                + " / " + alc + " " + vAlc + " / " + alt + " " + vAlt);
    }

    private void volverASituacionTactica() {

        Container parent = getParent();
        while (parent != null && !(parent instanceof ObservadorAdelantado))
            parent = parent.getParent();

        if (parent instanceof ObservadorAdelantado obs)
            obs.mostrarPanel("SITUACION");
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
        		mt.getPiezas(), mt.getSeccion(), mt.isFgoSi(), mt.isTesSi());

        modeloHistorial.addElement(nuevo);
        
        if (comunicacionIP != null) {
            String msg = "PIF"
                    + "|BLANCO=" + b.getNombre()
                    + "|NAT=" + b.getNaturaleza()    
                 
                    + "|MISION=" + nuevo.getModoMision()
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
                    + "|FGOCONT=" + nuevo.isFgoCont()
                    + "|FGOCONT=" + nuevo.isTes();

            comunicacionIP.enviarATodos(msg);
            consolaMensajes.mostrarTx(msg);
        }

        JOptionPane.showMessageDialog(this, "PIF registrado correctamente.", "Confirmado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarPIFSeleccionado() {
        PIF p = listaHistorial.getSelectedValue();
        if (p == null) return;

        // NOTA: Se asume que 'Blanco' tiene los getters requeridos.
        Blanco b = p.getBlanco();

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.BLACK);
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font fTitulo = new Font("Arial", Font.BOLD, 18);
        Font fTexto = new Font("Consolas", Font.PLAIN, 15);

        // --- PANEL BLANCO (Datos del Blanco) ---
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

        panelBlanco.add(crearLinea("Nombre: ", b.getNombre(), fTexto));
        panelBlanco.add(crearLinea("Naturaleza: ", b.getNaturaleza(), fTexto));
        panelBlanco.add(crearLinea("Coordenadas: ", b.getCoordenadas().toString(), fTexto));
        panelBlanco.add(crearLinea("Fecha/Hora PIF: ", p.getFechaHora().toString(), fTexto)); // Uso getFechaHora() de PIF
        panelBlanco.add(crearLinea("Situación: ", String.valueOf(b.getSituacionMovimiento()), fTexto));
        panelBlanco.add(crearLinea("Orientación: ", b.getOrientacion() + "°", fTexto));
        panelBlanco.add(crearLinea("Info adicional: ", b.getInformacionAdicional(), fTexto));
        panelBlanco.add(crearLinea("PIF ID: ", p.getId(), fTexto)); // Uso getId() de PIF

        contenido.add(panelBlanco);
        contenido.add(Box.createVerticalStrut(15));

        // --- PANEL MÉTODO / TIRO Y CONTROL (Configuración de Fuego) ---
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
        
        // --- INFORMACIÓN DE MISIÓN ---
        panelMetodo.add(crearLinea("--- Misión y Ajustes ---", "", fTitulo));
        panelMetodo.add(crearLinea("Misión (Modo): ", p.getModoMision(), fTexto)); 
        panelMetodo.add(crearLinea("Reg. Sobre: ", p.getRegistroSobre(), fTexto));
        panelMetodo.add(crearLinea("Barrera Frente: ", p.getBarreraFrente(), fTexto));
        panelMetodo.add(crearLinea("Barrera Incl: ", p.getBarreraInclinacion(), fTexto));
        panelMetodo.add(Box.createVerticalStrut(8));
        
        // --- MÉTODO DE ATAQUE DETALLADO ---
        panelMetodo.add(crearLinea("--- Método de Ataque ---", "", fTitulo));
        panelMetodo.add(crearLinea("Efecto Deseado: ", p.getEfectoDeseado(), fTexto));
        panelMetodo.add(crearLinea("Granada: ", p.getGranada(), fTexto)); 
        panelMetodo.add(crearLinea("Espoleta: ", p.getEspoleta(), fTexto));
        panelMetodo.add(crearLinea("Volumen: ", p.getVolumen(), fTexto));
        panelMetodo.add(crearLinea("Haz: ", p.getHaz(), fTexto));
        panelMetodo.add(crearLinea("Cercano: ", p.isCercano() ? "Sí" : "No", fTexto));
        panelMetodo.add(crearLinea("Gran Ángulo: ", p.isGranAngulo() ? "Sí" : "No", fTexto));
        panelMetodo.add(Box.createVerticalStrut(8));

        // --- TIRO Y CONTROL ---
        panelMetodo.add(crearLinea("--- Tiro y Control ---", "", fTitulo));
        panelMetodo.add(crearLinea("Piezas: ", p.getPiezas(), fTexto));
        
        // *** AJUSTE NECESARIO: getRondas() NO EXISTE. Usamos getVolumen() como valor proxy/texto. ***
        panelMetodo.add(crearLinea("Rondas (Vol.): ", p.getVolumen(), fTexto)); 
        
        panelMetodo.add(crearLinea("Sección: ", p.getSeccion(), fTexto));
        
        // *** AJUSTE NECESARIO: La lógica de Disparo/Ráfaga debe basarse en una variable existente (getVolumen o getPiezas).
        // Asumo que 'getPiezas()' o 'getVolumen()' tienen un formato numérico o de texto que implica el modo.
        // Si getPiezas() es "1", es Disparos. Si es "2", "3", etc., es Ráfaga.
        int piezas = 0; 
        try {
            piezas = Integer.parseInt(p.getPiezas().trim());
        } catch (NumberFormatException ignored) {
            // Si no es un número, asumimos Disparos
        }
        
        panelMetodo.add(crearLinea("Modo Disparo: ", (piezas > 1 ? "RÁFAGA" : "DISPAROS"), fTexto));
        
        panelMetodo.add(crearLinea("Modo Fuego: ", p.getModoFuego(), fTexto)); 
        
        // *** CORRECCIÓN: Se usa isFgoCont() en lugar de isFuegoContinuo() ***
        panelMetodo.add(crearLinea("FGO continuo: ", p.isFgoCont() ? "Sí" : "No", fTexto));
        panelMetodo.add(crearLinea("TES: ", p.isTes() ? "Sí" : "No", fTexto));


        contenido.add(panelMetodo);

        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalle del PIF: " + p.getId());
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.getContentPane().add(new JScrollPane(contenido));
        dialogo.setSize(600, 800); 
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private void configurarCampo(JTextField t){
        t.setBackground(new Color(60,60,60));
        t.setForeground(Color.WHITE);
    }

    private JComboBox<String> crearCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setBackground(new Color(60,60,60));
        c.setForeground(Color.WHITE);
        c.setFont(new Font("Arial", Font.PLAIN, 14));

        // Centrar texto en elementos
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        c.setRenderer(renderer);

        return c;
    }
    
    private void agregarFila(JPanel panel, String etiqueta, JComponent componente,GridBagConstraints gbc, int y) {
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.weightx = 0.3;
		panel.add(crearLabel(etiqueta), gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 0.7;
		panel.add(componente, gbc);
	}
    
    private void configurarArea(JTextArea a){
        a.setForeground(Color.WHITE);
        a.setBackground(new Color(60,60,60));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
    }

    private void agregarFila(JPanel panel, String etiqueta, JTextField campo,
                             GridBagConstraints gbc, int y){

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearLabel(etiqueta), gbc);

        gbc.gridx = 1; gbc.gridy = y;
        panel.add(campo, gbc);
    }

    private void agregarArea(JPanel panel, String etiqueta, JTextArea area,
                             GridBagConstraints gbc, int y){

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearLabel(etiqueta), gbc);

        gbc.gridx = 1; gbc.gridy = y;
        panel.add(new JScrollPane(area), gbc);
    }

    private JLabel crearLabel(String t){
        JLabel l = new JLabel(t);
        l.setForeground(Color.WHITE);
        return l;
    }
    
    private JPanel crearLinea(String etiqueta, String valor, Font font) {
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linea.setBackground(Color.BLACK);

        JLabel l1 = new JLabel(etiqueta);
        l1.setForeground(Color.WHITE);
        l1.setFont(font);

        JLabel l2 = new JLabel(valor);
        l2.setForeground(new Color(180, 255, 180));
        l2.setFont(font);

        linea.add(l1);
        linea.add(l2);

        return linea;
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

    private TitledBorder crearBordeTitulo(String titulo) {
        TitledBorder b = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                titulo,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 20),
                Color.WHITE
        );
        return b;
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
