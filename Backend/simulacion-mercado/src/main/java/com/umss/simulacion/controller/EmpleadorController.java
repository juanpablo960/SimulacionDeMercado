package com.umss.simulacion.controller;

import com.umss.simulacion.model.Empleador;
import com.umss.simulacion.repository.EmpleadorRepository;
import com.umss.simulacion.service.EmpleadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador que maneja las rutas web relacionadas con los empleadores.
 */
@Controller
@RequestMapping("/empleadores")
public class EmpleadorController {

    @Autowired
    private EmpleadorRepository empleadorRepository;

    @Autowired
    private EmpleadorService empleadorService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("totalEmpleadores", empleadorRepository.count());
        return "empleadores/index";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("empleador", new Empleador());
        return "empleadores/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Empleador empleador) {
        empleadorRepository.save(empleador);
        return "redirect:/empleadores/lista";
    }

    @GetMapping("/lista")
    public String listar(Model model) {
        model.addAttribute("empleadores", empleadorRepository.findAll());
        return "empleadores/lista";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Empleador empleador = empleadorRepository.findById(id).orElse(null);
        model.addAttribute("empleador", empleador);
        return "empleadores/detalle";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        empleadorRepository.deleteById(id);
        return "redirect:/empleadores/lista";
    }

    // ============================================================
    // DASHBOARD DE EMPLEADORES
    // ============================================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("reporte", empleadorService.generarReporte());
        return "empleadores/dashboard";
    }

    // ============================================================
    // EXPORTAR CSV
    // ============================================================
    @GetMapping("/exportar/csv")
    public ResponseEntity<byte[]> exportarCSV() {
        String csv = empleadorService.exportarCSV();
        byte[] bytes = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "empleadores.csv");
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    // ============================================================
    // EXPORTAR EXCEL
    // ============================================================
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel() throws Exception {
        byte[] bytes = empleadorService.exportarExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "empleadores.xlsx");
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}