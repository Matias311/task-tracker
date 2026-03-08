package com.tasktracker.app.Exception;

public class NotFoundException extends RuntimeException {

  public NotFoundException(String s) {
    super(s);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
