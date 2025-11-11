package strategies;

import interfaces.IMatchMakingStrategy;
import models.Scrim;
import models.Usuario;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Estrategia de matchmaking por MMR (Match Making Rating / Rango)
 *
 * CORRECCIÓN: Strategy ahora SELECCIONA jugadores sin modificar estado.
 * La lógica de transición de estado pertenece a State Pattern.
 *
 * Criterio de selección:
 * 1. Filtra jugadores dentro del rango permitido (rangoMin - rangoMax)
 * 2. Ordena por cercanía de MMR al rango mínimo del scrim
 * 3. Selecciona hasta el número máximo de cupos
 *
 * @pattern Strategy (Fixed Implementation)
 */
public class ByMMRStrategy implements IMatchMakingStrategy {

    @Override
    public List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim) {
        System.out.println("[STRATEGY - MMR] Filtrando jugadores por rango " +
                         scrim.getRangoMin() + "-" + scrim.getRangoMax());

        return candidatos.stream()
            // Filtrar solo jugadores con rango configurado para el juego
            .filter(u -> u.getRangoPorJuego().containsKey(scrim.getJuego()))
            // Filtrar por rango permitido
            .filter(u -> {
                int mmr = u.getRangoPorJuego().get(scrim.getJuego());
                return mmr >= scrim.getRangoMin() && mmr <= scrim.getRangoMax();
            })
            // Ordenar por cercanía al rango mínimo (jugadores más cercanos primero)
            .sorted(Comparator.comparingInt(u ->
                Math.abs(u.getRangoPorJuego().get(scrim.getJuego()) - scrim.getRangoMin())
            ))
            // Limitar a cupos máximos
            .limit(scrim.getCuposMaximos())
            .collect(Collectors.toList());
    }

    /**
     * Implementación legacy mantenida para backward compatibility
     * @deprecated Use seleccionar() para implementación correcta
     */
    @Override
    @Deprecated
    public void ejecutarEmparejamiento(Scrim scrim) {
        System.out.println("Ejecutando emparejamiento por MMR (legacy)");
        // Usar implementación default de la interface
        IMatchMakingStrategy.super.ejecutarEmparejamiento(scrim);
    }
}
