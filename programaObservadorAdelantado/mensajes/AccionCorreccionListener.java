package mensajes;

public interface AccionCorreccionListener {
    void onEnviarCorreccion(String dir, int dirVal, String alc, int alcVal, String alt, int altVal);
    void onFinMision();
    void onNuevoPIF();
}
