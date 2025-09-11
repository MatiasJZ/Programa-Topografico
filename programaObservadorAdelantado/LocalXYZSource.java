import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.TileRange;
import org.openstreetmap.gui.jmapviewer.TileXY;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.IProjected;
import org.openstreetmap.gui.jmapviewer.tilesources.AbstractTMSTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.TileSourceInfo;

public class LocalXYZSource extends AbstractTMSTileSource {
    private final String format = "png";

    public LocalXYZSource() {
        super(new TileSourceInfo(
            "CalTopoBATERIAS",
            "http://localhost:3650/api/tiles/CalTopoBATERIAS", // baseUrl del TileJSON
            "© MapTiler Engine FREE 14.1-6989d8c302"
        ));
    }

    @Override public int getMinZoom() { return 10; }
    @Override public int getMaxZoom() { return 15; }
    @Override public int getTileSize() { return 256; }
    public String getTileType() { return format; }

    @Override public String getTileUrl(int zoom, int x, int y) {
        return baseUrl + getTilePath(zoom, x, y) + "." + getTileType();
    }
    
    @Override
    public String getTilePath(int zoom, int x, int y) {
        // esquema XYZ
    	return "/"+zoom + "/" + x + "/" + y;
    }

    @Override
    public String getServerCRS() {
        return "EPSG:3857"; // Web Mercator
    }

	@Override
	public TileRange getCoveringTileRange(Tile arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
            final double R = 6378137.0; // Earth radius in meters
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return R * c;
    }

    @Override
    public boolean isInside(Tile t1, Tile t2) { return t1.equals(t2); }

    @Override
    public TileXY latLonToTileXY(double lat, double lon, int zoom) {
        int xtile = (int) ((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) ((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        return new TileXY(xtile, ytile);
    }

    @Override
    public Point latLonToXY(double lat, double lon, int zoom) {
        TileXY t = latLonToTileXY(lat, lon, zoom);
        Point p = new Point();
        p.x = (int) t.getX();
        p.y = (int) t.getY();
        return p;
    }

    @Override
    public TileXY projectedToTileXY(IProjected proj, int zoom) {
        return new TileXY((int) ((TileXY) proj).getX(), (int) ((TileXY) proj).getY());
    }

    @Override
    public ICoordinate tileXYToLatLon(int x, int y, int zoom) {
        double n = Math.pow(2.0, zoom);
        double lon = x / n * 360.0 - 180.0;
        double lat = Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n))));
        final double[] latLon = new double[]{lat, lon};
        return new ICoordinate() {
            @Override public double getLat() { return latLon[0]; }
            @Override public double getLon() { return latLon[1]; }
            @Override public void setLat(double newLat) { latLon[0] = newLat; }
            @Override public void setLon(double newLon) { latLon[1] = newLon; }
        };
    }

    @Override
    public IProjected tileXYtoProjected(int x, int y, int zoom) {
        return new IProjected() {
            public double getX() { return x; }
            public double getY() { return y; }
            @Override
            public double getEast() { return x; }
            @Override
            public double getNorth() { return y; }
        };
    }
    @Override
    public ICoordinate xyToLatLon(int x, int y, int zoom) {
        return tileXYToLatLon(x, y, zoom);
    }
}