package dominio;
public abstract class Coordenadas{
	public abstract double getX();
	public abstract double getY();
	public abstract double getCota();
	public abstract void setCota(double c);
    public abstract double distanciaA(CoordenadasRectangulares otro);
}