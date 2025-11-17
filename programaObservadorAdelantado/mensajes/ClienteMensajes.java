package mensajes;
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
            while (activo) {
                String msg = puerto.recibir();

                if (msg != null && !msg.isEmpty()) {
                    String msgFinal = msg;
                    SwingUtilities.invokeLater(() ->
                        procesador.procesar(msgFinal)
                    );
                }

                try { Thread.sleep(20); } catch (Exception ignored) {}
            }
        });

        t.start();
    }

    public void detener() {
        activo = false;
    }
}
