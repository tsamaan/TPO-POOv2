package moderators;

import models.ReporteConducta;
import models.ReporteConducta.SeveridadReporte;
import models.ReporteConducta.TipoReporte;

/**
 * RF9: Primer handler - Auto-resuelve casos simples.
 * 
 * Criterios:
 * - Severidad BAJA
 * - Tipos: SPAM, COMPORTAMIENTO_ANTISPORTIVO leve
 * 
 * Acción: Warning automático al reportado
 * 
 * @pattern Chain of Responsibility
 */
public class AutoResolverHandler extends ModerationHandler {
    
    @Override
    public void procesar(ReporteConducta reporte) {
        // Solo procesa severidad BAJA
        if (reporte.getSeveridad() == SeveridadReporte.BAJA) {
            // Casos que podemos auto-resolver
            if (esAutoResolvible(reporte)) {
                String resolucion = generarResolucionAutomatica(reporte);
                reporte.marcarAutoResuelto(resolucion);
                
                System.out.println("✅ [AUTO-RESOLVER] Reporte " + reporte.getId() + " resuelto automáticamente");
                System.out.println("   Tipo: " + reporte.getTipo());
                System.out.println("   Acción: " + resolucion);
                
                // Aquí se podría integrar con NotificationService para avisar al reportado
                return;
            }
        }
        
        // No podemos manejar este caso, pasamos al siguiente
        pasarAlSiguiente(reporte);
    }
    
    private boolean esAutoResolvible(ReporteConducta reporte) {
        // Solo SPAM y comportamiento antisportivo leve
        return reporte.getTipo() == TipoReporte.SPAM || 
               reporte.getTipo() == TipoReporte.COMPORTAMIENTO_ANTISPORTIVO;
    }
    
    private String generarResolucionAutomatica(ReporteConducta reporte) {
        switch (reporte.getTipo()) {
            case SPAM:
                return "Warning automático: Evitar spam en chat. Reincidencia resultará en cooldown.";
            case COMPORTAMIENTO_ANTISPORTIVO:
                return "Warning automático: Mantener conducta deportiva. Siguiente infracción será sancionada.";
            default:
                return "Warning automático emitido.";
        }
    }
}
