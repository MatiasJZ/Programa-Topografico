public class Grupo extends tipoDeBlanco {
    private String nombre;

    public Grupo(String nombre, boolean aliado) {
        super(aliado);
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}