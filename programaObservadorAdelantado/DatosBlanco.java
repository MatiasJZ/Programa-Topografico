import javax.swing.*;
import java.awt.*;

public class DatosBlanco extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField txtNombre;
    private JTextField txtSIDC;
    private JTextField txtFecha;
    private JTextField txtX;
    private JTextField txtY;
    private JTextField txtSituacion;

    public DatosBlanco() {
    	
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font fuente = new Font("Consolas", Font.PLAIN, 16);
        Color textoColor = Color.WHITE;

        int fila = 0;

        // nombre
        agregarLabel("Nombre:", fila, gbc);
        txtNombre = crearCampo(fuente, textoColor);
        agregarCampo(txtNombre, fila++, gbc);

        // sidc
        agregarLabel("Naturaleza (SIDC):", fila, gbc);
        txtSIDC = crearCampo(fuente, textoColor);
        agregarCampo(txtSIDC, fila++, gbc);

        // fecha
        agregarLabel("Fecha de actualización:", fila, gbc);
        txtFecha = crearCampo(fuente, textoColor);
        agregarCampo(txtFecha, fila++, gbc);

        // coordenada x
        agregarLabel("Coordenada X:", fila, gbc);
        txtX = crearCampo(fuente, textoColor);
        agregarCampo(txtX, fila++, gbc);

        // coordenada y
        agregarLabel("Coordenada Y:", fila, gbc);
        txtY = crearCampo(fuente, textoColor);
        agregarCampo(txtY, fila++, gbc);

        // situación de movimiento 
        agregarLabel("Situación de movimiento:", fila, gbc);
        txtSituacion = crearCampo(fuente, textoColor);
        agregarCampo(txtSituacion, fila++, gbc);
    }

    private void agregarLabel(String texto, int fila, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = fila;
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.YELLOW);
        add(lbl, gbc);
    }

    private void agregarCampo(JTextField campo, int fila, GridBagConstraints gbc) {
        gbc.gridx = 1; gbc.gridy = fila;
        add(campo, gbc);
    }

    private JTextField crearCampo(Font fuente, Color color) {
        JTextField campo = new JTextField(20);
        campo.setFont(fuente);
        campo.setBackground(new Color(50, 50, 50));
        campo.setForeground(color);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return campo;
    }

    public void setDatosBlanco(Blanco b) {
        if (b == null) return;
        SwingUtilities.invokeLater(() -> { 
            txtNombre.setText(b.getNombre());
            txtSIDC.setText(b.getNaturaleza());
            txtFecha.setText(b.getFechaDeActualizacion());
            txtX.setText(String.valueOf(b.getCoordenadas().getX()));
            txtY.setText(String.valueOf(b.getCoordenadas().getY()));
            txtSituacion.setText(b.getSituacionMovimiento().toString());
        });
    }
}
