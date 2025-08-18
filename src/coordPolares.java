public class coordPolares extends coordenadas{

     //polares 
private double direccion;
private double distancia;
private double angVertical;

public coordPolares(){
    direccion = 0;
    distancia = 0;
    angVertical = 0;
}

public double getDireccion(){
    return direccion;
 }

public double getDistancia(){
    return distancia;
 }

public double getAnguloVertical(){
    return angVertical;
 }

public void setDireccion(double i){
 direccion = i;
}

public void setDistancia(double i){
 distancia = i;
}

public void setAnguloVertical(double i){
 angVertical = i;
}

@Override
public double distanciaA(coordenadas otro) {

    coordPolares o = (coordPolares) otro;

    // Convertimos esta coordenada a rectangulares
    double x1 = this.distancia * Math.cos(Math.toRadians(this.angVertical)) * Math.cos(Math.toRadians(this.direccion));
    double y1 = this.distancia * Math.cos(Math.toRadians(this.angVertical)) * Math.sin(Math.toRadians(this.direccion));
    double z1 = this.distancia * Math.sin(Math.toRadians(this.angVertical));

    // Convertimos la coordenada del otro punto
    double x2 = o.getDistancia() * Math.cos(Math.toRadians(o.getAnguloVertical())) * Math.cos(Math.toRadians(o.getDireccion()));
    double y2 = o.getDistancia() * Math.cos(Math.toRadians(o.getAnguloVertical())) * Math.sin(Math.toRadians(o.getDireccion()));
    double z2 = o.getDistancia() * Math.sin(Math.toRadians(o.getAnguloVertical()));

    // Calculamos la distancia 3D
    double dx = x1 - x2;
    double dy = y1 - y2;
    double dz = z1 - z2;

    return Math.sqrt(dx*dx + dy*dy + dz*dz);
}

}