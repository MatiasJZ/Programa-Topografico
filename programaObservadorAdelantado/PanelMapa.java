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
import org.geotools.geopkg.GeoPackage;
import org.geotools.geopkg.mosaic.GeoPackageReader;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.styling.SLD;
import org.geotools.styling.StyleBuilder;
import org.geotools.swing.JMapPane;
import org.geotools.swing.tool.ZoomInTool;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PanelMapa extends JPanel {
    private static final long serialVersionUID = 1L;
    private MapContent mapContent;
    private JMapPane mapPane;
    private ListFeatureCollection blancosCollection;
    private SimpleFeatureType layerDeBlancos;
    private FeatureLayer blancosLayer;

    public PanelMapa(String rutaArchivo) {
        setLayout(new BorderLayout());
        mapContent = new MapContent();
        mapContent.setTitle("Mapa Táctico");

        try {
            leerArchivo(rutaArchivo);
        } catch (IllegalArgumentException | FactoryException | IOException  e) {
            e.printStackTrace();
        }

        crearLayerDeBlancos();

        mapPane = new JMapPane(mapContent);
        add(mapPane, BorderLayout.CENTER);

        if (mapContent.getMaxBounds() != null) {
            mapPane.setDisplayArea(mapContent.getMaxBounds());
        }

        mapPane.setCursorTool(new ZoomInTool());
    }

    private void crearLayerDeBlancos() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Blancos");
        builder.add("the_geom", Point.class, mapContent.getCoordinateReferenceSystem());
        builder.add("nombre", String.class);
        builder.add("naturaleza", String.class);
        builder.add("fecha", String.class);
        builder.add("aliado", Boolean.class);
        builder.setCRS(mapContent.getCoordinateReferenceSystem());

        layerDeBlancos = builder.buildFeatureType();
        blancosCollection = new ListFeatureCollection(layerDeBlancos, new LinkedList<>());

        Style pointStyle = SLD.createPointStyle("cross", Color.WHITE, new Color(0,123,255), 1.0f, 18.0f); ///////////////////////////////////////////////////////////////
        blancosLayer = new FeatureLayer(blancosCollection, pointStyle);
        blancosLayer.setTitle("Blancos");

        mapContent.addLayer(blancosLayer);
        System.out.println("[MAPA] Capa de blancos creada correctamente.");
    }

    private void leerArchivo(String ruta) throws IllegalArgumentException, NoSuchAuthorityCodeException, FactoryException, IOException {
        File file = new File(ruta);
        if (!file.exists()) throw new IllegalArgumentException("No se encontró el archivo: " + ruta);
        
        String extension = ruta.toLowerCase();
        
        if (extension.endsWith(".tif") || extension.endsWith(".tiff")) {
            GeoTiffFormat format = new GeoTiffFormat();
            GeoTiffReader reader = (GeoTiffReader) format.getReader(file);
            CoordinateReferenceSystem crs = reader.getCoordinateReferenceSystem();
            if (crs == null) 
            	crs = CRS.decode("EPSG:9265", true);
            mapContent.getViewport().setCoordinateReferenceSystem(crs);

            StyleBuilder sb = new StyleBuilder();
            Style rasterStyle = sb.createStyle(sb.createRasterSymbolizer());		
            mapContent.addLayer(new GridReaderLayer((AbstractGridCoverage2DReader) reader, rasterStyle));

            System.out.println("[TIFF] Archivo cargado: " + ruta);
        } else {
        	if(extension.endsWith(".gpkg")) {
        		System.out.println("[GEOPACKAGE] Cargando GeoPackage: " + ruta);

                GeoPackage geoPackage = new GeoPackage(file);
                geoPackage.init();

                GeoPackageReader gpkgReader = new GeoPackageReader(file, null);

                CoordinateReferenceSystem crs = CRS.decode("EPSG:5347", true);
                mapContent.getViewport().setCoordinateReferenceSystem(crs);
                
                StyleBuilder sb = new StyleBuilder();
                Style rasterStyle = sb.createStyle(sb.createRasterSymbolizer());
                GridReaderLayer gpkgLayer = new GridReaderLayer(gpkgReader, rasterStyle);
                mapContent.addLayer(gpkgLayer);
                
                System.out.println("[GEOPACKAGE] GeoPackage raster agregado al mapa.");

                geoPackage.close();

        	}
        	else throw new IllegalArgumentException("Formato no soportado: " + ruta);
        }
    }

    public JMapPane getMapPane() {
        return mapPane;
    }

    public void agregarBlanco(Blanco b) {
        if (b == null) return;

        coordenadas base = b.getCoordenadas();
        coordRectangulares c = (base instanceof coordRectangulares)
                ? (coordRectangulares) base
                : ((coordPolares) base).toRectangulares();

        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(c.getX(), c.getY()));

        Object[] atributos = {
                p,
                b.getNombre(),
                b.getNaturaleza(),
                b.getFechaDeActualizacion(),
                b.isAliado()
        };

        String fid = "blanco-" + b.hashCode();
        SimpleFeature feature = SimpleFeatureBuilder.build(layerDeBlancos, atributos, fid);

        blancosCollection.removeIf(f -> f.getID().equals(fid));
        blancosCollection.add(feature);

        System.out.println("[AGREGAR BLANCO] " + b.getNombre() + " agregado en (" + c.getX() + ", " + c.getY() + ")");
        System.out.println("Total de blancos: " + blancosCollection.size());

        refrescarCapas();
    }

    public void eliminarBlanco(Blanco b) {
        if (b == null || !(b.getCoordenadas() instanceof coordRectangulares)) return;

        coordRectangulares c = (coordRectangulares) b.getCoordenadas();
        final double EPS = 1e-7;
        LinkedList<SimpleFeature> aBorrar = new LinkedList<>();

        try (var it = blancosCollection.features()) {
            while (it.hasNext()) {
                SimpleFeature f = it.next();
                Point p = (Point) f.getAttribute("the_geom");
                if (p != null &&
                        Math.abs(p.getX() - c.getX()) < EPS &&
                        Math.abs(p.getY() - c.getY()) < EPS) {
                    aBorrar.add(f);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (SimpleFeature f : aBorrar) blancosCollection.remove(f);
        System.out.println("[ELIMINAR BLANCO] Eliminados: " + aBorrar.size());

        refrescarCapas();
    }

    public void refrescarCapas() {
        mapContent.layers().forEach(layer -> {
            if (layer instanceof FeatureLayer) {
                FeatureLayer fl = (FeatureLayer) layer;
                fl.setVisible(false);
                fl.setVisible(true);
            }
        });
        mapPane.repaint();
    }

    public void dispose() {
        if (mapContent != null) mapContent.dispose();
    }
}
