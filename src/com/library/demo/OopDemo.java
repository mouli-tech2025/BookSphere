package com.library.demo;

import com.library.exception.*;
import com.library.model.*;
import java.util.*;

/**
 * OopDemo — standalone demo of all 7 OOP concepts.
 * No database or server needed. Run with:
 *   java -cp out com.library.demo.OopDemo
 */
public class OopDemo {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║      LIBRARY MANAGEMENT SYSTEM — OOP DEMO       ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        demo1_ClassesAndObjects();
        demo2_Encapsulation();
        demo3_Inheritance();
        demo4_Polymorphism();
        demo5_Abstraction();
        demo6_Collections();
        demo7_ExceptionHandling();

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║           ALL 7 OOP CONCEPTS DEMONSTRATED        ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    // ─────────────────────────────────────────────────────────
    static void demo1_ClassesAndObjects() {
        System.out.println("===== 1. CLASSES & OBJECTS =====");
        try {
            // Creating objects from classes
            Student s = new Student(1, "Aarav Sharma", "aarav@college.edu",
                                    "9876500001", "21CS001", "CSE");
            TextBook tb = new TextBook(1, "Data Structures in Java", "Robert Lafore",
                                       "9780672324536", "Computer Science", 5, 5);
            System.out.println("Student object  : " + s.getDisplayInfo());
            System.out.println("TextBook object : " + tb);
            System.out.println("Student role    : " + s.getRole());
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    static void demo2_Encapsulation() {
        System.out.println("===== 2. ENCAPSULATION =====");
        try {
            Student s = new Student(2, "Priya Das", "priya@college.edu",
                                    "9876500002", "21CS014", "CSE");
            System.out.println("Valid student email: " + s.getEmail());

            // Try to set an invalid email — setter throws InvalidInputException
            System.out.println("Attempting to set invalid email 'not-an-email'...");
            s.setEmail("not-an-email"); // This will throw
        } catch (InvalidInputException e) {
            System.out.println("Caught InvalidInputException: " + e.getMessage());
        }

        try {
            // Try invalid phone
            System.out.println("Attempting to set invalid phone '123'...");
            Student s2 = new Student(3, "Test", "test@a.com", "123", "21CS999", "CSE");
        } catch (InvalidInputException e) {
            System.out.println("Caught InvalidInputException: " + e.getMessage());
        }
        System.out.println("Fields are private — only accessible via validated setters!\n");
    }

    // ─────────────────────────────────────────────────────────
    static void demo3_Inheritance() {
        System.out.println("===== 3. INHERITANCE =====");
        try {
            Student s = new Student(4, "Rohan Mukherjee", "rohan@college.edu",
                                    "9876500003", "21IT007", "IT");

            // Student inherits getName(), getEmail(), getPhone() from Person
            System.out.println("Inherited getName()  : " + s.getName());
            System.out.println("Inherited getEmail() : " + s.getEmail());
            System.out.println("Inherited getPhone() : " + s.getPhone());

            // Student's own fields
            System.out.println("Student getRollNo()    : " + s.getRollNo());
            System.out.println("Student getDepartment(): " + s.getDepartment());

            // TextBook inherits from LibraryItem
            TextBook tb = new TextBook(2, "Database System Concepts", "Silberschatz",
                                       "9780078022159", "Computer Science", 4, 4);
            System.out.println("TextBook inherits getTitle(): " + tb.getTitle());
            System.out.println("TextBook inherits isAvailable(): " + tb.isAvailable());
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    static void demo4_Polymorphism() {
        System.out.println("===== 4. POLYMORPHISM =====");
        try {
            // --- Runtime polymorphism ---
            // List<LibraryItem> holds BOTH TextBook and Novel objects
            List<LibraryItem> items = new ArrayList<>();
            items.add(new TextBook(1, "Data Structures in Java", "Lafore",
                                   "9780672324536", "CS", 5, 5));
            items.add(new Novel(7, "The Alchemist", "Paulo Coelho",
                                "9780062315007", "Fiction", 4, 4));
            items.add(new TextBook(2, "Database Concepts", "Silberschatz",
                                   "9780078022159", "CS", 4, 4));
            items.add(new Novel(8, "Wings of Fire", "A P J Abdul Kalam",
                                "9788173711466", "Biography", 3, 3));

            System.out.println("--- Runtime Polymorphism (same method, different behaviour) ---");
            int daysLate = 5;
            for (LibraryItem item : items) {
                // item.getItemType(), getLoanPeriodDays(), calculateFine() behave
                // DIFFERENTLY depending on whether item is a TextBook or Novel
                double fine = item.calculateFine(daysLate); // POLYMORPHISM
                System.out.printf("  %-10s | %-35s | Loan: %2d days | Fine for %d days late: \u20b9%.1f%n",
                    item.getItemType(), item.getTitle(),
                    item.getLoanPeriodDays(), daysLate, fine);
            }

            // --- Compile-time polymorphism (method overloading) ---
            System.out.println("\n--- Compile-time Polymorphism (overloaded search method) ---");
            System.out.println("  BookService.search(String keyword)             — overload 1");
            System.out.println("  BookService.search(String keyword, String cat) — overload 2");
            System.out.println("  (Both methods have the same name but different parameters)");

        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    static void demo5_Abstraction() {
        System.out.println("===== 5. ABSTRACTION =====");
        // Cannot instantiate abstract classes:
        // new Person(...) → compile error
        // new LibraryItem(...) → compile error
        System.out.println("Person and LibraryItem are abstract — cannot be instantiated directly.");
        System.out.println("They define a contract (abstract methods) that subclasses must fulfil.");

        // Searchable is an interface (another form of abstraction)
        // BookService and StudentService implement Searchable<T>
        System.out.println("Searchable<T> interface: BookService and StudentService implement search(String).");
        System.out.println("Callers depend on the interface, not the concrete class — loose coupling!\n");
    }

    // ─────────────────────────────────────────────────────────
    static void demo6_Collections() {
        System.out.println("===== 6. COLLECTIONS =====");
        try {
            // ArrayList
            List<LibraryItem> books = new ArrayList<>();
            books.add(new TextBook(1, "Data Structures in Java", "Lafore", "ISBN1", "CS", 5, 5));
            books.add(new Novel(2, "The Alchemist", "Coelho", "ISBN2", "Fiction", 4, 4));
            books.add(new TextBook(3, "Head First Java", "Sierra", "ISBN3", "Programming", 6, 6));

            // Sorting with Comparator (by title)
            books.sort(Comparator.comparing(LibraryItem::getTitle));
            System.out.println("Books sorted by title (ArrayList + Comparator):");
            for (LibraryItem b : books) { // enhanced-for loop (Iterator under the hood)
                System.out.println("  " + b.getTitle() + " [" + b.getItemType() + "]");
            }

            // HashMap for dashboard stats
            Map<String, Integer> stats = new LinkedHashMap<>();
            stats.put("totalBooks",    15);
            stats.put("issuedBooks",   3);
            stats.put("totalStudents", 8);
            stats.put("availableBooks", 12);

            System.out.println("\nDashboard stats (LinkedHashMap — insertion-order preserved):");
            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    static void demo7_ExceptionHandling() {
        System.out.println("===== 7. EXCEPTION HANDLING =====");
        // try-with-resources, custom exception hierarchy, try-catch-finally

        // Test: BookNotAvailableException
        try {
            TextBook tb = new TextBook(5, "No Copies Book", "Author", "ISBN99",
                                       "CS", 3, 0); // 0 available copies
            System.out.println("Attempting to issue a book with 0 copies...");
            tb.decreaseAvailable(); // throws BookNotAvailableException
        } catch (BookNotAvailableException e) {
            System.out.println("Caught BookNotAvailableException: " + e.getMessage());
        } catch (InvalidInputException e) {
            System.out.println("Caught InvalidInputException: " + e.getMessage());
        } finally {
            System.out.println("  [finally block always executes — cleanup here]");
        }

        // Test: custom exception hierarchy
        try {
            throw new DuplicateEntryException("A student with roll number 21CS001 already exists.");
        } catch (LibraryException e) {
            // LibraryException catches all subclass exceptions (IS-A relationship)
            System.out.println("\nCaught as LibraryException (parent): " + e.getMessage());
            System.out.println("Actual type: " + e.getClass().getSimpleName());
        }

        System.out.println("\nException hierarchy:");
        System.out.println("  Exception");
        System.out.println("  └── LibraryException");
        System.out.println("      ├── BookNotFoundException");
        System.out.println("      ├── StudentNotFoundException");
        System.out.println("      ├── BookNotAvailableException");
        System.out.println("      ├── DuplicateEntryException");
        System.out.println("      └── InvalidInputException");
        System.out.println();
    }
}
