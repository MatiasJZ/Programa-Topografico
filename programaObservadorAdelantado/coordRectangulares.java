public class coordRectangulares extends coordenadas{
     //rectangulares
private double coordX;
private double coordY;
private double cotaXY;

public coordRectangulares(double x, double y, double cota){
    this.coordX = x;
    this.coordY = y;
    this.cotaXY = cota;
    
}
public double getX(){
    return coordX;
 }

public double getY(){
    return coordY;
 }
public double getCota(){
    return cotaXY;
 }

@Override
public double distanciaA(coordenadas otra) {
    double x1 = this.coordX;
    double y1 = this.coordY;
    double z1 = this.cotaXY;

    double x2, y2, z2;

    switch (otra) {
        case coordRectangulares o -> {
            // Si la otra también es rectangular
            x2 = o.getX();
            y2 = o.getY();
            z2 = o.getCota();
        }
        case coordPolares o -> {
            // Si la otra es polar, la pasamos a rectangulares
            x2 = o.getDistancia() * Math.cos(Math.toRadians(o.getAnguloVertical())) * Math.cos(Math.toRadians(o.getDireccion()));
            y2 = o.getDistancia() * Math.cos(Math.toRadians(o.getAnguloVertical())) * Math.sin(Math.toRadians(o.getDireccion()));
            z2 = o.getDistancia() * Math.sin(Math.toRadians(o.getAnguloVertical()));
        }
        default -> throw new IllegalArgumentException("Tipo de coordenada no soportado");
    }

    // Distancia 3D
    double dx = x1 - x2;
    double dy = y1 - y2;
    double dz = z1 - z2;

    return Math.sqrt(dx*dx + dy*dy + dz*dz);
}

}