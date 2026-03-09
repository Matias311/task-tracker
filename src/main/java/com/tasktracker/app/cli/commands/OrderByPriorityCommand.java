package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import java.util.List;

/** This command represent the action of order by priority the task. */
public class OrderByPriorityCommand implements TaskCommand {

  private TaskService service;

  /**
   * Creates the order by priority command using TaskService.
   *
   * @param service TaskService, if its null throw IllegalArgumentException
   */
  public OrderByPriorityCommand(TaskService service) {
    if (service == null) {
      throw new IllegalArgumentException("The service must have a value");
    }
    this.service = service;
  }

  @Override
  public void execute() {
    List<Task> list = service.orderTaskByPriority();

    if (list.isEmpty()) {
      System.out.println("Empty List");
    } else {
      list.forEach(System.out::println);
    }
  }
}
