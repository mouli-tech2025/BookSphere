package com.library.handler;

import com.library.exception.BookNotFoundException;
import com.library.exception.LibraryException;
import com.library.exception.StudentNotFoundException;
import com.library.util.JsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * ABSTRACT CLASS — ABSTRACTION + INHERITANCE
 * All handlers extend BaseHandler for shared HTTP helpers.
 */
public abstract class BaseHandler implements HttpHandler {

    // Send a JSON response with a given HTTP status code
    protected void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        // try-finally ensures the stream is always closed (EXCEPTION HANDLING)
        OutputStream os = exchange.getResponseBody();
        try {
            os.write(bytes);
        } finally {
            os.close();
        }
    }

    // Map a LibraryException to the right HTTP status code
    protected int statusFor(LibraryException e) {
        if (e instanceof BookNotFoundException || e instanceof StudentNotFoundException)
            return 404;
        return 400;
    }

    // Shared error handler: catches LibraryException → 400/404, anything else → 500
    protected void handleError(HttpExchange exchange, Exception e) {
        try {
            if (e instanceof LibraryException le) {
                sendJson(exchange, statusFor(le), JsonBuilder.error(le.getMessage()));
            } else {
                System.err.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
                sendJson(exchange, 500, JsonBuilder.error("An internal server error occurred."));
            }
        } catch (IOException ioEx) {
            System.err.println("Failed to send error response: " + ioEx.getMessage());
        }
    }

    // Reject wrong HTTP methods
    protected boolean checkMethod(HttpExchange exchange, String... allowed) throws IOException {
        String method = exchange.getRequestMethod();
        for (String m : allowed) {
            if (m.equalsIgnoreCase(method)) return true;
        }
        sendJson(exchange, 405, JsonBuilder.error("Method not allowed: " + method));
        return false;
    }

    // Extract path segment after a base: /api/books/42 → "42"
    protected String getPathSegment(String path, String base) {
        String suffix = path.substring(base.length());
        if (suffix.startsWith("/")) suffix = suffix.substring(1);
        int slash = suffix.indexOf('/');
        return slash >= 0 ? suffix.substring(0, slash) : suffix;
    }

    // Extract trailing path: /api/books/42/availability → "availability"
    protected String getTrailingSegment(String path, String base) {
        String suffix = path.substring(base.length());
        if (suffix.startsWith("/")) suffix = suffix.substring(1);
        int slash = suffix.indexOf('/');
        return slash >= 0 ? suffix.substring(slash + 1) : "";
    }
}
