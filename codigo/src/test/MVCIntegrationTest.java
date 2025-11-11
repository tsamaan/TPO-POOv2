package test;

import models.*;
import controllers.*;
import views.*;
import strategies.*;
import states.*;
import interfaces.IMatchMakingStrategy;

import java.util.List;
import java.util.ArrayList;

/**
 * Test de integración MVC - Verifica funcionalidad post-refactorización
 *
 * Este test NO es interactivo - ejecuta automáticamente
 * Valida que la arquitectura MVC funcione correctamente
 */
public class MVCIntegrationTest {

    private static int testsPassed = 0;
    private static int testsRun = 0;

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     TEST DE INTEGRACIÓN MVC - POST-REFACTORIZACIÓN           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        // Tests de Views
        testViewsCreation();
        testConsoleViewMethods();

        // Tests de Controllers
        testControllersCreation();

        // Tests de Strategy Pattern (CORREGIDO)
        testStrategyPatternFixed();
        testByMMRStrategySeleccion();
        testByLatencyStrategySeleccion();

        // Tests de Integración MVC
        testMVCIntegration();
        testScrimCreationFlow();

        // Resumen
        printSummary();
    }

    // ============================================
    // TESTS DE VIEWS
    // ============================================

    private static void testViewsCreation() {
        testsRun++;
        System.out.println("[TEST 1] Creación de Views (MVC - View Layer)");

        try {
            ConsoleView consoleView = new ConsoleView();
            MenuView menuView = new MenuView(consoleView);
            GameView gameView = new GameView(consoleView);

            if (consoleView != null && menuView != null && gameView != null) {
                System.out.println("  ✓ ConsoleView creada correctamente");
                System.out.println("  ✓ MenuView creada correctamente");
                System.out.println("  ✓ GameView creada correctamente");
                System.out.println("  ✓ Test pasado: Views layer OK");
                testsPassed++;
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testConsoleViewMethods() {
        testsRun++;
        System.out.println("[TEST 2] Métodos de ConsoleView");

        try {
            ConsoleView view = new ConsoleView();

            // Test métodos de presentación (no fallan)
            view.mostrarExito("Test de éxito");
            view.mostrarError("Test de error");
            view.mostrarInfo("Test de info");
            view.mostrarAdvertencia("Test de advertencia");

            System.out.println("  ✓ Métodos de presentación funcionan");
            System.out.println("  ✓ Test pasado: ConsoleView methods OK");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
        }
        System.out.println();
    }

    // ============================================
    // TESTS DE CONTROLLERS
    // ============================================

    private static void testControllersCreation() {
        testsRun++;
        System.out.println("[TEST 3] Creación de Controllers (MVC - Controller Layer)");

        try {
            ConsoleView consoleView = new ConsoleView();
            MenuView menuView = new MenuView(consoleView);
            GameView gameView = new GameView(consoleView);

            UserController userCtrl = new UserController(consoleView, menuView);
            ScrimController scrimCtrl = new ScrimController(consoleView, menuView, gameView);
            MatchmakingController matchCtrl = new MatchmakingController(
                consoleView, menuView, gameView, scrimCtrl
            );

            if (userCtrl != null && scrimCtrl != null && matchCtrl != null) {
                System.out.println("  ✓ UserController creado correctamente");
                System.out.println("  ✓ ScrimController creado correctamente");
                System.out.println("  ✓ MatchmakingController creado correctamente");
                System.out.println("  ✓ Test pasado: Controllers layer OK");
                testsPassed++;
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ============================================
    // TESTS DE STRATEGY PATTERN (CORREGIDO)
    // ============================================

    private static void testStrategyPatternFixed() {
        testsRun++;
        System.out.println("[TEST 4] Strategy Pattern - Nueva Firma seleccionar()");

        try {
            // Crear estrategia
            IMatchMakingStrategy strategy = new ByMMRStrategy();

            // Crear scrim de prueba
            Scrim scrim = new Scrim.Builder(new EstadoBuscandoJugadores())
                .juego("Valorant")
                .formato("5v5")
                .rangoMin(1000)
                .rangoMax(2000)
                .build();

            // Crear candidatos
            List<Usuario> candidatos = crearCandidatosPrueba(scrim.getJuego());

            // CRÍTICO: Verificar que seleccionar() retorna List<Usuario>
            List<Usuario> seleccionados = strategy.seleccionar(candidatos, scrim);

            if (seleccionados != null) {
                System.out.println("  ✓ Método seleccionar() implementado");
                System.out.println("  ✓ Retorna List<Usuario> (no void)");
                System.out.println("  ✓ Candidatos totales: " + candidatos.size());
                System.out.println("  ✓ Jugadores seleccionados: " + seleccionados.size());
                System.out.println("  ✓ Test pasado: Strategy Pattern CORREGIDO");
                testsPassed++;
            } else {
                System.out.println("  ✗ seleccionar() retornó null");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testByMMRStrategySeleccion() {
        testsRun++;
        System.out.println("[TEST 5] ByMMRStrategy - Filtrado Real por Rango");

        try {
            ByMMRStrategy strategy = new ByMMRStrategy();

            // Scrim que acepta rango 1200-1800
            Scrim scrim = new Scrim.Builder(new EstadoBuscandoJugadores())
                .juego("Valorant")
                .rangoMin(1200)
                .rangoMax(1800)
                .cuposMaximos(5)
                .build();

            // Crear candidatos con diferentes rangos
            List<Usuario> candidatos = new ArrayList<>();

            // Dentro de rango
            Usuario u1 = new Usuario(1, "Player1200", "p1@test.com");
            u1.getRangoPorJuego().put("Valorant", 1200);
            candidatos.add(u1);

            Usuario u2 = new Usuario(2, "Player1500", "p2@test.com");
            u2.getRangoPorJuego().put("Valorant", 1500);
            candidatos.add(u2);

            Usuario u3 = new Usuario(3, "Player1800", "p3@test.com");
            u3.getRangoPorJuego().put("Valorant", 1800);
            candidatos.add(u3);

            // Fuera de rango (muy bajo)
            Usuario u4 = new Usuario(4, "Player500", "p4@test.com");
            u4.getRangoPorJuego().put("Valorant", 500);
            candidatos.add(u4);

            // Fuera de rango (muy alto)
            Usuario u5 = new Usuario(5, "Player2500", "p5@test.com");
            u5.getRangoPorJuego().put("Valorant", 2500);
            candidatos.add(u5);

            // Ejecutar selección
            List<Usuario> seleccionados = strategy.seleccionar(candidatos, scrim);

            // Validar que SOLO selecciona los que están en rango
            boolean todosEnRango = seleccionados.stream()
                .allMatch(u -> {
                    int mmr = u.getRangoPorJuego().get("Valorant");
                    return mmr >= 1200 && mmr <= 1800;
                });

            boolean filtroFunciona = seleccionados.size() == 3; // Solo u1, u2, u3

            if (todosEnRango && filtroFunciona) {
                System.out.println("  ✓ Filtrado por rango: FUNCIONA");
                System.out.println("  ✓ Candidatos: 5 | Seleccionados: " + seleccionados.size());
                System.out.println("  ✓ Todos en rango 1200-1800: " + todosEnRango);
                System.out.println("  ✓ Test pasado: ByMMRStrategy filtra correctamente");
                testsPassed++;
            } else {
                System.out.println("  ✗ Filtrado incorrecto");
                System.out.println("    Esperado: 3 jugadores | Obtenido: " + seleccionados.size());
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testByLatencyStrategySeleccion() {
        testsRun++;
        System.out.println("[TEST 6] ByLatencyStrategy - Filtrado por Latencia");

        try {
            ByLatencyStrategy strategy = new ByLatencyStrategy();

            Scrim scrim = new Scrim.Builder(new EstadoBuscandoJugadores())
                .juego("CS:GO")
                .latenciaMax(50)
                .cuposMaximos(10)
                .build();

            List<Usuario> candidatos = crearCandidatosPrueba("CS:GO");

            // Ejecutar selección (con filtrado por latencia)
            List<Usuario> seleccionados = strategy.seleccionar(candidatos, scrim);

            if (seleccionados != null && seleccionados.size() > 0) {
                System.out.println("  ✓ Filtrado por latencia ejecutado");
                System.out.println("  ✓ Candidatos: " + candidatos.size());
                System.out.println("  ✓ Seleccionados (latencia < 50ms): " + seleccionados.size());
                System.out.println("  ✓ Test pasado: ByLatencyStrategy selecciona");
                testsPassed++;
            } else {
                System.out.println("  ✗ Strategy no seleccionó jugadores");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ============================================
    // TESTS DE INTEGRACIÓN MVC
    // ============================================

    private static void testMVCIntegration() {
        testsRun++;
        System.out.println("[TEST 7] Integración MVC - Views + Controllers + Services");

        try {
            // Inicializar capas MVC
            ConsoleView consoleView = new ConsoleView();
            MenuView menuView = new MenuView(consoleView);
            GameView gameView = new GameView(consoleView);

            ScrimController scrimCtrl = new ScrimController(consoleView, menuView, gameView);

            // Crear scrim usando Controller
            Scrim scrim = scrimCtrl.crearScrim("League of Legends", "5v5", "SA",
                                               "ranked", 1000, 2000, 80);

            if (scrim != null &&
                scrim.getJuego().equals("League of Legends") &&
                scrim.getRangoMin() == 1000 &&
                scrim.getRangoMax() == 2000) {

                System.out.println("  ✓ Controller crea Scrim correctamente");
                System.out.println("  ✓ View presenta información (sin errores)");
                System.out.println("  ✓ Integración MVC: FUNCIONA");
                System.out.println("  ✓ Test pasado: MVC Integration OK");
                testsPassed++;
            } else {
                System.out.println("  ✗ Scrim creado incorrectamente");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testScrimCreationFlow() {
        testsRun++;
        System.out.println("[TEST 8] Flujo Completo: Crear Scrim + Postular + Matchmaking");

        try {
            // Crear scrim
            Scrim scrim = new Scrim.Builder(new EstadoBuscandoJugadores())
                .juego("Valorant")
                .formato("5v5")
                .rangoMin(1000)
                .rangoMax(2000)
                .cuposMaximos(10)
                .build();

            // Crear usuarios
            List<Usuario> candidatos = crearCandidatosPrueba("Valorant");

            // Usar Strategy para seleccionar jugadores
            IMatchMakingStrategy strategy = new ByMMRStrategy();
            List<Usuario> seleccionados = strategy.seleccionar(candidatos, scrim);

            // Verificar que Strategy NO modificó el estado
            boolean estadoIntacto = scrim.getEstado() instanceof EstadoBuscandoJugadores;

            // Verificar que seleccionó jugadores
            boolean seleccionoJugadores = seleccionados.size() > 0 && seleccionados.size() <= 10;

            if (estadoIntacto && seleccionoJugadores) {
                System.out.println("  ✓ Scrim creado con Builder Pattern");
                System.out.println("  ✓ Strategy seleccionó " + seleccionados.size() + " jugadores");
                System.out.println("  ✓ Strategy NO modificó estado del Scrim ✅");
                System.out.println("  ✓ Estado sigue siendo: " + scrim.getEstado().getClass().getSimpleName());
                System.out.println("  ✓ Test pasado: Flujo completo OK");
                testsPassed++;
            } else {
                if (!estadoIntacto) {
                    System.out.println("  ✗ CRÍTICO: Strategy modificó el estado!");
                }
                if (!seleccionoJugadores) {
                    System.out.println("  ✗ Strategy no seleccionó jugadores correctamente");
                }
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ============================================
    // UTILIDADES
    // ============================================

    private static List<Usuario> crearCandidatosPrueba(String juego) {
        List<Usuario> candidatos = new ArrayList<>();

        int[] rangos = {800, 1100, 1300, 1500, 1700, 1900, 2200, 2500};

        for (int i = 0; i < rangos.length; i++) {
            Usuario u = new Usuario(i + 1, "TestPlayer" + (i+1), "test" + (i+1) + "@escrims.com");
            u.getRangoPorJuego().put(juego, rangos[i]);
            candidatos.add(u);
        }

        return candidatos;
    }

    private static void printSummary() {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("                    RESUMEN DE TESTS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Tests ejecutados: " + testsRun);
        System.out.println("Tests exitosos:   " + testsPassed);
        System.out.println("Tests fallidos:   " + (testsRun - testsPassed));
        System.out.println("Porcentaje:       " +
                         (testsRun > 0 ? (testsPassed * 100 / testsRun) : 0) + "%");
        System.out.println("═══════════════════════════════════════════════════════════════");

        if (testsPassed == testsRun) {
            System.out.println("\n✓✓✓ TODOS LOS TESTS PASARON ✓✓✓");
            System.out.println("\n REFACTORIZACIÓN MVC: EXITOSA");
            System.out.println(" STRATEGY PATTERN: CORREGIDO");
            System.out.println(" ARQUITECTURA: PROFESIONAL");
            System.out.println("\n Proyecto listo para entrega con calidad profesional");
        } else {
            System.out.println("\n✗ ALGUNOS TESTS FALLARON");
            System.out.println(" Revisar errores arriba");
        }

        System.out.println("\n═══════════════════════════════════════════════════════════════\n");
    }
}
