package dominio;
public class CoordenadasRectangulares extends Coordenadas {
    private double coordX; 
    private double coordY;
    private double cotaXY; 

    public CoordenadasRectangulares(double x, double y, double cota) {
        this.coordX = x;
        this.coordY = y;
        this.cotaXY = cota;
    }

    public double getX() { return coordX; }
    
    public double getY() { return coordY; }
    
    public double getCota() { return cotaXY; }

    @Override
    public double distanciaA(CoordenadasRectangulares otro) {
        double dx = otro.getX() - this.coordX;
        double dy = otro.getY() - this.coordY;
        return Math.hypot(dx, dy); // equivalente a sqrt(dx² + dy²) pero numéricamente más estable
    }
    
    @Override
    public String toString() {
    	return ("X: " +coordX + " ; "+"Y: "+ coordY);
    }

	@Override
	public void setCota(double c) {
		cotaXY = c;
	}
}