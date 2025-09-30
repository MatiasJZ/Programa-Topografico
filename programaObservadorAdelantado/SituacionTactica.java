import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import javax.swing.*;

import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;

public class SituacionTactica extends JPanel {

    private static final long serialVersionUID = 1L;
    private DefaultListModel<Blanco> modeloLista;
    private JList<Blanco> listaUI;
    protected LinkedList<Blanco> listaDeBlancos;
    private PanelMapaGeoTools panelMapa;

    public SituacionTactica(LinkedList<Blanco> listaDeBlancos) {
        setSize(900, 600);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        this.listaDeBlancos = listaDeBlancos;
        modeloLista = new DefaultListModel<>();
        listaUI = new JList<>(modeloLista);
        listaUI.setFont(new Font("Arial", Font.BOLD, 20));
        listaUI.setBackground(Color.BLACK);

        listaUI.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (value instanceof Blanco) {
                    Blanco b = (Blanco) value;
                    label.setText(b.getNombre());
                    label.setForeground(b.isAliado() ? new Color(100, 150, 255) : new Color(255, 100, 100));
                }
                return label;
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaUI);
        scrollLista.setPreferredSize(new Dimension(250, 0));
        scrollLista.getViewport().setBackground(Color.BLACK);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBackground(Color.BLACK);
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setBackground(Color.BLACK);

        JButton btnAgregar = new JButton("AGREGAR");
        JButton btnEliminar = new JButton("ELIMINAR");
        JButton btnActualizar = new JButton("ACTUALIZAR");

        btnAgregar.setBackground(new Color(0, 120, 255));
        btnAgregar.setForeground(Color.WHITE);
        btnEliminar.setBackground(Color.orange);
        btnEliminar.setForeground(Color.WHITE);
        btnActualizar.setBackground(Color.red);
        btnActualizar.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0; gbc.gridy = 0; panelBotones.add(btnAgregar, gbc);
        gbc.gridx = 1; panelBotones.add(btnEliminar, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; panelBotones.add(btnActualizar, gbc);

        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        // MAPA 
        panelMapa = new PanelMapaGeoTools("C:/Users/54293/Desktop/mapaV2.tif");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelMapa);
        splitPane.setDividerLocation(250);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // CLICK EN MAPA
        panelMapa.getMapPane().setCursorTool(new CursorTool() {
            @Override
            public void onMouseClicked(MapMouseEvent ev) {
                double lon = ev.getWorldPos().getX();
                double lat = ev.getWorldPos().getY();
                coordRectangulares coord = new coordRectangulares(lon, lat, 0);
                mostrarDialogoAgregar(null, coord); // el punto se dibuja si el usuario acepta
            }
        });

        // AGREGAR
        btnAgregar.addActionListener(e -> {
            mostrarDialogoAgregar(null, new coordRectangulares(0, 0, 0));
        });

        // ELIMINAR
        btnEliminar.addActionListener(e -> {
            Blanco seleccionado = listaUI.getSelectedValue();
            if (seleccionado != null) {
                listaDeBlancos.remove(seleccionado);
                modeloLista.removeElement(seleccionado);
                panelMapa.eliminarBlanco(seleccionado);
            }
        });

        // ACTUALIZAR
        btnActualizar.addActionListener(e -> actualizarBlancosEnMapa());

        // MENU CONTEXTUAL
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("Editar/Marcar");
        JMenuItem itemMedir = new JMenuItem("Medir");

        popupMenu.add(itemEditar);
        popupMenu.add(itemMedir);

        listaUI.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) mostrarPopup(e);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) mostrarPopup(e);
            }
            private void mostrarPopup(java.awt.event.MouseEvent e) {
                int idx = listaUI.locationToIndex(e.getPoint());
                if (idx >= 0) {
                    listaUI.setSelectedIndex(idx);
                    popupMenu.show(listaUI, e.getX(), e.getY());
                }
            }
        });
    }

    private void actualizarBlancosEnMapa() {
        for (Blanco b : listaDeBlancos) {
            panelMapa.agregarBlanco(b);
        }
        listaUI.repaint();
    }

    private void mostrarDialogoAgregar(Blanco blancoEditar, coordRectangulares coordInicial){
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, (blancoEditar==null?"Nuevo Blanco":"Editar Blanco"), true);
        dialog.setSize(500,400);
        dialog.setLocationRelativeTo(this);

        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panelDialog);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(); 
        txtNombre.setPreferredSize(new Dimension(250,30));
        JTextField txtNaturaleza = new JTextField(); 
        txtNaturaleza.setPreferredSize(new Dimension(250,30));
        addPlaceholder(txtNombre, blancoEditar!=null?blancoEditar.getNombre():"Nombre del Blanco");
        addPlaceholder(txtNaturaleza, blancoEditar!=null?blancoEditar.getNaturaleza():"Naturaleza");
        txtNombre.setBackground(new Color(70,70,70)); 
        txtNombre.setForeground(Color.WHITE);
        txtNaturaleza.setBackground(new Color(70,70,70));
        txtNaturaleza.setForeground(Color.WHITE);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFecha = new JTextField(blancoEditar!=null?blancoEditar.getFechaDeActualizacion():dtf.format(LocalDateTime.now()));
        txtFecha.setPreferredSize(new Dimension(250,30));
        txtFecha.setBackground(new Color(70,70,70)); txtFecha.setForeground(Color.WHITE);

        JLabel lblNombre = new JLabel("Nombre:"); lblNombre.setForeground(Color.WHITE);
        JLabel lblNaturaleza = new JLabel("Naturaleza:"); lblNaturaleza.setForeground(Color.WHITE);
        JLabel lblFecha = new JLabel("Fecha:"); lblFecha.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; panelDialog.add(lblNombre, gbc);
        gbc.gridx=1; panelDialog.add(txtNombre, gbc);
        gbc.gridx=0; gbc.gridy=1; panelDialog.add(lblNaturaleza, gbc);
        gbc.gridx=1; panelDialog.add(txtNaturaleza, gbc);
        gbc.gridx=0; gbc.gridy=2; panelDialog.add(lblFecha, gbc);
        gbc.gridx=1; panelDialog.add(txtFecha, gbc);

        JTextField campoX = new JTextField(); campoX.setPreferredSize(new Dimension(250,30));
        JTextField campoY = new JTextField(); campoY.setPreferredSize(new Dimension(250,30));
        campoX.setBackground(new Color(70,70,70)); campoX.setForeground(Color.WHITE);
        campoY.setBackground(new Color(70,70,70)); campoY.setForeground(Color.WHITE);

        if(blancoEditar!=null){
            coordRectangulares stored = (coordRectangulares) blancoEditar.getCoordenadas();
            campoX.setText(String.valueOf(stored.getX()));
            campoY.setText(String.valueOf(stored.getY()));
        } else if(coordInicial!=null){
            campoX.setText(String.valueOf(coordInicial.getX()));
            campoY.setText(String.valueOf(coordInicial.getY()));
        } else {
            campoX.setText("0"); campoY.setText("0");
        }
        
        JLabel lblCampoX = new JLabel("X"); lblCampoX.setForeground(Color.WHITE);
        JLabel lblCampoY = new JLabel("Y"); lblCampoY.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=3; panelDialog.add(lblCampoX, gbc);
        gbc.gridx=1; panelDialog.add(campoX, gbc);
        gbc.gridx=0; gbc.gridy=4; panelDialog.add(lblCampoY, gbc);
        gbc.gridx=1; panelDialog.add(campoY, gbc);

        JCheckBox chkAliado = new JCheckBox("Aliado");
        chkAliado.setBackground(new Color(50,50,50));
        chkAliado.setForeground(Color.WHITE);
        if(blancoEditar!=null) chkAliado.setSelected(blancoEditar.isAliado());
        else chkAliado.setSelected(true);

        gbc.gridx=0; gbc.gridy=5; panelDialog.add(new JLabel("Tipo:"), gbc);
        gbc.gridx=1; panelDialog.add(chkAliado, gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel panelBotonesDialog = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotonesDialog.setBackground(new Color(50,50,50));
        panelBotonesDialog.add(btnAceptar);
        panelBotonesDialog.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; panelDialog.add(panelBotonesDialog, gbc);

        btnAceptar.addActionListener(ev -> {
            try{
                String nombre = txtNombre.getText().trim();
                String naturaleza = txtNaturaleza.getText().trim();
                String fecha = dtf.format(LocalDateTime.now());
                if(nombre.isEmpty()||naturaleza.isEmpty()){
                    JOptionPane.showMessageDialog(dialog,"Complete todos los campos","Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double x = Double.parseDouble(campoX.getText().trim());
                double y = Double.parseDouble(campoY.getText().trim());
                coordRectangulares coords = new coordRectangulares(x,y,0);

                if(blancoEditar==null){
                    Blanco nuevo = new Blanco(nombre, coords, naturaleza, fecha, chkAliado.isSelected());
                    listaDeBlancos.add(nuevo);
                    modeloLista.addElement(nuevo);
                    panelMapa.agregarBlanco(nuevo); // ⬅️ se pinta en el mapa al aceptar
                } else {
                    blancoEditar.setNombre(nombre);
                    blancoEditar.setNaturaleza(naturaleza);
                    blancoEditar.setFecha(fecha);
                    blancoEditar.setCoordenadas(coords);
                    blancoEditar.setAliado(chkAliado.isSelected());
                    panelMapa.agregarBlanco(blancoEditar); // refresco el punto editado
                    listaUI.repaint();
                }

                dialog.dispose();
            } catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(dialog,
                    "Formato numérico inválido en X/Y.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void addPlaceholder(JTextField field, String placeholder){
        Color placeholderColor = new Color(180,180,180);
        Color textColor = Color.WHITE;
        field.setForeground(placeholderColor);
        field.setText(placeholder);
        field.setBackground(new Color(70,70,70));
        field.addFocusListener(new FocusAdapter(){
            @Override
            public void focusGained(FocusEvent e){
                if(field.getText().equals(placeholder)){
                    field.setText("");
                    field.setForeground(textColor);
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                if(field.getText().isEmpty()){
                    field.setForeground(placeholderColor);
                    field.setText(placeholder);
                }
            }
        });
    }
}
