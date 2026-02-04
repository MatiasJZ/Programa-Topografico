package dominio;

import org.locationtech.jts.geom.Coordinate;

public class Blanco implements Posicionable {
    private String nombre;
    private coordRectangulares coordenadas;
    private String naturaleza;
    private String fecha;
    private String simID;
    private SituacionMovimiento situacionMovimiento;
    private String informacionAdicional;
    private double orientacion;
    private String ultEntidad;
    private String ultAfiliacion;
    private String ultEchelon;
    
    public Blanco(String nombre, coordRectangulares c, String naturaleza, String fecha) {
        this.nombre = nombre;
        this.coordenadas = c;
        this.naturaleza = naturaleza;
        this.fecha = fecha;
        this.simID = "";
        this.situacionMovimiento = SituacionMovimiento.FIJO; 
        this.informacionAdicional = ""; 
        this.orientacion = 0;
    }
    
    public String getUltEntidad() {
    	return ultEntidad;
    }
    
    public String getUltAfiliacion() {
    	return ultAfiliacion;
    }
    
    public String getUltEchelon() {
    	return ultEchelon;
    }
    
    public void setUltEntidad(String e) {
    	ultEntidad = e;
    }
    
    public void setUltAfiliacion(String e) {
    	ultAfiliacion = e;
    }
    
    public void setUltEchelon(String e) {
    	ultEchelon = e;
    }

    public String getNombre() { return nombre; }
    
    public void setNombre(String n) { nombre = n; }

    public coordRectangulares getCoordenadas() { return coordenadas; }
    
    public void setCoordenadas(coordRectangulares c) { coordenadas = c; }

    public String NombretoString() {
        return nombre;
    }
    
    public void setOrientacion(double e) {
    	orientacion = e;
    }
    
    public double getOrientacion() {
    	return orientacion;
    }
    
    public String getNaturaleza() { return naturaleza; }
    
    public void setNaturaleza(String n) { naturaleza = n; }

    public String getFechaDeActualizacion() { return fecha; }
    
    public void setFecha(String f) { fecha = f; }
    
    public void setSimID(String a) {
    	simID = a;
    }
    
    public String getSimID() {
    	return simID;
    }
    
    public SituacionMovimiento getSituacionMovimiento() { return situacionMovimiento; }
    
    public void setSituacionMovimiento(SituacionMovimiento situacionMovimiento) {
        this.situacionMovimiento = situacionMovimiento;
    }
    
    public String getInformacionAdicional() { return informacionAdicional; }
    
    public void setInformacionAdicional(String info) { this.informacionAdicional = info; }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s [%s] {%s}",nombre,naturaleza,coordenadas != null ? coordenadas.toString() : "Sin coordenadas",
        		situacionMovimiento,(informacionAdicional != null && !informacionAdicional.isEmpty() ? informacionAdicional : "Sin info"));
    }
}
