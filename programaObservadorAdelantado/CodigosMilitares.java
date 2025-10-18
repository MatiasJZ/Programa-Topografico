import java.util.HashMap;
import java.util.Map;

public class CodigosMilitares {

    private static final Map<String, String> mapa = new HashMap<>();

    static {
        // === Amigo (Friendly) ===
        mapa.put("INF", "10031000001101000000"); // Infantería
        mapa.put("ART", "10031000001301000000"); // Artillería
        mapa.put("OBS", "10031000001401000000"); // Observador
        mapa.put("TNK", "10031000001201000000"); // Tanque / Blindado

        // === Enemigo (Hostile) ===
        mapa.put("INF_E", "10061000001101000000"); // Infantería enemiga
        mapa.put("ART_E", "10061000001301000000"); // Artillería enemiga
        mapa.put("TNK_E", "10061000001201000000"); // Blindado enemigo
    }

    public static String obtenerSIDC(String codigoAlfa) {
        return mapa.get(codigoAlfa.toUpperCase());
    }
}
