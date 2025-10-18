import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import armyc2.c5isr.renderer.MilStdIconRenderer;
import armyc2.c5isr.renderer.utilities.ImageInfo;
import armyc2.c5isr.renderer.utilities.RendererSettings;

public class proveedorMilSym extends JPanel {
    private BufferedImage simbolo;

    public proveedorMilSym(String sidc, int size) {

        setBackground(Color.BLACK);
        try {
            // Config básica (solo lo que está en javadoc 2525D)
            RendererSettings rs = RendererSettings.getInstance();
            rs.setDefaultPixelSize(size);
            rs.setUseLineInterpolation(true);
            rs.getCacheEnabled();
            rs.setUseLineInterpolation(false);
            
            MilStdIconRenderer renderer = MilStdIconRenderer.getInstance();

            Map<String, String> modifiers = new HashMap<>();   // vacío está bien
            Map<String, String> attributes = new HashMap<>();
            attributes.put("SIZE", Integer.toString(size));     // tamaño en px
	    

            // Ejemplo:
	    // Usa un SIDC 2525D numérico (20 chars). Ejemplos que funcionan:
            //  - 10031000001101000000  -> Friendly Infantry Unit 1
            //  - 10031000001201000000  -> Friendly Armored Unit 2
            //  - 10031000001301000000  -> Friendly Artillery Unit
            //String sidc = "10031000001101000000";
            //int size = 180;
            ImageInfo info = renderer.RenderIcon(sidc, modifiers, attributes);
            if (info != null) simbolo = info.getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (simbolo != null) {
            int x = (getWidth() - simbolo.getWidth()) / 2;
            int y = (getHeight() - simbolo.getHeight()) / 2;
            g.drawImage(simbolo, x, y, null);
        } else {
            g.setColor(Color.WHITE);
            g.drawString("No se pudo generar el símbolo.", 20, 20);
        }
    }
}
