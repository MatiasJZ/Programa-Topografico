package interfaces;

import app.PedidoDeFuego;
import dominio.PIF;
import dominio.ReporteFinMision;

public interface PDFGenerator {
	void generarPDF();
	void generarPDF(PedidoDeFuego panelPIF, PIF pif, ReporteFinMision rep);
}
