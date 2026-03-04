package mensajes;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import app.PedidoDeFuego;
import app.SituacionTacticaTopografica;
import comunicaciones.ConsolaMensajes;
import comunicaciones.DispatcherNotificacionesTacticas;
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
    private PedidoDeFuego panelPif;

    public ProcesadorMensajes(PedidoDeFuego panelPif, SituacionTacticaTopografica panelTactico,LinkedList<Blanco> listaDeBlancos, LinkedList<Punto> listaDePuntos) {
        this.panelTactico = panelTactico;
        this.listaDeBlancos = listaDeBlancos;
        this.listaDePuntos = listaDePuntos;
        this.panelPif = panelPif;
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
            case "MTO":
            	procesarMTO(mensaje);
            default:
                consola.agregarMensaje("[INFO] Mensaje desconocido: " + mensaje);
        }
    }
    
    private void procesarEstado(String msg) {

        String contenido = ProtocoloMensajes.obtenerCampo(msg, "MSG");
        if (contenido == null) return;

        consola.agregarMensaje("[HARRIS] " + contenido);

        if (contenido.toUpperCase().contains("FUEGO") || contenido.toUpperCase().contains("DISPARO")) {
            if (panelPif != null && panelPif.getMetodoYTiroPanel() != null) {

            	panelPif.getMetodoYTiroPanel().getCorreccionesPanel().iniciarCuentaRegresivaVolido();
            }
        }
        SwingUtilities.invokeLater(() ->
            DispatcherNotificacionesTacticas.mostrar(
                "ESTADO OPERATIVO",
                contenido
            )
        );
    }
    
    private void procesarMTO(String msg) {
    	String EPA = ProtocoloMensajes.obtenerCampo(msg, "EPA");
    	String ANGOB = ProtocoloMensajes.obtenerCampo(msg, "ANGOB");
    	String TVOLIDO = ProtocoloMensajes.obtenerCampo(msg, "TVOLIDO");

    	panelPif.recibirMTO(EPA,ANGOB,TVOLIDO);
    		
        consola.agregarMensaje("[MTO] RECIBIDO");
               
        SwingUtilities.invokeLater(() ->
        DispatcherNotificacionesTacticas.mostrar(
            "MTO",
            "RECIBIDO Y DISPONIBLE")
    );
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

            for (Punto p : listaDePuntos) {
                if (p.getNombre().equalsIgnoreCase(nombre)) {
                    p.setCoord(coords);
                    panelTactico.actualizarPunto(p); 
                    consola.agregarMensaje("[ACTUALIZADO] Punto: " + nombre);
                    return;
                }
            }

            Punto nuevoPunto = new Punto(coords, nombre);
            listaDePuntos.add(nuevoPunto);
            panelTactico.agregarPunto(nuevoPunto); 

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
    
    public void procesarCrudo(String msg) {
        consola.agregarMensaje("[RX RAW] " + msg);
    }
    
    private void procesarBlanco(String msg) {
        try {
            String nombre = ProtocoloMensajes.obtenerCampo(msg, "NOMBRE");
            
            double x = Double.parseDouble(ProtocoloMensajes.obtenerCampo(msg, "X"));
            double y = Double.parseDouble(ProtocoloMensajes.obtenerCampo(msg, "Y"));
            double z = Double.parseDouble(ProtocoloMensajes.obtenerCampo(msg, "Z"));
            CoordenadasRectangulares coords = new CoordenadasRectangulares(x, y, z);

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
                new Thread(() -> {
                    try {
                        Thread.sleep(150);
                        SwingUtilities.invokeLater(() -> {
                            panelTactico.getPanelMapa().refrescar();
                        });
                    } catch (InterruptedException ignored) {}
                }).start();
                
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

    	b.setCoordenadas(coords);
        String fecha = ProtocoloMensajes.obtenerCampo(msg, "FECHA");
        if (fecha != null) b.setFecha(fecha);
        
        String entidad = ProtocoloMensajes.obtenerCampo(msg, "ENTIDAD");
        String afiliacion = ProtocoloMensajes.obtenerCampo(msg, "AFILIACION");
        String echelon = ProtocoloMensajes.obtenerCampo(msg, "ECHELON");
        
        if (entidad != null) b.setUltEntidad(entidad);
        if (afiliacion != null) b.setUltAfiliacion(afiliacion);
        if (echelon != null) b.setUltEchelon(echelon);

        String nat = ProtocoloMensajes.obtenerCampo(msg, "NAT");
        String simID = ProtocoloMensajes.obtenerCampo(msg, "SIMID");
        if (nat != null) b.setNaturaleza(nat);
        if (simID != null) b.setSimID(simID);

        String orient = ProtocoloMensajes.obtenerCampo(msg, "ORI");
        String situacion = ProtocoloMensajes.obtenerCampo(msg, "SIT");
        
        if (orient != null) b.setOrientacion(Double.parseDouble(orient));
        if (situacion != null) {
            try {
                b.setSituacionMovimiento(SituacionMovimiento.valueOf(situacion));
            } catch (Exception e) {
            }
        }

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
