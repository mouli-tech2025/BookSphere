package com.library.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connectivity using MySQL JDBC Driver.
 * Reads configuration from db.properties file.
 */
public class DBConnection {

    private static final String CONFIG_FILE = "db.properties";

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            throw new SQLException("Unable to load database configuration from " + CONFIG_FILE, e);
        }

        String url      = properties.getProperty("db.url");
        String user     = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }
}
