package com.library.exception;

// BASE CUSTOM EXCEPTION — EXCEPTION HANDLING OOP concept
// All library-specific checked exceptions extend this class
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }
    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
