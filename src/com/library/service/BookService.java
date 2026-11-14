package com.library.service;

import com.library.dao.BookDao;
import com.library.exception.*;
import com.library.model.*;
import com.library.util.Validator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Business rules for books.
 * Implements Searchable<LibraryItem> — ABSTRACTION via interface.
 */
public class BookService implements Searchable<LibraryItem> {

    private final BookDao bookDao = new BookDao();

    // POLYMORPHISM (compile-time): overloaded search method (same name, different params)
    @Override
    public List<LibraryItem> search(String keyword) throws LibraryException {
        List<LibraryItem> results = bookDao.search(keyword, null);
        results.sort(Comparator.comparing(LibraryItem::getTitle)); // COLLECTIONS: Comparator sort
        return results;
    }

    // Overloaded search — compile-time polymorphism
    public List<LibraryItem> search(String keyword, String category) throws LibraryException {
        List<LibraryItem> results = bookDao.search(keyword, category);
        results.sort(Comparator.comparing(LibraryItem::getTitle));
        return results;
    }

    public List<LibraryItem> getAllBooks() throws LibraryException {
        return bookDao.findAll();
    }

    public LibraryItem getBookById(int id) throws LibraryException {
        LibraryItem item = bookDao.findById(id);
        if (item == null)
            throw new BookNotFoundException("No book found with ID: " + id);
        return item;
    }

    public LibraryItem addBook(String title, String author, String isbn,
                                String category, String type, String totalCopiesStr)
            throws LibraryException {

        // Validation
        Validator.requireNonBlank(title, "Title");
        Validator.requireNonBlank(author, "Author");
        Validator.requireNonBlank(isbn, "ISBN");
        Validator.requireNonBlank(category, "Category");
        Validator.validateBookType(type);
        int totalCopies = Validator.parsePositiveInt(totalCopiesStr, "Total copies");

        // Duplicate ISBN check
        if (bookDao.findByIsbn(isbn) != null)
            throw new DuplicateEntryException("A book with ISBN '" + isbn + "' already exists.");

        // Create model object (POLYMORPHISM: TextBook or Novel)
        LibraryItem item;
        try {
            if ("TEXTBOOK".equals(type))
                item = new TextBook(0, title, author, isbn, category, totalCopies, totalCopies);
            else
                item = new Novel(0, title, author, isbn, category, totalCopies, totalCopies);
        } catch (InvalidInputException e) {
            throw e; // re-throw as LibraryException subtype
        }

        return bookDao.insert(item);
    }

    public LibraryItem updateBook(int id, String title, String author, String isbn,
                                   String category, String type, String totalCopiesStr)
            throws LibraryException {

        LibraryItem existing = getBookById(id);
        Validator.requireNonBlank(title, "Title");
        Validator.requireNonBlank(author, "Author");
        Validator.requireNonBlank(isbn, "ISBN");
        Validator.requireNonBlank(category, "Category");
        Validator.validateBookType(type);
        int newTotal = Validator.parsePositiveInt(totalCopiesStr, "Total copies");

        // Business rule: totalCopies cannot be less than currently issued
        int issued = bookDao.countIssuedCopies(id);
        if (newTotal < issued)
            throw new InvalidInputException(
                "Cannot reduce copies to " + newTotal + ": " + issued + " copies are currently issued.");

        int newAvailable = newTotal - issued; // recompute available

        try {
            existing.setTitle(title);
            existing.setAuthor(author);
            existing.setIsbn(isbn);
            existing.setCategory(category);
            existing.setTotalCopies(newTotal);
            existing.setAvailableCopies(newAvailable);
        } catch (InvalidInputException e) {
            throw e;
        }

        // If type changed, we need a new object (factory)
        LibraryItem updated;
        try {
            if ("TEXTBOOK".equals(type))
                updated = new TextBook(id, title, author, isbn, category, newTotal, newAvailable);
            else
                updated = new Novel(id, title, author, isbn, category, newTotal, newAvailable);
        } catch (InvalidInputException e) {
            throw e;
        }

        return bookDao.update(updated);
    }

    public void deleteBook(int id) throws LibraryException {
        getBookById(id); // ensures book exists (throws BookNotFoundException if not)
        int issued = bookDao.countIssuedCopies(id);
        if (issued > 0)
            throw new LibraryException(
                "Cannot delete: " + issued + " copies are currently issued.");
        bookDao.delete(id);
    }
}
