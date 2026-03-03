package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import java.util.List;

public class OrderByPriorityCommand implements TaskCommand {

  private TaskService service;

  /**
   * Creates the order by priority command using TaskService.
   *
   * @param service TaskService
   */
  public OrderByPriorityCommand(TaskService service) {
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
