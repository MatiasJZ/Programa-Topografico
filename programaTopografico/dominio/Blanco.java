package dominio;

/**
 * La clase {@code Blanco} representa un objeto posicionable con información relevante
 * para aplicaciones topográficas o militares. Un Blanco tiene un nombre, coordenadas,
 * naturaleza, fecha de actualización, identificador simbólico, situación de movimiento,
 * información adicional, orientación y atributos relacionados con entidad, afiliación y escalón.
 * 
 * Implementa la interfaz {@link Posicionable}.
 * 
 * Campos principales:
 * <ul>
 *   <li>nombre: Nombre del blanco.</li>
 *   <li>coordenadas: Coordenadas rectangulares asociadas al blanco.</li>
 *   <li>naturaleza: Naturaleza o tipo del blanco.</li>
 *   <li>fecha: Fecha de actualización de la información del blanco.</li>
 *   <li>simID: Identificador simbólico.</li>
 *   <li>situacionMovimiento: Estado de movimiento del blanco.</li>
 *   <li>informacionAdicional: Información adicional relevante.</li>
 *   <li>orientacion: Orientación del blanco en grados.</li>
 *   <li>ultEntidad, ultAfiliacion, ultEchelon: Últimos valores de entidad, afiliación y escalón.</li>
 * </ul>
 * 
 * Métodos principales:
 * <ul>
 *   <li>Getters y setters para todos los campos.</li>
 *   <li>{@code getPrefijoTipo()}: Devuelve el prefijo de tipo para el blanco.</li>
 *   <li>{@code toString()}: Devuelve el nombre del blanco.</li>
 * </ul>
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class Blanco implements Posicionable {
    private String nombre;
    private CoordenadasRectangulares coordenadas;
    private String naturaleza;
    private String fecha;
    private String simID;
    private SituacionMovimiento situacionMovimiento;
    private String informacionAdicional;
    private double orientacion;
    private String ultEntidad;
    private String ultAfiliacion;
    private String ultEchelon;
    
    public Blanco(String nombre, CoordenadasRectangulares c, String naturaleza, String fecha) {
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
    
    @Override
    public String getPrefijoTipo() { return "[B] "; }

    public String getNombre() { return nombre; }
    
    public void setNombre(String n) { nombre = n; }

    public CoordenadasRectangulares getCoordenadas() { return coordenadas; }
    
    public void setCoordenadas(CoordenadasRectangulares c) { coordenadas = c; }

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
        return this.nombre;
    }
}
