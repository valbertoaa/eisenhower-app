package com.valberto.model;

public class Task {
    private int id;
    private String title;
    private String description;
    private String deadline;
    private String quadrant;
    private boolean status; // Novo campo: true para Encerrada, false para Aberta

    public Task(int id, String title, String description, String deadline, String quadrant, boolean status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.quadrant = quadrant;
        this.status = status;
    }

    public Task(String title, String description, String deadline, String quadrant, boolean status) {
        this(0, title, description, deadline, quadrant, status);
    }

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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String statusText = status ? "Encerrada" : "Aberta";
        return id + " - " + title + " (" + deadline + ") [" + statusText + "]";
    }
}
