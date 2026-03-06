package simbologia;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import armyc2.c5isr.renderer.MilStdIconRenderer;
import armyc2.c5isr.renderer.utilities.ImageInfo;
import armyc2.c5isr.renderer.utilities.RendererSettings;

/**
 * ProveedorSimbologiaMilitarizada es un componente Swing personalizado que genera y muestra
 * un símbolo militar basado en el estándar MIL-STD utilizando un identificador SIDC y un tamaño especificado.
 * 
 * <p>Utiliza un renderizador de iconos militares para crear una imagen del símbolo, la cual se centra
 * automáticamente en el panel. Si ocurre un error durante la generación del símbolo, se muestra un mensaje
 * de error en su lugar.</p>
 *
 * <p>Este panel está diseñado para integrarse en interfaces gráficas Java y facilitar la visualización
 * de simbología militarizada en aplicaciones topográficas o de mapeo.</p>
 *
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class ProveedorSimbologiaMilitarizada extends JPanel {
	
	private static final long serialVersionUID = 3682810635662561460L;
	private BufferedImage simbolo;

    public ProveedorSimbologiaMilitarizada(String sidc, int size) {

        setBackground(Color.BLACK);
        try {
            RendererSettings rs = RendererSettings.getInstance();
            rs.setDefaultPixelSize(size);
            rs.setUseLineInterpolation(true);
            rs.getCacheEnabled();
            rs.setUseLineInterpolation(false);
            
            MilStdIconRenderer renderer = MilStdIconRenderer.getInstance();

            Map<String, String> modifiers = new HashMap<>(); 
            Map<String, String> attributes = new HashMap<>();
            attributes.put("SIZE", Integer.toString(size)); 

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
