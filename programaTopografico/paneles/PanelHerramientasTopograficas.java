package paneles;

import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

import app.SituacionTacticaTopografica;
import dominio.Blanco;
import dominio.Punto;
import dominio.RegistroCalculos;
import interfaces.DialogFactory;

/**
 * PanelHerramientasTopograficas — Barra de herramientas de cálculo topográfico.
 *
 * <p>Contiene los botones TRIANG, RAD, TRILAT, INT-INV-3P, INT-INV-2P, INT-D-M,
 * MESA-P, ANG-B, ACT-MAG, NIVEL-T, REG-PPAL, REG-C-M y PIF, con sus respectivos
 * listeners que delegan en {@link DialogFactory}.
 *
 * <p>Todos los botones de cálculo requieren que haya un {@link Blanco} seleccionado
 * en la lista táctica antes de ejecutar. Si no hay selección, se muestra un aviso
 * y la operación se cancela.
 *
 * <p>El panel es visible/ocultable desde el HUD principal.
 *
 * @author [Matias Leonel Juarez]
 * @version 1.1
 */
public class PanelHerramientasTopograficas extends JPanel {

    private static final long serialVersionUID = 1L;

    @FunctionalInterface
    public interface AccionPIF {
        void ejecutar();
    }

    public PanelHerramientasTopograficas(DialogFactory dialogFactory,LinkedList<Punto> listaDePuntos,LinkedList<Blanco> listaDeBlancos,
            SituacionTacticaTopografica ctx,AccionPIF accionPIF) {

        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
        setPreferredSize(new Dimension(1050, 145));

        Font fuenteBtn = new Font("Arial", Font.BOLD, 10);
        Dimension dimBtn = new Dimension(98, 60);

        String[] nombres = {
            "TRIANG","RAD","TRILAT","INT-INV-3P","INT-INV-2P",
            "INT-D-M","MESA-P","ANG-B","ACT-MAG","NIVEL-T","REG-PPAL","REG-C-M"
        };

        for (String nombre : nombres) {
            JButton btn = crearBoton(nombre, fuenteBtn, dimBtn, Color.DARK_GRAY, Color.WHITE);
            add(btn);
            btn.addActionListener(e -> despachar(nombre, dialogFactory, listaDePuntos, listaDeBlancos, ctx));
        }

        JButton pif = crearBoton("PIF", new Font("Arial", Font.BOLD, 12), dimBtn,
                new Color(180, 40, 40), Color.WHITE);
        add(pif);
        pif.addActionListener(e -> {
	        if (ctx.getlistaUIBlancos().getSelectedValue() == null) {
	            JOptionPane.showMessageDialog(ctx,
	                    "Debe seleccionar un blanco de la lista táctica antes de ejecutar esta operación.",
	                    "SELECCIÓN REQUERIDA",
	                    JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        else {
	        	accionPIF.ejecutar();
	        }
    	});
    }

    private void despachar(String cmd, DialogFactory df,
            LinkedList<Punto> puntos, LinkedList<Blanco> blancos,
            SituacionTacticaTopografica ctx) {

        Blanco blancoSeleccionado = ctx.getlistaUIBlancos().getSelectedValue();
        if (blancoSeleccionado == null) {
            JOptionPane.showMessageDialog(ctx,
                    "Debe seleccionar un blanco de la lista táctica antes de ejecutar esta operación.",
                    "SELECCIÓN REQUERIDA",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (cmd) {
            case "TRIANG" ->
                df.TriangulacionDialog(puntos, blancos, (r, inf) -> {
                    ctx.agregarPunto(r); RegistroCalculos.guardar("TRIANGULACIÓN", inf);
                });
            case "RAD" ->
                df.RadiacionDialog(puntos, blancos, (r, inf) -> {
                    ctx.agregarPunto(r); RegistroCalculos.guardar("RADIACIÓN", inf);
                });
            case "INT-INV-3P" ->
                df.InterseccionInversa3PDialog(puntos, blancos, (r, inf) -> {
                    ctx.agregarPunto(r); RegistroCalculos.guardar("INTERSECCIÓN INVERSA 3P", inf);
                    JOptionPane.showMessageDialog(ctx, "Posición propia determinada con éxito.");
                });
            case "MESA-P" ->
                df.MesaPlottingDialog(puntos, blancos, (r, inf) -> {
                    ctx.agregarPunto(r); RegistroCalculos.guardar("MESA PLOTTING", inf);
                    JOptionPane.showMessageDialog(ctx, "Intersección de Plotting graficada con éxito.");
                });
            case "TRILAT" ->
                df.TrilateracionDialog(puntos, blancos, (r, inf) -> {
                    ctx.agregarPunto(r); RegistroCalculos.guardar("TRILATERACIÓN", inf);
                    JOptionPane.showMessageDialog(ctx, "Trilateración calculada y graficada con éxito.");
                });
            case "INT-INV-2P" ->
                df.InterseccionInversa2PDialog(puntos, blancos, (r, inf) -> {
                    ctx.agregarPunto(r); RegistroCalculos.guardar("INTERSECCIÓN INVERSA 2P", inf);
                    JOptionPane.showMessageDialog(ctx, "Posición propia (2P) determinada con éxito.");
                });
            case "INT-D-M" ->
                df.InterseccionDirectaMDialog(puntos, blancos, (r, inf) -> {
                    ctx.agregarPunto(r); RegistroCalculos.guardar("INTERSECCIÓN DIRECTA", inf);
                    JOptionPane.showMessageDialog(ctx, "Objetivo ubicado por Intersección Directa.");
                });
            case "ANG-B" ->
                df.AnguloBaseDialog(puntos, blancos, (r, inf) -> {
                    ctx.agregarPunto(r); RegistroCalculos.guardar("ÁNGULO BASE", inf);
                    JOptionPane.showMessageDialog(ctx, "Objetivo por Ángulo Base graficado con éxito.");
                });
            case "ACT-MAG" ->
                df.ActualizacionMagneticaDialog(puntos, blancos, (r, inf) -> {
                    if (r != null) ctx.agregarPunto(r);
                    RegistroCalculos.guardar("ACTUALIZACIÓN MAGNÉTICA", inf);
                    JOptionPane.showMessageDialog(ctx, "Declinación actualizada con éxito.");
                });
            case "REG-C-M" ->
                df.RegistroCoordModDialog(puntos, blancos, (r, inf) -> {
                    ctx.agregarPunto(r); RegistroCalculos.guardar("COORDENADAS MODIFICADAS", inf);
                    JOptionPane.showMessageDialog(ctx, "Coordenadas modificadas y registradas con éxito.");
                });
            case "NIVEL-T" ->
                df.NivelTrigonometricoDialog(puntos, blancos, (r, inf) -> {
                    ctx.agregarPunto(r); RegistroCalculos.guardar("NIVELACIÓN TRIGONOMÉTRICA", inf);
                    JOptionPane.showMessageDialog(ctx, "Nivelación trigonométrica calculada con éxito.");
                });
            case "REG-PPAL" ->
                df.RegistroPPALDialog(puntos, blancos, (r, inf) -> {
                    RegistroCalculos.guardar("ENCABEZADO DE EXPORTACIÓN", inf);
                    JOptionPane.showMessageDialog(ctx, "Registro PPAL guardado con éxito.");
                    JOptionPane.showMessageDialog(ctx, "Registro PDF exportado correctamente.");
                });
        }
    }

    private JButton crearBoton(String texto, Font fuente, Dimension dim,
            Color fondo, Color frente) {
        JButton btn = new JButton(texto);
        btn.setFont(fuente);
        btn.setPreferredSize(dim);
        btn.setBackground(fondo);
        btn.setForeground(frente);
        btn.setFocusPainted(false);
        return btn;
    }
}