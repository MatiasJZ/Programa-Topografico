package dominio;

import org.locationtech.jts.geom.Geometry;

public interface Poligonal {
    String getName();
    Geometry getGeometry();
    Boolean tienePopUpMenu();
}