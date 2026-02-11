package dominio;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class Punto implements Poligonal,Posicionable{
	
	private CoordenadasRectangulares coord;
	private String nombre;
	
	@Override
    public String getName() {
        return this.nombre;
    }

    @Override
    public Geometry getGeometry() {
        CoordenadasRectangulares c = (CoordenadasRectangulares) coord;
        GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(new Coordinate(c.getX(), c.getY()));
    }
	
	public Punto(CoordenadasRectangulares c, String n) {
		setCoord(c);
		setNombre(n);
	}
	
	public CoordenadasRectangulares getCoordenadas() {
		return coord;
	}
	
	@Override
	public String toString() {
	    return this.nombre; // O el método que uses para obtener el nombre
	}
	
	public void setCoord(CoordenadasRectangulares coord) {
		this.coord = coord;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
