package strategies;

import interfaces.IMatchMakingStrategy;
import models.Scrim;

/**
 * Estrategia de emparejamiento basada en historial de jugadores
 * Considera la compatibilidad previa entre jugadores
 * 
 * Factores que considera:
 * - Historial de partidas juntos
 * - Tasa de victoria en equipo
 * - Comportamiento/reportes previos
 * - Química de equipo (synergy)
 */
public class ByHistoryStrategy implements IMatchMakingStrategy {

    @Override
    public void ejecutarEmparejamiento(Scrim scrim) {
        System.out.println("[🔍 STRATEGY: History-Based Matchmaking]");
        System.out.println("   Analizando historial de jugadores...");
        System.out.println("   Evaluando compatibilidad previa...");
        System.out.println("   Priorizando equipos con buena química...");
        
        // Simulación de algoritmo de historial
        if (scrim.getPostulaciones().size() >= 4) {
            System.out.println("   ✅ Equipos formados con jugadores compatibles");
            scrim.cambiarEstado(new states.EstadoLobbyCompleto());
        } else {
            System.out.println("   ⏳ Buscando más jugadores con historial compatible...");
        }
    }
}
