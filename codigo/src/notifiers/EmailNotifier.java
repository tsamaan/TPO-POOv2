package notifiers;

import interfaces.INotifier;
import interfaces.INotificationComponent;
import models.Notificacion;
import models.Usuario;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Notificador de Email (Leaf en patrón COMPOSITE)
 * 
 * Envía emails reales usando el endpoint:
 * https://send-email-zeta.vercel.app/send-email
 * 
 * @pattern Composite - Leaf
 * @pattern Observer - Concrete Observer
 */
public class EmailNotifier implements INotifier, INotificationComponent {

    private static final String EMAIL_ENDPOINT = "https://send-email-zeta.vercel.app/send-email";
    
    @Override
    public void sendNotification(Notificacion notificacion) {
        try {
            // Obtener destinatario
            Usuario destinatario = notificacion.getDestinatario();
            
            if (destinatario == null || destinatario.getEmail() == null) {
                System.out.println("⚠️ [EMAIL] No se puede enviar: destinatario sin email");
                return;
            }
            
            // Enviar email real
            boolean enviado = sendEmail(
                destinatario.getUsername(),
                destinatario.getEmail(),
                notificacion.getTitulo(),
                notificacion.getMensaje()
            );
            
            if (enviado) {
                System.out.println("✅ [EMAIL] Enviado a: " + destinatario.getEmail());
                System.out.println("   Asunto: " + notificacion.getTitulo());
            } else {
                System.out.println("❌ [EMAIL] Error al enviar a: " + destinatario.getEmail());
            }
            
        } catch (Exception e) {
            System.err.println("❌ [EMAIL] Error: " + e.getMessage());
            // Fallback: mostrar en consola
            System.out.println("📧 [EMAIL - FALLBACK] " + notificacion.getMensaje());
        }
    }
    
    /**
     * Envía un email usando el endpoint de Vercel
     * 
     * @param name Nombre del destinatario
     * @param email Email del destinatario
     * @param subject Asunto del email
     * @param message Mensaje del email
     * @return true si se envió exitosamente, false en caso contrario
     */
    private boolean sendEmail(String name, String email, String subject, String message) {
        HttpURLConnection connection = null;
        
        try {
            // 1. Crear conexión
            URL url = new URL(EMAIL_ENDPOINT);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000); // 5 segundos timeout
            connection.setReadTimeout(5000);
            
            // 2. Construir JSON body
            String jsonBody = buildJsonBody(name, email, subject, message);
            
            // 3. Enviar request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // 4. Verificar respuesta
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                return true;
            } else {
                System.err.println("[EMAIL] Response code: " + responseCode);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("[EMAIL] Error al enviar: " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Construye el JSON body para el endpoint
     * 
     * Formato esperado:
     * {
     *   "name": "Nombre",
     *   "email": "email@example.com",
     *   "subject": "Asunto",
     *   "message": "Mensaje"
     * }
     */
    private String buildJsonBody(String name, String email, String subject, String message) {
        // Escapar caracteres especiales en JSON
        String escapedName = escapeJson(name);
        String escapedSubject = escapeJson(subject);
        String escapedMessage = escapeJson(message);
        
        return String.format(
            "{\"name\":\"%s\",\"email\":\"%s\",\"subject\":\"%s\",\"message\":\"%s\"}",
            escapedName,
            email,
            escapedSubject,
            escapedMessage
        );
    }
    
    /**
     * Escapa caracteres especiales para JSON
     */
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        
        return text
            .replace("\\", "\\\\")  // \ -> \\
            .replace("\"", "\\\"")  // " -> \"
            .replace("\n", "\\n")   // newline -> \n
            .replace("\r", "\\r")   // carriage return -> \r
            .replace("\t", "\\t");  // tab -> \t
    }

    @Override
    public String getName() {
        return "EmailNotifier";
    }
}
