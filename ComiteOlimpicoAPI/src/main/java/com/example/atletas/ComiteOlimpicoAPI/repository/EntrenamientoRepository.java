package com.example.atletas.ComiteOlimpicoAPI.repository;

import com.example.atletas.ComiteOlimpicoAPI.SesionEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrenamientoRepository extends JpaRepository<SesionEntrenamiento, Integer> {

    List<SesionEntrenamiento> findByAtletaId(int atletaId);
}