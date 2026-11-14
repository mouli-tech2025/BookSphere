package com.library.model;

import com.library.exception.InvalidInputException;

// INHERITANCE: extends LibraryItem
// POLYMORPHISM: provides concrete implementations of abstract methods
public class TextBook extends LibraryItem {

    public TextBook(int id, String title, String author, String isbn,
                    String category, int totalCopies, int availableCopies)
            throws InvalidInputException {
        super(id, title, author, isbn, category, totalCopies, availableCopies);
    }

    // POLYMORPHISM: each subclass returns a different value
    @Override
    public String getItemType()      { return "TEXTBOOK"; }

    @Override
    public int getLoanPeriodDays()   { return 30; }   // TextBook: 30 days

    @Override
    public double getFinePerDay()    { return 1.0; }  // Fine: ₹1 per day
}
