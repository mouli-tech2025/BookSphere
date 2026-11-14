package com.library.dao;

import com.library.exception.LibraryException;
import com.library.model.Student;
import com.library.dao.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    private Student mapRow(ResultSet rs) throws SQLException {
        try {
            return new Student(
                rs.getInt("student_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("roll_no"),
                rs.getString("department")
            );
        } catch (com.library.exception.InvalidInputException e) {
            throw new SQLException("DB data invalid: " + e.getMessage(), e);
        }
    }

    public List<Student> findAll() throws LibraryException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new LibraryException("Failed to fetch students: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Student> search(String keyword) throws LibraryException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE name LIKE ? OR roll_no LIKE ? OR department LIKE ? ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword.trim() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Student search failed: " + e.getMessage(), e);
        }
        return list;
    }

    public Student findById(int id) throws LibraryException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to find student: " + e.getMessage(), e);
        }
        return null;
    }

    public Student findByEmail(String email) throws LibraryException {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to check email: " + e.getMessage(), e);
        }
        return null;
    }

    public Student findByRollNo(String rollNo) throws LibraryException {
        String sql = "SELECT * FROM students WHERE roll_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new LibraryException("Failed to check roll number: " + e.getMessage(), e);
        }
        return null;
    }

    public Student insert(Student student) throws LibraryException {
        String sql = "INSERT INTO students (name, email, phone, roll_no, department) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPhone());
            ps.setString(4, student.getRollNo());
            ps.setString(5, student.getDepartment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) student.setId(keys.getInt(1));
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new com.library.exception.DuplicateEntryException(
                "A student with this email or roll number already exists.");
        } catch (SQLException e) {
            throw new LibraryException("Failed to register student: " + e.getMessage(), e);
        }
        return student;
    }
}
