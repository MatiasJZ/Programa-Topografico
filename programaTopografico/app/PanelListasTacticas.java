package app;

import java.awt.*;
import javax.swing.*;
import dominio.Blanco;
import dominio.RenderizadorListas;
import interfaces.Poligonal;

/**
 * PanelListasTacticas — Panel izquierdo con las listas de blancos y poligonales.
 *
 * <p>Encapsula los modelos, las JList y el layout de la columna izquierda,
 * dejando a {@link SituacionTacticaTopografica} libre de toda lógica de presentación
 * de listas.
 *
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class PanelListasTacticas extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DefaultListModel<Blanco>    modeloBlancos    = new DefaultListModel<>();
    private final DefaultListModel<Poligonal> modeloPoligonales = new DefaultListModel<>();
    private final JList<Blanco>    listaUIBlancos;
    private final JList<Poligonal> listaUIPoligonales;

    public PanelListasTacticas() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        RenderizadorListas render = new RenderizadorListas();

        listaUIBlancos = new JList<>(modeloBlancos);
        listaUIBlancos.setFont(new Font("Arial", Font.BOLD, 20));
        listaUIBlancos.setBackground(Color.BLACK);
        listaUIBlancos.setFixedCellHeight(50);
        listaUIBlancos.setCellRenderer(render);

        listaUIPoligonales = new JList<>(modeloPoligonales);
        listaUIPoligonales.setFont(new Font("Arial", Font.BOLD, 20));
        listaUIPoligonales.setBackground(Color.BLACK);
        listaUIPoligonales.setFixedCellHeight(40);
        listaUIPoligonales.setCellRenderer(render);

        JLabel lblBlancos = etiqueta("BLANCOS");
        JLabel lblPoligonales = etiqueta("POLIGONALES");

        JScrollPane scrollBlancos = new JScrollPane(listaUIBlancos);
        scrollBlancos.getViewport().setBackground(Color.BLACK);

        JScrollPane scrollPoligonales = new JScrollPane(listaUIPoligonales);
        scrollPoligonales.getViewport().setBackground(Color.BLACK);

        JPanel panelListas = new JPanel(new GridBagLayout());
        panelListas.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.gridx = 0;

        gbc.gridy = 0; gbc.weightx = 1; gbc.weighty = 0;
        panelListas.add(lblBlancos, gbc);

        gbc.gridy = 1; gbc.weighty = 0.75;
        panelListas.add(scrollBlancos, gbc);

        gbc.gridy = 2; gbc.weighty = 0;
        panelListas.add(lblPoligonales, gbc);

        gbc.gridy = 3; gbc.weighty = 0.25;
        panelListas.add(scrollPoligonales, gbc);

        add(panelListas, BorderLayout.CENTER);
    }

    public DefaultListModel<Blanco>    getModeloBlancos()     { return modeloBlancos; }
    public DefaultListModel<Poligonal> getModeloPoligonales() { return modeloPoligonales; }
    public JList<Blanco>               getListaUIBlancos()    { return listaUIBlancos; }
    public JList<Poligonal>            getListaUIPoligonales(){ return listaUIPoligonales; }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setForeground(Color.GRAY);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        return lbl;
    }
}