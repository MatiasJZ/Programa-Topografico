package dominio;

import app.SituacionTacticaTopografica;

/**
 * Representa una entidad que posee una posición definida mediante coordenadas rectangulares
 * y un nombre identificador. Extiende la interfaz {@link Vertice}.
 * 
 * Proporciona métodos para obtener las coordenadas, el nombre y el prefijo del tipo de la entidad.
 * Además, define métodos por defecto relacionados con el cierre poligonal, permitiendo que las
 * implementaciones especifiquen si soportan esta operación y cómo ejecutarla en un contexto dado.
 */
public interface Posicionable extends Vertice{
	
    CoordenadasRectangulares getCoordenadas();
    
    String getNombre();
    
    default boolean soportaCierrePoligonal() {return false;}
    
    default void ejecutarCierrePoligonal(SituacionTacticaTopografica contexto) {}
    
    String getPrefijoTipo();
}