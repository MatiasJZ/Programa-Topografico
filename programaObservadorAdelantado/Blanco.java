import java.awt.Color;

public class Blanco extends tipoDeBlanco {
    private String nombre;
    private coordenadas coordenadas;
    private String naturaleza;
    private String fecha;
    private boolean aliado;
    private String forma;  // nueva propiedad
    private Color color;   // nueva propiedad

    public Blanco(String nombre, coordenadas c, String naturaleza, String fecha,
                  boolean aliado, String forma, Color color) {
        super(aliado);
        this.nombre = nombre;
        this.coordenadas = c;
        this.naturaleza = naturaleza;
        this.fecha = fecha;
        this.aliado = aliado;
        this.forma = forma;
        this.color = color;
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
    
    public String ColorToString() {
    	if(aliado) {
    		return "Aliado";
    	}
    	else return "Enemigo";
    }
    public String getForma() { return forma; }
    public void setForma(String forma) { this.forma = forma; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
}
