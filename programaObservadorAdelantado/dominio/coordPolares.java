package dominio;
public class coordPolares extends coordenadas {
    private double direccion;   // milésimos (0–6399)
    private double distancia;   // metros
    private double angVertical; // milésimos
    private coordRectangulares referencia;

    public coordPolares(double direccion, double distancia, double angVertical, coordRectangulares referencia) {
        this.direccion = direccion;
        this.distancia = distancia;
        this.angVertical = angVertical;
        this.referencia = referencia;
    }
    
    public double getDireccion() { return direccion; }       // en mils
    
    public double getDistancia() { return distancia; }       // en metros
    
    public double getAnguloVertical() { return angVertical; }// en mils
    
    // Convertir coordenadas polares a rectangulares (en metros)
    public coordRectangulares toRectangulares() {
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

        return new coordRectangulares(x, y, 0);
    }
    
    @Override
    public double distanciaA(coordRectangulares otro) {
    	
        coordRectangulares p1 = this.toRectangulares();

        double dx = otro.getX() - p1.getX();
        double dy = otro.getY() - p1.getY();
        return Math.hypot(dx, dy);
    }
    
    @Override
    public double getX() { return this.toRectangulares().getX(); }
    
    @Override
    public double getY() { return this.toRectangulares().getY(); }
}