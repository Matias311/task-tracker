package com.tasktracker.app;

import com.tasktracker.app.cli.Menu;
import com.tasktracker.app.repository.EventDao;
import com.tasktracker.app.repository.TaskDao;
import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.ConnectionJdbc;
import java.sql.Connection;

/** Class where start the app. */
public class App {
  /**
   * Main method, start the app.
   *
   * @param args String of arrays
   */
  public static void main(String[] args) {
    try {
      Connection conn = ConnectionJdbc.getConnection();
      Menu.of(
              System.in,
              System.out,
              new TaskService(
                  new TaskDao(conn),
                  new EventDao(conn),
                  conn))
          .start();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
