public class Blanco {
    private String nombre;
    private coordenadas coordenadas;
    private String naturaleza;
    private String fecha;
    private String simID;
    private SituacionMovimiento situacionMovimiento;
    private String informacionAdicional;
    private double orientacion;
    
    public Blanco(String nombre, coordRectangulares c, String naturaleza, String fecha) {
        this.nombre = nombre;
        this.coordenadas = c;
        this.naturaleza = naturaleza;
        this.fecha = fecha;
        this.simID = "";
        this.situacionMovimiento = SituacionMovimiento.DESCONOCIDO; 
        this.informacionAdicional = ""; 
        this.orientacion = 0;
    }

    public String getNombre() { return nombre; }
    
    public void setNombre(String n) { nombre = n; }

    public coordenadas getCoordenadas() { return coordenadas; }
    
    public void setCoordenadas(coordenadas c) { coordenadas = c; }

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
