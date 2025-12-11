package dominio;
public abstract class coordenadas{
	public abstract double getX();
	public abstract double getY();
	public abstract double getCota();
	public abstract void setCota(double c);
    public abstract double distanciaA(coordRectangulares otro);
}