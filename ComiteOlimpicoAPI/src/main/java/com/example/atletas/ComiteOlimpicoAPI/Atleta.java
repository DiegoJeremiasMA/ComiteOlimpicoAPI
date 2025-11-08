package com.example.atletas.ComiteOlimpicoAPI;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "atletas")
public class Atleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombreCompleto;
    private int edad;
    private String departamento;
    private String nacionalidad;
    private String fechaIngreso;

    @Column(name = "disciplina_id")
    private int disciplinaId;

    @OneToMany
    @JoinColumn(name = "atleta_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<SesionEntrenamiento> sesiones;

    public Atleta() {
    }
}