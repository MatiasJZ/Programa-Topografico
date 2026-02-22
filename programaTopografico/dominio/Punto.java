package dominio;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * La clase {@code Punto} representa un punto topográfico con coordenadas rectangulares y un nombre identificador.
 * Implementa las interfaces {@link Poligonal} y {@link Posicionable}, proporcionando métodos para obtener
 * información geométrica y de identificación, así como funcionalidades relacionadas con la poligonal.
 *
 * <p>Principales características:
 * <ul>
 *   <li>Almacena coordenadas rectangulares y un nombre.</li>
 *   <li>Permite obtener la geometría del punto como un objeto {@code Geometry}.</li>
 *   <li>Proporciona métodos para manipular y acceder a las propiedades del punto.</li>
 *   <li>Soporta operaciones relacionadas con el cierre poligonal.</li>
 * </ul>
 *
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class Punto implements Poligonal,Posicionable{
	
	private CoordenadasRectangulares coord;
	private String nombre;
	
	@Override
    public String getName() {
        return this.nombre;
    }
	
	@Override
	public String getPrefijoTipo() { return "[P] "; }

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
    public boolean soportaCierrePoligonal() {
        return true; 
    }

    /*@Override
    public void ejecutarCierrePoligonal(SituacionTacticaTopografica contexto) {
        contexto.dialogoCierreControlado(this);
    }*/
	
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

	@Override
	public Boolean tienePopUpMenu() {
		return true;
	}
}
