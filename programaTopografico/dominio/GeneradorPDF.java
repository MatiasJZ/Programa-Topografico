package dominio;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import util.PDFGenerator;

public class GeneradorPDF implements PDFGenerator{

	private final Component padre;
	
	public GeneradorPDF(Component padre) {
		this.padre = padre;
	}
	
	@Override
	public void generarPDF() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        FileDialog fd = new FileDialog(parentFrame, "GUARDAR REGISTRO TOPOGRÁFICO", FileDialog.SAVE);
        fd.setFile("Reporte_Topografico.pdf");
        fd.setVisible(true);

        if (fd.getFile() != null) {
            String rutaCompleta = fd.getDirectory() + fd.getFile();
            if (!rutaCompleta.toLowerCase().endsWith(".pdf")) rutaCompleta += ".pdf";

            // Definimos las fuentes de iText correctamente
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
                    "MEDICIÓN AYD"
                };

                for (String funcion : funcionesPDF) {
                    if (datos.containsKey(funcion)) {
                        PdfPTable tabla = new PdfPTable(1);
                        tabla.setWidthPercentage(100);
                        tabla.setSpacingBefore(10f);

                        // Celda de título de la función
                        PdfPCell celdaTitulo = new PdfPCell(new Phrase(funcion, fuenteCabeceraTabla));
                        celdaTitulo.setBackgroundColor(new BaseColor(192, 192, 192));
                        celdaTitulo.setPadding(5);
                        tabla.addCell(celdaTitulo);
                        
                        // Celda de contenido del cálculo
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
    }

}
