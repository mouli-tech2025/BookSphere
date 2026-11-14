package com.library.handler;

import com.library.model.Student;
import com.library.service.StudentService;
import com.library.util.FormParser;
import com.library.util.JsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// INHERITANCE: extends BaseHandler
public class StudentHandler extends BaseHandler {

    private final StudentService studentService = new StudentService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path   = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String query  = exchange.getRequestURI().getQuery();

        try {
            if ("GET".equalsIgnoreCase(method)) {
                Map<String, String> params = FormParser.parseQuery(query);
                String keyword = FormParser.get(params, "query");
                List<Student> students = studentService.search(keyword);
                sendJson(exchange, 200, JsonBuilder.ok("Students loaded.", studentsToJson(students)));
            } else if ("POST".equalsIgnoreCase(method)) {
                Map<String, String> p = FormParser.parseBody(exchange.getRequestBody());
                Student s = studentService.registerStudent(
                    FormParser.get(p, "name"),
                    FormParser.get(p, "email"),
                    FormParser.get(p, "phone"),
                    FormParser.get(p, "rollNo"),
                    FormParser.get(p, "department")
                );
                sendJson(exchange, 201, JsonBuilder.ok("Student registered.", studentToJson(s)));
            } else {
                checkMethod(exchange, "GET", "POST");
            }
        } catch (Exception e) {
            handleError(exchange, e);
        }
    }

    private String studentToJson(Student s) {
        return JsonBuilder.obj(
            JsonBuilder.num("id",         s.getId()),
            JsonBuilder.str("name",       s.getName()),
            JsonBuilder.str("email",      s.getEmail()),
            JsonBuilder.str("phone",      s.getPhone()),
            JsonBuilder.str("rollNo",     s.getRollNo()),
            JsonBuilder.str("department", s.getDepartment())
        );
    }

    private String studentsToJson(List<Student> students) {
        List<String> items = new ArrayList<>();
        for (Student s : students) items.add(studentToJson(s));
        return JsonBuilder.arr(items);
    }
}
