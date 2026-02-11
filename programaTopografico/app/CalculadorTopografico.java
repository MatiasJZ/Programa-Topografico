package app;

import dominio.Posicionable;
import dominio.CoordenadasRectangulares;

public class CalculadorTopografico {

    /**
     * Calcula las coordenadas de un punto mediante Triangulación (Base y dos ángulos)
     * @param pA Punto de estación A (conocido)
     * @param pB Punto de estación B (conocido)
     * @param anguloA Angulo medido en A hacia el objetivo (en milésimos)
     * @param anguloB Angulo medido en B hacia el objetivo (en milésimos)
     * @return coordRectangulares del nuevo punto
     */
	public static CoordenadasRectangulares triangulacion(Posicionable pA, Posicionable pB, double anguloA, double anguloB) {
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

	    return new CoordenadasRectangulares(nuevoX, nuevoY, pA.getCoordenadas().getCota());
	}
	
	/**
	 * Calcula coordenadas mediante Radiación (Punto, Azimut y Distancia)
	 * @param origen Punto conocido
	 * @param azimut Dirección en milésimos
	 * @param distancia Distancia al blanco en metros
	 */
	public static CoordenadasRectangulares radiacion(Posicionable origen, double azimut, double distancia) {
	    double x1 = origen.getCoordenadas().getX();
	    double y1 = origen.getCoordenadas().getY();

	    // Convertimos azimut a radianes (6400 mils = 2PI)
	    double radAzimut = (azimut * 2 * Math.PI) / 6400.0;

	    // Proyección de coordenadas (Ecuación fundamental de la topografía)
	    double nuevoX = x1 + distancia * Math.sin(radAzimut);
	    double nuevoY = y1 + distancia * Math.cos(radAzimut);

	    return new CoordenadasRectangulares(nuevoX, nuevoY, origen.getCoordenadas().getCota());
	}	
	
	/**
	 * Calcula la posición del observador mediante el Algoritmo de Tienstra (3 puntos).
	 * @param p1 Punto conocido a la izquierda
	 * @param p2 Punto conocido central
	 * @param p3 Punto conocido a la derecha
	 * @param alfa Angulo medido entre P1 y P2 (en milésimos)
	 * @param beta Angulo medido entre P2 y P3 (en milésimos)
	 * @return coordRectangulares del observador
	 */
	public static CoordenadasRectangulares interseccionInversa3P(Posicionable p1, Posicionable p2, Posicionable p3, double alfa, double beta) throws Exception {
	    // 1. Coordenadas de los puntos de referencia
	    double x1 = p1.getCoordenadas().getX();
	    double y1 = p1.getCoordenadas().getY();
	    double x2 = p2.getCoordenadas().getX();
	    double y2 = p2.getCoordenadas().getY();
	    double x3 = p3.getCoordenadas().getX();
	    double y3 = p3.getCoordenadas().getY();

	    // 2. Convertir ángulos de milésimos a radianes
	    double radAlfa = milsToRadians(alfa);
	    double radBeta = milsToRadians(beta);
	    // Angulo total entre P1 y P3
	    double radGamma = radAlfa + radBeta;

	    // 3. Calcular ángulos internos del triángulo de referencia (P1-P2-P3)
	    // Usamos el teorema de la distancia y azimut para hallar los ángulos en los vértices
	    double az12 = calcularAzimutRadianes(x1, y1, x2, y2);
	    double az23 = calcularAzimutRadianes(x2, y2, x3, y3);
	    double az31 = calcularAzimutRadianes(x3, y3, x1, y1);

	    // Ángulos internos en los vértices P1, P2 y P3
	    double A = reducirAngulo(az12 - az31 + Math.PI);
	    double B = reducirAngulo(az23 - az12 + Math.PI);
	    double C = reducirAngulo(az31 - az23 + Math.PI);

	    // 4. Calcular Pesos de Tienstra (w1, w2, w3)
	    // La fórmula es: wi = 1 / (cot(Angulo_Interno) - cot(Angulo_Observado))
	    double w1 = 1.0 / (cot(A) - cot(radAlfa));
	    double w2 = 1.0 / (cot(B) - cot(radGamma)); // El peso central usa el ángulo total
	    double w3 = 1.0 / (cot(C) - cot(radBeta));

	    // Verificación de Círculo Crítico (si la suma de pesos es cercana a cero)
	    if (Math.abs(w1 + w2 + w3) < 0.000001) {
	        throw new Exception("Puntos en Círculo Crítico: Geometría indeterminada. Elija otros puntos.");
	    }

	    // 5. Coordenadas finales del observador (Promedio ponderado)
	    double nuevoX = (w1 * x1 + w2 * x2 + w3 * x3) / (w1 + w2 + w3);
	    double nuevoY = (w1 * y1 + w2 * y2 + w3 * y3) / (w1 + w2 + w3);

	    // La cota se estima como el promedio de las cotas de referencia
	    double nuevaCota = (p1.getCoordenadas().getCota() + p2.getCoordenadas().getCota() + p3.getCoordenadas().getCota()) / 3.0;

	    return new CoordenadasRectangulares(nuevoX, nuevoY, nuevaCota);
	}
	
	/**
	 * Calcula el error de cierre lineal y la precisión relativa de una poligonal.
	 * @param real Coordenadas verdaderas del punto de cierre.
	 * @param calculado Coordenadas obtenidas mediante el cálculo itinerante.
	 * @param distanciaTotal Sumatoria de todas las distancias medidas en la poligonal.
	 * @return String formateado con el informe de precisión.
	 */
	public static String obtenerInformeError(CoordenadasRectangulares real, CoordenadasRectangulares calculado, double distanciaTotal) {
	    double errorX = real.getX() - calculado.getX();
	    double errorY = real.getY() - calculado.getY();
	    
	    // Error lineal total (Hipotenusa de los errores)
	    double errorLineal = Math.sqrt(Math.pow(errorX, 2) + Math.pow(errorY, 2));
	    
	    // Precisión relativa (1 / (Distancia / Error))
	    double precision = (errorLineal > 0) ? (distanciaTotal / errorLineal) : 0;

	    StringBuilder sb = new StringBuilder();
	    sb.append("--- INFORME DE PRECISIÓN ---\n");
	    sb.append(String.format("Error en X (Derechas): %.3f m\n", errorX));
	    sb.append(String.format("Error en Y (Arribas): %.3f m\n", errorY));
	    sb.append(String.format("ERROR LINEAL TOTAL: %.3f m\n", errorLineal));
	    sb.append(String.format("DISTANCIA TOTAL RECORRIDA: %.2f m\n", distanciaTotal));
	    sb.append(String.format("PRECISIÓN RELATIVA: 1 / %.0f\n", precision));
	    
	    if (precision < 1000) {
	        sb.append("ESTADO: PRECISIÓN INSUFICIENTE (Requiere repetir medición)");
	    } else {
	        sb.append("ESTADO: TOLERANCIA ACEPTABLE");
	    }
	    
	    return sb.toString();
	}

	// Funciones auxiliares necesarias para el cálculo:

	private static double cot(double angulo) {
	    return 1.0 / Math.tan(angulo);
	}

	private static double calcularAzimutRadianes(double x1, double y1, double x2, double y2) {
	    return Math.atan2(x2 - x1, y2 - y1);
	}

	private static double reducirAngulo(double ang) {
	    while (ang <= -Math.PI) ang += 2 * Math.PI;
	    while (ang > Math.PI) ang -= 2 * Math.PI;
	    return Math.abs(ang);
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
