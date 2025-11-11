package test;

import models.Notificacion;
import models.Notificacion.TipoNotificacion;
import models.Usuario;
import notifiers.EmailNotifier;

/**
 * Clase de prueba para EmailNotifier
 * 
 * Envía un email de prueba real usando el endpoint de Vercel.
 * 
 * IMPORTANTE: Cambiar el email de destino antes de ejecutar.
 */
public class EmailNotifierTest {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   TEST: EmailNotifier con Endpoint Real");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // 1. Crear usuario de prueba (CAMBIAR EMAIL AQUÍ)
        Usuario destinatario = new Usuario(
            1,
            "Teo",
            "teosp2004@gmail.com"  // ← CAMBIAR POR TU EMAIL
        );
        
        // 2. Crear notificación de prueba
        Notificacion notificacion = new Notificacion(
            TipoNotificacion.SCRIM_CREADO,
            "Se ha creado un nuevo scrim de Valorant.\n\n" +
            "Detalles:\n" +
            "- Juego: Valorant\n" +
            "- Rango: 1500-1700 MMR\n" +
            "- Fecha: Hoy 20:00 hs\n" +
            "- Jugadores: 8/10\n\n" +
            "¡Postúlate ahora!",
            destinatario
        );
        
        // 3. Crear EmailNotifier
        EmailNotifier emailNotifier = new EmailNotifier();
        
        // 4. Enviar email
        System.out.println("🚀 Enviando email...\n");
        emailNotifier.sendNotification(notificacion);
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("   Test completado");
        System.out.println("   Verifica tu bandeja de entrada");
        System.out.println("═══════════════════════════════════════════════════════");
    }
}
