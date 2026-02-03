package interfaz;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.style.ExternalGraphic;
import org.geotools.api.style.FeatureTypeStyle;
import org.geotools.api.style.Graphic;
import org.geotools.api.style.LineSymbolizer;
import org.geotools.api.style.PointSymbolizer;
import org.geotools.api.style.Rule;
import org.geotools.api.style.Stroke;
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
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import dominio.Blanco;
import dominio.CodigosMilitares;
import dominio.Linea;
import dominio.coordRectangulares;
import dominio.poligonal;
import milsymb.proveedorMilSym;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PanelMapa extends JPanel {

	private static final long serialVersionUID = -6801957143279809848L;
	private MapContent mapContent;
    private JMapPane mapPane;
    
    private SimpleFeatureType tipoBlancos;
    private SimpleFeatureType tipoPuntos;
    private SimpleFeatureType tipoLineas;

    private final Map<poligonal, FeatureLayer> capaPorPoligonal = new HashMap<>();
    private final Map<poligonal, ListFeatureCollection> coleccionPorPoligonal = new HashMap<>();

    private final Map<Blanco, FeatureLayer> capaPorBlanco = new HashMap<>();
    private final Map<Blanco, ListFeatureCollection> coleccionPorBlanco = new HashMap<>();

    public PanelMapa(String rutaArchivo) {

        setLayout(new BorderLayout());
        mapContent = new MapContent();
        mapContent.setTitle("Mapa Táctico");

        try {
            leerArchivo(rutaArchivo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapPane = new JMapPane(mapContent);
        add(mapPane, BorderLayout.CENTER);

        if (mapContent.getMaxBounds() != null)
            mapPane.setDisplayArea(mapContent.getMaxBounds());

        mapPane.setCursorTool(new ZoomInTool());
        mapPane.setOpaque(true);
        mapPane.setBackground(Color.BLACK);

        crearTypes();
        agregarControlesDeZoom();
    }
    
    private void crearTypes() {

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

        SimpleFeatureTypeBuilder b2 = new SimpleFeatureTypeBuilder();
        b2.setName("Punto");
        b2.add("the_geom", Point.class, mapContent.getCoordinateReferenceSystem());
        b2.add("nombre", String.class);
        b2.add("x", Double.class);
        b2.add("y", Double.class);
        b2.setCRS(mapContent.getCoordinateReferenceSystem());
        tipoPuntos = b2.buildFeatureType();
        
        SimpleFeatureTypeBuilder b3 = new SimpleFeatureTypeBuilder();
        b3.setName("Linea");
        b3.add("the_geom", LineString.class, mapContent.getCoordinateReferenceSystem());
        b3.add("nombre", String.class);
        b3.add("distancia", String.class);
        tipoLineas = b3.buildFeatureType();
    }
    
    private void leerArchivo(String ruta) throws IllegalArgumentException, NoSuchAuthorityCodeException, FactoryException, IOException {

        File file = new File(ruta);
        GeoTiffFormat format = new GeoTiffFormat();
        GeoTiffReader reader = (GeoTiffReader) format.getReader(file);

        CoordinateReferenceSystem crs = reader.getCoordinateReferenceSystem();
        if (crs == null) crs = CRS.decode("EPSG:9265", true);

        mapContent.getViewport().setCoordinateReferenceSystem(crs);

        StyleBuilder sb = new StyleBuilder();
        Style rasterStyle = sb.createStyle(sb.createRasterSymbolizer());

        mapContent.addLayer(new GridReaderLayer((AbstractGridCoverage2DReader) reader, rasterStyle));
    }

    public JPanel crearVistaSoloObservacion() {
        // Crea un segundo JMapPane que comparte el mismo MapContent (misma vista/capas)
        JMapPane mapPaneSoloObs = new JMapPane(mapContent);
        mapPaneSoloObs.setOpaque(true);
        mapPaneSoloObs.setBackground(Color.BLACK);

        // CursorTool "inofensivo": no hace nada en clicks
        CursorTool noClickTool = new CursorTool() {};
        mapPaneSoloObs.setCursorTool(noClickTool);

        // Contenedor en capas con overlay de controles (zoom y pan) propio de esta vista
        JLayeredPane capa = new JLayeredPane();
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.BLACK);
        wrapper.add(capa, BorderLayout.CENTER);

        // map al fondo
        capa.add(mapPaneSoloObs, JLayeredPane.DEFAULT_LAYER);

        // overlay con botones
        JPanel overlay = new JPanel(null);
        overlay.setOpaque(false);
        capa.add(overlay, JLayeredPane.PALETTE_LAYER);

        // resize bounds
        wrapper.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                mapPaneSoloObs.setBounds(0, 0, wrapper.getWidth(), wrapper.getHeight());
                overlay.setBounds(0, 0, wrapper.getWidth(), wrapper.getHeight());
            }
        });

        // Botones (idénticos estilo a los de este PanelMapa)
        int size = 42, margin = 10;
        JButton btnZoomIn = new JButton("+");
        JButton btnZoomOut = new JButton("-");
        JButton btnPan = new JButton();

        // icono arrastrar si lo tenés en recursos (mismo que usás arriba)
        try {
            ImageIcon icono = new ImageIcon(getClass().getResource("/arrastrar.png"));
            Image imgEscalada = icono.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btnPan.setIcon(new ImageIcon(imgEscalada));
        } catch (Exception ignore) {}

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
        final boolean[] modoPan = {false};
        final CursorTool[] anterior = {noClickTool};
        btnPan.addActionListener(e -> {
            if (!modoPan[0]) {
                anterior[0] = mapPaneSoloObs.getCursorTool();
                mapPaneSoloObs.setCursorTool(panTool);
                mapPaneSoloObs.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                btnPan.setBackground(new Color(60, 160, 60, 180));
                modoPan[0] = true;
            } else {
                mapPaneSoloObs.setCursorTool(anterior[0]);
                mapPaneSoloObs.setCursor(Cursor.getDefaultCursor());
                btnPan.setBackground(new Color(25, 25, 25));
                modoPan[0] = false;
            }
        });
        	ActionListener zoom = e -> {
            boolean in = e.getSource() == btnZoomIn;
            double factor = in ? 0.5 : 2.0;
            ReferencedEnvelope view = mapPaneSoloObs.getDisplayArea();
            if (view == null) view = mapContent.getViewport().getBounds();
            ReferencedEnvelope bounds = mapContent.getMaxBounds();

            double cx = view.getMedian(0);
            double cy = view.getMedian(1);
            double w = view.getWidth() * factor;
            double h = view.getHeight() * factor;

            ReferencedEnvelope nueva =
                new ReferencedEnvelope(
                    cx - w/2, cx + w/2, cy - h/2, cy + h/2, view.getCoordinateReferenceSystem()
                );

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
            mapPaneSoloObs.setDisplayArea(nueva);
        };
        btnZoomIn.addActionListener(zoom);
        btnZoomOut.addActionListener(zoom);

        overlay.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                for (int i = 0; i < botones.length; i++) {
                    botones[i].setBounds(margin, margin + i * (size + 8), size, size);
                }
            }
        });

        return wrapper;
    }
    
    public JMapPane getMapPane() {
        return mapPane;
    }

    public void agregarPoligonal(poligonal p) {

        Geometry geom = p.getGeometry();
        SimpleFeatureType tipo;
        Object[] attrs;

        if (geom instanceof Point pt) {
            tipo = tipoPuntos;
            attrs = new Object[]{
                pt,
                p.getName(),
                pt.getX(),
                pt.getY()
            };
        }
        else if (geom instanceof LineString ls) {
            Linea lm = (Linea)p;
            tipo = tipoLineas;
            
            String textoMedicion = String.format(
                "%.0f m%nAD: %.0f mils",
                lm.getDistancia(), 
                lm.getAzimut()
            );
            
            attrs = new Object[]{
                ls,
                p.getName(),
                textoMedicion
            };
            
        }
        else {
            throw new IllegalArgumentException("Geometría no soportada.");
        }

        ListFeatureCollection col = new ListFeatureCollection(tipo, new LinkedList<>());
        SimpleFeature feature = SimpleFeatureBuilder.build(
            tipo,
            attrs,
            p.getName() + "-" + UUID.randomUUID()
        );
        col.add(feature);

        Style estilo = (geom instanceof Point) ? estiloPunto() : estiloLinea();

        FeatureLayer capa = new FeatureLayer(col, estilo);

        mapContent.addLayer(capa);

        capaPorPoligonal.put(p, capa);
        coleccionPorPoligonal.put(p, col);

        refrescar();
    }
    
    private Style estiloPunto() {
        return SLD.createPointStyle("circle", Color.WHITE, Color.BLACK, 1.0f, 12.0f);
    }
    
    private Style estiloLinea() {

        StyleBuilder sb = new StyleBuilder();
        StyleFactory sf = CommonFactoryFinder.getStyleFactory();
        Color azulOscuro = new Color(0, 45, 130);
        Stroke stroke = sb.createStroke(azulOscuro, 3.0); 
        LineSymbolizer ls = sb.createLineSymbolizer(stroke);
        TextSymbolizer ts = sb.createTextSymbolizer(Color.WHITE,sb.createFont("Arial", false, false, 20),"distancia");

        ts.setLabelPlacement(sb.createLinePlacement(0.5)); // CENTRADO EN LA LÍNEA

        Rule rule = sf.createRule();
        rule.symbolizers().add(ls);
        rule.symbolizers().add(ts);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(rule);

        Style s = sf.createStyle();
        s.featureTypeStyles().add(fts);

        return s;
    }

    public void agregarBlanco(Blanco b) {

        coordRectangulares c = b.getCoordenadas();

        GeometryFactory gf = new GeometryFactory();
        Point geom = gf.createPoint(new Coordinate(c.getX(), c.getY()));

        String sidc = b.getSimID();
        if (sidc == null || sidc.isEmpty()) {
            sidc = CodigosMilitares.obtenerSIDC(b.getNaturaleza());
            b.setSimID(sidc);
        }

        ListFeatureCollection col = new ListFeatureCollection(tipoBlancos, new LinkedList<>());

        Object[] attrs = new Object[]{
                geom,
                b.getNombre(),
                b.getNaturaleza(),
                b.getFechaDeActualizacion(),
                c.getX(),
                c.getY()
        };

        SimpleFeature f = SimpleFeatureBuilder.build(tipoBlancos, attrs, "blanco-" + UUID.randomUUID());
        col.add(f);

        Style estilo = crearEstilo(sidc, b.getOrientacion());
        FeatureLayer capa = new FeatureLayer(col, estilo);

        mapContent.addLayer(capa);

        capaPorBlanco.put(b, capa);
        coleccionPorBlanco.put(b, col);

        refrescar();
    }
    
    private Style crearEstilo(String sidc, double orient) {

        try {
            proveedorMilSym prov = new proveedorMilSym(sidc, 55);
            Field f = proveedorMilSym.class.getDeclaredField("simbolo");
            f.setAccessible(true);
            BufferedImage simbolo = (BufferedImage) f.get(prov);

            if (simbolo != null) {

                File tempFile = File.createTempFile("sym", ".png");
                ImageIO.write(simbolo, "png", tempFile);
                tempFile.deleteOnExit();
                URL url = tempFile.toURI().toURL();

                StyleBuilder sb = new StyleBuilder();
                ExternalGraphic eg = sb.createExternalGraphic(url, "image/png");

                double rot = orient * 0.05625;

                Graphic g = sb.createGraphic(new ExternalGraphic[]{eg}, null, null, 1.0, 0.0, rot);
                PointSymbolizer ps = sb.createPointSymbolizer(g);

                TextSymbolizer ts = sb.createTextSymbolizer(Color.BLACK,
                        sb.createFont("Arial Black", false, false, 18), "nombre");

                StyleFactory sf = CommonFactoryFinder.getStyleFactory();
                FeatureTypeStyle fts = sf.createFeatureTypeStyle();
                Rule r = sf.createRule();
                r.symbolizers().add(ps);
                r.symbolizers().add(ts);
                fts.rules().add(r);

                Style s = sf.createStyle();
                s.featureTypeStyles().add(fts);
                return s;
            }

        } catch (Exception ignored) {}

        return SLD.createPointStyle("circle", Color.WHITE, Color.RED, 1.0f, 14.0f);
    }

    public void eliminarPoligonal(poligonal p) {

        FeatureLayer capa = capaPorPoligonal.remove(p);
        coleccionPorPoligonal.remove(p);

        if (capa != null)
            mapContent.removeLayer(capa);

        refrescar();
    }

    public void eliminarBlanco(Blanco b) {

        FeatureLayer capa = capaPorBlanco.remove(b);
        coleccionPorBlanco.remove(b);

        if (capa != null) mapContent.removeLayer(capa);

        refrescar();
    }
    
    private void refrescar() {

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refrescar);
            return;
        }

        mapPane.setDisplayArea(mapPane.getDisplayArea());
        mapPane.repaint();
    }

    public void actualizar(){
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

        int size = 80;
        int margin = 12;

        JButton btnZoomIn = new JButton("+");
        JButton btnZoomOut = new JButton("-");
        JButton btnPan = new JButton("•");

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
            b.setFont(new Font("Arial", Font.BOLD, 30)); 
            overlay.add(b);
        }

        Consumer<Boolean> ejecutarZoom = (Boolean in) -> {
            double factor = in ? 0.5 : 2.0;
            ReferencedEnvelope view = mapPane.getDisplayArea();
            ReferencedEnvelope bounds = mapContent.getMaxBounds();
            double cx = view.getMedian(0);
            double cy = view.getMedian(1);
            double w = view.getWidth() * factor;
            double h = view.getHeight() * factor;

            ReferencedEnvelope nueva = new ReferencedEnvelope(
                cx - w / 2, cx + w / 2, cy - h / 2, cy + h / 2, 
                view.getCoordinateReferenceSystem()
            );

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

        btnZoomIn.addActionListener(e -> ejecutarZoom.accept(true));
        btnZoomOut.addActionListener(e -> ejecutarZoom.accept(false));

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    int keyCode = e.getKeyCode();
                    if (keyCode == KeyEvent.VK_PAGE_UP) {
                        ejecutarZoom.accept(true);
                        return true; 
                    } else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
                        ejecutarZoom.accept(false);
                        return true;
                    }
                }
                return false;
            }
        });

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
                btnPan.setBackground(new Color(25, 25, 25)); 
                modoPan[0] = false;
            }
        });

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
