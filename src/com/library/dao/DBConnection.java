
package com.library.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static HikariDataSource dataSource;

    private static synchronized void initialize() throws SQLException {
        if (dataSource != null) {
            return;
        }

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            Properties properties = new Properties();

            try (InputStream input = new FileInputStream("db.properties")) {
                properties.load(input);
            } catch (IOException e) {
                throw new SQLException("Unable to load database configuration.", e);
            }

            url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");
        }

        if (url == null || user == null || password == null) {
            throw new SQLException("Database configuration is incomplete.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1500000);
        config.setPoolName("BookSpherePool");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        initialize();
        return dataSource.getConnection();
    }

    public static synchronized void closePool() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
