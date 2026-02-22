package util;

import dominio.Blanco;

/**
 * Callback interface for handling the creation of a {@link Blanco} object.
 * Implement this interface to receive a notification when a new Blanco is created.
 */
public interface BlancoCallback {
    void onBlancoCreated(Blanco b);
}
