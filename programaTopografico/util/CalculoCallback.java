package util;

import dominio.Punto;

public interface CalculoCallback {
    
    void onCalculationComplete(Punto resultado, String informe);
}