package com.library.handler;

import com.library.service.DashboardService;
import com.library.util.JsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

// INHERITANCE: extends BaseHandler
public class DashboardHandler extends BaseHandler {

    private final DashboardService dashboardService = new DashboardService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!checkMethod(exchange, "GET")) return;
            Map<String, Integer> stats = dashboardService.getStats();
            String json = JsonBuilder.obj(
                JsonBuilder.num("totalBooks",     stats.get("totalBooks")),
                JsonBuilder.num("availableBooks", stats.get("availableBooks")),
                JsonBuilder.num("issuedBooks",    stats.get("issuedBooks")),
                JsonBuilder.num("totalStudents",  stats.get("totalStudents"))
            );
            sendJson(exchange, 200, JsonBuilder.ok("Dashboard stats loaded.", json));
        } catch (Exception e) {
            handleError(exchange, e);
        }
    }
}
