package com.example.atletas.ComiteOlimpicoAPI.controller;

// --- Imports de Servicios ---
import com.example.atletas.ComiteOlimpicoAPI.SesionEntrenamiento;
import com.example.atletas.ComiteOlimpicoAPI.repository.EntrenamientoRepository;
import com.example.atletas.ComiteOlimpicoAPI.service.Estadistica;
import com.example.atletas.ComiteOlimpicoAPI.service.PlanillaService;
import com.example.atletas.ComiteOlimpicoAPI.service.ReporteService;
import com.example.atletas.ComiteOlimpicoAPI.service.RespaldoService;

// --- Imports de Spring ---
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import com.example.atletas.ComiteOlimpicoAPI.repository.AtletaRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/api/servicios") // URL base para todos los servicios
public class ServiciosController {

    // Insertamos todos los servicios y repositorios que necesitamos
    private final Estadistica estadisticaService;
    private final PlanillaService planillaService;
    private final ReporteService reporteService;
    private final RespaldoService respaldoService;
    private final EntrenamientoRepository entrenamientoRepository;
    private final AtletaRepository atletaRepository;

    @Autowired
    public ServiciosController(Estadistica estadisticaService, PlanillaService planillaService,
                               ReporteService reporteService, RespaldoService respaldoService,
                               EntrenamientoRepository entrenamientoRepository,
                               AtletaRepository atletaRepository) {
        this.estadisticaService = estadisticaService;
        this.planillaService = planillaService;
        this.reporteService = reporteService;
        this.respaldoService = respaldoService;
        this.entrenamientoRepository = entrenamientoRepository;
        this.atletaRepository = atletaRepository;
    }

    /**
     * Reemplaza Opción 7: "Calcular y mostrar estadísticas"
     * URL: GET http://localhost:8080/api/servicios/estadisticas/1
     */
    @GetMapping("/estadisticas/{atletaId}")
    public Map<String, Object> obtenerEstadisticas(@PathVariable int atletaId) {
        List<SesionEntrenamiento> sesiones = entrenamientoRepository.findByAtletaId(atletaId);
        return Map.of("mensaje", "Estadísticas para el atleta " + atletaId);
    }

    /**
     * Reemplaza Opción 8: "Procesar planilla de pagos"
     * URL: POST http://localhost:8080/api/servicios/planilla/1
     */
    @PostMapping("/planilla/{atletaId}")
    public Map<String, Object> procesarPlanilla(@PathVariable int atletaId) {
        return planillaService.procesarPlanillaDeAtleta(atletaId);
    }

    /**
     * "Generar reporte de atletas en CSV"
     * URL: GET http://localhost:8080/api/servicios/reportes/csv
     */
    @GetMapping("/reportes/csv")
    public void generarReporte(HttpServletResponse response) {
        String nombreArchivo = "reporte_entrenamientos_" + LocalDate.now() + ".csv";

        response.setContentType("text/csv");

        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

        try {
            reporteService.generarReporteCompletoCSV(response, atletaRepository, entrenamientoRepository);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println("Error al generar reporte CSV: " + e.getMessage());
        }
    }

    /**
     *"Crear Respaldo en JSON"
     * URL: POST http://localhost:8080/api/servicios/respaldos/crear
     */
    @PostMapping("/respaldos/crear")
    public String crearRespaldo() {
        try {
            respaldoService.crearRespaldoJSON();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/api/servicios/admin";
    }

    /**
     *función para listar respaldos
     * URL: GET http://localhost:8080/api/servicios/respaldos/listar
     */
    @GetMapping("/respaldos/listar")
    public List<String> listarRespaldos() {
        return respaldoService.listarRespaldosDisponibles();
    }

    /**
     *"Restaurar Respaldo desde JSON"
     * URL: POST http://localhost:8080/api/servicios/respaldos/restaurar
     */
    @PostMapping("/respaldos/restaurar")
    public String restaurarRespaldo(@RequestParam("nombreArchivo") String nombreArchivo) {
        try {
            respaldoService.restaurarDesdeJSON(nombreArchivo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/api/servicios/admin";
    }
    @GetMapping("/admin")
    public String mostrarPaginaAdmin(Model model) {
        List<String> respaldos = respaldoService.listarRespaldosDisponibles();

        model.addAttribute("respaldos", respaldos);

        return "admin";
    }
    @PostMapping("/respaldos/eliminar")
    public String eliminarRespaldo(@RequestParam("nombreArchivo") String nombreArchivo) {
        try {
            respaldoService.eliminarRespaldo(nombreArchivo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/api/servicios/admin";
    }
}