package dominio;

public interface Vertice {
    String getNombre();
    coordRectangulares getCoordenadas();
    
    default double distanciaA(Vertice otro) {
        return this.getCoordenadas().distanciaA(otro.getCoordenadas());
    }
}