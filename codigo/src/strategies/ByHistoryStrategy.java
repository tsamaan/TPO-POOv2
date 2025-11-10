package strategies;

import interfaces.IMatchMakingStrategy;
import models.Postulacion;
import models.Scrim;
import models.Usuario;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Estrategia de emparejamiento por historial/compatibilidad
 * RF5: Prioriza jugadores con buen comportamiento, alta sinergia y fair play
 * 
 * Criterios:
 * - Bajo número de abandonos
 * - Rating alto
 * - Sin sanciones activas (cooldown)
 * - Historial positivo
 */
public class ByHistoryStrategy implements IMatchMakingStrategy {
    
    @Override
    public void ejecutarEmparejamiento(Scrim scrim) {
        System.out.println("\n[*] Aplicando estrategia de emparejamiento por HISTORIAL/COMPATIBILIDAD");
        System.out.println("    Criterios: Comportamiento, rating, sinergia de roles, fair play");
        
        List<Postulacion> postulaciones = scrim.getPostulaciones();
        
        if (postulaciones.isEmpty()) {
            System.out.println("    [!] No hay postulaciones para evaluar");
            return;
        }
        
        // Ordenar por score de compatibilidad (mayor a menor)
        List<Postulacion> ordenadas = postulaciones.stream()
            .sorted((p1, p2) -> {
                int score1 = calcularScoreCompatibilidad(p1.getUsuario());
                int score2 = calcularScoreCompatibilidad(p2.getUsuario());
                return Integer.compare(score2, score1); // Descendente
            })
            .collect(Collectors.toList());
        
        // Mostrar resultados del emparejamiento
        System.out.println("\n    [+] Jugadores ordenados por compatibilidad:");
        for (int i = 0; i < ordenadas.size(); i++) {
            Usuario usuario = ordenadas.get(i).getUsuario();
            int score = calcularScoreCompatibilidad(usuario);
            String estado = usuario.estaSancionado() ? "⚠️ SANCIONADO" : "✓ Activo";
            
            System.out.println(String.format("    [%d] %s - Score: %d | Rating: %.1f | Abandonos: %d | Strikes: %d | %s",
                i + 1,
                usuario.getUsername(),
                score,
                usuario.getRating(),
                usuario.getAbandonos(),
                usuario.getStrikes(),
                estado
            ));
        }
        
        // Actualizar orden en el scrim
        scrim.getPostulaciones().clear();
        scrim.getPostulaciones().addAll(ordenadas);
        
        System.out.println("\n    [+] Emparejamiento por historial completado");
        System.out.println("    [*] Los jugadores con mejor comportamiento tienen prioridad");
    }
    
    /**
     * Calcula un score de compatibilidad basado en el historial del jugador
     * Score más alto = Mejor jugador
     * 
     * Fórmula:
     * - Base: 100 puntos
     * - Rating: +10 puntos por cada punto de rating
     * - Abandonos: -50 puntos por cada abandono
     * - Strikes: -30 puntos por cada strike
     * - Sancionado: -200 puntos si está en cooldown
     * - Partidas jugadas: +1 punto por cada 10 partidas (experiencia)
     */
    private int calcularScoreCompatibilidad(Usuario usuario) {
        int score = 100; // Base
        
        // Premiar buen rating (0-10)
        score += (int)(usuario.getRating() * 10);
        
        // Penalizar abandonos (cada abandono resta 50 puntos)
        score -= usuario.getAbandonos() * 50;
        
        // Penalizar strikes (cada strike resta 30 puntos)
        score -= usuario.getStrikes() * 30;
        
        // Penalizar fuertemente si está sancionado
        if (usuario.estaSancionado()) {
            score -= 200;
        }
        
        // Premiar experiencia (partidas jugadas)
        score += usuario.getTotalPartidas() / 10;
        
        // Asegurar que el score no sea negativo
        return Math.max(0, score);
    }
    
    /**
     * Verifica si un jugador cumple con los requisitos mínimos de compatibilidad
     * @param usuario Usuario a verificar
     * @return true si cumple requisitos mínimos
     */
    public boolean cumpleRequisitosMinimosFairPlay(Usuario usuario) {
        // Requisitos mínimos:
        // - No estar sancionado
        // - Máximo 2 strikes
        // - Máximo 3 abandonos
        // - Rating mínimo de 3.0
        
        if (usuario.estaSancionado()) {
            return false;
        }
        
        if (usuario.getStrikes() > 2) {
            return false;
        }
        
        if (usuario.getAbandonos() > 3) {
            return false;
        }
        
        if (usuario.getRating() < 3.0 && usuario.getTotalPartidas() > 5) {
            return false; // Solo si tiene suficientes partidas para tener rating significativo
        }
        
        return true;
    }
    
    /**
     * Calcula la sinergia entre dos jugadores basándose en sus roles preferidos
     * (Implementación simplificada - en producción sería más complejo)
     */
    public int calcularSinergia(Usuario u1, Usuario u2) {
        int sinergia = 0;
        
        // Si comparten roles preferidos, tienen experiencia jugando juntos
        if (u1.getRolesPreferidos() != null && u2.getRolesPreferidos() != null) {
            for (String rol1 : u1.getRolesPreferidos()) {
                for (String rol2 : u2.getRolesPreferidos()) {
                    if (rol1.equals(rol2)) {
                        sinergia += 10; // Bonus por compartir rol
                    }
                }
            }
        }
        
        // Si tienen ratings similares (diferencia < 2), mejor sinergia
        double difRating = Math.abs(u1.getRating() - u2.getRating());
        if (difRating < 2.0) {
            sinergia += 20;
        }
        
        return sinergia;
    }
}
