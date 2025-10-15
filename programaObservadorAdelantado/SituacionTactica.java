import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private PanelMapa panelMapa;
    

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
        JButton btnPIF = new JButton("PIF");

        btnAgregar.setBackground(new Color(0,120,255)); btnAgregar.setForeground(Color.WHITE);
        btnEliminar.setBackground(Color.ORANGE);        btnEliminar.setForeground(Color.WHITE);
        btnActualizar.setBackground(Color.RED);         btnActualizar.setForeground(Color.WHITE);
        btnPIF.setBackground(Color.GREEN);              btnPIF.setForeground(Color.WHITE);
        
        Dimension compacto = new Dimension(110, 32); // ajustá a gusto (ancho, alto)
        Font fuente = new Font("Arial", Font.BOLD, 12);
        Insets padding = new Insets(4, 10, 4, 10);

        for (JButton b : new JButton[]{btnAgregar, btnEliminar, btnActualizar, btnPIF}) {
            b.setFont(fuente);
            b.setMargin(padding);
            b.setPreferredSize(compacto);
            b.setMinimumSize(compacto);
            b.setMaximumSize(compacto);
            b.setFocusPainted(false);
        }
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0; gbc.gridy = 0; panelBotones.add(btnAgregar, gbc);
        gbc.gridx = 1; panelBotones.add(btnEliminar, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelBotones.add(btnActualizar, gbc); 
        gbc.gridx = 1; panelBotones.add(btnPIF,gbc);

        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        // MAPA 
        panelMapa = new PanelMapa("C:/Users/54293/Desktop/Archivos SARGO/mapaV1.TIF");
        
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
        //PIF
        btnPIF.addActionListener(e -> armarPIF(listaUI.getSelectedValue()));
        
        // MENU CONTEXTUAL
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("Editar Blanco");
        JMenuItem itemMarcarPolares = new JMenuItem("Marcar en Polares");
        JMenuItem itemMedir = new JMenuItem("Medir");

        popupMenu.add(itemEditar);
        popupMenu.add(itemMedir);
        popupMenu.add(itemMarcarPolares);
        
        itemMedir.addActionListener(e -> {
            Blanco BSeleccionado = listaUI.getSelectedValue();
            if (BSeleccionado != null) {
                mostrarDialogoMedir(BSeleccionado);
            }
        });
        
        itemEditar.addActionListener(e -> {
            Blanco BSeleccionado = listaUI.getSelectedValue();
            if (BSeleccionado != null) {
                mostrarDialogoEditar(BSeleccionado);
            }
        });
        
        itemMarcarPolares.addActionListener(e -> {
            Blanco seleccionado = listaUI.getSelectedValue();
            if (seleccionado != null) {
                mostrarDialogoPolares(seleccionado);
            }
        });
        
        listaUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) mostrarPopup(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) mostrarPopup(e);
            }
            private void mostrarPopup(MouseEvent e) {
                int idx = listaUI.locationToIndex(e.getPoint());
                if (idx >= 0) {
                    listaUI.setSelectedIndex(idx);
                    popupMenu.show(listaUI, e.getX(), e.getY());
                }
            }
        });        
    }
    
    private void mostrarDialogoPolares(Blanco referencia) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Marcar en Polares desde: " + referencia.getNombre(), true);
        dialog.setSize(600, 420);
        dialog.setLocationRelativeTo(this);

        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panelDialog);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // labels 
        JLabel lblTitulo = new JLabel("Ingrese los datos del nuevo blanco:");
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelDialog.add(lblTitulo, gbc);

        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblNaturaleza = new JLabel("Naturaleza:");
        JLabel lblDir = new JLabel("Dirección (milésimos):");
        JLabel lblDist = new JLabel("Distancia (m):");
        JLabel lblAng = new JLabel("Ángulo vertical (milésimos):");

        JLabel[] labels = {lblNombre, lblNaturaleza, lblDir, lblDist, lblAng};
        for (JLabel l : labels) l.setForeground(Color.WHITE);

        // campos
        JTextField txtNombre = new JTextField();
        JTextField txtNaturaleza = new JTextField();
        JTextField txtDireccion = new JTextField();
        JTextField txtDistancia = new JTextField();
        JTextField txtAngulo = new JTextField("0");

        JTextField[] fields = {txtNombre, txtNaturaleza, txtDireccion, txtDistancia, txtAngulo};
        for (JTextField f : fields) {
            f.setPreferredSize(new Dimension(300, 30));
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
            f.setCaretColor(Color.WHITE);
        }

        // placeholders
        addPlaceholder(txtNombre, "Nombre");
        addPlaceholder(txtNaturaleza, "Naturaleza");
        addPlaceholder(txtDireccion, "Milésimos");
        addPlaceholder(txtDistancia, "Metros");
        addPlaceholder(txtAngulo, "Milésimos");

        // layout 
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; panelDialog.add(lblNombre, gbc);
        gbc.gridx = 1; panelDialog.add(txtNombre, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelDialog.add(lblNaturaleza, gbc);
        gbc.gridx = 1; panelDialog.add(txtNaturaleza, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panelDialog.add(lblDir, gbc);
        gbc.gridx = 1; panelDialog.add(txtDireccion, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panelDialog.add(lblDist, gbc);
        gbc.gridx = 1; panelDialog.add(txtDistancia, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panelDialog.add(lblAng, gbc);
        gbc.gridx = 1; panelDialog.add(txtAngulo, gbc);

        // tipo y forma
        JCheckBox chkAliado = new JCheckBox("Aliado");
        chkAliado.setBackground(new Color(50, 50, 50));
        chkAliado.setForeground(Color.WHITE);
        chkAliado.setSelected(referencia.isAliado());
        gbc.gridx = 0; gbc.gridy = 6; panelDialog.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; panelDialog.add(chkAliado, gbc);

        String[] formas = {"círculo", "cruz", "triángulo"};
        JComboBox<String> comboForma = new JComboBox<>(formas);
        comboForma.setBackground(new Color(70, 70, 70));
        comboForma.setForeground(Color.WHITE);
        comboForma.setSelectedItem(referencia.getForma() != null ? referencia.getForma() : "círculo");
        gbc.gridx = 0; gbc.gridy = 7; panelDialog.add(new JLabel("Forma:"), gbc);
        gbc.gridx = 1; panelDialog.add(comboForma, gbc);

        // botones
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setBackground(new Color(50, 50, 50));
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        panelDialog.add(panelBotones, gbc);

        // acción aceptar 
        btnAceptar.addActionListener(e -> {
            try {
                double direccionMils = Double.parseDouble(txtDireccion.getText().trim());
                double distancia = Double.parseDouble(txtDistancia.getText().trim());
                double angVertMils = Double.parseDouble(txtAngulo.getText().trim());

                String nombre = txtNombre.getText().trim();
                String naturaleza = txtNaturaleza.getText().trim();

                if (nombre.isEmpty() || naturaleza.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                coordRectangulares refCoord = (coordRectangulares) referencia.getCoordenadas();
                coordPolares polar = new coordPolares(direccionMils, distancia, angVertMils, refCoord);
                coordRectangulares nuevasCoords = polar.toRectangulares();

                Color color = chkAliado.isSelected() ? Color.BLUE : Color.RED;
                String forma = (String) comboForma.getSelectedItem();
                String fecha = LocalDateTime.now().toString();

                Blanco nuevo = new Blanco(nombre, nuevasCoords, naturaleza, fecha,chkAliado.isSelected(), forma, color);
                listaDeBlancos.add(nuevo);
                modeloLista.addElement(nuevo);
                panelMapa.agregarBlanco(nuevo);
                JOptionPane.showMessageDialog(dialog,String.format("Blanco '%s' marcado a %.2f m y %.1f mils desde '%s'.",nombre, distancia, direccionMils, referencia.getNombre()),"Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Formato numérico inválido en dirección o distancia.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(e -> dialog.dispose());

        JRootPane rootPane = dialog.getRootPane();
        rootPane.setDefaultButton(btnAceptar);
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelar.doClick();
            }
        });
        dialog.setVisible(true);
    }
    
    private void mostrarDialogoMedir(Blanco b1) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Medir distancia desde: " + b1.getNombre(), true);
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panelDialog);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Seleccione el blanco de destino:");
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelDialog.add(lblTitulo, gbc);

        JComboBox<Blanco> comboBlancos = new JComboBox<>();
        comboBlancos.setBackground(new Color(70, 70, 70));
        comboBlancos.setForeground(Color.WHITE);

        comboBlancos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Blanco) {
                    Blanco b = (Blanco) value;
                    setText(b.NombretoString()); // usar tu método
                }
                setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
                setForeground(Color.WHITE);
                return this;
            }
        });
        for (Blanco b : listaDeBlancos) {
            if (b != b1) comboBlancos.addItem(b);
        }
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panelDialog.add(comboBlancos, gbc);

        // botones
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setBackground(new Color(50, 50, 50));
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelDialog.add(panelBotones, gbc);

        // acción de los botones
        btnAceptar.addActionListener(e -> {
            Blanco b2 = (Blanco) comboBlancos.getSelectedItem();
            if (b2 == null) {
                JOptionPane.showMessageDialog(dialog, "Seleccione un blanco destino válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double distancia = b1.getCoordenadas().distanciaA(b2.getCoordenadas());
                String mensaje = String.format("Distancia entre %s y %s:\n%.2f metros",b1.getNombre(), b2.getNombre(), distancia);
                JOptionPane.showMessageDialog(dialog, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,"Error al calcular la distancia:\n" + ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(e -> dialog.dispose());

        JRootPane rootPane = dialog.getRootPane();
        rootPane.setDefaultButton(btnAceptar);
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelar.doClick();
            }
        });
        dialog.setVisible(true);
    }

    
    private void mostrarDialogoEditar(Blanco b) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Editar Blanco", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panelDialog);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // campos
        JTextField txtNombre = new JTextField(b.getNombre());
        txtNombre.setPreferredSize(new Dimension(250, 30));
        JTextField txtNaturaleza = new JTextField(b.getNaturaleza());
        txtNaturaleza.setPreferredSize(new Dimension(250, 30));
        JTextField txtTipo = new JTextField(b.isAliado() ? "Aliado" : "Enemigo");
        txtTipo.setPreferredSize(new Dimension(250, 30));
        // coordenadas
        JTextField txtX = new JTextField(String.valueOf(b.getCoordenadas().getX()));
        JTextField txtY = new JTextField(String.valueOf(b.getCoordenadas().getY()));
        txtX.setPreferredSize(new Dimension(250, 30));
        txtY.setPreferredSize(new Dimension(250, 30));
        txtX.setEditable(false);
        txtY.setEditable(false);
        txtTipo.setEditable(false);
        
        JTextField[] fields = {txtNombre, txtNaturaleza, txtTipo, txtX, txtY};
        for (JTextField f : fields) {
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
            f.setCaretColor(Color.WHITE);
        }

        // labels
        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblNaturaleza = new JLabel("Naturaleza:");
        JLabel lblTipo = new JLabel("Tipo (Aliado/Enemigo):");
        JLabel lblX = new JLabel("Coordenada X:");
        JLabel lblY = new JLabel("Coordenada Y:");
        JLabel[] labels = {lblNombre, lblNaturaleza, lblTipo, lblX, lblY};
        for (JLabel l : labels) l.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; panelDialog.add(lblNombre, gbc);
        gbc.gridx = 1; panelDialog.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy++; panelDialog.add(lblNaturaleza, gbc);
        gbc.gridx = 1; panelDialog.add(txtNaturaleza, gbc);

        gbc.gridx = 0; gbc.gridy++; panelDialog.add(lblTipo, gbc);
        gbc.gridx = 1; panelDialog.add(txtTipo, gbc);

        gbc.gridx = 0; gbc.gridy++; panelDialog.add(lblX, gbc);
        gbc.gridx = 1; panelDialog.add(txtX, gbc);

        gbc.gridx = 0; gbc.gridy++; panelDialog.add(lblY, gbc);
        gbc.gridx = 1; panelDialog.add(txtY, gbc);

        // botones
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel panelBotonesDialog = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotonesDialog.setBackground(new Color(50, 50, 50));
        panelBotonesDialog.add(btnAceptar);
        panelBotonesDialog.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panelDialog.add(panelBotonesDialog, gbc);

        btnAceptar.addActionListener(e -> {
        	b.setNombre(txtNombre.getText());
            b.setNaturaleza(txtNaturaleza.getText());
            listaUI.repaint();
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.setMinimumSize(new Dimension(400, 350));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        
        JRootPane rootPane = dialog.getRootPane();
        rootPane.setDefaultButton(btnAceptar);
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelar.doClick();
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void actualizarBlancosEnMapa() {
        for (Blanco b : listaDeBlancos) {
            panelMapa.agregarBlanco(b);
        }
        listaUI.repaint();
    }
    
    private void armarPIF(Blanco b) {
    	JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Pedido Inicial de Fuego", true);
        dialog.setSize(1000,600);
        dialog.setLocationRelativeTo(this);

        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panelDialog);
        
        /////
        /*proximos camiobios*/
        /////
        
        dialog.setVisible(true);
    }

    @SuppressWarnings("serial")
    private void mostrarDialogoAgregar(Blanco blancoEditar, coordRectangulares coordInicial) {
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

        // 🔹 NUEVAS OPCIONES: forma geométrica y color automático
        String[] formas = {"círculo", "cruz", "triángulo"};
        JComboBox<String> comboForma = new JComboBox<>(formas);
        comboForma.setBackground(new Color(70,70,70));
        comboForma.setForeground(Color.WHITE);
        if (blancoEditar != null && blancoEditar.getForma()!=null) comboForma.setSelectedItem(blancoEditar.getForma());

        JLabel lblColor = new JLabel("Color:");
        lblColor.setForeground(Color.WHITE);
        JLabel lblColorPreview = new JLabel("    ");
        lblColorPreview.setOpaque(true);
        lblColorPreview.setBackground(chkAliado.isSelected() ? Color.BLUE : Color.RED);

        chkAliado.addActionListener(e -> {
            lblColorPreview.setBackground(chkAliado.isSelected() ? Color.BLUE : Color.RED);
        });

        gbc.gridx=0; gbc.gridy=6; panelDialog.add(new JLabel("Forma:"), gbc);
        gbc.gridx=1; panelDialog.add(comboForma, gbc);
        gbc.gridx=0; gbc.gridy=7; panelDialog.add(lblColor, gbc);
        gbc.gridx=1; panelDialog.add(lblColorPreview, gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel panelBotonesDialog = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotonesDialog.setBackground(new Color(50,50,50));
        panelBotonesDialog.add(btnAceptar);
        panelBotonesDialog.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; panelDialog.add(panelBotonesDialog, gbc);

        btnAceptar.addActionListener(ev -> {
            try {
                String nombre = txtNombre.getText().trim();
                String naturaleza = txtNaturaleza.getText().trim();
                String fecha = dtf.format(LocalDateTime.now());
                if (nombre.isEmpty() || naturaleza.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double x = Double.parseDouble(campoX.getText().trim());
                double y = Double.parseDouble(campoY.getText().trim());
                coordRectangulares coords = new coordRectangulares(x, y, 0);

                String forma = (String) comboForma.getSelectedItem();
                Color color = chkAliado.isSelected() ? Color.BLUE : Color.RED;

                if (blancoEditar == null) {
                    Blanco nuevo = new Blanco(nombre, coords, naturaleza, fecha, chkAliado.isSelected(), forma, color);
                    listaDeBlancos.add(nuevo);
                    modeloLista.addElement(nuevo);
                    panelMapa.agregarBlanco(nuevo);
                } else {
                    blancoEditar.setNombre(nombre);
                    blancoEditar.setNaturaleza(naturaleza);
                    blancoEditar.setFecha(fecha);
                    blancoEditar.setCoordenadas(coords);
                    blancoEditar.setAliado(chkAliado.isSelected());
                    blancoEditar.setForma(forma);
                    blancoEditar.setColor(color);
                    panelMapa.agregarBlanco(blancoEditar); 
                    listaUI.repaint();
                }
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Formato numérico inválido en X/Y.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(ev -> dialog.dispose());

        JRootPane rootPane = dialog.getRootPane();
        rootPane.setDefaultButton(btnAceptar);
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { btnCancelar.doClick(); }
        });

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
