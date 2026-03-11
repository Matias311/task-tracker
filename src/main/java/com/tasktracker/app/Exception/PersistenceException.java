package com.tasktracker.app.Exception;

/** Exception for errors with the persistance. */
public class PersistenceException extends RuntimeException {

  /**
   * Creates a exception with the message.
   *
   * @param m String , is the message
   */
  public PersistenceException(String m) {
    super(m);
  }

  /**
   * Creates a exception with the message and we pass the orginal exception and that helps with
   * chainging exception.
   *
   * @param m String , message of the exception
   * @param t Throwable is the original exception
   */
  public PersistenceException(String m, Throwable t) {
    super(m, t);
  }
}
