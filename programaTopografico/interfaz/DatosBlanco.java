package interfaz;
import dominio.Blanco;
import java.awt.*;
import javax.swing.*;

/**
 * {@code DatosBlanco} es un panel Swing personalizado que muestra los datos detallados de un objeto {@link Blanco}.
 * Presenta información como designación, naturaleza, fecha, orientación, coordenadas, estado y datos adicionales,
 * todo organizado en un diseño de cuadrícula con estilos visuales personalizados.
 * 
 * <p>Los campos de texto son de solo lectura y se actualizan mediante el método {@link #setDatosBlanco(Blanco)}.
 * El panel está diseñado para integrarse en interfaces gráficas de usuario que requieren la visualización
 * estructurada de información topográfica o de blancos.</p>
 * 
 * <ul>
 *   <li><b>txtNombre</b>: Designación del blanco.</li>
 *   <li><b>txtNaturaleza</b>: Naturaleza o tipo del blanco.</li>
 *   <li><b>txtFecha</b>: Fecha de actualización de los datos.</li>
 *   <li><b>txtOrientacion</b>: Orientación del blanco en mils.</li>
 *   <li><b>txtX</b>: Coordenada X (Derechas).</li>
 *   <li><b>txtY</b>: Coordenada Y (Arribas).</li>
 *   <li><b>txtSituacion</b>: Estado o situación del blanco.</li>
 *   <li><b>txtInfoAdicional</b>: Información adicional relevante.</li>
 * </ul>
 * 
 * @author  [Matias Leonel Juarez]
 * @version 1.0
 * @see     Blanco
 */
public class DatosBlanco extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField txtNombre, txtNaturaleza, txtFecha, txtOrientacion;
    private JTextField txtX, txtY, txtSituacion;
    private JTextArea txtInfoAdicional;
    private Blanco blancoActual;

    public DatosBlanco() {
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 15, 15));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                "DATOS DEL BLANCO",
                0, 0,
                new Font("Consolas", Font.BOLD, 16),
                new Color(255, 215, 0)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 10, 3, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font fuenteCampo = new Font("Consolas", Font.PLAIN, 14);
        Font fuenteLabel = new Font("Consolas", Font.BOLD, 14);
        Color colorEtiqueta = new Color(255, 215, 0);
        Color colorCampo = Color.WHITE;
        
        int fila = 0;

        agregarLabel("DESIGNACION:", fila, 0, fuenteLabel, colorEtiqueta, gbc);
        txtNombre = crearCampo(fuenteCampo, colorCampo);
        agregarCampo(txtNombre, fila++, 1, gbc);

        agregarLabel("NATURALEZA:", fila, 0, fuenteLabel, colorEtiqueta, gbc);
        txtNaturaleza = crearCampo(fuenteCampo, colorCampo);
        agregarCampo(txtNaturaleza, fila++, 1, gbc);

        agregarLabel("FECHA:", fila, 0, fuenteLabel, colorEtiqueta, gbc);
        txtFecha = crearCampo(fuenteCampo, colorCampo);
        agregarCampo(txtFecha, fila++, 1, gbc);

        agregarLabel("ORIENTACION (mils):", fila, 0, fuenteLabel, colorEtiqueta, gbc);
        txtOrientacion = crearCampo(fuenteCampo, colorCampo);
        agregarCampo(txtOrientacion, fila++, 1, gbc);

        fila = 0;

        agregarLabel("DERECHAS:", fila, 2, fuenteLabel, colorEtiqueta, gbc);
        txtX = crearCampo(fuenteCampo, colorCampo);
        agregarCampo(txtX, fila++, 3, gbc);

        agregarLabel("ARRIBAS:", fila, 2, fuenteLabel, colorEtiqueta, gbc);
        txtY = crearCampo(fuenteCampo, colorCampo);
        agregarCampo(txtY, fila++, 3, gbc);

        agregarLabel("ESTADO:", fila, 2, fuenteLabel, colorEtiqueta, gbc);
        txtSituacion = crearCampo(fuenteCampo, colorCampo);
        agregarCampo(txtSituacion, fila++, 3, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(255, 215, 0));
        add(sep, gbc);

        gbc.gridy = 6;
        JLabel lblInfo = new JLabel("INFORMACIÓN ADICIONAL");
        lblInfo.setFont(new Font("Consolas", Font.BOLD, 13));
        lblInfo.setForeground(colorEtiqueta);
        add(lblInfo, gbc);

        txtInfoAdicional = new JTextArea(2, 30); 
        txtInfoAdicional.setFont(fuenteCampo);
        txtInfoAdicional.setBackground(new Color(35, 35, 35));
        txtInfoAdicional.setForeground(Color.WHITE);
        txtInfoAdicional.setLineWrap(true);
        txtInfoAdicional.setWrapStyleWord(true);
        txtInfoAdicional.setEditable(false);
        txtInfoAdicional.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        gbc.gridy = 7;
        gbc.weighty = 0;
        add(new JScrollPane(txtInfoAdicional), gbc);
    }

    private void agregarLabel(String texto, int fila, int col, Font fuente, Color color, GridBagConstraints gbc) {
        gbc.gridx = col;
        gbc.gridy = fila;
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente);
        lbl.setForeground(color);
        add(lbl, gbc);
    }

    private void agregarCampo(JTextField campo, int fila, int col, GridBagConstraints gbc) {
        gbc.gridx = col;
        gbc.gridy = fila;
        add(campo, gbc);
    }

    private JTextField crearCampo(Font fuente, Color color) {
        JTextField campo = new JTextField(18);
        campo.setFont(fuente);
        campo.setBackground(new Color(35, 35, 35));
        campo.setForeground(color);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        campo.setEditable(false);
        campo.setHorizontalAlignment(SwingConstants.LEFT); 
        return campo;
    }

    public void setDatosBlanco(Blanco b) {
        if (b == null) return;
        this.blancoActual = b;

        SwingUtilities.invokeLater(() -> {
            txtNombre.setText(b.getNombre());
            txtNaturaleza.setText(b.getNaturaleza());
            txtFecha.setText(b.getFechaDeActualizacion());
            txtOrientacion.setText(String.format("%.1f°", b.getOrientacion()));
            txtX.setText(String.format("%.2f", b.getCoordenadas().getX()));
            txtY.setText(String.format("%.2f", b.getCoordenadas().getY()));
            txtSituacion.setText(b.getSituacionMovimiento().toString());
            txtInfoAdicional.setText(
                (b.getInformacionAdicional() == null || b.getInformacionAdicional().isEmpty())
                    ? "Sin información adicional."
                    : b.getInformacionAdicional()
            );
        });
    }

    public Blanco getBlancoActual() {
        return blancoActual;
    }
}
