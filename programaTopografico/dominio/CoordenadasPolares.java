package dominio;
/**
 * La clase {@code CoordenadasPolares} representa un punto en el espacio utilizando coordenadas polares
 * relativas a una referencia de tipo {@link CoordenadasRectangulares}. 
 * 
 * <p>Las coordenadas polares se definen por una dirección (en milésimos, 0–6399), una distancia (en metros)
 * y una cota (ángulo vertical, en milésimos). La clase permite convertir estas coordenadas a coordenadas
 * rectangulares (cartesianas) y calcular distancias a otros puntos.
 * 
 * <ul>
 *   <li><b>direccion</b>: Dirección en milésimos (0–6399), donde 6400 milésimos equivalen a 360 grados.</li>
 *   <li><b>distancia</b>: Distancia al punto desde la referencia, en metros.</li>
 *   <li><b>cota</b>: Ángulo vertical en milésimos.</li>
 *   <li><b>referencia</b>: Punto de referencia en coordenadas rectangulares desde el cual se mide la posición polar.</li>
 * </ul>
 * 
 * <p>Incluye métodos para obtener los valores de dirección, distancia y cota, así como para convertir a coordenadas
 * rectangulares y calcular la distancia a otro punto.
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class CoordenadasPolares extends Coordenadas {
    private double direccion;   // milésimos (0–6399)
    private double distancia;   // metros
    private double cota; // milésimos
    private CoordenadasRectangulares referencia;

    public CoordenadasPolares(double direccion, double distancia, double angVertical, CoordenadasRectangulares referencia) {
        this.direccion = direccion;
        this.distancia = distancia;
        this.cota = angVertical;
        this.referencia = referencia;
    }
    
    public double getDireccion() { return direccion; }       
    
    public double getDistancia() { return distancia; }   
    
    public double getCota() { return cota; }

    public CoordenadasRectangulares toRectangulares() {
        if (referencia == null) {
            throw new IllegalStateException("No hay referencia definida para la conversión polar.");
        }
        // Convertir milésimos a radianes (6400 mils = 2π rad)
        double dirRad = direccion * (Math.PI / 3200.0);

        double deltaX = distancia * Math.sin(dirRad);
        double deltaY = distancia * Math.cos(dirRad);
        // Sumar desplazamientos
        double x = referencia.getX() + deltaX;
        double y = referencia.getY() + deltaY;

        return new CoordenadasRectangulares(x, y, 0);
    }
    
    @Override
    public double distanciaA(CoordenadasRectangulares otro) {
    	
        CoordenadasRectangulares p1 = this.toRectangulares();

        double dx = otro.getX() - p1.getX();
        double dy = otro.getY() - p1.getY();
        return Math.hypot(dx, dy);
    }
    
    @Override
    public double getX() { return this.toRectangulares().getX(); }
    
    @Override
    public double getY() { return this.toRectangulares().getY(); }

	@Override
	public void setCota(double c) {
		cota = c; 
	}
	@Override
	public boolean equals(Coordenadas c) {
		return (this.getX() == c.getX() && this.getY() == c.getY() && this.cota == c.getCota());
	}
}