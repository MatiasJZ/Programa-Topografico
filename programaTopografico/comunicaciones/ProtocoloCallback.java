package comunicaciones;

import java.io.File;

/**
 * Interfaz para manejar callbacks de protocolo de comunicación.
 * Permite recibir mensajes de texto, archivos y registrar logs.
 */
public interface ProtocoloCallback {

	    void recibir(String mensaje);

	    default void recibirArchivo(File archivo) {}

	    void log(String texto);
	}