package models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase Confirmacion - Gestiona la confirmación de jugadores para un Scrim
 * Permite confirmar o rechazar la participación
 */
public class Confirmacion {
    private UUID id;
    private Usuario usuario;
    private Scrim scrim;
    private LocalDateTime fechaConfirmacion;
    private EstadoConfirmacion estado;

    // Enum para el estado de confirmación
    public enum EstadoConfirmacion {
        PENDIENTE,
        CONFIRMADO,
        RECHAZADO
    }

    public Confirmacion(Usuario usuario, Scrim scrim) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.scrim = scrim;
        this.fechaConfirmacion = LocalDateTime.now();
        this.estado = EstadoConfirmacion.PENDIENTE;
    }

    public void confirmar() {
        if (estado == EstadoConfirmacion.PENDIENTE) {
            this.estado = EstadoConfirmacion.CONFIRMADO;
            this.fechaConfirmacion = LocalDateTime.now();
            System.out.println("Usuario " + usuario.getUsername() + " confirmó su participación en el Scrim");
        } else {
            System.out.println("Esta confirmación ya fue procesada: " + estado);
        }
    }

    public void rechazar() {
        if (estado == EstadoConfirmacion.PENDIENTE) {
            this.estado = EstadoConfirmacion.RECHAZADO;
            this.fechaConfirmacion = LocalDateTime.now();
            System.out.println("Usuario " + usuario.getUsername() + " rechazó su participación en el Scrim");
        } else {
            System.out.println("Esta confirmación ya fue procesada: " + estado);
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Scrim getScrim() {
        return scrim;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public EstadoConfirmacion getEstado() {
        return estado;
    }

    public boolean isPendiente() {
        return estado == EstadoConfirmacion.PENDIENTE;
    }

    public boolean isConfirmado() {
        return estado == EstadoConfirmacion.CONFIRMADO;
    }

    public boolean isRechazado() {
        return estado == EstadoConfirmacion.RECHAZADO;
    }

    @Override
    public String toString() {
        return "Confirmacion{" +
               "usuario=" + usuario.getUsername() +
               ", estado=" + estado +
               ", fecha=" + fechaConfirmacion +
               '}';
    }
}
