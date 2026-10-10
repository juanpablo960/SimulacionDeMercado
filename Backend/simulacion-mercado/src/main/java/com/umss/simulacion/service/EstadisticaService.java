package com.umss.simulacion.service;

import com.umss.simulacion.model.Titulado;
import com.umss.simulacion.repository.TituladoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que contiene toda la lógica estadística del análisis de mercado.
 * Calcula estadísticas descriptivas: media, mediana, desviación estándar, frecuencias.
 */
@Service
public class EstadisticaService {

    @Autowired
    private TituladoRepository tituladoRepository;

    // ============================================================
    // ESTADÍSTICAS BÁSICAS
    // ============================================================

    /**
     * Calcula la media aritmética de una lista de números.
     * Fórmula: x̄ = Σxᵢ / n
     */
    public double media(List<Integer> datos) {
        if (datos == null || datos.isEmpty()) return 0;
        return datos.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    /**
     * Calcula la mediana de una lista de números.
     */
    public double mediana(List<Integer> datos) {
        if (datos == null || datos.isEmpty()) return 0;
        List<Integer> ordenados = datos.stream()
                .sorted()
                .collect(Collectors.toList());
        int n = ordenados.size();
        if (n % 2 == 0) {
            return (ordenados.get(n / 2 - 1) + ordenados.get(n / 2)) / 2.0;
        } else {
            return ordenados.get(n / 2);
        }
    }

    /**
     * Calcula la desviación estándar muestral.
     * Fórmula: S = √(Σ(xᵢ - x̄)² / (n - 1))
     */
    public double desviacionEstandar(List<Integer> datos) {
        if (datos == null || datos.size() < 2) return 0;
        double media = media(datos);
        double sumaCuadrados = datos.stream()
                .mapToDouble(d -> Math.pow(d - media, 2))
                .sum();
        return Math.sqrt(sumaCuadrados / (datos.size() - 1));
    }

    /**
     * Calcula la varianza muestral.
     */
    public double varianza(List<Integer> datos) {
        if (datos == null || datos.size() < 2) return 0;
        double media = media(datos);
        return datos.stream()
                .mapToDouble(d -> Math.pow(d - media, 2))
                .sum() / (datos.size() - 1);
    }

    /**
     * Calcula el valor mínimo.
     */
    public int minimo(List<Integer> datos) {
        if (datos == null || datos.isEmpty()) return 0;
        return datos.stream().mapToInt(Integer::intValue).min().orElse(0);
    }

    /**
     * Calcula el valor máximo.
     */
    public int maximo(List<Integer> datos) {
        if (datos == null || datos.isEmpty()) return 0;
        return datos.stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    /**
     * Calcula la moda.
     */
    public String moda(List<String> datos) {
        if (datos == null || datos.isEmpty()) return "Sin datos";
        return datos.stream()
                .filter(d -> d != null && !d.isEmpty())
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin datos");
    }

    // ============================================================
    // FRECUENCIAS
    // ============================================================

    /**
     * Calcula las frecuencias absolutas.
     */
    public Map<String, Long> frecuencias(List<String> datos) {
        if (datos == null || datos.isEmpty()) return new HashMap<>();
        return datos.stream()
                .filter(d -> d != null && !d.isEmpty())
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));
    }

    /**
     * Calcula las frecuencias relativas (porcentajes).
     */
    public Map<String, Double> frecuenciasRelativas(List<String> datos) {
        Map<String, Long> absolutas = frecuencias(datos);
        long total = absolutas.values().stream().mapToLong(Long::longValue).sum();
        Map<String, Double> relativas = new LinkedHashMap<>();
        absolutas.forEach((clave, valor) -> {
            double porcentaje = total > 0 ? (valor * 100.0 / total) : 0;
            relativas.put(clave, Math.round(porcentaje * 100.0) / 100.0);
        });
        return relativas;
    }

    // ============================================================
    // REPORTE COMPLETO DE TITULADOS
    // ============================================================

    /**
     * Genera un reporte completo con las estadísticas de todos los titulados.
     * Siempre incluye TODAS las claves, incluso si están vacías.
     */
    public Map<String, Object> generarReporteTitulados() {
        List<Titulado> titulados = tituladoRepository.findAll();
        Map<String, Object> reporte = new HashMap<>();

        // Total
        reporte.put("totalTitulados", titulados.size());

        // --- Estadísticas de edad (SIEMPRE incluidas) ---
        List<Integer> edades = titulados.stream()
                .map(Titulado::getEdad)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Object> edadStats = new HashMap<>();
        edadStats.put("media", edades.isEmpty() ? 0 : Math.round(media(edades) * 100.0) / 100.0);
        edadStats.put("mediana", edades.isEmpty() ? 0 : mediana(edades));
        edadStats.put("desviacionEstandar", edades.isEmpty() ? 0 : Math.round(desviacionEstandar(edades) * 100.0) / 100.0);
        edadStats.put("varianza", edades.isEmpty() ? 0 : Math.round(varianza(edades) * 100.0) / 100.0);
        edadStats.put("minimo", edades.isEmpty() ? 0 : minimo(edades));
        edadStats.put("maximo", edades.isEmpty() ? 0 : maximo(edades));
        reporte.put("estadisticasEdad", edadStats);

        // --- Estadísticas de años de vida profesional (SIEMPRE incluidas) ---
        List<Integer> aniosProf = titulados.stream()
                .map(Titulado::getAniosVidaProfesional)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Object> aniosStats = new HashMap<>();
        aniosStats.put("media", aniosProf.isEmpty() ? 0 : Math.round(media(aniosProf) * 100.0) / 100.0);
        aniosStats.put("mediana", aniosProf.isEmpty() ? 0 : mediana(aniosProf));
        aniosStats.put("desviacionEstandar", aniosProf.isEmpty() ? 0 : Math.round(desviacionEstandar(aniosProf) * 100.0) / 100.0);
        aniosStats.put("minimo", aniosProf.isEmpty() ? 0 : minimo(aniosProf));
        aniosStats.put("maximo", aniosProf.isEmpty() ? 0 : maximo(aniosProf));
        reporte.put("estadisticasAniosProfesionales", aniosStats);

        // --- Frecuencias por género (SIEMPRE incluidas) ---
        List<String> generos = titulados.stream()
                .map(Titulado::getGenero)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        reporte.put("porGenero", frecuenciasRelativas(generos));

        // --- Frecuencias por área de interés (SIEMPRE incluidas) ---
        List<String> areas = titulados.stream()
                .map(Titulado::getAreaInteres)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        reporte.put("porAreaInteres", frecuenciasRelativas(areas));

        // --- Frecuencias por financiamiento (SIEMPRE incluidas) ---
        List<String> financiamientos = titulados.stream()
                .map(Titulado::getFinanciamiento)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        reporte.put("porFinanciamiento", frecuenciasRelativas(financiamientos));

        // --- Frecuencias por situación laboral (SIEMPRE incluidas) ---
        List<String> situaciones = titulados.stream()
                .map(Titulado::getSituacionLaboral)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        reporte.put("porSituacionLaboral", frecuenciasRelativas(situaciones));

        // --- Promedios de competencias (SIEMPRE incluidos) ---
        List<Integer> compTecnicas = titulados.stream()
                .map(Titulado::getCompetenciasTecnicas)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Integer> compBlandas = titulados.stream()
                .map(Titulado::getCompetenciasBlandas)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Integer> satisfaccion = titulados.stream()
                .map(Titulado::getSatisfaccion)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        reporte.put("promedioCompetenciasTecnicas",
                compTecnicas.isEmpty() ? 0 : Math.round(media(compTecnicas) * 100.0) / 100.0);
        reporte.put("promedioCompetenciasBlandas",
                compBlandas.isEmpty() ? 0 : Math.round(media(compBlandas) * 100.0) / 100.0);
        reporte.put("promedioSatisfaccion",
                satisfaccion.isEmpty() ? 0 : Math.round(media(satisfaccion) * 100.0) / 100.0);

        return reporte;
    }
}