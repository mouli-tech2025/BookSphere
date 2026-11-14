package com.library.dao;

import com.library.exception.LibraryException;
import com.library.model.IssueRecord;
import com.library.dao.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IssueDao {

    private IssueRecord mapRow(ResultSet rs) throws SQLException {
        LocalDate returnDate = rs.getDate("return_date") != null
            ? rs.getDate("return_date").toLocalDate() : null;
        return new IssueRecord(
            rs.getInt("issue_id"),
            rs.getInt("book_id"),
            rs.getInt("student_id"),
            rs.getString("title"),
            rs.getString("name"),
            rs.getString("roll_no"),
            rs.getDate("issue_date").toLocalDate(),
            rs.getDate("due_date").toLocalDate(),
            returnDate,
            rs.getDouble("fine"),
            rs.getString("status")
        );
    }

    public List<IssueRecord> findAll(String status) throws LibraryException {
        List<IssueRecord> list = new ArrayList<>();
        String sql = "SELECT ir.*, b.title, s.name, s.roll_no "
                   + "FROM issue_records ir "
                   + "JOIN books b ON ir.book_id = b.book_id "
                   + "JOIN students s ON ir.student_id = s.student_id "
                   + (status != null && !status.isEmpty() ? "WHERE ir.status = ? " : "")
                   + "ORDER BY ir.issue_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (status != null && !status.isEmpty()) ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to fetch issue records: " + e.getMessage(), e);
        }
        return list;
    }

    public IssueRecord findById(int id) throws LibraryException {
        String sql = "SELECT ir.*, b.title, s.name, s.roll_no "
                   + "FROM issue_records ir "
                   + "JOIN books b ON ir.book_id = b.book_id "
                   + "JOIN students s ON ir.student_id = s.student_id "
                   + "WHERE ir.issue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to find issue record: " + e.getMessage(), e);
        }
        return null;
    }

    public int countActiveByStudent(int studentId) throws LibraryException {
        String sql = "SELECT COUNT(*) FROM issue_records WHERE student_id=? AND status='ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to count student's issues: " + e.getMessage(), e);
        }
    }

    public boolean hasActiveIssue(int studentId, int bookId) throws LibraryException {
        String sql = "SELECT COUNT(*) FROM issue_records WHERE student_id=? AND book_id=? AND status='ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to check active issue: " + e.getMessage(), e);
        }
    }

    // Issue a book — JDBC TRANSACTION (atomic: insert record + decrement copies)
    public IssueRecord issueBook(int bookId, int studentId, LocalDate issueDate,
                                  LocalDate dueDate, BookDao bookDao) throws LibraryException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            // Insert issue record
            String insertSql = "INSERT INTO issue_records (book_id, student_id, issue_date, due_date, fine, status) VALUES (?,?,?,?,0,'ISSUED')";
            int issueId;
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, bookId);
                ps.setInt(2, studentId);
                ps.setDate(3, Date.valueOf(issueDate));
                ps.setDate(4, Date.valueOf(dueDate));
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    issueId = keys.getInt(1);
                }
            }

            // Decrement available_copies (same transaction)
            String updateSql = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id=? AND available_copies > 0";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, bookId);
                int rows = ps.executeUpdate();
                if (rows == 0) throw new SQLException("No copies available (concurrent update).");
            }

            conn.commit(); // COMMIT TRANSACTION

            return findById(issueId);

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore rollback error */ }
            }
            throw new LibraryException("Issue transaction failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }

    // Return a book — JDBC TRANSACTION (atomic: update record + increment copies)
    public IssueRecord returnBook(int issueId, LocalDate returnDate,
                                   double fine, BookDao bookDao) throws LibraryException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            // Update issue record
            String updateRecord = "UPDATE issue_records SET return_date=?, fine=?, status='RETURNED' WHERE issue_id=?";
            try (PreparedStatement ps = conn.prepareStatement(updateRecord)) {
                ps.setDate(1, Date.valueOf(returnDate));
                ps.setDouble(2, fine);
                ps.setInt(3, issueId);
                ps.executeUpdate();
            }

            // Increment available_copies (same transaction)
            String updateBook = "UPDATE books b JOIN issue_records ir ON b.book_id = ir.book_id "
                              + "SET b.available_copies = b.available_copies + 1 "
                              + "WHERE ir.issue_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBook)) {
                ps.setInt(1, issueId);
                ps.executeUpdate();
            }

            conn.commit(); // COMMIT TRANSACTION

            return findById(issueId);

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            throw new LibraryException("Return transaction failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }
}
