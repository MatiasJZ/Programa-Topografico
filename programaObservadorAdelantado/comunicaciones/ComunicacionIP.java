package comunicaciones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ComunicacionIP {

    // Configuración
    private InetAddress interfazlocal = null;      // Interfaz local (Harris)
    private int puerto = 5056;
    private final List<String> destinos = new ArrayList<>();
    
    // Servidor
    private ServerSocket servidor;
    private boolean servidorActivo = false;

    // Callback a la lógica de la app
    private ProtocoloCallback callback;

    // ==========================================================
    // SETUP
    // ==========================================================
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

    // ==========================================================
    // SERVIDOR TCP
    // ==========================================================
    public void iniciarServidor() {

        // --------------------------------------------------------
        // 1) DETENER SERVIDOR ANTERIOR SI EXISTE
        // --------------------------------------------------------
        detenerServidor();   // <--- ESTA ES LA LÍNEA CLAVE

        // --------------------------------------------------------
        // 2) VALIDACIONES
        // --------------------------------------------------------
        if (interfazlocal == null) {
            if (callback != null) callback.log("[ERROR] No se seleccionó interfaz local.");
            return;
        }

        if (callback == null) {
            throw new IllegalStateException("ComunicacionIP: callback no seteado.");
        }

        // --------------------------------------------------------
        // 3) ARRANCAR SERVIDOR NUEVO
        // --------------------------------------------------------
        new Thread(() -> {
            try {

                servidor = new ServerSocket(puerto, 50, interfazlocal);
                servidorActivo = true;

                callback.log("[SERVIDOR] Escuchando en " + interfazlocal.getHostAddress() + ":" + puerto);

                while (servidorActivo) {

                    Socket cli = servidor.accept();

                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(cli.getInputStream())
                    );

                    String msg = br.readLine();
                    if (msg != null && !msg.isEmpty()) {
                        callback.recibir(msg);
                    }

                    cli.close();
                }

            } catch (Exception e) {
                servidorActivo = false;
                if (callback != null)
                    callback.log("[ERROR] Servidor detenido: " + e.getMessage());
            }

        }, "ServidorTCP-Harris").start();
    }


    // ==========================================================
    // CLIENTE TCP - ENVIAR A UNA IP
    // ==========================================================
    public void enviar(String ipDestino, String mensaje) {

        if (interfazlocal == null) {
            if (callback != null) callback.log("[ERROR] No se configuró interfaz local (ipLocal).");
            return;
        }

        if (ipDestino == null || ipDestino.isEmpty()) {
            if (callback != null) callback.log("[ERROR] IP destino vacía.");
            return;
        }

        if (mensaje == null || mensaje.isEmpty()) {
            if (callback != null) callback.log("[INFO] Mensaje vacío, no se envía nada.");
            return;
        }

        new Thread(() -> {
            try {
                Socket socket = new Socket();

                // Forzar uso de la interfaz del Harris
                socket.bind(new InetSocketAddress(interfazlocal, 0));
                socket.connect(new InetSocketAddress(ipDestino, puerto), 3000);

                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                pw.println(mensaje);

                if (callback != null) {
                    callback.log("[TX → " + ipDestino + "] " + mensaje);
                }

                socket.close();

            } catch (Exception e) {
                if (callback != null) {
                    callback.log("[ERROR] No se pudo enviar a " + ipDestino + ": " + e.getMessage());
                }
            }
        }, "ClienteTCP-Harris").start();
    }

    // ==========================================================
    // CLIENTE TCP - ENVIAR A TODOS
    // ==========================================================
    public void enviarATodos(String mensaje) {
        List<String> copia;
        synchronized (destinos) {
            copia = new ArrayList<>(destinos);
        }
        if (copia.isEmpty() && callback != null) {
            callback.log("[WARN] No hay destinos configurados.");
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
