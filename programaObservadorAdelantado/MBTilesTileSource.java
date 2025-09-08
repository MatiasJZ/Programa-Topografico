import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.TileRange;
import org.openstreetmap.gui.jmapviewer.TileXY;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.IProjected;
import org.openstreetmap.gui.jmapviewer.tilesources.AbstractTileSource;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class MBTilesTileSource extends AbstractTileSource {

    private Connection conn;
    private int minZoom = 12;
    private int maxZoom = 15;
    private String id = "MBTiles";
    private String name = "MBTiles Local";

    public MBTilesTileSource(File mbtilesFile) throws SQLException {
        super();
        String url = "jdbc:sqlite:" + mbtilesFile.getAbsolutePath();
        conn = DriverManager.getConnection(url);
    }

    @Override
    public String getTileUrl(int zoom, int tilex, int tiley) {
        return ""; // no usamos URL
    }

    public BufferedImage getTileImage(int zoom, int tilex, int tiley) {
        try {
            int tmsY = (int) (Math.pow(2, zoom) - 1 - tiley);
            String sql = "SELECT tile_data FROM tiles WHERE zoom_level=? AND tile_column=? AND tile_row=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, zoom);
            stmt.setInt(2, tilex);
            stmt.setInt(3, tmsY);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                byte[] data = rs.getBytes("tile_data");
                return ImageIO.read(new ByteArrayInputStream(data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TileRange getCoveringTileRange(Tile tile, int zoom) {
        // Retorna un rango mínimo: desde 0 hasta 0 en X y Y
        return new TileRange(new TileXY(0, 0), new TileXY(0, 0), zoom);
    }

    @Override
    public int getDefaultTileSize() {
        return 256;
    }

    @Override
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        // aproximación simple (no geodésica)
        double dx = lon1 - lon2;
        double dy = lat1 - lat2;
        return Math.sqrt(dx*dx + dy*dy);
    }

    @Override
    public String getId() { return id; }

    @Override
    public int getMaxZoom() { return maxZoom; }

    @Override
    public Map<String, String> getMetadata(Map<String, List<String>> arg0) {
        return new HashMap<>();
    }

    @Override
    public int getMinZoom() { return minZoom; }

    @Override
    public String getName() { return name; }

    @Override
    public String getServerCRS() {
        return "EPSG:3857"; // web mercator
    }

    @Override
    public String getTileId(int zoom, int x, int y) {
        return zoom + "/" + x + "/" + y;
    }

    @Override
    public int getTileSize() { return 256; }

    @Override
    public int getTileXMax(int zoom) { return (1 << zoom) - 1; }

    @Override
    public int getTileXMin(int zoom) { return 0; }

    @Override
    public int getTileYMax(int zoom) { return (1 << zoom) - 1; }

    @Override
    public int getTileYMin(int zoom) { return 0; }

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
        return new ICoordinate() {
            @Override public double getLat() { return lat; }
            @Override public double getLon() { return lon; }
			@Override
			public void setLat(double arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void setLon(double arg0) {
				// TODO Auto-generated method stub
				
			}
        };
    }

    @Override
    public IProjected tileXYtoProjected(int x, int y, int zoom) {
        return new IProjected() {
            public double getX() { return x; }
            public double getY() { return y; }
			@Override
			public double getEast() {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public double getNorth() {
				// TODO Auto-generated method stub
				return 0;
			}
        };
    }

    @Override
    public ICoordinate xyToLatLon(int x, int y, int zoom) {
        return tileXYToLatLon(x, y, zoom);
    }
}