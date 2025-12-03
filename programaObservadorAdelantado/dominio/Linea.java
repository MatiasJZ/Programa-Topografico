package dominio;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class Linea implements poligonal {

    private String nombre;
    private Coordinate inicio;
    private Coordinate fin;
    private double distancia;

    public Linea(String nombre, Coordinate inicio, Coordinate fin, double distancia) {
        this.nombre = nombre;
        this.inicio = inicio;
        this.fin = fin;
        this.distancia = distancia;
    }

    @Override
    public String getName() {
        return nombre;
    }

    @Override
    public String toString() {
    	return nombre;
    }
    
    public double getDistancia() {
        return distancia;
    }

    @Override
    public Geometry getGeometry() {
        GeometryFactory gf = new GeometryFactory();
        return gf.createLineString(new Coordinate[]{inicio, fin});
    }
}