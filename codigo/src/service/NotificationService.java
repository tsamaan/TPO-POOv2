package service;

import models.*;
import notifiers.*;
import interfaces.INotifier;
import java.util.List;
import java.util.ArrayList;

/**
 * Servicio centralizado para enviar notificaciones a usuarios
 * Implementa el patrón Observer para notificar eventos del scrim
 * RF7: Notificaciones (Observer + Abstract Factory)
 */
public class NotificationService {
    
    private SimpleNotifierFactory notifierFactory;
    private List<INotifier> notifiers;
    
    public NotificationService() {
        this.notifierFactory = new SimpleNotifierFactory();
        this.notifiers = new ArrayList<>();
        
        // Inicializar todos los canales de notificación (Composite pattern manual)
        this.notifiers.add(notifierFactory.createEmailNotifier());
        this.notifiers.add(notifierFactory.createDiscordNotifier());
        this.notifiers.add(notifierFactory.createPushNotifier());
    }
    
    /**
     * Notifica a todos los jugadores sobre un evento del scrim
     * @param jugadores Lista de usuarios a notificar
     * @param tipo Tipo de notificación
     * @param mensaje Mensaje a enviar
     */
    public void notificarJugadores(List<Usuario> jugadores, String tipo, String mensaje) {
        for (Usuario jugador : jugadores) {
            enviarNotificacion(jugador, tipo, mensaje);
        }
    }
    
    /**
     * Envía una notificación individual a un usuario
     * @param usuario Usuario destinatario
     * @param tipoStr Tipo de notificación como String
     * @param mensaje Contenido del mensaje
     */
    public void enviarNotificacion(Usuario usuario, String tipoStr, String mensaje) {
        // Convertir String a TipoNotificacion
        Notificacion.TipoNotificacion tipo;
        try {
            tipo = Notificacion.TipoNotificacion.valueOf(tipoStr);
        } catch (IllegalArgumentException e) {
            tipo = Notificacion.TipoNotificacion.SCRIM_CREADO; // default
        }
        
        // Crear la notificación (DTO) - constructor: TipoNotificacion, mensaje, destinatario
        Notificacion notificacion = new Notificacion(tipo, mensaje, usuario);
        
        // Agregar a la lista de notificaciones del usuario
        usuario.agregarNotificacion(notificacion);
        
        // Enviar por todos los canales disponibles (Composite pattern)
        for (INotifier notifier : notifiers) {
            notifier.sendNotification(notificacion);
        }
    }
    
    /**
     * RF7.i: Scrim creado que coincide con preferencias
     */
    public void notificarScrimCreado(List<Usuario> interesados, Scrim scrim) {
        String mensaje = String.format(
            "[eScrims] ¡Nuevo scrim disponible! %s %s en %s - Rango: %s a %s",
            scrim.getJuego() != null ? scrim.getJuego() : "LoL",
            scrim.getFormato() != null ? scrim.getFormato() : "5v5",
            scrim.getRegion() != null ? scrim.getRegion() : "LAS",
            scrim.getRangoMinimo() != null ? scrim.getRangoMinimo() : "Cualquiera",
            scrim.getRangoMaximo() != null ? scrim.getRangoMaximo() : "Cualquiera"
        );
        notificarJugadores(interesados, "SCRIM_CREADO", mensaje);
    }
    
    /**
     * RF7.ii: Cambio a Lobby armado (cupo completo)
     */
    public void notificarLobbyCompleto(List<Usuario> jugadores, Scrim scrim) {
        String mensaje = String.format(
            "[eScrims] ¡Lobby completo! 10/10 jugadores listos. Por favor confirma tu participación."
        );
        notificarJugadores(jugadores, "LOBBY_COMPLETO", mensaje);
    }
    
    /**
     * RF7.iii: Confirmado por todos
     */
    public void notificarTodosConfirmaron(List<Usuario> jugadores, Scrim scrim) {
        String mensaje = "[eScrims] ¡Todos confirmaron! La partida comenzará pronto.";
        notificarJugadores(jugadores, "SCRIM_CONFIRMADO", mensaje);
    }
    
    /**
     * RF7.iv: Cambio a En juego
     */
    public void notificarEnJuego(List<Usuario> jugadores, Scrim scrim) {
        String mensaje = "[eScrims] ¡La partida ha comenzado! ¡Buena suerte!";
        notificarJugadores(jugadores, "SCRIM_INICIADO", mensaje);
    }
    
    /**
     * RF7.iv: Cambio a Finalizado
     */
    public void notificarFinalizado(List<Usuario> jugadores, Scrim scrim) {
        String mensaje = "[eScrims] Partida finalizada. Por favor, carga tus estadísticas y valora a tus compañeros.";
        notificarJugadores(jugadores, "SCRIM_FINALIZADO", mensaje);
    }
    
    /**
     * RF7.iv: Cambio a Cancelado
     */
    public void notificarCancelado(List<Usuario> jugadores, Scrim scrim, String razon) {
        String mensaje = String.format(
            "[eScrims] Scrim cancelado: %s. Serás notificado cuando encuentres otra partida.",
            razon
        );
        notificarJugadores(jugadores, "SCRIM_CANCELADO", mensaje);
    }
    
    /**
     * Notificación de abandono (RF9)
     */
    public void notificarAbandono(Usuario jugador, int strikes) {
        String mensaje = String.format(
            "[eScrims] Has abandonado una partida. Strikes: %d/3. " +
            (strikes >= 3 ? "Estás temporalmente sancionado." : ""),
            strikes
        );
        enviarNotificacion(jugador, "ABANDONO_JUGADOR", mensaje);
    }
    
    /**
     * Recordatorio antes de la partida (RF10)
     */
    public void notificarRecordatorio(List<Usuario> jugadores, Scrim scrim, int minutosAntes) {
        String mensaje = String.format(
            "[eScrims] Recordatorio: Tu partida comienza en %d minutos. ¡Prepárate!",
            minutosAntes
        );
        notificarJugadores(jugadores, "RECORDATORIO", mensaje);
    }
}
