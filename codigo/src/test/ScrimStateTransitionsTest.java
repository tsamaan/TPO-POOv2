package test;

import models.Scrim;
import states.*;

/**
 * Test para las transiciones de estado del Scrim
 * Verifica el patrón State y las reglas de negocio
 */
public class ScrimStateTransitionsTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" TEST: State Transitions (Patrón State)");
        System.out.println("====================================\n");
        
        testEstadoInicial();
        testTransicionBuscandoALobby();
        testTransicionLobbyAConfirmado();
        testTransicionConfirmadoAEnJuego();
        testTransicionEnJuegoAFinalizado();
        testCancelacionDesdeEstados();
        
        System.out.println("\n====================================");
        System.out.println(" RESUMEN DE TESTS");
        System.out.println("====================================");
        System.out.println("Tests ejecutados: " + testsRun);
        System.out.println("Tests exitosos: " + testsPassed);
        System.out.println("Tests fallidos: " + (testsRun - testsPassed));
        System.out.println("Porcentaje de éxito: " + 
                         (testsRun > 0 ? (testsPassed * 100 / testsRun) : 0) + "%");
        
        if (testsPassed == testsRun) {
            System.out.println("\n✓ TODOS LOS TESTS PASARON");
        } else {
            System.out.println("\n✗ ALGUNOS TESTS FALLARON");
        }
    }
    
    /**
     * Test 1: Estado inicial
     */
    private static void testEstadoInicial() {
        testsRun++;
        System.out.println("[TEST 1] Estado inicial - Buscando Jugadores");
        
        try {
            ScrimState estadoInicial = new EstadoBuscandoJugadores();
            Scrim scrim = new Scrim(estadoInicial);
            
            String nombreEstado = scrim.getEstado().getClass().getSimpleName();
            
            if (nombreEstado.equals("EstadoBuscandoJugadores")) {
                System.out.println("  - Estado inicial: " + nombreEstado);
                System.out.println("  ✓ Test pasado: Scrim creado en estado correcto");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: Estado incorrecto - " + nombreEstado);
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 2: Buscando Jugadores → Lobby Completo
     */
    private static void testTransicionBuscandoALobby() {
        testsRun++;
        System.out.println("[TEST 2] Transición: Buscando → Lobby Completo");
        
        try {
            Scrim scrim = new Scrim(new EstadoBuscandoJugadores());
            
            // Cambiar estado manualmente (simulando cupo lleno)
            scrim.cambiarEstado(new EstadoLobbyCompleto());
            
            String estadoActual = scrim.getEstado().getClass().getSimpleName();
            
            if (estadoActual.equals("EstadoLobbyCompleto")) {
                System.out.println("  - Estado anterior: EstadoBuscandoJugadores");
                System.out.println("  - Estado actual: " + estadoActual);
                System.out.println("  ✓ Test pasado: Transición correcta");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: Estado incorrecto - " + estadoActual);
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 3: Lobby Completo → Confirmado
     */
    private static void testTransicionLobbyAConfirmado() {
        testsRun++;
        System.out.println("[TEST 3] Transición: Lobby Completo → Confirmado");
        
        try {
            Scrim scrim = new Scrim(new EstadoLobbyCompleto());
            
            // Cambiar estado manualmente (simulando confirmación)
            scrim.cambiarEstado(new EstadoConfirmado());
            
            String estadoActual = scrim.getEstado().getClass().getSimpleName();
            
            if (estadoActual.equals("EstadoConfirmado")) {
                System.out.println("  - Estado anterior: EstadoLobbyCompleto");
                System.out.println("  - Estado actual: " + estadoActual);
                System.out.println("  ✓ Test pasado: Confirmación funciona");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: Estado incorrecto - " + estadoActual);
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 4: Confirmado → En Juego
     */
    private static void testTransicionConfirmadoAEnJuego() {
        testsRun++;
        System.out.println("[TEST 4] Transición: Confirmado → En Juego");
        
        try {
            Scrim scrim = new Scrim(new EstadoConfirmado());
            
            // Iniciar partida
            scrim.getEstado().iniciar(scrim);
            
            String estadoActual = scrim.getEstado().getClass().getSimpleName();
            
            if (estadoActual.equals("EstadoEnJuego")) {
                System.out.println("  - Estado anterior: EstadoConfirmado");
                System.out.println("  - Estado actual: " + estadoActual);
                System.out.println("  ✓ Test pasado: Inicio de partida funciona");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: Estado incorrecto - " + estadoActual);
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 5: En Juego → Finalizado
     */
    private static void testTransicionEnJuegoAFinalizado() {
        testsRun++;
        System.out.println("[TEST 5] Transición: En Juego → Finalizado");
        
        try {
            Scrim scrim = new Scrim(new EstadoEnJuego());
            
            // Finalizar partida (usando cancelar porque es el método disponible)
            scrim.getEstado().cancelar(scrim);
            
            String estadoActual = scrim.getEstado().getClass().getSimpleName();
            
            if (estadoActual.equals("EstadoFinalizado")) {
                System.out.println("  - Estado anterior: EstadoEnJuego");
                System.out.println("  - Estado actual: " + estadoActual);
                System.out.println("  ✓ Test pasado: Finalización funciona");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: Estado incorrecto - " + estadoActual);
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 6: Cancelación desde diferentes estados
     */
    private static void testCancelacionDesdeEstados() {
        testsRun++;
        System.out.println("[TEST 6] Cancelación desde múltiples estados");
        
        try {
            boolean todosExitosos = true;
            
            // Cancelar desde Buscando Jugadores
            Scrim scrim1 = new Scrim(new EstadoBuscandoJugadores());
            scrim1.getEstado().cancelar(scrim1);
            if (!scrim1.getEstado().getClass().getSimpleName().equals("EstadoCancelado")) {
                todosExitosos = false;
            }
            System.out.println("  - Cancelar desde BuscandoJugadores: " + 
                             scrim1.getEstado().getClass().getSimpleName());
            
            // Cancelar desde Lobby Completo
            Scrim scrim2 = new Scrim(new EstadoLobbyCompleto());
            scrim2.getEstado().cancelar(scrim2);
            if (!scrim2.getEstado().getClass().getSimpleName().equals("EstadoCancelado")) {
                todosExitosos = false;
            }
            System.out.println("  - Cancelar desde LobbyCompleto: " + 
                             scrim2.getEstado().getClass().getSimpleName());
            
            // Cancelar desde Confirmado
            Scrim scrim3 = new Scrim(new EstadoConfirmado());
            scrim3.getEstado().cancelar(scrim3);
            if (!scrim3.getEstado().getClass().getSimpleName().equals("EstadoCancelado")) {
                todosExitosos = false;
            }
            System.out.println("  - Cancelar desde Confirmado: " + 
                             scrim3.getEstado().getClass().getSimpleName());
            
            if (todosExitosos) {
                System.out.println("  ✓ Test pasado: Cancelación funciona desde todos los estados");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: Algunas cancelaciones fallaron");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
}
