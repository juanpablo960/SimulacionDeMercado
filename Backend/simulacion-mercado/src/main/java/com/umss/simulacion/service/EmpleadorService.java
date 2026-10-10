package com.umss.simulacion.service;

import com.umss.simulacion.model.Empleador;
import com.umss.simulacion.repository.EmpleadorRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que contiene la lógica de empleadores:
 * estadísticas, frecuencias y exportación.
 */
@Service
public class EmpleadorService {

    @Autowired
    private EmpleadorRepository empleadorRepository;

    // ============================================================
    // GENERAR REPORTE DE EMPLEADORES
    // ============================================================
    public Map<String, Object> generarReporte() {
        List<Empleador> empleadores = empleadorRepository.findAll();
        Map<String, Object> reporte = new HashMap<>();

        reporte.put("totalEmpleadores", empleadores.size());

        // Frecuencias por tipo
        List<String> tipos = empleadores.stream()
                .map(Empleador::getTipoOrganizacion)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        reporte.put("porTipo", frecuenciasRelativas(tipos));

        // Frecuencias por tamaño
        List<String> tamanos = empleadores.stream()
                .map(Empleador::getTamanoOrganizacion)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        reporte.put("porTamano", frecuenciasRelativas(tamanos));

        // Frecuencias por rubro
        List<String> rubros = empleadores.stream()
                .map(Empleador::getRubro)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        reporte.put("porRubro", frecuenciasRelativas(rubros));

        // Frecuencias por probabilidad de contratación
        List<String> probs = empleadores.stream()
                .map(Empleador::getProbabilidadContratacion)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        reporte.put("porProbabilidad", frecuenciasRelativas(probs));

        // Frecuencias por nivel de formación
        List<String> niveles = empleadores.stream()
                .map(Empleador::getNivelFormacionRequerido)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        reporte.put("porNivelFormacion", frecuenciasRelativas(niveles));

        // Promedios de valoración Likert
        Map<String, Double> promediosLikert = new LinkedHashMap<>();
        promediosLikert.put("Confianza UMSS", promedio(empleadores, Empleador::getValConfianza));
        promediosLikert.put("Consistencia título", promedio(empleadores, Empleador::getValConsistenciaTitulo));
        promediosLikert.put("Consulta empleadores", promedio(empleadores, Empleador::getValConsultasEmpleadores));
        promediosLikert.put("Conoce perfil", promedio(empleadores, Empleador::getValConocePerfil));
        promediosLikert.put("Coherencia perfil", promedio(empleadores, Empleador::getValCoherenciaPerfil));
        promediosLikert.put("Formación integral", promedio(empleadores, Empleador::getValFormacionIntegral));
        promediosLikert.put("Incorporación necesidades", promedio(empleadores, Empleador::getValIncorporacionNecesidades));
        promediosLikert.put("Competencias laborales", promedio(empleadores, Empleador::getValCompetenciasLaborales));
        promediosLikert.put("Valores y ética", promedio(empleadores, Empleador::getValValoresEtica));
        promediosLikert.put("Participación procesos", promedio(empleadores, Empleador::getValParticipacionProcesos));
        reporte.put("promediosLikert", promediosLikert);

        // Promedio general Likert
        double promedioGeneral = promediosLikert.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
        reporte.put("promedioGeneralLikert", Math.round(promedioGeneral * 100.0) / 100.0);

        return reporte;
    }

    // ============================================================
    // EXPORTAR A CSV
    // ============================================================
    public String exportarCSV() {
        List<Empleador> empleadores = empleadorRepository.findAll();
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        writer.println("ID,NombreEmpresa,TipoOrganizacion,TamanoOrganizacion,Rubro," +
                "ContratoUltimos5Anios,ProbabilidadContratacion,NivelFormacionRequerido," +
                "MedioConvocatoria,CargosDesempenados,NuevasAreasConocimiento," +
                "NuevasHerramientas,HabilidadesCompetencias," +
                "ValConfianza,ValConsistenciaTitulo,ValConsultasEmpleadores,ValConocePerfil," +
                "ValCoherenciaPerfil,ValFormacionIntegral,ValIncorporacionNecesidades," +
                "ValCompetenciasLaborales,ValValoresEtica,ValParticipacionProcesos");

        for (Empleador e : empleadores) {
            writer.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    safe(e.getId()),
                    safe(e.getNombreEmpresa()),
                    safe(e.getTipoOrganizacion()),
                    safe(e.getTamanoOrganizacion()),
                    safe(e.getRubro()),
                    safe(e.getContratoUltimos5Anios()),
                    safe(e.getProbabilidadContratacion()),
                    safe(e.getNivelFormacionRequerido()),
                    safe(e.getMedioConvocatoria()),
                    safe(e.getCargosDesempenados()),
                    safe(e.getNuevasAreasConocimiento()),
                    safe(e.getNuevasHerramientas()),
                    safe(e.getHabilidadesCompetencias()),
                    safe(e.getValConfianza()),
                    safe(e.getValConsistenciaTitulo()),
                    safe(e.getValConsultasEmpleadores()),
                    safe(e.getValConocePerfil()),
                    safe(e.getValCoherenciaPerfil()),
                    safe(e.getValFormacionIntegral()),
                    safe(e.getValIncorporacionNecesidades()),
                    safe(e.getValCompetenciasLaborales()),
                    safe(e.getValValoresEtica()),
                    safe(e.getValParticipacionProcesos())
            ));
        }

        writer.flush();
        return stringWriter.toString();
    }

    // ============================================================
    // EXPORTAR A EXCEL
    // ============================================================
    public byte[] exportarExcel() throws IOException {
        List<Empleador> empleadores = empleadorRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Empleadores");

            // Estilo cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] columnas = {
                    "ID", "Nombre Empresa", "Tipo", "Tamaño", "Rubro",
                    "Contrato 5 años", "Prob. Contratación", "Nivel Formación",
                    "Medio Convocatoria", "Cargos", "Nuevas Áreas",
                    "Herramientas", "Habilidades",
                    "Confianza UMSS", "Consistencia Título", "Consulta Empleadores",
                    "Conoce Perfil", "Coherencia Perfil", "Formación Integral",
                    "Incorpora Necesidades", "Competencias", "Valores y Ética", "Participación"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Empleador e : empleadores) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(safe(e.getId()));
                row.createCell(1).setCellValue(safe(e.getNombreEmpresa()));
                row.createCell(2).setCellValue(safe(e.getTipoOrganizacion()));
                row.createCell(3).setCellValue(safe(e.getTamanoOrganizacion()));
                row.createCell(4).setCellValue(safe(e.getRubro()));
                row.createCell(5).setCellValue(safe(e.getContratoUltimos5Anios()));
                row.createCell(6).setCellValue(safe(e.getProbabilidadContratacion()));
                row.createCell(7).setCellValue(safe(e.getNivelFormacionRequerido()));
                row.createCell(8).setCellValue(safe(e.getMedioConvocatoria()));
                row.createCell(9).setCellValue(safe(e.getCargosDesempenados()));
                row.createCell(10).setCellValue(safe(e.getNuevasAreasConocimiento()));
                row.createCell(11).setCellValue(safe(e.getNuevasHerramientas()));
                row.createCell(12).setCellValue(safe(e.getHabilidadesCompetencias()));
                row.createCell(13).setCellValue(safe(e.getValConfianza()));
                row.createCell(14).setCellValue(safe(e.getValConsistenciaTitulo()));
                row.createCell(15).setCellValue(safe(e.getValConsultasEmpleadores()));
                row.createCell(16).setCellValue(safe(e.getValConocePerfil()));
                row.createCell(17).setCellValue(safe(e.getValCoherenciaPerfil()));
                row.createCell(18).setCellValue(safe(e.getValFormacionIntegral()));
                row.createCell(19).setCellValue(safe(e.getValIncorporacionNecesidades()));
                row.createCell(20).setCellValue(safe(e.getValCompetenciasLaborales()));
                row.createCell(21).setCellValue(safe(e.getValValoresEtica()));
                row.createCell(22).setCellValue(safe(e.getValParticipacionProcesos()));
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private Map<String, Double> frecuenciasRelativas(List<String> datos) {
        if (datos.isEmpty()) return new LinkedHashMap<>();
        Map<String, Long> absolutas = datos.stream()
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));
        long total = absolutas.values().stream().mapToLong(Long::longValue).sum();
        Map<String, Double> relativas = new LinkedHashMap<>();
        absolutas.forEach((k, v) -> {
            double p = total > 0 ? (v * 100.0 / total) : 0;
            relativas.put(k, Math.round(p * 100.0) / 100.0);
        });
        return relativas;
    }

    private double promedio(List<Empleador> empleadores, java.util.function.Function<Empleador, Integer> getter) {
        List<Integer> valores = empleadores.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (valores.isEmpty()) return 0;
        double suma = valores.stream().mapToInt(Integer::intValue).sum();
        return Math.round((suma / valores.size()) * 100.0) / 100.0;
    }

    private String safe(Object valor) {
        if (valor == null) return "";
        String s = valor.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}