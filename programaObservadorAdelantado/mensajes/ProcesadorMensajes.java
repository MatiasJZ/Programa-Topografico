package mensajes;
import java.util.LinkedList;

import app.ConsolaMensajes;
import app.PopupAlerta;
import app.SituacionTactica;
import dominio.Blanco;
import dominio.SituacionMovimiento;
import dominio.coordRectangulares;

public class ProcesadorMensajes {

    private ConsolaMensajes consola;
    private SituacionTactica panelTactico;
    private LinkedList<Blanco> listaDeBlancos;

    public ProcesadorMensajes(ConsolaMensajes consola,SituacionTactica panelTactico,LinkedList<Blanco> listaDeBlancos) {
        this.consola = consola;
        this.panelTactico = panelTactico;
        this.listaDeBlancos = listaDeBlancos;
    }

    public void procesar(String mensaje) {

        if (mensaje == null || mensaje.trim().isEmpty()) return;

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

    private void procesarEstado(String msg) {
        String contenido = ProtocoloMensajes.obtenerCampo(msg, "MSG");
        if (contenido == null) return;
        consola.agregarMensaje("[RADIO] " + contenido);
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

        coordRectangulares coords = new coordRectangulares(x, y,0);

        // Buscamos si existe en la lista
        for (Blanco b : listaDeBlancos) {
            if (b.getNombre().equalsIgnoreCase(nombre)) {

                // Actualizar campos existentes
                b.setCoordenadas(coords);
                b.setNaturaleza(nat);
                b.setFecha(fecha);

                if (orient != null) b.setOrientacion(Double.parseDouble(orient));
                if (info != null) b.setInformacionAdicional(info);
                if (simID != null) b.setSimID(simID);

                if (situacion != null) {
                    try {
                        b.setSituacionMovimiento(
                            SituacionMovimiento.valueOf(situacion)
                        );
                    } catch (Exception ignored) {}
                }

                panelTactico.actualizarBlanco(b);
                consola.agregarMensaje("[ACTUALIZADO] Blanco: " + nombre);
                return;
            }
        }
        Blanco nuevo = new Blanco(nombre, coords, nat, fecha);

        if (orient != null) nuevo.setOrientacion(Double.parseDouble(orient));
        if (info != null) nuevo.setInformacionAdicional(info);
        if (simID != null) nuevo.setSimID(simID);

        if (situacion != null) {
            try {
                nuevo.setSituacionMovimiento(
                    SituacionMovimiento.valueOf(situacion)
                );
            } catch (Exception ignored) {}
        }

        listaDeBlancos.add(nuevo);
        panelTactico.agregarBlanco(nuevo);

        consola.agregarMensaje("[NUEVO BLANCO] " + nombre + "  (" + nat + ")");
    }
    
    private void procesarAviso(String msg) {

        String contenido = ProtocoloMensajes.obtenerCampo(msg, "MSG");

        if (esCritico(contenido)) {
            PopupAlerta.mostrar("ALERTA", contenido);
        }

        consola.agregarMensaje("[AVISO] " + contenido);
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
