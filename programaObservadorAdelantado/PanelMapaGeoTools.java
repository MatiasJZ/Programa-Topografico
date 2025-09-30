import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.style.Style;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.collection.ListFeatureCollection;
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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;

public class PanelMapaGeoTools extends JPanel {
    private static final long serialVersionUID = 1L;
    private MapContent mapContent;
    private JMapPane mapPane;
    private ListFeatureCollection blancosCollection;
    private SimpleFeatureType layerDeBlancos;

    public PanelMapaGeoTools(String rutaArchivo) {
        setLayout(new BorderLayout());

        try {
            File file = new File(rutaArchivo);
            if (!file.exists()) {
                throw new IllegalArgumentException("No se encontró el archivo: " + rutaArchivo);
            }

            mapContent = new MapContent();
            mapContent.setTitle("Mapa táctico");

            if (rutaArchivo.toLowerCase().endsWith(".tif") || rutaArchivo.toLowerCase().endsWith(".tiff")) {
                GeoTiffFormat format = new GeoTiffFormat();
                GeoTiffReader reader = (GeoTiffReader) format.getReader(file);
                
                CoordinateReferenceSystem crs = reader.getCoordinateReferenceSystem();
                if (crs == null) {
                    crs = CRS.decode("EPSG:9265", true);
                }
                mapContent.getViewport().setCoordinateReferenceSystem(crs);
                StyleBuilder sb = new StyleBuilder();
                Style rasterStyle = sb.createStyle(sb.createRasterSymbolizer());
                mapContent.addLayer(new GridReaderLayer((AbstractGridCoverage2DReader) reader, rasterStyle));

            } else throw new IllegalArgumentException("Formato no soportado: " + rutaArchivo);

            // Crear capa editable para Blancos
            SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
            builder.setName("Blancos");
            builder.add("location", Point.class);
            builder.setCRS(mapContent.getCoordinateReferenceSystem());
            layerDeBlancos = builder.buildFeatureType();
            
            blancosCollection = new ListFeatureCollection(layerDeBlancos, new ArrayList<>());

            Style pointStyle = SLD.createPointStyle("circle", Color.BLUE, Color.BLACK, 1.0f, 14.0f);

            FeatureLayer blancosLayer = new FeatureLayer(blancosCollection, pointStyle);
            mapContent.addLayer(blancosLayer);

            // Map pane 
            mapPane = new JMapPane(mapContent);
            add(mapPane, BorderLayout.CENTER);

            // Ajustar vista
            if (mapContent.getMaxBounds() != null) {
                mapPane.setDisplayArea(mapContent.getMaxBounds());
            }

            // Herramientas de navegación
            mapPane.setCursorTool(new PanTool()); // Pan por defecto

            // Listener para clicks 
            mapPane.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Point2D worldPos = mapPane.getScreenToWorldTransform()
                                .inverseTransform(new Point2D.Double(e.getX(), e.getY()), null);

                        GeometryFactory gf = new GeometryFactory();
                        Point p = gf.createPoint(new Coordinate(worldPos.getX(), worldPos.getY()));

                        SimpleFeature feature = SimpleFeatureBuilder.build(layerDeBlancos, new Object[]{p}, null);
                        blancosCollection.add(feature);

                        mapPane.repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar mapa: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JMapPane getMapPane() {
        return mapPane;
    }

    public void agregarBlanco(Blanco b) {
        coordenadas base = b.getCoordenadas();
        coordRectangulares c = (base instanceof coordRectangulares) ? (coordRectangulares) base : ((coordPolares) base).toRectangulares();

        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(c.getX(), c.getY()));
        SimpleFeature feature = SimpleFeatureBuilder.build(layerDeBlancos, new Object[]{p}, null);
        blancosCollection.add(feature);
        mapPane.repaint();
    }


    public void eliminarBlanco(Blanco b) {
        if (!(b.getCoordenadas() instanceof coordRectangulares)) return;
        coordRectangulares c = (coordRectangulares) b.getCoordenadas();
        final double EPS = 1e-7;
        ArrayList<SimpleFeature> aBorrar = new ArrayList<>();

        try (var it = blancosCollection.features()) {
            while (it.hasNext()) {
                SimpleFeature f = it.next();
                Point p = (Point) f.getAttribute("location");
                if (Math.abs(p.getX() - c.getX()) < EPS && Math.abs(p.getY() - c.getY()) < EPS) {
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
