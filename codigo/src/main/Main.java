package main;

import models.*;
import states.*;
import notifiers.*;
import strategies.*;
import service.*;
import auth.*;
import context.ScrimContext;
import interfaces.INotifier;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== eScrims - Plataforma de Scrims para eSports ===\n");

        // 1. Patrón ADAPTER - Autenticación con múltiples proveedores
        System.out.println("1. PATRON ADAPTER - Autenticación");
        AuthService authService = new AuthService();
        AuthController authController = new AuthController(authService);
        
        Usuario user1 = authController.login("jugador1@test.com", "pass123");
        Usuario user2 = authController.loginWithProvider("google", "google-token");
        System.out.println("Usuario autenticado: " + user1.getUsername());
        System.out.println("Usuario Google: " + user2.getUsername() + "\n");

        // 2. Patrón ABSTRACT FACTORY - Creación de notificadores
        System.out.println("2. PATRON ABSTRACT FACTORY - Notificadores");
        NotifierFactory factory = new SimpleNotifierFactory();
        INotifier emailNotifier = factory.createEmailNotifier();
        INotifier discordNotifier = factory.createDiscordNotifier();
        INotifier pushNotifier = factory.createPushNotifier();
        System.out.println("Notificadores creados via factory\n");

        // 3. Patrón STATE - Gestión de estados del Scrim
        System.out.println("3. PATRON STATE - Estados del Scrim");
        ScrimState estadoInicial = new EstadoBuscandoJugadores();
        Scrim scrim = new Scrim(estadoInicial);
        
        // Agregar notificadores al scrim (Observer implícito)
        scrim.addNotifier(emailNotifier);
        scrim.addNotifier(discordNotifier);
        scrim.addNotifier(pushNotifier);

        ScrimContext context = new ScrimContext(scrim, estadoInicial);
        
        // Simular postulaciones
        Usuario user3 = new Usuario(3, "Player3", "p3@test.com");
        Usuario user4 = new Usuario(4, "Player4", "p4@test.com");
        
        context.postular(user1, "Support");
        context.postular(user2, "ADC");
        context.postular(user3, "Mid");
        context.postular(user4, "Jungle");
        System.out.println("Postulaciones agregadas\n");

        // 4. Patrón STRATEGY - Matchmaking
        System.out.println("4. PATRON STRATEGY - Matchmaking");
        MatchmakingService mmService = new MatchmakingService(new ByMMRStrategy());
        mmService.ejecutarEmparejamiento(scrim);
        System.out.println("Estado actual: " + scrim.getEstado().getClass().getSimpleName() + "\n");

        // Cambiar estrategia
        mmService = new MatchmakingService(new ByLatencyStrategy());
        mmService.ejecutarEmparejamiento(scrim);
        System.out.println("Matchmaking con estrategia de latencia completado\n");

        // Transiciones de estado
        System.out.println("5. Transiciones de Estado");
        scrim.getEstado().iniciar(scrim);
        System.out.println("Estado después de iniciar: " + scrim.getEstado().getClass().getSimpleName());
        
        scrim.getEstado().iniciar(scrim);
        System.out.println("Estado después de segundo iniciar: " + scrim.getEstado().getClass().getSimpleName());
        
        scrim.getEstado().cancelar(scrim);
        System.out.println("Estado después de cancelar: " + scrim.getEstado().getClass().getSimpleName() + "\n");

        // 6. Nuevas clases del modelo de dominio
        System.out.println("6. MODELO DE DOMINIO - Equipos, Confirmaciones y Estadísticas");
        
        // Crear equipos
        Equipo equipo1 = new Equipo("Equipo Azul");
        Equipo equipo2 = new Equipo("Equipo Rojo");
        
        equipo1.asignarJugador(user1);
        equipo1.asignarJugador(user2);
        equipo2.asignarJugador(user3);
        equipo2.asignarJugador(user4);
        
        System.out.println(equipo1);
        System.out.println(equipo2);
        System.out.println();
        
        // Crear confirmaciones
        Confirmacion conf1 = new Confirmacion(user1, scrim);
        Confirmacion conf2 = new Confirmacion(user2, scrim);
        Confirmacion conf3 = new Confirmacion(user3, scrim);
        
        conf1.confirmar();
        conf2.confirmar();
        conf3.rechazar();
        
        System.out.println(conf1);
        System.out.println(conf2);
        System.out.println(conf3);
        System.out.println();
        
        // Crear estadísticas
        Estadistica stats1 = new Estadistica(user1, scrim, 12, 3, 8);
        Estadistica stats2 = new Estadistica(user2, scrim, 5, 10, 2);
        Estadistica stats3 = new Estadistica(user3, scrim, 15, 2, 10);
        
        System.out.println(stats1);
        System.out.println("Rendimiento: " + stats1.obtenerRendimiento());
        System.out.println();
        System.out.println(stats2);
        System.out.println("Rendimiento: " + stats2.obtenerRendimiento());
        System.out.println();
        System.out.println(stats3);
        System.out.println("Rendimiento: " + stats3.obtenerRendimiento());
        System.out.println();

        System.out.println("=== Demo completada - Todos los patrones y clases del diagrama implementados ===");
    }
}
