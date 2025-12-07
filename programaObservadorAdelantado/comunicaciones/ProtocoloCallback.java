package comunicaciones;

public interface ProtocoloCallback {

    /**
     * Mensaje recibido por TCP (línea completa).
     */
    void recibir(String mensaje);

    /**
     * Mensaje de log / estado de la comunicación.
     */
    void log(String texto);
}
