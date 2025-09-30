import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.style.Style;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
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
import org.geotools.swing.tool.ZoomInTool;
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

            mapContent = new MapContent();
            mapContent.setTitle("Mapa Táctico");
            
            try {
				leerTIFF(rutaArchivo);
			} catch (IllegalArgumentException | FactoryException e) {e.printStackTrace();}

            // Crear capa editable para Blancos
            crearLayerDeBlancos();

            // MapPane 
            mapPane = new JMapPane(mapContent);
            add(mapPane, BorderLayout.CENTER);

            // Ajustar vista
            if (mapContent.getMaxBounds() != null) {
                mapPane.setDisplayArea(mapContent.getMaxBounds());
            }

            mapPane.setCursorTool(new ZoomInTool());
            
            crearListenerParaClick();
    }
    
    private void crearLayerDeBlancos() {
    	
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Blancos");
        builder.add("the_geom", Point.class, mapContent.getCoordinateReferenceSystem());
        
        //seteo el sistema coordinado de referencia
        builder.setCRS(mapContent.getCoordinateReferenceSystem());
        
        layerDeBlancos = builder.buildFeatureType();
        blancosCollection = new ListFeatureCollection(layerDeBlancos, new LinkedList<>());
        
        //estilo del punto de marca
        Style pointStyle = SLD.createPointStyle("circle", Color.BLUE, Color.BLACK, 1.0f, 14.0f);
        FeatureLayer blancosLayer = new FeatureLayer(blancosCollection, pointStyle);
        
        mapContent.addLayer(blancosLayer);
    }
    
    private void crearListenerParaClick() {
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
    }
    
    private void leerTIFF(String ruta) throws IllegalArgumentException, NoSuchAuthorityCodeException, FactoryException{
            File file = new File(ruta);
            if (!file.exists()) {
                throw new IllegalArgumentException("No se encontró el archivo: " + ruta);
            }
    	if (ruta.toLowerCase().endsWith(".tif") || ruta.toLowerCase().endsWith(".tiff")) {
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

        } else throw new IllegalArgumentException("Formato no soportado: " + ruta);
    }

    public JMapPane getMapPane() {
        return mapPane;
    }

    public void agregarBlanco(Blanco b) {
        coordenadas base = b.getCoordenadas();
        coordRectangulares c = (base instanceof coordRectangulares) ? (coordRectangulares) base : ((coordPolares) base).toRectangulares();

        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(c.getX(), c.getY()));
        SimpleFeature feature = SimpleFeatureBuilder.build(layerDeBlancos, new Object[]{p}, b.getNombre());
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
