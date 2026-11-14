package com.library.service;

import com.library.dao.StudentDao;
import com.library.exception.*;
import com.library.model.Student;
import com.library.util.Validator;

import java.util.List;

/**
 * Business rules for students.
 * Implements Searchable<Student> — ABSTRACTION via interface.
 */
public class StudentService implements Searchable<Student> {

    private final StudentDao studentDao = new StudentDao();

    @Override
    public List<Student> search(String keyword) throws LibraryException {
        if (keyword == null || keyword.trim().isEmpty())
            return studentDao.findAll();
        return studentDao.search(keyword);
    }

    public List<Student> getAllStudents() throws LibraryException {
        return studentDao.findAll();
    }

    public Student getStudentById(int id) throws LibraryException {
        Student s = studentDao.findById(id);
        if (s == null)
            throw new StudentNotFoundException("No student found with ID: " + id);
        return s;
    }

    public Student registerStudent(String name, String email, String phone,
                                    String rollNo, String department)
            throws LibraryException {

        Validator.requireNonBlank(name, "Name");
        Validator.requireNonBlank(rollNo, "Roll number");
        Validator.requireNonBlank(department, "Department");
        Validator.validateEmail(email);
        Validator.validatePhone(phone);

        if (studentDao.findByEmail(email) != null)
            throw new DuplicateEntryException("A student with email '" + email + "' already exists.");
        if (studentDao.findByRollNo(rollNo) != null)
            throw new DuplicateEntryException("A student with roll number '" + rollNo + "' already exists.");

        Student s;
        try {
            s = new Student(0, name, email, phone, rollNo, department);
        } catch (InvalidInputException e) {
            throw e;
        }
        return studentDao.insert(s);
    }
}
