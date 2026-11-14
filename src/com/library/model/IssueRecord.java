package com.library.model;

import java.time.LocalDate;

// Plain encapsulated class — ENCAPSULATION
// Represents a single book-issue transaction
public class IssueRecord {

    // ENCAPSULATION: all private fields
    private int       issueId;
    private int       bookId;
    private int       studentId;
    private String    bookTitle;
    private String    studentName;
    private String    rollNo;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;   // null if not yet returned
    private double    fine;
    private String    status;       // "ISSUED" or "RETURNED"

    // Full constructor
    public IssueRecord(int issueId, int bookId, int studentId, String bookTitle,
                       String studentName, String rollNo,
                       LocalDate issueDate, LocalDate dueDate,
                       LocalDate returnDate, double fine, String status) {
        this.issueId     = issueId;
        this.bookId      = bookId;
        this.studentId   = studentId;
        this.bookTitle   = bookTitle;
        this.studentName = studentName;
        this.rollNo      = rollNo;
        this.issueDate   = issueDate;
        this.dueDate     = dueDate;
        this.returnDate  = returnDate;
        this.fine        = fine;
        this.status      = status;
    }

    // Getters
    public int       getIssueId()     { return issueId; }
    public int       getBookId()      { return bookId; }
    public int       getStudentId()   { return studentId; }
    public String    getBookTitle()   { return bookTitle; }
    public String    getStudentName() { return studentName; }
    public String    getRollNo()      { return rollNo; }
    public LocalDate getIssueDate()   { return issueDate; }
    public LocalDate getDueDate()     { return dueDate; }
    public LocalDate getReturnDate()  { return returnDate; }
    public double    getFine()        { return fine; }
    public String    getStatus()      { return status; }

    // Setters
    public void setIssueId(int issueId)         { this.issueId = issueId; }
    public void setReturnDate(LocalDate d)       { this.returnDate = d; }
    public void setFine(double fine)             { this.fine = fine; }
    public void setStatus(String status)         { this.status = status; }
}
