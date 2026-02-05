package app;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroCalculos {
    // Usamos un LinkedHashMap para que los datos mantengan el orden en que se calcularon
    private static final Map<String, String> bitacora = new LinkedHashMap<>();

    public static void guardar(String titulo, String resultado) {
        bitacora.put(titulo, resultado);
    }

    public static Map<String, String> getBitacora() {
        return bitacora;
    }
}