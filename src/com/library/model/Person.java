package com.library.model;

import com.library.exception.InvalidInputException;

// ABSTRACT CLASS — demonstrates ABSTRACTION + ENCAPSULATION
// Cannot be instantiated directly; Student extends it (INHERITANCE)
public abstract class Person {

    // ENCAPSULATION: private fields, accessed only via validated getters/setters
    private int id;
    private String name;
    private String email;
    private String phone;

    // Constructor
    public Person(int id, String name, String email, String phone) throws InvalidInputException {
        this.id = id;
        setName(name);
        setEmail(email);
        setPhone(phone);
    }

    // --- Getters ---
    public int getId()      { return id; }
    public String getName() { return name; }
    public String getEmail(){ return email; }
    public String getPhone(){ return phone; }

    // --- Validated Setters (ENCAPSULATION) ---
    public void setId(int id) { this.id = id; }

    public void setName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty())
            throw new InvalidInputException("Name cannot be blank.");
        this.name = name.trim();
    }

    public void setEmail(String email) throws InvalidInputException {
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new InvalidInputException("Invalid email address: must contain '@' and '.'");
        this.email = email.trim();
    }

    public void setPhone(String phone) throws InvalidInputException {
        if (phone == null || !phone.matches("\\d{10}"))
            throw new InvalidInputException("Phone must be exactly 10 digits.");
        this.phone = phone.trim();
    }

    // ABSTRACTION: subclasses must define these
    public abstract String getRole();
    public abstract String getDisplayInfo();

    @Override
    public String toString() {
        return getDisplayInfo();
    }
}
