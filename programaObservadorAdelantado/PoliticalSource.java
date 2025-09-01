import org.openstreetmap.gui.jmapviewer.tilesources.AbstractOsmTileSource;

public class PoliticalSource extends AbstractOsmTileSource {
    private static final String BASE_URL = "https://tile.openstreetmap.org";

    public PoliticalSource() {
        super("OSM Political", BASE_URL, "osm_political");
    }

    @Override
    public int getMaxZoom() { return 19; }

    @Override
    public int getMinZoom() { return 1; }

    @Override
    public String getTilePath(int zoom, int tilex, int tiley) {
        return String.format("%s/%d/%d/%d.png", this.baseUrl, zoom, tilex, tiley);
    }
}