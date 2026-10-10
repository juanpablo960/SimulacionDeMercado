package com.umss.simulacion.service;

import com.umss.simulacion.model.Titulado;
import com.umss.simulacion.repository.TituladoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Servicio encargado de exportar los datos de titulados a CSV y Excel.
 */
@Service
public class ExportacionService {

    @Autowired
    private TituladoRepository tituladoRepository;

    // ============================================================
    // EXPORTAR A CSV
    // ============================================================
    public String exportarCSV() {
        List<Titulado> titulados = tituladoRepository.findAll();

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        // Cabecera del CSV
        writer.println("ID,Edad,Genero,AnioEgreso,FormacionComplementaria,NivelMaximo,DondeCurso," +
                "Financiamiento,NombrePrograma,InteresadoPosgrado,AreaInteres,OrganizacionPreferida," +
                "AniosVidaProfesional,TiempoDesempleado,SituacionLaboral,TiempoPrimerEmpleo,NumeroEmpleos," +
                "CompetenciasTecnicas,CompetenciasBlandas,Satisfaccion,Email,Telefono,Linkedin,RedContactos");

        // Filas
        for (Titulado t : titulados) {
            writer.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    safe(t.getId()),
                    safe(t.getEdad()),
                    safe(t.getGenero()),
                    safe(t.getAnioEgreso()),
                    safe(t.getFormacionComplementaria()),
                    safe(t.getNivelMaximo()),
                    safe(t.getDondeCurso()),
                    safe(t.getFinanciamiento()),
                    safe(t.getNombrePrograma()),
                    safe(t.getInteresadoPosgrado()),
                    safe(t.getAreaInteres()),
                    safe(t.getOrganizacionPreferida()),
                    safe(t.getAniosVidaProfesional()),
                    safe(t.getTiempoDesempleado()),
                    safe(t.getSituacionLaboral()),
                    safe(t.getTiempoPrimerEmpleo()),
                    safe(t.getNumeroEmpleos()),
                    safe(t.getCompetenciasTecnicas()),
                    safe(t.getCompetenciasBlandas()),
                    safe(t.getSatisfaccion()),
                    safe(t.getEmail()),
                    safe(t.getTelefono()),
                    safe(t.getLinkedin()),
                    safe(t.getRedContactos())
            ));
        }

        writer.flush();
        return stringWriter.toString();
    }

    // ============================================================
    // EXPORTAR A EXCEL
    // ============================================================
    public byte[] exportarExcel() throws IOException {
        List<Titulado> titulados = tituladoRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Titulados");

            // Estilo para la cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Cabecera
            String[] columnas = {
                    "ID", "Edad", "Género", "Año Egreso", "Formación Compl.", "Nivel Máximo",
                    "Dónde Cursó", "Financiamiento", "Nombre Programa", "Interesado Posgrado",
                    "Área Interés", "Organización Preferida", "Años Profesionales",
                    "Tiempo Desempleado", "Situación Laboral", "Tiempo Primer Empleo",
                    "Número Empleos", "Comp. Técnicas", "Comp. Blandas", "Satisfacción",
                    "Email", "Teléfono", "LinkedIn", "Red Contactos"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Filas de datos
            int rowNum = 1;
            for (Titulado t : titulados) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(safe(t.getId()));
                row.createCell(1).setCellValue(safe(t.getEdad()));
                row.createCell(2).setCellValue(safe(t.getGenero()));
                row.createCell(3).setCellValue(safe(t.getAnioEgreso()));
                row.createCell(4).setCellValue(safe(t.getFormacionComplementaria()));
                row.createCell(5).setCellValue(safe(t.getNivelMaximo()));
                row.createCell(6).setCellValue(safe(t.getDondeCurso()));
                row.createCell(7).setCellValue(safe(t.getFinanciamiento()));
                row.createCell(8).setCellValue(safe(t.getNombrePrograma()));
                row.createCell(9).setCellValue(safe(t.getInteresadoPosgrado()));
                row.createCell(10).setCellValue(safe(t.getAreaInteres()));
                row.createCell(11).setCellValue(safe(t.getOrganizacionPreferida()));
                row.createCell(12).setCellValue(safe(t.getAniosVidaProfesional()));
                row.createCell(13).setCellValue(safe(t.getTiempoDesempleado()));
                row.createCell(14).setCellValue(safe(t.getSituacionLaboral()));
                row.createCell(15).setCellValue(safe(t.getTiempoPrimerEmpleo()));
                row.createCell(16).setCellValue(safe(t.getNumeroEmpleos()));
                row.createCell(17).setCellValue(safe(t.getCompetenciasTecnicas()));
                row.createCell(18).setCellValue(safe(t.getCompetenciasBlandas()));
                row.createCell(19).setCellValue(safe(t.getSatisfaccion()));
                row.createCell(20).setCellValue(safe(t.getEmail()));
                row.createCell(21).setCellValue(safe(t.getTelefono()));
                row.createCell(22).setCellValue(safe(t.getLinkedin()));
                row.createCell(23).setCellValue(safe(t.getRedContactos()));
            }

            // Autoajustar ancho de columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    // ============================================================
    // HELPER: convertir null a string vacío
    // ============================================================
    private String safe(Object valor) {
        if (valor == null) return "";
        String s = valor.toString();
        // Escapar comas y comillas para CSV
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}