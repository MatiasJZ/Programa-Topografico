import org.openstreetmap.gui.jmapviewer.tilesources.AbstractOsmTileSource;

public class SatelliteSource extends AbstractOsmTileSource {
    private static final String BASE_URL = "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile";

    public SatelliteSource() {
        super("Esri Satellite", BASE_URL, "esri_satellite");
    }

    @Override
    public int getMaxZoom() {
        return 19; // Esri soporta zoom alto
    }

    @Override
    public int getMinZoom() {
        return 1;
    }

    @Override
    public String getTilePath(int zoom, int tilex, int tiley) {
        return String.format("%s/%d/%d/%d.jpg", this.baseUrl, zoom, tiley, tilex);
    }
}