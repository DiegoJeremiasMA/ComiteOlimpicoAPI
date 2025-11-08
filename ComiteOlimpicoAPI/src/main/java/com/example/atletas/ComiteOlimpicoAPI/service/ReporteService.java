package com.example.atletas.ComiteOlimpicoAPI.service;

// --- Imports de Entidades y Repositorios ---
import com.example.atletas.ComiteOlimpicoAPI.Atleta;
import com.example.atletas.ComiteOlimpicoAPI.Disciplina;
import com.example.atletas.ComiteOlimpicoAPI.SesionEntrenamiento;
import com.example.atletas.ComiteOlimpicoAPI.repository.AtletaRepository;
import com.example.atletas.ComiteOlimpicoAPI.repository.EntrenamientoRepository;

// --- Imports de Spring ---
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// --- Imports de CSV y Java ---
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import java.io.PrintWriter;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ReporteService {

    private final AtletaRepository atletaRepository;
    private final EntrenamientoRepository entrenamientoRepository;

    @Autowired
    public ReporteService(AtletaRepository atletaRepository, EntrenamientoRepository entrenamientoRepository) {
        this.atletaRepository = atletaRepository;
        this.entrenamientoRepository = entrenamientoRepository;
    }

    /**
     * Genera un reporte CSV y lo escribe directamente en el HttpServletResponse
     * para que el navegador lo descargue.
     * @param response La respuesta HTTP a la que se escribirá el CSV.
     */
    public void generarReporteCompletoCSV(HttpServletResponse response, AtletaRepository atletaRepository, EntrenamientoRepository entrenamientoRepository) throws IOException {

        try (CSVWriter writer = new CSVWriter(new PrintWriter(response.getWriter()))) {

            String[] cabeceras = {
                    "ID_Atleta", "Nombre_Completo", "Edad", "Disciplina", "Unidad_Medida", "Departamento",
                    "ID_Entrenamiento", "Fecha", "Tipo_Entrenamiento", "Marca",
                    "Ubicacion", "Pais"
            };
            writer.writeNext(cabeceras);

            List<Atleta> atletas = atletaRepository.findAll();
            if (atletas.isEmpty()) {
                writer.writeNext(new String[]{"No hay atletas en la base de datos."});
                return;
            }

            for (Atleta atleta : atletas) {
                Disciplina disciplina = Disciplina.fromId(atleta.getDisciplinaId());
                List<SesionEntrenamiento> sesiones = entrenamientoRepository.findByAtletaId(atleta.getId());

                if (sesiones.isEmpty()) {
                    String[] fila = {
                            String.valueOf(atleta.getId()), atleta.getNombreCompleto(), String.valueOf(atleta.getEdad()),
                            disciplina.getNombreMostrado(), disciplina.getUnidad(), atleta.getDepartamento(),
                            "", "", "", "", "", ""
                    };
                    writer.writeNext(fila);
                } else {
                    for (SesionEntrenamiento sesion : sesiones) {
                        String[] fila = {
                                String.valueOf(atleta.getId()),
                                atleta.getNombreCompleto(),
                                String.valueOf(atleta.getEdad()),
                                disciplina.getNombreMostrado(),
                                disciplina.getUnidad(),
                                atleta.getDepartamento(),
                                String.valueOf(sesion.getId()),
                                sesion.getFecha(),
                                sesion.getTipo(),
                                String.valueOf(sesion.getMarca()),
                                sesion.getUbicacion(),
                                sesion.getPais() != null ? sesion.getPais() : ""
                        };
                        writer.writeNext(fila);
                    }
                }
            }
            System.out.println("\n✅ Reporte CSV enviado directamente al navegador.");

        } catch (IOException e) {
            System.out.println("❌ Error al escribir el CSV en el response: " + e.getMessage());
            throw e;
        }
    }
}