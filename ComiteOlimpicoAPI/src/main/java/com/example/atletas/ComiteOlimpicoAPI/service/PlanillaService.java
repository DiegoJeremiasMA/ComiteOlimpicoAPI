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

// --- Imports de Java ---
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlanillaService {

    // Definimos las tarifas como constantes
    private static final double PAGO_POR_ENTRENAMIENTO = 100.00;
    private static final double BONO_INTERNACIONAL = 250.00;
    private static final double BONO_POR_RECORD = 500.00;

    private final AtletaRepository atletaRepository;
    private final EntrenamientoRepository entrenamientoRepository;

    @Autowired
    public PlanillaService(AtletaRepository atletaRepository, EntrenamientoRepository entrenamientoRepository) {
        this.atletaRepository = atletaRepository;
        this.entrenamientoRepository = entrenamientoRepository;
    }

    //

    /**
     * Procesa la planilla de un atleta basándose en su ID.
     * @param atletaId El ID del atleta a procesar.
     * @return Un Mapa (que se convertirá en JSON) con el desglose del pago.
     */

    public Map<String, Object> procesarPlanillaDeAtleta(int atletaId) {

        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta no encontrado con id: " + atletaId));

        List<SesionEntrenamiento> sesiones = entrenamientoRepository.findByAtletaId(atletaId);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("atletaNombre", atleta.getNombreCompleto());

        resultado.put("atletaNombre", atleta.getNombreCompleto());
        resultado.put("atletaId", atleta.getId());
        resultado.put("mensaje", null);


        resultado.put("mensaje", null);

        if (sesiones.isEmpty()) {
            resultado.put("mensaje", "No hay entrenamientos para procesar.");
            resultado.put("pagoTotal", 0.0);
            return resultado;
        }

        Disciplina disciplina = Disciplina.fromId(atleta.getDisciplinaId());

        sesiones.sort(Comparator.comparing(SesionEntrenamiento::getFecha));

        double pagoBase = 0;
        double totalBonoInternacional = 0;
        double totalBonoRecord = 0;
        int recordsRotos = 0;
        double mejorMarcaHastaAhora = -1;

        for (int i = 0; i < sesiones.size(); i++) {
            SesionEntrenamiento sesionActual = sesiones.get(i);

            pagoBase += PAGO_POR_ENTRENAMIENTO;

            if ("Internacional".equalsIgnoreCase(sesionActual.getUbicacion())) {
                totalBonoInternacional += BONO_INTERNACIONAL;
            }

            boolean esMejorMarca = false;
            if (i == 0) {
                mejorMarcaHastaAhora = sesionActual.getMarca();
            } else {
                if (disciplina.isMenorEsMejor()) {
                    if (sesionActual.getMarca() < mejorMarcaHastaAhora) esMejorMarca = true;
                } else {
                    if (sesionActual.getMarca() > mejorMarcaHastaAhora) esMejorMarca = true;
                }

                if (esMejorMarca) {
                    totalBonoRecord += BONO_POR_RECORD;
                    recordsRotos++;
                    mejorMarcaHastaAhora = sesionActual.getMarca();
                }
            }
        }

        double pagoTotal = pagoBase + totalBonoInternacional + totalBonoRecord;

        resultado.put("totalEntrenamientos", sesiones.size());
        resultado.put("pagoBase", pagoBase);
        resultado.put("bonoInternacional", totalBonoInternacional);
        resultado.put("recordsRotos", recordsRotos);
        resultado.put("bonoPorRecord", totalBonoRecord);
        resultado.put("pagoTotal", pagoTotal);

        return resultado;
    }
}