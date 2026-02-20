package app;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import org.locationtech.jts.geom.Coordinate;

import dominio.*;
import util.*;

public class FabricaDialogosTacticos implements DialogFactory{

	private final Component padre;
    private final GestorSonido sonidos;
    private final SituacionTacticaTopografica sit;
    private CalculadorTopografico calculadora;
    private int contadorPlotting = 1;

    public FabricaDialogosTacticos(Component padre, GestorSonido sonidos) {
        this.padre = padre;
        this.sonidos = sonidos;
        sit = (SituacionTacticaTopografica) padre;
        calculadora = new CalculadorTopografico();
    }

	@SuppressWarnings("serial")
	@Override
	public void AgregarBlancoDialog(CoordenadasRectangulares coord, BlancoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "Nuevo Blanco", true);
        // Tamaño aumentado para acomodar componentes más grandes
        dialog.setSize(850, 750); 
        dialog.setLocationRelativeTo(padre);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); // Insets aumentados para mejor espaciado
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // DEFINICIÓN DE FUENTES ESCALADAS
        Font fuenteGrande = new Font("Arial", Font.BOLD, 18);
        Font fuenteMedia = new Font("Arial", Font.PLAIN, 16);

        // ORIENTACIÓN + NOMBRE
        JLabel lblOrient = new JLabel("Orientación:");
        lblOrient.setForeground(Color.WHITE);
        lblOrient.setFont(fuenteGrande);

        JTextField txtOrient = new JTextField();
        FabricaComponentes.addPlaceholder(txtOrient, "mils");
        txtOrient.setPreferredSize(new Dimension(120, 40)); // Dimensiones aumentadas
        txtOrient.setBackground(new Color(70, 70, 70));
        txtOrient.setForeground(Color.WHITE);
        txtOrient.setFont(fuenteMedia);

        JTextField txtNombre = new JTextField();
        FabricaComponentes.addPlaceholder(txtNombre, sit.getPrefijo() +" "+ sit.getContador());
        txtNombre.setBackground(new Color(70, 70, 70));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setPreferredSize(new Dimension(300, 40)); // Dimensiones aumentadas
        txtNombre.setFont(fuenteMedia);

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelNombre.setBackground(new Color(50, 50, 50));
        panelNombre.add(lblOrient);
        panelNombre.add(txtOrient);
        panelNombre.add(txtNombre);

        // FECHA
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFecha = new JTextField(dtf.format(LocalDateTime.now()));
        txtFecha.setEditable(false);
        txtFecha.setBackground(new Color(70, 70, 70));
        txtFecha.setForeground(Color.WHITE);
        txtFecha.setFont(fuenteMedia);
        txtFecha.setPreferredSize(new Dimension(0, 40));

        // COORDENADAS: DERECHAS (X) y ARRIBAS (Y)
        JTextField txtX = new JTextField(String.valueOf(coord.getX()));
        JTextField txtY = new JTextField(String.valueOf(coord.getY()));
        JTextField txtCota = new JTextField(String.valueOf(coord.getCota())); 

        txtCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Estilos para X, Y, COTA
        for (JTextField f : new JTextField[]{txtX, txtY, txtCota}) {
            f.setEditable(true);
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
            f.setFont(fuenteMedia);
            f.setPreferredSize(new Dimension(150, 35)); // Aumentado
        }

        txtCota.setPreferredSize(new Dimension(120, 35)); 
        
        JPanel panelCoordenadas = new JPanel(new GridBagLayout());
        panelCoordenadas.setBackground(new Color(50, 50, 50));
        GridBagConstraints gCoord = new GridBagConstraints();
        gCoord.insets = new Insets(2, 2, 4, 8); 
        gCoord.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCota = new JLabel("COTA");
        lblCota.setForeground(Color.WHITE);
        lblCota.setFont(fuenteGrande);
        lblCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        gCoord.gridx = 2; gCoord.gridy = 0; gCoord.weightx = 0;
        panelCoordenadas.add(lblCota, gCoord);

        gCoord.gridx = 2; gCoord.gridy = 1; gCoord.insets = new Insets(0, 0, 0, 8); 
        panelCoordenadas.add(txtCota, gCoord);
        
        gCoord.gridx = 0; gCoord.gridy = 0; gCoord.gridwidth = 2; gCoord.weightx = 1.0;
        gCoord.insets = new Insets(0, 0, 4, 6); 
        panelCoordenadas.add(txtX, gCoord);

        gCoord.gridx = 0; gCoord.gridy = 1; gCoord.insets = new Insets(0, 0, 0, 6);
        panelCoordenadas.add(txtY, gCoord);

        // COMBOS
        String[] entidades = {
            "INFANTERIA", "INFANTERIA-MOTORIZADA", "INFANTERIA-ANFIBIA",
            "INFANTERIA-MECANIZADA", "INFANTERIA-FORTIFICADA",
            "INFANTERIA-RECONOCIMIENTO", "INFANTERIA-REC-MOTORIZADA",
            "ANTITANQUE", "ANTITANQUE-BLINDADO", "ANTITANQUE-MOTORIZADO",
            "ARTILLERIA", "ARTILLERIA-AUTOPROPULSADA", "ARTILLERIA-ADQ-BLANCOS",
            "DEFENSA-AEREA", "MORTERO", "MORTERO-MOTORIZADO", "MORTERO-ACORAZADO",
            "INGENIEROS", "COMUNICACIONES", "GUERRA-ELECTRONICA",
            "COMANDO-Y-CONTROL", "GRUPO-LOGISTICO/APOYO",
            "OBSERVADOR", "OBSERVADOR-ARTILLERIA",
            "DRON-TERRESTRE", "INSTALACION-MEDICA"
        };

        String[] afiliaciones = {
            "HOSTIL", "ALIADO", "NEUTRO", "DESCONOCIDO",
            "ASUMIDO-ENEMIGO", "PENDIENTE", "ASUMIDO-AMIGO"
        };

        String[] escalafones = {
            "Por Defecto", "PELOTON", "COMPANIA", "GRUPO", "SECCION", "BATALLON"
        };

        JComboBox<String> cbEntidad = new JComboBox<>(entidades);
        JComboBox<String> cbAfiliacion = new JComboBox<>(afiliaciones);
        JComboBox<String> cbEchelon = new JComboBox<>(escalafones);
        JComboBox<SituacionMovimiento> cbEstado = new JComboBox<>(SituacionMovimiento.values());

        for (JComboBox<?> cb : new JComboBox[]{cbEntidad, cbAfiliacion, cbEchelon, cbEstado}) {
            cb.setPreferredSize(new Dimension(350, 45)); // Tamaño aumentado
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
            cb.setFont(fuenteMedia);
        }

        cbEstado.setSelectedItem(SituacionMovimiento.FIJO);

        ListCellRenderer<String> guionRenderer = (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.replace("-", " "));
            label.setOpaque(true);
            label.setFont(fuenteMedia); // Fuente en lista
            label.setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
            label.setForeground(Color.WHITE);
            return label;
        };

        cbEntidad.setRenderer(guionRenderer);
        cbAfiliacion.setRenderer(guionRenderer);

        // PANEL NATURALEZA 
        JPanel panelNaturaleza = new JPanel(new GridBagLayout());
        panelNaturaleza.setBackground(new Color(50, 50, 50));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(5, 5, 5, 5);
        g2.anchor = GridBagConstraints.WEST;

        JLabel lblEnt = new JLabel("Tipo:"); lblEnt.setForeground(Color.WHITE); lblEnt.setFont(fuenteGrande);
        JLabel lblAfi = new JLabel("Afiliación:"); lblAfi.setForeground(Color.WHITE); lblAfi.setFont(fuenteGrande);
        JLabel lblEsc = new JLabel("Magnitud:"); lblEsc.setForeground(Color.WHITE); lblEsc.setFont(fuenteGrande);
        JLabel lblSit = new JLabel("Estado:"); lblSit.setForeground(Color.WHITE); lblSit.setFont(fuenteGrande);

        g2.gridx = 0; g2.gridy = 0; panelNaturaleza.add(lblEnt, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEntidad, g2);

        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(lblAfi, g2);
        g2.gridx = 1; panelNaturaleza.add(cbAfiliacion, g2);

        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(lblEsc, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEchelon, g2);

        g2.gridx = 0; g2.gridy++; panelNaturaleza.add(lblSit, g2);
        g2.gridx = 1; panelNaturaleza.add(cbEstado, g2);

        // INFO ADICIONAL
        JLabel lblInfo = new JLabel("Información adicional:");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(fuenteGrande);

        JTextArea txtInfo = new JTextArea();
        FabricaComponentes.addPlaceholder(txtInfo, "Información adicional necesaria");
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(new Color(70, 70, 70));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setCaretColor(Color.WHITE);
        txtInfo.setFont(fuenteMedia);
        txtInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        txtInfo.setPreferredSize(new Dimension(350, 150));

        // GRILLA PRINCIPAL
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1; panel.add(panelNombre, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1; panel.add(panelNaturaleza, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(new JLabel("Fecha de creación:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1; panel.add(txtFecha, gbc);

        JPanel panelCoordLabels = new JPanel(new GridLayout(2, 1, 0, 10)); 
        panelCoordLabels.setBackground(new Color(50, 50, 50));
        
        JLabel lblX = new JLabel("DERECHAS:"); lblX.setForeground(Color.WHITE); lblX.setFont(fuenteGrande);
        lblX.setVerticalAlignment(SwingConstants.BOTTOM); 
        
        JLabel lblY = new JLabel("ARRIBAS:"); lblY.setForeground(Color.WHITE); lblY.setFont(fuenteGrande);
        lblY.setVerticalAlignment(SwingConstants.TOP); 

        panelCoordLabels.add(lblX);
        panelCoordLabels.add(lblY);
        
        gbc.gridx = 0; gbc.gridy++; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH; 
        panel.add(panelCoordLabels, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelCoordenadas, gbc); 
        
        gbc.gridheight = 1; gbc.gridy += 2; gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; panel.add(lblInfo, gbc);
        gbc.gridx = 1; panel.add(new JScrollPane(txtInfo), gbc);

        // BOTONES
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel botones = new JPanel(new GridLayout(1, 2, 20, 0));
        botones.setBackground(new Color(50, 50, 50));
        for(JButton b : new JButton[]{btnAceptar, btnCancelar}) {
            b.setFont(fuenteGrande);
            b.setPreferredSize(new Dimension(0, 60)); // Botones mucho más altos
            b.setFocusPainted(false);
        }
        botones.add(btnAceptar);
        botones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(botones, gbc);

        // ACCIÓN BOTÓN ACEPTAR (Lógica intacta)
        btnAceptar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Ingrese un nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String nombreSugerido = sit.getPrefijo() + " " + sit.getContador();

            if (nombre.equals(nombreSugerido)) {
                int contActual = sit.getContador();
                sit.setContador(contActual + 1);
            }

            String entidad = (String) cbEntidad.getSelectedItem();
            String afiliacion = (String) cbAfiliacion.getSelectedItem();
            String echelon = (String) cbEchelon.getSelectedItem();
            String naturaleza = entidad + "_" + afiliacion;

            if (!echelon.equals("Por Defecto"))
                naturaleza += "_" + echelon.toUpperCase();
            
            double x = 0;
            double y = 0;
            double cota = 0;
            
            try {
                x = Double.parseDouble(txtX.getText().trim());
                y = Double.parseDouble(txtY.getText().trim());
                cota = Double.parseDouble(txtCota.getText().trim());
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Las coordenadas (DERECHAS, ARRIBAS, COTA) deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CoordenadasRectangulares coordAux = new CoordenadasRectangulares(x, y, cota);
            Blanco nuevo = new Blanco(nombre, coordAux, naturaleza, txtFecha.getText());

            nuevo.setUltAfiliacion(afiliacion);
            nuevo.setUltEchelon(echelon);
            nuevo.setUltEntidad(entidad);
            nuevo.setSimID(GestorCodigosSIDC.obtenerSIDC(naturaleza));
            nuevo.setSituacionMovimiento( (SituacionMovimiento) cbEstado.getSelectedItem());

            try {
                nuevo.setOrientacion(Double.parseDouble(txtOrient.getText().trim()));
            } catch (Exception ex) {
                nuevo.setOrientacion(0);
            }

            String info = txtInfo.getText().trim();
            if (info.equals("Información adicional necesaria")) info = "";
            nuevo.setInformacionAdicional(info);

            sit.getModeloListaBlancos().addElement(nuevo);
            sit.getPanelMapa().agregarBlanco(nuevo);
            sit.getListaBlancos().add(nuevo);
            
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}
	
	@Override
	public void AgregarPuntoDialog(CoordenadasRectangulares coord, PuntoCallback callback) {
	    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
	    JDialog dialog = new JDialog(parentFrame, "Nuevo Punto", true);
	    dialog.setSize(650, 550); 
	    dialog.setLocationRelativeTo(padre);

	    Font fuenteGrande = new Font("Arial", Font.BOLD, 22);
	    Font fuenteMedia = new Font("Arial", Font.PLAIN, 18);

	    JPanel panel = new JPanel(new GridBagLayout());
	    panel.setBackground(new Color(50, 50, 50));
	    dialog.setContentPane(panel);
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(12, 12, 12, 12); 
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    
	    JLabel lblNombre = new JLabel("Nombre del Punto:"); 
	    lblNombre.setForeground(Color.WHITE);
	    lblNombre.setFont(fuenteGrande);

	    JTextField txtNombre = new JTextField(); 
	    FabricaComponentes.addPlaceholder(txtNombre, "Nombre del Punto");
	    txtNombre.setBackground(new Color(70,70,70)); 
	    txtNombre.setForeground(Color.WHITE);
	    txtNombre.setFont(fuenteMedia);
	    txtNombre.setPreferredSize(new Dimension(300, 55));

	    JLabel lblX = new JLabel("DERECHAS (X):"); 
	    lblX.setForeground(Color.WHITE);
	    lblX.setFont(fuenteGrande);

	    JLabel lblY = new JLabel("ARRIBAS (Y):"); 
	    lblY.setForeground(Color.WHITE);
	    lblY.setFont(fuenteGrande);

	    JLabel lblZ = new JLabel("COTA (Z):"); 
	    lblZ.setForeground(Color.WHITE);
	    lblZ.setFont(fuenteGrande);

	    JTextField txtX = new JTextField(String.format(java.util.Locale.US, "%.2f", coord.getX()));
	    JTextField txtY = new JTextField(String.format(java.util.Locale.US, "%.2f", coord.getY()));
	    JTextField txtCota = new JTextField(String.format(java.util.Locale.US, "%.2f", coord.getCota()));
	    
	    for (JTextField f : new JTextField[]{txtX, txtY, txtCota}) {
	        f.setBackground(new Color(70, 70, 70));
	        f.setForeground(Color.WHITE);
	        f.setFont(fuenteMedia);
	        f.setPreferredSize(new Dimension(300, 50));
	    }
	    
	    txtX.setEditable(false);
	    txtY.setEditable(false);
	    txtCota.setEditable(true); 
	    
	    gbc.gridx=0; gbc.gridy=0; panel.add(lblNombre, gbc);
	    gbc.gridx=1; panel.add(txtNombre, gbc);
	    
	    gbc.gridx=0; gbc.gridy=1; panel.add(lblX, gbc);
	    gbc.gridx=1; panel.add(txtX, gbc);
	    
	    gbc.gridx=0; gbc.gridy=2; panel.add(lblY, gbc);
	    gbc.gridx=1; panel.add(txtY, gbc);

	    // Nuevo campo Cota en la fila 3
	    gbc.gridx=0; gbc.gridy=3; panel.add(lblZ, gbc);
	    gbc.gridx=1; panel.add(txtCota, gbc);

	    JButton btnAceptar = new JButton("Aceptar");
	    JButton btnCancelar = new JButton("Cancelar");
	    
	    for (JButton b : new JButton[]{btnAceptar, btnCancelar}) {
	        b.setFont(fuenteGrande);
	        b.setPreferredSize(new Dimension(0, 70)); 
	        b.setFocusPainted(false);
	    }

	    JPanel botones = new JPanel(new GridLayout(1, 2, 20, 0));
	    botones.setBackground(new Color(50, 50, 50));
	    botones.add(btnAceptar); 
	    botones.add(btnCancelar);
	   
	    gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; 
	    gbc.insets = new Insets(30, 12, 10, 12); 
	    panel.add(botones, gbc);

	    btnAceptar.addActionListener(e -> {
	        String nombre = txtNombre.getText().trim();
	        if (nombre.isEmpty() || nombre.equals("Nombre del Punto")) {
	            sonidos.clickError();
	            JOptionPane.showMessageDialog(dialog, "Ingrese un nombre para el punto.", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        try {
	            @SuppressWarnings("unused")
				double cotaIngresada = Double.parseDouble(txtCota.getText().trim());
	            
	            Punto nuevo = new Punto(coord, nombre);
	            
	            sit.getPanelMapa().agregarPoligonal(nuevo);
	            sit.getListaPoligonales().add(nuevo);
	            sit.getListaDePuntos().add(nuevo); 
	            sit.getModeloListaPoligonales().addElement(nuevo);
	            
	            sit.getPanelMapa().repaint();
	            
	            dialog.dispose();

	        } catch (NumberFormatException ex) {
	            sonidos.clickError();
	            JOptionPane.showMessageDialog(dialog, "La Cota debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    btnCancelar.addActionListener(e -> dialog.dispose());
	    dialog.setVisible(true);
	}

	@SuppressWarnings("serial")
	@Override
	public void EditarBlancoDialog(Blanco blanco, BlancoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "Editar Blanco", true);
        // Tamaño aumentado para acomodar el escalado de componentes
        dialog.setSize(850, 850); 
        dialog.setLocationRelativeTo(padre);

        // DEFINICIÓN DE FUENTES ESCALADAS
        Font fuenteGrande = new Font("Arial", Font.BOLD, 18);
        Font fuenteMedia = new Font("Arial", Font.PLAIN, 16);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0; 
        gbc.anchor = GridBagConstraints.EAST;

        // ORIENTACIÓN + NOMBRE
        JLabel lblOrient = new JLabel("Orientación:");
        lblOrient.setForeground(Color.WHITE);
        lblOrient.setFont(fuenteGrande);

        JTextField txtOrient = new JTextField(String.valueOf(blanco.getOrientacion()));
        txtOrient.setPreferredSize(new Dimension(120, 45)); // Aumentado
        txtOrient.setBackground(new Color(70, 70, 70));
        txtOrient.setForeground(Color.WHITE);
        txtOrient.setFont(fuenteMedia);

        JTextField txtNombre = new JTextField(blanco.getNombre());
        txtNombre.setPreferredSize(new Dimension(300, 45)); // Aumentado
        txtNombre.setBackground(new Color(70, 70, 70));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setFont(fuenteMedia);

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelNombre.setBackground(new Color(50, 50, 50));
        panelNombre.add(lblOrient);
        panelNombre.add(txtOrient);
        panelNombre.add(txtNombre);

        // COORDENADAS (solo lectura X, Y - editable COTA)
        JTextField txtX = new JTextField(String.valueOf(blanco.getCoordenadas().getX()));
        JTextField txtY = new JTextField(String.valueOf(blanco.getCoordenadas().getY()));
        JTextField txtCota = new JTextField(String.valueOf(blanco.getCoordenadas().getCota())); 
        txtCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (JTextField f : new JTextField[]{txtX, txtY, txtCota}) {
            f.setBackground(new Color(70, 70, 70));
            f.setForeground(Color.WHITE);
            f.setFont(fuenteMedia);
            f.setPreferredSize(new Dimension(200, 40)); 
        }
        txtX.setEditable(false);
        txtY.setEditable(false);
        txtCota.setEditable(true);
        txtCota.setPreferredSize(new Dimension(120, 40)); 

        // PANEL DE COORDENADAS
        JPanel panelCoordenadas = new JPanel(new GridBagLayout());
        panelCoordenadas.setBackground(new Color(50, 50, 50));
        GridBagConstraints gCoord = new GridBagConstraints();
        gCoord.insets = new Insets(0, 0, 5, 10); 
        gCoord.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCota = new JLabel("COTA");
        lblCota.setForeground(Color.WHITE);
        lblCota.setFont(fuenteGrande);
        lblCota.setHorizontalAlignment(SwingConstants.CENTER);
        
        gCoord.gridx = 2; gCoord.gridy = 0; gCoord.weightx = 0; 
        panelCoordenadas.add(lblCota, gCoord);

        gCoord.gridx = 2; gCoord.gridy = 1; gCoord.insets = new Insets(0, 0, 0, 10);
        panelCoordenadas.add(txtCota, gCoord);
        
        gCoord.gridx = 0; gCoord.gridy = 0; gCoord.gridwidth = 2; 
        gCoord.weightx = 1.0; 
        gCoord.insets = new Insets(0, 0, 5, 8); 
        panelCoordenadas.add(txtX, gCoord);

        gCoord.gridx = 0; gCoord.gridy = 1; 
        gCoord.insets = new Insets(0, 0, 0, 8);
        panelCoordenadas.add(txtY, gCoord);

        // FECHA ACTUALIZACIÓN
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField txtFechaAct = new JTextField(dtf.format(LocalDateTime.now()));
        txtFechaAct.setEditable(false);
        txtFechaAct.setPreferredSize(new Dimension(350, 45));
        txtFechaAct.setBackground(new Color(70, 70, 70));
        txtFechaAct.setForeground(Color.WHITE);
        txtFechaAct.setFont(fuenteMedia);

        // COMBOS
        String[] entidades = {
            "INFANTERIA", "INFANTERIA-MOTORIZADA", "INFANTERIA-ANFIBIA",
            "INFANTERIA-MECANIZADA", "INFANTERIA-FORTIFICADA",
            "INFANTERIA-RECONOCIMIENTO", "INFANTERIA-REC-MOTORIZADA",
            "ANTITANQUE", "ANTITANQUE-BLINDADO", "ANTITANQUE-MOTORIZADO",
            "ARTILLERIA", "ARTILLERIA-AUTOPROPULSADA", "ARTILLERIA-ADQ-BLANCOS",
            "DEFENSA-AEREA", "MORTERO", "MORTERO-MOTORIZADO", "MORTERO-ACORAZADO",
            "INGENIEROS", "COMUNICACIONES", "GUERRA-ELECTRONICA",
            "COMANDO-Y-CONTROL", "GRUPO-LOGISTICO/APOYO",
            "OBSERVADOR", "OBSERVADOR-ARTILLERIA",
            "DRON-TERRESTRE", "INSTALACION-MEDICA"
        };

        String[] afiliaciones = {
            "ALIADO", "HOSTIL", "NEUTRO", "DESCONOCIDO",
            "ASUMIDO-ENEMIGO", "PENDIENTE", "ASUMIDO-AMIGO"
        };

        String[] escalafones = {
                "Por Defecto", "PELOTON", "COMPANIA", "GRUPO", "SECCION", "BATALLON"
        };

        JComboBox<String> cbEntidad = new JComboBox<>(entidades);
        JComboBox<String> cbAfiliacion = new JComboBox<>(afiliaciones);
        JComboBox<String> cbEchelon = new JComboBox<>(escalafones);

        cbEntidad.setSelectedItem(blanco.getUltEntidad());
        cbAfiliacion.setSelectedItem(blanco.getUltAfiliacion());
        cbEchelon.setSelectedItem(blanco.getUltEchelon());

        for (JComboBox<?> cb : new JComboBox[]{cbEntidad, cbAfiliacion, cbEchelon}) {
            cb.setPreferredSize(new Dimension(350, 50));
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
            cb.setFont(fuenteMedia);
        }

        ListCellRenderer<String> guionRenderer = (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? "" : value.replace("-", " "));
            label.setOpaque(true);
            label.setFont(fuenteMedia);
            label.setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
            label.setForeground(Color.WHITE);
            return label;
        };
        cbEntidad.setRenderer(guionRenderer);
        cbAfiliacion.setRenderer(guionRenderer);

        // SITUACIÓN
        JLabel lblSituacion = new JLabel("Estado:");
        lblSituacion.setForeground(Color.WHITE);
        lblSituacion.setFont(fuenteGrande);

        JComboBox<SituacionMovimiento> cbEstado = new JComboBox<>(SituacionMovimiento.values());
        cbEstado.setPreferredSize(new Dimension(350, 50));
        cbEstado.setBackground(new Color(70, 70, 70));
        cbEstado.setForeground(Color.WHITE);
        cbEstado.setFont(fuenteMedia);
        cbEstado.setSelectedItem(blanco.getSituacionMovimiento());

        // PANEL NATURALEZA
        JPanel panelNaturaleza = new JPanel(new GridBagLayout());
        panelNaturaleza.setBackground(new Color(50, 50, 50));

        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(5, 5, 5, 5);
        g2.anchor = GridBagConstraints.WEST;

        g2.gridx = 0; g2.gridy = 0;
        panelNaturaleza.add(new JLabel("Tipo:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEntidad, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(new JLabel("Afiliación:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbAfiliacion, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(new JLabel("Magnitud:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEchelon, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(lblSituacion, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEstado, g2);

        // INFO ADICIONAL
        JLabel lblInfo = new JLabel("Información adicional:");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(fuenteGrande);

        JTextArea txtInfo = new JTextArea(
        		blanco.getInformacionAdicional() != null && !blanco.getInformacionAdicional().isEmpty()
                ? blanco.getInformacionAdicional() : "Información adicional necesaria"
        );
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(new Color(70, 70, 70));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setCaretColor(Color.WHITE);
        txtInfo.setFont(fuenteMedia);
        txtInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        txtInfo.setPreferredSize(new Dimension(350, 150));

        // ARMADO GRILLA PRINCIPAL
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1;
        panel.add(panelNombre, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1;
        panel.add(panelNaturaleza, gbc);

        JPanel panelCoordLabels = new JPanel(new GridLayout(2, 1, 0, 8)); 
        panelCoordLabels.setBackground(new Color(50, 50, 50));
        
        JLabel lblX = new JLabel("DERECHAS:"); lblX.setForeground(Color.WHITE); lblX.setFont(fuenteGrande);
        lblX.setVerticalAlignment(SwingConstants.BOTTOM); 
        
        JLabel lblY = new JLabel("ARRIBAS:"); lblY.setForeground(Color.WHITE); lblY.setFont(fuenteGrande);
        lblY.setVerticalAlignment(SwingConstants.TOP); 

        panelCoordLabels.add(lblX);
        panelCoordLabels.add(lblY);
        
        gbc.gridx = 0; gbc.gridy++; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH; 
        panel.add(panelCoordLabels, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelCoordenadas, gbc); 
        
        gbc.gridheight = 1; gbc.gridy += 2; gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; 
        panel.add(new JLabel("Fecha act.:") {{ setForeground(Color.WHITE); setFont(fuenteGrande); }}, gbc);
        gbc.gridx = 1;
        panel.add(txtFechaAct, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(lblInfo, gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(txtInfo) {{ 
            setPreferredSize(new Dimension(400, 160)); 
            getVerticalScrollBar().setPreferredSize(new Dimension(35, 0));
        }}, gbc);

        // BOTONES
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 20, 0));
        panelBotones.setBackground(new Color(50, 50, 50));
        for (JButton bBtn : new JButton[]{btnAceptar, btnCancelar}) {
            bBtn.setFont(fuenteGrande);
            bBtn.setPreferredSize(new Dimension(0, 70));
            bBtn.setFocusPainted(false);
        }
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(panelBotones, gbc);

        // ACCIÓN ACEPTAR
        btnAceptar.addActionListener(e -> {
            try {
                String entidad = (String) cbEntidad.getSelectedItem();
                String afiliacion = (String) cbAfiliacion.getSelectedItem();
                String echelon = (String) cbEchelon.getSelectedItem();

                String naturaleza = entidad + "_" + afiliacion;
                if (!echelon.equals("Por Defecto"))
                    naturaleza += "_" + echelon.toUpperCase();
                
                double nuevaCota;
                try {
                    nuevaCota = Double.parseDouble(txtCota.getText().trim());
                    blanco.getCoordenadas().setCota(nuevaCota); 
                } catch (NumberFormatException ex) {
                    sonidos.clickError();
                    JOptionPane.showMessageDialog(dialog, "La COTA debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                blanco.setNombre(txtNombre.getText().trim());
                blanco.setNaturaleza(naturaleza);
                blanco.setFecha(txtFechaAct.getText());
                blanco.setSituacionMovimiento((SituacionMovimiento) cbEstado.getSelectedItem());

                String infoAd = txtInfo.getText().trim();
                if (infoAd.equals("Información adicional necesaria")) infoAd = "";
                blanco.setInformacionAdicional(infoAd);

                try { blanco.setOrientacion(Double.parseDouble(txtOrient.getText().trim())); } 
                catch (Exception ex) { blanco.setOrientacion(0); }

                blanco.setUltEntidad(entidad);
                blanco.setUltAfiliacion(afiliacion);
                blanco.setUltEchelon(echelon);
                blanco.setSimID(GestorCodigosSIDC.obtenerSIDC(naturaleza));

                sit.getlistaUIBlancos().repaint();
                sit.getPanelMapa().eliminarBlanco(blanco);

                new Thread(() -> {
                    try {
                        Thread.sleep(150);
                        SwingUtilities.invokeLater(() -> {
                        	sit.getPanelMapa().agregarBlanco(blanco);
                        	sit.getPanelMapa().repaint();
                        });
                    } catch (InterruptedException ignored) {}
                }).start();

                dialog.dispose();

            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error al guardar cambios:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}

	@SuppressWarnings("serial")
	@Override
	public void AgregarEnPolaresDialog(Blanco blanco, BlancoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "Marcado en Polares ", true);
        dialog.setSize(800, 740);
        dialog.setLocationRelativeTo(padre);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));
        dialog.setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TÍTULO
        JLabel lblTitulo = new JLabel("BLANCO DE REFERENCIA: " + blanco.getNombre());
        lblTitulo.setForeground(Color.RED);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 15f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        gbc.gridwidth = 1;

        JLabel lblOrient = new JLabel("Orientación:");
        lblOrient.setForeground(Color.WHITE);

        JTextField txtOrient = new JTextField("0");
        txtOrient.setPreferredSize(new Dimension(80, 26));
        txtOrient.setBackground(new Color(70, 70, 70));
        txtOrient.setForeground(Color.WHITE);

        JTextField txtNombre = new JTextField();
        txtNombre.setPreferredSize(new Dimension(180, 26));
        txtNombre.setBackground(new Color(70, 70, 70));
        txtNombre.setForeground(Color.WHITE);

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelNombre.setBackground(new Color(50, 50, 50));
        panelNombre.add(lblOrient);
        panelNombre.add(txtOrient);
        panelNombre.add(txtNombre);

        // POLARES (DIR, DISTANCIA, ANGULO)
        JLabel lblDir = new JLabel("Dirección (mil):");
        JLabel lblDist = new JLabel("Distancia (m):");
        JLabel lblAng = new JLabel("Cota (mil):");
        for (JLabel l : new JLabel[]{lblDir, lblDist, lblAng}) l.setForeground(Color.WHITE);

        JTextField txtDir = new JTextField();
        JTextField txtDist = new JTextField();
        JTextField txtAng = new JTextField("0");

        for (JTextField t : new JTextField[]{txtDir, txtDist, txtAng}) {
            t.setPreferredSize(new Dimension(160, 26));
            t.setBackground(new Color(70, 70, 70));
            t.setForeground(Color.WHITE);
        }

        // COMBOS
        String[] entidades = {
            "INFANTERIA", "INFANTERIA-MOTORIZADA", "INFANTERIA-ANFIBIA",
            "INFANTERIA-MECANIZADA", "INFANTERIA-FORTIFICADA",
            "INFANTERIA-RECONOCIMIENTO", "INFANTERIA-REC-MOTORIZADA",
            "ANTITANQUE", "ANTITANQUE-BLINDADO", "ANTITANQUE-MOTORIZADO",
            "ARTILLERIA", "ARTILLERIA-AUTOPROPULSADA", "ARTILLERIA-ADQ-BLANCOS",
            "DEFENSA-AEREA", "MORTERO", "MORTERO-MOTORIZADO", "MORTERO-ACORAZADO",
            "INGENIEROS", "COMUNICACIONES", "GUERRA-ELECTRONICA",
            "COMANDO-Y-CONTROL", "GRUPO-LOGISTICO/APOYO",
            "OBSERVADOR", "OBSERVADOR-ARTILLERIA",
            "DRON-TERRESTRE", "INSTALACION-MEDICA"
        };

        String[] afiliaciones = {
            "HOSTIL", "ALIADO", "NEUTRO", "DESCONOCIDO",
            "ASUMIDO-ENEMIGO", "PENDIENTE", "ASUMIDO-AMIGO"
        };

        String[] escalafones = {
                "Por Defecto", "PELOTON", "COMPANIA", "GRUPO", "SECCION", "BATALLON"
        };

        JComboBox<String> cbEntidad = new JComboBox<>(entidades);
        JComboBox<String> cbAfiliacion = new JComboBox<>(afiliaciones);
        JComboBox<String> cbEchelon = new JComboBox<>(escalafones);
        JComboBox<SituacionMovimiento> cbSituacion = new JComboBox<>(SituacionMovimiento.values());

        for (JComboBox<?> cb : new JComboBox[]{cbEntidad, cbAfiliacion, cbEchelon, cbSituacion}) {
            cb.setPreferredSize(new Dimension(220, 26));
            cb.setBackground(new Color(70, 70, 70));
            cb.setForeground(Color.WHITE);
        }

        cbSituacion.setSelectedItem(SituacionMovimiento.FIJO);

        JLabel lblSituacion = new JLabel("Estado:");
        lblSituacion.setForeground(Color.WHITE);

        // PANEL NATURALEZA (Tipo + Afiliación + Magnitud + Estado)
        JPanel panelNaturaleza = new JPanel(new GridBagLayout());
        panelNaturaleza.setBackground(new Color(50, 50, 50));

        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(2, 4, 2, 4);
        g2.anchor = GridBagConstraints.WEST;

        g2.gridx = 0; g2.gridy = 0;
        panelNaturaleza.add(new JLabel("Tipo:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEntidad, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(new JLabel("Afiliación:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbAfiliacion, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(new JLabel("Magnitud:") {{ setForeground(Color.WHITE); }}, g2);
        g2.gridx = 1;
        panelNaturaleza.add(cbEchelon, g2);

        g2.gridx = 0; g2.gridy++;
        panelNaturaleza.add(lblSituacion, g2);

        g2.gridx = 1;
        panelNaturaleza.add(cbSituacion, g2);

        // INFORMACIÓN ADICIONAL
        JLabel lblInfo = new JLabel("Información adicional:");
        lblInfo.setForeground(Color.WHITE);

        JTextArea txtInfo = new JTextArea();
        FabricaComponentes.addPlaceholder(txtInfo, "Información adicional necesaria");
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(new Color(70, 70, 70));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setCaretColor(Color.WHITE);
        txtInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        txtInfo.setPreferredSize(new Dimension(240, 120));

        // GRILLA PRINCIPAL
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nombre:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1;
        panel.add(panelNombre, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Naturaleza:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1;
        panel.add(panelNaturaleza, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(lblDir, gbc);
        gbc.gridx = 1; panel.add(txtDir, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(lblDist, gbc);
        gbc.gridx = 1; panel.add(txtDist, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(lblAng, gbc);
        gbc.gridx = 1; panel.add(txtAng, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(lblInfo, gbc);
        gbc.gridx = 1; panel.add(new JScrollPane(txtInfo), gbc);

        // BOTONES
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
        botones.setBackground(new Color(50, 50, 50));
        botones.add(btnAceptar);
        botones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(botones, gbc);

        // ACCIÓN ACEPTAR
        btnAceptar.addActionListener(e -> {
            try {
                double direccion = Double.parseDouble(txtDir.getText().trim());
                double distancia = Double.parseDouble(txtDist.getText().trim());
                double angulo = Double.parseDouble(txtAng.getText().trim());

                CoordenadasRectangulares refCoord = (CoordenadasRectangulares) blanco.getCoordenadas();
                CoordenadasPolares polar = new CoordenadasPolares(direccion, distancia, angulo, refCoord);
                CoordenadasRectangulares nuevas = polar.toRectangulares();

                String nombre = txtNombre.getText().trim();

                String entidad = (String) cbEntidad.getSelectedItem();
                String afiliacion = (String) cbAfiliacion.getSelectedItem();
                String echelon = (String) cbEchelon.getSelectedItem();

                String naturaleza = entidad + "_" + afiliacion;
                if (!echelon.equals("Por Defecto"))
                    naturaleza += "_" + echelon.toUpperCase();

                Blanco nuevo = new Blanco(nombre, nuevas, naturaleza, LocalDateTime.now().toString());
                nuevo.setSimID(GestorCodigosSIDC.obtenerSIDC(naturaleza));
                nuevo.setSituacionMovimiento((SituacionMovimiento) cbSituacion.getSelectedItem());
                nuevo.setOrientacion(Double.parseDouble(txtOrient.getText().trim()));
                nuevo.setUltEntidad(entidad);
                nuevo.setUltAfiliacion(afiliacion);
                nuevo.setUltEchelon(echelon);

                String info = txtInfo.getText().trim();
                if (info.equals("Información adicional necesaria")) info = "";
                nuevo.setInformacionAdicional(info);

                sit.getListaBlancos().add(nuevo);
                sit.getModeloListaBlancos().addElement(nuevo);
                sit.getPanelMapa().agregarBlanco(nuevo);

                dialog.dispose();

            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Datos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}

	@Override
	public void InfoBlancoDialog(Blanco blanco) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre); 
        JDialog dialogo = new JDialog(parentFrame, "Detalle del Blanco: " + blanco.getNombre(), true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 1. ESCALADO DE FUENTES (+40%)
        Font fTitulo = new Font("Arial", Font.BOLD, 25);   
        Font fTexto = new Font("Consolas", Font.PLAIN, 21); 

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.BLACK);
        contenido.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // PANEL DE INFORMACIÓN PRINCIPAL
        JPanel panelBlanco = new JPanel();
        panelBlanco.setLayout(new BoxLayout(panelBlanco, BoxLayout.Y_AXIS));
        panelBlanco.setBackground(Color.BLACK);
        panelBlanco.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "INFORMACIÓN DEL BLANCO",
                        0, 0, fTitulo, Color.WHITE
                )
        );

        int separacion = 12;

        panelBlanco.add(FabricaComponentes.crearLinea2("Nombre: ", blanco.getNombre(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("Fecha Creación: ", blanco.getFechaDeActualizacion(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("SIM ID (SIDC): ", blanco.getSimID(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("Orientación: ", String.format("%.2f mils", blanco.getOrientacion()), fTexto));
        panelBlanco.add(Box.createVerticalStrut(separacion));
        
        panelBlanco.add(FabricaComponentes.crearLinea2("Coordenadas: ", "", fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - DERECHAS (X): ", String.format("%.6f", blanco.getCoordenadas().getX()), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - ARRIBAS (Y): ", String.format("%.6f", blanco.getCoordenadas().getY()), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - COTA (Z): ", String.format("%.2f m", blanco.getCoordenadas().getCota()), fTexto)); 
        panelBlanco.add(Box.createVerticalStrut(separacion));

        panelBlanco.add(FabricaComponentes.crearLinea2("Naturaleza: ", blanco.getNaturaleza(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - Tipo (Entidad): ", blanco.getUltEntidad(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - Afiliación: ", blanco.getUltAfiliacion(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - Magnitud : ", blanco.getUltEchelon(), fTexto));
        panelBlanco.add(FabricaComponentes.crearLinea2("  - Situación Mov.: ", String.valueOf(blanco.getSituacionMovimiento()), fTexto));

        contenido.add(panelBlanco);
        contenido.add(Box.createVerticalStrut(20));
        
        // PANEL DE INFORMACIÓN ADICIONAL
        JPanel panelInfoAdicional = new JPanel();
        panelInfoAdicional.setLayout(new BoxLayout(panelInfoAdicional, BoxLayout.Y_AXIS));
        panelInfoAdicional.setBackground(Color.BLACK);
        panelInfoAdicional.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "DETALLES ADICIONALES",
                        0, 0, fTitulo, Color.WHITE
                )
        );
        
        String info = (blanco.getInformacionAdicional() == null || blanco.getInformacionAdicional().trim().isEmpty()) 
                      ? "Ninguna información adicional registrada." : blanco.getInformacionAdicional();
        
        JTextArea txtInfo = new JTextArea(info);
        txtInfo.setFont(fTexto);
        txtInfo.setBackground(new Color(50, 50, 50));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setEditable(false);
        txtInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); 
        scrollInfo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        // Barra deslizadora interna ancha
        scrollInfo.getVerticalScrollBar().setPreferredSize(new Dimension(30, 0));

        panelInfoAdicional.add(scrollInfo);
        contenido.add(panelInfoAdicional);

        // 3. SCROLL PRINCIPAL Y BARRA DESLIZADORA GENERAL (TÁCTICA)
        JScrollPane scrollPrincipal = new JScrollPane(contenido);
        // Barra de desplazamiento general muy ancha para facilitar el uso
        scrollPrincipal.getVerticalScrollBar().setPreferredSize(new Dimension(40, 0)); 
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(20); // Scroll más fluido

        dialogo.getContentPane().add(scrollPrincipal);
        
        // Aumentamos el tamaño para evitar que los datos largos se corten a la derecha
        dialogo.setSize(1000, 800); 
        dialogo.setLocationRelativeTo(parentFrame);
        dialogo.setVisible(true);
	}

	@Override
	public void InfoPuntoDialog(Posicionable p) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre); 
        JDialog dialogo = new JDialog(parentFrame, "Detalle del Punto: " + p.getNombre(), true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 1. ESCALADO DE FUENTES (+40%)
        Font fTitulo = new Font("Arial", Font.BOLD, 25);   
        Font fTexto = new Font("Consolas", Font.PLAIN, 21); 

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.BLACK);
        // Margen más amplio para acompañar el tamaño de letra
        contenido.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // PANEL DE INFORMACIÓN BÁSICA
        JPanel panelPunto = new JPanel();
        panelPunto.setLayout(new BoxLayout(panelPunto, BoxLayout.Y_AXIS));
        panelPunto.setBackground(Color.BLACK);
        panelPunto.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "DATOS DEL PUNTO",
                        0, 0, fTitulo, Color.WHITE
                )
        );

        // Nombre y separadores escalados
        panelPunto.add(FabricaComponentes.crearLinea2("Nombre: ", p.getNombre(), fTexto));
        panelPunto.add(Box.createVerticalStrut(15));
        
        // Coordenadas con formato de alta precisión
        panelPunto.add(FabricaComponentes.crearLinea2("Coordenadas: ", "", fTexto));
        panelPunto.add(FabricaComponentes.crearLinea2("  - DERECHAS (X): ", String.format("%.6f", p.getCoordenadas().getX()), fTexto));
        panelPunto.add(FabricaComponentes.crearLinea2("  - ARRIBAS (Y): ", String.format("%.6f", p.getCoordenadas().getY()), fTexto));
        
        // Cota (Z)
        panelPunto.add(FabricaComponentes.crearLinea2("  - COTA (Z): ", String.format("%.2f m", p.getCoordenadas().getCota()), fTexto)); 

        contenido.add(panelPunto);

        // 2. USO DE SCROLL TÁCTICO (Previsión por si la resolución es baja o el nombre es largo)
        JScrollPane scrollPrincipal = new JScrollPane(contenido);
        scrollPrincipal.getVerticalScrollBar().setPreferredSize(new Dimension(40, 0)); 
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(20);

        dialogo.getContentPane().add(scrollPrincipal);
        
        // 3. TAMAÑO AJUSTADO PARA FUENTES GRANDES
        // Ancho suficiente para que las coordenadas no se corten
        dialogo.setSize(850, 450); 
        dialogo.setLocationRelativeTo(parentFrame);
        dialogo.setVisible(true);
	}

	@Override
	public void ConfiguracionDialog() {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "SISTEMA - CONFIGURACIÓN TÁCTICA", true);
        dialog.setSize(750, 600); 
        dialog.setLocationRelativeTo(padre);
        dialog.setResizable(false);

        // Panel principal con fondo oscuro 
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 20));
        panelPrincipal.setBackground(new Color(30, 30, 30));
        panelPrincipal.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 3),BorderFactory.createEmptyBorder(30, 30, 30, 30) ));

        JLabel lblHeader = new JLabel("UNIDAD DE CONTROL DE DATOS");
        lblHeader.setForeground(new Color(200, 200, 200));
        lblHeader.setFont(new Font("Consolas", Font.BOLD, 24)); 
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblHeader, BorderLayout.NORTH);

        // Contenedor de botones 
        JPanel panelContenido = new JPanel(new GridLayout(2, 1, 25, 25));
        panelContenido.setOpaque(false);
        
        JButton btnMapa = new JButton("<html><center><font size='6'>ACTUALIZAR CARTOGRAFÍA</font><br>"
                                    + "<font size='5' color='#BBBBBB'>Cargar nuevo archivo GeoTIFF</font></center></html>");
        FabricaComponentes.configurarBotonMilitar(btnMapa, new Font("Arial", Font.BOLD, 22), new Color(130, 40, 40)); 
        btnMapa.setPreferredSize(new Dimension(0, 150)); // Altura táctica para tablet
        btnMapa.addActionListener(e -> {
            dialog.dispose();
            sit.cambiarMapaEnTiempoReal();
        });

        JButton btnDesig = new JButton("<html><center><font size='6'>MODIFICAR DESIGNACIÓN</font><br>"
                                     + "<font size='5' color='#BBBBBB'>Prefijo y contador de blancos</font></center></html>");
        FabricaComponentes.configurarBotonMilitar(btnDesig, new Font("Arial", Font.BOLD, 22), new Color(40, 70, 130));
        btnDesig.setPreferredSize(new Dimension(0, 150));
        btnDesig.addActionListener(e -> {
            dialog.dispose();
            sit.cambiarDesignacionEnTiempoReal();
        });

        panelContenido.add(btnMapa);
        panelContenido.add(btnDesig);
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);

        dialog.add(panelPrincipal);
        dialog.setVisible(true);
	}

	@Override
	public void MedirDialog(Posicionable origen) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "Medir distancia desde: " + origen.getNombre(), true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(padre);

        Font fuenteTitulo = new Font("Arial", Font.BOLD, 18);
        Font fuenteComponente = new Font("Arial", Font.PLAIN, 20);

        JPanel panelDialog = new JPanel(new GridBagLayout());
        panelDialog.setBackground(new Color(50, 50, 50));
        panelDialog.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialog.setContentPane(panelDialog);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Seleccione el destino (Blanco o Punto):");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(fuenteTitulo); 	
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelDialog.add(lblTitulo, gbc);

        JComboBox<Posicionable> comboDestinos = new JComboBox<>();

        comboDestinos.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value != null) {
                    Posicionable p = (Posicionable) value;
                    setText(p.getPrefijoTipo() + p.getNombre());
                }
                
                setFont(fuenteComponente);
                setBackground(isSelected ? new Color(100, 100, 100) : new Color(70, 70, 70));
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
        
        comboDestinos.setBackground(new Color(70, 70, 70));
        comboDestinos.setForeground(Color.WHITE);
        comboDestinos.setFont(fuenteComponente);
        comboDestinos.setPreferredSize(new Dimension(0, 60));
        
        if (sit.getListaBlancos() != null) {
            for (Blanco b : sit.getListaBlancos()) {
                if (!b.equals(origen)) comboDestinos.addItem(b);
            }
        }
        
        if (sit.getListaPuntos() != null) {
            for (Punto p : sit.getListaPuntos()) {
                    comboDestinos.addItem(p);
            }
        }
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panelDialog.add(comboDestinos, gbc);

        // BOTONES GIGANTES PARA TABLET
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 20, 0));
        panelBotones.setBackground(new Color(50, 50, 50));
        
        for (JButton b : new JButton[]{btnAceptar, btnCancelar}) {
            b.setFont(fuenteTitulo);
            b.setPreferredSize(new Dimension(0, 80));
            b.setFocusPainted(false);
        }
        
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        panelDialog.add(panelBotones, gbc);

        // ACCIÓN DE LOS BOTONES
        btnAceptar.addActionListener(e -> {
            Posicionable destino = (Posicionable) comboDestinos.getSelectedItem();
            if (destino == null) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Seleccione un destino válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double x1 = origen.getCoordenadas().getX();
                double y1 = origen.getCoordenadas().getY();
                double x2 = destino.getCoordenadas().getX();
                double y2 = destino.getCoordenadas().getY();
                
                System.out.println(x1 + " " + y1);

                double distancia = origen.getCoordenadas().distanciaA(destino.getCoordenadas());
                double azimutMils = calculadora.calcularAzimutEnMils(x1, y1, x2, y2);

                String resultado = String.format(
                        "Distancia entre %s y %s:\n%.0f metros\n\nAD: %.0f milésimos",
                        origen.getNombre(), destino.getNombre(), distancia, azimutMils
                );

                JOptionPane.showMessageDialog(dialog, resultado, "Resultado de Medición", JOptionPane.INFORMATION_MESSAGE);

                Coordinate c1 = new Coordinate();
                c1.setX(origen.getCoordenadas().getX());
                c1.setY(origen.getCoordenadas().getY());
                Coordinate c2 = new Coordinate();
                c2.setX(destino.getCoordenadas().getX());
                c2.setY(destino.getCoordenadas().getY());
                
                String nombreLinea = origen.getNombre() + "→" + destino.getNombre();
                Linea nuevaLinea = new Linea(nombreLinea, c1,c2, distancia, azimutMils);
                
                sit.getListaPoligonales().add(nuevaLinea);
                sit.getModeloListaPoligonales().addElement(nuevaLinea);
                sit.getPanelMapa().agregarPoligonal(nuevaLinea);
                
                String medicionData = String.format("Origen: %s -> Destino: %s | Dist: %.2f m | Az: %.0f mil", 
                        origen.getNombre(), destino.getNombre(), distancia, azimutMils);

				RegistroCalculos.guardar("MEDICIÓN AYD", medicionData);
                
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                sonidos.clickError();
            }
        });
        	
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}

	@Override
	public void CierrePoligonalDialog(Punto puntoCalculado, List<Punto> puntosReales, CalculoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "CONTROL DE PRECISIÓN: CIERRE DE POLIGONAL", true);
        dialog.setSize(600, 500); 
        dialog.setLocationRelativeTo(padre);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fCombo = new Font("Arial", Font.PLAIN, 18);
        Font fInput = new Font("Monospaced", Font.BOLD, 22);

        // 1. Info del Punto Calculado (Llegada)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("<html><center>PUNTO DE CIERRE EVALUADO:<br><font color='#00FF00' size='5'>" + puntoCalculado.getNombre() + "</font></center></html>");
        lblInfo.setFont(fLabel);
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(lblInfo, gbc);

        // Separador
        gbc.gridy = 1; 
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // 2. Punto Real (Origen Verdadero)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(FabricaComponentes.crearEtiqueta("PUNTO REAL DE ORIGEN (Para comparar)", fLabel), gbc);
        
        JComboBox<Punto> comboReales = new JComboBox<>();
        comboReales.setFont(fCombo);
        for (Punto p : puntosReales) {
            if (!p.equals(puntoCalculado)) comboReales.addItem(p);
        }
        gbc.gridx = 1; formPanel.add(comboReales, gbc);
        
        // 3. Perímetro / Distancia Recorrida
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(FabricaComponentes.crearEtiqueta("LONGITUD TOTAL DE LA POLIGONAL (m)", fLabel), gbc);
        JTextField txtPerimetro = FabricaComponentes.crearCampoTexto(fInput);
        txtPerimetro.setText("0.00");
        gbc.gridx = 1; formPanel.add(txtPerimetro, gbc);
        
        comboReales.addActionListener(e -> {
            Punto real = (Punto) comboReales.getSelectedItem();
            if (real != null) {
                CoordenadasRectangulares actual = puntoCalculado.getCoordenadas();
                CoordenadasRectangulares destino = real.getCoordenadas();
                LinkedList<Poligonal> poligonales = sit.getListaPoligonales();
                double distCamino = calculadora.calcularPerimetroRecursivo(actual, destino, poligonales, new HashSet<String>());
                if (distCamino > 0) {
                    txtPerimetro.setText(String.format(Locale.US, "%.2f", distCamino));
                    txtPerimetro.setForeground(Color.GREEN);
                } else {
                    // FALLO: Los puntos no están unidos físicamente. Trazamos distancia recta por seguridad.
                    double distDirecta = puntoCalculado.getCoordenadas().distanciaA(real.getCoordenadas());
                    txtPerimetro.setText(String.format(Locale.US, "%.2f", distDirecta));
                    txtPerimetro.setForeground(Color.ORANGE);
                }
            }
        });

        // PANEL DE BOTONES
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JButton btnCalcular = new JButton("EVALUAR ERROR");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(40, 70, 120)); 
        
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        // ACCIONES
        btnCalcular.addActionListener(e -> {
            try {
                Punto real = (Punto) comboReales.getSelectedItem();
                if (real == null) throw new Exception("Debe seleccionar un punto real para comparar.");

                double perimetro = Double.parseDouble(txtPerimetro.getText().trim());
                if (perimetro <= 0) throw new Exception("La longitud de la poligonal debe ser mayor a 0 para calcular la precisión relativa.");

                // Llamada a la matemática
                String informe = CalculadorTopografico.obtenerInformeError(
                    real.getCoordenadas(), 
                    puntoCalculado.getCoordenadas(), 
                    perimetro,
                    real.getNombre(),
                    puntoCalculado.getNombre()
                );

                // Como esto no grafica un punto nuevo, pasamos null al callback
                callback.onCalculationComplete(null, informe);
                
                // Mostramos el informe en pantalla inmediatamente para el operador
                JOptionPane.showMessageDialog(dialog, informe, "REPORTE DE PRECISIÓN", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: La longitud debe ser un valor numérico.");
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}

	@Override
	public void RadiacionDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE RADIACIÓN (PUNTO Y DISTANCIA)", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(padre);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fCombo = new Font("Arial", Font.PLAIN, 18);
        Font fInput = new Font("Monospaced", Font.BOLD, 22);

        // 1. Estación de Origen
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN DE ORIGEN", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboOrigen = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboOrigen, gbc);

        // 2. Azimut (Dirección)
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("AZIMUT (mils)", fLabel), gbc);
        JTextField txtAzimut = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAzimut, gbc);

        // 3. Distancia
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(FabricaComponentes.crearEtiqueta("DISTANCIA (m)", fLabel), gbc);
        JTextField txtDistancia = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtDistancia, gbc);

        // 4. Nombre del Objetivo
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(FabricaComponentes.crearEtiqueta("ID OBJETIVO", fLabel), gbc);
        JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
        txtNombre.setText("RAD-" + (sit.getListaDePuntos().size() + 1));
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        JButton btnCalcular = new JButton("CALCULAR");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(45, 85, 45));
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        btnCalcular.addActionListener(e -> {
            try {
                Posicionable origen = (Posicionable) comboOrigen.getSelectedItem();
                double azimut = Double.parseDouble(txtAzimut.getText());
                double distancia = Double.parseDouble(txtDistancia.getText());
                String id = txtNombre.getText().trim();

                CoordenadasRectangulares res = CalculadorTopografico.radiacion(origen, azimut, distancia);
                
                Punto ptoNuevo = new Punto(res, id);
                sit.agregarPunto(ptoNuevo);

                // REGISTRO PARA EL PDF
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("ESTACIÓN: %s (X: %.2f, Y: %.2f)\n", origen.getNombre(), origen.getCoordenadas().getX(), origen.getCoordenadas().getY()));
                sb.append(String.format("DATOS MEDIDOS: Azimut: %.0f mils | Distancia: %.2f m\n", azimut, distancia));
                sb.append(String.format("RESULTADO: %s en (X: %.3f, Y: %.3f)", id, res.getX(), res.getY()));
                
                RegistroCalculos.guardar("RADIACIÓN", sb.toString());
                
                dialog.dispose();
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}

	@Override
	public void TriangulacionDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE TRIANGULACIÓN TÁCTICA", true);
        dialog.setSize(650, 600); // Tamaño más contenido y equilibrado
        dialog.setLocationRelativeTo(padre);

        // Panel Principal con un degradado o color sólido oscuro
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));

        // SECCIÓN DE FORMULARIO 
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fuentes
        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fCombo = new Font("Arial", Font.PLAIN, 18);
        Font fInput = new Font("Monospaced", Font.BOLD, 22);

        // 1. Grupo Estaciones (Línea Base)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN A (Izquierda)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboA;
        comboA = FabricaComponentes.crearComboPuntosYBlancos(fCombo,sit.getListaDePuntos(),sit.getListaBlancos());

        formPanel.add(comboA, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN B (Derecha)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboB = FabricaComponentes.crearComboPuntosYBlancos(fCombo,sit.getListaDePuntos(),sit.getListaBlancos());
        formPanel.add(comboB, gbc);

        // Separador
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // 2. Grupo Mediciones (Ángulos)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(FabricaComponentes.crearEtiqueta("ANG. ALFA (mils)", fLabel), gbc);
        JTextField txtAngA = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAngA, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(FabricaComponentes.crearEtiqueta("ANG. BETA (mils)", fLabel), gbc);
        JTextField txtAngB = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAngB, gbc);

        // 3. Grupo Resultado
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(FabricaComponentes.crearEtiqueta("ID OBJETIVO", fLabel), gbc);
        JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
        txtNombre.setText("EST-" + (sit.getListaDePuntos().size() + 1));
        txtNombre.setForeground(new Color(255, 200, 0)); // Color distintivo
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        // --- PANEL DE BOTONES (Inferior) ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        JButton btnCalcular = new JButton("CALCULAR");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(45, 85, 45));
        
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        // ACCIONES
        btnCalcular.addActionListener(e -> {
            try {
                Posicionable pA = (Posicionable) comboA.getSelectedItem();
                Posicionable pB = (Posicionable) comboB.getSelectedItem();
                
                if (pA == null || pB == null) throw new Exception("Seleccione ambas estaciones.");
                if (pA.equals(pB)) throw new Exception("Las estaciones deben ser distintas.");

                double alfa = Double.parseDouble(txtAngA.getText());
                double beta = Double.parseDouble(txtAngB.getText());
                
                double distLB = pA.getCoordenadas().distanciaA(pB.getCoordenadas());
                CoordenadasRectangulares res = CalculadorTopografico.triangulacion(pA, pB, alfa, beta);
                String id = txtNombre.getText().trim();

                Punto ptoNuevo = new Punto(res, id);

                sit.getListaDePuntos().add(ptoNuevo);
                
                sit.getListaPoligonales().add(ptoNuevo);
                
                sit.getModeloListaPoligonales().addElement(ptoNuevo);
                
                sit.getPanelMapa().agregarPoligonal(ptoNuevo);

                StringBuilder sb = new StringBuilder();
                sb.append("DATOS DE ESTACIONES:\n");
                sb.append(String.format(" - ETR A: %s (X: %.2f, Y: %.2f)\n", pA.getNombre(), pA.getCoordenadas().getX(), pA.getCoordenadas().getY()));
                sb.append(String.format(" - ETR B: %s (X: %.2f, Y: %.2f)\n", pB.getNombre(), pB.getCoordenadas().getX(), pB.getCoordenadas().getY()));
                sb.append(String.format(" - LÍNEA BASE (LB): %.2f m\n\n", distLB));
                sb.append("MEDICIONES DE CAMPO:\n");
                sb.append(String.format(" - ÁNGULO ALFA (α): %.0f mils\n", alfa));
                sb.append(String.format(" - ÁNGULO BETA (β): %.0f mils\n\n", beta));
                sb.append("RESULTADO POSICIONAMIENTO:\n");
                sb.append(String.format(" - OBJETIVO: %s\n", id));
                sb.append(String.format(" - COORDENADAS: X: %.3f | Y: %.3f", res.getX(), res.getY()));

                RegistroCalculos.guardar("TRIANGULACIÓN", sb.toString());
                
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: Los ángulos deben ser valores numéricos.");
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}

	@Override
	public void InterseccionInversa3PDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE INTERSECCIÓN INVERSA (3 PUNTOS)", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(padre);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 13);
        Font fCombo = new Font("Arial", Font.PLAIN, 16);
        Font fInput = new Font("Monospaced", Font.BOLD, 20);

        // SELECCIÓN DE PUNTOS CONOCIDOS
        // Punto Izquierda (P1)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("PUNTO IZQUIERDA (P1)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboP1 = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboP1, gbc);

        // Punto Central (P2)
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("PUNTO CENTRAL (P2)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboP2 = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboP2, gbc);

        // Punto Derecha (P3)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(FabricaComponentes.crearEtiqueta("PUNTO DERECHA (P3)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboP3 = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboP3, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(FabricaComponentes.crearEtiqueta("ANG. ALFA P1-P2 (mils)", fLabel), gbc);
        JTextField txtAlfa = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAlfa, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(FabricaComponentes.crearEtiqueta("ANG. BETA P2-P3 (mils)", fLabel), gbc);
        JTextField txtBeta = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtBeta, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(FabricaComponentes.crearEtiqueta("ID POSICIÓN PROPIA", fLabel), gbc);
        JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
        txtNombre.setText("POS-PROPIA-" + (sit.getListaDePuntos().size() + 1));
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton btnCalcular = new JButton("DETERMINAR POSICIÓN");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(40, 70, 120)); // Azul táctico
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(80, 40, 40));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        btnCalcular.addActionListener(e -> {
            try {
                Posicionable p1 = (Posicionable) comboP1.getSelectedItem();
                Posicionable p2 = (Posicionable) comboP2.getSelectedItem();
                Posicionable p3 = (Posicionable) comboP3.getSelectedItem();
                
                if (p1 == p2 || p2 == p3 || p1 == p3) throw new Exception("Seleccione tres puntos distintos.");

                double alfa = Double.parseDouble(txtAlfa.getText());
                double beta = Double.parseDouble(txtBeta.getText());
                
                // Llamaremos a la calculadora (Próximo paso)
                CoordenadasRectangulares res = CalculadorTopografico.interseccionInversa3P(p1, p2, p3, alfa, beta);
                
                Punto miPosicion = new Punto(res, txtNombre.getText().trim());
                sit.agregarPunto(miPosicion);

                // Registro detallado para el PDF
                StringBuilder sb = new StringBuilder();
                sb.append("MÉTODO: INTERSECCIÓN INVERSA (POTENOT)\n");
                sb.append(String.format("REFERENCIAS: %s, %s, %s\n", p1.getNombre(), p2.getNombre(), p3.getNombre()));
                sb.append(String.format("ÁNGULOS OBS: α=%.0f mils, β=%.0f mils\n", alfa, beta));
                sb.append(String.format("POSICIÓN CALCULADA: X: %.3f | Y: %.3f", res.getX(), res.getY()));
                
                RegistroCalculos.guardar("INTERSECCIÓN INVERSA 3P", sb.toString());
                
                dialog.dispose();
                JOptionPane.showMessageDialog(padre, "Posición propia determinada con éxito.");
                
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error en el cálculo: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}
	
	@Override
	public void TrilateracionDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE TRILATERACIÓN TÁCTICA", true);
        dialog.setSize(650, 600); 
        dialog.setLocationRelativeTo(padre);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));

        // SECCIÓN DE FORMULARIO
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fCombo = new Font("Arial", Font.PLAIN, 18);
        Font fInput = new Font("Monospaced", Font.BOLD, 22);

        // 1. Grupo Estaciones (Línea Base)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN A (Izquierda)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboA = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboA, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN B (Derecha)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboB = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboB, gbc);

        // Separador
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // 2. Grupo Mediciones (Distancias en lugar de Ángulos)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(FabricaComponentes.crearEtiqueta("DISTANCIA DESDE A (m)", fLabel), gbc);
        JTextField txtDistA = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtDistA, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(FabricaComponentes.crearEtiqueta("DISTANCIA DESDE B (m)", fLabel), gbc);
        JTextField txtDistB = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtDistB, gbc);

        // 3. Grupo Resultado
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(FabricaComponentes.crearEtiqueta("ID OBJETIVO", fLabel), gbc);
        JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
        txtNombre.setText("TRILAT-" + (sit.getListaDePuntos().size() + 1));
        txtNombre.setForeground(new Color(255, 200, 0)); // Color distintivo heredado
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        // PANEL DE BOTONES (Inferior)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        JButton btnCalcular = new JButton("CALCULAR");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(45, 85, 45));
        
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        // ACCIONES
        btnCalcular.addActionListener(e -> {
            try {
                Posicionable pA = (Posicionable) comboA.getSelectedItem();
                Posicionable pB = (Posicionable) comboB.getSelectedItem();
                
                if (pA == null || pB == null) throw new Exception("Seleccione ambas estaciones.");
                if (pA.equals(pB)) throw new Exception("Las estaciones deben ser distintas.");

                double distA = Double.parseDouble(txtDistA.getText());
                double distB = Double.parseDouble(txtDistB.getText());
                
                double distLB = pA.getCoordenadas().distanciaA(pB.getCoordenadas());
                
                // Validación matemática crucial (Desigualdad Triangular) para que no crashee la calculadora
                if (distA + distB <= distLB || Math.abs(distA - distB) >= distLB) {
                    throw new Exception("Geometría imposible: Las distancias proporcionadas no se intersectan (no forman un triángulo con la Línea Base).");
                }

                // Llamada teórica a tu clase de cálculos (necesitarás este método allí)
                CoordenadasRectangulares res = CalculadorTopografico.trilateracion(pA, pB, distA, distB);
                String id = txtNombre.getText().trim();

                Punto ptoNuevo = new Punto(res, id);

                sit.getListaDePuntos().add(ptoNuevo);
                sit.getListaPoligonales().add(ptoNuevo);
                sit.getModeloListaPoligonales().addElement(ptoNuevo);
                sit.getPanelMapa().agregarPoligonal(ptoNuevo);

                StringBuilder sb = new StringBuilder();
                sb.append("DATOS DE ESTACIONES:\n");
                sb.append(String.format(" - ETR A: %s (X: %.2f, Y: %.2f)\n", pA.getNombre(), pA.getCoordenadas().getX(), pA.getCoordenadas().getY()));
                sb.append(String.format(" - ETR B: %s (X: %.2f, Y: %.2f)\n", pB.getNombre(), pB.getCoordenadas().getX(), pB.getCoordenadas().getY()));
                sb.append(String.format(" - LÍNEA BASE (LB): %.2f m\n\n", distLB));
                sb.append("MEDICIONES DE CAMPO (TRILATERACIÓN):\n");
                sb.append(String.format(" - DISTANCIA A OBJETIVO (Desde A): %.2f m\n", distA));
                sb.append(String.format(" - DISTANCIA A OBJETIVO (Desde B): %.2f m\n\n", distB));
                sb.append("RESULTADO POSICIONAMIENTO:\n");
                sb.append(String.format(" - OBJETIVO: %s\n", id));
                sb.append(String.format(" - COORDENADAS: X: %.3f | Y: %.3f", res.getX(), res.getY()));

                RegistroCalculos.guardar("TRILATERACIÓN", sb.toString());
                
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: Las distancias deben ser valores numéricos válidos.");
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}
	
	@Override
	public void InterseccionInversa2PDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE INTERSECCIÓN INVERSA (2 PUNTOS)", true);
        dialog.setSize(600, 600); 
        dialog.setLocationRelativeTo(padre);

        // Panel Principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        // SECCIÓN DE FORMULARIO
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fCombo = new Font("Arial", Font.PLAIN, 18);
        Font fInput = new Font("Monospaced", Font.BOLD, 22);

        // 1. Grupo Estaciones Conocidas
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN A (Izquierda)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboA = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboA, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN B (Derecha)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboB = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboB, gbc);

        // Separador
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // 2. Grupo Mediciones (Azimuts observados)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(FabricaComponentes.crearEtiqueta("AZIMUT A LA EST. A (mils)", fLabel), gbc);
        JTextField txtAzA = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAzA, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(FabricaComponentes.crearEtiqueta("AZIMUT A LA EST. B (mils)", fLabel), gbc);
        JTextField txtAzB = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAzB, gbc);

        // 3. Resultado (Posición Propia)
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(FabricaComponentes.crearEtiqueta("ID POSICIÓN PROPIA", fLabel), gbc);
        JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
        txtNombre.setText("POS-PROPIA-" + (sit.getListaDePuntos().size() + 1));
        txtNombre.setForeground(new Color(255, 200, 0)); 
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JButton btnCalcular = new JButton("DETERMINAR POSICIÓN");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(40, 70, 120)); 
        
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        // ACCIONES
        btnCalcular.addActionListener(e -> {
            try {
                Posicionable pA = (Posicionable) comboA.getSelectedItem();
                Posicionable pB = (Posicionable) comboB.getSelectedItem();
                
                if (pA == null || pB == null) throw new Exception("Seleccione ambas estaciones de referencia.");
                if (pA.equals(pB)) throw new Exception("Las estaciones deben ser distintas.");

                double azA = Double.parseDouble(txtAzA.getText());
                double azB = Double.parseDouble(txtAzB.getText());
                
                if(azA < 0 || azA >= 6400 || azB < 0 || azB >= 6400) {
                    throw new Exception("Los azimuts deben estar entre 0 y 6399 milésimos.");
                }

                // Llamada a la calculadora matemática
                CoordenadasRectangulares res = CalculadorTopografico.interseccionInversa2P(pA, pB, azA, azB);
                
                if (res == null) {
                    throw new Exception("Las líneas de observación son paralelas o no se cruzan geométricamente.");
                }

                String id = txtNombre.getText().trim();
                Punto miPosicion = new Punto(res, id);

                StringBuilder sb = new StringBuilder();
                sb.append("MÉTODO: INTERSECCIÓN INVERSA (2 PUNTOS)\n\n");
                sb.append("REFERENCIAS OBSERVADAS:\n");
                sb.append(String.format(" - ETR A: %s (X: %.2f, Y: %.2f)\n", pA.getNombre(), pA.getCoordenadas().getX(), pA.getCoordenadas().getY()));
                sb.append(String.format(" - ETR B: %s (X: %.2f, Y: %.2f)\n\n", pB.getNombre(), pB.getCoordenadas().getX(), pB.getCoordenadas().getY()));
                sb.append("DATOS DE CAMPO (AZIMUTS MAGNÉTICOS/CUADRÍCULA):\n");
                sb.append(String.format(" - Hacia ETR A: %.0f mils\n", azA));
                sb.append(String.format(" - Hacia ETR B: %.0f mils\n\n", azB));
                sb.append("POSICIÓN PROPIA CALCULADA:\n");
                sb.append(String.format(" - ID: %s\n", id));
                sb.append(String.format(" - COORDENADAS: X: %.3f | Y: %.3f", res.getX(), res.getY()));

                // Despacho al Callback 
                callback.onCalculationComplete(miPosicion, sb.toString());
                
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: Los azimuts deben ser números enteros o decimales válidos.");
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}
	
	@Override
	public void InterseccionDirectaMDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE INTERSECCIÓN DIRECTA", true);
        dialog.setSize(600, 600); 
        dialog.setLocationRelativeTo(padre);

        // Panel Principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        // SECCIÓN DE FORMULARIO
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fCombo = new Font("Arial", Font.PLAIN, 18);
        Font fInput = new Font("Monospaced", Font.BOLD, 22);

        // 1. Grupo Estaciones (Observadores Conocidos)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN A (Izquierda)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboA = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboA, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN B (Derecha)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboB = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboB, gbc);

        // Separador
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // 2. Grupo Mediciones (Azimuts hacia el objetivo)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(FabricaComponentes.crearEtiqueta("AZIMUT DESDE A (mils)", fLabel), gbc);
        JTextField txtAzA = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAzA, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(FabricaComponentes.crearEtiqueta("AZIMUT DESDE B (mils)", fLabel), gbc);
        JTextField txtAzB = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAzB, gbc);

        // 3. Resultado (Blanco u Objetivo)
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(FabricaComponentes.crearEtiqueta("ID OBJETIVO", fLabel), gbc);
        JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
        txtNombre.setText("INT-DIR-" + (sit.getListaDePuntos().size() + 1));
        txtNombre.setForeground(new Color(255, 100, 100)); // Un rojo/naranja táctico para indicar posible blanco
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        // PANEL DE BOTONES
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JButton btnCalcular = new JButton("DETERMINAR BLANCO");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(40, 70, 120)); 
        
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        // ACCIONES
        btnCalcular.addActionListener(e -> {
            try {
                Posicionable pA = (Posicionable) comboA.getSelectedItem();
                Posicionable pB = (Posicionable) comboB.getSelectedItem();
                
                if (pA == null || pB == null) throw new Exception("Seleccione ambas estaciones de observación.");
                if (pA.equals(pB)) throw new Exception("Las estaciones de observación deben ser distintas.");

                double azA = Double.parseDouble(txtAzA.getText());
                double azB = Double.parseDouble(txtAzB.getText());
                
                if(azA < 0 || azA >= 6400 || azB < 0 || azB >= 6400) {
                    throw new Exception("Los azimuts deben estar entre 0 y 6399 milésimos.");
                }

                // Llamada teórica a tu clase de matemáticas
                CoordenadasRectangulares res = CalculadorTopografico.interseccionDirecta(pA, pB, azA, azB);
                
                if (res == null) {
                    throw new Exception("Las líneas de visión son paralelas o divergen. No hay intersección posible.");
                }

                String id = txtNombre.getText().trim();
                Punto ptoNuevo = new Punto(res, id);

                // Armado del reporte táctico
                StringBuilder sb = new StringBuilder();
                sb.append("MÉTODO: INTERSECCIÓN DIRECTA\n\n");
                sb.append("PUESTOS DE OBSERVACIÓN:\n");
                sb.append(String.format(" - P.O. A: %s (X: %.2f, Y: %.2f)\n", pA.getNombre(), pA.getCoordenadas().getX(), pA.getCoordenadas().getY()));
                sb.append(String.format(" - P.O. B: %s (X: %.2f, Y: %.2f)\n\n", pB.getNombre(), pB.getCoordenadas().getX(), pB.getCoordenadas().getY()));
                sb.append("MEDICIONES REPORTADAS:\n");
                sb.append(String.format(" - Azimut desde A al blanco: %.0f mils\n", azA));
                sb.append(String.format(" - Azimut desde B al blanco: %.0f mils\n\n", azB));
                sb.append("POSICIÓN DEL BLANCO CALCULADA:\n");
                sb.append(String.format(" - ID: %s\n", id));
                sb.append(String.format(" - COORDENADAS: X: %.3f | Y: %.3f", res.getX(), res.getY()));

                callback.onCalculationComplete(ptoNuevo, sb.toString());
                
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: Los azimuts deben ser numéricos.");
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}
	
	@SuppressWarnings("serial")
	@Override
	public void MesaPlottingDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
	    JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(padre);
	    JDialog dialog = new JDialog(parent, "MESA DE PLOTTING TÁCTICA", true);

	    JPanel panel = new JPanel(new GridBagLayout());
	    panel.setBackground(new Color(45, 45, 45));
	    panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); 
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.insets = new Insets(15, 15, 15, 15); 

	    Font fLabel = new Font("Arial", Font.BOLD, 22); 
	    Font fInput = new Font("Monospaced", Font.BOLD, 26);
	    Font fCombo = new Font("Arial", Font.PLAIN, 20);
	    Font fMil = new Font("Arial", Font.BOLD, 18); 

	    List<Posicionable> basesVisibles = new ArrayList<>();
	    basesVisibles.addAll(puntos);
	    basesVisibles.addAll(blancos);

	    @SuppressWarnings("unchecked")
	    JComboBox<Posicionable>[] combosBases = new JComboBox[3];
	    JTextField[] txtAngulos = new JTextField[3];

	    for (int i = 0; i < 3; i++) {
	        gbc.gridy = i;
	        
	        gbc.gridx = 0;
	        panel.add(new JLabel("BASE " + (i + 1) + ":") {{ setForeground(Color.WHITE); setFont(fLabel); }}, gbc);

	        combosBases[i] = new JComboBox<>(basesVisibles.toArray(new Posicionable[0]));
	        combosBases[i].setFont(fCombo); 
	        
	        combosBases[i].setPreferredSize(new Dimension(180, 40)); 
	        
	        gbc.gridx = 1;
	        panel.add(combosBases[i], gbc);

	        gbc.gridx = 2;
	        txtAngulos[i] = new JTextField("0", 6);
	        txtAngulos[i].setFont(fInput);
	        txtAngulos[i].setBackground(Color.BLACK);
	        txtAngulos[i].setForeground(Color.GREEN);
	        txtAngulos[i].setHorizontalAlignment(JTextField.CENTER);
	        panel.add(txtAngulos[i], gbc);
	        
	        gbc.gridx = 3;
	        panel.add(new JLabel("mil") {{ setForeground(Color.GRAY); setFont(fMil); }}, gbc);
	    }

	    Blanco[] arrBlancos = new Blanco[blancos.size() + 1];
	    arrBlancos[0] = null;
	    for (int i = 0; i < blancos.size(); i++) {
	        arrBlancos[i + 1] = blancos.get(i);
	    }

	    JComboBox<Blanco> comboRef = new JComboBox<>(arrBlancos);
	    comboRef.setFont(fCombo);
	    comboRef.setRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	            if (value == null) {
	                setText("Ninguno (Opcional)");
	            } else {
	                setText(((Blanco) value).getNombre()); 
	            }
	            return this;
	        }
	    });

	    gbc.gridy = 0; 
	    gbc.gridx = 4;
	    gbc.insets = new Insets(15, 50, 15, 15);
	    panel.add(new JLabel("REF. FINAL:") {{ setForeground(Color.ORANGE); setFont(fLabel); }}, gbc);

	    gbc.gridy = 1; 
	    gbc.gridx = 4;
	    panel.add(comboRef, gbc);

	    JButton btnEjecutar = new JButton("CALCULAR E INTERSECTAR");
	    btnEjecutar.setFont(new Font("Arial", Font.BOLD, 24));
	    btnEjecutar.setBackground(new Color(0, 100, 0));
	    btnEjecutar.setForeground(Color.WHITE);
	    gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 5; gbc.ipady = 35; 
	    gbc.insets = new Insets(40, 15, 15, 15);
	    panel.add(btnEjecutar, gbc);

	    btnEjecutar.addActionListener(e -> {
	        try {
	            Coordinate interseccion = calculadora.calcularInterseccionMesa(
	                (Posicionable) combosBases[0].getSelectedItem(), Double.parseDouble(txtAngulos[0].getText()),
	                (Posicionable) combosBases[1].getSelectedItem(), Double.parseDouble(txtAngulos[1].getText()),
	                (Posicionable) combosBases[2].getSelectedItem(), Double.parseDouble(txtAngulos[2].getText())
	            );

	            if (interseccion != null) {
	                CoordenadasRectangulares c = new CoordenadasRectangulares(interseccion.x, interseccion.y, 0);
	                String nombreDinamico = "M-PLOT-" + contadorPlotting++;
	                Punto resultado = new Punto(c, nombreDinamico);

	                sit.agregarPunto(resultado);

	                // 3. INFORME DETALLADO AMPLIADO
	                StringBuilder informe = new StringBuilder();
	                informe.append("=================================\n");
	                informe.append(" INFORME DETALLADO MESA PLOTTING \n");
	                informe.append("=================================\n\n");
	                
	                informe.append("[DATOS DE ENTRADA]\n");
	                for (int i = 0; i < 3; i++) {
	                    Posicionable base = (Posicionable) combosBases[i].getSelectedItem();
	                    double angulo = Double.parseDouble(txtAngulos[i].getText());
	                    informe.append(String.format("BASE %d: %s (X: %.2f, Y: %.2f) | Ángulo Obs: %.0f mil\n", 
	                        (i + 1), base.getNombre(), base.getCoordenadas().getX(), base.getCoordenadas().getY(), angulo));
	                }

	                informe.append("\n[RESULTADO CÁLCULO]\n");
	                informe.append(String.format("Punto Generado: %s\nCoordenadas: X=%.2f Y=%.2f\n", nombreDinamico, interseccion.x, interseccion.y));

	                Blanco ref = (Blanco) comboRef.getSelectedItem();
	                informe.append("\n[DATOS DE REFERENCIA FINAL]\n");
	                
	                if (ref != null) {
	                    double dist = ref.getCoordenadas().distanciaA(c);
	                    double az = calculadora.calcularAzimutEnMils(ref.getCoordenadas().getX(), ref.getCoordenadas().getY(), interseccion.x, interseccion.y);
	                    
	                    Linea lineaRef = new Linea(ref.getNombre() + " -> " + nombreDinamico, 
	                    	    new Coordinate(ref.getCoordenadas().getX(), ref.getCoordenadas().getY()), 
	                    	    interseccion, dist, az);

	                    sit.getPanelMapa().agregarPoligonal(lineaRef);
	                    sit.getListaPoligonales().add(lineaRef);
	                    sit.getModeloListaPoligonales().addElement(lineaRef);

	                    informe.append(String.format("Referencia Utilizada: %s (X: %.2f, Y: %.2f)\n", 
	                                                 ref.getNombre(), ref.getCoordenadas().getX(), ref.getCoordenadas().getY()));
	                    informe.append(String.format("Distancia a %s: %.2f metros\n", nombreDinamico, dist));
	                    informe.append(String.format("Azimut a %s: %.2f milésimos\n", nombreDinamico, az));
	                } else {
	                    informe.append("No se utilizó ninguna referencia final para este cálculo.\n");
	                }
	                
	                informe.append("========================================\n");

	                callback.onCalculationComplete(resultado, informe.toString());
	                
	                dialog.dispose();
	            }
	        } catch (Exception ex) {
	            sonidos.clickError();
	            JOptionPane.showMessageDialog(dialog, "Error en los datos de entrada o geometría.");
	        }
	    });

	    dialog.add(panel);
	    dialog.pack(); 
	    dialog.setLocationRelativeTo(padre); 
	    dialog.setVisible(true);
	}
	
	@Override
	public void AnguloBaseDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE ÁNGULO BASE (RADIACIÓN C/ REF)", true);
        dialog.setSize(600, 650); 
        dialog.setLocationRelativeTo(padre);

        // Panel Principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        // SECCIÓN DE FORMULARIO
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fCombo = new Font("Arial", Font.PLAIN, 18);
        Font fInput = new Font("Monospaced", Font.BOLD, 22);

        // 1. Grupo Estación y Referencia
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN DE ORIGEN", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboOrigen = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboOrigen, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("PUNTO REF. (Cero Topográfico)", fLabel), gbc);
        gbc.gridx = 1;
        JComboBox<Posicionable> comboRef = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
        formPanel.add(comboRef, gbc);

        // Separador
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // 2. Grupo Mediciones
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(FabricaComponentes.crearEtiqueta("ÁNGULO AL OBJETIVO (mils)", fLabel), gbc);
        JTextField txtAngulo = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAngulo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(FabricaComponentes.crearEtiqueta("DISTANCIA AL OBJETIVO (m)", fLabel), gbc);
        JTextField txtDistancia = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtDistancia, gbc);

        // 3. Resultado
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(FabricaComponentes.crearEtiqueta("ID OBJETIVO", fLabel), gbc);
        JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
        txtNombre.setText("AB-" + (sit.getListaDePuntos().size() + 1));
        txtNombre.setForeground(new Color(150, 255, 150)); // Verde táctico claro
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        // --- PANEL DE BOTONES ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JButton btnCalcular = new JButton("DETERMINAR BLANCO");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(40, 70, 120)); 
        
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        // ACCIONES
        btnCalcular.addActionListener(e -> {
            try {
                Posicionable origen = (Posicionable) comboOrigen.getSelectedItem();
                Posicionable ref = (Posicionable) comboRef.getSelectedItem();
                
                if (origen == null || ref == null) throw new Exception("Seleccione Estación y Punto de Referencia.");
                if (origen.equals(ref)) throw new Exception("La Estación y la Referencia no pueden ser el mismo punto.");

                double anguloMedido = Double.parseDouble(txtAngulo.getText());
                double distancia = Double.parseDouble(txtDistancia.getText());
                
                if(anguloMedido < 0 || anguloMedido >= 6400) {
                    throw new Exception("El ángulo debe estar entre 0 y 6399 milésimos.");
                }

                // 1. Calculamos el Azimut hacia la Referencia
                double azimutBase = calculadora.calcularAzimutEnMils(
                    origen.getCoordenadas().getX(), origen.getCoordenadas().getY(),
                    ref.getCoordenadas().getX(), ref.getCoordenadas().getY()
                );

                // 2. Sumamos el ángulo medido para obtener el Azimut Real al Blanco
                double azimutAlBlanco = (azimutBase + anguloMedido) % 6400.0;

                // 3. Usamos el método de radiación que ya tienes en CalculadorTopografico
                CoordenadasRectangulares res = CalculadorTopografico.radiacion(origen, azimutAlBlanco, distancia);

                String id = txtNombre.getText().trim();
                Punto ptoNuevo = new Punto(res, id);

                // Preparamos el informe detallado
                StringBuilder sb = new StringBuilder();
                sb.append("MÉTODO: ÁNGULO BASE (RADIACIÓN CON REFERENCIA)\n\n");
                sb.append("DATOS DE ORIENTACIÓN:\n");
                sb.append(String.format(" - ESTACIÓN ORIGEN: %s (X: %.2f, Y: %.2f)\n", origen.getNombre(), origen.getCoordenadas().getX(), origen.getCoordenadas().getY()));
                sb.append(String.format(" - PUNTO REFERENCIA: %s\n", ref.getNombre()));
                sb.append(String.format(" - AZIMUT BASE (Calculado): %.0f mils\n\n", azimutBase));
                sb.append("DATOS DE CAMPO:\n");
                sb.append(String.format(" - ÁNGULO AL BLANCO: %.0f mils\n", anguloMedido));
                sb.append(String.format(" - AZIMUT REAL AL BLANCO: %.0f mils\n", azimutAlBlanco));
                sb.append(String.format(" - DISTANCIA: %.2f m\n\n", distancia));
                sb.append("POSICIÓN DEL OBJETIVO CALCULADA:\n");
                sb.append(String.format(" - ID: %s\n", id));
                sb.append(String.format(" - COORDENADAS: X: %.3f | Y: %.3f", res.getX(), res.getY()));

                callback.onCalculationComplete(ptoNuevo, sb.toString());
                
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: El ángulo y la distancia deben ser valores numéricos.");
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}
	
	@Override
	public void ActualizacionMagneticaDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "MÓDULO DE ACTUALIZACIÓN MAGNÉTICA", true);
        dialog.setSize(600, 600); 
        dialog.setLocationRelativeTo(padre);

        // Panel Principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        // SECCIÓN DE FORMULARIO
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fCombo = new Font("Arial", Font.PLAIN, 18);
        Font fInput = new Font("Monospaced", Font.BOLD, 22);

        // 1. Datos de la Carta Topográfica
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("AÑO DE LA CARTA TOPOGRÁFICA", fLabel), gbc);
        JTextField txtAnioCarta = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtAnioCarta, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("DECLINACIÓN ORIGINAL (mils)", fLabel), gbc);
        JTextField txtDecOriginal = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtDecOriginal, gbc);

        // Separador
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // 2. Datos de Variación
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(FabricaComponentes.crearEtiqueta("VARIACIÓN ANUAL (mils/año)", fLabel), gbc);
        JTextField txtVarAnual = FabricaComponentes.crearCampoTexto(fInput);
        gbc.gridx = 1; formPanel.add(txtVarAnual, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(FabricaComponentes.crearEtiqueta("COMPORTAMIENTO ANUAL", fLabel), gbc);
        JComboBox<String> comboComportamiento = new JComboBox<>(new String[]{"AUMENTA (+)", "DISMINUYE (-)"});
        comboComportamiento.setFont(fCombo);
        gbc.gridx = 1; formPanel.add(comboComportamiento, gbc);

        // 3. Actualidad
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(FabricaComponentes.crearEtiqueta("AÑO ACTUAL AL CÁLCULO", fLabel), gbc);
        JTextField txtAnioActual = FabricaComponentes.crearCampoTexto(fInput);
        txtAnioActual.setText("2026"); // Lo seteamos al año actual
        txtAnioActual.setForeground(new Color(150, 200, 255)); // Azul táctico para diferenciar
        gbc.gridx = 1; formPanel.add(txtAnioActual, gbc);

        // --- PANEL DE BOTONES ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JButton btnCalcular = new JButton("ACTUALIZAR DECLINACIÓN");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(40, 70, 120)); 
        
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        // ACCIONES
        btnCalcular.addActionListener(e -> {
            try {
                int anioCarta = Integer.parseInt(txtAnioCarta.getText().trim());
                int anioActual = Integer.parseInt(txtAnioActual.getText().trim());
                double decOriginal = Double.parseDouble(txtDecOriginal.getText().trim());
                double varAnual = Double.parseDouble(txtVarAnual.getText().trim());
                boolean aumenta = comboComportamiento.getSelectedIndex() == 0; // True si aumenta, False si disminuye

                if (anioActual < anioCarta) {
                    throw new Exception("El año actual no puede ser menor al año de edición de la carta.");
                }

                // Matemática pura y simple
                int difAnios = anioActual - anioCarta;
                double variacionTotal = difAnios * varAnual;
                double declinacionActualizada = decOriginal + (aumenta ? variacionTotal : -variacionTotal);

                // Armamos el informe topográfico
                StringBuilder sb = new StringBuilder();
                sb.append("MÉTODO: ACTUALIZACIÓN DE DECLINACIÓN MAGNÉTICA\n\n");
                sb.append("DATOS DE LA CARTA TOPOGRÁFICA:\n");
                sb.append(String.format(" - AÑO DE EDICIÓN: %d\n", anioCarta));
                sb.append(String.format(" - DECLINACIÓN ORIGINAL: %.2f mils\n", decOriginal));
                sb.append(String.format(" - VARIACIÓN ANUAL: %.2f mils (%s)\n\n", varAnual, aumenta ? "AUMENTA" : "DISMINUYE"));
                sb.append("CÁLCULO DE ACTUALIZACIÓN:\n");
                sb.append(String.format(" - AÑO ACTUAL: %d (Han pasado %d años)\n", anioActual, difAnios));
                sb.append(String.format(" - VARIACIÓN TOTAL ACUMULADA: %.2f mils\n\n", (aumenta ? variacionTotal : -variacionTotal)));
                sb.append("RESULTADO FINAL:\n");
                sb.append(String.format(" - DECLINACIÓN MAGNÉTICA ACTUALIZADA: %.2f mils", declinacionActualizada));

                // Pasamos 'null' como Punto porque esto no grafica nada en el mapa
                callback.onCalculationComplete(null, sb.toString());
                
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: Todos los campos deben ser rellenados con valores numéricos.");
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}
	
	@Override
	public void RegistroCoordModDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
	    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
	    JDialog dialog = new JDialog(parentFrame, "MODIFICAR COORDENADAS EXISTENTES", true);
	    dialog.setSize(600, 650);
	    dialog.setLocationRelativeTo(padre);

	    // Panel Principal
	    JPanel mainPanel = new JPanel(new BorderLayout());
	    mainPanel.setBackground(new Color(30, 30, 30));
	    mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

	    // SECCIÓN DE FORMULARIO
	    JPanel formPanel = new JPanel(new GridBagLayout());
	    formPanel.setOpaque(false);
	    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(10, 10, 10, 10);
	    gbc.fill = GridBagConstraints.HORIZONTAL;

	    Font fLabel = new Font("Arial", Font.BOLD, 14);
	    Font fCombo = new Font("Arial", Font.PLAIN, 18);
	    Font fInput = new Font("Monospaced", Font.BOLD, 22);

	    // 1. Punto Original
	    gbc.gridx = 0; gbc.gridy = 0;
	    formPanel.add(FabricaComponentes.crearEtiqueta("SELECCIONE EL PUNTO/BLANCO:", fLabel), gbc);
	    gbc.gridx = 1;
	    JComboBox<Posicionable> comboOriginal = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
	    formPanel.add(comboOriginal, gbc);

	    // Separador
	    gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
	    formPanel.add(new JSeparator(), gbc);
	    gbc.gridwidth = 1;

	    // 2. Modificaciones (Deltas)
	    gbc.gridx = 0; gbc.gridy = 2;
	    formPanel.add(FabricaComponentes.crearEtiqueta("CORRECCIÓN ESTE (ΔX) [m]", fLabel), gbc);
	    JTextField txtDeltaX = FabricaComponentes.crearCampoTexto(fInput);
	    txtDeltaX.setText("0"); 
	    gbc.gridx = 1; formPanel.add(txtDeltaX, gbc);

	    gbc.gridx = 0; gbc.gridy = 3;
	    formPanel.add(FabricaComponentes.crearEtiqueta("CORRECCIÓN NORTE (ΔY) [m]", fLabel), gbc);
	    JTextField txtDeltaY = FabricaComponentes.crearCampoTexto(fInput);
	    txtDeltaY.setText("0");
	    gbc.gridx = 1; formPanel.add(txtDeltaY, gbc);

	    gbc.gridx = 0; gbc.gridy = 4;
	    formPanel.add(FabricaComponentes.crearEtiqueta("CORRECCIÓN COTA (ΔZ) [m]", fLabel), gbc);
	    JTextField txtDeltaZ = FabricaComponentes.crearCampoTexto(fInput);
	    txtDeltaZ.setText("0");
	    gbc.gridx = 1; formPanel.add(txtDeltaZ, gbc);

	    // 3. Identificador (Nombre)
	    gbc.gridx = 0; gbc.gridy = 5;
	    formPanel.add(FabricaComponentes.crearEtiqueta("CONFIRMAR ID/NOMBRE:", fLabel), gbc);
	    JTextField txtNombre = FabricaComponentes.crearCampoTexto(fInput);
	    txtNombre.setForeground(new Color(255, 150, 50)); 
	    gbc.gridx = 1; formPanel.add(txtNombre, gbc);

	    comboOriginal.addActionListener(e -> {
	        Posicionable p = (Posicionable) comboOriginal.getSelectedItem();
	        if (p != null) {
	            txtNombre.setText(p.getNombre());
	        }
	    });
	    // Inicializar con el primero
	    if(comboOriginal.getSelectedItem() != null) {
	        txtNombre.setText(((Posicionable)comboOriginal.getSelectedItem()).getNombre());
	    }
	    
	    // PANEL DE BOTONES 
	    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
	    buttonPanel.setOpaque(false);
	    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

	    JButton btnCalcular = new JButton("MOVER Y ACTUALIZAR");
	    FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(0, 100, 0)); // Verde oscuro
	    
	    JButton btnCancelar = new JButton("CANCELAR");
	    FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

	    buttonPanel.add(btnCalcular);
	    buttonPanel.add(btnCancelar);

	    mainPanel.add(formPanel, BorderLayout.CENTER);
	    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	    dialog.add(mainPanel);

	    // ACCIONES
	    btnCalcular.addActionListener(e -> {
	        try {
	            Posicionable original = (Posicionable) comboOriginal.getSelectedItem();
	            if (original == null) throw new Exception("Seleccione un elemento para modificar.");

	            // 1. Obtener correcciones
	            double dX = Double.parseDouble(txtDeltaX.getText().trim());
	            double dY = Double.parseDouble(txtDeltaY.getText().trim());
	            double dZ = Double.parseDouble(txtDeltaZ.getText().trim());

	            double oldX = original.getCoordenadas().getX();
	            double oldY = original.getCoordenadas().getY();
	            double oldZ = original.getCoordenadas().getCota();

	            double newX = oldX + dX;
	            double newY = oldY + dY;
	            double newZ = oldZ + dZ;

	            CoordenadasRectangulares nuevasCoord = new CoordenadasRectangulares(newX, newY, newZ);
	            
	            String nuevoNombre = txtNombre.getText().trim();
	            
	            Punto puntoParaCallback = null;

	            if (original instanceof Punto) {
	                Punto p = (Punto) original;

	                sit.getListaDePuntos().remove(p);
	                sit.getListaPoligonales().remove(p);
	                sit.getModeloListaPoligonales().removeElement(p);
	                sit.getPanelMapa().eliminarPoligonal(p); 

	                // B. ACTUALIZAR DATOS
	                p.setCoord(nuevasCoord);
	                p.setNombre(nuevoNombre);

	                sit.agregarPunto(p);
	                
	                puntoParaCallback = p;

	            } else if (original instanceof Blanco) {
	                Blanco b = (Blanco) original;

	                // A. ELIMINAR
	                sit.getListaBlancos().remove(b);
	                sit.getModeloListaBlancos().removeElement(b);
	                sit.getPanelMapa().eliminarBlanco(b);

	                // B. ACTUALIZAR
	                b.setCoordenadas(nuevasCoord);
	                b.setNombre(nuevoNombre);

	                // C. RE-INSERTAR
	                sit.agregarBlanco(b);
	            }
	            
	            StringBuilder sb = new StringBuilder();
	            sb.append("ACTUALIZACIÓN DE COORDENADAS\n\n");
	            sb.append(String.format("ELEMENTO MODIFICADO: %s\n", nuevoNombre));
	            sb.append("--------------------------------\n");
	            sb.append(String.format("ANTIGUAS: X: %.2f | Y: %.2f\n", oldX, oldY));
	            sb.append(String.format("CORRECCIÓN: ΔX: %.2f | ΔY: %.2f\n", dX, dY));
	            sb.append(String.format("NUEVAS:   X: %.2f | Y: %.2f\n", newX, newY));
	            sb.append("--------------------------------\n");
	            sb.append("Estado: Re-graficado exitosamente.");

	            if (puntoParaCallback != null) {
	                callback.onCalculationComplete(puntoParaCallback, sb.toString());
	            } else {
	                 JOptionPane.showMessageDialog(dialog, sb.toString(), "Modificación Exitosa", JOptionPane.INFORMATION_MESSAGE);
	            }
	            
	            dialog.dispose();
	            
	        } catch (NumberFormatException ex) {
	            sonidos.clickError();
	            JOptionPane.showMessageDialog(dialog, "Los valores de corrección deben ser números.");
	        } catch (Exception ex) {
	            sonidos.clickError();
	            JOptionPane.showMessageDialog(dialog, "Error al modificar: " + ex.getMessage());
	        }
	        
	        sit.getPanelMapa().refrescar();
	    });

	    btnCancelar.addActionListener(e -> dialog.dispose());
	    dialog.setVisible(true);
	}
	
	@Override
	public void NivelTrigonometricoDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
	    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
	    JDialog dialog = new JDialog(parentFrame, "MÓDULO DE NIVELACIÓN TRIGONOMÉTRICA", true);
	    dialog.setSize(600, 600); 
	    dialog.setLocationRelativeTo(padre);

	    // Panel Principal
	    JPanel mainPanel = new JPanel(new BorderLayout());
	    mainPanel.setBackground(new Color(30, 30, 30));
	    mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

	    // SECCIÓN DE FORMULARIO
	    JPanel formPanel = new JPanel(new GridBagLayout());
	    formPanel.setOpaque(false);
	    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(10, 10, 10, 10);
	    gbc.fill = GridBagConstraints.HORIZONTAL;

	    Font fLabel = new Font("Arial", Font.BOLD, 14);
	    Font fCombo = new Font("Arial", Font.PLAIN, 18);
	    Font fInput = new Font("Monospaced", Font.BOLD, 22);

	    // 1. Estación de Origen y Objetivo
	    gbc.gridx = 0; gbc.gridy = 0;
	    formPanel.add(FabricaComponentes.crearEtiqueta("ESTACIÓN ORIGEN (Cota Conocida)", fLabel), gbc);
	    gbc.gridx = 1;
	    JComboBox<Posicionable> comboOrigen = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
	    formPanel.add(comboOrigen, gbc);

	    gbc.gridx = 0; gbc.gridy = 1;
	    formPanel.add(FabricaComponentes.crearEtiqueta("OBJETIVO A ACTUALIZAR", fLabel), gbc);
	    gbc.gridx = 1;
	    JComboBox<Posicionable> comboObjetivo = FabricaComponentes.crearComboPuntosYBlancos(fCombo, sit.getListaDePuntos(), sit.getListaBlancos());
	    formPanel.add(comboObjetivo, gbc);

	    // Separador
	    gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
	    formPanel.add(new JSeparator(), gbc);
	    gbc.gridwidth = 1;

	    // 2. Datos de Nivelación
	    gbc.gridx = 0; gbc.gridy = 3;
	    formPanel.add(FabricaComponentes.crearEtiqueta("ALTURA INSTRUMENTO (m)", fLabel), gbc);
	    JTextField txtAltInst = FabricaComponentes.crearCampoTexto(fInput);
	    txtAltInst.setText("1.50"); 
	    gbc.gridx = 1; formPanel.add(txtAltInst, gbc);

	    gbc.gridx = 0; gbc.gridy = 4;
	    formPanel.add(FabricaComponentes.crearEtiqueta("ÁNGULO VERTICAL (mils) [+ Elev / - Depr]", fLabel), gbc);
	    JTextField txtAngVert = FabricaComponentes.crearCampoTexto(fInput);
	    txtAngVert.setText("0");
	    gbc.gridx = 1; formPanel.add(txtAngVert, gbc);

	    // --- PANEL DE BOTONES ---
	    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
	    buttonPanel.setOpaque(false);
	    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

	    JButton btnCalcular = new JButton("ACTUALIZAR COTA");
	    FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(40, 70, 120)); 
	    
	    JButton btnCancelar = new JButton("CANCELAR");
	    FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 45, 45));

	    buttonPanel.add(btnCalcular);
	    buttonPanel.add(btnCancelar);

	    mainPanel.add(formPanel, BorderLayout.CENTER);
	    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	    dialog.add(mainPanel);

	    // ACCIONES
	    btnCalcular.addActionListener(e -> {
	        try {
	            Posicionable origen = (Posicionable) comboOrigen.getSelectedItem();
	            Posicionable objetivo = (Posicionable) comboObjetivo.getSelectedItem();
	            
	            if (origen == null || objetivo == null) throw new Exception("Seleccione Estación y Objetivo.");
	            if (origen.equals(objetivo)) throw new Exception("El origen y el objetivo deben ser distintos.");

	            // Parsing
	            double altInst = Double.parseDouble(txtAltInst.getText().trim());
	            double angVertMils = Double.parseDouble(txtAngVert.getText().trim());
	            
	            // Datos Geográficos
	            double distHorizontal = origen.getCoordenadas().distanciaA(objetivo.getCoordenadas());
	            double cotaOrigen = origen.getCoordenadas().getCota();
	            double cotaAntiguaObjetivo = objetivo.getCoordenadas().getCota();
	            
	            // Matemática (Trigonometría)
	            // Tan(angulo) = Cateto Opuesto (Desnivel) / Cateto Adyacente (Distancia)
	            // Desnivel = Distancia * Tan(angulo)
	            double angRad = Math.toRadians(angVertMils * 360.0 / 6400.0);
	            double deltaZ = distHorizontal * Math.tan(angRad);
	            
	            // Fórmula: Z_final = Z_origen + Alt_Instrumento + Desnivel_Calculado
	            double cotaFinal = cotaOrigen + altInst + deltaZ;
	            
	            // Crear la nueva coordenada (Mismos X e Y, nueva Z)
	            CoordenadasRectangulares coordActualizada = new CoordenadasRectangulares(
	                objetivo.getCoordenadas().getX(), 
	                objetivo.getCoordenadas().getY(), 
	                cotaFinal
	            );
	            Punto puntoParaCallback = null;

	            if (objetivo instanceof Punto) {
	                Punto p = (Punto) objetivo;
	                // 1. Eliminar rastro antiguo
	                sit.getListaDePuntos().remove(p);
	                sit.getListaPoligonales().remove(p);
	                sit.getModeloListaPoligonales().removeElement(p);
	                sit.getPanelMapa().eliminarPoligonal(p);

	                // 2. Modificar
	                p.setCoord(coordActualizada);

	                // 3. Reinsertar (El mapa lo dibujará de nuevo)
	                sit.agregarPunto(p);
	                puntoParaCallback = p;

	            } else if (objetivo instanceof Blanco) {
	                Blanco b = (Blanco) objetivo;
	                // 1. Eliminar rastro antiguo
	                sit.getListaBlancos().remove(b);
	                sit.getModeloListaBlancos().removeElement(b);
	                sit.getPanelMapa().eliminarBlanco(b);

	                // 2. Modificar
	                b.setCoordenadas(coordActualizada);

	                // 3. Reinsertar
	                sit.agregarBlanco(b);
	            }

	            // Informe
	            StringBuilder sb = new StringBuilder();
	            sb.append("MÉTODO: NIVELACIÓN TRIGONOMÉTRICA (ACTUALIZACIÓN)\n\n");
	            sb.append(String.format("ORIGEN: %s (Z: %.2f m | Inst: %.2f m)\n", origen.getNombre(), cotaOrigen, altInst));
	            sb.append(String.format("OBJETIVO: %s\n", objetivo.getNombre()));
	            sb.append("--------------------------------------\n");
	            sb.append(String.format("DISTANCIA HORIZONTAL: %.2f m\n", distHorizontal));
	            sb.append(String.format("ÁNGULO VERTICAL: %.0f mils\n", angVertMils));
	            sb.append(String.format("DESNIVEL (ΔZ): %s%.2f m\n", (deltaZ >= 0 ? "+" : ""), deltaZ));
	            sb.append("--------------------------------------\n");
	            sb.append(String.format("COTA ANTERIOR: %.2f m\n", cotaAntiguaObjetivo));
	            sb.append(String.format("NUEVA COTA (Z): %.2f m", cotaFinal));

	            // Notificar
	            if (puntoParaCallback != null) {
	                callback.onCalculationComplete(puntoParaCallback, sb.toString());
	            } else {
	                 JOptionPane.showMessageDialog(dialog, sb.toString(), "Nivelación Exitosa", JOptionPane.INFORMATION_MESSAGE);
	            }
	            
	            dialog.dispose();
	            
	        } catch (NumberFormatException ex) {
	            sonidos.clickError();
	            JOptionPane.showMessageDialog(dialog, "Error numérico: Verifique altura y ángulo.");
	        } catch (Exception ex) {
	            sonidos.clickError();
	            JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
	        }
	        
	        sit.getPanelMapa().refrescar();
	    });

	    btnCancelar.addActionListener(e -> dialog.dispose());
	    dialog.setVisible(true);
	}
	
	@Override
	public void RegistroPPALDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback) {
		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(padre);
        JDialog dialog = new JDialog(parentFrame, "EXPORTACIÓN DE REGISTRO TÁCTICO (PDF)", true);
        dialog.setSize(550, 450); 
        dialog.setLocationRelativeTo(padre);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fLabel = new Font("Arial", Font.BOLD, 14);
        Font fInput = new Font("Arial", Font.PLAIN, 18);

        // 1. Datos del Operador
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(FabricaComponentes.crearEtiqueta("OPERADOR / JEFE DE PIEZA:", fLabel), gbc);
        JTextField txtOperador = new JTextField("Operador Táctico");
        txtOperador.setFont(fInput);
        gbc.gridx = 1; formPanel.add(txtOperador, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(FabricaComponentes.crearEtiqueta("UNIDAD / DESTINO:", fLabel), gbc);
        JTextField txtUnidad = new JTextField("Batería Alfa");
        txtUnidad.setFont(fInput);
        gbc.gridx = 1; formPanel.add(txtUnidad, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(FabricaComponentes.crearEtiqueta("OBSERVACIONES DE CAMPO:", fLabel), gbc);
        JTextField txtObs = new JTextField("Sin novedades.");
        txtObs.setFont(fInput);
        gbc.gridx = 1; formPanel.add(txtObs, gbc);

        // PANEL DE BOTONES
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JButton btnCalcular = new JButton("GENERAR PDF");
        FabricaComponentes.configurarBotonEstilo(btnCalcular, new Color(120, 40, 40)); 
        
        JButton btnCancelar = new JButton("CANCELAR");
        FabricaComponentes.configurarBotonEstilo(btnCancelar, new Color(85, 85, 85));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);

        btnCalcular.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("DATOS DEL RESPONSABLE DE LA EXPORTACIÓN:\n");
                sb.append(String.format(" - OPERADOR: %s\n", txtOperador.getText().trim()));
                sb.append(String.format(" - UNIDAD: %s\n", txtUnidad.getText().trim()));
                sb.append(String.format(" - OBSERVACIONES: %s", txtObs.getText().trim()));

                callback.onCalculationComplete(null, sb.toString());
                dialog.dispose();
            } catch (Exception ex) {
                sonidos.clickError();
                JOptionPane.showMessageDialog(dialog, "Error al procesar los datos: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
	}
}
