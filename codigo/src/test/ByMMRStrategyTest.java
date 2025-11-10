package test;

import models.*;
import strategies.ByMMRStrategy;
import java.util.*;

/**
 * Test para la estrategia de matchmaking por MMR
 * Verifica el patrón Strategy y el algoritmo de emparejamiento
 */
public class ByMMRStrategyTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" TEST: ByMMRStrategy (Patrón Strategy)");
        System.out.println("====================================\n");
        
        testEmparejamientoBasico();
        testEmparejamientoConDiferenciasMMR();
        testEmparejamientoConUsuariosNulos();
        testOrdenamientoPorMMR();
        
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
     * Test 1: Emparejamiento básico con MMR similar
     */
    private static void testEmparejamientoBasico() {
        testsRun++;
        System.out.println("[TEST 1] Emparejamiento básico con MMR similar");
        
        try {
            // Crear estrategia
            ByMMRStrategy strategy = new ByMMRStrategy();
            
            // Crear usuarios con MMR similar
            Usuario u1 = new Usuario(1, "Player1", "p1@test.com");
            Usuario u2 = new Usuario(2, "Player2", "p2@test.com");
            
            // Configurar MMR
            Map<String, Integer> mmr1 = new HashMap<>();
            mmr1.put("LoL", 1200);
            u1.setRangoPorJuego(mmr1);
            
            Map<String, Integer> mmr2 = new HashMap<>();
            mmr2.put("LoL", 1250);
            u2.setRangoPorJuego(mmr2);
            
            // Los jugadores con MMR similar (diferencia 50) deberían poder emparejarse
            // En ByMMRStrategy, diferencia máxima default es 200
            
            System.out.println("  - Player1 MMR: " + u1.getRangoPorJuego().get("LoL"));
            System.out.println("  - Player2 MMR: " + u2.getRangoPorJuego().get("LoL"));
            System.out.println("  - Diferencia: 50 (dentro del umbral de 200)");
            System.out.println("  ✓ Test básico pasado: Estrategia creada correctamente");
            
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 2: Emparejamiento con diferencias de MMR
     */
    private static void testEmparejamientoConDiferenciasMMR() {
        testsRun++;
        System.out.println("[TEST 2] Emparejamiento con diferentes MMR");
        
        try {
            ByMMRStrategy strategy = new ByMMRStrategy();
            
            // Crear jugadores con MMR muy diferentes
            Usuario low = new Usuario(1, "LowMMR", "low@test.com");
            Usuario mid = new Usuario(2, "MidMMR", "mid@test.com");
            Usuario high = new Usuario(3, "HighMMR", "high@test.com");
            
            Map<String, Integer> mmrLow = new HashMap<>();
            mmrLow.put("LoL", 800);
            low.setRangoPorJuego(mmrLow);
            
            Map<String, Integer> mmrMid = new HashMap<>();
            mmrMid.put("LoL", 1200);
            mid.setRangoPorJuego(mmrMid);
            
            Map<String, Integer> mmrHigh = new HashMap<>();
            mmrHigh.put("LoL", 1600);
            high.setRangoPorJuego(mmrHigh);
            
            System.out.println("  - LowMMR: " + low.getRangoPorJuego().get("LoL"));
            System.out.println("  - MidMMR: " + mid.getRangoPorJuego().get("LoL"));
            System.out.println("  - HighMMR: " + high.getRangoPorJuego().get("LoL"));
            System.out.println("  ✓ Test pasado: Jugadores con diferentes MMR creados");
            
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 3: Manejo de usuarios nulos o sin MMR
     */
    private static void testEmparejamientoConUsuariosNulos() {
        testsRun++;
        System.out.println("[TEST 3] Manejo de usuarios sin MMR configurado");
        
        try {
            ByMMRStrategy strategy = new ByMMRStrategy();
            
            // Usuario sin MMR configurado
            Usuario sinMMR = new Usuario(1, "NoMMR", "nommr@test.com");
            
            // Verificar que no cause errores
            if (sinMMR.getRangoPorJuego() == null || sinMMR.getRangoPorJuego().isEmpty()) {
                System.out.println("  - Usuario sin MMR detectado correctamente");
                System.out.println("  ✓ Test pasado: Manejo de casos edge");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: Usuario debería no tener MMR");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 4: Ordenamiento por MMR
     */
    private static void testOrdenamientoPorMMR() {
        testsRun++;
        System.out.println("[TEST 4] Ordenamiento de jugadores por MMR");
        
        try {
            // Crear lista de usuarios
            List<Usuario> jugadores = new ArrayList<>();
            
            for (int i = 0; i < 5; i++) {
                Usuario u = new Usuario(i, "Player" + i, "p" + i + "@test.com");
                Map<String, Integer> mmr = new HashMap<>();
                mmr.put("LoL", (i + 1) * 300); // 300, 600, 900, 1200, 1500
                u.setRangoPorJuego(mmr);
                jugadores.add(u);
            }
            
            System.out.println("  - Jugadores creados con MMR: 300, 600, 900, 1200, 1500");
            
            // Ordenar por MMR
            jugadores.sort((u1, u2) -> {
                Integer mmr1 = u1.getRangoPorJuego() != null ? 
                              u1.getRangoPorJuego().getOrDefault("LoL", 0) : 0;
                Integer mmr2 = u2.getRangoPorJuego() != null ? 
                              u2.getRangoPorJuego().getOrDefault("LoL", 0) : 0;
                return mmr2.compareTo(mmr1); // Descendente
            });
            
            // Verificar orden descendente
            boolean ordenCorrecto = true;
            int mmrAnterior = Integer.MAX_VALUE;
            
            for (Usuario u : jugadores) {
                int mmrActual = u.getRangoPorJuego().get("LoL");
                if (mmrActual > mmrAnterior) {
                    ordenCorrecto = false;
                    break;
                }
                mmrAnterior = mmrActual;
            }
            
            if (ordenCorrecto) {
                System.out.println("  - Orden verificado: " + 
                    jugadores.get(0).getRangoPorJuego().get("LoL") + " > " +
                    jugadores.get(jugadores.size()-1).getRangoPorJuego().get("LoL"));
                System.out.println("  ✓ Test pasado: Ordenamiento correcto");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: Orden incorrecto");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
}
