-- Run with:  mysql -u root -p < schema.sql
DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db;
USE library_db;

CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    phone      VARCHAR(15)  NOT NULL,
    roll_no    VARCHAR(30)  NOT NULL UNIQUE,
    department VARCHAR(60)  NOT NULL
);

CREATE TABLE books (
    book_id          INT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(150) NOT NULL,
    author           VARCHAR(100) NOT NULL,
    isbn             VARCHAR(30)  NOT NULL UNIQUE,
    category         VARCHAR(60)  NOT NULL,
    book_type        ENUM('TEXTBOOK','NOVEL') NOT NULL,
    total_copies     INT NOT NULL,
    available_copies INT NOT NULL
);

CREATE TABLE issue_records (
    issue_id    INT AUTO_INCREMENT PRIMARY KEY,
    book_id     INT NOT NULL,
    student_id  INT NOT NULL,
    issue_date  DATE NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE NULL,
    fine        DECIMAL(8,2) NOT NULL DEFAULT 0,
    status      ENUM('ISSUED','RETURNED') NOT NULL DEFAULT 'ISSUED',
    FOREIGN KEY (book_id)    REFERENCES books(book_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- ---------- Sample students ----------
INSERT INTO students (name, email, phone, roll_no, department) VALUES
('Aarav Sharma',    'aarav.sharma@college.edu',  '9876500001', '21CS001', 'CSE'),
('Priya Das',       'priya.das@college.edu',     '9876500002', '21CS014', 'CSE'),
('Rohan Mukherjee', 'rohan.m@college.edu',       '9876500003', '21IT007', 'IT'),
('Sneha Banerjee',  'sneha.b@college.edu',       '9876500004', '21EC011', 'ECE'),
('Arjun Ghosh',     'arjun.ghosh@college.edu',   '9876500005', '21EC023', 'ECE'),
('Ananya Roy',      'ananya.roy@college.edu',    '9876500006', '21IT019', 'IT'),
('Sourav Saha',     'sourav.saha@college.edu',   '9876500007', '21ME005', 'MECH'),
('Ishita Dutta',    'ishita.dutta@college.edu',  '9876500008', '21CS032', 'CSE');

-- ---------- Sample books (available_copies already matches the ISSUED records below) ----------
INSERT INTO books (title, author, isbn, category, book_type, total_copies, available_copies) VALUES
('Data Structures in Java',      'Robert Lafore',      '9780672324536', 'Computer Science', 'TEXTBOOK', 5, 4),
('Database System Concepts',     'Silberschatz',       '9780078022159', 'Computer Science', 'TEXTBOOK', 4, 3),
('Operating System Concepts',    'Abraham Galvin',     '9781119800361', 'Computer Science', 'TEXTBOOK', 3, 3),
('Head First Java',              'Kathy Sierra',       '9780596009205', 'Programming',      'TEXTBOOK', 6, 6),
('Digital Electronics',          'Morris Mano',        '9780132774208', 'Electronics',      'TEXTBOOK', 3, 3),
('Engineering Mathematics',      'B S Grewal',         '9788174091956', 'Mathematics',      'TEXTBOOK', 4, 4),
('The Alchemist',                'Paulo Coelho',       '9780062315007', 'Fiction',          'NOVEL',    4, 3),
('Wings of Fire',                'A P J Abdul Kalam',  '9788173711466', 'Biography',        'NOVEL',    3, 3),
('The Guide',                    'R K Narayan',        '9780143039648', 'Fiction',          'NOVEL',    2, 2),
('Train to Pakistan',            'Khushwant Singh',    '9780143065883', 'Fiction',          'NOVEL',    3, 3),
('Godan',                        'Munshi Premchand',   '9788126400010', 'Fiction',          'NOVEL',    2, 2),
('Ikigai',                       'Hector Garcia',      '9781786330895', 'Self Help',        'NOVEL',    5, 5);

-- ---------- Sample issue records ----------
-- 3 currently ISSUED (one of them is overdue), 2 RETURNED (one with a fine)
INSERT INTO issue_records (book_id, student_id, issue_date, due_date, return_date, fine, status) VALUES
(1, 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY),  DATE_ADD(CURDATE(), INTERVAL 25 DAY), NULL, 0, 'ISSUED'),
(7, 2, DATE_SUB(CURDATE(), INTERVAL 3 DAY),  DATE_ADD(CURDATE(), INTERVAL 11 DAY), NULL, 0, 'ISSUED'),
(2, 3, DATE_SUB(CURDATE(), INTERVAL 40 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), NULL, 0, 'ISSUED'),
(4, 1, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 8 DAY),  0, 'RETURNED'),
(8, 4, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 16 DAY), DATE_SUB(CURDATE(), INTERVAL 12 DAY), 8, 'RETURNED');