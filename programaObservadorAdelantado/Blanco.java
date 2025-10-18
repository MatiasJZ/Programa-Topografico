import java.awt.Color;

public class Blanco {
    private String nombre;
    private coordenadas coordenadas;
    private String naturaleza;
    private String fecha;
    private String simID;

    public Blanco(String nombre, coordRectangulares c, String naturaleza, String fecha) {
        this.nombre = nombre;
        this.coordenadas = c;
        this.naturaleza = naturaleza;
        this.fecha = fecha;
        simID = "";
    }

    public String getNombre() { return nombre; }
    public void setNombre(String n) { nombre = n; }

    public coordenadas getCoordenadas() { return coordenadas; }
    public void setCoordenadas(coordenadas c) { coordenadas = c; }

    public String NombretoString() {
        return nombre;
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
}
