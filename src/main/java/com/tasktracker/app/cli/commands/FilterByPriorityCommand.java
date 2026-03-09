package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.domain.TaskPriority;
import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.VerifyData;
import java.util.List;

/** This filter the task by priority represent the action of filter by priority the task. */
public class FilterByPriorityCommand implements TaskCommand {

  private TaskService service;
  private String priority;

  /**
   * Creates the command using the service to get the filter task by priority. And checks the
   * service value and the priority
   *
   * @param service TaskService, if its null throw IllegalArgumentException
   * @param priority String, priority can be: HIGH, MEDIUM, LOW, if its invalid throw
   *     IllegalArgumentException
   */
  public FilterByPriorityCommand(TaskService service, String priority) {
    if (service == null) {
      throw new IllegalArgumentException("Service must have a value");
    }
    this.service = service;
    VerifyData.verifyEnum(
        priority,
        TaskPriority.class,
        "Invalid priority, must be HIGH, MEDIUM or LOW and you pass " + priority);
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
