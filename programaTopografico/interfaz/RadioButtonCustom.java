package interfaz;
import javax.swing.*;
import java.awt.*;

/**
 * A custom icon implementation for radio buttons that draws a circular outline,
 * and fills the center when the button is selected.
 * <p>
 * The icon size is configurable via the constructor.
 * </p>
 *
 * <p>
 * Usage example:
 * <pre>
 * JRadioButton radioButton = new JRadioButton("Option");
 * radioButton.setIcon(new RadioButtonCustom(16));
 * </pre>
 * </p>
 *
 * @author [Matias Leonel Juarez]
 */
class RadioButtonCustom implements Icon {
    private int size;

    public RadioButtonCustom(int size) {
        this.size = size;
    }

    @Override
    public int getIconWidth() { return size; }

    @Override
    public int getIconHeight() { return size; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();

        // circulo externo
        g.setColor(Color.WHITE);
        g.drawOval(x, y, size - 1, size - 1);

        // circulo relleno si está seleccionado
        if (model.isSelected()) {
            g.fillOval(x + size/4, y + size/4, size/2, size/2);
        }
    }
}