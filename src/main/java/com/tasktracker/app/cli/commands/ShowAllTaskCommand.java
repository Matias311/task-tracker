package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import java.util.List;

/** This command represent the action of showing all the task. */
public class ShowAllTaskCommand implements TaskCommand {

  private TaskService service;

  /**
   * Creates the command using the service to get all the task.
   *
   * @param service TaskService, if its null throw IllegalArgumentException
   */
  public ShowAllTaskCommand(TaskService service) {
    if (service == null) {
      throw new IllegalArgumentException("The service must have a value");
    }
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
