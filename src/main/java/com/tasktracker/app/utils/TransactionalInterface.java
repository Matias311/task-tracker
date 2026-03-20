package com.tasktracker.app.utils;

/** FuntionalInterface that is responsible for execute transactions. */
@FunctionalInterface
public interface TransactionalInterface {

  /** Execute the code that we pass. */
  void execute();
}
