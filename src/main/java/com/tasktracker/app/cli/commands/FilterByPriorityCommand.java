package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import java.util.List;

public class FilterByPriorityCommand implements TaskCommand {

  private TaskService service;
  private String priority;

  /**
   * Creates the command using the service to get the filter task by priority.
   *
   * @param service TaskService
   * @param priority String, priority can be: HIGH, MEDIUM, LOW
   */
  public FilterByPriorityCommand(TaskService service, String priority) {
    this.service = service;
    this.priority = priority;
  }

  @Override
  public void execute() {
    List<Task> list = service.filterByPriorityTask(priority);
    if (list.isEmpty()) {
      System.out.println("Empty list");
    } else {
      list.forEach(System.out::println);
    }
  }
}
