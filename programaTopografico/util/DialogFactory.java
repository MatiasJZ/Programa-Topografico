package util;

import java.util.List;

import dominio.Blanco;
import dominio.CoordenadasRectangulares;
import dominio.Posicionable;
import dominio.Punto;

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
	void CierrePoligonalDialog(Punto puntoCalculado, List<Punto> puntosReales, CalculoCallback callback);
    
}
