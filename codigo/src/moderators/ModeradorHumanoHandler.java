package moderators;

import models.ReporteConducta;
import models.ReporteConducta.TipoReporte;

/**
 * RF9: Tercer handler - Moderador humano (último recurso).
 * 
 * Criterios:
 * - Severidad ALTA o CRITICA
 * - Tipos: TRAMPA, casos complejos elevados por bot
 * 
 * Acción:
 * - Revisión manual
 * - Ban temporal (ALTA) o permanente (CRITICA)
 * 
 * @pattern Chain of Responsibility
 */
public class ModeradorHumanoHandler extends ModerationHandler {
    
    private String moderadorId;
    
    public ModeradorHumanoHandler(String moderadorId) {
        this.moderadorId = moderadorId;
    }
    
    @Override
    public void procesar(ReporteConducta reporte) {
        // Este es el último handler, procesa TODO lo que llega
        reporte.marcarEnRevision();
        
        System.out.println("👤 [MODERADOR HUMANO] Reporte " + reporte.getId() + " requiere revisión manual");
        System.out.println("   Tipo: " + reporte.getTipo());
        System.out.println("   Severidad: " + reporte.getSeveridad());
        System.out.println("   Descripción: " + reporte.getDescripcion());
        
        // Simular decisión humana
        String resolucion = tomarDecision(reporte);
        boolean rechazar = resolucion.startsWith("RECHAZADO");
        
        reporte.resolver(moderadorId, resolucion, rechazar);
        
        System.out.println("   Decisión: " + resolucion);
    }
    
    /**
     * Simula la toma de decisión de un moderador humano.
     * En producción esto sería manual a través de un panel de administración.
     */
    private String tomarDecision(ReporteConducta reporte) {
        switch (reporte.getSeveridad()) {
            case CRITICA:
                if (reporte.getTipo() == TipoReporte.TRAMPA) {
                    return "BAN PERMANENTE: Uso de cheats/hacks confirmado. Cuenta cerrada.";
                }
                return "BAN de 30 días: Conducta extremadamente grave. Revisión de apelación en 7 días.";
                
            case ALTA:
                return "BAN de 7 días: Conducta grave confirmada. Reincidencia resultará en ban permanente.";
                
            case MEDIA:
                // Casos elevados por el bot
                if (reporte.getEvidencia() == null) {
                    return "RECHAZADO: Evidencia insuficiente. Se requiere prueba para proceder.";
                }
                return "Cooldown de 24 horas: Caso elevado confirmado tras revisión manual.";
                
            case BAJA:
                // Casos excepcionales que llegaron hasta aquí
                if (Math.random() < 0.3) {
                    return "RECHAZADO: Reporte infundado. Warning al reportante por mal uso del sistema.";
                }
                return "Warning final: Última advertencia antes de sanción mayor.";
                
            default:
                return "RECHAZADO: No se puede determinar la acción apropiada.";
        }
    }
}
