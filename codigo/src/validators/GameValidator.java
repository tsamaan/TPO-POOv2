package validators;

/**
 * RF11: Template Method para validación de reglas específicas por juego.
 * 
 * Define el esqueleto del algoritmo de validación:
 * 1. Validar número de jugadores
 * 2. Validar roles requeridos
 * 3. Validar restricciones del juego
 * 4. Validar configuración de partida
 * 
 * Subclases implementan reglas específicas (LoL, Valorant, etc.)
 * 
 * @pattern Template Method
 */
public abstract class GameValidator {
    
    /**
     * Template Method: Define el flujo de validación.
     * No debe ser sobrescrito por subclases.
     */
    public final boolean validarScrim(int numJugadores, String[] roles, 
                                       String modalidad, String mapa) {
        System.out.println("\n🔍 Validando Scrim para " + getNombreJuego() + "...");
        
        // Paso 1: Validar número de jugadores
        if (!validarNumeroJugadores(numJugadores)) {
            System.out.println("❌ Número de jugadores inválido");
            return false;
        }
        System.out.println("✅ Número de jugadores: OK");
        
        // Paso 2: Validar roles
        if (!validarRoles(roles)) {
            System.out.println("❌ Roles inválidos");
            return false;
        }
        System.out.println("✅ Roles: OK");
        
        // Paso 3: Validar modalidad
        if (!validarModalidad(modalidad)) {
            System.out.println("❌ Modalidad no soportada");
            return false;
        }
        System.out.println("✅ Modalidad: OK");
        
        // Paso 4: Validar mapa (opcional)
        if (mapa != null && !mapa.isEmpty()) {
            if (!validarMapa(mapa)) {
                System.out.println("❌ Mapa inválido");
                return false;
            }
            System.out.println("✅ Mapa: OK");
        }
        
        // Paso 5: Validaciones adicionales específicas del juego
        if (!validacionesAdicionales()) {
            System.out.println("❌ Validaciones adicionales fallaron");
            return false;
        }
        System.out.println("✅ Validaciones adicionales: OK");
        
        System.out.println("✅ Scrim válido para " + getNombreJuego());
        return true;
    }
    
    // Métodos abstractos (DEBEN ser implementados por subclases)
    
    /**
     * Retorna el nombre del juego.
     */
    protected abstract String getNombreJuego();
    
    /**
     * Valida que el número de jugadores sea correcto.
     */
    protected abstract boolean validarNumeroJugadores(int numJugadores);
    
    /**
     * Valida que los roles sean válidos para el juego.
     */
    protected abstract boolean validarRoles(String[] roles);
    
    /**
     * Valida que la modalidad sea soportada.
     */
    protected abstract boolean validarModalidad(String modalidad);
    
    /**
     * Valida que el mapa sea válido.
     */
    protected abstract boolean validarMapa(String mapa);
    
    // Métodos hook (PUEDEN ser sobrescritos por subclases)
    
    /**
     * Hook: Validaciones adicionales específicas del juego.
     * Por defecto retorna true.
     */
    protected boolean validacionesAdicionales() {
        return true;
    }
}
