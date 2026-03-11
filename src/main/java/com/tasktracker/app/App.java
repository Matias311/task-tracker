package com.tasktracker.app;

import com.tasktracker.app.cli.Menu;
import com.tasktracker.app.repository.EventDao;
import com.tasktracker.app.repository.TaskDao;
import com.tasktracker.app.repository.observer.AudditLoggerInDB;
import com.tasktracker.app.service.EventService;
import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.ConnectionJdbc;

/** Class where start the app. */
public class App {
  /**
   * Main method, start the app.
   *
   * @param args String of arrays
   */
  public static void main(String[] args) {
    Menu.of(
            System.in,
            System.out,
            new TaskService(
                new TaskDao(ConnectionJdbc.INSTANCE.getConnection()),
                new AudditLoggerInDB(
                    new EventService(new EventDao(ConnectionJdbc.INSTANCE.getConnection())))))
        .start();
  }
}
