package app;

import dominio.Posicionable;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Locale;
import org.locationtech.jts.geom.Coordinate;
import dominio.CoordenadasRectangulares;

/**
 * CalculadorTopografico
 * 
 * Clase utilitaria que implementa algoritmos topográficos para el cálculo de coordenadas
 * mediante diversos métodos de levantamiento y posicionamiento.
 * 
 * Métodos principales:
 * 
 * - triangulacion(): Calcula coordenadas por triangulación (base y dos ángulos)
 * - trilateracion(): Calcula coordenadas por trilateración (dos distancias conocidas)
 * - calcularInterseccionMesa(): Calcula intersección de tres líneas de visada
 * - calcularAzimutEnMils(): Convierte ángulos a azimut en milésimos
 * - interseccionDirecta(): Determina coordenadas de un objetivo mediante dos observaciones
 * - radiacion(): Calcula posición por radiación (punto, azimut y distancia)
 * - interseccionInversa2P(): Posicionamiento propio con dos puntos de referencia
 * - interseccionInversa3P(): Posicionamiento propio mediante Algoritmo de Tienstra (3 puntos)
 * - obtenerInformeError(): Genera reportes de precisión y error de cierre en poligonales
 * - calcularPerimetroRecursivo(): Navega grafo de poligonales mediante DFS para acumular distancias
 * 
 * Características:
 * - Todos los ángulos se manejan en milésimos (0-6400)
 * - Soporta objetos que implementen Posicionable para obtener coordenadas
 * - Incluye validaciones de geometría crítica y condiciones de paralelismo
 * - Manejo de excepciones para casos indeterminados
 * - Métodos auxiliares privados para conversiones (radianes, azimut, ángulos reducidos)
 * 
 * @author [Matias Leonel Juarez]
 * @version 1.0
 */
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

	    double x1 = pA.getCoordenadas().getX();
	    double y1 = pA.getCoordenadas().getY();
	    double x2 = pB.getCoordenadas().getX();
	    double y2 = pB.getCoordenadas().getY();

	    double distBase = pA.getCoordenadas().distanciaA(pB.getCoordenadas());
	    double anguloC = 3200 - (anguloA + anguloB); 
	    
	    double distAC = (distBase * Math.sin(milsToRadians(anguloB))) / Math.sin(milsToRadians(anguloC));
	    double azimutAB = calcularAzimutEnMils(x1, y1, x2, y2);
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
	
	public static double calcularAzimutEnMils(double x1, double y1, double x2, double y2) {
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
	    
	    double x1 = p1.getCoordenadas().getX();
	    double y1 = p1.getCoordenadas().getY();
	    double x2 = p2.getCoordenadas().getX();
	    double y2 = p2.getCoordenadas().getY();
	    double x3 = p3.getCoordenadas().getX();
	    double y3 = p3.getCoordenadas().getY();

	    double radAlfa = milsToRadians(alfa);
	    double radBeta = milsToRadians(beta);
	    
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

    public static String calcularCierrePoligonal(LinkedList<Posicionable> camino) {
        if (camino == null || camino.size() < 3) return "Datos insuficientes (Mínimo 3 vértices).";

        StringBuilder sb = new StringBuilder();

        try (Formatter fmt = new Formatter(sb, Locale.US)) {
            
            int n = camino.size();
            double perimetroTotal = 0.0;
            double[] distanciasAcumuladas = new double[n]; 
            distanciasAcumuladas[0] = 0.0;
            
            double suma1 = 0.0; 
            double suma2 = 0.0; 

            for (int i = 0; i < n - 1; i++) {
                Posicionable actual = camino.get(i);
                Posicionable siguiente = camino.get(i+1);

                double distTramo = actual.distanciaA(siguiente);
                perimetroTotal += distTramo;
                distanciasAcumuladas[i+1] = perimetroTotal;

                suma1 += actual.getCoordenadas().getX() * siguiente.getCoordenadas().getY();
                suma2 += actual.getCoordenadas().getY() * siguiente.getCoordenadas().getX();
            }

            double areaMetros = Math.abs(suma1 - suma2) / 2.0;
            double areaHectareas = areaMetros / 10000.0;

            Posicionable pInicial = camino.getFirst();
            Posicionable pFinalCalculado = camino.getLast();

            double eX = pFinalCalculado.getCoordenadas().getX() - pInicial.getCoordenadas().getX();
            double eY = pFinalCalculado.getCoordenadas().getY() - pInicial.getCoordenadas().getY();
            double eZ = pFinalCalculado.getCoordenadas().getCota() - pInicial.getCoordenadas().getCota();
            
            double errorLineal = Math.sqrt(eX * eX + eY * eY);

            long precisionDenominador = (long) (errorLineal > 0.001 ? perimetroTotal / errorLineal : 0);
            String precisionTexto = (errorLineal < 0.01) ? "CIERRE PERFECTO" : "1 : " + precisionDenominador;

            sb.append("================================================================\n");
            sb.append("       REPORTE DE CIERRE DE POLIGONAL (MÉTODO TAF)\n");
            sb.append("================================================================\n\n");
            
            sb.append("1. CONTROL DE PRECISIÓN:\n");
            fmt.format(" - Perímetro Total:         %,12.3f m\n", perimetroTotal);
            fmt.format(" - Error Lineal (ECL):      %,12.3f m\n", errorLineal);
            fmt.format(" - Precisión Relativa:      %s\n", precisionTexto);
            sb.append("\n");
            fmt.format(" - Error en X (ΔX):         %+.3f m\n", eX);
            fmt.format(" - Error en Y (ΔY):         %+.3f m\n", eY);
            fmt.format(" - Error en Z (ΔZ):         %+.3f m\n", eZ);
            sb.append("\n");

            sb.append("2. CÁLCULO DE SUPERFICIE (ÁREA):\n");
            sb.append("----------------------------------------------------------------\n");
            fmt.format(" - SUPERFICIE EN M²:        %,12.2f m²\n", areaMetros);
            fmt.format(" - SUPERFICIE EN HECTÁREAS: %,12.4f ha\n", areaHectareas);
            sb.append("----------------------------------------------------------------\n\n");

            sb.append("3. TABLA DE COORDENADAS COMPENSADAS (AJUSTADAS):\n");
            sb.append("----------------------------------------------------------------\n");
            sb.append(String.format("%-10s | %-14s | %-14s | %-10s\n", "PUNTO", "ESTE (X)", "NORTE (Y)", "COTA (Z)"));
            sb.append("----------------------------------------------------------------\n");

            for (int i = 0; i < n; i++) {
                Posicionable p = camino.get(i);
                double xCrudo = p.getCoordenadas().getX();
                double yCrudo = p.getCoordenadas().getY();
                double zCrudo = p.getCoordenadas().getCota();

                double ratio = (perimetroTotal > 0) ? (distanciasAcumuladas[i] / perimetroTotal) : 0;

                double xCompensada = xCrudo - (eX * ratio);
                double yCompensada = yCrudo - (eY * ratio);
                double zCompensada = zCrudo - (eZ * ratio);

                if (i == n - 1) {
                    xCompensada = pInicial.getCoordenadas().getX();
                    yCompensada = pInicial.getCoordenadas().getY();
                    zCompensada = pInicial.getCoordenadas().getCota();
                }

                fmt.format("%-10s | %,14.3f | %,14.3f | %,10.3f\n", 
                           p.getNombre(), xCompensada, yCompensada, zCompensada);
            }
            sb.append("----------------------------------------------------------------\n");
            sb.append("NOTA: Coordenadas ajustadas por Método de la Brújula (Bowditch).\n");

        } catch (Exception e) {
            return "Error en cálculo: " + e.getMessage();
        }

        return sb.toString();
    }
}
