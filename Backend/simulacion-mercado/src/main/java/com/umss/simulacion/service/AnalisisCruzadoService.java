package com.umss.simulacion.service;

import com.umss.simulacion.model.Empleador;
import com.umss.simulacion.model.Titulado;
import com.umss.simulacion.repository.EmpleadorRepository;
import com.umss.simulacion.repository.TituladoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que compara la información de titulados vs. empleadores
 * para detectar brechas de competencias y oportunidades de mejora.
 */
@Service
public class AnalisisCruzadoService {

    @Autowired
    private TituladoRepository tituladoRepository;

    @Autowired
    private EmpleadorRepository empleadorRepository;

    // ============================================================
    // CLASE INTERNA: Resultado del análisis cruzado
    // ============================================================
    public static class ResultadoCruzado {
        public int totalTitulados;
        public int totalEmpleadores;
        public double satisfaccionPromedioTitulados;
        public double competenciasTecnicasPromedio;
        public double competenciasBlandasPromedio;
        public double valoracionEmpleadoresPromedio;
        public double confianzaEmpleadores;
        public double consultaEmpleadores;

        // Brecha principal
        public String brechaPrincipal;
        public double magnitudBrecha;

        // Análisis por área
        public Map<String, Integer> demandaAreasTitulados;
        public Map<String, Double> valoracionEmpleadoresItems;

        // Fortalezas y debilidades detectadas
        public List<String> fortalezas;
        public List<String> debilidades;
        public List<String> recomendaciones;

        // Getters
        public int getTotalTitulados() { return totalTitulados; }
        public int getTotalEmpleadores() { return totalEmpleadores; }
        public double getSatisfaccionPromedioTitulados() { return satisfaccionPromedioTitulados; }
        public double getCompetenciasTecnicasPromedio() { return competenciasTecnicasPromedio; }
        public double getCompetenciasBlandasPromedio() { return competenciasBlandasPromedio; }
        public double getValoracionEmpleadoresPromedio() { return valoracionEmpleadoresPromedio; }
        public double getConfianzaEmpleadores() { return confianzaEmpleadores; }
        public double getConsultaEmpleadores() { return consultaEmpleadores; }
        public String getBrechaPrincipal() { return brechaPrincipal; }
        public double getMagnitudBrecha() { return magnitudBrecha; }
        public Map<String, Integer> getDemandaAreasTitulados() { return demandaAreasTitulados; }
        public Map<String, Double> getValoracionEmpleadoresItems() { return valoracionEmpleadoresItems; }
        public List<String> getFortalezas() { return fortalezas; }
        public List<String> getDebilidades() { return debilidades; }
        public List<String> getRecomendaciones() { return recomendaciones; }
    }

    // ============================================================
    // ANÁLISIS CRUZADO PRINCIPAL
    // ============================================================
    public ResultadoCruzado analizar() {
        List<Titulado> titulados = tituladoRepository.findAll();
        List<Empleador> empleadores = empleadorRepository.findAll();

        ResultadoCruzado r = new ResultadoCruzado();

        // 1. Estadísticas básicas
        r.totalTitulados = titulados.size();
        r.totalEmpleadores = empleadores.size();

        // 2. Promedios de titulados
        r.competenciasTecnicasPromedio = promedioTitulados(titulados, Titulado::getCompetenciasTecnicas);
        r.competenciasBlandasPromedio = promedioTitulados(titulados, Titulado::getCompetenciasBlandas);
        r.satisfaccionPromedioTitulados = promedioTitulados(titulados, Titulado::getSatisfaccion);

        // 3. Promedios de empleadores
        Map<String, Double> itemsLikert = new LinkedHashMap<>();
        itemsLikert.put("Confianza en la UMSS", promedioEmpleadores(empleadores, Empleador::getValConfianza));
        itemsLikert.put("Consistencia del título", promedioEmpleadores(empleadores, Empleador::getValConsistenciaTitulo));
        itemsLikert.put("Consulta a empleadores", promedioEmpleadores(empleadores, Empleador::getValConsultasEmpleadores));
        itemsLikert.put("Conoce el perfil", promedioEmpleadores(empleadores, Empleador::getValConocePerfil));
        itemsLikert.put("Coherencia perfil", promedioEmpleadores(empleadores, Empleador::getValCoherenciaPerfil));
        itemsLikert.put("Formación integral", promedioEmpleadores(empleadores, Empleador::getValFormacionIntegral));
        itemsLikert.put("Incorpora necesidades", promedioEmpleadores(empleadores, Empleador::getValIncorporacionNecesidades));
        itemsLikert.put("Competencias laborales", promedioEmpleadores(empleadores, Empleador::getValCompetenciasLaborales));
        itemsLikert.put("Valores y ética", promedioEmpleadores(empleadores, Empleador::getValValoresEtica));
        itemsLikert.put("Participación procesos", promedioEmpleadores(empleadores, Empleador::getValParticipacionProcesos));
        r.valoracionEmpleadoresItems = itemsLikert;

        // Promedio general de empleadores
        double valoracionProm = itemsLikert.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
        r.valoracionEmpleadoresPromedio = Math.round(valoracionProm * 100.0) / 100.0;

        r.confianzaEmpleadores = itemsLikert.get("Confianza en la UMSS");
        r.consultaEmpleadores = itemsLikert.get("Consulta a empleadores");

        // 4. Demanda de áreas de interés
        Map<String, Long> areasCount = titulados.stream()
                .filter(t -> t.getAreaInteres() != null && !t.getAreaInteres().isEmpty())
                .collect(Collectors.groupingBy(Titulado::getAreaInteres, Collectors.counting()));

        Map<String, Integer> areasInt = new LinkedHashMap<>();
        areasCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> areasInt.put(e.getKey(), e.getValue().intValue()));
        r.demandaAreasTitulados = areasInt;

        // 5. Detectar brecha principal
        // La brecha = baja consulta a empleadores vs. alta satisfacción de titulados
        double brecha = r.satisfaccionPromedioTitulados - r.consultaEmpleadores;
        if (brecha > 0) {
            r.brechaPrincipal = "Desconexión entre la satisfacción de titulados y la vinculación con empleadores";
            r.magnitudBrecha = Math.round(brecha * 100.0) / 100.0;
        } else {
            r.brechaPrincipal = "Alineación adecuada entre titulados y empleadores";
            r.magnitudBrecha = 0;
        }

        // 6. Detectar fortalezas
        List<String> fortalezas = new ArrayList<>();
        if (r.satisfaccionPromedioTitulados >= 4.0) {
            fortalezas.add("Alta satisfacción de los titulados con la formación recibida (" + r.satisfaccionPromedioTitulados + "/5)");
        }
        if (r.competenciasBlandasPromedio >= 3.5) {
            fortalezas.add("Buena formación en competencias blandas (" + r.competenciasBlandasPromedio + "/5)");
        }
        if (itemsLikert.get("Valores y ética") >= 3.5) {
            fortalezas.add("Alta valoración de los empleadores en valores y ética profesional (" + itemsLikert.get("Valores y ética") + "/4)");
        }
        if (itemsLikert.get("Confianza en la UMSS") >= 3.5) {
            fortalezas.add("Los empleadores confían en la UMSS como formadora (" + itemsLikert.get("Confianza en la UMSS") + "/4)");
        }
        r.fortalezas = fortalezas;

        // 7. Detectar debilidades
        List<String> debilidades = new ArrayList<>();
        if (r.competenciasTecnicasPromedio < 4.0) {
            debilidades.add("Brecha en competencias técnicas (" + r.competenciasTecnicasPromedio + "/5)");
        }
        if (itemsLikert.get("Consulta a empleadores") < 3.0) {
            debilidades.add("Baja consulta a empleadores sobre el perfil profesional (" + itemsLikert.get("Consulta a empleadores") + "/4)");
        }
        if (itemsLikert.get("Incorpora necesidades") < 3.0) {
            debilidades.add("Falta de incorporación de necesidades del sector (" + itemsLikert.get("Incorpora necesidades") + "/4)");
        }
        if (itemsLikert.get("Participación procesos") < 3.0) {
            debilidades.add("Baja participación de empleadores en procesos de mejora (" + itemsLikert.get("Participación procesos") + "/4)");
        }
        r.debilidades = debilidades;

        // 8. Generar recomendaciones
        List<String> recomendaciones = new ArrayList<>();
        if (itemsLikert.get("Consulta a empleadores") < 3.0) {
            recomendaciones.add("Establecer un Comité Asesor de Empleadores que se reúna semestralmente");
        }
        if (r.competenciasTecnicasPromedio < 4.0) {
            recomendaciones.add("Actualizar el plan de estudios con tecnologías emergentes (IA, Cloud, Ciberseguridad)");
        }
        if (itemsLikert.get("Incorpora necesidades") < 3.0) {
            recomendaciones.add("Crear un Observatorio Permanente de Titulados y Empleadores");
        }
        if (r.satisfaccionPromedioTitulados >= 4.0) {
            recomendaciones.add("Mantener la calidad formativa y fortalecer las prácticas profesionales");
        }
        recomendaciones.add("Firmar convenios B2B con las 5 organizaciones encuestadas");
        recomendaciones.add("Diseñar posgrados modulares en las áreas de mayor demanda");
        r.recomendaciones = recomendaciones;

        return r;
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private double promedioTitulados(List<Titulado> titulados,
                                      java.util.function.Function<Titulado, Integer> getter) {
        List<Integer> valores = titulados.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (valores.isEmpty()) return 0;
        double suma = valores.stream().mapToInt(Integer::intValue).sum();
        return Math.round((suma / valores.size()) * 100.0) / 100.0;
    }

    private double promedioEmpleadores(List<Empleador> empleadores,
                                        java.util.function.Function<Empleador, Integer> getter) {
        List<Integer> valores = empleadores.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (valores.isEmpty()) return 0;
        double suma = valores.stream().mapToInt(Integer::intValue).sum();
        return Math.round((suma / valores.size()) * 100.0) / 100.0;
    }
}
