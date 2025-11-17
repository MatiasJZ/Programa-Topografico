package dominio;
public class Punto {
	
	private coordenadas coord;
	private String nombre;
	
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
