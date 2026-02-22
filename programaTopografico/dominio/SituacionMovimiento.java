package dominio;

/**
 * Enumera las posibles situaciones de movimiento para un objeto o entidad.
 * <ul>
 *   <li><b>FIJO</b>: El objeto permanece en una posición fija.</li>
 *   <li><b>MOVIL</b>: El objeto es capaz de moverse.</li>
 *   <li><b>CUBIERTO</b>: El objeto está protegido o cubierto.</li>
 *   <li><b>DESCUBIERTO</b>: El objeto está expuesto o sin cobertura.</li>
 *   <li><b>FORTIFICADO</b>: El objeto está reforzado o fortificado.</li>
 * </ul>
 */
public enum SituacionMovimiento {
	FIJO,
	MOVIL,
	CUBIERTO,
	DESCUBIERTO,
	FORTIFICADO
}
