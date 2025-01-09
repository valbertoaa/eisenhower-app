package com.valberto.database;

import com.valberto.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    // Método para adicionar uma nova tarefa
    public void addTask(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (title, description, deadline, quadrant) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false); // Inicia a transação
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, task.getTitle());
                preparedStatement.setString(2, task.getDescription());
                preparedStatement.setString(3, task.getDeadline());
                preparedStatement.setString(4, task.getQuadrant());
                preparedStatement.executeUpdate();

                // Recupera o ID gerado automaticamente
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        task.setId(generatedKeys.getInt(1)); // Define o ID gerado na tarefa
                    }
                }

                connection.commit(); // Finaliza a transação
            } catch (SQLException ex) {
                connection.rollback(); // Desfaz a transação em caso de erro
                throw ex;
            }
        }
    }

    // Método para listar todas as tarefas
    public List<Task> getAllTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, deadline, quadrant FROM tasks";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Task task = new Task(
                    resultSet.getInt("id"), // ID da tarefa
                    resultSet.getString("title"),
                    resultSet.getString("description"),
                    resultSet.getString("deadline"),
                    resultSet.getString("quadrant")
                );
                tasks.add(task);
            }
        }
        return tasks;
    }

    // Método para obter uma tarefa pelo ID
    public Task getTaskById(int id) throws SQLException {
        String sql = "SELECT id, title, description, deadline, quadrant FROM tasks WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Task(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("deadline"),
                        resultSet.getString("quadrant")
                    );
                }
            }
        }
        return null; // Retorna null se a tarefa não for encontrada
    }

    // Método para excluir uma tarefa pelo ID
    public void deleteTask(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    // Método para atualizar uma tarefa existente
    public void updateTask(Task task) throws SQLException {
        String sql = "UPDATE tasks SET title = ?, description = ?, deadline = ?, quadrant = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setString(3, task.getDeadline());
            preparedStatement.setString(4, task.getQuadrant());
            preparedStatement.setInt(5, task.getId());
            preparedStatement.executeUpdate();
        }
    }
}
