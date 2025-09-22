import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.style.Style;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.styling.SLD;
import org.geotools.styling.StyleBuilder;
import org.geotools.swing.JMapPane;
import org.geotools.swing.tool.PanTool;
import org.geotools.swing.tool.ZoomInTool;
import org.geotools.swing.tool.ZoomOutTool;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PanelMapaGeoTools extends JPanel {
    private static final long serialVersionUID = 1L;
    private MapContent mapContent;
    private JMapPane mapPane;
    private ListFeatureCollection blancosCollection;
    private SimpleFeatureType blancoType;

    public PanelMapaGeoTools(String rutaArchivo) {
        setLayout(new BorderLayout());

        try {
            if (!rutaArchivo.toLowerCase().endsWith(".tif") &&
                !rutaArchivo.toLowerCase().endsWith(".tiff")) {
                throw new IllegalArgumentException("El archivo no es un GeoTIFF válido: " + rutaArchivo);
            }

            File file = new File(rutaArchivo);
            if (!file.exists()) {
                throw new IllegalArgumentException("No se encontró el archivo: " + rutaArchivo);
            }

            mapContent = new MapContent();
            mapContent.setTitle("Mapa táctico");

            // 1) Cargar GeoTIFF
            GeoTiffFormat format = new GeoTiffFormat();
            GeoTiffReader reader = (GeoTiffReader) format.getReader(file);

            // Si el TIFF no tiene CRS definido correctamente, forzar EPSG:9265
            CoordinateReferenceSystem crs = reader.getCoordinateReferenceSystem();
            if (crs == null) {
                crs = CRS.decode("EPSG:9265", true); // POSGAR 2007 / UTM 19S
            }
            mapContent.getViewport().setCoordinateReferenceSystem(crs);

            StyleBuilder sb = new StyleBuilder();
            Style rasterStyle = sb.createStyle(sb.createRasterSymbolizer());
            mapContent.addLayer(new GridReaderLayer((AbstractGridCoverage2DReader) reader, rasterStyle));

            // 2) Crear capa editable para "Blancos"
            SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
            builder.setName("Blancos");
            builder.add("location", Point.class);
            blancoType = builder.buildFeatureType();

            blancosCollection = new ListFeatureCollection(blancoType, new ArrayList<>());
            Style pointStyle = SLD.createPointStyle("circle", Color.RED, Color.BLACK, 1.0f, 18.0f);
            FeatureLayer blancosLayer = new FeatureLayer(blancosCollection, pointStyle);
            mapContent.addLayer(blancosLayer);

            // 3) Map pane
            mapPane = new JMapPane(mapContent);
            add(mapPane, BorderLayout.CENTER);

            // Ajustar vista al raster completo
            mapPane.setDisplayArea(mapContent.getMaxBounds());

            // Herramientas de navegación disponibles
            mapPane.setCursorTool(new PanTool()); // por defecto Pan
            // Podés cambiar con: mapPane.setCursorTool(new ZoomInTool());
            // o: mapPane.setCursorTool(new ZoomOutTool());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar GeoTIFF: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JMapPane getMapPane() {
        return mapPane;
    }

    // API de Blancos
    public void agregarBlanco(Blanco b) {
        if (!(b.getCoordenadas() instanceof coordRectangulares)) return;
        coordRectangulares c = (coordRectangulares) b.getCoordenadas();

        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(c.getX(), c.getY()));

        SimpleFeature feature = SimpleFeatureBuilder.build(blancoType, new Object[]{p}, null);
        blancosCollection.add(feature);
        mapPane.repaint();
    }

    public void eliminarBlanco(Blanco b) {
        if (!(b.getCoordenadas() instanceof coordRectangulares)) return;
        coordRectangulares c = (coordRectangulares) b.getCoordenadas();
        eliminarPorCoordenadas(c.getX(), c.getY());
    }

    public void agregarBlanco(double x, double y) {
        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(x, y));
        SimpleFeature feature = SimpleFeatureBuilder.build(blancoType, new Object[]{p}, null);
        blancosCollection.add(feature);
        mapPane.repaint();
    }

    public void eliminarBlanco(double x, double y) {
        eliminarPorCoordenadas(x, y);
    }

    private void eliminarPorCoordenadas(double x, double y) {
        final double EPS = 1e-7;
        List<SimpleFeature> aBorrar = new ArrayList<>();

        try (FeatureIterator<SimpleFeature> it = blancosCollection.features()) {
            while (it.hasNext()) {
                SimpleFeature f = it.next();
                Point p = (Point) f.getAttribute("location");
                if (Math.abs(p.getX() - x) < EPS && Math.abs(p.getY() - y) < EPS) {
                    aBorrar.add(f);
                }
            }
        }
        for (SimpleFeature f : aBorrar) {
            blancosCollection.remove(f);
        }
        mapPane.repaint();
    }

    public void dispose() {
        if (mapContent != null) {
            mapContent.dispose();
        }
    }
}
