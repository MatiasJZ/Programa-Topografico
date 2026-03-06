package dominio;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import interfaces.Poligonal;

/**
 * Represents a line segment defined by a start and end coordinate, distance, and azimuth.
 * Implements the {@link Poligonal} interface.
 * <p>
 * This class provides methods to access the line's name, azimuth, distance, and coordinates,
 * as well as to obtain its geometric representation.
 * </p>
 *
 * Fields:
 * <ul>
 *   <li>nombre - The name of the line.</li>
 *   <li>inicio - The starting coordinate of the line.</li>
 *   <li>fin - The ending coordinate of the line.</li>
 *   <li>distancia - The distance between the start and end coordinates.</li>
 *   <li>azimut - The azimuth (direction) of the line.</li>
 * </ul>
 *
 * Methods:
 * <ul>
 *   <li>{@link #getName()} - Returns the name of the line.</li>
 *   <li>{@link #getAzimut()} - Returns the azimuth of the line.</li>
 *   <li>{@link #setAzimut(double)} - Sets the azimuth of the line.</li>
 *   <li>{@link #getDistancia()} - Returns the distance of the line.</li>
 *   <li>{@link #getC1()} - Returns the starting coordinate.</li>
 *   <li>{@link #getC2()} - Returns the ending coordinate.</li>
 *   <li>{@link #getGeometry()} - Returns the geometric representation as a LineString.</li>
 *   <li>{@link #tienePopUpMenu()} - Indicates if the line has a popup menu (always false).</li>
 *   <li>{@link #toString()} - Returns the name of the line as its string representation.</li>
 * </ul>
 */
public class Linea implements Poligonal {

    private String nombre;
    private Coordinate inicio;
    private Coordinate fin;
    private double distancia;
    private double azimut;

    public Linea(String nombre, Coordinate inicio, Coordinate fin, double distancia, double azimut) {
        this.nombre = nombre;
        this.azimut = azimut;
        this.inicio = inicio;
        this.fin = fin;
        this.distancia = distancia;
    }

    @Override
    public String getName() {
        return nombre;
    }

    public double getAzimut() {
    	return azimut;
    }
    
    public void setAzimut(double a) {
    	azimut = a;
    }
    
    @Override
    public String toString() {
    	return nombre;
    }
    
    public double getDistancia() {
        return distancia;
    }

    public Coordinate getC1() {
    	return inicio;
    }
    
    public Coordinate getC2() {
    	return fin;
    }
    
    @Override
    public Geometry getGeometry() {
        GeometryFactory gf = new GeometryFactory();
        return gf.createLineString(new Coordinate[]{inicio, fin});
    }

	@Override
	public Boolean tienePopUpMenu() {
		return false;
	}
}