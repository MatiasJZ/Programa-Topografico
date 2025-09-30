

public class Blanco extends tipoDeBlanco {
    private String nombre;
    private coordenadas coordenadas;
    private String naturaleza;
    private String fecha;

    public Blanco(String nombre, coordenadas c, String naturaleza, String fecha, boolean aliado) {
        super(aliado);
        this.nombre = nombre;
        this.coordenadas = c;
        this.naturaleza = naturaleza;
        this.fecha = fecha;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String n) { nombre = n; }
    public coordenadas getCoordenadas() { return coordenadas; }
    public void setCoordenadas(coordenadas c) { coordenadas = c; }
    public String getNaturaleza() { return naturaleza; }
    public void setNaturaleza(String n) { naturaleza = n; }
    public String getFechaDeActualizacion() { return fecha; }
    public void setFecha(String f) { fecha = f; }
}