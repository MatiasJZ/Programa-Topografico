package gestores;

import javax.sound.sampled.*;
import javax.swing.Timer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * La clase GestorSonido gestiona la carga y reproducción de efectos de sonido en la aplicación.
 * Utiliza un mapa para almacenar clips de sonido identificados por una clave.
 * 
 * <p>Actualmente, soporta los siguientes sonidos:
 * <ul>
 *   <li>"clickError": Sonido para indicar un error al hacer clic.</li>
 *   <li>"ingresoError": Sonido para indicar un error de ingreso.</li>
 * </ul>
 * 
 * <p>Los archivos de sonido deben estar disponibles en el classpath.
 * 
 * <p>Métodos públicos:
 * <ul>
 *   <li>{@link #clickError()}: Reproduce el sonido de error al hacer clic.</li>
 *   <li>{@link #ingresoError()}: Reproduce el sonido de error de ingreso.</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:
 * <pre>
 *     GestorSonido gestor = new GestorSonido();
 *     gestor.clickError();
 * </pre>
 * 
 * @author [Matias Leonel Juarez]
 */

public class GestorSonido {

    private final Map<String, Clip> sonidos = new HashMap<>();

    public GestorSonido() {
        cargarSonidos();
    }

    private void cargarSonidos() {
        sonidos.put("ClickError", cargar("ClickError.wav"));
        sonidos.put("IngresoError", cargar("IngresoError.wav"));
        sonidos.put("5segRestantes", cargar("5segRestantes.wav"));
        sonidos.put("AlertaFinalPique", cargar("AlertaFinalPique.wav"));
    }

    private Clip cargar(String archivo) {
        try {
            URL url = getClass().getResource(archivo);
            if (url == null) {
                System.err.println("No se encontró el archivo de sonido: " + archivo);
                return null;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;

        } catch (Exception e) {
            System.err.println("Error al cargar sonido " + archivo + ": " + e.getMessage());
            return null;
        }
    }

    private void reproducir(String clave) {
        Clip clip = sonidos.get(clave);
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public void detenerSonido5segRestantes() {
        Clip clip = sonidos.get("5segRestantes");
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    
    public void clickError() {
        reproducir("ClickError");
    }

    public void ingresoError() {
        reproducir("IngresoError");
    }

    public void alertaFinalPique() {
        Clip clip = sonidos.get("AlertaFinalPique");
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0); 
            clip.start();
            Timer timerCorte = new Timer(1000, e -> {
                if (clip.isRunning()) {
                    clip.stop();
                }
            });
            timerCorte.setRepeats(false);
            timerCorte.start();
        }
    }
    
    public void cincoSegundosRestantes() {
        reproducir("5segRestantes");
    }
}
