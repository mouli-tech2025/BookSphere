package com.library.model;

import com.library.exception.BookNotAvailableException;
import com.library.exception.InvalidInputException;

// ABSTRACT CLASS — ABSTRACTION + ENCAPSULATION
// TextBook and Novel extend this (INHERITANCE + POLYMORPHISM)
public abstract class LibraryItem {

    // ENCAPSULATION: all fields private
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int totalCopies;
    private int availableCopies;

    public LibraryItem(int id, String title, String author, String isbn,
                       String category, int totalCopies, int availableCopies)
            throws InvalidInputException {
        this.id = id;
        setTitle(title);
        setAuthor(author);
        setIsbn(isbn);
        setCategory(category);
        setTotalCopies(totalCopies);
        setAvailableCopies(availableCopies);
    }

    // --- Getters ---
    public int    getId()              { return id; }
    public String getTitle()           { return title; }
    public String getAuthor()          { return author; }
    public String getIsbn()            { return isbn; }
    public String getCategory()        { return category; }
    public int    getTotalCopies()     { return totalCopies; }
    public int    getAvailableCopies() { return availableCopies; }

    // --- Validated Setters (ENCAPSULATION) ---
    public void setId(int id) { this.id = id; }

    public void setTitle(String title) throws InvalidInputException {
        if (title == null || title.trim().isEmpty())
            throw new InvalidInputException("Title cannot be blank.");
        this.title = title.trim();
    }

    public void setAuthor(String author) throws InvalidInputException {
        if (author == null || author.trim().isEmpty())
            throw new InvalidInputException("Author cannot be blank.");
        this.author = author.trim();
    }

    public void setIsbn(String isbn) throws InvalidInputException {
        if (isbn == null || isbn.trim().isEmpty())
            throw new InvalidInputException("ISBN cannot be blank.");
        this.isbn = isbn.trim();
    }

    public void setCategory(String category) throws InvalidInputException {
        if (category == null || category.trim().isEmpty())
            throw new InvalidInputException("Category cannot be blank.");
        this.category = category.trim();
    }

    public void setTotalCopies(int totalCopies) throws InvalidInputException {
        if (totalCopies < 1)
            throw new InvalidInputException("Total copies must be at least 1.");
        this.totalCopies = totalCopies;
    }

    public void setAvailableCopies(int availableCopies) throws InvalidInputException {
        if (availableCopies < 0 || availableCopies > totalCopies)
            throw new InvalidInputException(
                "Available copies must be between 0 and total copies (" + totalCopies + ").");
        this.availableCopies = availableCopies;
    }

    // --- Concrete methods ---
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    // Called when a book is issued — throws if none available
    public void decreaseAvailable() throws BookNotAvailableException {
        if (availableCopies <= 0)
            throw new BookNotAvailableException(
                "No copies of '" + title + "' are currently available.");
        availableCopies--;
    }

    // Called when a book is returned
    public void increaseAvailable() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    // POLYMORPHISM: uses overridden getFinePerDay() — behaves differently per subclass
    public double calculateFine(int daysLate) {
        if (daysLate <= 0) return 0.0;
        return daysLate * getFinePerDay(); // POLYMORPHISM
    }

    // ABSTRACTION: subclasses define these (TextBook vs Novel have different values)
    public abstract String getItemType();
    public abstract int    getLoanPeriodDays();
    public abstract double getFinePerDay();

    @Override
    public String toString() {
        return getItemType() + ": " + title + " by " + author
               + " [" + availableCopies + "/" + totalCopies + " available]";
    }
}
