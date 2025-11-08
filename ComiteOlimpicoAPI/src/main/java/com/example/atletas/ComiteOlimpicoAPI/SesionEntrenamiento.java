package com.example.atletas.ComiteOlimpicoAPI;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "entrenamientos")
public class SesionEntrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fecha;

    @Column(name = "tipo_entrenamiento")
    private String tipo;

    @Column(name = "valor_rendimiento")
    private double marca;

    private String ubicacion;
    private String pais;

    @Column(name = "atleta_id")
    private int atletaId;

    public SesionEntrenamiento() {
    }
}