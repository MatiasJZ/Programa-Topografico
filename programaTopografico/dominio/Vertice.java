package dominio;

/**
 * Representa un vértice en un sistema topográfico, identificado por un nombre y coordenadas rectangulares.
 * Proporciona métodos para obtener el nombre, las coordenadas y calcular la distancia a otro vértice.
 */
public interface Vertice {
    String getNombre();
    CoordenadasRectangulares getCoordenadas();
    
    default double distanciaA(Vertice otro) {
        return this.getCoordenadas().distanciaA(otro.getCoordenadas());
    }
}