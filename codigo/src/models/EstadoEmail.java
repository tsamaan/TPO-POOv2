package models;

/**
 * RF1: Estado de verificación del email del usuario
 *
 * Flujo:
 * - PENDIENTE: Email registrado pero no verificado (se envió email)
 * - VERIFICADO: Usuario hizo click en link de verificación
 *
 * @pattern State (Email Verification State)
 */
public enum EstadoEmail {
    PENDIENTE,      // Email no verificado
    VERIFICADO      // Email verificado
}
