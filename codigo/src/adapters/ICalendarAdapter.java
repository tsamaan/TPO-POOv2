package adapters;

import models.Scrim;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * RF10: Adapter para exportar Scrims a formato iCalendar (.ics).
 * 
 * Permite sincronizar scrims con calendarios externos:
 * - Google Calendar
 * - Outlook
 * - Apple Calendar
 * 
 * @pattern Adapter
 */
public class ICalendarAdapter {
    
    private static final String ICAL_VERSION = "2.0";
    private static final String PRODID = "-//eScrims Platform//Scrim Scheduler//EN";
    
    /**
     * Convierte un Scrim a formato iCalendar.
     */
    public String toICalendar(Scrim scrim) {
        StringBuilder ical = new StringBuilder();
        
        // Encabezado
        ical.append("BEGIN:VCALENDAR\r\n");
        ical.append("VERSION:").append(ICAL_VERSION).append("\r\n");
        ical.append("PRODID:").append(PRODID).append("\r\n");
        ical.append("CALSCALE:GREGORIAN\r\n");
        ical.append("METHOD:PUBLISH\r\n");
        
        // Evento
        ical.append("BEGIN:VEVENT\r\n");
        ical.append("UID:").append(scrim.getId().toString()).append("@escrims.com\r\n");
        ical.append("DTSTAMP:").append(formatoICalendar(java.time.LocalDateTime.now())).append("\r\n");
        ical.append("DTSTART:").append(formatoICalendar(scrim.getFechaHora())).append("\r\n");
        
        // Duración estimada
        int duracion = scrim.getDuracionEstimada();
        ical.append("DURATION:PT").append(duracion).append("M\r\n");
        
        // Información del evento
        ical.append("SUMMARY:").append(escaparTexto(generarResumen(scrim))).append("\r\n");
        ical.append("DESCRIPTION:").append(escaparTexto(generarDescripcion(scrim))).append("\r\n");
        ical.append("LOCATION:").append("eScrims Platform - ").append(scrim.getJuego()).append("\r\n");
        ical.append("STATUS:").append(convertirEstado(scrim.getEstado().toString())).append("\r\n");
        ical.append("PRIORITY:").append(calcularPrioridad(scrim)).append("\r\n");
        
        // Categorías
        ical.append("CATEGORIES:eSports,").append(scrim.getJuego()).append(",Scrim\r\n");
        
        // Alarma (recordatorio 15 min antes)
        ical.append("BEGIN:VALARM\r\n");
        ical.append("TRIGGER:-PT15M\r\n");
        ical.append("ACTION:DISPLAY\r\n");
        ical.append("DESCRIPTION:Scrim comienza en 15 minutos\r\n");
        ical.append("END:VALARM\r\n");
        
        ical.append("END:VEVENT\r\n");
        ical.append("END:VCALENDAR\r\n");
        
        return ical.toString();
    }
    
    /**
     * Formatea LocalDateTime a formato iCalendar (yyyyMMdd'T'HHmmss'Z').
     */
    private String formatoICalendar(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            dateTime = java.time.LocalDateTime.now();
        }
        
        // Convertir a UTC
        java.time.ZonedDateTime zdt = dateTime.atZone(ZoneId.systemDefault())
                                               .withZoneSameInstant(ZoneId.of("UTC"));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        return zdt.format(formatter);
    }
    
    /**
     * Genera un resumen corto para el evento.
     */
    private String generarResumen(Scrim scrim) {
        return String.format("Scrim %s - %s", 
            scrim.getJuego(), 
            scrim.getModalidad());
    }
    
    /**
     * Genera descripción detallada del scrim.
     */
    private String generarDescripcion(Scrim scrim) {
        StringBuilder desc = new StringBuilder();
        desc.append("Scrim ID: ").append(scrim.getId()).append("\\n");
        desc.append("Juego: ").append(scrim.getJuego()).append("\\n");
        desc.append("Modalidad: ").append(scrim.getModalidad()).append("\\n");
        desc.append("Formato: ").append(scrim.getFormato()).append("\\n");
        desc.append("Rango Min: ").append(scrim.getRangoMinimo()).append("\\n");
        desc.append("Rango Max: ").append(scrim.getRangoMaximo()).append("\\n");
        desc.append("Estado: ").append(scrim.getEstado()).append("\\n");
        desc.append("\\n");
        desc.append("Plataforma eScrims - Matchmaking competitivo");
        
        return desc.toString();
    }
    
    /**
     * Convierte el estado del scrim a estado iCalendar.
     */
    private String convertirEstado(String estado) {
        switch (estado) {
            case "CONFIRMADO":
            case "EN_CURSO":
                return "CONFIRMED";
            case "CANCELADO":
                return "CANCELLED";
            case "FINALIZADO":
                return "CONFIRMED";
            default:
                return "TENTATIVE";
        }
    }
    
    /**
     * Calcula la prioridad del evento (1-9, siendo 1 la más alta).
     */
    private int calcularPrioridad(Scrim scrim) {
        // Prioridad basada en el estado del scrim
        String estado = scrim.getEstado().toString();
        
        if (estado.contains("CONFIRMADO")) return 1; // Alta prioridad
        if (estado.contains("EN_CURSO")) return 2; // Muy alta
        if (estado.contains("BUSCANDO")) return 5; // Media
        return 7; // Baja prioridad
    }
    
    /**
     * Escapa caracteres especiales para iCalendar.
     */
    private String escaparTexto(String texto) {
        return texto.replace("\\", "\\\\")
                   .replace(";", "\\;")
                   .replace(",", "\\,")
                   .replace("\n", "\\n");
    }
    
    /**
     * Guarda el iCalendar en un archivo .ics
     */
    public void guardarArchivo(Scrim scrim, String rutaArchivo) {
        String contenido = toICalendar(scrim);
        
        try {
            java.nio.file.Files.write(
                java.nio.file.Paths.get(rutaArchivo),
                contenido.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            System.out.println("📅 Calendario exportado: " + rutaArchivo);
        } catch (Exception e) {
            System.err.println("❌ Error al guardar calendario: " + e.getMessage());
        }
    }
}
