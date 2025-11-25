package harris;
import com.fazecast.jSerialComm.SerialPort;

public class GestorPuertoHarris {

    private SerialPort puerto;

    public boolean abrir(String nombrePuerto) {
    	
        try {
        	
            puerto = SerialPort.getCommPort(nombrePuerto);
            puerto.setComPortParameters(9600,8,SerialPort.ONE_STOP_BIT,SerialPort.NO_PARITY);
            puerto.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING,100,0);
            
            return puerto.openPort();

        } catch (Exception e) {System.err.println("[ERROR] No se pudo abrir el puerto " + nombrePuerto);
            e.printStackTrace();
            return false;
        }
    }

    public void cerrar() {
        try {
            if (puerto != null && puerto.isOpen()) {
                puerto.closePort();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo cerrar el puerto.");
        }
    }

    public boolean enviar(String texto) {

        if (texto == null || texto.isEmpty())
            return false;

        try {
            if (estaAbierto()) {
                return enviarDirecto(texto);
            }
            System.out.println("[HARRIS] Puerto cerrado. Intentando reconectar...");
            SerialPort[] puertos = SerialPort.getCommPorts();

            if (puertos.length == 0) {
                System.out.println("[HARRIS] No se encontraron puertos COM.");
                return false;
            }
            // Probar cada puerto disponible
            for (SerialPort p : puertos) {

                System.out.println("[HARRIS] Intentando abrir: " + p.getSystemPortName());

                puerto = p;
                puerto.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                puerto.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

                if (puerto.openPort()) {

                    System.out.println("[HARRIS] Reconectado correctamente a " + p.getSystemPortName());

                    // Intentar enviar tras reconexión
                    return enviarDirecto(texto);
                }
            }
            System.out.println("[HARRIS] No se pudo reconectar a ningún puerto.");
            return false;

        } catch (Exception e) {
            System.err.println("[ERROR] Excepción en enviar(): " + e.getMessage());
            return false;
        }
    }
    
    private boolean enviarDirecto(String texto) {
        try {
            String mensaje = texto + "\n";
            byte[] datos = mensaje.getBytes();
            puerto.writeBytes(datos, datos.length);

            System.out.println("[HARRIS][TX] " + texto);
            return true;

        } catch (Exception e) {
            System.err.println("[ERROR] Falló el envío directo: " + texto);
            return false;
        }
    }

    public String recibir() {
        if (!estaAbierto()) return null;

        try {
            byte[] buffer = new byte[2048];
            int bytesLeidos = puerto.readBytes(buffer, buffer.length);

            if (bytesLeidos > 0) {
                return new String(buffer, 0, bytesLeidos).trim();
            }

        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo leer del puerto.");
        }

        return null;
    }

    public boolean estaAbierto() {
        return puerto != null && puerto.isOpen();
    }
}
