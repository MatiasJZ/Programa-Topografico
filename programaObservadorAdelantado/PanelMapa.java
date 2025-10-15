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
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class PanelMapa extends JPanel {

	private static final long serialVersionUID = 1L;
	private MapContent mapContent;
	private JMapPane mapPane;
	private SimpleFeatureType tipoBlancos; 
	private Map<String, ListFeatureCollection> coleccionesPorBucket = new HashMap<>();
	private Map<String, FeatureLayer> capasPorBucket = new HashMap<>();

    public PanelMapa(String rutaArchivo) {
        setLayout(new BorderLayout());
        mapContent = new MapContent();
        mapContent.setTitle("Mapa Táctico");
        try {
            leerArchivo(rutaArchivo);
        } catch (IllegalArgumentException | FactoryException | IOException e) {
            e.printStackTrace();
        }
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
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Blancos");
        builder.add("the_geom", Point.class, mapContent.getCoordinateReferenceSystem());
        builder.add("nombre", String.class);
        builder.add("naturaleza", String.class);
        builder.add("fecha", String.class);
        builder.add("aliado", Boolean.class);
        builder.setCRS(mapContent.getCoordinateReferenceSystem());

        tipoBlancos = builder.buildFeatureType();
    }
    
    private void leerArchivo(String ruta) throws IllegalArgumentException, NoSuchAuthorityCodeException, FactoryException, IOException {
        File file = new File(ruta);
        if (!file.exists()) throw new IllegalArgumentException("No se encontró el archivo: " + ruta);
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
    }

    public JMapPane getMapPane() {
        return mapPane;
    }

    public void agregarBlanco(Blanco b) {
        if (b == null) return;
        coordenadas base = b.getCoordenadas();
        coordRectangulares c = (base instanceof coordRectangulares)?(coordRectangulares) base:((coordPolares) base).toRectangulares();
        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(c.getX(), c.getY()));
        String f = (b.getForma() == null ? "círculo" : b.getForma().trim().toLowerCase());
        String formaWKN = (f.startsWith("círc") || f.startsWith("circ")) ? "circle" : (f.startsWith("cruz")) ? "cross"  : (f.startsWith("trián") || f.startsWith("trian"))? "triangle" : "circle";
        Color color = b.isAliado() ? Color.BLUE : Color.RED;
        String clave = formaWKN + (b.isAliado() ? "_azul" : "_rojo");

        ListFeatureCollection coleccion = coleccionesPorBucket.get(clave);
        if (coleccion == null) {
            coleccion = new ListFeatureCollection(tipoBlancos, new LinkedList<>());
            coleccionesPorBucket.put(clave, coleccion);
            Style estilo = SLD.createPointStyle(formaWKN, Color.WHITE, color, 1.0f, 16.0f);
            FeatureLayer capa = new FeatureLayer(coleccion, estilo);
            capa.setTitle("Blancos: " + formaWKN + (b.isAliado() ? " (azul)" : " (rojo)"));
            capasPorBucket.put(clave, capa);
            mapContent.addLayer(capa);
        }
        Object[] attrs = new Object[]{ p, b.getNombre(), b.getNaturaleza(), b.getFechaDeActualizacion(), b.isAliado() };
        String fid = "blanco-" + UUID.randomUUID();
        SimpleFeature feature = SimpleFeatureBuilder.build(tipoBlancos, attrs, fid);
        coleccion.add(feature);

        refrescarCapas();
    }

    public void eliminarBlanco(Blanco b) {
        if (b == null || !(b.getCoordenadas() instanceof coordRectangulares)) return;

        coordRectangulares c = (coordRectangulares) b.getCoordenadas();
        final double EPS = 1e-7;

        for (ListFeatureCollection col : coleccionesPorBucket.values()) {
            LinkedList<SimpleFeature> borrar = new LinkedList<>();
            try (var it = col.features()) {
                while (it.hasNext()) {
                    SimpleFeature f = it.next();
                    Point p = (Point) f.getAttribute("the_geom");
                    if (p != null &&
                        Math.abs(p.getX() - c.getX()) < EPS &&
                        Math.abs(p.getY() - c.getY()) < EPS) {
                        borrar.add(f);
                    }
                }
            } catch (Exception ignore) {}
            for (SimpleFeature f : borrar) col.remove(f);
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

        ImageIcon icono = new ImageIcon(getClass().getResource("/arrastre.png"));
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
