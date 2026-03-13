package paneles;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * PanelHistorialNotificaciones — Lista lateral de notificaciones tácticas recibidas.
 *
 * <p>Cada tarjeta muestra tipo, nombre resumido y hora. Al hacer clic se abre
 * un diálogo de detalle con borde del color del tipo, mostrando todos los
 * datos de la notificación (tipo, nombre completo, IP de origen, hora).
 *
 * @author [Matias Leonel Juarez]
 * @version 1.1
 */
public class PanelHistorialNotificaciones extends JPanel {

    private static final long serialVersionUID = 1L;

    public enum TipoNotificacion {
        BLANCO, AVISO, ESTADO, ACK, MTO, PUNTO
    }

    private static final Color COLOR_BLANCO = new Color(220, 80,  80);
    private static final Color COLOR_AVISO  = new Color(220, 160, 30);
    private static final Color COLOR_ESTADO = new Color(60,  160, 220);
    private static final Color COLOR_ACK    = new Color(60,  180, 90);
    private static final Color COLOR_MTO    = new Color(160, 90,  220);
    private static final Color COLOR_PUNTO  = new Color(80,  200, 180);

    private static final Color COLOR_FONDO_TARJETA = new Color(28, 28, 28);
    private static final Color COLOR_FONDO_PANEL   = new Color(18, 18, 18);
    private static final Color COLOR_HOVER_TARJETA = new Color(40, 40, 40);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");


    private record EntradaNotificacion(
        TipoNotificacion tipo,
        String nombre,      
        String mensaje,   
        String ipOrigen, 
        String hora       
    ) {}

    private final LinkedList<EntradaNotificacion> entradas = new LinkedList<>();
    private final JPanel panelTarjetas;
    private final JScrollPane scroll;

    public PanelHistorialNotificaciones() {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO_PANEL);
        setPreferredSize(new Dimension(185, 0));
        setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 8, 8, 4),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(80, 80, 80)),
                        "HISTORIAL",
                        0, 0,
                        new Font("Arial", Font.BOLD, 12),
                        Color.GRAY)
        ));

        panelTarjetas = new JPanel();
        panelTarjetas.setLayout(new BoxLayout(panelTarjetas, BoxLayout.Y_AXIS));
        panelTarjetas.setBackground(COLOR_FONDO_PANEL);

        scroll = new JScrollPane(panelTarjetas);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_FONDO_PANEL);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        add(scroll, BorderLayout.CENTER);
    }

    public void agregar(TipoNotificacion tipo, String nombre) {
        agregar(tipo, nombre, nombre, "");
    }

    public void agregar(TipoNotificacion tipo, String nombre, String mensaje, String ipOrigen) {
        String hora = LocalTime.now().format(FMT_HORA);
        EntradaNotificacion entrada = new EntradaNotificacion(tipo, nombre, mensaje, ipOrigen, hora);

        SwingUtilities.invokeLater(() -> {
            entradas.addFirst(entrada);
            panelTarjetas.add(crearTarjeta(entrada), 0);
            panelTarjetas.revalidate();
            panelTarjetas.repaint();
            SwingUtilities.invokeLater(() ->
                scroll.getVerticalScrollBar().setValue(0)
            );
        });
    }

    public void limpiar() {
        SwingUtilities.invokeLater(() -> {
            entradas.clear();
            panelTarjetas.removeAll();
            panelTarjetas.revalidate();
            panelTarjetas.repaint();
        });
    }

    public int getCantidad() {
        return entradas.size();
    }

    private JPanel crearTarjeta(EntradaNotificacion e) {
        Color acento = acentoPara(e.tipo());

        JPanel tarjeta = new JPanel(new BorderLayout(0, 2));
        tarjeta.setBackground(COLOR_FONDO_TARJETA);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, acento),
                new EmptyBorder(6, 8, 6, 6)
        ));

        JLabel lblTipo = new JLabel(e.tipo().name());
        lblTipo.setFont(new Font("Arial", Font.BOLD, 11));
        lblTipo.setForeground(acento);

        JLabel lblNombre = new JLabel(recortar(e.nombre(), 18));
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setToolTipText(e.nombre());

        JLabel lblHora = new JLabel(e.hora());
        lblHora.setFont(new Font("Consolas", Font.PLAIN, 11));
        lblHora.setForeground(new Color(130, 130, 130));

        JPanel panelTexto = new JPanel(new GridLayout(3, 1, 0, 1));
        panelTexto.setBackground(COLOR_FONDO_TARJETA);
        panelTexto.add(lblTipo);
        panelTexto.add(lblNombre);
        panelTexto.add(lblHora);

        tarjeta.add(panelTexto, BorderLayout.CENTER);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent ev) {
                tarjeta.setBackground(COLOR_HOVER_TARJETA);
                panelTexto.setBackground(COLOR_HOVER_TARJETA);
            }
            @Override
            public void mouseExited(MouseEvent ev) {
                tarjeta.setBackground(COLOR_FONDO_TARJETA);
                panelTexto.setBackground(COLOR_FONDO_TARJETA);
            }
            @Override
            public void mouseClicked(MouseEvent ev) {
                mostrarDetalle(e);
            }
        };

        tarjeta.addMouseListener(ma);
        panelTexto.addMouseListener(ma);
        lblTipo.addMouseListener(ma);
        lblNombre.addMouseListener(ma);
        lblHora.addMouseListener(ma);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(45, 45, 45));
        sep.setBackground(COLOR_FONDO_PANEL);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_FONDO_PANEL);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        wrapper.add(tarjeta, BorderLayout.CENTER);
        wrapper.add(sep, BorderLayout.SOUTH);

        return wrapper;
    }

    private void mostrarDetalle(EntradaNotificacion e) {
        Color acento = acentoPara(e.tipo());

        Window padre = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(padre, "Detalle de Notificación",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setUndecorated(true);
        dlg.setSize(420, 310);
        dlg.setLocationRelativeTo(padre);

        JPanel panelRaiz = new JPanel(new BorderLayout());
        panelRaiz.setBackground(new Color(22, 22, 22));
        panelRaiz.setBorder(BorderFactory.createLineBorder(acento, 2));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 30));
        header.setBorder(new EmptyBorder(10, 14, 8, 14));

        JLabel lblTipoHeader = new JLabel(e.tipo().name());
        lblTipoHeader.setFont(new Font("Arial", Font.BOLD, 16));
        lblTipoHeader.setForeground(acento);

        JLabel lblHoraHeader = new JLabel(e.hora());
        lblHoraHeader.setFont(new Font("Consolas", Font.PLAIN, 13));
        lblHoraHeader.setForeground(new Color(160, 160, 160));

        header.add(lblTipoHeader, BorderLayout.WEST);
        header.add(lblHoraHeader, BorderLayout.EAST);

        JSeparator lineaHeader = new JSeparator();
        lineaHeader.setForeground(acento);

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(30, 30, 30));
        panelHeader.add(header, BorderLayout.CENTER);
        panelHeader.add(lineaHeader, BorderLayout.SOUTH);

        panelRaiz.add(panelHeader, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(new Color(22, 22, 22));
        cuerpo.setBorder(new EmptyBorder(12, 14, 8, 14));

        cuerpo.add(crearFilaDetalle("DESIGNACIÓN",    e.nombre(),  acento));
        cuerpo.add(Box.createVerticalStrut(8));

        String ipTexto = (e.ipOrigen() == null || e.ipOrigen().isBlank())
                ? "LOCAL / DESCONOCIDA"
                : e.ipOrigen();
        cuerpo.add(crearFilaDetalle("ORIGEN IP",      ipTexto,     acento));
        cuerpo.add(Box.createVerticalStrut(8));

        cuerpo.add(crearFilaDetalle("HORA RECEPCIÓN", e.hora(),    acento));
        cuerpo.add(Box.createVerticalStrut(12));

        JLabel lblMsgTitulo = new JLabel("MENSAJE");
        lblMsgTitulo.setFont(new Font("Arial", Font.BOLD, 11));
        lblMsgTitulo.setForeground(acento);
        lblMsgTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cuerpo.add(lblMsgTitulo);
        cuerpo.add(Box.createVerticalStrut(4));

        JTextArea txtMensaje = new JTextArea(e.mensaje());
        txtMensaje.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtMensaje.setForeground(Color.WHITE);
        txtMensaje.setBackground(new Color(30, 30, 30));
        txtMensaje.setEditable(false);
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 55, 55)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        txtMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollMensaje = new JScrollPane(txtMensaje);
        scrollMensaje.setBorder(null);
        scrollMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollMensaje.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        cuerpo.add(scrollMensaje);

        panelRaiz.add(cuerpo, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panelBoton.setBackground(new Color(22, 22, 22));

        JButton btnCerrar = new JButton("CERRAR");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCerrar.setBackground(new Color(35, 35, 35));
        btnCerrar.setForeground(acento);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(acento, 1),
                new EmptyBorder(6, 28, 6, 28)
        ));
        btnCerrar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent ev) {
                btnCerrar.setBackground(acento);
                btnCerrar.setForeground(Color.BLACK);
            }
            @Override public void mouseExited(MouseEvent ev) {
                btnCerrar.setBackground(new Color(35, 35, 35));
                btnCerrar.setForeground(acento);
            }
        });
        btnCerrar.addActionListener(ev -> dlg.dispose());

        panelBoton.add(btnCerrar);
        panelRaiz.add(panelBoton, BorderLayout.SOUTH);

        dlg.setContentPane(panelRaiz);
        dlg.setVisible(true);
    }

    private JPanel crearFilaDetalle(String etiqueta, String valor, Color acento) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setBackground(new Color(22, 22, 22));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.BOLD, 11));
        lblEtiqueta.setForeground(acento);
        lblEtiqueta.setPreferredSize(new Dimension(120, 18));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Consolas", Font.PLAIN, 13));
        lblValor.setForeground(Color.WHITE);

        fila.add(lblEtiqueta, BorderLayout.WEST);
        fila.add(lblValor, BorderLayout.CENTER);

        return fila;
    }

    private static Color acentoPara(TipoNotificacion tipo) {
        return switch (tipo) {
            case BLANCO -> COLOR_BLANCO;
            case AVISO  -> COLOR_AVISO;
            case ESTADO -> COLOR_ESTADO;
            case ACK    -> COLOR_ACK;
            case MTO    -> COLOR_MTO;
            case PUNTO  -> COLOR_PUNTO;
        };
    }

    private static String recortar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() > max ? texto.substring(0, max - 1) + "…" : texto;
    }
}