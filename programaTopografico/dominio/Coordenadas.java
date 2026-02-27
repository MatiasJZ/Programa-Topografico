package dominio;
/**
 * Clase abstracta que representa un conjunto de coordenadas en un espacio topográfico.
 * Proporciona métodos para obtener y establecer valores de coordenadas y calcular distancias.
 */
public abstract class Coordenadas{
	public abstract double getX();
	public abstract double getY();
	public abstract double getCota();
	public abstract void setCota(double c);
    public abstract double distanciaA(CoordenadasRectangulares otro);
    public abstract boolean equals(Coordenadas c);
}