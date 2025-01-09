package com.valberto.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id; // Novo campo para o ID
    private String title;
    private String description;
    private String deadline; // A data continua como String para facilitar integração com o banco
    private String quadrant;

    public Task(int id, String title, String description, String deadline, String quadrant) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.quadrant = quadrant;
    }

    // Construtor alternativo sem o ID (para novas tarefas)
    public Task(String title, String description, String deadline, String quadrant) {
        this(0, title, description, deadline, quadrant); // ID padrão: 0
    }

    // Getter e Setter para o ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getQuadrant() {
        return quadrant;
    }

    public void setQuadrant(String quadrant) {
        this.quadrant = quadrant;
    }

    @Override
    public String toString() {
        String formattedDeadline = formatDeadline(deadline); // Formata a data no padrão dd/MM/yyyy
        return id + " - " + title + " (" + formattedDeadline + ")";
    }

    // Método auxiliar para formatar a data
    private String formatDeadline(String deadline) {
        try {
            LocalDate date = LocalDate.parse(deadline); // Converte a data de String para LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(formatter); // Formata para o padrão dd/MM/yyyy
        } catch (Exception e) {
            return deadline; // Retorna o valor original em caso de erro
        }
    }
}
