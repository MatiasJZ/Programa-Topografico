package util;

import dominio.Punto;

/**
 * Callback interface for handling the creation of a {@link Punto} object.
 * Implement this interface to receive notifications when a new {@code Punto} is created.
 */
public interface PuntoCallback {
    void onPuntoCreated(Punto p);
}