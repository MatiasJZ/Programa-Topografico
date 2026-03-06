package dominio;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import app.PedidoDeFuego;
import interfaces.PDFGenerator;
import panelesSecundarios.CorreccionesPanel;

public class GeneradorPDF implements PDFGenerator{

	private final Component padre;
	private int PDFnum;
	
	public GeneradorPDF(Component padre) {
		this.padre = padre;
		this.PDFnum = 1;
	}
	
	@Override
	public void generarPDF() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        FileDialog fd = new FileDialog(parentFrame, "GUARDAR REGISTRO TOPOGRÁFICO", FileDialog.SAVE);
        fd.setFile("Reporte_Topografico_" +PDFnum+".pdf");
        fd.setVisible(true);

        if (fd.getFile() != null) {
            String rutaCompleta = fd.getDirectory() + fd.getFile();
            if (!rutaCompleta.toLowerCase().endsWith(".pdf")) rutaCompleta += ".pdf";

            com.itextpdf.text.Font fuenteCabeceraTabla = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
            com.itextpdf.text.Font fuenteTexto = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);

            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, new FileOutputStream(rutaCompleta));
                documento.open();

                // Encabezado
                Paragraph titulo = new Paragraph("BATALLÓN DE ARTILLERÍA DE CAMPAÑA N°1\nSECCIÓN ADQUISICIÓN DE BLANCOS");
                titulo.setAlignment(Element.ALIGN_CENTER);
                documento.add(titulo);
                
                documento.add(new Paragraph("\nFECHA DE OPERACIÓN: " + LocalDateTime.now().toString()));
                documento.add(new Paragraph("------------------------------------------------------------------------------------------"));

                Map<String, String> datos = RegistroCalculos.getBitacora();

                String[] funcionesPDF = {
                    "TRIANGULACIÓN", "RADIACIÓN", "TRILATERACIÓN", 
                    "INTERSECCIÓN INVERSA 3P", "INTERSECCIÓN INVERSA 2P", 
                    "INTERSECCIÓN DIRECTA", "POLIGONAL", "MESA PLOTTING", 
                    "ÁNGULO BASE", "ACTUALIZACIÓN MAGNÉTICA", 
                    "REGISTRO COORD. MODIFICADAS", "NIVELACIÓN TRIGONOMÉTRICA",
                    "MEDICIÓN AYD", "CIERRE DE POLIGONAL"
                };

                for (String funcion : funcionesPDF) {
                    if (datos.containsKey(funcion)) {
                        PdfPTable tabla = new PdfPTable(1);
                        tabla.setWidthPercentage(100);
                        tabla.setSpacingBefore(10f);

                        PdfPCell celdaTitulo = new PdfPCell(new Phrase(funcion, fuenteCabeceraTabla));
                        celdaTitulo.setBackgroundColor(new BaseColor(192, 192, 192));
                        celdaTitulo.setPadding(5);
                        tabla.addCell(celdaTitulo);
                        
                        PdfPCell celdaContenido = new PdfPCell(new Phrase(datos.get(funcion), fuenteTexto));
                        celdaContenido.setPadding(10);
                        tabla.addCell(celdaContenido);

                        documento.add(tabla);
                    }
                }

                documento.close();
                JOptionPane.showMessageDialog(padre, "Informe PDF generado con éxito.");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(padre, "Error al generar PDF: " + ex.getMessage());
            }
        }
        
        PDFnum++;
    }

	@Override
	public void generarPDF(PedidoDeFuego panelPIF, PIF pif, ReporteFinMision rep) {
		try {
		CorreccionesPanel corr = panelPIF.getMetodoYTiroPanel().getCorreccionesPanel();
    	Map<String, String> historial = corr.getHistorialCorrecciones();
    	
    	String desktop = System.getProperty("user.home") + File.separator + "Desktop";
        String nombreArchivo = desktop + File.separator + "ReporteFinMision_" + pif.getId() + "_" + PDFnum + ".pdf";

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
                padre,
                "PDF generado:\n" + nombreArchivo,
                "PDF Fin de Misión",
                JOptionPane.INFORMATION_MESSAGE
        );
		}
		catch(FileNotFoundException | DocumentException e1) {e1.printStackTrace();}
		
		this.PDFnum++;
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
}
