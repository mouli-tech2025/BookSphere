package com.library.model;

import com.library.exception.InvalidInputException;

// INHERITANCE: Student extends Person (inherits id, name, email, phone + their getters/setters)
// POLYMORPHISM: overrides getRole() and getDisplayInfo()
public class Student extends Person {

    // ENCAPSULATION: additional private fields specific to Student
    private String rollNo;
    private String department;

    // Constructor calls super() — INHERITANCE
    public Student(int id, String name, String email, String phone,
                   String rollNo, String department) throws InvalidInputException {
        super(id, name, email, phone); // INHERITANCE: delegate to parent constructor
        setRollNo(rollNo);
        setDepartment(department);
    }

    // --- Getters ---
    public String getRollNo()     { return rollNo; }
    public String getDepartment() { return department; }

    // --- Validated Setters ---
    public void setRollNo(String rollNo) throws InvalidInputException {
        if (rollNo == null || rollNo.trim().isEmpty())
            throw new InvalidInputException("Roll number cannot be blank.");
        this.rollNo = rollNo.trim();
    }

    public void setDepartment(String department) throws InvalidInputException {
        if (department == null || department.trim().isEmpty())
            throw new InvalidInputException("Department cannot be blank.");
        this.department = department.trim();
    }

    // POLYMORPHISM: overrides abstract method from Person
    @Override
    public String getRole() {
        return "Student";
    }

    // POLYMORPHISM: overrides abstract method from Person
    @Override
    public String getDisplayInfo() {
        return getName() + " (" + rollNo + ") - " + department;
    }
}
