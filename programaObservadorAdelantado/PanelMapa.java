import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.style.ExternalGraphic;
import org.geotools.api.style.FeatureTypeStyle;
import org.geotools.api.style.Graphic;
import org.geotools.api.style.PointSymbolizer;
import org.geotools.api.style.Rule;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleFactory;
import org.geotools.api.style.TextSymbolizer;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.styling.SLD;
import org.geotools.styling.StyleBuilder;
import org.geotools.swing.JMapPane;
import org.geotools.swing.tool.ZoomInTool;
import org.geotools.swing.tool.CursorTool;
import org.geotools.swing.tool.PanTool;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class PanelMapa extends JPanel {

	private static final long serialVersionUID = 1L;
	private MapContent mapContent;
	private JMapPane mapPane;
	private SimpleFeatureType tipoBlancos;
	private SimpleFeatureType tipoPuntos; 
	private Map<String, ListFeatureCollection> coleccionesPorBucket = new HashMap<>();
	private Map<String, FeatureLayer> capasPorBucket = new HashMap<>();
	private final Map<String, Style> estilosPorSIDC = new HashMap<>();

    public PanelMapa(String rutaArchivo) {
    	
        setLayout(new BorderLayout());
        mapContent = new MapContent();
        mapContent.setTitle("Mapa Táctico");
        
        try {
            leerArchivo(rutaArchivo);
        } catch (IllegalArgumentException | FactoryException | IOException e) {e.printStackTrace();}
        
        mapPane = new JMapPane(mapContent);
        add(mapPane, BorderLayout.CENTER);
        
        if (mapContent.getMaxBounds() != null) {
            mapPane.setDisplayArea(mapContent.getMaxBounds());
        }
        
        mapPane.setCursorTool(new ZoomInTool());
        mapPane.setOpaque(true);
        mapPane.setBackground(Color.BLACK);
        
        crearLayerDeBlancos();
        agregarControlesDeZoom();
    }

    private void crearLayerDeBlancos() {
    	
        // Tipo para blancos 
        SimpleFeatureTypeBuilder b1 = new SimpleFeatureTypeBuilder();
        b1.setName("Blanco");
        b1.add("the_geom", Point.class, mapContent.getCoordinateReferenceSystem());
        b1.add("nombre", String.class);
        b1.add("naturaleza", String.class);
        b1.add("fechaCreacion", String.class);
        b1.add("x", Double.class);
        b1.add("y", Double.class);
        b1.setCRS(mapContent.getCoordinateReferenceSystem());
        tipoBlancos = b1.buildFeatureType();

        // Tipo para puntos 
        SimpleFeatureTypeBuilder b2 = new SimpleFeatureTypeBuilder();
        b2.setName("Punto");
        b2.add("the_geom", Point.class, mapContent.getCoordinateReferenceSystem());
        b2.add("nombre", String.class);
        b2.add("x", Double.class);
        b2.add("y", Double.class);
        b2.setCRS(mapContent.getCoordinateReferenceSystem());
        tipoPuntos = b2.buildFeatureType();
    }
    
    private void leerArchivo(String ruta) throws IllegalArgumentException, NoSuchAuthorityCodeException, FactoryException, IOException {
        File file = new File(ruta);
        if (!file.exists()) throw new IllegalArgumentException("No se encontró el archivo: " + ruta);
        GeoTiffFormat format = new GeoTiffFormat();
        GeoTiffReader reader = (GeoTiffReader) format.getReader(file);
        CoordinateReferenceSystem crs = reader.getCoordinateReferenceSystem();
        if (crs == null) crs = CRS.decode("EPSG:9265", true);
        mapContent.getViewport().setCoordinateReferenceSystem(crs);

        StyleBuilder sb = new StyleBuilder();
        Style rasterStyle = sb.createStyle(sb.createRasterSymbolizer());
        mapContent.addLayer(new GridReaderLayer((AbstractGridCoverage2DReader) reader, rasterStyle));

        System.out.println("[TIFF] Archivo cargado: " + ruta);
    }

    public JMapPane getMapPane() {
        return mapPane;
    }

    public void agregarPunto(Punto pto) {
        if (pto == null) return;
        coordenadas base = pto.getCoord();
        coordRectangulares c = (base instanceof coordRectangulares)? (coordRectangulares) base: ((coordPolares) base).toRectangulares();
        GeometryFactory gf = new GeometryFactory();
        Point geom = gf.createPoint(new Coordinate(c.getX(), c.getY()));
        String clave = "puntos";
        ListFeatureCollection coleccion = coleccionesPorBucket.get(clave);

        if (coleccion == null) {
            coleccion = new ListFeatureCollection(tipoPuntos, new LinkedList<>());
            coleccionesPorBucket.put(clave, coleccion);
            // color negro con borde blanco
            Style estilo = SLD.createPointStyle("circle", Color.WHITE, Color.BLACK, 1.0f, 12.0f);
            FeatureLayer capa = new FeatureLayer(coleccion, estilo);
            capa.setTitle("Puntos");
            capasPorBucket.put(clave, capa);
            mapContent.addLayer(capa);
        }
        Object[] attrs = new Object[]{ geom, pto.getNombre(), c.getX(), c.getY() };
        String fid = "punto-" + UUID.randomUUID();
        SimpleFeature feature = SimpleFeatureBuilder.build(tipoPuntos, attrs, fid);
        coleccion.add(feature);

        refrescarCapas();
    }

    public void agregarBlanco(Blanco b) {
        if (b == null) return;

        coordenadas base = b.getCoordenadas();
        coordRectangulares c = (base instanceof coordRectangulares)
                ? (coordRectangulares) base
                : ((coordPolares) base).toRectangulares();

        GeometryFactory gf = new GeometryFactory();
        Point geom = gf.createPoint(new Coordinate(c.getX(), c.getY()));

        String sidc = b.getSimID();
        if (sidc == null || sidc.isEmpty()) {
            sidc = CodigosMilitares.obtenerSIDC(b.getNaturaleza());
            b.setSimID(sidc);
        }

        String clave = "blancos_" + sidc;
        ListFeatureCollection coleccion = coleccionesPorBucket.get(clave);

        if (coleccion == null) {
            coleccion = new ListFeatureCollection(tipoBlancos, new LinkedList<>());
            coleccionesPorBucket.put(clave, coleccion);

            Style estiloSimbolo = null;

            try {
                proveedorMilSym prov = new proveedorMilSym(sidc, 70);
                Field f = proveedorMilSym.class.getDeclaredField("simbolo");
                f.setAccessible(true);
                BufferedImage simbolo = (BufferedImage) f.get(prov);

                if (simbolo != null) {
                    File tempFile = File.createTempFile("milSym_" + sidc, ".png");
                    ImageIO.write(simbolo, "png", tempFile);
                    tempFile.deleteOnExit();
                    URL imageURL = tempFile.toURI().toURL();

                    StyleBuilder sb = new StyleBuilder();
                    ExternalGraphic eg = sb.createExternalGraphic(imageURL, "image/png");

                    double rotacionGrados = b.getOrientacion() * 0.05625;

                    Graphic graphic = sb.createGraphic(new ExternalGraphic[]{eg}, null, null, 1.0, 0.0, rotacionGrados);

                    PointSymbolizer ps = sb.createPointSymbolizer(graphic);

                    TextSymbolizer ts = sb.createTextSymbolizer(Color.BLACK,sb.createFont("Arial Black", false, false, 18),"nombre");

                    StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
                    FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
                    Rule rule = styleFactory.createRule();
                    rule.symbolizers().add(ps);
                    rule.symbolizers().add(ts);
                    fts.rules().add(rule);

                    estiloSimbolo = styleFactory.createStyle();
                    estiloSimbolo.featureTypeStyles().add(fts);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (estiloSimbolo == null)
                estiloSimbolo = SLD.createPointStyle("circle", Color.WHITE, Color.RED, 1.0f, 14.0f);

            FeatureLayer capa = new FeatureLayer(coleccion, estiloSimbolo);
            capa.setTitle("Blancos " + b.getNaturaleza());
            capasPorBucket.put(clave, capa);

            mapContent.addLayer(capa); // se añade luego del raster, visible
        }

        Object[] attrs = new Object[]{
                geom,
                b.getNombre(),
                b.getNaturaleza(),
                b.getFechaDeActualizacion(),
                c.getX(),
                c.getY()
        };
        String fid = "blanco-" + UUID.randomUUID();
        SimpleFeature feature = SimpleFeatureBuilder.build(tipoBlancos, attrs, fid);
        coleccion.add(feature);

        refrescarCapas();
    }

    public void eliminarPunto(Punto p) {
    	
        if (p == null || p.getCoord() == null) return;
        coordRectangulares c = (p.getCoord() instanceof coordRectangulares)
                ? (coordRectangulares) p.getCoord()
                : ((coordPolares) p.getCoord()).toRectangulares();
        final double EPS = 1e-7;
        ListFeatureCollection col = coleccionesPorBucket.get("puntos");
        if (col == null) return;

        LinkedList<SimpleFeature> borrar = new LinkedList<>();
        try (var it = col.features()) {
            while (it.hasNext()) {
                SimpleFeature f = it.next();
                Point geom = (Point) f.getAttribute("the_geom");
                if (geom != null &&
                    Math.abs(geom.getX() - c.getX()) < EPS &&
                    Math.abs(geom.getY() - c.getY()) < EPS) {
                    borrar.add(f);
                }
            }
        } catch (Exception ignore) {}

        for (SimpleFeature f : borrar) col.remove(f);
        refrescarCapas();
    }
    
    public void eliminarBlanco(Blanco b) {   	
        eliminarBlanco(b, b != null ? b.getSimID() : null);
    }

    public void eliminarBlanco(Blanco b, String sidcReferencia) {
        if (b == null || b.getCoordenadas() == null) return;
        coordRectangulares c = (b.getCoordenadas() instanceof coordRectangulares)
                ? (coordRectangulares) b.getCoordenadas()
                : ((coordPolares) b.getCoordenadas()).toRectangulares();

        // determinar la capa correspondiente 
        String sidc = (sidcReferencia != null && !sidcReferencia.isEmpty())
                ? sidcReferencia
                : b.getSimID();

        if (sidc == null || sidc.isEmpty()) {
            sidc = CodigosMilitares.obtenerSIDC(b.getNaturaleza());
            b.setSimID(sidc);
        }

        String clave = "blancos_" + sidc;
        ListFeatureCollection coleccion = coleccionesPorBucket.get(clave);
        if (coleccion == null) return;

        final double EPS = 1e-6;
        LinkedList<SimpleFeature> borrar = new LinkedList<>();

        // buscar el feature por coordenadas 
        try (var it = coleccion.features()) {
            while (it.hasNext()) {
                SimpleFeature f = it.next();
                Point p = (Point) f.getAttribute("the_geom");
                if (p != null &&
                    Math.abs(p.getX() - c.getX()) < EPS &&
                    Math.abs(p.getY() - c.getY()) < EPS) {
                    borrar.add(f);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (SimpleFeature f : borrar) {
            coleccion.remove(f);
        }
        if (coleccion.isEmpty()) {
            FeatureLayer capa = capasPorBucket.remove(clave);
            coleccionesPorBucket.remove(clave);
            if (capa != null) {
                mapContent.removeLayer(capa);
            }
            estilosPorSIDC.remove(sidc);
        }
        
        refrescarCapas();
    }
    
    public void refrescarCapas() {
    	
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refrescarCapas);
            return;
        }
        for (FeatureLayer capa : capasPorBucket.values()) {
            if (!capa.isVisible()) capa.setVisible(true);
        }
        mapPane.setDisplayArea(mapPane.getDisplayArea());
        mapPane.repaint();
    }

    public void dispose() {
        if (mapContent != null) mapContent.dispose();
    }
    
    private void agregarControlesDeZoom() {
    	
        JLayeredPane capa = new JLayeredPane();
        setLayout(new BorderLayout());
        add(capa, BorderLayout.CENTER);

        capa.add(mapPane, JLayeredPane.DEFAULT_LAYER);

        JPanel overlay = new JPanel(null);
        overlay.setOpaque(false);
        capa.add(overlay, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mapPane.setBounds(0, 0, getWidth(), getHeight());
                overlay.setBounds(0, 0, getWidth(), getHeight());
            }
        });
        int size = 42;
        int margin = 10;
        
        JButton btnZoomIn = new JButton("+");
        JButton btnZoomOut = new JButton("-");
        JButton btnPan = new JButton();

        ImageIcon icono = new ImageIcon(getClass().getResource("/arrastrar.png"));
        Image imgEscalada = icono.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        btnPan.setIcon(new ImageIcon(imgEscalada));

        JButton[] botones = {btnZoomIn, btnZoomOut, btnPan};
        for (int i = 0; i < botones.length; i++) {
        	JButton b = botones[i];
            b.setBounds(margin, margin + i * (size + 8), size, size);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(true);
            b.setOpaque(true);
            b.setBackground(new Color(25, 25, 25));
            b.setForeground(Color.WHITE);
            overlay.add(b);
        }
        PanTool panTool = new PanTool();
        boolean[] modoPan = {false};
        CursorTool[] anterior = {null};
        btnPan.addActionListener(e -> {
            if (!modoPan[0]) {
                anterior[0] = mapPane.getCursorTool();
                mapPane.setCursorTool(panTool);
                mapPane.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                btnPan.setBackground(new Color(60, 160, 60, 180));
                modoPan[0] = true;
            } else {
                mapPane.setCursorTool(anterior[0]);
                mapPane.setCursor(Cursor.getDefaultCursor());
                btnPan.setBackground(new Color(0, 0, 0, 150));
                modoPan[0] = false;
            }
        });
        ActionListener zoom = e -> {
            boolean in = e.getSource() == btnZoomIn;
            double factor = in ? 0.5 : 2.0;
            ReferencedEnvelope view = mapPane.getDisplayArea();
            ReferencedEnvelope bounds = mapContent.getMaxBounds();
            double cx = view.getMedian(0);
            double cy = view.getMedian(1);
            double w = view.getWidth() * factor;
            double h = view.getHeight() * factor;
            double minX = cx - w / 2;
            double maxX = cx + w / 2;
            double minY = cy - h / 2;
            double maxY = cy + h / 2;
            ReferencedEnvelope nueva = new ReferencedEnvelope(minX, maxX, minY, maxY, view.getCoordinateReferenceSystem());

            if (bounds != null) {
                if (nueva.getWidth() > bounds.getWidth() || nueva.getHeight() > bounds.getHeight()) {
                    nueva = bounds;
                } else {
                    if (nueva.getMinX() < bounds.getMinX()) nueva.translate(bounds.getMinX() - nueva.getMinX(), 0);
                    if (nueva.getMaxX() > bounds.getMaxX()) nueva.translate(bounds.getMaxX() - nueva.getMaxX(), 0);
                    if (nueva.getMinY() < bounds.getMinY()) nueva.translate(0, bounds.getMinY() - nueva.getMinY());
                    if (nueva.getMaxY() > bounds.getMaxY()) nueva.translate(0, bounds.getMaxY() - nueva.getMaxY());
                }
            }
            mapPane.setDisplayArea(nueva);
        };
        btnZoomIn.addActionListener(zoom);
        btnZoomOut.addActionListener(zoom);
        overlay.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                for (int i = 0; i < botones.length; i++) {
                    botones[i].setBounds(margin, margin + i * (size + 8), size, size);
                }
            }
        });
    }
}
