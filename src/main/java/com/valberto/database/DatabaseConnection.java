package com.valberto.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Configurações do banco de dados  
    private static final String DB_URL = "jdbc:firebirdsql://localhost:3050/d:/bancos/eisenhower.fdb";
    private static final String DB_USER = "SYSDBA";
    private static final String DB_PASSWORD = "masterkey";

    // Retorna uma conexão com o banco de dados
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
