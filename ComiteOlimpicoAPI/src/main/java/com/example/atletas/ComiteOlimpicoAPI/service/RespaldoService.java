package com.example.atletas.ComiteOlimpicoAPI.service;

// --- Imports de Entidades y Repositorios ---
import com.example.atletas.ComiteOlimpicoAPI.Atleta;
import com.example.atletas.ComiteOlimpicoAPI.SesionEntrenamiento;
import com.example.atletas.ComiteOlimpicoAPI.repository.AtletaRepository;
import com.example.atletas.ComiteOlimpicoAPI.repository.EntrenamientoRepository;

// --- Imports de Spring ---
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// --- Imports de Gson y Java ---
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;

@Service
public class RespaldoService {

    private final AtletaRepository atletaRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final Gson gson;

    @Autowired
    public RespaldoService(AtletaRepository atletaRepository, EntrenamientoRepository entrenamientoRepository) {
        this.atletaRepository = atletaRepository;
        this.entrenamientoRepository = entrenamientoRepository;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Crea un respaldo JSON de todos los atletas y sus entrenamientos.
     * @return El nombre del archivo generado.
     */
    public String crearRespaldoJSON() throws IOException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formato);
        String nombreArchivo = "respaldo_atletas_" + timestamp + ".json";

        System.out.println("\n--- Creando respaldo en '" + nombreArchivo + "' ---");

        List<Atleta> atletas = atletaRepository.findAll();
        if (atletas.isEmpty()) {
            System.out.println("No hay atletas para respaldar.");
            return null;
        }

        for (Atleta atleta : atletas) {
            atleta.setSesiones(entrenamientoRepository.findByAtletaId(atleta.getId()));
        }

        try (Writer writer = new FileWriter(nombreArchivo)) {
            gson.toJson(atletas, writer);
        }

        System.out.println("✅ Respaldo creado con éxito.");
        return nombreArchivo;
    }

    /**
     * Lista todos los archivos de respaldo .json disponibles.
     */
    public List<String> listarRespaldosDisponibles() {
        File directorio = new File(".");
        FilenameFilter filtro = (dir, name) -> name.startsWith("respaldo_atletas_") && name.endsWith(".json");
        String[] archivos = directorio.list(filtro);
        if (archivos == null) return new ArrayList<>();
        return Arrays.stream(archivos).collect(Collectors.toList());
    }

    /**
     * Restaura la base de datos desde un archivo JSON.
     * Esta es una operación destructiva.
     * @param nombreArchivo El nombre del archivo a restaurar.
     */

    @Transactional
    public void restaurarDesdeJSON(String nombreArchivo) throws IOException {
        System.out.println("\n--- Restaurando desde el respaldo '" + nombreArchivo + "' ---");

        List<Atleta> atletasDelRespaldo;
        try (Reader reader = new FileReader(nombreArchivo)) {
            atletasDelRespaldo = gson.fromJson(reader, new TypeToken<List<Atleta>>() {}.getType());
        }

        if (atletasDelRespaldo == null || atletasDelRespaldo.isEmpty()) {
            System.out.println("El archivo de respaldo está vacío o no se pudo leer.");
            return;
        }

        entrenamientoRepository.deleteAll();
        atletaRepository.deleteAll();

        System.out.println("Base de datos limpiada. Insertando datos del respaldo...");

        for (Atleta atleta : atletasDelRespaldo) {

            atleta.setId(0);

            Atleta atletaGuardado = atletaRepository.save(atleta);

            if (atleta.getSesiones() != null) {
                for (SesionEntrenamiento sesion : atleta.getSesiones()) {

                    sesion.setId(0);

                    sesion.setAtletaId(atletaGuardado.getId());
                    entrenamientoRepository.save(sesion);
                }
            }
        }
        System.out.println("✅ Restauración completada con éxito.");
    }
    public void eliminarRespaldo(String nombreArchivo) {
        File archivo = new File(nombreArchivo);

        if (archivo.exists()) {
            if (archivo.delete()) {
                System.out.println("✅ Respaldo eliminado con éxito: " + nombreArchivo);
            } else {
                System.out.println("❌ No se pudo eliminar el respaldo: " + nombreArchivo);
            }
        } else {
            System.out.println("El respaldo a eliminar no existe (es posible que ya haya sido borrado): " + nombreArchivo);
        }
    }
}