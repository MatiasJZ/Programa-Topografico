package app;

import dominio.Posicionable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;

import org.locationtech.jts.geom.Coordinate;

import dominio.CoordenadasRectangulares;
import dominio.Linea;
import dominio.Poligonal;

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
	
    @SuppressWarnings("unused")
	public static CoordenadasRectangulares trilateracion(Posicionable pA, Posicionable pB, double distA, double distB) throws Exception {
        double x1 = pA.getCoordenadas().getX();
        double y1 = pA.getCoordenadas().getY();
        double x2 = pB.getCoordenadas().getX();
        double y2 = pB.getCoordenadas().getY();

        double d = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

        if (d > distA + distB) {
            throw new Exception("Las distancias son muy cortas. Los radios no se alcanzan a tocar.");
        }
        if (d < Math.abs(distA - distB)) {
            throw new Exception("Un radio está completamente dentro del otro. No hay intersección.");
        }
        if (d == 0) {
            throw new Exception("Ambas estaciones base tienen exactamente las mismas coordenadas.");
        }

        double a = (Math.pow(distA, 2) - Math.pow(distB, 2) + Math.pow(d, 2)) / (2 * d);
        
        double h = Math.sqrt(Math.abs(Math.pow(distA, 2) - Math.pow(a, 2)));

        double xp = x1 + (a / d) * (x2 - x1);
        double yp = y1 + (a / d) * (y2 - y1);
        
        double rx = -(h / d) * (y2 - y1);
        double ry = (h / d) * (x2 - x1);

        double xInt1 = xp + rx; 
        double yInt1 = yp + ry;

        double xInt2 = xp - rx; 
        double yInt2 = yp - ry;
        
        return new CoordenadasRectangulares(xInt1, yInt1, 0);
    }
	
	public Coordinate calcularInterseccionMesa(Posicionable p1, double a1, Posicionable p2, double a2, Posicionable p3, double a3) {
	    Coordinate int12 = intersectarDosLineas(p1, a1, p2, a2);
	    Coordinate int23 = intersectarDosLineas(p2, a2, p3, a3);
	    Coordinate int31 = intersectarDosLineas(p3, a3, p1, a1);
	    
	    if (int12 == null || int23 == null || int31 == null) {
	        return null; 
	    }

	    double xPromedio = (int12.x + int23.x + int31.x) / 3.0;
	    double yPromedio = (int12.y + int23.y + int31.y) / 3.0;

	    return new Coordinate(xPromedio, yPromedio);
	}

	private Coordinate intersectarDosLineas(Posicionable pA, double azA, Posicionable pB, double azB) {
	    double radA = Math.toRadians(azA * 360.0 / 6400.0);
	    double radB = Math.toRadians(azB * 360.0 / 6400.0);

	    double A1 = Math.cos(radA);
	    double B1 = -Math.sin(radA);
	    double C1 = A1 * pA.getCoordenadas().getX() + B1 * pA.getCoordenadas().getY();

	    double A2 = Math.cos(radB);
	    double B2 = -Math.sin(radB);
	    double C2 = A2 * pB.getCoordenadas().getX() + B2 * pB.getCoordenadas().getY();

	    double determinante = A1 * B2 - A2 * B1;

	    if (Math.abs(determinante) < 1e-6) {
	        return null;
	    }

	    double xRes = (C1 * B2 - C2 * B1) / determinante;
	    double yRes = (A1 * C2 - A2 * C1) / determinante;

	    return new Coordinate(xRes, yRes);
	}
	
	public double calcularAzimutEnMils(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double radianes = Math.atan2(dx, dy);

        double grados = Math.toDegrees(radianes);

        if (grados < 0) {
            grados += 360;
        }
        double mils = grados * (6400.0 / 360.0);
        return Math.round(mils);
    }
	
	/**
     * Calcula la coordenada de un objetivo desconocido mediante Intersección Directa.
     * @param pA Estación de observación A (conocida)
     * @param pB Estación de observación B (conocida)
     * @param azA Azimut medido desde A hacia el objetivo (en milésimos)
     * @param azB Azimut medido desde B hacia el objetivo (en milésimos)
     * @return CoordenadasRectangulares del objetivo calculado
     */
    public static CoordenadasRectangulares interseccionDirecta(Posicionable pA, Posicionable pB, double azA, double azB) throws Exception {
        
        double radA = Math.toRadians(azA * 360.0 / 6400.0);
        double radB = Math.toRadians(azB * 360.0 / 6400.0);

        double xA = pA.getCoordenadas().getX();
        double yA = pA.getCoordenadas().getY();
        double xB = pB.getCoordenadas().getX();
        double yB = pB.getCoordenadas().getY();

        double A1 = Math.cos(radA);
        double B1 = -Math.sin(radA);
        double C1 = A1 * xA + B1 * yA;

        double A2 = Math.cos(radB);
        double B2 = -Math.sin(radB);
        double C2 = A2 * xB + B2 * yB;

        double determinante = A1 * B2 - A2 * B1;

        if (Math.abs(determinante) < 1e-6) {
            throw new Exception("Geometría nula: Las líneas de observación son paralelas y nunca se cruzarán.");
        }

        double xRes = (C1 * B2 - C2 * B1) / determinante;
        double yRes = (A1 * C2 - A2 * C1) / determinante;

        return new CoordenadasRectangulares(xRes, yRes, 0);
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

	    double radAzimut = (azimut * 2 * Math.PI) / 6400.0;

	    double nuevoX = x1 + distancia * Math.sin(radAzimut);
	    double nuevoY = y1 + distancia * Math.cos(radAzimut);

	    return new CoordenadasRectangulares(nuevoX, nuevoY, origen.getCoordenadas().getCota());
	}	
	
	/**
     * Calcula la coordenada de la posición propia mediante Intersección Inversa de 2 Puntos.
     * @param pA Estación de referencia A (conocida)
     * @param pB Estación de referencia B (conocida)
     * @param azA Azimut medido desde nuestra posición hacia A (en milésimos)
     * @param azB Azimut medido desde nuestra posición hacia B (en milésimos)
     * @return CoordenadasRectangulares de la posición calculada
     */
    public static CoordenadasRectangulares interseccionInversa2P(Posicionable pA, Posicionable pB, double azA, double azB) throws Exception {
        double contraAzA = (azA < 3200) ? azA + 3200 : azA - 3200;
        double contraAzB = (azB < 3200) ? azB + 3200 : azB - 3200;

        double radA = Math.toRadians(contraAzA * 360.0 / 6400.0);
        double radB = Math.toRadians(contraAzB * 360.0 / 6400.0);

        double xA = pA.getCoordenadas().getX();
        double yA = pA.getCoordenadas().getY();
        double xB = pB.getCoordenadas().getX();
        double yB = pB.getCoordenadas().getY();

        double A1 = Math.cos(radA);
        double B1 = -Math.sin(radA);
        double C1 = A1 * xA + B1 * yA;

        double A2 = Math.cos(radB);
        double B2 = -Math.sin(radB);
        double C2 = A2 * xB + B2 * yB;

        double determinante = A1 * B2 - A2 * B1;

        if (Math.abs(determinante) < 1e-6) {
            throw new Exception("Geometría nula: Las líneas de observación son paralelas o estás alineado con las dos bases.");
        }

        double xRes = (C1 * B2 - C2 * B1) / determinante;
        double yRes = (A1 * C2 - A2 * C1) / determinante;

        return new CoordenadasRectangulares(xRes, yRes, 0); 
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

	    double radAlfa = milsToRadians(alfa);
	    double radBeta = milsToRadians(beta);
	    // Angulo total entre P1 y P3
	    double radGamma = radAlfa + radBeta;

	    double az12 = calcularAzimutRadianes(x1, y1, x2, y2);
	    double az23 = calcularAzimutRadianes(x2, y2, x3, y3);
	    double az31 = calcularAzimutRadianes(x3, y3, x1, y1);

	    double A = reducirAngulo(az12 - az31 + Math.PI);
	    double B = reducirAngulo(az23 - az12 + Math.PI);
	    double C = reducirAngulo(az31 - az23 + Math.PI);

	    double w1 = 1.0 / (cot(A) - cot(radAlfa));
	    double w2 = 1.0 / (cot(B) - cot(radGamma)); 
	    double w3 = 1.0 / (cot(C) - cot(radBeta));

	    if (Math.abs(w1 + w2 + w3) < 0.000001) {
	        throw new Exception("Puntos en Círculo Crítico: Geometría indeterminada. Elija otros puntos.");
	    }

	    double nuevoX = (w1 * x1 + w2 * x2 + w3 * x3) / (w1 + w2 + w3);
	    double nuevoY = (w1 * y1 + w2 * y2 + w3 * y3) / (w1 + w2 + w3);

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

    public static String obtenerInformeError(CoordenadasRectangulares coordReal, CoordenadasRectangulares coordCalc, double perimetro, String idReal, String idCalc) {
        
        // 1. Diferencias en ejes
        double errorX = coordCalc.getX() - coordReal.getX();
        double errorY = coordCalc.getY() - coordReal.getY();
        double errorZ = coordCalc.getCota() - coordReal.getCota();

        // 2. Error de Cierre Lineal (Planimétrico - Teorema de Pitágoras)
        double errorLineal = Math.sqrt(Math.pow(errorX, 2) + Math.pow(errorY, 2));

        // 3. Precisión Relativa (1 : X)
        String precisionRelativa = "N/A";
        if (perimetro > 0 && errorLineal > 0) {
            double denominador = perimetro / errorLineal;
            // Se suele redondear al entero más cercano en topografía
            precisionRelativa = "1 : " + Math.round(denominador); 
        } else if (errorLineal == 0) {
            precisionRelativa = "CIERRE PERFECTO (Error 0)";
        }

        // 4. Construcción del informe táctico
        StringBuilder sb = new StringBuilder();
        sb.append("MÉTODO: CONTROL DE CIERRE DE POLIGONAL\n\n");
        
        sb.append("PUNTOS EVALUADOS:\n");
        sb.append(String.format(" - ORIGEN VERDADERO (%s): X: %.2f | Y: %.2f | Z: %.2f\n", idReal, coordReal.getX(), coordReal.getY(), coordReal.getCota()));
        sb.append(String.format(" - LLEGADA CALCULADA (%s): X: %.2f | Y: %.2f | Z: %.2f\n\n", idCalc, coordCalc.getX(), coordCalc.getY(), coordCalc.getCota()));
        
        sb.append("ERRORES LINEALES:\n");
        sb.append(String.format(" - ERROR EN DERECHAS (ΔX): %s%.2f m\n", (errorX > 0 ? "+" : ""), errorX));
        sb.append(String.format(" - ERROR EN ARRIBAS (ΔY): %s%.2f m\n", (errorY > 0 ? "+" : ""), errorY));
        sb.append(String.format(" - ERROR EN COTA (ΔZ): %s%.2f m\n\n", (errorZ > 0 ? "+" : ""), errorZ));
        
        sb.append("RESULTADOS DE PRECISIÓN TÁCTICA:\n");
        sb.append(String.format(" - LONGITUD TOTAL (PERÍMETRO): %.2f m\n", perimetro));
        sb.append(String.format(" - ERROR DE CIERRE LINEAL (ECL): %.3f m\n", errorLineal));
        sb.append(String.format(" - PRECISIÓN RELATIVA: %s", precisionRelativa));

        return sb.toString();
    }

    /**
     * Navega por el grafo de poligonales (DFS) buscando el camino más directo 
     * entre el punto de inicio y el destino, acumulando la distancia.
     */
    public double calcularPerimetroRecursivo(CoordenadasRectangulares actual, CoordenadasRectangulares destino, LinkedList<Poligonal> poligonales, HashSet<String> visitados) {
        // Tolerancia de 10 cm (0.1m) para considerar que chocamos con el objetivo
        if (actual.distanciaA(destino) < 0.1) {
            return 0.0;
        }

        // Usamos una clave String para el Set de visitados y así evitar ciclos infinitos
        String nodoClave = String.format(Locale.US, "%.1f,%.1f", actual.getX(), actual.getY());
        visitados.add(nodoClave);

        for (Poligonal p : poligonales) {
            // Filtramos solo las Líneas, ignorando los Puntos sueltos en el mapa
            if (p.getClass().getSimpleName().equals("Linea")) {
                Linea linea = (Linea) p;
                
                // NOTA: Ajusta "getC1()" y "getC2()" si tu clase Linea los llama diferente (ej: getInicio(), getFin())
                Coordinate c1 = linea.getC1(); 
                Coordinate c2 = linea.getC2(); 

                CoordenadasRectangulares coord1 = new CoordenadasRectangulares(c1.x, c1.y, 0);
                CoordenadasRectangulares coord2 = new CoordenadasRectangulares(c2.x, c2.y, 0);

                CoordenadasRectangulares vecino = null;
                
                // Verificamos si nuestro punto "actual" toca alguno de los extremos de esta línea
                if (actual.distanciaA(coord1) < 0.1) vecino = coord2;
                else if (actual.distanciaA(coord2) < 0.1) vecino = coord1;

                if (vecino != null) {
                    String vecinoClave = String.format(Locale.US, "%.1f,%.1f", vecino.getX(), vecino.getY());
                    
                    if (!visitados.contains(vecinoClave)) {
                        // Navegamos más profundo por este camino (Recursividad)
                        double distRestante = calcularPerimetroRecursivo(vecino, destino, poligonales, visitados);
                        
                        if (distRestante != -1.0) { // Si este camino nos llevó al destino
                            return linea.getDistancia() + distRestante; // Sumamos la distancia de esta línea y regresamos
                        }
                    }
                }
            }
        }

        // Backtracking: liberamos el nodo si llegamos a un callejón sin salida
        visitados.remove(nodoClave);
        return -1.0;
    }
    
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
