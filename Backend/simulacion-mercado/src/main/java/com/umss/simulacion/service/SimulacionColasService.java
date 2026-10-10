package com.umss.simulacion.service;

import com.umss.simulacion.service.SimulacionService.GeneradorCongruencial;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Servicio de Simulación de Colas
 * Determina el número óptimo de servidores para atención a titulados.
 */
@Service
public class SimulacionColasService {

    // ============================================================
    // CLASE INTERNA: Resultado de la simulación de colas
    // ============================================================
    public static class ResultadoColas {
        public int numServidores;
        public int numClientes;
        public double lambdaLlegadas;
        public double tiempoServicioMin;
        public double tiempoServicioMax;
        public double tiempoEsperaPromedio;
        public double tiempoEsperaMaximo;
        public double tiempoEnSistemaPromedio;
        public double colaPromedio;
        public double colaMaxima;
        public double utilizacion;
        public double tiempoTotalSimulado;
        public int clientesAtendidos;

        public int getNumServidores() { return numServidores; }
        public int getNumClientes() { return numClientes; }
        public double getLambdaLlegadas() { return lambdaLlegadas; }
        public double getTiempoServicioMin() { return tiempoServicioMin; }
        public double getTiempoServicioMax() { return tiempoServicioMax; }
        public double getTiempoEsperaPromedio() { return tiempoEsperaPromedio; }
        public double getTiempoEsperaMaximo() { return tiempoEsperaMaximo; }
        public double getTiempoEnSistemaPromedio() { return tiempoEnSistemaPromedio; }
        public double getColaPromedio() { return colaPromedio; }
        public double getColaMaxima() { return colaMaxima; }
        public double getUtilizacion() { return utilizacion; }
        public double getTiempoTotalSimulado() { return tiempoTotalSimulado; }
        public int getClientesAtendidos() { return clientesAtendidos; }
    }

    // ============================================================
    // SIMULACIÓN DE COLAS - MÚLTIPLES SERVIDORES
    // ============================================================
    public ResultadoColas simular(int numServidores, int numClientes,
                                   double lambda, double minServicio, double maxServicio) {
        GeneradorCongruencial gen = new GeneradorCongruencial(5, 7, 8, 4);

        // Arreglos para almacenar datos de cada cliente
        double[] tiempoLlegada = new double[numClientes];
        double[] tiempoServicio = new double[numClientes];
        double[] tiempoInicioServicio = new double[numClientes];
        double[] tiempoFinServicio = new double[numClientes];
        double[] tiempoEspera = new double[numClientes];

        // 1. Generar tiempos de llegada (exponencial con media 1/lambda)
        double tiempoAcumulado = 0;
        for (int i = 0; i < numClientes; i++) {
            double tiempoEntreLlegadas = -Math.log(1 - gen.next()) / lambda;
            tiempoAcumulado += tiempoEntreLlegadas;
            tiempoLlegada[i] = tiempoAcumulado;

            // Tiempo de servicio uniforme [min, max]
            tiempoServicio[i] = minServicio + (maxServicio - minServicio) * gen.next();
        }

        // 2. Simular servidores
        double[] servidorLibreEn = new double[numServidores];
        // (todos empiezan libres en tiempo 0)

        double sumaEsperas = 0;
        double maxEspera = 0;
        double sumaTiempoEnSistema = 0;
        int colaMax = 0;
        double sumaCola = 0;

        for (int i = 0; i < numClientes; i++) {
            // Encontrar el servidor que se libera primero
            int servidorElegido = 0;
            double menorTiempo = servidorLibreEn[0];
            for (int s = 1; s < numServidores; s++) {
                if (servidorLibreEn[s] < menorTiempo) {
                    menorTiempo = servidorLibreEn[s];
                    servidorElegido = s;
                }
            }

            // El cliente es atendido cuando el servidor esté libre O cuando llegue
            double inicioServicio = Math.max(tiempoLlegada[i], servidorLibreEn[servidorElegido]);
            double espera = inicioServicio - tiempoLlegada[i];

            tiempoInicioServicio[i] = inicioServicio;
            tiempoFinServicio[i] = inicioServicio + tiempoServicio[i];
            tiempoEspera[i] = espera;

            // Actualizar el servidor
            servidorLibreEn[servidorElegido] = tiempoFinServicio[i];

            // Acumular estadísticas
            sumaEsperas += espera;
            if (espera > maxEspera) maxEspera = espera;
            sumaTiempoEnSistema += espera + tiempoServicio[i];

            // Calcular cola en este instante
            int colaActual = 0;
            for (int j = 0; j < i; j++) {
                if (tiempoFinServicio[j] > tiempoLlegada[i]) colaActual++;
            }
            colaActual = Math.max(0, colaActual - numServidores);
            if (colaActual > colaMax) colaMax = colaActual;
            sumaCola += colaActual;
        }

        // 3. Calcular estadísticas finales
        double esperaPromedio = sumaEsperas / numClientes;
        double tiempoSistemaPromedio = sumaTiempoEnSistema / numClientes;
        double colaPromedio = sumaCola / numClientes;
        double tiempoTotal = tiempoFinServicio[numClientes - 1];

        // Utilización: tiempo total ocupado / (numServidores * tiempo total)
        double tiempoTotalServicio = 0;
        for (int i = 0; i < numClientes; i++) {
            tiempoTotalServicio += tiempoServicio[i];
        }
        double utilizacion = (tiempoTotalServicio / (numServidores * tiempoTotal)) * 100;
        if (utilizacion > 100) utilizacion = 100;

        // 4. Armar resultado
        ResultadoColas r = new ResultadoColas();
        r.numServidores = numServidores;
        r.numClientes = numClientes;
        r.lambdaLlegadas = lambda;
        r.tiempoServicioMin = minServicio;
        r.tiempoServicioMax = maxServicio;
        r.tiempoEsperaPromedio = Math.round(esperaPromedio * 100.0) / 100.0;
        r.tiempoEsperaMaximo = Math.round(maxEspera * 100.0) / 100.0;
        r.tiempoEnSistemaPromedio = Math.round(tiempoSistemaPromedio * 100.0) / 100.0;
        r.colaPromedio = Math.round(colaPromedio * 100.0) / 100.0;
        r.colaMaxima = colaMax;
        r.utilizacion = Math.round(utilizacion * 100.0) / 100.0;
        r.tiempoTotalSimulado = Math.round(tiempoTotal * 100.0) / 100.0;
        r.clientesAtendidos = numClientes;

        return r;
    }

    // ============================================================
    // COMPARAR MÚLTIPLES ESCENARIOS
    // ============================================================
    public List<ResultadoColas> compararEscenarios(int[] numServidoresList, int numClientes,
         double lambda, double minServicio, double maxServicio) {
        List<ResultadoColas> resultados = new ArrayList<>();
        for (int numServidores : numServidoresList) {
            resultados.add(simular(numServidores, numClientes, lambda, minServicio, maxServicio));
        }
        return resultados;
    }
}