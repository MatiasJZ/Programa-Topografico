package dominio;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * La clase {@code RegistroCalculos} proporciona una bitácora estática para almacenar y recuperar
 * registros de cálculos realizados en la aplicación. Cada registro se asocia a un título único
 * y su resultado correspondiente. Si se guarda un resultado bajo un título ya existente, el nuevo
 * resultado se concatena al anterior, separado por una línea divisoria.
 *
 * <p>Esta clase utiliza un {@link LinkedHashMap} para mantener el orden de inserción de los registros.
 *
 * <ul>
 *   <li>{@link #guardar(String, String)}: Guarda un resultado bajo un título específico. Si el título ya existe,
 *       concatena el nuevo resultado al anterior.</li>
 *   <li>{@link #getBitacora()}: Devuelve la bitácora completa de registros.</li>
 * </ul>
 *
 * <p>Esta clase no está diseñada para ser instanciada, ya que todos sus métodos y campos son estáticos.
 */
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