package models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo de dominio que representa una notificación a enviar a un usuario.
 * Según diagrama UML del proyecto TPO-POOv2.
 */
public class Notificacion {
    // ============ ATRIBUTOS DEL DIAGRAMA ============
    private UUID id;                          // Identificador único
    private Usuario destinatario;             // A quién se le envía
    private TipoNotificacion tipo;            // Tipo de notificación (enum)
    private String mensaje;                   // Cuerpo del mensaje
    private LocalDateTime fechaEnvio;         // Cuándo se envió
    private boolean leida;                    // ¿Usuario la leyó?
    
    // ============ ENUM PARA TIPO DE NOTIFICACION ============
    public enum TipoNotificacion {
        SCRIM_CREADO,           // Cuando se crea un scrim que coincide con preferencias
        LOBBY_COMPLETO,         // 10/10 jugadores
        CONFIRMADO,             // Todos confirmaron
        EN_JUEGO,               // Partida iniciada
        FINALIZADO,             // Partida terminada
        CANCELADO,              // Scrim cancelado
        RECORDATORIO,           // Recordatorio pre-partida
        JUGADOR_REEMPLAZADO,    // Un jugador fue reemplazado
        APLICACION_ACEPTADA,    // Postulación aceptada
        APLICACION_RECHAZADA    // Postulación rechazada
    }
    
    // ============ CONSTRUCTORES ============
    
    /**
     * Constructor completo
     */
    public Notificacion(TipoNotificacion tipo, String mensaje, Usuario destinatario) {
        this.id = UUID.randomUUID();
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.leida = false;
    }
    
    /**
     * Constructor simple (compatible con código existente)
     */
    public Notificacion(String mensaje) {
        this.id = UUID.randomUUID();
        this.tipo = TipoNotificacion.SCRIM_CREADO; // default
        this.mensaje = mensaje;
        this.leida = false;
    }
    
    // ============ MÉTODOS DEL DIAGRAMA ============
    
    /**
     * Marca la notificación como leída por el usuario
     */
    public void marcarComoLeida() {
        this.leida = true;
    }
    
    /**
     * Obtiene el contenido formateado de la notificación
     */
    public String obtenerContenido() {
        return String.format("[%s] %s", tipo.name(), mensaje);
    }
    
    // ============ GETTERS ============
    public UUID getId() {
        return id;
    }
    
    public Usuario getDestinatario() {
        return destinatario;
    }
    
    public TipoNotificacion getTipo() {
        return tipo;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }
    
    public boolean isLeida() {
        return leida;
    }
    
    // ============ SETTERS ============
    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
    }
    
    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
    
    public void setLeida(boolean leida) {
        this.leida = leida;
    }
    
    // ============ toString ============
    @Override
    public String toString() {
        return String.format("Notificacion[%s] %s - %s [%s]", 
            id.toString().substring(0, 8), 
            tipo, 
            mensaje.substring(0, Math.min(30, mensaje.length())),
            leida ? "✓ Leída" : "○ No leída");
    }
}
