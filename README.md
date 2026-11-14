# BookSphere — Object-Oriented Library Management System (Mini Project)

An academic, production-quality, yet student-friendly **Library Management System** built with **Core Java 17+** and **MySQL 8**, featuring a high-impact **Neo-Brutalism UI**.

Designed specifically for college **Object-Oriented Programming (OOP) Lab Mini Projects**, viva demonstrations, and technical examinations.

---

## 📑 Table of Contents
1. [Project Overview](#project-overview)
2. [Key Features](#key-features)
3. [Strict Technology Stack](#strict-technology-stack)
4. [Project Folder Structure](#project-folder-structure)
5. [Prerequisites](#prerequisites)
6. [Step-by-Step Setup Guide](#step-by-step-setup-guide)
7. [Running the Application](#running-the-application)
8. [Running the Standalone OOP Demo](#running-the-standalone-oop-demo)
9. [REST API Endpoints Specification](#rest-api-endpoints-specification)
10. [Mapping the 7 OOP Concepts to Code](#mapping-the-7-oop-concepts-to-code)
11. [Class Relationship Diagram (ASCII)](#class-relationship-diagram-ascii)
12. [How Polymorphism & Factory Method Work](#how-polymorphism--factory-method-work)
13. [Sample Demo Flow for Viva & Evaluation](#sample-demo-flow-for-viva--evaluation)
14. [10 Likely Viva Questions & Answers](#10-likely-viva-questions--answers)
15. [Troubleshooting Guide](#troubleshooting-guide)

---

## 1. Project Overview

**BookSphere** is a complete, working Library Management System developed without enterprise frameworks (no Spring Boot, no Hibernate, no Maven/Gradle, no Node.js, no frontend frameworks). 

Instead, it relies purely on the standard **JDK 17+** standard library (including `com.sun.net.httpserver.HttpServer`), pure **JDBC** with `PreparedStatement`, and standard web technologies (vanilla HTML5, CSS3, JavaScript `fetch`).

### Architecture: 4-Layer Separation of Concerns
```text
┌────────────────────────────────────────────────────────┐
│               Browser (HTML5 / CSS3 / JS)              │
└───────────────────────────┬────────────────────────────┘
                            │ HTTP Request (Form-UrlEncoded)
                            ▼
┌────────────────────────────────────────────────────────┐
│     Handler Layer (BaseHandler, BookHandler, etc.)      │
└───────────────────────────┬────────────────────────────┘
                            │ Java Method Invocations
                            ▼
┌────────────────────────────────────────────────────────┐
│     Service Layer (BookService, IssueService, etc.)    │
│            * Business rules & validation *             │
└───────────────────────────┬────────────────────────────┘
                            │ POJOs / Exception Handling
                            ▼
┌────────────────────────────────────────────────────────┐
│       DAO Layer (BookDao, StudentDao, IssueDao)        │
│          * PreparedStatement & Transactions *          │
└───────────────────────────┬────────────────────────────┘
                            │ Plain JDBC
                            ▼
┌────────────────────────────────────────────────────────┐
│                      MySQL 8 Database                  │
└────────────────────────────────────────────────────────┘
```

The single Java HTTP server on port `8080` serves both the static frontend files (`/` serves `frontend/`) and the dynamic JSON REST APIs (`/api/*`), avoiding CORS issues and simplifying local deployment to a single command.

---

## 2. Key Features

- **Dashboard**: Live metrics showing Total Books, Available Copies, Active Issues, and Registered Students, along with a recent circulation history table.
- **Book Management**: Full CRUD (Create, Read, Update, Delete) with search by title/author/ISBN, category filtering, and real-time availability badges.
- **Student Directory**: Student registration with strict validation (10-digit phone number, valid university email format, unique roll number).
- **Circulation Desk (Issue & Return)**:
  - Atomic transactions using JDBC `setAutoCommit(false)`, `commit()`, and `rollback()`.
  - Student circulation quota enforcement (maximum 3 active issued books per student).
  - Duplicate issue prevention (cannot issue the same book twice to the same student).
  - Dynamic due date generation based on book classification (TextBook: 30 days, Novel: 14 days).
  - Polymorphic late fine calculation (TextBook: ₹1.00/day, Novel: ₹2.00/day).
- **Neo-Brutalism UI**: Bold aesthetics, high-contrast borders (`3px solid #111`), offset drop-shadows (`6px 6px 0 #111`), responsive grid layout, and interactive toasts.

---

## 3. Strict Technology Stack

- **Backend**: Core Java 17+ (JDK standard library only, `com.sun.net.httpserver.HttpServer`)
- **Database**: MySQL 8.x
- **Database Access**: Plain JDBC using `PreparedStatement`
- **External JAR**: MySQL Connector/J driver (`lib/mysql-connector-j-*.jar`)
- **Frontend**: Vanilla HTML5, CSS3 (Neo-Brutalism Design System), Vanilla JavaScript (Native `fetch` API)
- **Zero Third-Party Frameworks**: No Spring, No Hibernate, No Maven, No Gradle, No npm, No React, No Lombok, No Jackson/Gson.

---

## 4. Project Folder Structure

```text
LibraryManagementSystem/
├── README.md                           # Documentation & Viva preparation
├── schema.sql                          # MySQL database schema & sample dataset
├── db.properties                       # Database credentials config
├── run.bat                             # Windows 1-click compile & run script
├── run.sh                              # Linux / macOS 1-click compile & run script
├── lib/
│   └── mysql-connector-j-26.10.0.jar   # MySQL JDBC Driver JAR
├── src/com/library/
│   ├── Main.java                       # Server entry point & route registration
│   ├── model/                          # OOP Entities (Encapsulation & Inheritance)
│   │   ├── Person.java                 # Abstract base class
│   │   ├── Student.java                # Concrete subclass of Person
│   │   ├── LibraryItem.java            # Abstract base class for books
│   │   ├── TextBook.java               # Concrete subclass (30 days, ₹1/day)
│   │   ├── Novel.java                  # Concrete subclass (14 days, ₹2/day)
│   │   └── IssueRecord.java            # Circulation entity
│   ├── exception/                      # Custom checked exception hierarchy
│   │   ├── LibraryException.java       # Base checked exception
│   │   ├── BookNotFoundException.java
│   │   ├── StudentNotFoundException.java
│   │   ├── BookNotAvailableException.java
│   │   ├── DuplicateEntryException.java
│   │   └── InvalidInputException.java
│   ├── dao/                            # Data Access Objects (JDBC & SQL)
│   │   ├── DBConnection.java           # Reads db.properties, supplies Connection
│   │   ├── BookDao.java                # Polymorphic Factory Method & CRUD
│   │   ├── StudentDao.java             # Student queries & registration
│   │   └── IssueDao.java               # Atomic circulation transactions
│   ├── service/                        # Business logic & Validation
│   │   ├── Searchable.java             # Generic interface abstraction
│   │   ├── BookService.java            # Overloaded search & book rules
│   │   ├── StudentService.java         # Student search & validation
│   │   ├── IssueService.java           # Fine calculation & issue quota checks
│   │   └── DashboardService.java       # LinkedHashMap aggregation metrics
│   ├── handler/                        # HTTP endpoints & routing
│   │   ├── BaseHandler.java            # Abstract HTTP handler with helper methods
│   │   ├── DashboardHandler.java       # /api/dashboard endpoint
│   │   ├── BookHandler.java            # /api/books endpoints
│   │   ├── StudentHandler.java         # /api/students endpoints
│   │   ├── IssueHandler.java           # /api/issue, /api/return, /api/issues
│   │   └── StaticFileHandler.java      # Serves frontend static assets
│   ├── util/                           # Helper utilities
│   │   ├── JsonBuilder.java            # Handcrafted JSON builder
│   │   ├── FormParser.java             # URL-encoded query and body parser
│   │   └── Validator.java              # Static input validation routines
│   └── demo/
│       └── OopDemo.java                # Console demo testing all 7 OOP concepts
└── frontend/
    ├── dashboard.html                  # Landing page & statistical overview
    ├── books.html                      # Book catalog with search & modals
    ├── students.html                   # Student roster & registration modal
    ├── issue-return.html               # Circulation desk interface
    ├── css/
    │   └── style.css                   # Unified Neo-Brutalism design system
    └── js/
        ├── api.js                      # Fetch API wrapper
        ├── common.js                   # Navigation, toast & modal helpers
        ├── dashboard.js                # Dashboard API integration
        ├── books.js                    # Books API integration
        ├── students.js                 # Students API integration
        └── issue-return.js             # Issue/Return API integration
```

---

## 5. Prerequisites

Before running the project, ensure you have:
1. **JDK 17 or higher** installed. Verify with:
   ```bash
   javac -version
   java -version
   ```
2. **MySQL Server 8.0+** running locally. Verify with:
   ```bash
   mysql --version
   ```

---

## 6. Step-by-Step Setup Guide

### Step 1: Clone or Navigate to the Workspace
```bash
cd BookSphere
```

### Step 2: Import the Database Schema
Launch MySQL and source the provided `schema.sql` script:
```bash
mysql -u root -p < schema.sql
```
*Note: This creates the `library_db` database, sets up the 3 tables (`students`, `books`, `issue_records`), and seeds sample data (8 students, 12 books, 5 circulation records).*

### Step 3: Configure Database Credentials
Open `db.properties` in any text editor and update the username/password to match your local MySQL configuration:
```properties
db.url=jdbc:mysql://localhost:3306/library_db
db.user=root
db.password=your_mysql_password
```

---

## 7. Running the Application

### On Linux or macOS:
```bash
./run.sh
```

### On Windows:
Double-click `run.bat` or run in Command Prompt:
```cmd
run.bat
```

### Accessing the Web Application
Open your browser and navigate to:
```text
http://localhost:8080
```

---

## 8. Running the Standalone OOP Demo

The project includes a standalone console demonstration that tests and showcases all 7 Object-Oriented Programming concepts in action **without needing MySQL or the HTTP server**.

### Running via Script:
```bash
# On Linux / macOS:
./run.sh demo

# On Windows:
run.bat demo
```

### Running Manually:
```bash
# Compile (if not already compiled)
javac -cp "lib/*" -d out src/com/library/demo/OopDemo.java

# Execute
java -cp "out:lib/*" com.library.demo.OopDemo
```

---

## 9. REST API Endpoints Specification

All API endpoints return a standardized JSON envelope:
```json
{
  "success": true,
  "message": "Human readable status message",
  "data": { ... }
}
```

| Method | Endpoint | Parameters / Body | Description |
|---|---|---|---|
| `GET` | `/api/dashboard` | None | Returns totalBooks, availableBooks, issuedBooks, totalStudents |
| `GET` | `/api/books` | `query` (optional), `category` (optional) | List or search books |
| `GET` | `/api/books/{id}` | None | Fetch single book details |
| `GET` | `/api/books/{id}/availability` | None | Returns available boolean and availableCopies count |
| `POST` | `/api/books` | `title`, `author`, `isbn`, `category`, `type`, `totalCopies` | Add a new book |
| `PUT` | `/api/books/{id}` | `title`, `author`, `isbn`, `category`, `type`, `totalCopies` | Update existing book |
| `DELETE` | `/api/books/{id}` | None | Delete book (fails if currently issued) |
| `GET` | `/api/students` | `query` (optional) | List or search students |
| `POST` | `/api/students` | `name`, `email`, `phone`, `rollNo`, `department` | Register a new student |
| `POST` | `/api/issue` | `studentId`, `bookId` | Issue a book to a student |
| `POST` | `/api/return` | `issueId` | Return an issued book & calculate fine |
| `GET` | `/api/issues` | `status` (optional: `ISSUED` or `RETURNED`) | Retrieve circulation history |

---

## 10. Mapping the 7 OOP Concepts to Code

| OOP Concept | Implementation Location(s) | Explanation in Context of the Project |
|---|---|---|
| **1. Classes & Objects** | `Person.java`, `Student.java`, `LibraryItem.java`, `TextBook.java`, `Novel.java`, `IssueRecord.java` | Real-world library entities are modeled as Java classes with fields and methods. Instances (objects) are dynamically instantiated from MySQL query results. |
| **2. Encapsulation** | `Person.java`, `LibraryItem.java`, `IssueRecord.java` | All entity fields are declared `private`. Access is strictly controlled through getters and validated setters that reject invalid inputs (e.g. blank titles, invalid emails, negative copies). |
| **3. Inheritance** | `Student extends Person`<br>`TextBook extends LibraryItem`<br>`Novel extends LibraryItem`<br>`Handlers extend BaseHandler` | Code reusability and hierarchical modeling. `Student` inherits personal attributes (`name`, `email`, `phone`) from `Person` via `super()`. Handlers inherit HTTP response and routing utilities from `BaseHandler`. |
| **4. Polymorphism** | **Runtime**: `LibraryItem.calculateFine()` and `getLoanPeriodDays()` overridden in `TextBook` vs `Novel`.<br>**Compile-time**: `BookService.search(keyword)` vs `BookService.search(keyword, category)` | At runtime, `LibraryItem` references invoke subclass-specific logic dynamically (TextBook = 30 days & ₹1/day; Novel = 14 days & ₹2/day). At compile time, overloaded search methods offer flexible query signatures. |
| **5. Abstraction** | `abstract class Person`<br>`abstract class LibraryItem`<br>`interface Searchable<T>`<br>`abstract class BaseHandler` | Hiding implementation complexity while establishing a strict contract. Abstract classes define methods that subclasses must implement. `Searchable<T>` decouples search callers from concrete service implementations. |
| **6. Collections** | `List<LibraryItem>`, `ArrayList`, `LinkedHashMap<String, Integer>`, `Collections.sort` | `ArrayList` stores collections of polymorphic items. `Comparator.comparing()` sorts books by title. `LinkedHashMap` maintains ordered key-value pairs for dashboard metrics. |
| **7. Exception Handling** | `com.library.exception.*`, `try-with-resources`, `try-catch-finally` | Custom checked exception hierarchy rooted at `LibraryException`. All database access utilizes automatic resource cleanup via `try-with-resources`. Handlers map exceptions into appropriate HTTP status codes (400, 404, 500). |

---

## 11. Class Relationship Diagram (ASCII)

```text
                     ┌───────────────────────┐
                     │    <<interface>>      │
                     │     Searchable<T>     │
                     └───────────▲───────────┘
                                 │ implements
                ┌────────────────┴───────────────┐
                │                                │
    ┌───────────────────────┐        ┌───────────────────────┐
    │      BookService      │        │    StudentService     │
    └───────────┬───────────┘        └───────────┬───────────┘
                │ uses                           │ uses
                ▼                                ▼
    ┌───────────────────────┐        ┌───────────────────────┐
    │        BookDao        │        │      StudentDao       │
    └───────────┬───────────┘        └───────────┬───────────┘
                │ creates/returns                │ creates/returns
                ▼                                ▼
    ┌───────────────────────┐        ┌───────────────────────┐
    │      <<abstract>>     │        │      <<abstract>>     │
    │      LibraryItem      │        │         Person        │
    └───────────▲───────────┘        └───────────▲───────────┘
                │ extends                        │ extends
        ┌───────┴───────┐                        │
        │               │                        │
┌───────────────┐ ┌───────────┐          ┌───────────────┐
│   TextBook    │ │   Novel   │          │    Student    │
└───────────────┘ └───────────┘          └───────────────┘
```

---

## 12. How Polymorphism & Factory Method Work

### The Factory Method (`BookDao.createItem`)
The database contains a `book_type` column storing `'TEXTBOOK'` or `'NOVEL'`. When querying MySQL, `BookDao` inspects this column and instantiates the correct subclass:

```java
// BookDao.java
private LibraryItem createItem(ResultSet rs) throws SQLException, InvalidInputException {
    String type = rs.getString("book_type");
    if ("TEXTBOOK".equals(type)) {
        return new TextBook(...);
    } else {
        return new Novel(...);
    }
}
```

### Runtime Dynamic Method Dispatch
When a book is issued or returned, the system treats it uniformly as a `LibraryItem`:

```java
// IssueService.java
LibraryItem item = bookService.getBookById(bookId);

// POLYMORPHIC CALL:
// If item is a TextBook -> returns 30 days
// If item is a Novel    -> returns 14 days
LocalDate dueDate = today.plusDays(item.getLoanPeriodDays());

// When returning late:
// If TextBook -> daysLate * ₹1.00
// If Novel    -> daysLate * ₹2.00
double fine = item.calculateFine(daysLate);
```
Neither `BookDao`, `IssueService`, nor `IssueHandler` needs to know the concrete type of the book; Java's virtual method table handles the dynamic dispatch at runtime.

---

## 13. Sample Demo Flow for Viva & Evaluation

1. **Start with the OOP Demo Console Application**:
   - Run `./run.sh demo` (or `run.bat demo`).
   - Walk the examiner through each of the 7 labeled sections printed in the terminal.
2. **Start the Web Server**:
   - Run `./run.sh` (or `run.bat`).
   - Point the browser to `http://localhost:8080`.
   - Show the Neo-Brutalism dashboard displaying real database counts.
3. **Demonstrate Encapsulation & Validation**:
   - Go to **Students** -> click **+ Register Student**.
   - Enter an invalid email (`student123`) or a short phone number (`987`) to demonstrate validation rejection.
   - Enter valid details and confirm the student appears immediately in the table.
4. **Demonstrate Polymorphism (Circulation Rules)**:
   - Go to **Issue & Return**.
   - Issue a **TEXTBOOK** (e.g. *Head First Java*) to a student -> observe due date is 30 days ahead.
   - Issue a **NOVEL** (e.g. *The Alchemist*) to a student -> observe due date is 14 days ahead.
5. **Demonstrate Business Rules & Edge Cases**:
   - Attempt to issue a book that has 0 available copies -> observe error toast.
   - Attempt to issue a 4th book to a student already holding 3 books -> observe maximum quota rejection.
   - Attempt to delete a book that is currently issued -> observe deletion prevention.

---

## 14. 10 Likely Viva Questions & Answers

### Q1: What is the difference between an abstract class and an interface in your project?
> **Answer**: `LibraryItem` and `Person` are abstract classes because they provide common state (fields like `id`, `name`, `title`), constructor logic, and concrete utility methods (`calculateFine`, `isAvailable`) that subclasses share. In contrast, `Searchable<T>` is an interface because it defines only a pure contract (`List<T> search(String keyword)`) with no state, allowing unrelated classes (`BookService` and `StudentService`) to implement search capabilities independently.

### Q2: How did you implement Encapsulation in your model classes?
> **Answer**: In `Person`, `LibraryItem`, and `IssueRecord`, all member variables are declared `private`. External access is restricted to public getters and setters. Furthermore, setters contain defensive validation logic (e.g. checking for `@` in emails, enforcing 10-digit phone numbers, and ensuring copies cannot be negative) that throws `InvalidInputException` if violated.

### Q3: How is Polymorphism demonstrated during book return?
> **Answer**: In `IssueService.returnBook()`, we retrieve a `LibraryItem` reference. Calling `item.calculateFine(daysLate)` automatically invokes the overridden `calculateFine()` implementation in either `TextBook` (charging ₹1/day) or `Novel` (charging ₹2/day) without using `if-else` or type checks.

### Q4: Why did you use `PreparedStatement` instead of `Statement`?
> **Answer**: 
> 1. **Security**: `PreparedStatement` uses parameterized queries, which prevents SQL Injection attacks by treating input as literal data rather than executable SQL.
> 2. **Performance**: MySQL pre-compiles the SQL query template and reuses the execution plan for subsequent executions.

### Q5: How do you guarantee atomicity when issuing a book?
> **Answer**: In `IssueDao.issueBook()`, we turn off auto-commit (`conn.setAutoCommit(false)`). We insert the row into `issue_records` and decrement `available_copies` in `books` within the same transaction. If any step fails, `conn.rollback()` restores the database state, preventing inconsistencies.

### Q6: How does the application parse requests and format responses without Jackson or Gson?
> **Answer**: 
> - Incoming requests use standard `application/x-www-form-urlencoded` bodies and query parameters, parsed by our lightweight `FormParser` using standard `URLDecoder`.
> - Outgoing JSON is constructed by our custom `JsonBuilder` utility class, which handles string escaping (`\n`, `\"`, `\\`) and formats the standardized JSON envelope.

### Q7: What is the role of `try-with-resources` in your DAO classes?
> **Answer**: `try-with-resources` automatically closes resources that implement `java.lang.AutoCloseable` (such as `Connection`, `PreparedStatement`, and `ResultSet`) when the block exits, even if an exception occurs. This eliminates database connection leaks.

### Q8: What is Method Overloading, and where did you use it?
> **Answer**: Method Overloading is compile-time polymorphism where two or more methods in the same class share the same name but have different parameter lists. In `BookService`, we have `search(String keyword)` and `search(String keyword, String category)`.

### Q9: Why is `DBConnection` placed in the `dao` package?
> **Answer**: Following the Data Access Object (DAO) pattern, the DAO layer is the sole layer responsible for interacting with the database. Placing connection management within the DAO package encapsulates low-level JDBC details away from services and handlers.

### Q10: How does your frontend communicate with the backend?
> **Answer**: The frontend uses standard vanilla JavaScript `fetch()` calls wrapped in `api.js`. The backend's `HttpServer` serves both static files (`StaticFileHandler`) and API routes (`/api/*`), eliminating cross-origin resource sharing (CORS) complications.

---

## 15. Troubleshooting Guide

| Issue | Root Cause | Solution |
|---|---|---|
| **`CommunicationsException: Communications link failure`** | MySQL server is stopped or port `3306` is not reachable. | Start MySQL service (`brew services start mysql` on macOS, or `net start MySQL80` on Windows). |
| **`Access denied for user 'root'@'localhost'`** | Incorrect password in `db.properties`. | Open `db.properties` and verify `db.user` and `db.password` match your MySQL root credentials. |
| **`Unknown database 'library_db'`** | Database was not imported. | Run `mysql -u root -p < schema.sql` in terminal. |
| **`Could not start server on port 8080: Address already in use`** | Another application (or previous Java instance) is using port 8080. | Find and kill the process: macOS/Linux: `lsof -ti :8080 \| xargs kill -9`, Windows: `netstat -ano \| findstr :8080` followed by `taskkill /F /PID <pid>`. |
| **`ClassNotFoundException: com.mysql.cj.jdbc.Driver`** | Driver JAR missing from classpath. | Ensure `lib/mysql-connector-j-*.jar` exists and classpath is passed via `-cp "lib/*:out"`. |

---
*Built with Core Java & Passion for Clean Software Architecture.*
