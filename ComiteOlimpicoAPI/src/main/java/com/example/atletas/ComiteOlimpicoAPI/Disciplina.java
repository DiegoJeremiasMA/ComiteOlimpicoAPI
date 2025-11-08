package com.example.atletas.ComiteOlimpicoAPI;

public enum Disciplina {
    // Añadimos el ID de la base de datos como primer parámetro
    ATLETISMO(1, "Atletismo", "metros", false),
    NATACION(2, "Natación", "segundos", true),
    LEVANTAMIENTO_PESAS(3, "Levantamiento de Pesas", "kg", false),
    CICLISMO(4, "Ciclismo", "segundos", true),
    BOXEO(5, "Boxeo", "puntos", false);

    private final int id;
    private final String nombreMostrado;
    private final String unidad;
    private final boolean menorEsMejor;

    Disciplina(int id, String nombreMostrado, String unidad, boolean menorEsMejor) {
        this.id = id;
        this.nombreMostrado = nombreMostrado;
        this.unidad = unidad;
        this.menorEsMejor = menorEsMejor;
    }

    public int getId() { return id; }
    public String getNombreMostrado() { return nombreMostrado; }
    public String getUnidad() { return unidad; }
    public boolean isMenorEsMejor() { return menorEsMejor; }

    public static Disciplina fromString(String nombre) {
        for (Disciplina d : Disciplina.values()) {
            if (d.getNombreMostrado().equalsIgnoreCase(nombre)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Disciplina no encontrada: " + nombre);
    }

    /**
     * Busca y devuelve la constante del enum que corresponde a un ID de la BD.
     * @param id El ID de la tabla 'disciplinas'.
     * @return La constante del enum.
     */
    public static Disciplina fromId(int id) {
        for (Disciplina d : Disciplina.values()) {
            if (d.getId() == id) {
                return d;
            }
        }
        throw new IllegalArgumentException("ID de disciplina no válido: " + id);
    }
}