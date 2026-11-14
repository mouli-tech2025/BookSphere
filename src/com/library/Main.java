package com.library;

import com.library.handler.*;
import com.library.dao.DBConnection;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Main entry point. Wires up the HttpServer, registers all handlers,
 * and verifies the DB connection on startup.
 */
public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {

        // Verify DB connection on startup
        System.out.println("Connecting to database...");
        // try-catch-finally: EXCEPTION HANDLING showcase
        Connection testConn = null;
        try {
            testConn = DBConnection.getConnection();
            System.out.println("\u2713 Database connected successfully.");
        } catch (SQLException e) {
            System.err.println("\u2717 Database connection failed: " + e.getMessage());
            System.err.println("  \u2192 Check db.properties (url, user, password) and ensure MySQL is running.");
            System.exit(1);
        } finally {
            if (testConn != null) {
                try { testConn.close(); } catch (SQLException ignored) {}
            }
        }

        // Find the frontend directory (relative to working directory)
        String frontendDir = "frontend";
        java.io.File frontendCheck = new java.io.File(frontendDir);
        if (!frontendCheck.exists()) {
            System.err.println("\u2717 frontend/ directory not found. Run from the BookSphere root.");
            System.exit(1);
        }

        // Create and configure HttpServer
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            System.err.println("\u2717 Could not start server on port " + PORT + ": " + e.getMessage());
            System.err.println("  \u2192 Is port " + PORT + " already in use? Try: lsof -i :" + PORT);
            System.exit(1);
            return;
        }

        // Register API handlers
        server.createContext("/api/dashboard", new DashboardHandler());
        server.createContext("/api/books",     new BookHandler());
        server.createContext("/api/students",  new StudentHandler());
        server.createContext("/api/issue",     new IssueHandler());
        server.createContext("/api/return",    new IssueHandler());
        server.createContext("/api/issues",    new IssueHandler());

        // Static file handler (serves frontend/) — catches everything else
        server.createContext("/", new StaticFileHandler(frontendDir));

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557");
        System.out.println("\u2551   Library Management System \u2014 Server Running  \u2551");
        System.out.println("\u2560\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2563");
        System.out.printf( "\u2551   %-44s \u2551%n", "URL: http://localhost:" + PORT);
        System.out.println("\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d");
        System.out.println("Press Ctrl+C to stop.");
    }
}
