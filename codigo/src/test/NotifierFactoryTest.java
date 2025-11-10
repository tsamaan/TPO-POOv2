package test;

import notifiers.*;
import interfaces.INotifier;
import models.*;

/**
 * Test para NotifierFactory
 * Verifica el patrón Abstract Factory
 */
public class NotifierFactoryTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" TEST: NotifierFactory (Patrón Abstract Factory)");
        System.out.println("====================================\n");
        
        testCreacionEmailNotifier();
        testCreacionDiscordNotifier();
        testCreacionPushNotifier();
        testEnvioNotificacion();
        
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
     * Test 1: Creación de EmailNotifier
     */
    private static void testCreacionEmailNotifier() {
        testsRun++;
        System.out.println("[TEST 1] Creación de EmailNotifier");
        
        try {
            SimpleNotifierFactory factory = new SimpleNotifierFactory();
            INotifier emailNotifier = factory.createEmailNotifier();
            
            if (emailNotifier != null && emailNotifier instanceof EmailNotifier) {
                System.out.println("  - Tipo: " + emailNotifier.getClass().getSimpleName());
                System.out.println("  - Interface implementada: INotifier");
                System.out.println("  ✓ Test pasado: EmailNotifier creado correctamente");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: EmailNotifier no creado");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 2: Creación de DiscordNotifier
     */
    private static void testCreacionDiscordNotifier() {
        testsRun++;
        System.out.println("[TEST 2] Creación de DiscordNotifier");
        
        try {
            SimpleNotifierFactory factory = new SimpleNotifierFactory();
            INotifier discordNotifier = factory.createDiscordNotifier();
            
            if (discordNotifier != null && discordNotifier instanceof DiscordNotifier) {
                System.out.println("  - Tipo: " + discordNotifier.getClass().getSimpleName());
                System.out.println("  - Interface implementada: INotifier");
                System.out.println("  ✓ Test pasado: DiscordNotifier creado correctamente");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: DiscordNotifier no creado");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 3: Creación de PushNotifier
     */
    private static void testCreacionPushNotifier() {
        testsRun++;
        System.out.println("[TEST 3] Creación de PushNotifier");
        
        try {
            SimpleNotifierFactory factory = new SimpleNotifierFactory();
            INotifier pushNotifier = factory.createPushNotifier();
            
            if (pushNotifier != null && pushNotifier instanceof PushNotifier) {
                System.out.println("  - Tipo: " + pushNotifier.getClass().getSimpleName());
                System.out.println("  - Interface implementada: INotifier");
                System.out.println("  ✓ Test pasado: PushNotifier creado correctamente");
                testsPassed++;
            } else {
                System.out.println("  ✗ Test falló: PushNotifier no creado");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 4: Envío de notificación a través del notifier
     */
    private static void testEnvioNotificacion() {
        testsRun++;
        System.out.println("[TEST 4] Envío de notificación");
        
        try {
            SimpleNotifierFactory factory = new SimpleNotifierFactory();
            INotifier notifier = factory.createEmailNotifier();
            
            // Crear usuario y notificación de prueba
            Usuario usuario = new Usuario(1, "TestUser", "test@example.com");
            Notificacion notificacion = new Notificacion(
                Notificacion.TipoNotificacion.SCRIM_CREADO,
                "Mensaje de prueba para testing",
                usuario
            );
            
            // Enviar notificación (esto debería imprimir en consola)
            System.out.println("  - Enviando notificación de prueba...");
            notifier.sendNotification(notificacion);
            
            System.out.println("  ✓ Test pasado: Notificación enviada sin errores");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
}
