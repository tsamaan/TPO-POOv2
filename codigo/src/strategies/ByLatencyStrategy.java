package strategies;

import interfaces.IMatchMakingStrategy;
import models.Scrim;
import models.Usuario;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;

/**
 * Estrategia de matchmaking por Latencia (Ping)
 *
 * Criterio de selección:
 * 1. Simula latencia para cada jugador (en producción vendría de servidor)
 * 2. Filtra jugadores con latencia menor al máximo permitido
 * 3. Ordena por menor latencia primero (mejor ping)
 * 4. Selecciona hasta cupos máximos
 *
 * @pattern Strategy (Fixed Implementation)
 */
public class ByLatencyStrategy implements IMatchMakingStrategy {

    private Random random = new Random();

    @Override
    public List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim) {
        System.out.println("[STRATEGY - LATENCY] Filtrando por latencia < " +
                         scrim.getLatenciaMax() + "ms");

        return candidatos.stream()
            // Simular latencia y filtrar (en producción se mediría ping real)
            .filter(u -> {
                int latenciaSimulada = 20 + random.nextInt(100); // 20-120ms
                boolean cumple = latenciaSimulada <= scrim.getLatenciaMax();
                if (!cumple) {
                    System.out.println("   [✗] " + u.getUsername() + " descartado (ping: " + latenciaSimulada + "ms)");
                }
                return cumple;
            })
            // Limitar a cupos máximos
            .limit(scrim.getCuposMaximos())
            .collect(Collectors.toList());
    }
}
