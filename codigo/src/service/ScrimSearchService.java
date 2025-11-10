package service;

import models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de búsqueda de scrims
 * RF2: Búsqueda de scrims con filtros
 */
public class ScrimSearchService {
    
    // Base de datos simulada de scrims disponibles
    private List<Scrim> scrimsDisponibles;
    
    public ScrimSearchService() {
        this.scrimsDisponibles = new ArrayList<>();
    }
    
    /**
     * Registra un scrim en el sistema para que sea encontrable
     */
    public void registrarScrim(Scrim scrim) {
        scrimsDisponibles.add(scrim);
    }
    
    /**
     * RF2: Búsqueda de scrims con filtros múltiples
     * @param juego Filtro por juego (null para cualquiera)
     * @param formato Filtro por formato (null para cualquiera)
     * @param rangoMin Filtro por rango mínimo (null para cualquiera)
     * @param rangoMax Filtro por rango máximo (null para cualquiera)
     * @param region Filtro por región (null para cualquiera)
     * @return Lista de scrims que coinciden con los filtros
     */
    public List<Scrim> buscarScrims(String juego, String formato, 
                                     String rangoMin, String rangoMax, 
                                     String region) {
        return scrimsDisponibles.stream()
            .filter(s -> juego == null || juego.equalsIgnoreCase(s.getJuego()))
            .filter(s -> formato == null || formato.equalsIgnoreCase(s.getFormato()))
            .filter(s -> rangoMin == null || cumpleRangoMinimo(s, rangoMin))
            .filter(s -> rangoMax == null || cumpleRangoMaximo(s, rangoMax))
            .filter(s -> region == null || region.equalsIgnoreCase(s.getRegion()))
            .collect(Collectors.toList());
    }
    
    /**
     * RF2: Búsqueda simple por juego
     */
    public List<Scrim> buscarPorJuego(String juego) {
        return buscarScrims(juego, null, null, null, null);
    }
    
    /**
     * RF2: Búsqueda por región
     */
    public List<Scrim> buscarPorRegion(String region) {
        return buscarScrims(null, null, null, null, region);
    }
    
    /**
     * RF2: Búsqueda por formato (5v5, 3v3, 1v1)
     */
    public List<Scrim> buscarPorFormato(String formato) {
        return buscarScrims(null, formato, null, null, null);
    }
    
    /**
     * RF2: Búsqueda por latencia máxima
     */
    public List<Scrim> buscarPorLatencia(int latenciaMax) {
        return scrimsDisponibles.stream()
            .filter(s -> s.getLatenciaMaxima() <= latenciaMax)
            .collect(Collectors.toList());
    }
    
    /**
     * RF2: Búsqueda que coincida con las preferencias del usuario
     */
    public List<Scrim> buscarCoincidencias(Usuario usuario) {
        return scrimsDisponibles.stream()
            .filter(s -> s.cumpleRequisitos(usuario))
            .collect(Collectors.toList());
    }
    
    /**
     * RF2: Guardar búsqueda favorita (serializada como String)
     */
    public String guardarBusquedaFavorita(String juego, String formato, String region) {
        return String.format("juego:%s,formato:%s,region:%s", 
                           juego != null ? juego : "*",
                           formato != null ? formato : "*",
                           region != null ? region : "*");
    }
    
    /**
     * RF2: Ejecutar búsqueda desde favorita
     */
    public List<Scrim> buscarDesdeFavorita(String busquedaFavorita, Usuario usuario) {
        // Parse del string: "juego:LoL,formato:5v5,region:LAS"
        String[] partes = busquedaFavorita.split(",");
        String juego = null, formato = null, region = null;
        
        for (String parte : partes) {
            String[] kv = parte.split(":");
            if (kv.length == 2) {
                String valor = kv[1].equals("*") ? null : kv[1];
                switch (kv[0]) {
                    case "juego": juego = valor; break;
                    case "formato": formato = valor; break;
                    case "region": region = valor; break;
                }
            }
        }
        
        List<Scrim> resultados = buscarScrims(juego, formato, null, null, region);
        
        // RF2: Si hay coincidencias, notificar al usuario
        if (!resultados.isEmpty()) {
            System.out.println("[!] Se encontraron " + resultados.size() + 
                             " scrims que coinciden con tu búsqueda favorita!");
        }
        
        return resultados;
    }
    
    /**
     * Obtiene todos los scrims disponibles
     */
    public List<Scrim> obtenerTodos() {
        return new ArrayList<>(scrimsDisponibles);
    }
    
    /**
     * Elimina un scrim de la lista (cuando se llena o cancela)
     */
    public void eliminarScrim(Scrim scrim) {
        scrimsDisponibles.remove(scrim);
    }
    
    // ============ MÉTODOS AUXILIARES ============
    
    private boolean cumpleRangoMinimo(Scrim scrim, String rangoMin) {
        // Comparación simplificada de rangos
        // En producción esto sería más sofisticado
        if (scrim.getRangoMinimo() == null) return true;
        return compararRangos(scrim.getRangoMinimo(), rangoMin) >= 0;
    }
    
    private boolean cumpleRangoMaximo(Scrim scrim, String rangoMax) {
        // Comparación simplificada de rangos
        if (scrim.getRangoMaximo() == null) return true;
        return compararRangos(scrim.getRangoMaximo(), rangoMax) <= 0;
    }
    
    /**
     * Compara dos rangos (simplificado)
     * @return negativo si rango1 < rango2, 0 si iguales, positivo si rango1 > rango2
     */
    private int compararRangos(String rango1, String rango2) {
        // Orden simplificado para LoL
        String[] ordenRangos = {"Iron", "Bronze", "Silver", "Gold", "Platinum", 
                               "Diamond", "Master", "Grandmaster", "Challenger"};
        
        int pos1 = buscarPosicion(ordenRangos, rango1);
        int pos2 = buscarPosicion(ordenRangos, rango2);
        
        return Integer.compare(pos1, pos2);
    }
    
    private int buscarPosicion(String[] array, String valor) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(valor)) {
                return i;
            }
        }
        return -1; // No encontrado
    }
}
