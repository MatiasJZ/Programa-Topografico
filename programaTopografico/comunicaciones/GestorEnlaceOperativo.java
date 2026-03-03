package comunicaciones;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * GestorEnlaceOperativo es una clase encargada de gestionar la comunicación TCP entre diferentes nodos
 * en una red local, permitiendo el envío y recepción de mensajes de texto y archivos.
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Permite configurar la interfaz local, el puerto de escucha y la lista de destinos.</li>
 *   <li>Implementa un servidor TCP que acepta conexiones entrantes para recibir mensajes o archivos.</li>
 *   <li>Proporciona métodos para enviar mensajes de texto o archivos a destinos individuales o a todos los destinos configurados.</li>
 *   <li>Utiliza un callback (ProtocoloCallback) para notificar eventos relevantes a la lógica de la aplicación.</li>
 *   <li>Incluye mecanismos para manejar la recepción de archivos, incluyendo la opción de abrirlos automáticamente.</li>
 *   <li>Gestiona la concurrencia mediante sincronización en la lista de destinos y el uso de hilos para operaciones de red.</li>
 * </ul>
 * 
 * <p>Uso típico:</p>
 * <ol>
 *   <li>Configurar la interfaz local, el puerto y los destinos.</li>
 *   <li>Establecer el callback para recibir notificaciones y mensajes.</li>
 *   <li>Iniciar el servidor para comenzar a aceptar conexiones entrantes.</li>
 *   <li>Utilizar los métodos de envío para comunicar mensajes o archivos a otros nodos.</li>
 * </ol>
 * 
 * <p>Nota: Esta clase está diseñada para aplicaciones de escritorio Java y utiliza Swing para algunas interacciones de usuario.</p>
 * 
 * @author [Matias Leonel Juarez]
 */
public class GestorEnlaceOperativo {

    private InetAddress interfazlocal = null; 
    private int puerto = 10011;
    private final List<String> destinos = new ArrayList<>();
    
    private ServerSocket servidor;
    private boolean servidorActivo = false;

    private ProtocoloCallback callback;

    public void setCallback(ProtocoloCallback cb) {
        this.callback = cb;
    }

    public void setInterfazLocal(InetAddress ip) {
        this.interfazlocal = ip;
    }

    public void setPuerto(int p) {
        this.puerto = p;
    }

    public void setDestinos(List<String> ips) {
        synchronized (destinos) {
            destinos.clear();
            if (ips != null) destinos.addAll(ips);
        }
    }

    public void addDestino(String ip) {
        synchronized (destinos) {
            destinos.add(ip);
        }
    }

    public List<String> getDestinos() {
        synchronized (destinos) {
            return new ArrayList<>(destinos);
        }
    }

    public void iniciarServidor() {
        detenerServidor();
        
        if (interfazlocal == null) {
            if (callback != null) callback.log("[ERROR] No se seleccionó interfaz local.");
            return;
        }

        if (callback == null) {
            throw new IllegalStateException("ComunicacionIP: callback no seteado.");
        }

        new Thread(() -> {
            try {

                servidor = new ServerSocket(puerto, 50, interfazlocal);
                servidorActivo = true;

                callback.log("[SERVIDOR] Escuchando en " + interfazlocal.getHostAddress() + ":" + puerto);

                while (servidorActivo) {

                    Socket cli = servidor.accept();

                    new Thread(() -> manejarArchivo(cli),
                            "RX-Cliente-" + cli.getRemoteSocketAddress()
                    ).start();
                }

            } catch (Exception e) {
                servidorActivo = false;
                if (callback != null)
                    callback.log("[ERROR] Servidor detenido: " + e.getMessage());
            }

        }, "ServidorTCP-Harris").start();
    }

    private void manejarArchivo(Socket cli) {

        try (DataInputStream dis =
                     new DataInputStream(cli.getInputStream())) {

            String tipo = dis.readUTF(); 

            if ("TEXT".equals(tipo)) {

                String msg = dis.readUTF();
                callback.recibir(msg);

            } else if ("FILE".equals(tipo)) {

                String nombre = dis.readUTF();
                long size = dis.readLong();

                String userHome = System.getProperty("user.home");
                File desktop = new File(userHome, "Desktop"); 
                File dir = new File(desktop, "archivos recibidos");

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File out = new File(dir, nombre);

                try (FileOutputStream fos = new FileOutputStream(out)) {

                    byte[] buf = new byte[4096];
                    long recibidos = 0;

                    while (recibidos < size) {
                        int r = dis.read(buf, 0,
                                (int) Math.min(buf.length, size - recibidos));
                        if (r == -1) break;
                        fos.write(buf, 0, r);
                        recibidos += r;
                    }
                }

                callback.log("[RX-FILE] " + out.getName() + " (" + size + " bytes)");

                SwingUtilities.invokeLater(() ->
                    preguntarApertura(out)
                );
            }

        } catch (Exception e) {
            callback.log("[ERROR RX] " + e.getMessage());
        } finally {
            try { cli.close(); } catch (Exception ignored) {}
        }
    }
    
    private void preguntarApertura(File f) {

        String[] opciones = {
            "Abrir ahora",
            "Solo descargar",
            "Cancelar"
        };

        int r = JOptionPane.showOptionDialog(
                null,
                "Archivo recibido:\n" + f.getName() +
                "\n\n¿Qué desea hacer?",
                "Archivo recibido",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (r == 0) {
            abrirArchivo(f);
        } else if (r == 1) {
            callback.log("[INFO] Archivo descargado: " + f.getName());
        } else {
            callback.log("[INFO] Recepción ignorada: " + f.getName());
        }
    }

    private void abrirArchivo(File f) {
        try {
            if (!Desktop.isDesktopSupported()) {
                callback.log("[INFO] Apertura automática no soportada.");
                return;
            }
            Desktop.getDesktop().open(f);
            callback.log("[INFO] Archivo abierto: " + f.getName());
        } catch (Exception e) {
            callback.log("[ERROR] No se pudo abrir archivo: " + e.getMessage());
        }
    }
  
    public void enviarArchivo(String ipDestino, File f) {

        if (interfazlocal == null || f == null || !f.exists()) {
            callback.log("[ERROR] Archivo o interfaz inválidos.");
            return;
        }

        new Thread(() -> {
            try (Socket socket = new Socket()) {

                socket.connect(new InetSocketAddress(ipDestino, puerto), 10000);

                DataOutputStream dos =
                        new DataOutputStream(socket.getOutputStream());

                dos.writeUTF("FILE");
                dos.writeUTF(f.getName());
                dos.writeLong(f.length());

                Files.copy(f.toPath(), dos);
                dos.flush();

                callback.log("[TX-FILE → " + ipDestino + "] "
                             + f.getName());

            } catch (Exception e) {
                callback.log("[ERROR TX-FILE] " + e.getMessage());
            }

        }, "TX-FILE-Harris").start();
    }

    public void enviar(String ipDestino, String mensaje) {

        if (interfazlocal == null || callback == null) return;
        if (ipDestino == null || ipDestino.isEmpty()) return;
        if (mensaje == null || mensaje.isEmpty()) return;

        new Thread(() -> {
            try (Socket socket = new Socket()) {

                socket.connect(new InetSocketAddress(ipDestino, puerto), 10000);

                DataOutputStream dos =
                        new DataOutputStream(socket.getOutputStream());

                dos.writeUTF("TEXT");
                dos.writeUTF(mensaje);
                dos.flush();

                callback.log("[TX → " + ipDestino + "] " + mensaje);

            } catch (Exception e) {
                callback.log("[ERROR TX] " + e.getMessage());
            }

        }, "TX-TEXT-Harris").start();
    }
    
    public void enviarATodos(String mensaje) {
        List<String> copia;
        synchronized (destinos) {
            copia = new ArrayList<>(destinos);
        }
        if (copia.isEmpty() && callback != null) {
            callback.log("[ADVERTENCIA] No hay destinos configurados.");
        }
        for (String ip : copia) {
            enviar(ip, mensaje);
        }
    }
    
    public void detenerServidor() {
        servidorActivo = false;
        try {
            if (servidor != null && !servidor.isClosed()) {
                servidor.close();
            }
        } catch (Exception ignored) {}
    }
    
    public InetAddress getInterfazLocal() {
        return interfazlocal;
    }
    
    public int getPuerto() {
        return puerto;
    }
    
}
