public class coordPolares extends coordenadas {
    private double direccion;   // grados
    private double distancia;   // metros
    private double angVertical; // grados, opcional
    private coordRectangulares referencia; // lat/lon de referencia

    public coordPolares(double direccion, double distancia, double angVertical, coordRectangulares referencia) {
        this.direccion = direccion;
        this.distancia = distancia;
        this.angVertical = angVertical;
        this.referencia = referencia;
    }

    public double getDireccion() { return direccion; }
    public double getDistancia() { return distancia; }
    public double getAnguloVertical() { return angVertical; }

    // Convertir polar -> coordenada rectangular real (lat/lon)
    public coordRectangulares toRectangulares() {
        double R = 6371000; // radio Tierra
        double deltaLat = (distancia * Math.cos(Math.toRadians(direccion))) / R;
        double deltaLon = (distancia * Math.sin(Math.toRadians(direccion))) / 
                          (R * Math.cos(Math.toRadians(referencia.getY())));
        double lat = referencia.getY() + Math.toDegrees(deltaLat);
        double lon = referencia.getX() + Math.toDegrees(deltaLon);
        return new coordRectangulares(lon, lat, 0);
    }

    @Override
    public double distanciaA(coordenadas otro) {
        coordRectangulares punto1 = this.toRectangulares();
        coordRectangulares punto2;

        if (otro instanceof coordRectangulares) {
            punto2 = (coordRectangulares) otro;
        } else if (otro instanceof coordPolares) {
            punto2 = ((coordPolares) otro).toRectangulares();
        } else {
            throw new IllegalArgumentException("Tipo de coordenada no soportado");
        }

        return new coordRectangulares(0,0,0).distanciaVincenty(punto1.getY(), punto1.getX(),punto2.getY(), punto2.getX()
        );
    }
}