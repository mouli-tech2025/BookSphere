package com.library.service;

import com.library.dao.BookDao;
import com.library.dao.IssueDao;
import com.library.exception.*;
import com.library.model.IssueRecord;
import com.library.model.LibraryItem;
import com.library.model.Student;

import java.time.LocalDate;

public class IssueService {

    private final IssueDao   issueDao   = new IssueDao();
    private final BookDao    bookDao    = new BookDao();
    private final StudentService studentService = new StudentService();
    private final BookService    bookService    = new BookService();

    public java.util.List<IssueRecord> getAllIssues(String status) throws LibraryException {
        return issueDao.findAll(status);
    }

    public IssueRecord issueBook(String studentIdStr, String bookIdStr) throws LibraryException {

        int studentId = parseId(studentIdStr, "Student ID");
        int bookId    = parseId(bookIdStr, "Book ID");

        // Validate student exists
        Student student = studentService.getStudentById(studentId);

        // Validate book exists and is available
        LibraryItem item = bookService.getBookById(bookId);
        if (!item.isAvailable())
            throw new BookNotAvailableException(
                "No copies of '" + item.getTitle() + "' are currently available.");

        // Business rule: max 3 books per student
        int held = issueDao.countActiveByStudent(studentId);
        if (held >= 3)
            throw new LibraryException(
                student.getName() + " already holds 3 books — the maximum allowed.");

        // Business rule: cannot issue same book twice
        if (issueDao.hasActiveIssue(studentId, bookId))
            throw new LibraryException(
                student.getName() + " already has '" + item.getTitle() + "' issued.");

        LocalDate today   = LocalDate.now();
        LocalDate dueDate = today.plusDays(item.getLoanPeriodDays()); // POLYMORPHISM

        return issueDao.issueBook(bookId, studentId, today, dueDate, bookDao);
    }

    public IssueRecord returnBook(String issueIdStr) throws LibraryException {

        int issueId = parseId(issueIdStr, "Issue ID");

        IssueRecord record = issueDao.findById(issueId);
        if (record == null)
            throw new LibraryException("Issue record not found with ID: " + issueId);
        if ("RETURNED".equals(record.getStatus()))
            throw new LibraryException("This book has already been returned.");

        // Load the book to calculate fine polymorphically
        LibraryItem item = bookService.getBookById(record.getBookId());

        LocalDate today    = LocalDate.now();
        long daysLate      = Math.max(0, today.toEpochDay() - record.getDueDate().toEpochDay());
        double fine        = item.calculateFine((int) daysLate); // POLYMORPHISM

        return issueDao.returnBook(issueId, today, fine, bookDao);
    }

    private int parseId(String s, String fieldName) throws InvalidInputException {
        try {
            int v = Integer.parseInt(s.trim());
            if (v < 1) throw new InvalidInputException(fieldName + " must be positive.");
            return v;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid number.");
        }
    }
}
