package com.example.atletas.ComiteOlimpicoAPI.service;

import com.example.atletas.ComiteOlimpicoAPI.Disciplina;
import com.example.atletas.ComiteOlimpicoAPI.SesionEntrenamiento;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Estadistica {

    /**
     * Calcula el promedio de las marcas de una lista de sesiones.
     */
    public double calcularPromedio(List<SesionEntrenamiento> sesiones) {
        if (sesiones == null || sesiones.isEmpty()) {
            return 0.0;
        }
        double sumaMarcas = 0;
        for (SesionEntrenamiento sesion : sesiones) {
            sumaMarcas += sesion.getMarca();
        }
        return sumaMarcas / sesiones.size();
    }

    /**
     * Encuentra la mejor marca.
     */
    public double encontrarMejorMarca(List<SesionEntrenamiento> sesiones, Disciplina disciplina) {
        if (sesiones == null || sesiones.isEmpty()) {
            return 0.0;
        }

        if (disciplina.isMenorEsMejor()) {
            return sesiones.stream()
                    .min(Comparator.comparing(SesionEntrenamiento::getMarca))
                    .get()
                    .getMarca();
        } else {
            return sesiones.stream()
                    .max(Comparator.comparing(SesionEntrenamiento::getMarca))
                    .get()
                    .getMarca();
        }
    }

    /**
     * Devuelve una lista de sesiones ordenadas por fecha para mostrar la evolución.
     */
    public List<SesionEntrenamiento> obtenerEvolucion(List<SesionEntrenamiento> sesiones) {
        if (sesiones == null) {
            return new ArrayList<>();
        }
        return sesiones.stream()
                .sorted(Comparator.comparing(SesionEntrenamiento::getFecha))
                .collect(Collectors.toList());
    }

    /**
     * Compara el rendimiento nacional vs internacional.
     */
    public void compararRendimientoNacionalVsInternacional(List<SesionEntrenamiento> sesiones) {
        // (Este método ya estaba bien, solo le quitamos 'static')
        List<SesionEntrenamiento> nacionales = sesiones.stream()
                .filter(s -> "Nacional".equalsIgnoreCase(s.getUbicacion()))
                .collect(Collectors.toList());

        List<SesionEntrenamiento> internacionales = sesiones.stream()
                .filter(s -> "Internacional".equalsIgnoreCase(s.getUbicacion()))
                .collect(Collectors.toList());

        System.out.println("Comparativa de Rendimiento:");
        if (!nacionales.isEmpty()) {
            double promedioNacional = calcularPromedio(nacionales);
            System.out.printf(" -> Promedio Nacional (%d sesiones): %.2f\n", nacionales.size(), promedioNacional);
        } else {
            System.out.println(" -> No hay entrenamientos nacionales registrados.");
        }

        if (!internacionales.isEmpty()) {
            double promedioInternacional = calcularPromedio(internacionales);
            System.out.printf(" -> Promedio Internacional (%d sesiones): %.2f\n", internacionales.size(), promedioInternacional);
        } else {
            System.out.println(" -> No hay entrenamientos internacionales registrados.");
        }
    }
}