package app;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroCalculos {
    private static final Map<String, String> bitacora = new LinkedHashMap<>();

    public static void guardar(String titulo, String resultado) {
        // Si ya existe la clave, concatenamos el nuevo resultado con un separador
        if (bitacora.containsKey(titulo)) {
            String anterior = bitacora.get(titulo);
            bitacora.put(titulo, anterior + "\n--------------------\n" + resultado);
        } else {
            bitacora.put(titulo, resultado);
        }
    }

    public static Map<String, String> getBitacora() {
        return bitacora;
    }
}