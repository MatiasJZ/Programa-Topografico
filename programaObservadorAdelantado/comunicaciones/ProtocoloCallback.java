package comunicaciones;

import java.io.File;

public interface ProtocoloCallback {

	    // Texto (ya existente)
	    void recibir(String mensaje);

	    // NUEVO: archivo recibido
	    default void recibirArchivo(File archivo) {}

	    void log(String texto);
	}