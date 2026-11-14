package com.library.dao;

import com.library.exception.LibraryException;
import com.library.model.LibraryItem;
import com.library.model.Novel;
import com.library.model.TextBook;
import com.library.dao.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for books table.
 * Uses PreparedStatement only. Try-with-resources for all JDBC objects.
 */
public class BookDao {

    // POLYMORPHISM: Factory method returns TextBook or Novel based on DB value
    // This is the key runtime-polymorphism showcase
    private LibraryItem createItem(ResultSet rs) throws SQLException {
        String type = rs.getString("book_type");
        try {
            if ("TEXTBOOK".equals(type)) {
                return new TextBook(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("category"),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies")
                );
            } else {
                return new Novel(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("category"),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies")
                );
            }
        } catch (com.library.exception.InvalidInputException e) {
            throw new SQLException("DB data invalid: " + e.getMessage(), e);
        }
    }

    public List<LibraryItem> findAll() throws LibraryException {
        List<LibraryItem> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(createItem(rs));
        } catch (SQLException e) {
            throw new LibraryException("Failed to fetch books: " + e.getMessage(), e);
        }
        return books;
    }

    public List<LibraryItem> search(String keyword, String category) throws LibraryException {
        List<LibraryItem> books = new ArrayList<>();
        boolean hasKeyword  = keyword  != null && !keyword.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty();

        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1=1");
        if (hasKeyword)  sql.append(" AND (title LIKE ? OR author LIKE ? OR isbn LIKE ?)");
        if (hasCategory) sql.append(" AND category = ?");
        sql.append(" ORDER BY title");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasKeyword) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (hasCategory) ps.setString(idx, category.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) books.add(createItem(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Search failed: " + e.getMessage(), e);
        }
        return books;
    }

    public LibraryItem findById(int id) throws LibraryException {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return createItem(rs);
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to find book: " + e.getMessage(), e);
        }
        return null;
    }

    public LibraryItem findByIsbn(String isbn) throws LibraryException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return createItem(rs);
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to check ISBN: " + e.getMessage(), e);
        }
        return null;
    }

    public LibraryItem insert(LibraryItem item) throws LibraryException {
        String sql = "INSERT INTO books (title, author, isbn, category, book_type, total_copies, available_copies) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getTitle());
            ps.setString(2, item.getAuthor());
            ps.setString(3, item.getIsbn());
            ps.setString(4, item.getCategory());
            ps.setString(5, item.getItemType());
            ps.setInt(6, item.getTotalCopies());
            ps.setInt(7, item.getAvailableCopies());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) item.setId(keys.getInt(1));
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new com.library.exception.DuplicateEntryException(
                "A book with this ISBN already exists.");
        } catch (SQLException e) {
            throw new LibraryException("Failed to add book: " + e.getMessage(), e);
        }
        return item;
    }

    public LibraryItem update(LibraryItem item) throws LibraryException {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, category=?, book_type=?, total_copies=?, available_copies=? WHERE book_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getTitle());
            ps.setString(2, item.getAuthor());
            ps.setString(3, item.getIsbn());
            ps.setString(4, item.getCategory());
            ps.setString(5, item.getItemType());
            ps.setInt(6, item.getTotalCopies());
            ps.setInt(7, item.getAvailableCopies());
            ps.setInt(8, item.getId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new com.library.exception.DuplicateEntryException(
                "Another book already uses this ISBN.");
        } catch (SQLException e) {
            throw new LibraryException("Failed to update book: " + e.getMessage(), e);
        }
        return item;
    }

    public void delete(int id) throws LibraryException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Failed to delete book: " + e.getMessage(), e);
        }
    }

    public int countIssuedCopies(int bookId) throws LibraryException {
        String sql = "SELECT COUNT(*) FROM issue_records WHERE book_id=? AND status='ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to count issued copies: " + e.getMessage(), e);
        }
    }

    // Update only available_copies (called during issue/return transactions — same connection)
    public void updateAvailableCopies(Connection conn, int bookId, int availableCopies)
            throws SQLException {
        String sql = "UPDATE books SET available_copies=? WHERE book_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, availableCopies);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }
}
