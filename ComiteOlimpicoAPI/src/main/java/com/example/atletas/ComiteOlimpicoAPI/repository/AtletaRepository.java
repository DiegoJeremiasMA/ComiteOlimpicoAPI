package com.example.atletas.ComiteOlimpicoAPI.repository;

import com.example.atletas.ComiteOlimpicoAPI.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Le dice a Spring que esta es una interfaz de acceso a datos
public interface AtletaRepository extends JpaRepository<Atleta, Integer> {
}