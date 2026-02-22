package dominio;

import org.locationtech.jts.geom.Geometry;

/**
 * Represents a polygonal entity with a name, geometry, and popup menu capability.
 * Implementations of this interface should provide access to the polygon's name,
 * its geometric representation, and indicate whether it supports a popup menu.
 */
public interface Poligonal {
    String getName();
    Geometry getGeometry();
    Boolean tienePopUpMenu();
}