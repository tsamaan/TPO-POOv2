package moderators;

import models.ReporteConducta;

/**
 * RF9: Patrón Chain of Responsibility para procesar reportes de conducta.
 * 
 * Cadena:
 * 1. AutoResolverHandler: Casos simples (BAJA severidad, reincidente claro)
 * 2. BotModeradorHandler: IA analiza evidencia (MEDIA severidad)
 * 3. ModeradorHumanoHandler: Casos complejos (ALTA/CRITICA severidad)
 * 
 * @pattern Chain of Responsibility
 */
public abstract class ModerationHandler {
    
    protected ModerationHandler siguiente;
    
    /**
     * Establece el siguiente handler en la cadena.
     */
    public void setSiguiente(ModerationHandler handler) {
        this.siguiente = handler;
    }
    
    /**
     * Procesa el reporte. Si no puede manejarlo, lo pasa al siguiente.
     */
    public abstract void procesar(ReporteConducta reporte);
    
    /**
     * Pasa el reporte al siguiente handler en la cadena.
     */
    protected void pasarAlSiguiente(ReporteConducta reporte) {
        if (siguiente != null) {
            siguiente.procesar(reporte);
        } else {
            // Fin de la cadena sin resolver
            System.err.println("⚠️ Reporte sin resolver: " + reporte.getId());
        }
    }
}
