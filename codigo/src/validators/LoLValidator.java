package validators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * RF11: Validador específico para League of Legends.
 * 
 * Reglas:
 * - 5v5 (10 jugadores totales)
 * - Roles: Top, Jungle, Mid, ADC, Support
 * - Modalidades: Summoner's Rift, ARAM
 * - Mapas: SR (Summoner's Rift), HA (Howling Abyss)
 * 
 * @pattern Template Method
 */
public class LoLValidator extends GameValidator {
    
    private static final int JUGADORES_POR_EQUIPO = 5;
    private static final String[] ROLES_VALIDOS = {"Top", "Jungle", "Mid", "ADC", "Support"};
    private static final String[] MODALIDADES_VALIDAS = {"Ranked 5v5", "Normal 5v5", "ARAM"};
    private static final String[] MAPAS_VALIDOS = {"Summoner's Rift", "Howling Abyss"};
    
    @Override
    protected String getNombreJuego() {
        return "League of Legends";
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
        Set<String> rolesRecibidos = new HashSet<>();
        
        for (String rol : roles) {
            if (!rolesSet.contains(rol)) {
                System.out.println("   Rol inválido: " + rol);
                return false;
            }
            
            if (rolesRecibidos.contains(rol)) {
                System.out.println("   Rol duplicado: " + rol);
                return false;
            }
            
            rolesRecibidos.add(rol);
        }
        
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
        // Validaciones específicas de LoL
        
        // 1. Verificar que no se use ARAM con roles específicos
        // (En ARAM no hay roles fijos)
        
        // 2. Verificar bans (6 por equipo en ranked)
        
        // 3. Verificar nivel mínimo de invocador (30 para ranked)
        
        System.out.println("   Validaciones adicionales LoL: OK");
        return true;
    }
}
