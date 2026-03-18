package com.tasktracker.app.utils;

/** Interface that helps us to execute querys with transaction. */
@FunctionalInterface
public interface TransactionalInterface {

  /** Execute the query with transaction (setAutocommit false). */
  void execute() throws Exception;
}
