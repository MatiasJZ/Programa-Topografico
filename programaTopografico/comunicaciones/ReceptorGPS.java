package comunicaciones;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import javax.swing.SwingUtilities;
import app.SituacionTacticaTopografica;
import dominio.Punto;
import dominio.CoordenadasRectangulares;

/**
 * Escucha pasiva de tramas GPS/SA provenientes de la red táctica.
 * Utiliza UDP Broadcast para captar la posición de otros equipos Harris.
 */
public class ReceptorGPS implements Runnable {

    private DatagramSocket socket;
    private boolean corriendo = false;
    private int puertoEscucha; // Puerto UDP donde la Harris escupe el GPS (Ej: 4001, 10001)
    private SituacionTacticaTopografica panelTactico;

    // Buffer para recibir datos
    private byte[] buffer = new byte[4096];

    public ReceptorGPS(int puerto, SituacionTacticaTopografica panel) {
        this.puertoEscucha = puerto;
        this.panelTactico = panel;
    }

    public void iniciar() {
        if (corriendo) return;
        corriendo = true;
        new Thread(this).start();
    }

    public void detener() {
        corriendo = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(puertoEscucha);

            while (corriendo) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); 
                String trama = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                InetAddress ipEmisor = packet.getAddress();

                analizarTrama(trama, ipEmisor.getHostAddress());
            }

        } catch (Exception e) {
            if (corriendo) {
                System.err.println("Error en Receptor GPS: " + e.getMessage());
            }
        }
    }

    private void analizarTrama(String trama, String ipOrigen) {
        // NOTA IMPORTANTE:
        // Las radios Harris suelen mandar NMEA ($GPGGA, $GPRMC) o COT (XML).
        // Aquí hacemos un parser simple de NMEA como ejemplo.
        
        // Ejemplo NMEA: $GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47
        
        if (trama.startsWith("$GPGGA") || trama.startsWith("$GPRMC")) {
            try {
                // Parsing muy básico de NMEA (Simplificado)
                @SuppressWarnings("unused")
				String[] partes = trama.split(",");
                
                // En GPGGA: Latitud está en índice 2, Longitud en 4 (aprox, depende del fix)
                // OJO: NMEA viene en Grados Decimales Minutos, hay que convertir a UTM o lo que use tu mapa.
                // Para este ejemplo, simulo que extraigo X e Y directos o que ya vienen convertidos.
                
                // SIMULACIÓN DE EXTRACCIÓN (Debes ajustar esto según el dato real que veas en Wireshark)
                // Supongamos que la radio manda algo más simple como "POS|ID|X|Y"
                // O si es NMEA real, necesitarás una librería como "Java Marine API" o convertir a mano.
                
                System.out.println("TRAMA GPS RECIBIDA DE " + ipOrigen + ": " + trama);

                // --- LOGICA DE ACTUALIZACIÓN EN MAPA ---
                // Suponiendo que logramos sacar X e Y de la trama:
                @SuppressWarnings("unused")
				double x = 0; // valor extraido
                @SuppressWarnings("unused")
				double y = 0; // valor extraido
                
                // Si la trama es compleja, la imprimimos para que tú la veas y ajustes el parser
                // System.out.println("GPS RAW: " + trama);

            } catch (Exception e) {
                System.err.println("Error parseando GPS: " + e.getMessage());
            }
        }
        // SOPORTE PARA PROTOCOLO PROPIO (Si decides configurar las otras radios para mandar tu formato)
        else if (trama.startsWith("POSICION|")) {
            // Ejemplo: POSICION|NOMBRE=HARRIS-02|X=12345|Y=67890
            procesarPosicionPropia(trama);
        }
    }

    private void procesarPosicionPropia(String trama) {
        try {
            // Parser manual rápido
            String[] tokens = trama.split("\\|");
            String nombre = "ALIADO-DESC";
            double x = 0, y = 0;

            for(String t : tokens) {
                if(t.startsWith("NOMBRE=")) nombre = t.split("=")[1];
                if(t.startsWith("X=")) x = Double.parseDouble(t.split("=")[1]);
                if(t.startsWith("Y=")) y = Double.parseDouble(t.split("=")[1]);
            }

            final String fNombre = nombre;
            final double fX = x;
            final double fY = y;

            // Actualizar en el hilo de Swing
            SwingUtilities.invokeLater(() -> {
                // Creamos un punto temporal para representar al aliado
                CoordenadasRectangulares coord = new CoordenadasRectangulares(fX, fY, 0);
                Punto aliado = new Punto(coord, fNombre);
                
                // Usamos el método de tu panel táctico para actualizar o agregar
                // Nota: Podrías querer un método específico "actualizarAliado" para pintarlo azul/diferente
                panelTactico.actualizarPunto(aliado); 
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
