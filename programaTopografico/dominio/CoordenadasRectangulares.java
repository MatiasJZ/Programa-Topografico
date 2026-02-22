package dominio;
/**
 * La clase {@code CoordenadasRectangulares} representa un punto en un sistema de coordenadas cartesianas (rectangulares),
 * extendiendo la clase base {@code Coordenadas}. Incluye las componentes X, Y y una cota (elevación) asociada al punto.
 * 
 * <p>Proporciona métodos para acceder y modificar las coordenadas y la cota, así como para calcular la distancia
 * euclidiana en el plano XY entre dos puntos de tipo {@code CoordenadasRectangulares}.</p>
 * 
 * <ul>
 *   <li>{@code getX()} y {@code getY()}: Devuelven las coordenadas X e Y respectivamente.</li>
 *   <li>{@code getCota()}: Devuelve la cota (elevación) del punto.</li>
 *   <li>{@code setCota(double c)}: Permite modificar la cota del punto.</li>
 *   <li>{@code distanciaA(CoordenadasRectangulares otro)}: Calcula la distancia euclidiana en el plano XY a otro punto.</li>
 *   <li>{@code toString()}: Devuelve una representación en cadena de las coordenadas X e Y.</li>
 * </ul>
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
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