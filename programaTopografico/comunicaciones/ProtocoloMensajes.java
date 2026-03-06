package comunicaciones;
/**
 * La clase {@code ProtocoloMensajes} proporciona métodos utilitarios para analizar y extraer información
 * de mensajes estructurados en formato de texto, donde los campos están separados por el carácter '|'
 * y cada campo puede tener la forma "clave=valor".
 *
 * <p>Ejemplo de mensaje: {@code "TIPO|campo1=valor1|campo2=valor2"}</p>
 *
 * <ul>
 *   <li>{@link #obtenerTipo(String)}: Extrae el tipo de mensaje, que corresponde al primer campo antes del primer '|'.</li>
 *   <li>{@link #obtenerCampo(String, String)}: Busca y retorna el valor asociado a un campo específico en el mensaje.</li>
 * </ul>
 */
public class ProtocoloMensajes {

    public static String obtenerTipo(String mensaje) {
        return mensaje.split("\\|")[0].trim();
    }

    public static String obtenerCampo(String mensaje, String campo) {
        String[] partes = mensaje.split("\\|");
        for (String p : partes) {
            if (p.startsWith(campo + "=")) {
                return p.substring((campo + "=").length());
            }
        }
        return null;
    }
}
