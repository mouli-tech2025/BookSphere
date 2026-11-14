package com.library.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Serves static files from the frontend/ directory.
 * Maps / and /dashboard to dashboard.html.
 */
public class StaticFileHandler implements HttpHandler {

    private final String frontendDir;

    public StaticFileHandler(String frontendDir) {
        this.frontendDir = frontendDir;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();

        // Default routes
        if ("/".equals(uriPath) || "/dashboard".equals(uriPath) || uriPath.isEmpty())
            uriPath = "/dashboard.html";

        // Prevent path traversal
        if (uriPath.contains("..")) {
            sendError(exchange, 400, "Bad request.");
            return;
        }

        Path filePath = Paths.get(frontendDir, uriPath.substring(1)); // strip leading /

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            sendError(exchange, 404, "File not found: " + uriPath);
            return;
        }

        String mimeType = guessMime(uriPath);
        byte[] bytes = Files.readAllBytes(filePath);

        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int code, String msg) throws IOException {
        byte[] bytes = msg.getBytes();
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String guessMime(String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css"))  return "text/css; charset=utf-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=utf-8";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg"))  return "image/jpeg";
        if (path.endsWith(".ico"))  return "image/x-icon";
        if (path.endsWith(".svg"))  return "image/svg+xml";
        return "application/octet-stream";
    }
}
