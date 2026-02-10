package mensajes;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import app.ConsolaMensajes;
import app.PopupAlerta;
import app.SituacionTacticaTopo;
import dominio.Blanco;
import dominio.SituacionMovimiento;
import dominio.coordRectangulares;

public class ProcesadorMensajes {

    private ConsolaMensajes consola;
    private SituacionTacticaTopo panelTactico;
    private LinkedList<Blanco> listaDeBlancos;

    public ProcesadorMensajes(SituacionTacticaTopo panelTactico,LinkedList<Blanco> listaDeBlancos) {
        this.panelTactico = panelTactico;
        this.listaDeBlancos = listaDeBlancos;
    }

    public void procesar(String mensaje) {

        if (mensaje == null || mensaje.trim().isEmpty()) return;
        
        if (consola == null) {
            System.out.println("[WARN] Procesador sin consola vinculada: " + mensaje);
        }

        String tipo = ProtocoloMensajes.obtenerTipo(mensaje);

        switch (tipo) {
            case "BLANCO":
                procesarBlanco(mensaje);
                break;
            case "AVISO":
                procesarAviso(mensaje);
                break;
            case "ESTADO":
                procesarEstado(mensaje);
                break;              
            default:
                consola.agregarMensaje("[INFO] Mensaje desconocido: " + mensaje);
        }
    }
    
    public void setConsola(ConsolaMensajes consola) {
        this.consola = consola;
    }
    
    private void procesarEstado(String msg) {

        String contenido = ProtocoloMensajes.obtenerCampo(msg, "MSG");
        if (contenido == null) return;

        consola.agregarMensaje("[RADIO] " + contenido);

        SwingUtilities.invokeLater(() ->
            PopupAlerta.mostrar(
                "ESTADO OPERATIVO",
                contenido
            )
        );
    }
    
    public void procesarCrudo(String msg) {
        consola.agregarMensaje("[RX RAW] " + msg);
    }
    
    private void procesarBlanco(String msg) {

        String nombre = ProtocoloMensajes.obtenerCampo(msg, "NOMBRE");
        String nat = ProtocoloMensajes.obtenerCampo(msg, "NAT");
        String fecha = ProtocoloMensajes.obtenerCampo(msg, "FECHA");
        String orient = ProtocoloMensajes.obtenerCampo(msg, "ORI");
        String info = ProtocoloMensajes.obtenerCampo(msg, "INFO");
        String simID = ProtocoloMensajes.obtenerCampo(msg, "SIMID");
        String situacion = ProtocoloMensajes.obtenerCampo(msg, "SIT");

        double x = Double.parseDouble(ProtocoloMensajes.obtenerCampo(msg, "X"));
        double y = Double.parseDouble(ProtocoloMensajes.obtenerCampo(msg, "Y"));

        coordRectangulares coords = new coordRectangulares(x, y, 0);

        for (Blanco b : listaDeBlancos) {
            if (b.getNombre().equalsIgnoreCase(nombre)) {

                b.setCoordenadas(coords);
                b.setNaturaleza(nat);
                b.setFecha(fecha);

                if (orient != null)
                    b.setOrientacion(Double.parseDouble(orient));

                if (info != null)
                    b.setInformacionAdicional(info);

                if (simID != null)
                    b.setSimID(simID);

                if (situacion != null) {
                    try {
                        b.setSituacionMovimiento(
                            SituacionMovimiento.valueOf(situacion)
                        );
                    } catch (Exception ignored) {}
                }

                panelTactico.actualizarBlanco(b);

                consola.agregarMensaje("[ACTUALIZADO] Blanco: " + nombre);

                mostrarPopupBlanco(
                        "BLANCO ACTUALIZADO",
                        b,
                        coords
                );
                return;
            }
        }

        // Nuevo blanco
        Blanco nuevo = new Blanco(nombre, coords, nat, fecha);

        if (orient != null)
            nuevo.setOrientacion(Double.parseDouble(orient));

        if (info != null)
            nuevo.setInformacionAdicional(info);

        if (simID != null)
            nuevo.setSimID(simID);

        if (situacion != null) {
            try {
                nuevo.setSituacionMovimiento(
                    SituacionMovimiento.valueOf(situacion)
                );
            } catch (Exception ignored) {}
        }

        listaDeBlancos.add(nuevo);
        panelTactico.agregarBlanco(nuevo);

        consola.agregarMensaje("[NUEVO BLANCO] " + nombre + " (" + nat + ")");

        mostrarPopupBlanco(
                "NUEVO BLANCO RECIBIDO",
                nuevo,
                coords
        );
    }
    
    private void procesarAviso(String msg) {

        String contenido = ProtocoloMensajes.obtenerCampo(msg, "MSG");
        if (contenido == null) return;

        consola.agregarMensaje("[AVISO] " + contenido);

        String titulo = esCritico(contenido) ? "ALERTA CRÍTICA" : "AVISO";

        SwingUtilities.invokeLater(() ->
    		PopupAlerta.mostrar(titulo, contenido)
        );
    }
    
    private void mostrarPopupBlanco(String titulo, Blanco b, coordRectangulares c) {

        String texto =
                "Nombre: " + b.getNombre() + "\n" +
                "Naturaleza: " + b.getNaturaleza() + "\n" +
                "Coordenadas: X=" + c.getX() + "  Y=" + c.getY() + "\n" +
                (b.getSituacionMovimiento() != null
                        ? "Situación: " + b.getSituacionMovimiento()
                        : "");

        SwingUtilities.invokeLater(() ->
            PopupAlerta.mostrar(titulo, texto)
        );
    }

    private boolean esCritico(String m) {
        return m.contains("FINAL")
                || m.contains("EFICAZ")
                || m.contains("NEUTRALIZADO")
                || m.contains("PELIGRO")
                || m.contains("SUSPENDIDO")
                || m.contains("ERROR");
    }
}
