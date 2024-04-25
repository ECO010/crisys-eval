package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    public static Connection connect() {
        Connection conn = null;
        try {
            // The URL specifies the path to your SQLite database
            String url = "jdbc:sqlite:src/main/resources/db/capecdb01";
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
