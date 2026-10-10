package com.umss.simulacion.controller;

import com.umss.simulacion.service.AnalisisCruzadoService;
import com.umss.simulacion.service.SimulacionColasService;
import com.umss.simulacion.model.Titulado;
import com.umss.simulacion.repository.TituladoRepository;
import com.umss.simulacion.service.ChiCuadradaService;
import com.umss.simulacion.service.EstadisticaService;
import com.umss.simulacion.service.ExportacionService;
import com.umss.simulacion.service.SimulacionInversionService;
import com.umss.simulacion.service.SimulacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador que maneja todas las rutas web relacionadas con los titulados.
 */
@Controller
@RequestMapping("/titulados")
public class TituladoController {

    @Autowired
    private TituladoRepository tituladoRepository;

    @Autowired
    private EstadisticaService estadisticaService;

    @Autowired
    private ChiCuadradaService chiCuadradaService;

    @Autowired
    private ExportacionService exportacionService;

    @Autowired
    private SimulacionService simulacionService;

    @Autowired
    private SimulacionInversionService simulacionInversionService;

    @Autowired
    private SimulacionColasService simulacionColasService;

    @Autowired
    private AnalisisCruzadoService analisisCruzadoService;

    // ============================================================
    // PÁGINA DE INICIO DE TITULADOS
    // ============================================================
    @GetMapping
    public String index(Model model) {
        model.addAttribute("totalTitulados", tituladoRepository.count());
        return "titulados/index";
    }

    // ============================================================
    // MOSTRAR FORMULARIO DE ENCUESTA
    // ============================================================
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("titulado", new Titulado());
        return "titulados/formulario";
    }

    // ============================================================
    // GUARDAR ENCUESTA
    // ============================================================
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Titulado titulado) {
        tituladoRepository.save(titulado);
        return "redirect:/titulados/lista";
    }

    // ============================================================
    // LISTAR TODOS LOS TITULADOS
    // ============================================================
    @GetMapping("/lista")
    public String listar(Model model) {
        model.addAttribute("titulados", tituladoRepository.findAll());
        return "titulados/lista";
    }

    // ============================================================
    // VER DETALLE DE UN TITULADO
    // ============================================================
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Titulado titulado = tituladoRepository.findById(id).orElse(null);
        model.addAttribute("titulado", titulado);
        return "titulados/detalle";
    }

    // ============================================================
    // ELIMINAR UN TITULADO
    // ============================================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        tituladoRepository.deleteById(id);
        return "redirect:/titulados/lista";
    }

    // ============================================================
    // DASHBOARD CON ESTADÍSTICAS
    // ============================================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("reporte", estadisticaService.generarReporteTitulados());
        return "titulados/dashboard";
    }

    // ============================================================
    // EXPORTAR A CSV
    // ============================================================
    @GetMapping("/exportar/csv")
    public ResponseEntity<byte[]> exportarCSV() {
        String csv = exportacionService.exportarCSV();
        byte[] bytes = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "titulados.csv");
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    // ============================================================
    // EXPORTAR A EXCEL
    // ============================================================
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel() throws Exception {
        byte[] bytes = exportacionService.exportarExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "titulados.xlsx");
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    // ============================================================
    // CHI-CUADRADA
    // ============================================================
    @GetMapping("/chi-cuadrada")
    public String chiCuadrada(@RequestParam(value = "var1", required = false) String var1,
                              @RequestParam(value = "var2", required = false) String var2,
                              Model model) {
        model.addAttribute("variablesDisponibles", chiCuadradaService.obtenerVariablesDisponibles());

        if (var1 != null && !var1.isEmpty() && var2 != null && !var2.isEmpty()) {
            ChiCuadradaService.ResultadoChiCuadrada resultado = chiCuadradaService.calcular(var1, var2);
            model.addAttribute("resultado", resultado);
            model.addAttribute("var1Seleccionada", var1);
            model.addAttribute("var2Seleccionada", var2);
        }

        return "titulados/chi-cuadrada";
    }

    // ============================================================
    // SIMULACIÓN MONTE CARLO
    // ============================================================
    @GetMapping("/simulacion")
    public String simulacion(@RequestParam(value = "n", required = false, defaultValue = "1000") Integer n,
                             Model model) {
        SimulacionService.ResultadoSimulacion resultado = simulacionService.simularDemandaPosgrados(n);
        model.addAttribute("resultado", resultado);
        model.addAttribute("nSimulaciones", n);
        model.addAttribute("infoGenerador", simulacionService.infoGenerador());
        return "titulados/simulacion";
    }

    // ============================================================
    // SIMULACIÓN DE INVERSIÓN (TIR)
    // ============================================================
    @GetMapping("/inversion")
    public String inversion(@RequestParam(value = "n", required = false, defaultValue = "1000") Integer n,
                            @RequestParam(value = "trema", required = false, defaultValue = "15") Double trema,
                            Model model) {
        double tremaDecimal = trema / 100.0;
        SimulacionInversionService.ResultadoInversion resultado =
                simulacionInversionService.simular(n, tremaDecimal);
        model.addAttribute("resultado", resultado);
        model.addAttribute("nSimulaciones", n);
        model.addAttribute("tremaSeleccionada", trema);
        return "titulados/inversion";
    }
    // ============================================================
// ============================================================
// SIMULACIÓN DE COLAS
// ============================================================
@GetMapping("/colas")
public String colas(@RequestParam(value = "n", required = false, defaultValue = "100") Integer n,
                    @RequestParam(value = "lambda", required = false, defaultValue = "10") Double lambda,
                    Model model) {
    int[] servidores = {1, 2, 3, 4, 5, 6};
    List<SimulacionColasService.ResultadoColas> resultados =
            simulacionColasService.compararEscenarios(servidores, n, lambda / 60.0, 5.0, 15.0);

    // Calcular el número óptimo de servidores (utilización entre 60% y 90%)
    int servidorOptimo = -1;
    for (SimulacionColasService.ResultadoColas r : resultados) {
        if (r.utilizacion >= 60 && r.utilizacion <= 90) {
            servidorOptimo = r.numServidores;
            break;
        }
    }

    // Si ninguno está en el rango ideal, escoger el de menor utilización >= 90% (o el de mayor número)
    if (servidorOptimo == -1) {
        double menorUtilizacion = 999;
        for (SimulacionColasService.ResultadoColas r : resultados) {
            if (r.utilizacion < menorUtilizacion) {
                menorUtilizacion = r.utilizacion;
                servidorOptimo = r.numServidores;
            }
        }
    }

    model.addAttribute("resultados", resultados);
    model.addAttribute("nClientes", n);
    model.addAttribute("lambda", lambda);
    model.addAttribute("servidorOptimo", servidorOptimo);

    return "titulados/colas";
}
// ============================================================
// ANÁLISIS CRUZADO TITULADOS × EMPLEADORES
// ============================================================
@GetMapping("/analisis-cruzado")
public String analisisCruzado(Model model) {
    AnalisisCruzadoService.ResultadoCruzado resultado = analisisCruzadoService.analizar();
    model.addAttribute("resultado", resultado);
    return "titulados/analisis-cruzado";
    }
}