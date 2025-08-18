public class coordRectangulares extends coordenadas{
     //rectangulares
private double coordX;
private double coordY;
private double cotaXY;

public coordRectangulares(){
    coordX = 0;
    coordY = 0;
    cotaXY = 0;
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

public void setX(double i){
 coordX = i;
}

public void setY(double i){
 coordY = i;
}

public void setCota(double i){
 cotaXY = i;
}

@Override
public double distanciaA(coordenadas otra) {
    coordRectangulares o = (coordRectangulares) otra;
    double dx = this.coordX - o.getX();
    double dy = this.coordY - o.getY();
    double dz = this.cotaXY - o.getCota();
    return Math.sqrt(dx*dx + dy*dy + dz*dz);
}

}