package com.library.model;

import com.library.exception.InvalidInputException;

// INHERITANCE: extends LibraryItem
// POLYMORPHISM: different loan period and fine rate from TextBook
public class Novel extends LibraryItem {

    public Novel(int id, String title, String author, String isbn,
                 String category, int totalCopies, int availableCopies)
            throws InvalidInputException {
        super(id, title, author, isbn, category, totalCopies, availableCopies);
    }

    // POLYMORPHISM: Novel has shorter loan period and higher fine
    @Override
    public String getItemType()      { return "NOVEL"; }

    @Override
    public int getLoanPeriodDays()   { return 14; }   // Novel: 14 days

    @Override
    public double getFinePerDay()    { return 2.0; }  // Fine: ₹2 per day
}
