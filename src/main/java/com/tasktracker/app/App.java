package com.tasktracker.app;

import com.tasktracker.app.cli.Menu;
import com.tasktracker.app.repository.TaskRepositoryImpl;
import com.tasktracker.app.repository.observer.AudditLogger;
import com.tasktracker.app.service.TaskService;

/** Class where start the app. */
public class App {
  /**
   * Main method, start the app.
   *
   * @param args String of arrays
   */
  public static void main(String[] args) {
    Menu.of(System.in, System.out, new TaskService(new TaskRepositoryImpl(), new AudditLogger()))
        .start();
  }
}
