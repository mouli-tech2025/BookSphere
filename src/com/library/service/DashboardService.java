package com.library.service;

import com.library.exception.LibraryException;
import com.library.dao.DBConnection;
import com.library.util.JsonBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardService {

    // COLLECTIONS: LinkedHashMap preserves insertion order for dashboard stats
    public Map<String, Integer> getStats() throws LibraryException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String sql =
            "SELECT "
          + "  (SELECT SUM(total_copies)     FROM books) AS totalBooks, "
          + "  (SELECT SUM(available_copies) FROM books) AS availableBooks, "
          + "  (SELECT COUNT(*) FROM issue_records WHERE status='ISSUED') AS issuedBooks, "
          + "  (SELECT COUNT(*) FROM students) AS totalStudents";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("totalBooks",     rs.getInt("totalBooks"));
                stats.put("availableBooks", rs.getInt("availableBooks"));
                stats.put("issuedBooks",    rs.getInt("issuedBooks"));
                stats.put("totalStudents",  rs.getInt("totalStudents"));
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to load dashboard stats: " + e.getMessage(), e);
        }
        return stats;
    }
}
