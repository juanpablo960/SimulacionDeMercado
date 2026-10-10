package com.umss.simulacion.service;

import com.umss.simulacion.model.Titulado;
import com.umss.simulacion.repository.TituladoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que implementa técnicas de simulación de sistemas:
 * - Generador Congruencial Mixto (números pseudoaleatorios)
 * - Simulación Monte Carlo (proyección de demanda)
 * - Cálculo de intervalos de confianza
 */
@Service
public class SimulacionService {

    @Autowired
    private TituladoRepository tituladoRepository;

    // ============================================================
    // GENERADOR CONGRUENCIAL MIXTO
    // Fórmula: X_{n+1} = (a * X_n + c) mod m
    // ============================================================
    public static class GeneradorCongruencial {
        private long a, c, m, xn;

        public GeneradorCongruencial(long a, long c, long m, long semilla) {
            this.a = a;
            this.c = c;
            this.m = m;
            this.xn = semilla;
        }

        public double next() {
            xn = (a * xn + c) % m;
            return (double) xn / m;
        }

        public boolean verificarPeriodoCompleto() {
            if (mcd(c, m) != 1) return false;
            for (long p = 2; p <= m; p++) {
                if (esPrimo(p) && m % p == 0 && (a - 1) % p != 0) return false;
            }
            if (m % 4 == 0 && (a - 1) % 4 != 0) return false;
            return true;
        }

        private long mcd(long a, long b) {
            while (b != 0) { long t = b; b = a % b; a = t; }
            return a;
        }

        private boolean esPrimo(long n) {
            if (n < 2) return false;
            for (long i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) return false;
            }
            return true;
        }
    }

    // ============================================================
    // CLASE INTERNA: Resultado
    // ============================================================
    public static class ResultadoSimulacion {
        public int nSimulaciones;
        public int nTitulados;
        public double probabilidadPosgrado;
        public double demandaPromedio;
        public double desviacionEstandar;
        public double demandaMinima;
        public double demandaMaxima;
        public double icInferior;
        public double icSuperior;
        public List<Integer> histograma;
        public List<String> etiquetasHistograma;
        public Map<String, Integer> demandaPorArea;

        public int getNSimulaciones() { return nSimulaciones; }
        public int getNTitulados() { return nTitulados; }
        public double getProbabilidadPosgrado() { return probabilidadPosgrado; }
        public double getDemandaPromedio() { return demandaPromedio; }
        public double getDesviacionEstandar() { return desviacionEstandar; }
        public double getDemandaMinima() { return demandaMinima; }
        public double getDemandaMaxima() { return demandaMaxima; }
        public double getIcInferior() { return icInferior; }
        public double getIcSuperior() { return icSuperior; }
        public List<Integer> getHistograma() { return histograma; }
        public List<String> getEtiquetasHistograma() { return etiquetasHistograma; }
        public Map<String, Integer> getDemandaPorArea() { return demandaPorArea; }
    }

    // ============================================================
    // SIMULACIÓN MONTE CARLO
    // ============================================================
    public ResultadoSimulacion simularDemandaPosgrados(int nSimulaciones) {
        List<Titulado> titulados = tituladoRepository.findAll();
        int nTitulados = titulados.size();

        long interesados = titulados.stream()
                .filter(t -> Boolean.TRUE.equals(t.getInteresadoPosgrado()))
                .count();
        double probPosgrado = nTitulados > 0 ? (double) interesados / nTitulados : 0.6;

        Map<String, Long> areasCount = titulados.stream()
                .filter(t -> t.getAreaInteres() != null && !t.getAreaInteres().isEmpty())
                .collect(Collectors.groupingBy(Titulado::getAreaInteres, Collectors.counting()));
        long totalAreas = areasCount.values().stream().mapToLong(Long::longValue).sum();

        GeneradorCongruencial gen = new GeneradorCongruencial(5, 7, 8, 4);

        List<Integer> demandas = new ArrayList<>();
        Map<String, Integer> demandaPorArea = new HashMap<>();

        for (int sim = 0; sim < nSimulaciones; sim++) {
            int demanda = 0;
            for (int i = 0; i < nTitulados; i++) {
                if (gen.next() < probPosgrado) {
                    demanda++;
                    if (totalAreas > 0) {
                        double r = gen.next();
                        double acumulado = 0;
                        for (Map.Entry<String, Long> entry : areasCount.entrySet()) {
                            acumulado += (double) entry.getValue() / totalAreas;
                            if (r <= acumulado) {
                                demandaPorArea.merge(entry.getKey(), 1, Integer::sum);
                                break;
                            }
                        }
                    }
                }
            }
            demandas.add(demanda);
        }

        double media = demandas.stream().mapToInt(Integer::intValue).average().orElse(0);
        double sumaCuadrados = demandas.stream()
                .mapToDouble(d -> Math.pow(d - media, 2))
                .sum();
        double desvEst = Math.sqrt(sumaCuadrados / (nSimulaciones - 1));
        int min = demandas.stream().mapToInt(Integer::intValue).min().orElse(0);
        int max = demandas.stream().mapToInt(Integer::intValue).max().orElse(0);

        double errorEstandar = desvEst / Math.sqrt(nSimulaciones);
        double icInf = media - 1.96 * errorEstandar;
        double icSup = media + 1.96 * errorEstandar;

        int numBins = 10;
        int binSize = Math.max(1, (max - min + 1) / numBins);
        List<Integer> histograma = new ArrayList<>(Collections.nCopies(numBins, 0));
        List<String> etiquetas = new ArrayList<>();
        for (int i = 0; i < numBins; i++) {
            etiquetas.add((min + i * binSize) + "-" + (min + (i + 1) * binSize - 1));
        }

        for (int d : demandas) {
            int bin = Math.min((d - min) / binSize, numBins - 1);
            histograma.set(bin, histograma.get(bin) + 1);
        }

        ResultadoSimulacion r = new ResultadoSimulacion();
        r.nSimulaciones = nSimulaciones;
        r.nTitulados = nTitulados;
        r.probabilidadPosgrado = Math.round(probPosgrado * 10000.0) / 100.0;
        r.demandaPromedio = Math.round(media * 100.0) / 100.0;
        r.desviacionEstandar = Math.round(desvEst * 100.0) / 100.0;
        r.demandaMinima = min;
        r.demandaMaxima = max;
        r.icInferior = Math.round(icInf * 100.0) / 100.0;
        r.icSuperior = Math.round(icSup * 100.0) / 100.0;
        r.histograma = histograma;
        r.etiquetasHistograma = etiquetas;
        r.demandaPorArea = demandaPorArea;

        return r;
    }

    // ============================================================
    // INFO DEL GENERADOR
    // ============================================================
    public Map<String, Object> infoGenerador() {
        GeneradorCongruencial gen = new GeneradorCongruencial(5, 7, 8, 4);
        Map<String, Object> info = new HashMap<>();
        info.put("a", 5);
        info.put("c", 7);
        info.put("m", 8);
        info.put("semilla", 4);
        info.put("periodoCompleto", gen.verificarPeriodoCompleto());

        List<Double> numeros = new ArrayList<>();
        GeneradorCongruencial gen2 = new GeneradorCongruencial(5, 7, 8, 4);
        for (int i = 0; i < 20; i++) {
            numeros.add(Math.round(gen2.next() * 10000.0) / 10000.0);
        }
        info.put("primeros20", numeros);

        return info;
    }
}