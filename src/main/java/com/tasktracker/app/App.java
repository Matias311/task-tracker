package com.tasktracker.app;

import com.tasktracker.app.cli.Menu;
import com.tasktracker.app.repository.TaskRepositoryImpl;
import com.tasktracker.app.service.TaskService;

/** Hello world!. */
public class App {
  public static void main(String[] args) {
    Menu.of(System.in, System.out, new TaskService(new TaskRepositoryImpl())).start();
  }
}
