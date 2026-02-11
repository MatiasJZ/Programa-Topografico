package util;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GestorSonido {

    private final Map<String, Clip> sonidos = new HashMap<>();

    public GestorSonido() {
        cargarSonidos();
    }

    private void cargarSonidos() {
        sonidos.put("clickError", cargar("clickError.wav"));
        sonidos.put("ingresoError", cargar("ingresoError.wav"));
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
    
    public void clickError() {
        reproducir("clickError");
    }

    public void ingresoError() {
        reproducir("ingresoError");
    }

}
