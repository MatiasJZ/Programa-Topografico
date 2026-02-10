package dominio;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class PIF {

    private String id;
    private LocalDateTime fechaHora;
    private Blanco blanco;
    private String modoMision;
    private String registroSobre;
    private String barreraFrente;
    private String barreraInclinacion;
    private String efectoDeseado;
    private String modoFuego;
    private boolean cercano;
    private boolean granAngulo;
    private String granada;
    private String espoleta;
    private String volumen;
    private String haz;
    private String piezas;
    private String seccion;
    private boolean fgoCont;
    private boolean tes;
    private String orden;
    private ReporteFinMision reporteFin;
    
    public PIF(String id, LocalDateTime fechaHora, Blanco blanco, String mision, String registroSobre, String barreraFrente, String barreraInclinacion,
    		   String efectoDeseado,String modoFuego, boolean cercano, boolean granAngulo, String granada,
    		   String espoleta, String volumen, String haz, String piezas, String seccion, boolean fgoCont, boolean tes, String orden) {
    	this.id = id;
    	this.fechaHora = fechaHora;
        this.blanco = blanco;
        this.modoMision = mision;
        this.registroSobre = registroSobre;
        this.barreraFrente = barreraFrente;
        this.barreraInclinacion = barreraInclinacion;
        this.efectoDeseado = efectoDeseado;
        this.modoFuego = modoFuego;
        this.cercano = cercano;
        this.granAngulo = granAngulo;
        this.granada = granada;
        this.espoleta = espoleta;
        this.volumen = volumen;
        this.haz = haz;
        this.piezas = piezas;
        this.seccion = seccion;
        this.fgoCont = fgoCont;
        this.tes = tes;
        this.orden = orden;
    }
    
    public String getOrden() {
    	return orden;
    }
    
    public String mostrarDatosDePIF() {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(id == null ? "-" : id).append("\n");
        sb.append("Fecha/Hora: ").append(fechaHora == null ? "-" : fechaHora.toString()).append("\n");

        if (blanco != null) {
            sb.append("Blanco: ").append(blanco.getNombre() == null ? "-" : blanco.getNombre()).append("\n");
            sb.append("Naturaleza: ").append(blanco.getNaturaleza() == null ? "-" : blanco.getNaturaleza()).append("\n");
            sb.append("Coordenadas: ").append(blanco.getCoordenadas() == null ? "-" : blanco.getCoordenadas().toString()).append("\n");
            sb.append("Situación: ").append(String.valueOf(blanco.getSituacionMovimiento())).append("\n");
            sb.append("Orientación: ").append(blanco.getOrientacion()).append("°\n");
            sb.append("Info adicional: ").append(blanco.getInformacionAdicional() == null ? "-" : blanco.getInformacionAdicional()).append("\n");
        } else {
            sb.append("Blanco: -\n");
        }

        sb.append("\n--- Misión ---\n");
        sb.append("Modo Misión: ").append(modoMision == null ? "-" : modoMision).append("\n");
        sb.append("Registro Sobre: ").append(registroSobre == null ? "-" : registroSobre).append("\n");
        sb.append("Barrera Frente: ").append(barreraFrente == null ? "-" : barreraFrente).append("\n");
        sb.append("Barrera Inclinación: ").append(barreraInclinacion == null ? "-" : barreraInclinacion).append("\n");

        sb.append("\n--- Método de Ataque ---\n");
        sb.append("Efecto Deseado: ").append(efectoDeseado == null ? "-" : efectoDeseado).append("\n");
        sb.append("Modo Fuego: ").append(modoFuego == null ? "-" : modoFuego).append("\n");
        sb.append("Cercano: ").append(cercano ? "Sí" : "No").append("\n");
        sb.append("Gran Ángulo: ").append(granAngulo ? "Sí" : "No").append("\n");
        sb.append("Granada: ").append(granada == null ? "-" : granada).append("\n");
        sb.append("Espoleta: ").append(espoleta == null ? "-" : espoleta).append("\n");
        sb.append("Volumen: ").append(volumen == null ? "-" : volumen).append("\n");
        sb.append("Haz: ").append(haz == null ? "-" : haz).append("\n");

        sb.append("\n--- Tiro y Control ---\n");
        sb.append("Piezas: ").append(piezas == null ? "-" : piezas).append("\n");
        sb.append("Sección: ").append(seccion == null ? "-" : seccion).append("\n");
        sb.append("FGO continuo: ").append(fgoCont ? "Sí" : "No").append("\n");
        sb.append("TES: ").append(tes ? "Sí" : "No").append("\n");

        // Si existe ReporteFinMision (si añadiste ese atributo en PIF)
        try {
            // evita dependencias si ReporteFinMision no existe
            Method m = this.getClass().getMethod("getReporteFin");
            Object rep = m.invoke(this);
            if (rep != null) {
                sb.append("\n--- Reporte Fin de Misión ---\n");
                // intentamos llamar a los getters comunes del ReporteFinMision
                try {
                    Method gEfecto = rep.getClass().getMethod("getEfectoObservado");
                    Method gDisp = rep.getClass().getMethod("getDispersion");
                    Method gDanos = rep.getClass().getMethod("getDanos");
                    Method gMov  = rep.getClass().getMethod("getMovimiento");
                    Method gRec  = rep.getClass().getMethod("getRecomendacion");
                    Method gObs  = rep.getClass().getMethod("getObservaciones");
                    Method gFecha = rep.getClass().getMethod("getFechaHora");

                    Object fechaRep = gFecha.invoke(rep);
                    sb.append("Fecha reporte: ").append(fechaRep == null ? "-" : fechaRep.toString()).append("\n");
                    sb.append("Efecto observado: ").append(String.valueOf(gEfecto.invoke(rep))).append("\n");
                    sb.append("Dispersión: ").append(String.valueOf(gDisp.invoke(rep))).append("\n");
                    sb.append("Daños: ").append(String.valueOf(gDanos.invoke(rep))).append("\n");
                    sb.append("Movimiento: ").append(String.valueOf(gMov.invoke(rep))).append("\n");
                    sb.append("Recomendación: ").append(String.valueOf(gRec.invoke(rep))).append("\n");
                    sb.append("Observaciones: ").append(String.valueOf(gObs.invoke(rep))).append("\n");
                } catch (NoSuchMethodException nsme) {
                    // si la clase ReporteFinMision tiene distinta API, simplemente mostrar toString()
                    sb.append(rep.toString()).append("\n");
                }
            }
        } catch (Exception ignored) {
            // si no existe getReporteFin o por reflexión falló, lo ignoramos.
        }

        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Blanco getBlanco() {
        return blanco;
    }

    public String getModoMision() {
        return modoMision;
    }
    
    public void setReporteFin(ReporteFinMision rep) {
        this.reporteFin = rep;
    }

    public ReporteFinMision getReporteFin() {
        return reporteFin;
    }

    public String getRegistroSobre() {
        return registroSobre;
    }
    
    public String getBarreraInclinacion() {
    	return barreraInclinacion;
    }
    
    public String getBarreraFrente() {
        return barreraFrente;
    }

    public String getEfectoDeseado() {
        return efectoDeseado;
    }

    public String getModoFuego() {
        return modoFuego;
    }

    public boolean isCercano() {
        return cercano;
    }

    public boolean isGranAngulo() {
        return granAngulo;
    }

    public String getGranada() {
        return granada;
    }

    public String getEspoleta() {
        return espoleta;
    }

    public String getVolumen() {
        return volumen;
    }

    public String getHaz() {
        return haz;
    }

    public String getPiezas() {
        return piezas;
    }

    public String getSeccion() {
        return seccion;
    }

    public boolean isFgoCont() {
        return fgoCont;
    }

    public boolean isTes() {
        return tes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setBlanco(Blanco blanco) {
        this.blanco = blanco;
    }

    public void setModoMision(String modoMision) {
        this.modoMision = modoMision;
    }

    public void setRegistroSobre(String registro) {
        this.registroSobre = registro;
    }

    public void setBarreraFrente(String barrera) {
    	this.barreraFrente = barrera;
    }
    
    public void setBarreraInclinacion(String barrera) {
        this.barreraInclinacion = barrera;
    }

    public void setEfectoDeseado(String efectoDeseado) {
        this.efectoDeseado = efectoDeseado;
    }

    public void setModoFuego(String modoFuego) {
        this.modoFuego = modoFuego;
    }

    public void setCercano(boolean cercano) {
        this.cercano = cercano;
    }

    public void setGranAngulo(boolean granAngulo) {
        this.granAngulo = granAngulo;
    }

    public void setGranada(String granada) {
        this.granada = granada;
    }

    public void setEspoleta(String espoleta) {
        this.espoleta = espoleta;
    }

    public void setVolumen(String volumen) {
        this.volumen = volumen;
    }

    public void setHaz(String haz) {
        this.haz = haz;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public void setFgoCont(boolean fgoCont) {
        this.fgoCont = fgoCont;
    }

    public void setTes(boolean tes) {
        this.tes = tes;
    }

    @Override
    public String toString() {
        return "PIF {" +
                "\n    id='" + id + '\'' +
                ", \n    fechaHora=" + fechaHora +
                ", \n    blanco=" + blanco + // Asume que la clase Blanco tiene su propio toString()
                ", \n    modoMision='" + modoMision + '\'' +
                ", \n    registro sobre='" + registroSobre + '\'' +
                ", \n    barrera frente='" + barreraFrente + '\'' +
                ", \n    barrera inclinacion='" + barreraInclinacion + '\'' +
                ", \n    efectoDeseado='" + efectoDeseado + '\'' +
                ", \n    modoFuego='" + modoFuego + '\'' +
                ", \n    cercano=" + cercano +
                ", \n    granAngulo=" + granAngulo +
                ", \n    granada='" + granada + '\'' +
                ", \n    espoleta='" + espoleta + '\'' +
                ", \n    volumen='" + volumen + '\'' +
                ", \n    haz='" + haz + '\'' +
                ", \n    piezas='" + piezas + '\'' +
                ", \n    seccion='" + seccion + '\'' +
                ", \n    fgoCont=" + fgoCont +
                ", \n    tes=" + tes +
                "\n}";
    }
}