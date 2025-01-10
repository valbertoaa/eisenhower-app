package com.valberto.database;

import com.valberto.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    public void addTask(Task task) throws SQLException {
        String sql = "INSERT INTO TASKS (title, description, deadline, quadrant, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getDeadline());
            stmt.setString(4, task.getQuadrant());
            stmt.setBoolean(5, task.isStatus());
            stmt.executeUpdate();
        }
    }

    public void updateTask(Task task) throws SQLException {
        String sql = "UPDATE TASKS SET title = ?, description = ?, deadline = ?, quadrant = ?, status = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getDeadline());
            stmt.setString(4, task.getQuadrant());
            stmt.setBoolean(5, task.isStatus());
            stmt.setInt(6, task.getId());
            stmt.executeUpdate();
        }
    }

    public List<Task> getAllTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM TASKS";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Task task = new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("deadline"),
                    rs.getString("quadrant"),
                    rs.getBoolean("status")
                );
                tasks.add(task);
            }
        }
        return tasks;
    }

    public Task getTaskById(int id) throws SQLException {
        String sql = "SELECT * FROM TASKS WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("deadline"),
                        rs.getString("quadrant"),
                        rs.getBoolean("status")
                    );
                }
            }
        }
        return null;
    }
}
