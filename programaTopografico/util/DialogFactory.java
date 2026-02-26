package util;

import java.util.List;

import dominio.Blanco;
import dominio.CoordenadasRectangulares;
import dominio.Posicionable;
import dominio.Punto;

/**
 * DialogFactory define una interfaz para la creación de diversos diálogos utilizados en la aplicación topográfica.
 * Proporciona métodos para mostrar diálogos relacionados con la gestión de entidades (puntos, blancos, posiciones)
 * y para la ejecución de cálculos topográficos específicos.
 *
 * <p>Los métodos de esta interfaz permiten:</p>
 * <ul>
 *   <li>Agregar, editar e informar sobre entidades como Blancos y Puntos.</li>
 *   <li>Configurar parámetros generales de la aplicación.</li>
 *   <li>Medir posiciones a partir de un origen dado.</li>
 *   <li>Ejecutar cálculos topográficos como radiación, triangulación, intersecciones, trilateración, nivel trigonométrico, cierre poligonal, entre otros.</li>
 *   <li>Registrar y actualizar coordenadas y datos magnéticos.</li>
 * </ul>
 *
 * <p>Cada método recibe los parámetros necesarios para inicializar el diálogo correspondiente y, en su caso,
 * un callback para manejar el resultado de la interacción del usuario.</p>
 */
public interface DialogFactory {
    // Diálogos de Entidades
    void AgregarBlancoDialog(CoordenadasRectangulares coord, BlancoCallback callback);
    void AgregarPuntoDialog(CoordenadasRectangulares coord, PuntoCallback callback);
    void EditarBlancoDialog(Blanco blanco, BlancoCallback callback);
    void AgregarEnPolaresDialog(Blanco blanco, BlancoCallback callback);
    void InfoBlancoDialog(Blanco blanco);
    void InfoPuntoDialog(Posicionable p);
    void ConfiguracionDialog();
    void MedirDialog(Posicionable origen);
    
    // Diálogos de Cálculos Topográficos
    void RadiacionDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void TriangulacionDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void InterseccionInversa3PDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void TrilateracionDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void InterseccionInversa2PDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void InterseccionDirectaMDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void MesaPlottingDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void AnguloBaseDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void ActualizacionMagneticaDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void RegistroCoordModDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void NivelTrigonometricoDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
    void RegistroPPALDialog(List<Punto> puntos, List<Blanco> blancos, CalculoCallback callback);
	void CierrePoligonalDialog(Punto puntoCalculado, CalculoCallback callback);
    
}
