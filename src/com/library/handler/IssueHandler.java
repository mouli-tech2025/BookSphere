package com.library.handler;

import com.library.model.IssueRecord;
import com.library.service.IssueService;
import com.library.util.FormParser;
import com.library.util.JsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// INHERITANCE: extends BaseHandler
public class IssueHandler extends BaseHandler {

    private final IssueService issueService = new IssueService();

    // Routes: POST /api/issue, POST /api/return, GET /api/issues
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path   = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String query  = exchange.getRequestURI().getQuery();

        try {
            if (path.equals("/api/issue") && "POST".equalsIgnoreCase(method)) {
                Map<String, String> p = FormParser.parseBody(exchange.getRequestBody());
                IssueRecord record = issueService.issueBook(
                    FormParser.get(p, "studentId"),
                    FormParser.get(p, "bookId")
                );
                sendJson(exchange, 201, JsonBuilder.ok("Book issued successfully.", issueToJson(record)));

            } else if (path.equals("/api/return") && "POST".equalsIgnoreCase(method)) {
                Map<String, String> p = FormParser.parseBody(exchange.getRequestBody());
                IssueRecord record = issueService.returnBook(FormParser.get(p, "issueId"));
                sendJson(exchange, 200, JsonBuilder.ok(
                    "Book returned. Fine: \u20b9" + String.format("%.2f", record.getFine()),
                    issueToJson(record)
                ));

            } else if (path.equals("/api/issues") && "GET".equalsIgnoreCase(method)) {
                Map<String, String> params = FormParser.parseQuery(query);
                String status = FormParser.get(params, "status");
                List<IssueRecord> records = issueService.getAllIssues(status);
                sendJson(exchange, 200, JsonBuilder.ok("Issues loaded.", issuesToJson(records)));

            } else {
                sendJson(exchange, 404, JsonBuilder.error("Endpoint not found."));
            }
        } catch (Exception e) {
            handleError(exchange, e);
        }
    }

    private String issueToJson(IssueRecord r) {
        return JsonBuilder.obj(
            JsonBuilder.num("issueId",     r.getIssueId()),
            JsonBuilder.num("bookId",      r.getBookId()),
            JsonBuilder.num("studentId",   r.getStudentId()),
            JsonBuilder.str("bookTitle",   r.getBookTitle()),
            JsonBuilder.str("studentName", r.getStudentName()),
            JsonBuilder.str("rollNo",      r.getRollNo()),
            JsonBuilder.str("issueDate",   r.getIssueDate().toString()),
            JsonBuilder.str("dueDate",     r.getDueDate().toString()),
            JsonBuilder.str("returnDate",  r.getReturnDate() != null ? r.getReturnDate().toString() : ""),
            JsonBuilder.num("fine",        r.getFine()),
            JsonBuilder.str("status",      r.getStatus())
        );
    }

    private String issuesToJson(List<IssueRecord> records) {
        List<String> items = new ArrayList<>();
        for (IssueRecord r : records) items.add(issueToJson(r));
        return JsonBuilder.arr(items);
    }
}
