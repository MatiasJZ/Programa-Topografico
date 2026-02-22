package dominio;

import java.time.LocalDateTime;

/**
 * Representa un reporte generado al finalizar una misión topográfica.
 * 
 * <p>Incluye información sobre el efecto observado, dispersión, daños,
 * movimiento y observaciones adicionales, junto con la fecha y hora de creación del reporte.</p>
 * 
 * <ul>
 *   <li><b>fechaHora</b>: Fecha y hora en que se crea el reporte.</li>
 *   <li><b>efectoObservado</b>: Descripción del efecto observado al finalizar la misión.</li>
 *   <li><b>dispersion</b>: Información sobre la dispersión observada.</li>
 *   <li><b>danos</b>: Detalles sobre los daños detectados.</li>
 *   <li><b>movimiento</b>: Observaciones sobre el movimiento registrado.</li>
 *   <li><b>observaciones</b>: Comentarios adicionales relevantes al reporte.</li>
 * </ul>
 * 
 * @author [Matias Leonel Juarez]
 */
public class ReporteFinMision {

    private LocalDateTime fechaHora;

    private String efectoObservado;
    private String dispersion;
    private String danos;
    private String movimiento;
    private String observaciones;

    public ReporteFinMision(String efectoObservado, String dispersion, String danos,
                            String movimiento, String observaciones) {

        this.fechaHora = LocalDateTime.now();
        this.efectoObservado = efectoObservado;
        this.dispersion = dispersion;
        this.danos = danos;
        this.movimiento = movimiento;
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getEfectoObservado() { return efectoObservado; }
    public String getDispersion() { return dispersion; }
    public String getDanos() { return danos; }
    public String getMovimiento() { return movimiento; }
    public String getObservaciones() { return observaciones; }
}
