public class Blanco extends tipoDeBlanco{

    private String nombre;
    private coordenadas coordenadas;
    private String naturaleza;
    private String fechaDeActualizacion;

    public Blanco(String nombre, coordenadas coordenadas, String naturaleza, String fechaDeActualizacion) {
        this.nombre = nombre;
        this.coordenadas = coordenadas;
        this.naturaleza = naturaleza;
        this.fechaDeActualizacion = fechaDeActualizacion;
    }
    public String getNombre() {
        return nombre;
    }
    public coordenadas getCoordenadas() {
        return coordenadas;
    }
    public String getNaturaleza() {
        return naturaleza;
    }
    public String getFechaDeActualizacion() {
        return fechaDeActualizacion;
    }
    @Override
    public String toString() {
        return "Blanco{" +
                "nombre='" + nombre + '\'' +
                ", coordenadas=" + coordenadas +
                ", naturaleza='" + naturaleza + '\'' +
                ", fechaDeActualizacion='" + fechaDeActualizacion + '\'' +
                '}';
    }
}