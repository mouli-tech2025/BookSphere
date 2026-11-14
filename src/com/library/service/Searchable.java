package com.library.service;

import com.library.exception.LibraryException;
import java.util.List;

/**
 * ABSTRACTION: Generic search interface implemented by BookService and StudentService.
 * Callers depend on this interface, not on concrete service classes.
 */
public interface Searchable<T> {
    List<T> search(String keyword) throws LibraryException;
}
