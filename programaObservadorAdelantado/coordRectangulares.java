public class coordRectangulares extends coordenadas {
    private double coordX; // longitud
    private double coordY; // latitud
    private double cotaXY; // opcional, no usada en distancia sobre superficie

    public coordRectangulares(double x, double y, double cota) {
        this.coordX = x;
        this.coordY = y;
        this.cotaXY = cota;
    }

    public double getX() { return coordX; }
    public double getY() { return coordY; }
    public double getCota() { return cotaXY; }

    @Override
    public double distanciaA(coordenadas otro) {
        coordRectangulares c2;
        if (otro instanceof coordRectangulares) {
            c2 = (coordRectangulares) otro;
        } else if (otro instanceof coordPolares) {
            c2 = ((coordPolares) otro).toRectangulares();
        } else {
            throw new IllegalArgumentException("Tipo de coordenada no soportado: " + otro.getClass().getSimpleName());
        }
        // Diferencias
        double dx = c2.getX() - this.coordX;
        double dy = c2.getY() - this.coordY;
        // Distancia euclídea de alta precisión
        return Math.hypot(dx, dy); // equivalente a sqrt(dx² + dy²) pero numéricamente más estable
    }
}