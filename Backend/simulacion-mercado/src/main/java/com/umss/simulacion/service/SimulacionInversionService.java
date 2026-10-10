package com.umss.simulacion.service;

import com.umss.simulacion.service.SimulacionService.GeneradorCongruencial;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Servicio de Simulación de Inversión (TIR / VPN)
 * Determina si vale la pena estudiar un posgrado.
 */
@Service
public class SimulacionInversionService {

    // ============================================================
    // CLASE INTERNA: Resultado de la simulación de inversión
    // ============================================================
    public static class ResultadoInversion {
        public int nSimulaciones;
        public double tremaPorcentaje;
        public double inversionPromedio;
        public double flujoPromedio;
        public double tirPromedio;
        public double tirMinima;
        public double tirMaxima;
        public double tirMediana;
        public double probabilidadExito;
        public double vpnPromedio;
        public boolean recomendacion;
        public List<Double> tirs;
        public List<Integer> histograma;
        public List<String> etiquetasHistograma;

        public int getNSimulaciones() { return nSimulaciones; }
        public double getTremaPorcentaje() { return tremaPorcentaje; }
        public double getInversionPromedio() { return inversionPromedio; }
        public double getFlujoPromedio() { return flujoPromedio; }
        public double getTirPromedio() { return tirPromedio; }
        public double getTirMinima() { return tirMinima; }
        public double getTirMaxima() { return tirMaxima; }
        public double getTirMediana() { return tirMediana; }
        public double getProbabilidadExito() { return probabilidadExito; }
        public double getVpnPromedio() { return vpnPromedio; }
        public boolean isRecomendacion() { return recomendacion; }
        public List<Double> getTirs() { return tirs; }
        public List<Integer> getHistograma() { return histograma; }
        public List<String> getEtiquetasHistograma() { return etiquetasHistograma; }
    }

    // ============================================================
    // SIMULACIÓN PRINCIPAL
    // ============================================================
    public ResultadoInversion simular(int nSimulaciones, double trema) {
        GeneradorCongruencial gen = new GeneradorCongruencial(5, 7, 8, 4);

        List<Double> tirs = new ArrayList<>();
        List<Double> inversiones = new ArrayList<>();
        List<Double> flujos = new ArrayList<>();
        List<Double> vpns = new ArrayList<>();
        int exitos = 0;

        for (int sim = 0; sim < nSimulaciones; sim++) {
            // 1. Inversión inicial (triangular: 5000, 8000, 12000)
            double I0 = triangular(gen, 5000, 8000, 12000);
            inversiones.add(I0);

            // 2. Flujo anual adicional por tener posgrado (triangular: 2000, 5000, 8000)
            double F = triangular(gen, 2000, 5000, 8000);
            flujos.add(F);

            // 3. Horizonte: 5 años
            int horizonte = 5;

            // 4. Calcular TIR
            double tir = calcularTIR(I0, F, horizonte);
            tirs.add(tir);

            // 5. Calcular VPN con TREMA
            double vpn = calcularVPN(I0, F, horizonte, trema);
            vpns.add(vpn);

            if (tir > trema) {
                exitos++;
            }
        }

        // Estadísticas de TIR
        double tirProm = tirs.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double tirMin = tirs.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double tirMax = tirs.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        List<Double> tirsOrdenados = new ArrayList<>(tirs);
        Collections.sort(tirsOrdenados);
        double tirMediana = tirsOrdenados.get(tirsOrdenados.size() / 2);

        double invProm = inversiones.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double fluProm = flujos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double vpnProm = vpns.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        double probExito = (double) exitos / nSimulaciones;

        // Histograma de TIR
        int numBins = 10;
        double rango = tirMax - tirMin;
        double binSize = rango > 0 ? rango / numBins : 1;

        List<Integer> histograma = new ArrayList<>(Collections.nCopies(numBins, 0));
        List<String> etiquetas = new ArrayList<>();
        for (int i = 0; i < numBins; i++) {
            double inf = tirMin + i * binSize;
            double sup = inf + binSize;
            etiquetas.add(String.format("%.0f%% - %.0f%%", inf * 100, sup * 100));
        }

        for (double t : tirs) {
            int bin = (int) ((t - tirMin) / binSize);
            if (bin >= numBins) bin = numBins - 1;
            if (bin < 0) bin = 0;
            histograma.set(bin, histograma.get(bin) + 1);
        }

        // Armar resultado
        ResultadoInversion r = new ResultadoInversion();
        r.nSimulaciones = nSimulaciones;
        r.tremaPorcentaje = trema * 100;
        r.inversionPromedio = Math.round(invProm * 100.0) / 100.0;
        r.flujoPromedio = Math.round(fluProm * 100.0) / 100.0;
        r.tirPromedio = Math.round(tirProm * 10000.0) / 100.0;
        r.tirMinima = Math.round(tirMin * 10000.0) / 100.0;
        r.tirMaxima = Math.round(tirMax * 10000.0) / 100.0;
        r.tirMediana = Math.round(tirMediana * 10000.0) / 100.0;
        r.probabilidadExito = Math.round(probExito * 10000.0) / 100.0;
        r.vpnPromedio = Math.round(vpnProm * 100.0) / 100.0;
        r.recomendacion = probExito >= 0.90; // Si P(TIR>TREMA) >= 90%
        r.tirs = tirs;
        r.histograma = histograma;
        r.etiquetasHistograma = etiquetas;

        return r;
    }

    // ============================================================
    // CÁLCULO DE TIR POR BISECCIÓN
    // ============================================================
    private double calcularTIR(double I0, double F, int años) {
        double tirMin = -0.99;
        double tirMax = 5.0;
        double precision = 0.0001;

        for (int i = 0; i < 100; i++) {
            double tirMedio = (tirMin + tirMax) / 2;
            double vpn = calcularVPN(I0, F, años, tirMedio);

            if (vpn > 0) {
                tirMin = tirMedio;
            } else {
                tirMax = tirMedio;
            }

            if (Math.abs(tirMax - tirMin) < precision) break;
        }

        return (tirMin + tirMax) / 2;
    }

    // ============================================================
    // CÁLCULO DE VPN
    // ============================================================
    private double calcularVPN(double I0, double F, int años, double tasa) {
        double vpn = -I0;
        for (int t = 1; t <= años; t++) {
            vpn += F / Math.pow(1 + tasa, t);
        }
        return vpn;
    }

    // ============================================================
    // DISTRIBUCIÓN TRIANGULAR
    // ============================================================
    private double triangular(GeneradorCongruencial gen, double a, double b, double c) {
        double r = gen.next();
        if (r < (b - a) / (c - a)) {
            return a + Math.sqrt(r * (b - a) * (c - a));
        } else {
            return c - Math.sqrt((1 - r) * (c - a) * (c - b));
        }
    }
}
