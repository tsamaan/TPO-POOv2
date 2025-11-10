package models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RF9: Representa un reporte de mala conducta en un Scrim.
 * 
 * Estados:
 * - PENDIENTE: Recién creado, esperando revisión
 * - EN_REVISION: Siendo analizado por moderador
 * - RESUELTO: Acción tomada
 * - RECHAZADO: Sin fundamento
 * 
 * @pattern Chain of Responsibility: Este objeto es procesado por una cadena de handlers
 */
public class ReporteConducta {
    
    public enum TipoReporte {
        LENGUAJE_OFENSIVO,
        ABANDONO_INJUSTIFICADO,
        TRAMPA,
        COMPORTAMIENTO_ANTISPORTIVO,
        SPAM,
        OTRO
    }
    
    public enum SeveridadReporte {
        BAJA,    // Warning
        MEDIA,   // Cooldown temporal
        ALTA,    // Ban temporal
        CRITICA  // Ban permanente
    }
    
    public enum EstadoReporte {
        PENDIENTE,
        EN_REVISION,
        RESUELTO,
        RECHAZADO
    }
    
    private String id;
    private String reportanteId;
    private String reportadoId;
    private String scrimId;
    private TipoReporte tipo;
    private SeveridadReporte severidad;
    private EstadoReporte estado;
    private String descripcion;
    private String evidencia; // URL a screenshot/video
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;
    private String moderadorId; // Quien resolvió
    private String resolucion; // Decisión tomada
    private boolean autoResuelto; // Si fue resuelto automáticamente
    
    public ReporteConducta(String reportanteId, String reportadoId, 
                          String scrimId, TipoReporte tipo, String descripcion) {
        this.id = UUID.randomUUID().toString();
        this.reportanteId = reportanteId;
        this.reportadoId = reportadoId;
        this.scrimId = scrimId;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.estado = EstadoReporte.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.autoResuelto = false;
        
        // Auto-asignar severidad según tipo
        this.severidad = calcularSeveridadInicial();
    }
    
    private SeveridadReporte calcularSeveridadInicial() {
        switch (tipo) {
            case TRAMPA:
                return SeveridadReporte.CRITICA;
            case ABANDONO_INJUSTIFICADO:
            case LENGUAJE_OFENSIVO:
                return SeveridadReporte.MEDIA;
            case COMPORTAMIENTO_ANTISPORTIVO:
            case SPAM:
                return SeveridadReporte.BAJA;
            default:
                return SeveridadReporte.BAJA;
        }
    }
    
    public void resolver(String moderadorId, String resolucion, boolean rechazado) {
        this.estado = rechazado ? EstadoReporte.RECHAZADO : EstadoReporte.RESUELTO;
        this.moderadorId = moderadorId;
        this.resolucion = resolucion;
        this.fechaResolucion = LocalDateTime.now();
    }
    
    public void marcarEnRevision() {
        this.estado = EstadoReporte.EN_REVISION;
    }
    
    public void marcarAutoResuelto(String resolucion) {
        this.estado = EstadoReporte.RESUELTO;
        this.resolucion = resolucion;
        this.fechaResolucion = LocalDateTime.now();
        this.autoResuelto = true;
        this.moderadorId = "SISTEMA";
    }
    
    // Getters
    public String getId() { return id; }
    public String getReportanteId() { return reportanteId; }
    public String getReportadoId() { return reportadoId; }
    public String getScrimId() { return scrimId; }
    public TipoReporte getTipo() { return tipo; }
    public SeveridadReporte getSeveridad() { return severidad; }
    public EstadoReporte getEstado() { return estado; }
    public String getDescripcion() { return descripcion; }
    public String getEvidencia() { return evidencia; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public String getModeradorId() { return moderadorId; }
    public String getResolucion() { return resolucion; }
    public boolean isAutoResuelto() { return autoResuelto; }
    
    // Setters
    public void setEvidencia(String evidencia) { this.evidencia = evidencia; }
    public void setSeveridad(SeveridadReporte severidad) { this.severidad = severidad; }
    
    @Override
    public String toString() {
        return String.format("Reporte[%s] %s → %s | %s | %s | Estado: %s",
            id.substring(0, 8), reportanteId, reportadoId, tipo, severidad, estado);
    }
}
