package app;

import dominio.Posicionable;
import dominio.coordRectangulares;

public class CalculadorTopografico {

    /**
     * Calcula las coordenadas de un punto mediante Triangulación (Base y dos ángulos)
     * @param pA Punto de estación A (conocido)
     * @param pB Punto de estación B (conocido)
     * @param anguloA Angulo medido en A hacia el objetivo (en milésimos)
     * @param anguloB Angulo medido en B hacia el objetivo (en milésimos)
     * @return coordRectangulares del nuevo punto
     */
	public static coordRectangulares triangulacion(Posicionable pA, Posicionable pB, double anguloA, double anguloB) {
	    // Extraigo las coordenadas sin importar el tipo de objeto
	    double x1 = pA.getCoordenadas().getX();
	    double y1 = pA.getCoordenadas().getY();
	    double x2 = pB.getCoordenadas().getX();
	    double y2 = pB.getCoordenadas().getY();

	    // El resto de la lógica matemática se mantiene igual...
	    double distBase = pA.getCoordenadas().distanciaA(pB.getCoordenadas());
	    double anguloC = 3200 - (anguloA + anguloB);
	    
	    // ... (tu lógica de senos y azimut) ...
	    
	    // Uso el método milsToRadians que ya tenés definido abajo
	    double distAC = (distBase * Math.sin(milsToRadians(anguloB))) / Math.sin(milsToRadians(anguloC));
	    double azimutAB = calcularAzimut(x1, y1, x2, y2);
	    double azimutObjetivo = azimutAB + anguloA; 

	    double nuevoX = x1 + distAC * Math.sin(milsToRadians(azimutObjetivo));
	    double nuevoY = y1 + distAC * Math.cos(milsToRadians(azimutObjetivo));

	    return new coordRectangulares(nuevoX, nuevoY, pA.getCoordenadas().getCota());
	}

    private static double milsToRadians(double mils) {
        return (mils * 2 * Math.PI) / 6400.0;
    }

    private static double calcularAzimut(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double az = Math.toDegrees(Math.atan2(dx, dy)) * (6400.0 / 360.0);
        return (az < 0) ? az + 6400 : az;
    }
}
