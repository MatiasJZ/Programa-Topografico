package mensajes;
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
