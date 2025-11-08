package com.example.atletas.ComiteOlimpicoAPI.controller;

// --- Imports de Entidades y Repositorios ---
import com.example.atletas.ComiteOlimpicoAPI.Atleta;
import com.example.atletas.ComiteOlimpicoAPI.SesionEntrenamiento;
import com.example.atletas.ComiteOlimpicoAPI.repository.AtletaRepository;
import com.example.atletas.ComiteOlimpicoAPI.repository.EntrenamientoRepository;

// --- Imports de Spring (Framework) ---
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.List;

// --- Imports de Spring (Web) ---
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.atletas.ComiteOlimpicoAPI.Disciplina;
import com.example.atletas.ComiteOlimpicoAPI.service.Estadistica;
import com.example.atletas.ComiteOlimpicoAPI.service.PlanillaService;
import java.util.Map;
@Controller
@RequestMapping("/api/atletas")
public class AtletaController {

    // Insertamos los repositorios que necesitamos
    private final AtletaRepository atletaRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final Estadistica estadisticaService;
    private final PlanillaService planillaService;

    @Autowired
    public AtletaController(AtletaRepository atletaRepository,
                            EntrenamientoRepository entrenamientoRepository,
                            Estadistica estadisticaService,
                            PlanillaService planillaService) {
        this.atletaRepository = atletaRepository;
        this.entrenamientoRepository = entrenamientoRepository;
        this.estadisticaService = estadisticaService;
        this.planillaService = planillaService;
    }

    /**
     * Muestra la PÁGINA WEB con la lista de atletas.
     * URL: GET http://localhost:8080/api/atletas/lista
     */
    @GetMapping("/lista")
    public String mostrarListaDeAtletas(Model model) {
        List<Atleta> listaDeAtletas = atletaRepository.findAll();
        model.addAttribute("atletas", listaDeAtletas);
        return "atletas"; // Renderiza atletas.html
    }

    /**
     * Muestra el formulario vacío para crear un nuevo atleta.
     * URL: GET http://localhost:8080/api/atletas/nuevo
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioDeAtleta(Model model) {
        model.addAttribute("atleta", new Atleta());
        return "form-atleta";
    }

    /**
     * Muestra el formulario pre-llenado para editar un atleta existente.
     * URL: GET http://localhost:8080/api/atletas/editar/1
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEdicion(@PathVariable int id, Model model) {
        Atleta atleta = atletaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Atleta no válido:" + id));
        model.addAttribute("atleta", atleta);
        return "form-atleta";
    }

    /**
     * Procesa el formulario para GUARDAR (ya sea crear uno nuevo o actualizar uno existente).
     * URL: POST http://localhost:8080/api/atletas/guardar
     */
    @PostMapping("/guardar")
    public String guardarAtleta(@ModelAttribute("atleta") Atleta atleta) {
        atletaRepository.save(atleta);
        return "redirect:/api/atletas/lista";
    }

    /**
     * Elimina un atleta por su ID.
     * URL: GET http://localhost:8080/api/atletas/eliminar/1
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarAtleta(@PathVariable int id) {
        atletaRepository.deleteById(id);

        return "redirect:/api/atletas/lista";
    }
    /**
     * Muestra la página de detalles, el historial y las estadisticas de un solo atleta.
     * URL: GET http://localhost:8080/api/atletas/historial/1
     */
    @GetMapping("/historial/{id}")
    public String mostrarHistorialDeAtleta(@PathVariable int id, Model model) {
        Atleta atleta = atletaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Atleta no válido:" + id));

        List<SesionEntrenamiento> sesiones = entrenamientoRepository.findByAtletaId(id);

        Disciplina disciplina = Disciplina.fromId(atleta.getDisciplinaId());

        if (!sesiones.isEmpty()) {
            double promedio = estadisticaService.calcularPromedio(sesiones);
            double mejorMarca = estadisticaService.encontrarMejorMarca(sesiones, disciplina);

            double promNacional = sesiones.stream()
                    .filter(s -> "Nacional".equalsIgnoreCase(s.getUbicacion()))
                    .mapToDouble(SesionEntrenamiento::getMarca).average().orElse(0.0);
            double promInternacional = sesiones.stream()
                    .filter(s -> "Internacional".equalsIgnoreCase(s.getUbicacion()))
                    .mapToDouble(SesionEntrenamiento::getMarca).average().orElse(0.0);

            model.addAttribute("promedio", promedio);
            model.addAttribute("mejorMarca", mejorMarca);
            model.addAttribute("promNacional", promNacional);
            model.addAttribute("promInternacional", promInternacional);
        }

        model.addAttribute("atleta", atleta);
        model.addAttribute("sesiones", sesiones);
        model.addAttribute("disciplina", disciplina);

        return "historial-atleta";
    }

    /**
     * Muestra el formulario vacío para registrar un nuevo entrenamiento.
     * Pasa el ID del atleta al formulario.
     * URL: GET http://localhost:8080/api/atletas/historial/1/nuevo-entrenamiento
     */
    @GetMapping("/historial/{atletaId}/nuevo-entrenamiento")
    public String mostrarFormularioEntrenamiento(@PathVariable int atletaId, Model model) {

        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new IllegalArgumentException("ID de Atleta no válido:" + atletaId));

        SesionEntrenamiento sesion = new SesionEntrenamiento();
        sesion.setAtletaId(atletaId);

        model.addAttribute("sesion", sesion);
        model.addAttribute("atletaNombre", atleta.getNombreCompleto());

        return "form-entrenamiento";
    }

    /**
     * Procesa el formulario para guardar la nueva sesión de entrenamiento.
     * URL: POST http://localhost:8080/api/atletas/historial/guardar-entrenamiento
     */
    @PostMapping("/historial/guardar-entrenamiento")
    public String guardarEntrenamiento(@ModelAttribute("sesion") SesionEntrenamiento sesion) {

        entrenamientoRepository.save(sesion);

        return "redirect:/api/atletas/historial/" + sesion.getAtletaId();
    }
    @GetMapping("/historial/eliminar-entrenamiento/{id}")
    public String eliminarEntrenamiento(@PathVariable int id) {
        SesionEntrenamiento sesion = entrenamientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Sesión no válido:" + id));

        entrenamientoRepository.deleteById(id);

        return "redirect:/api/atletas/historial/" + sesion.getAtletaId();
    }
    @GetMapping("/historial/editar-entrenamiento/{id}")
    public String mostrarFormularioEdicionEntrenamiento(@PathVariable int id, Model model) {
        SesionEntrenamiento sesion = entrenamientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Sesión no válido:" + id));

        Atleta atleta = atletaRepository.findById(sesion.getAtletaId())
                .orElseThrow(() -> new IllegalArgumentException("ID de Atleta no válido:" + sesion.getAtletaId()));

        model.addAttribute("sesion", sesion);
        model.addAttribute("atletaNombre", atleta.getNombreCompleto());

        return "form-entrenamiento";
    }
    @GetMapping("/planilla/{id}")
    public String mostrarPlanillaDeAtleta(@PathVariable int id, Model model) {
        Map<String, Object> resultadoPlanilla = planillaService.procesarPlanillaDeAtleta(id);

        model.addAttribute("resultado", resultadoPlanilla);

        return "planilla-atleta";
    }
}