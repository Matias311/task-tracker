package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import java.util.List;

public class ShowAllTaskCommand implements TaskCommand {

  private TaskService service;

  /**
   * Creates the command using the service to get all the task.
   *
   * @param service TaskService
   */
  public ShowAllTaskCommand(TaskService service) {
    this.service = service;
  }

  @Override
  public void execute() {
    List<Task> list = service.getAllTask();
    if (list.isEmpty()) {
      System.out.println("Empty list");
    } else {
      list.forEach(System.out::println);
    }
  }
}
