package gestores;

import java.awt.*;
import javax.swing.*;

import app.SituacionTacticaTopografica;
import dominio.Blanco;
import dominio.Punto;
import dominio.RegistroCalculos;
import interfaces.DialogFactory;
import interfaces.Poligonal;
import interfaces.Posicionable;

/**
 * GestorPopupMenus — Construye y conecta los menús contextuales de las listas tácticas.
 *
 * <p>Administra dos {@link JPopupMenu}:
 * <ul>
 *   <li><b>popupBlancos</b>: editar, medir, marcar en polares, info, enviar.</li>
 *   <li><b>popupPoligonales</b>: medir, info, enviar, cerrar poligonal.</li>
 * </ul>
 *
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class GestorPopupMenus {

    private final JPopupMenu popupBlancos;
    private final JPopupMenu popupPoligonales;

    public GestorPopupMenus(
            JList<Blanco>    listaUIBlancos,
            JList<Poligonal> listaUIPoligonales,
            DialogFactory    dialogFactory,
            SituacionTacticaTopografica ctx) {

        popupBlancos    = construirPopupBlancos(listaUIBlancos, dialogFactory, ctx);
        popupPoligonales = construirPopupPoligonales(listaUIPoligonales, dialogFactory, ctx);
    }

    public JPopupMenu getPopupBlancos()     { return popupBlancos; }
    public JPopupMenu getPopupPoligonales() { return popupPoligonales; }

    private JPopupMenu construirPopupBlancos(
            JList<Blanco> lista,
            DialogFactory df,
            SituacionTacticaTopografica ctx) {

        JPopupMenu menu = new JPopupMenu();
        menu.setPreferredSize(new Dimension(260, 220));

        JMenuItem editar        = item("Editar Blanco Seleccionado");
        JMenuItem medir         = item("Marcar Medicion");
        JMenuItem marcarPolares = item("Marcar Nuevo Blanco en Polares");
        JMenuItem info          = item("Informacion del Blanco");
        JMenuItem enviar        = item("Enviar");

        menu.add(editar);
        menu.add(medir);
        menu.add(marcarPolares);
        menu.add(info);
        menu.add(enviar);

        medir.addActionListener(e -> {
            Posicionable sel = lista.getSelectedValue();
            if (sel != null) df.MedirDialog(sel);
        });
        editar.addActionListener(e -> {
            Blanco sel = lista.getSelectedValue();
            if (sel != null)
                df.EditarBlancoDialog(sel, editado -> {
                    lista.repaint();
                    ctx.getPanelMapa().eliminarBlanco(sel);
                    ctx.getPanelMapa().agregarBlanco(editado);
                });
        });
        marcarPolares.addActionListener(e -> {
            Blanco sel = lista.getSelectedValue();
            if (sel != null)
                df.AgregarEnPolaresDialog(sel, nuevo -> {
                    ctx.agregarBlanco(nuevo);
                    String sugerido = ctx.getPrefijo() + " " + ctx.getContador();
                    if (nuevo.getNombre().equals(sugerido)) ctx.incrementarContador();
                });
        });
        info.addActionListener(e -> {
            Blanco sel = lista.getSelectedValue();
            if (sel != null) df.InfoBlancoDialog(sel);
        });
        enviar.addActionListener(e -> {
            Blanco sel = lista.getSelectedValue();
            if (sel != null) ctx.enviarBlanco(sel);
        });

        return menu;
    }

    private JPopupMenu construirPopupPoligonales(
            JList<Poligonal> lista,
            DialogFactory df,
            SituacionTacticaTopografica ctx) {

        JPopupMenu menu = new JPopupMenu();
        menu.setPreferredSize(new Dimension(250, 220));

        JMenuItem medir   = item("Marcar Medicion");
        JMenuItem info    = item("Informacion del Punto");
        JMenuItem enviar  = item("Enviar");
        JMenuItem cerrar  = item("Cerrar Poligonal");
        cerrar.setBackground(new Color(45, 45, 85));

        menu.add(medir);
        menu.add(info);
        menu.add(enviar);
        menu.add(cerrar);

        medir.addActionListener(e -> {
            Posicionable sel = (Posicionable) lista.getSelectedValue();
            if (sel != null) df.MedirDialog(sel);
        });
        info.addActionListener(e -> {
            Posicionable sel = (Posicionable) lista.getSelectedValue();
            if (sel != null) df.InfoPuntoDialog(sel);
        });
        enviar.addActionListener(e -> {
            Posicionable sel = (Posicionable) lista.getSelectedValue();
            if (sel != null) ctx.enviarPunto(sel);
        });
        cerrar.addActionListener(e -> {
            Posicionable sel = (Posicionable) lista.getSelectedValue();
            if (sel != null && sel.soportaCierrePoligonal())
                df.CierrePoligonalDialog((Punto) sel, (r, inf) -> {
                    RegistroCalculos.guardar("CIERRE DE POLIGONAL", inf);
                    JOptionPane.showMessageDialog(ctx, "Control de precisión registrado con éxito.");
                });
        });

        return menu;
    }

    private JMenuItem item(String texto) {
        JMenuItem mi = new JMenuItem(texto);
        mi.setBackground(Color.BLACK);
        mi.setForeground(Color.WHITE);
        mi.setFont(new Font("Arial", Font.BOLD, 15));
        return mi;
    }
}