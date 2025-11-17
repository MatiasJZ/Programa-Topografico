package harris;
import com.fazecast.jSerialComm.SerialPort;

public class GestorPuertoHarris {

    private SerialPort puerto;

    public boolean abrir(String nombrePuerto) {
        try {
            puerto = SerialPort.getCommPort(nombrePuerto);
            puerto.setComPortParameters(
                    9600,               
                    8,                  
                    SerialPort.ONE_STOP_BIT,
                    SerialPort.NO_PARITY
            );

            puerto.setComPortTimeouts(
                    SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                    100,
                    0
            );
            return puerto.openPort();

        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo abrir el puerto " + nombrePuerto);
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
        if (!estaAbierto()) return false;

        try {
            String mensaje = texto + "\n";
            byte[] datos = mensaje.getBytes();
            puerto.writeBytes(datos, datos.length);
            return true;

        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo enviar mensaje: " + texto);
            e.printStackTrace();
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
