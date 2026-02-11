package dominio;

public interface Vertice {
    String getNombre();
    CoordenadasRectangulares getCoordenadas();
    
    default double distanciaA(Vertice otro) {
        return this.getCoordenadas().distanciaA(otro.getCoordenadas());
    }
}