package dominio;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class Punto implements poligonal{
	
	private coordenadas coord;
	private String nombre;
	
	@Override
    public String getName() {
        return this.nombre;
    }

    @Override
    public Geometry getGeometry() {
        coordRectangulares c = (coordRectangulares) coord;
        GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(new Coordinate(c.getX(), c.getY()));
    }
	
	public Punto(coordenadas c, String n) {
		setCoord(c);
		setNombre(n);
	}
	
	public coordenadas getCoord() {
		return coord;
	}
	
	public void setCoord(coordenadas coord) {
		this.coord = coord;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
