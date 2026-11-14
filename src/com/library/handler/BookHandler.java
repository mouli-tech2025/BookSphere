package com.library.handler;

import com.library.model.LibraryItem;
import com.library.service.BookService;
import com.library.util.FormParser;
import com.library.util.JsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// INHERITANCE: extends BaseHandler
public class BookHandler extends BaseHandler {

    private final BookService bookService = new BookService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path   = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String query  = exchange.getRequestURI().getQuery();

        // /api/books
        // /api/books/{id}
        // /api/books/{id}/availability
        boolean hasId = path.length() > "/api/books".length() && path.charAt("/api/books".length()) == '/';

        try {
            if (!hasId) {
                // /api/books
                if ("GET".equalsIgnoreCase(method)) {
                    handleListOrSearch(exchange, query);
                } else if ("POST".equalsIgnoreCase(method)) {
                    handleAdd(exchange);
                } else {
                    checkMethod(exchange, "GET", "POST");
                }
            } else {
                String idStr  = getPathSegment(path, "/api/books");
                String trail  = getTrailingSegment(path, "/api/books/" + idStr);
                int id = Integer.parseInt(idStr);

                if ("availability".equals(trail)) {
                    handleAvailability(exchange, id);
                } else if ("GET".equalsIgnoreCase(method)) {
                    handleGetOne(exchange, id);
                } else if ("PUT".equalsIgnoreCase(method)) {
                    handleUpdate(exchange, id);
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    handleDelete(exchange, id);
                } else {
                    checkMethod(exchange, "GET", "PUT", "DELETE");
                }
            }
        } catch (NumberFormatException e) {
            sendJson(exchange, 400, JsonBuilder.error("Invalid book ID."));
        } catch (Exception e) {
            handleError(exchange, e);
        }
    }

    private void handleListOrSearch(HttpExchange exchange, String query) throws Exception {
        Map<String, String> params = FormParser.parseQuery(query);
        String keyword  = FormParser.get(params, "query");
        String category = FormParser.get(params, "category");
        List<LibraryItem> books = bookService.search(keyword, category);
        sendJson(exchange, 200, JsonBuilder.ok("Books loaded.", booksToJson(books)));
    }

    private void handleGetOne(HttpExchange exchange, int id) throws Exception {
        LibraryItem item = bookService.getBookById(id);
        sendJson(exchange, 200, JsonBuilder.ok("Book found.", bookToJson(item)));
    }

    private void handleAvailability(HttpExchange exchange, int id) throws Exception {
        LibraryItem item = bookService.getBookById(id);
        String json = JsonBuilder.obj(
            JsonBuilder.bool("available", item.isAvailable()),
            JsonBuilder.num("availableCopies", item.getAvailableCopies())
        );
        sendJson(exchange, 200, JsonBuilder.ok("Availability checked.", json));
    }

    private void handleAdd(HttpExchange exchange) throws Exception {
        Map<String, String> p = FormParser.parseBody(exchange.getRequestBody());
        LibraryItem item = bookService.addBook(
            FormParser.get(p, "title"),
            FormParser.get(p, "author"),
            FormParser.get(p, "isbn"),
            FormParser.get(p, "category"),
            FormParser.get(p, "type"),
            FormParser.get(p, "totalCopies")
        );
        sendJson(exchange, 201, JsonBuilder.ok("Book added successfully.", bookToJson(item)));
    }

    private void handleUpdate(HttpExchange exchange, int id) throws Exception {
        Map<String, String> p = FormParser.parseBody(exchange.getRequestBody());
        LibraryItem item = bookService.updateBook(id,
            FormParser.get(p, "title"),
            FormParser.get(p, "author"),
            FormParser.get(p, "isbn"),
            FormParser.get(p, "category"),
            FormParser.get(p, "type"),
            FormParser.get(p, "totalCopies")
        );
        sendJson(exchange, 200, JsonBuilder.ok("Book updated.", bookToJson(item)));
    }

    private void handleDelete(HttpExchange exchange, int id) throws Exception {
        bookService.deleteBook(id);
        sendJson(exchange, 200, JsonBuilder.ok("Book deleted.", "null"));
    }

    // --- JSON serialization for LibraryItem ---
    private String bookToJson(LibraryItem b) {
        return JsonBuilder.obj(
            JsonBuilder.num("id",              b.getId()),
            JsonBuilder.str("title",           b.getTitle()),
            JsonBuilder.str("author",          b.getAuthor()),
            JsonBuilder.str("isbn",            b.getIsbn()),
            JsonBuilder.str("category",        b.getCategory()),
            JsonBuilder.str("type",            b.getItemType()),
            JsonBuilder.num("totalCopies",     b.getTotalCopies()),
            JsonBuilder.num("availableCopies", b.getAvailableCopies()),
            JsonBuilder.num("loanPeriodDays",  b.getLoanPeriodDays()),
            JsonBuilder.num("finePerDay",      b.getFinePerDay())
        );
    }

    private String booksToJson(List<LibraryItem> books) {
        List<String> items = new ArrayList<>();
        for (LibraryItem b : books) items.add(bookToJson(b));
        return JsonBuilder.arr(items);
    }
}
