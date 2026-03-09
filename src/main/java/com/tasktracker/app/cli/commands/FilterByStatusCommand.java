package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.domain.TaskStatus;
import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.VerifyData;
import java.util.List;

/** This filter the task by status represent the action of filter by status the task. */
public class FilterByStatusCommand implements TaskCommand {

  private TaskService service;
  private String status;

  /**
   * Creates the command using the service to get the filter task by status.
   *
   * @param service TaskService if its null throw IllegalArgumentException
   * @param status String, the status can be: todo, DOING, DONE, if its invalid throw
   *     IllegalArgumentException
   */
  public FilterByStatusCommand(TaskService service, String status) {
    if (service == null) {
      throw new IllegalArgumentException("Service must have a value");
    }
    this.service = service;
    VerifyData.verifyEnum(
        status,
        TaskStatus.class,
        "Invalid status, must be todo, DOING or DONE and you pass " + status);
    this.status = status;
  }

  @Override
  public void execute() {
    List<Task> list = service.filterByStatusTask(status);
    if (list.isEmpty()) {
      System.out.println("Empty list");
    } else {
      list.forEach(System.out::println);
    }
  }
}
