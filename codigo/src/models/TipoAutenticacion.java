package models;

/**
 * RF1: Tipo de autenticación del usuario
 *
 * Opciones:
 * - LOCAL: Usuario/password en sistema local
 * - STEAM: OAuth con Steam (Adapter pattern)
 * - RIOT: OAuth con Riot Games (Adapter pattern)
 * - DISCORD: OAuth con Discord (Adapter pattern)
 *
 * @pattern Adapter (Authentication Providers)
 */
public enum TipoAutenticacion {
    LOCAL,      // Autenticación local con password
    STEAM,      // OAuth Steam
    RIOT,       // OAuth Riot Games
    DISCORD     // OAuth Discord
}
