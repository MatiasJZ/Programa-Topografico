package util;

import dominio.Punto;

/**
 * Callback interface for receiving the result of a calculation.
 * <p>
 * Implement this interface to handle the completion of a calculation,
 * receiving both the resulting {@link Punto} and an associated report.
 */
public interface CalculoCallback {
    
    void onCalculationComplete(Punto resultado, String informe);
}