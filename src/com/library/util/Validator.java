package com.library.util;

import com.library.exception.InvalidInputException;

/**
 * Static validation helpers used by services before calling DAOs.
 */
public class Validator {

    public static void requireNonBlank(String value, String fieldName)
            throws InvalidInputException {
        if (value == null || value.trim().isEmpty())
            throw new InvalidInputException(fieldName + " is required and cannot be blank.");
    }

    public static void validateEmail(String email) throws InvalidInputException {
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new InvalidInputException("Invalid email: must contain '@' and '.'");
    }

    public static void validatePhone(String phone) throws InvalidInputException {
        if (phone == null || !phone.matches("\\d{10}"))
            throw new InvalidInputException("Phone number must be exactly 10 digits.");
    }

    public static int parsePositiveInt(String value, String fieldName)
            throws InvalidInputException {
        try {
            int v = Integer.parseInt(value.trim());
            if (v < 1)
                throw new InvalidInputException(fieldName + " must be a positive integer.");
            return v;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid integer.");
        }
    }

    public static int parseInt(String value, String fieldName)
            throws InvalidInputException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid integer.");
        }
    }

    public static void validateBookType(String type) throws InvalidInputException {
        if (!"TEXTBOOK".equals(type) && !"NOVEL".equals(type))
            throw new InvalidInputException("Book type must be TEXTBOOK or NOVEL.");
    }
}
