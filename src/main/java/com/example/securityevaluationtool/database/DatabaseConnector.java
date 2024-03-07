package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//TODO: CLean up all DB files and remove personal data, just leave structure of how data was fetched and sent to the DB
public class DatabaseConnector {
    public static Connection connect() {
        Connection conn = null;
        try {
            // The URL specifies the path to your SQLite database
            String url = "jdbc:sqlite:C:\\Users\\okonj\\Desktop\\CAPECDB\\capecdb01";
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
