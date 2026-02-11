package dominio;
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
    
    public double getDireccion() { return direccion; }       // en mils
    
    public double getDistancia() { return distancia; }       // en metros
    
    public double getCota() { return cota; }// en mils

    // Convertir coordenadas polares a rectangulares (en metros)
    public CoordenadasRectangulares toRectangulares() {
        if (referencia == null) {
            throw new IllegalStateException("No hay referencia definida para la conversión polar.");
        }
        // Convertir milésimos a radianes (6400 mils = 2π rad)
        double dirRad = direccion * (Math.PI / 3200.0);
        // Descomposición
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
}