import javax.swing.*;
import java.awt.*;

class RadioButtonGrande implements Icon {
    private int size;

    public RadioButtonGrande(int size) {
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