📚 Library Management System

A Java OOP Lab mini project: a complete Library Management System built with Core Java, JDBC and MySQL on the backend and a Neo-Brutalism HTML/CSS/JavaScript frontend.

The Java backend contains all business logic. JavaScript only draws the UI and calls the API.

✨ Features
Dashboard: total books, available books, issued books, total students
Books: add, view, search, update, delete
Students: register, view, search
Issue & Return: issue books, return books, check availability, full issue/return history
Automatic due dates and fines that differ by book type (polymorphism)
🛠 Technology Stack
Layer	Technology
Backend	Core Java 17+, com.sun.net.httpserver.HttpServer
Database	MySQL 8 with JDBC (mysql-connector-j driver)
Frontend	HTML, CSS, vanilla JavaScript

No Spring, Hibernate, React, Node.js or Maven is used.

🧱 Architecture
Browser (HTML/CSS/JS) --HTTP--> Handler --> Service --> DAO --JDBC--> MySQL
Layer	Responsibility
Handler	Reads the HTTP request, calls a service, returns JSON
Service	Business rules (availability, fines, validation)
DAO	The only layer that talks to MySQL
Model	OOP classes: Person, Student, LibraryItem, TextBook, Novel, IssueRecord
📁 Folder Structure
LibraryManagementSystem/
├── README.md
├── schema.sql
├── db.properties
├── run.bat / run.sh
├── lib/                      (MySQL JDBC jar)
├── src/com/library/
│   ├── Main.java
│   ├── model/                Person, Student, LibraryItem, TextBook, Novel, IssueRecord
│   ├── exception/            LibraryException + 5 custom exceptions
│   ├── dao/                  DBConnection, BookDao, StudentDao, IssueDao
│   ├── service/              Searchable, BookService, StudentService, IssueService, DashboardService
│   ├── handler/              BaseHandler, Dashboard/Book/Student/Issue/StaticFile handlers
│   ├── util/                 JsonBuilder, FormParser, Validator
│   └── demo/OopDemo.java     Console demo of all 7 OOP concepts
└── frontend/
    ├── dashboard.html, books.html, students.html, issue-return.html
    ├── css/style.css
    └── js/                   api.js, common.js, dashboard.js, books.js, students.js, issue-return.js
⚙️ Setup and Run
Prerequisites
JDK 17 or newer (java -version)
MySQL 8 running locally
mysql-connector-j jar placed in the lib/ folder (download from the MySQL website if it is missing)
Steps
Create the database and sample data
bash
mysql -u root -p < schema.sql
Edit db.properties with your MySQL details:
properties
db.url=jdbc:mysql://localhost:3306/library_db
db.user=root
db.password=your_password
Compile and start the server
Windows: double-click run.bat
Linux/Mac: chmod +x run.sh && ./run.sh
Open the app: http://localhost:8080
Run the OOP demo (no database needed)
bash
# Windows
java -cp "out;lib/*" com.library.demo.OopDemo
# Linux/Mac
java -cp "out:lib/*" com.library.demo.OopDemo
🔌 API Reference

All responses look like {"success": true, "message": "...", "data": ...}. POST/PUT bodies are form-urlencoded.

Method	Endpoint	Purpose
GET	/api/dashboard	Dashboard statistics
GET	/api/books?query=&category=	List/search books
GET	/api/books/{id}	Get one book
GET	/api/books/{id}/availability	Check availability
POST	/api/books	Add book
PUT	/api/books/{id}	Update book
DELETE	/api/books/{id}	Delete book
GET	/api/students?query=	List/search students
POST	/api/students	Register student
POST	/api/issue	Issue a book (studentId, bookId)
POST	/api/return	Return a book (issueId)
GET	/api/issues?status=	Issue/return records
🗄 Database
Table	Key columns
students	student_id (PK), name, email (unique), phone, roll_no (unique), department
books	book_id (PK), title, author, isbn (unique), category, book_type, total_copies, available_copies
issue_records	issue_id (PK), book_id (FK), student_id (FK), issue_date, due_date, return_date, fine, status

One student has many issue records, and one book has many issue records.

🎯 OOP Concepts Used
#	Concept	Where it is used
1	Classes & Objects	Student, TextBook, Novel, IssueRecord objects are created from database rows and used by services
2	Encapsulation	Private fields with validated getters/setters in Person, LibraryItem, IssueRecord (e.g. invalid email throws an exception)
3	Inheritance	Student extends Person; TextBook and Novel extend LibraryItem; all handlers extend BaseHandler
4	Polymorphism	Runtime: calculateFine(), getLoanPeriodDays() behave differently for TextBook and Novel through a LibraryItem reference. Compile-time: overloaded search(keyword) and search(keyword, category) in BookService
5	Abstraction	Abstract classes Person, LibraryItem, BaseHandler; interface Searchable<T>
6	Collections	ArrayList, HashMap/LinkedHashMap, Comparator sorting, iteration over lists of mixed objects
7	Exception Handling	Custom checked exceptions, try/catch/finally, try-with-resources, errors converted to clean JSON messages
Class Relationships
Person (abstract)              LibraryItem (abstract)
   ▲                              ▲            ▲
   │                              │            │
Student                       TextBook        Novel
                              (30 days, ₹1)  (14 days, ₹2)

IssueRecord ── refers to ──> Student (studentId) and LibraryItem (bookId)

LibraryException
   ├── BookNotFoundException
   ├── StudentNotFoundException
   ├── BookNotAvailableException
   ├── DuplicateEntryException
   └── InvalidInputException
How polymorphism works in this project

BookDao.createItem(type, ...) is a small factory method. It reads the book_type column and returns either a TextBook or a Novel, but the rest of the code only sees a LibraryItem. When a book is issued or returned, item.getLoanPeriodDays() and item.calculateFine(daysLate) run the version belonging to the real object. The same late return costs ₹1 per day for a textbook and ₹2 per day for a novel.

Business rules
A student can hold at most 3 books at once, and cannot issue the same book twice while it is still issued
A book with zero available copies cannot be issued
A book that is currently issued cannot be deleted
Issue and return run in a single JDBC transaction (commit or rollback)
Duplicate email, roll number or ISBN is rejected
🎤 Suggested Viva Demo Flow
Run OopDemo and walk through the 7 labelled sections one by one.
Open the dashboard and show the live numbers.
Add a book (a Novel), then a TextBook, and point out their different loan periods.
Register a student and issue both books. Show how available copies and the dashboard change.
Try to issue a book with 0 copies to show exception handling.
Return a book and explain the fine calculation.
❓ Likely Viva Questions
Why is Person abstract? We never need a plain Person. It defines common data and forces subclasses to implement getRole().
What is the difference between an abstract class and an interface here? LibraryItem shares fields and code with subclasses. Searchable only declares a behaviour that different services implement.
Where is runtime polymorphism? calculateFine() and getLoanPeriodDays() called on a LibraryItem reference.
Where is compile-time polymorphism? The overloaded search methods in BookService.
How is encapsulation achieved? Private fields, with setters that validate and throw InvalidInputException.
Why custom exceptions? They give clear, specific error messages and let handlers return the right HTTP status.
Which collections are used and why? ArrayList for ordered result lists, LinkedHashMap for dashboard statistics, Comparator for sorting.
Why use PreparedStatement? It prevents SQL injection and handles data types safely.
Why a transaction for issue/return? The issue record and the copy count must change together, or not at all.
Why is the logic in Java and not JavaScript? The assignment is about OOP in Java. JavaScript only displays data and calls the API.
🧯 Troubleshooting
Problem	Fix
ClassNotFoundException: com.mysql.cj.jdbc.Driver	Put the mysql-connector-j jar in lib/
Access denied for user	Correct the username/password in db.properties
Unknown database 'library_db'	Run mysql -u root -p < schema.sql
Address already in use	Another program is using port 8080; stop it or change the port in Main.java
Page shows no data	Make sure the server is running and open it via http://localhost:8080, not by double-clicking the HTML file

Developed as a mini project for the Object-Oriented Programming (OOPs) Lab.