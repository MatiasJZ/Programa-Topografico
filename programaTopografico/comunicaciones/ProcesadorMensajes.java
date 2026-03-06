package comunicaciones;

import java.util.LinkedList;
import javax.swing.SwingUtilities;

import app.PedidoDeFuego;
import app.SituacionTacticaTopografica;
import dominio.Blanco;
import dominio.Punto;
import dominio.SituacionMovimiento;
import gestores.GestorEnlaceOperativo;
import dominio.CoordenadasRectangulares;

/**
 * ProcesadorMensajes es responsable de interpretar y procesar mensajes recibidos
 * en el contexto de una aplicación topográfica táctica. Gestiona la actualización
 * y creación de objetos como puntos y blancos, así como la notificación de avisos
 * y estados operativos a la consola y al usuario mediante popups.
 * <p>
 * Funcionalidades principales:
 * <ul>
 * <li>Procesar mensajes de tipo "BLANCO", "PUNTO", "AVISO" y "ESTADO".</li>
 * <li>Actualizar o agregar nuevos puntos y blancos a las listas correspondientes.</li>
 * <li>Notificar a la interfaz gráfica sobre cambios relevantes mediante DispatcherNotificacionesTacticas.</li>
 * <li>Registrar mensajes informativos, advertencias y errores en la consola asociada.</li>
 * </ul>
 * * Dependencias:
 * <ul>
 * <li>ConsolaMensajes: para mostrar mensajes en la interfaz de usuario.</li>
 * <li>SituacionTacticaTopografica: panel táctico donde se visualizan puntos y blancos.</li>
 * <li>ProtocoloMensajes: utilitario para extraer campos y tipos de los mensajes recibidos.</li>
 * <li>DispatcherNotificacionesTacticas: para mostrar notificaciones emergentes.</li>
 * </ul>
 * * Uso típico:
 * <pre>
 * ProcesadorMensajes procesador = new ProcesadorMensajes(panel, listaBlancos, listaPuntos);
 * procesador.setConsola(consolaMensajes);
 * procesador.procesar(mensajeRecibido);
 * </pre>
 * * @author [Matias Leonel Juarez]
 * @version 1.0
 */
public class ProcesadorMensajes {

    private ConsolaMensajes consola;
    private SituacionTacticaTopografica panelTactico;
    private LinkedList<Blanco> listaDeBlancos;
    private LinkedList<Punto> listaDePuntos;
    private PedidoDeFuego panelPif;
    private GestorEnlaceOperativo comunicacionIP;

    public ProcesadorMensajes(PedidoDeFuego panelPif, SituacionTacticaTopografica panelTactico, LinkedList<Blanco> listaDeBlancos, LinkedList<Punto> listaDePuntos, GestorEnlaceOperativo comunicacionIP) {
        this.panelTactico = panelTactico;
        this.listaDeBlancos = listaDeBlancos;
        this.listaDePuntos = listaDePuntos;
        this.panelPif = panelPif;
        this.comunicacionIP = comunicacionIP;
    }

    public void procesar(String mensajeConRastreo) {

        if (mensajeConRastreo == null || mensajeConRastreo.trim().isEmpty()) return;
        
        if (consola == null) {
            System.out.println("[WARN] Procesador sin consola vinculada.");
        }

        String ipRemota = "";
        String mensajeReal = mensajeConRastreo;

        if (mensajeConRastreo.contains("||")) {
            String[] partes = mensajeConRastreo.split("\\|\\|", 2);
            ipRemota = partes[0];
            mensajeReal = partes[1];
        }

        String tipo = ProtocoloMensajes.obtenerTipo(mensajeReal);

        switch (tipo) {
            case "BLANCO":
                procesarBlanco(mensajeReal, ipRemota);
                break;
            case "PUNTO":
                procesarPunto(mensajeReal, ipRemota);
                break;
            case "AVISO":
                procesarAviso(mensajeReal, ipRemota);
                break;
            case "ESTADO":
                procesarEstado(mensajeReal, ipRemota);
                break;  
            case "MTO":
                procesarMTO(mensajeReal, ipRemota);
                break;
            case "ACK":
                procesarAck(mensajeReal, ipRemota);
                break;
            default:
                consola.mostrarRx(ipRemota, "CHAT/DESCONOCIDO: " + mensajeReal);
        }
    }
    
    private void procesarAck(String msg, String ipRemota) {
        String contenido = ProtocoloMensajes.obtenerCampo(msg, "MSG");
        if (contenido == null) return;

        consola.mostrarRx(ipRemota, "CONFIRMACIÓN RECIBIDA: " + contenido);

        SwingUtilities.invokeLater(() ->
            DispatcherNotificacionesTacticas.mostrar("ACUSE DE RECIBO", contenido, null, null)
        );
    }
    
    private void procesarEstado(String msg, String ipRemota) {
        String contenido = ProtocoloMensajes.obtenerCampo(msg, "MSG");
        if (contenido == null) return;

        consola.mostrarRx(ipRemota, "HARRIS ESTADO: " + contenido);

        if (contenido.toUpperCase().contains("FUEGO") || contenido.toUpperCase().contains("DISPARO")) {
            if (panelPif != null && panelPif.getMetodoYTiroPanel() != null) {
                panelPif.getMetodoYTiroPanel().getCorreccionesPanel().iniciarCuentaRegresivaVolido();
            }
        }
        SwingUtilities.invokeLater(() ->
            DispatcherNotificacionesTacticas.mostrar("ESTADO OPERATIVO", contenido, ipRemota, comunicacionIP)
        );
    }
    
    private void procesarMTO(String msg, String ipRemota) {
        String EPA = ProtocoloMensajes.obtenerCampo(msg, "EPA");
        String ANGOB = ProtocoloMensajes.obtenerCampo(msg, "ANGOB");
        String TVOLIDO = ProtocoloMensajes.obtenerCampo(msg, "TVOLIDO");

        panelPif.recibirMTO(EPA,ANGOB,TVOLIDO);
            
        consola.mostrarRx(ipRemota, "MTO RECIBIDO");
                
        SwingUtilities.invokeLater(() ->
            DispatcherNotificacionesTacticas.mostrar("MTO", "RECIBIDO Y DISPONIBLE", ipRemota, comunicacionIP)
        );
    }
    
    private void procesarPunto(String msg, String ipRemota) {
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
                    consola.mostrarSistema("Punto actualizado localmente: " + nombre);
                    return;
                }
            }

            Punto nuevoPunto = new Punto(coords, nombre);
            listaDePuntos.add(nuevoPunto);
            panelTactico.agregarPunto(nuevoPunto); 

            consola.mostrarRx(ipRemota, "NUEVO PUNTO: " + nombre);
            
            String tituloPunto = "PUNTO RECIBIDO: " + nombre;
            SwingUtilities.invokeLater(() ->
                DispatcherNotificacionesTacticas.mostrar(
                        tituloPunto, 
                        "Nombre: " + nombre + "\nX: " + x + " Y: " + y, 
                        ipRemota, 
                        comunicacionIP)
            );

        } catch (Exception e) {
            consola.agregarMensaje("[ERROR] Fallo al procesar PUNTO: " + e.getMessage());
        }
    }
    
    public void setConsola(ConsolaMensajes consola) {
        this.consola = consola;
    }
    
    public void procesarCrudo(String msg) {
        consola.mostrarRx("DESCONOCIDO", "RAW: " + msg);
    }
    
    private void procesarBlanco(String msg, String ipRemota) {
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
                
                consola.mostrarSistema("Reemplazando blanco existente: " + nombre);
            }

            String nat = ProtocoloMensajes.obtenerCampo(msg, "NAT");
            String fecha = ProtocoloMensajes.obtenerCampo(msg, "FECHA");
            
            Blanco nuevoBlanco = new Blanco(nombre, coords, nat, fecha);
            
            mapearDatosBlanco(nuevoBlanco, msg, coords);
            panelTactico.agregarBlanco(nuevoBlanco);

            consola.mostrarRx(ipRemota, "BLANCO TÁCTICO: " + nombre);

            mostrarPopupBlanco(blancoAnterior == null ? "NUEVO BLANCO" : "BLANCO ACTUALIZADO", nuevoBlanco, coords, ipRemota);
            
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
            } catch (Exception e) {}
        }

        String info = ProtocoloMensajes.obtenerCampo(msg, "INFO");
        if (info != null) b.setInformacionAdicional(info);
    }
    
    private void procesarAviso(String msg, String ipRemota) {
        String contenido = ProtocoloMensajes.obtenerCampo(msg, "MSG");
        if (contenido == null) return;

        consola.mostrarRx(ipRemota, "AVISO: " + contenido);
        String titulo = esCritico(contenido) ? "ALERTA CRÍTICA" : "AVISO";

        SwingUtilities.invokeLater(() ->
            DispatcherNotificacionesTacticas.mostrar(titulo, contenido, ipRemota, comunicacionIP)
        );
    }
    
    private void mostrarPopupBlanco(String tituloBase, Blanco b, CoordenadasRectangulares c, String ipRemota) {
        String tituloCompleto = tituloBase + ": " + b.getNombre();
        String texto =
                "Nombre: " + b.getNombre() + "\n" +
                "Naturaleza: " + b.getNaturaleza() + "\n" +
                "Coordenadas: X=" + c.getX() + "  Y=" + c.getY() + "\n" +
                (b.getSituacionMovimiento() != null
                        ? "Situación: " + b.getSituacionMovimiento()
                        : "");

        SwingUtilities.invokeLater(() ->
            DispatcherNotificacionesTacticas.mostrar(tituloCompleto, texto, ipRemota, comunicacionIP)
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