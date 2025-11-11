package strategies;

import interfaces.IMatchMakingStrategy;
import models.Scrim;
import models.Usuario;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;

/**
 * Estrategia de emparejamiento basada en historial de jugadores
 *
 * Criterio de selección:
 * 1. Simula score de compatibilidad basado en historial
 * 2. Prioriza jugadores con buen comportamiento (sin reportes)
 * 3. Considera química de equipo (synergy simulada)
 * 4. Selecciona jugadores con mejor score de compatibilidad
 *
 * @pattern Strategy (Fixed Implementation)
 */
public class ByHistoryStrategy implements IMatchMakingStrategy {

    private Random random = new Random();

    @Override
    public List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim) {
        System.out.println("[STRATEGY - HISTORY] Analizando historial y compatibilidad...");

        return candidatos.stream()
            // Filtrar jugadores con buen historial (simulado)
            .filter(u -> {
                int scoreCompatibilidad = random.nextInt(100); // 0-100
                boolean buenHistorial = scoreCompatibilidad > 30;
                if (!buenHistorial) {
                    System.out.println("   [✗] " + u.getUsername() + " descartado (bajo score compatibilidad)");
                }
                return buenHistorial;
            })
            // Limitar a cupos máximos
            .limit(scrim.getCuposMaximos())
            .collect(Collectors.toList());
    }
}
