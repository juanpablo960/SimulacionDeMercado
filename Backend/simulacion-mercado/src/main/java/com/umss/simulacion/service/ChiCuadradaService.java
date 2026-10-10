package com.umss.simulacion.service;

import com.umss.simulacion.model.Titulado;
import com.umss.simulacion.repository.TituladoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que realiza la prueba Chi-Cuadrada para detectar
 * dependencia entre dos variables categóricas.
 */
@Service
public class ChiCuadradaService {

    @Autowired
    private TituladoRepository tituladoRepository;

    // ============================================================
    // CLASE INTERNA: Fila para la tabla
    // ============================================================
    public static class FilaMatriz {
        private String nombre;
        private List<String> valores;
        private int total;

        public FilaMatriz(String nombre, List<String> valores, int total) {
            this.nombre = nombre;
            this.valores = valores;
            this.total = total;
        }

        public String getNombre() { return nombre; }
        public List<String> getValores() { return valores; }
        public int getTotal() { return total; }
    }

    // ============================================================
    // CLASE INTERNA: Resultado
    // ============================================================
    public static class ResultadoChiCuadrada {
        public String variable1;
        public String variable2;
        public List<String> categorias2;
        public List<Integer> totalesColumnas;
        public List<FilaMatriz> filasObservadas;
        public List<FilaMatriz> filasEsperadas;
        public int totalGeneral;
        public double chiCuadrada;
        public int gradosLibertad;
        public double valorCritico;
        public boolean esIndependiente;
        public String conclusion;

        // Getters para Thymeleaf
        public String getVariable1() { return variable1; }
        public String getVariable2() { return variable2; }
        public List<String> getCategorias2() { return categorias2; }
        public List<Integer> getTotalesColumnas() { return totalesColumnas; }
        public List<FilaMatriz> getFilasObservadas() { return filasObservadas; }
        public List<FilaMatriz> getFilasEsperadas() { return filasEsperadas; }
        public int getTotalGeneral() { return totalGeneral; }
        public double getChiCuadrada() { return chiCuadrada; }
        public int getGradosLibertad() { return gradosLibertad; }
        public double getValorCritico() { return valorCritico; }
        public boolean isEsIndependiente() { return esIndependiente; }
        public String getConclusion() { return conclusion; }
    }

    // ============================================================
    // MÉTODO PRINCIPAL
    // ============================================================
    public ResultadoChiCuadrada calcular(String variable1, String variable2) {
        List<Titulado> titulados = tituladoRepository.findAll();

        // 1. Extraer valores
        List<String> valores1 = titulados.stream()
                .map(t -> obtenerValor(t, variable1))
                .filter(v -> v != null && !v.isEmpty())
                .collect(Collectors.toList());

        List<String> valores2 = titulados.stream()
                .map(t -> obtenerValor(t, variable2))
                .filter(v -> v != null && !v.isEmpty())
                .collect(Collectors.toList());

        // 2. Categorías únicas
        List<String> categorias1 = valores1.stream().distinct().sorted().collect(Collectors.toList());
        List<String> categorias2 = valores2.stream().distinct().sorted().collect(Collectors.toList());

        int filas = categorias1.size();
        int columnas = categorias2.size();

        // 3. Matriz de frecuencias observadas
        int[][] frecObs = new int[filas][columnas];
        for (Titulado t : titulados) {
            String v1 = obtenerValor(t, variable1);
            String v2 = obtenerValor(t, variable2);
            if (v1 != null && !v1.isEmpty() && v2 != null && !v2.isEmpty()) {
                int i = categorias1.indexOf(v1);
                int j = categorias2.indexOf(v2);
                if (i >= 0 && j >= 0) frecObs[i][j]++;
            }
        }

        // 4. Totales
        int[] totalesFilas = new int[filas];
        int[] totalesColumnas = new int[columnas];
        int totalGeneral = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                totalesFilas[i] += frecObs[i][j];
                totalesColumnas[j] += frecObs[i][j];
                totalGeneral += frecObs[i][j];
            }
        }

        // 5. Frecuencias esperadas
        double[][] frecEsp = new double[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (totalGeneral > 0) {
                    frecEsp[i][j] = (double) totalesFilas[i] * totalesColumnas[j] / totalGeneral;
                }
            }
        }

        // 6. Chi-Cuadrada
        double chi2 = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (frecEsp[i][j] > 0) {
                    chi2 += Math.pow(frecObs[i][j] - frecEsp[i][j], 2) / frecEsp[i][j];
                }
            }
        }

        // 7. Grados de libertad
        int gl = (filas - 1) * (columnas - 1);
        double valorCritico = obtenerValorCritico(gl, 0.05);

        // 8. Construir filas precalculadas para el HTML
        List<FilaMatriz> filasObs = new ArrayList<>();
        List<FilaMatriz> filasEsp = new ArrayList<>();

        for (int i = 0; i < filas; i++) {
            // Fila observadas
            List<String> valoresObs = new ArrayList<>();
            for (int j = 0; j < columnas; j++) {
                valoresObs.add(String.valueOf(frecObs[i][j]));
            }
            filasObs.add(new FilaMatriz(categorias1.get(i), valoresObs, totalesFilas[i]));

            // Fila esperadas
            List<String> valoresEsp = new ArrayList<>();
            for (int j = 0; j < columnas; j++) {
                valoresEsp.add(String.format("%.2f", frecEsp[i][j]));
            }
            filasEsp.add(new FilaMatriz(categorias1.get(i), valoresEsp, 0));
        }

        // Totales columnas como List
        List<Integer> totalesColsList = new ArrayList<>();
        for (int j = 0; j < columnas; j++) totalesColsList.add(totalesColumnas[j]);

        // 9. Armar resultado
        ResultadoChiCuadrada r = new ResultadoChiCuadrada();
        r.variable1 = variable1;
        r.variable2 = variable2;
        r.categorias2 = categorias2;
        r.totalesColumnas = totalesColsList;
        r.filasObservadas = filasObs;
        r.filasEsperadas = filasEsp;
        r.totalGeneral = totalGeneral;
        r.chiCuadrada = Math.round(chi2 * 10000.0) / 10000.0;
        r.gradosLibertad = gl;
        r.valorCritico = valorCritico;
        r.esIndependiente = chi2 < valorCritico;
        r.conclusion = r.esIndependiente
                ? "Las variables son INDEPENDIENTES (no hay relación significativa)"
                : "Las variables son DEPENDIENTES (sí hay relación significativa)";

        return r;
    }

    private String obtenerValor(Titulado t, String variable) {
        switch (variable) {
            case "genero": return t.getGenero();
            case "areaInteres": return t.getAreaInteres();
            case "financiamiento": return t.getFinanciamiento();
            case "situacionLaboral": return t.getSituacionLaboral();
            case "nivelMaximo": return t.getNivelMaximo();
            case "dondeCurso": return t.getDondeCurso();
            case "organizacionPreferida": return t.getOrganizacionPreferida();
            case "tiempoPrimerEmpleo": return t.getTiempoPrimerEmpleo();
            case "numeroEmpleos": return t.getNumeroEmpleos();
            default: return null;
        }
    }

    private double obtenerValorCritico(int gl, double alfa) {
        double[] tabla05 = {
                3.841, 5.991, 7.815, 9.488, 11.070, 12.592, 14.067, 15.507,
                16.919, 18.307, 19.675, 21.026, 22.362, 23.685, 24.996, 26.296,
                27.587, 28.869, 30.144, 31.410, 32.671, 33.924, 35.172, 36.415,
                37.652, 38.885, 40.113, 41.337, 42.557, 43.773
        };
        if (gl <= 0) return 0;
        if (gl <= 30) return tabla05[gl - 1];
        return gl + 1.96 * Math.sqrt(2 * gl);
    }

    public List<String> obtenerVariablesDisponibles() {
        return Arrays.asList(
                "genero", "areaInteres", "financiamiento", "situacionLaboral",
                "nivelMaximo", "dondeCurso", "organizacionPreferida",
                "tiempoPrimerEmpleo", "numeroEmpleos"
        );
    }
}