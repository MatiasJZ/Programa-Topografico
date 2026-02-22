package mensajes;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import app.ConsolaMensajes;
import app.DispatcherNotificacionesTacticas;
import app.SituacionTacticaTopografica;
import dominio.Blanco;
import dominio.Punto;
import dominio.SituacionMovimiento;
import dominio.CoordenadasRectangulares;

/**
 * ProcesadorMensajes es responsable de interpretar y procesar mensajes recibidos
 * en el contexto de una aplicación topográfica táctica. Gestiona la actualización
 * y creación de objetos como puntos y blancos, así como la notificación de avisos
 * y estados operativos a la consola y al usuario mediante popups.
 * <p>
 * Funcionalidades principales:
 * <ul>
 *   <li>Procesar mensajes de tipo "BLANCO", "PUNTO", "AVISO" y "ESTADO".</li>
 *   <li>Actualizar o agregar nuevos puntos y blancos a las listas correspondientes.</li>
 *   <li>Notificar a la interfaz gráfica sobre cambios relevantes mediante DispatcherNotificacionesTacticas.</li>
 *   <li>Registrar mensajes informativos, advertencias y errores en la consola asociada.</li>
 * </ul>
 * 
 * Dependencias:
 * <ul>
 *   <li>ConsolaMensajes: para mostrar mensajes en la interfaz de usuario.</li>
 *   <li>SituacionTacticaTopografica: panel táctico donde se visualizan puntos y blancos.</li>
 *   <li>ProtocoloMensajes: utilitario para extraer campos y tipos de los mensajes recibidos.</li>
 *   <li>DispatcherNotificacionesTacticas: para mostrar notificaciones emergentes.</li>
 * </ul>
 * 
 * Uso típico:
 * <pre>
 *     ProcesadorMensajes procesador = new ProcesadorMensajes(panel, listaBlancos, listaPuntos);
 *     procesador.setConsola(consolaMensajes);
 *     procesador.procesar(mensajeRecibido);
 * </pre>
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class ProcesadorMensajes {

    private ConsolaMensajes consola;
    private SituacionTacticaTopografica panelTactico;
    private LinkedList<Blanco> listaDeBlancos;
    private LinkedList<Punto> listaDePuntos;

    public ProcesadorMensajes(SituacionTacticaTopografica panelTactico,LinkedList<Blanco> listaDeBlancos, LinkedList<Punto> listaDePuntos) {
        this.panelTactico = panelTactico;
        this.listaDeBlancos = listaDeBlancos;
        this.listaDePuntos = listaDePuntos;
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
            case "PUNTO": // Nuevo caso
                procesarPunto(mensaje);
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
    
    private void procesarPunto(String msg) {
        try {
            String nombre = ProtocoloMensajes.obtenerCampo(msg, "NOMBRE");
            double x = Double.parseDouble(ProtocoloMensajes.obtenerCampo(msg, "X"));
            double y = Double.parseDouble(ProtocoloMensajes.obtenerCampo(msg, "Y"));
            double z = 0;
            
            String cotaStr = ProtocoloMensajes.obtenerCampo(msg, "Z");
            if(cotaStr != null) z = Double.parseDouble(cotaStr);

            CoordenadasRectangulares coords = new CoordenadasRectangulares(x, y, z);

            // Busco si ya existe para actualizar
            for (Punto p : listaDePuntos) {
                if (p.getNombre().equalsIgnoreCase(nombre)) {
                    p.setCoord(coords);
                    panelTactico.actualizarPunto(p); // Debes tener este método en SituacionTactica
                    consola.agregarMensaje("[ACTUALIZADO] Punto: " + nombre);
                    return;
                }
            }

            // Si llegamos aquí, es un punto nuevo
            Punto nuevoPunto = new Punto(coords, nombre);
            listaDePuntos.add(nuevoPunto);
            panelTactico.agregarPunto(nuevoPunto); // Debes tener este método en SituacionTactica

            consola.agregarMensaje("[NUEVO PUNTO] Recibido: " + nombre);
            
            SwingUtilities.invokeLater(() ->
                DispatcherNotificacionesTacticas.mostrar("PUNTO RECIBIDO", "Nombre: " + nombre + "\nX: " + x + " Y: " + y)
            );

        } catch (Exception e) {
            consola.agregarMensaje("[ERROR] Fallo al procesar PUNTO: " + e.getMessage());
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
            DispatcherNotificacionesTacticas.mostrar(
                "ESTADO OPERATIVO",
                contenido
            )
        );
    }
    
    public void procesarCrudo(String msg) {
        consola.agregarMensaje("[RX RAW] " + msg);
    }
    
    private void procesarBlanco(String msg) {
        try {
            String nombre = ProtocoloMensajes.obtenerCampo(msg, "NOMBRE");
            
            double x = Double.parseDouble(ProtocoloMensajes.obtenerCampo(msg, "X"));
            double y = Double.parseDouble(ProtocoloMensajes.obtenerCampo(msg, "Y"));
            CoordenadasRectangulares coords = new CoordenadasRectangulares(x, y, 0);

            Blanco blancoAnterior = null;
            for (Blanco b : listaDeBlancos) {
                if (b.getNombre().equalsIgnoreCase(nombre)) {
                    blancoAnterior = b;
                    break;
                }
            }

            if (blancoAnterior != null) {
                listaDeBlancos.remove(blancoAnterior);
                panelTactico.getModeloListaBlancos().removeElement(blancoAnterior);
                panelTactico.getPanelMapa().eliminarBlanco(blancoAnterior);
                
                consola.agregarMensaje("[SISTEMA] Reemplazando blanco existente: " + nombre);
            }

            String nat = ProtocoloMensajes.obtenerCampo(msg, "NAT");
            String fecha = ProtocoloMensajes.obtenerCampo(msg, "FECHA");
            
            Blanco nuevoBlanco = new Blanco(nombre, coords, nat, fecha);
            
            mapearDatosBlanco(nuevoBlanco, msg, coords);
            panelTactico.agregarBlanco(nuevoBlanco);

            if (blancoAnterior == null) {
                consola.agregarMensaje("[SISTEMA] Nuevo blanco detectado: " + nombre);
            }

            mostrarPopupBlanco(blancoAnterior == null ? "NUEVO BLANCO" : "BLANCO ACTUALIZADO", nuevoBlanco, coords);
            
        } catch (Exception e) {
            consola.agregarMensaje("[ERROR] Datos de blanco corruptos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void mapearDatosBlanco(Blanco b, String msg, CoordenadasRectangulares coords) {
        // 1. Datos Geográficos y Temporales
    	b.setCoordenadas(coords);
        String fecha = ProtocoloMensajes.obtenerCampo(msg, "FECHA");
        if (fecha != null) b.setFecha(fecha);
        
        // 2. Atributos de Identificación Militar (Campos que faltaban)
        String entidad = ProtocoloMensajes.obtenerCampo(msg, "ENTIDAD");
        String afiliacion = ProtocoloMensajes.obtenerCampo(msg, "AFILIACION");
        String echelon = ProtocoloMensajes.obtenerCampo(msg, "ECHELON");
        
        if (entidad != null) b.setUltEntidad(entidad);
        if (afiliacion != null) b.setUltAfiliacion(afiliacion);
        if (echelon != null) b.setUltEchelon(echelon);

        // 3. Simbología y Naturaleza
        String nat = ProtocoloMensajes.obtenerCampo(msg, "NAT");
        String simID = ProtocoloMensajes.obtenerCampo(msg, "SIMID");
        if (nat != null) b.setNaturaleza(nat);
        if (simID != null) b.setSimID(simID);

        // 4. Estado Dinámico y Orientación
        String orient = ProtocoloMensajes.obtenerCampo(msg, "ORI");
        String situacion = ProtocoloMensajes.obtenerCampo(msg, "SIT");
        
        if (orient != null) b.setOrientacion(Double.parseDouble(orient));
        if (situacion != null) {
            try {
                b.setSituacionMovimiento(SituacionMovimiento.valueOf(situacion));
            } catch (Exception e) {
                // Si el estado no es válido, mantengo el anterior o fijo por defecto
            }
        }

        // 5. Inteligencia Adicional
        String info = ProtocoloMensajes.obtenerCampo(msg, "INFO");
        if (info != null) b.setInformacionAdicional(info);
    }
    
    private void procesarAviso(String msg) {

        String contenido = ProtocoloMensajes.obtenerCampo(msg, "MSG");
        if (contenido == null) return;

        consola.agregarMensaje("[AVISO] " + contenido);

        String titulo = esCritico(contenido) ? "ALERTA CRÍTICA" : "AVISO";

        SwingUtilities.invokeLater(() ->
    		DispatcherNotificacionesTacticas.mostrar(titulo, contenido)
        );
    }
    
    private void mostrarPopupBlanco(String titulo, Blanco b, CoordenadasRectangulares c) {

        String texto =
                "Nombre: " + b.getNombre() + "\n" +
                "Naturaleza: " + b.getNaturaleza() + "\n" +
                "Coordenadas: X=" + c.getX() + "  Y=" + c.getY() + "\n" +
                (b.getSituacionMovimiento() != null
                        ? "Situación: " + b.getSituacionMovimiento()
                        : "");

        SwingUtilities.invokeLater(() ->
            DispatcherNotificacionesTacticas.mostrar(titulo, texto)
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
