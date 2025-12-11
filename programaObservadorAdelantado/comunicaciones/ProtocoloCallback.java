package comunicaciones;

public interface ProtocoloCallback {

    void recibir(String mensaje);

    void log(String texto);
}
