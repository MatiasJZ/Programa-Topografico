package mensajes;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;

import harris.GestorPuertoHarris;

public class ClienteMensajes {

    private GestorPuertoHarris puerto;
    private ProcesadorMensajes procesador;
    private boolean activo = true;

    public ClienteMensajes(GestorPuertoHarris puerto, ProcesadorMensajes procesador) {
        this.puerto = puerto;
        this.procesador = procesador;

        iniciar();
    }

    private void iniciar() {

        Thread t = new Thread(() -> {
        	
            boolean ultimoEstado = puerto.estaAbierto();

            while (activo) {
                boolean estadoActual = puerto.estaAbierto();
                // Detecta desconexión
                if (ultimoEstado && !estadoActual) {
                    sonidoDesconectar();
                    SwingUtilities.invokeLater(() ->
                        procesador.procesar("ESTADO|MSG=DESCONECTADO")
                    );
                }
                // Detecta conexión
                if (!ultimoEstado && estadoActual) {
                    sonidoConectar();
                    SwingUtilities.invokeLater(() ->
                        procesador.procesar("ESTADO|MSG=CONECTADO")
                    );
                }
                ultimoEstado = estadoActual;
                if (estadoActual) {
                    String msg = puerto.recibir();
                    if (msg != null && !msg.isEmpty()) {
                        String finalMsg = msg;
                        SwingUtilities.invokeLater(() -> procesador.procesar(finalMsg));
                    }
                }
                try { Thread.sleep(100); } catch (Exception ignored) {}
            }
        });
        
        t.start();
    }

    private void sonidoConectar() {
        Toolkit.getDefaultToolkit().beep();
    }

    private void sonidoDesconectar() {
        Toolkit.getDefaultToolkit().beep();
        try { Thread.sleep(120); } catch (Exception ignored) {}
        Toolkit.getDefaultToolkit().beep();
    }
    
    public void detener() {
        activo = false;
    }
}
