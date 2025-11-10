package moderators;

import models.ReporteConducta;
import models.ReporteConducta.SeveridadReporte;

/**
 * RF9: Segundo handler - Bot moderador con IA.
 * 
 * Criterios:
 * - Severidad MEDIA
 * - Tipos: LENGUAJE_OFENSIVO, ABANDONO_INJUSTIFICADO
 * 
 * Acción:
 * - Analiza evidencia (simulado)
 * - Aplica cooldown temporal (2-24 horas)
 * 
 * @pattern Chain of Responsibility
 */
public class BotModeradorHandler extends ModerationHandler {
    
    @Override
    public void procesar(ReporteConducta reporte) {
        // Solo procesa severidad MEDIA
        if (reporte.getSeveridad() == SeveridadReporte.MEDIA) {
            reporte.marcarEnRevision();
            
            // Simular análisis de IA
            boolean evidenciaValida = analizarEvidencia(reporte);
            
            if (evidenciaValida) {
                String resolucion = aplicarSancionMedia(reporte);
                reporte.resolver("BOT_MODERADOR_IA", resolucion, false);
                
                System.out.println("🤖 [BOT MODERADOR] Reporte " + reporte.getId() + " procesado por IA");
                System.out.println("   Tipo: " + reporte.getTipo());
                System.out.println("   Evidencia: " + (reporte.getEvidencia() != null ? "Analizada" : "No disponible"));
                System.out.println("   Acción: " + resolucion);
                return;
            } else {
                // Evidencia no concluyente, elevar a humano
                System.out.println("🤖 [BOT MODERADOR] Evidencia no concluyente, elevando a moderador humano...");
                pasarAlSiguiente(reporte);
            }
        } else {
            // No es severidad MEDIA, pasar al siguiente
            pasarAlSiguiente(reporte);
        }
    }
    
    /**
     * Simula análisis de evidencia por IA.
     * En producción analizaría:
     * - Screenshots (OCR para detectar lenguaje ofensivo)
     * - Videos (abandonos en tiempo real)
     * - Historial del jugador
     */
    private boolean analizarEvidencia(ReporteConducta reporte) {
        // Simular análisis
        if (reporte.getEvidencia() != null && !reporte.getEvidencia().isEmpty()) {
            // Si hay evidencia, 80% de probabilidad de ser válida
            return Math.random() < 0.8;
        }
        
        // Sin evidencia, solo 40% válido (basado en historial)
        return Math.random() < 0.4;
    }
    
    private String aplicarSancionMedia(ReporteConducta reporte) {
        switch (reporte.getTipo()) {
            case LENGUAJE_OFENSIVO:
                return "Cooldown de 12 horas por lenguaje ofensivo. Evidencia confirmada por IA.";
            case ABANDONO_INJUSTIFICADO:
                return "Cooldown de 6 horas por abandono. Strike +1. Total: 2/3 strikes.";
            case COMPORTAMIENTO_ANTISPORTIVO:
                return "Cooldown de 4 horas por conducta antisportiva repetida.";
            default:
                return "Cooldown de 2 horas aplicado.";
        }
    }
}
