package com.tasktracker.app.exception;

/**
 * Exception thrown when an expected entity cannot be found.
 *
 * <p>This exception is typically used when an operation attempts to retrieve an entity that does
 * not exist in the system. For example, when searching for a task by its identifier and no matching
 * task is found.
 *
 * <p>This is an unchecked exception because it extends {@link RuntimeException}. It is intended to
 * signal a logical error in the application flow rather than a recoverable condition.
 */
public class NotFoundException extends RuntimeException {

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param s the detail message describing the error
   */
  public NotFoundException(String s) {
    super(s);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the underlying cause of the exception
   */
  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
