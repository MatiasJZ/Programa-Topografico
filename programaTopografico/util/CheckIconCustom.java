package util;
import javax.swing.*;
import java.awt.*;

/**
 * A custom icon implementation that displays a square with a border and a check mark
 * when used in a selected {@link AbstractButton}. The icon's size, border color,
 * fill color, and check mark color are customizable via the constructor.
 *
 * <p>
 * The icon is rendered as follows:
 * <ul>
 *   <li>A filled square as the background.</li>
 *   <li>A border around the square.</li>
 *   <li>A green check mark is drawn if the associated component is an
 *       {@link AbstractButton} and is selected.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 * JCheckBox checkBox = new JCheckBox("Option");
 * checkBox.setIcon(new CheckIconCustom(16));
 * </pre>
 * </p>
 *
 * @author [Matias Leonel Juarez]
 */
public class CheckIconCustom implements Icon {

    private final int size;
    private final Color border;
    private final Color fill;
    private final Color check;

    public CheckIconCustom(int size) {
        this.size = size;
        this.border = Color.WHITE;
        this.fill = Color.BLACK;
        this.check = Color.GREEN;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // fondo
        g2.setColor(fill);
        g2.fillRect(x, y, size, size);

        // borde
        g2.setColor(border);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, size, size);

        // check si está seleccionado
        if (c instanceof AbstractButton b && b.isSelected()) {
            g2.setColor(check);
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(x + size/4, y + size/2,
                        x + size/2, y + size - size/4);
            g2.drawLine(x + size/2, y + size - size/4,
                        x + size - size/5, y + size/4);
        }

        g2.dispose();
    }
}
