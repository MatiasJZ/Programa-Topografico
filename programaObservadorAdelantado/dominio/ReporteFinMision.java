package dominio;

import java.time.LocalDateTime;

public class ReporteFinMision {

    private LocalDateTime fechaHora;

    private String efectoObservado;
    private String dispersion;
    private String danos;
    private String movimiento;
    private String recomendacion;
    private String observaciones;

    public ReporteFinMision(String efectoObservado, String dispersion, String danos,
                            String movimiento, String recomendacion, String observaciones) {

        this.fechaHora = LocalDateTime.now();
        this.efectoObservado = efectoObservado;
        this.dispersion = dispersion;
        this.danos = danos;
        this.movimiento = movimiento;
        this.recomendacion = recomendacion;
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getEfectoObservado() { return efectoObservado; }
    public String getDispersion() { return dispersion; }
    public String getDanos() { return danos; }
    public String getMovimiento() { return movimiento; }
    public String getRecomendacion() { return recomendacion; }
    public String getObservaciones() { return observaciones; }
}
