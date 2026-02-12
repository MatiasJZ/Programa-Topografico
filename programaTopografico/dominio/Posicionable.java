package dominio;

import app.SituacionTacticaTopografica;

public interface Posicionable extends Vertice{
	
    CoordenadasRectangulares getCoordenadas();
    
    String getNombre();
    
    default boolean soportaCierrePoligonal() {return false;}
    
    default void ejecutarCierrePoligonal(SituacionTacticaTopografica contexto) {}
}