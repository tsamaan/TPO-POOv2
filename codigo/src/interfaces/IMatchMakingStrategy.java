
package interfaces;

import models.Scrim;
import models.Usuario;
import java.util.List;

/**
 * Interface Strategy para algoritmos de matchmaking
 *
 * CORRECCIÓN: Strategy NO debe modificar estado del Scrim.
 * Solo debe SELECCIONAR jugadores según criterio específico.
 *
 * @pattern Strategy (Fixed)
 */
public interface IMatchMakingStrategy {

	/**
	 * Selecciona jugadores de una lista de candidatos según el criterio específico
	 *
	 * @param candidatos Lista de usuarios candidatos disponibles
	 * @param scrim Scrim con los requisitos (rango, latencia, etc.)
	 * @return Lista de usuarios seleccionados que cumplen el criterio
	 */
	List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim);

	/**
	 * @deprecated Use seleccionar() instead
	 * Mantenido para backward compatibility con código existente
	 */
	@Deprecated
	default void ejecutarEmparejamiento(Scrim scrim) {
		// Implementación legacy - transiciona estado si cupo completo
		if (scrim.getPostulaciones().size() >= scrim.getCuposMaximos() - 2) {
			scrim.cambiarEstado(new states.EstadoLobbyCompleto());
		}
	}
}
