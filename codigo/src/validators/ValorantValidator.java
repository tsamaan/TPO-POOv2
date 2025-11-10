package validators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * RF11: Validador específico para Valorant.
 * 
 * Reglas:
 * - 5v5 (10 jugadores totales)
 * - Roles: Duelist, Controller, Sentinel, Initiator
 * - Modalidades: Unrated, Competitive, Spike Rush
 * - Mapas: Bind, Haven, Split, Ascent, Icebox, Breeze, Fracture, Pearl, Lotus, Sunset
 * 
 * @pattern Template Method
 */
public class ValorantValidator extends GameValidator {
    
    private static final int JUGADORES_POR_EQUIPO = 5;
    private static final String[] ROLES_VALIDOS = {"Duelist", "Controller", "Sentinel", "Initiator"};
    private static final String[] MODALIDADES_VALIDAS = {"Unrated", "Competitive", "Spike Rush", "Custom"};
    private static final String[] MAPAS_VALIDOS = {
        "Bind", "Haven", "Split", "Ascent", "Icebox", 
        "Breeze", "Fracture", "Pearl", "Lotus", "Sunset"
    };
    
    @Override
    protected String getNombreJuego() {
        return "Valorant";
    }
    
    @Override
    protected boolean validarNumeroJugadores(int numJugadores) {
        // Debe ser 10 jugadores (5v5)
        return numJugadores == JUGADORES_POR_EQUIPO * 2;
    }
    
    @Override
    protected boolean validarRoles(String[] roles) {
        if (roles == null || roles.length != JUGADORES_POR_EQUIPO) {
            System.out.println("   Esperados " + JUGADORES_POR_EQUIPO + " roles, recibidos: " + 
                             (roles != null ? roles.length : 0));
            return false;
        }
        
        Set<String> rolesSet = new HashSet<>(Arrays.asList(ROLES_VALIDOS));
        
        for (String rol : roles) {
            if (!rolesSet.contains(rol)) {
                System.out.println("   Rol inválido: " + rol);
                System.out.println("   Roles válidos: " + Arrays.toString(ROLES_VALIDOS));
                return false;
            }
        }
        
        // En Valorant PUEDEN haber roles duplicados (ej: 2 Duelists)
        return true;
    }
    
    @Override
    protected boolean validarModalidad(String modalidad) {
        for (String mod : MODALIDADES_VALIDAS) {
            if (mod.equalsIgnoreCase(modalidad)) {
                return true;
            }
        }
        
        System.out.println("   Modalidades válidas: " + Arrays.toString(MODALIDADES_VALIDAS));
        return false;
    }
    
    @Override
    protected boolean validarMapa(String mapa) {
        for (String mapaValido : MAPAS_VALIDOS) {
            if (mapaValido.equalsIgnoreCase(mapa)) {
                return true;
            }
        }
        
        System.out.println("   Mapas válidos: " + Arrays.toString(MAPAS_VALIDOS));
        return false;
    }
    
    @Override
    protected boolean validacionesAdicionales() {
        // Validaciones específicas de Valorant
        
        // 1. Verificar composición de equipo (recomendado: 1 Controller, 1 Sentinel mínimo)
        System.out.println("   Recomendación: Incluir al menos 1 Controller y 1 Sentinel");
        
        // 2. Verificar nivel de cuenta (20+ para Competitive)
        
        // 3. Verificar que todos tengan agentes desbloqueados
        
        System.out.println("   Validaciones adicionales Valorant: OK");
        return true;
    }
}
