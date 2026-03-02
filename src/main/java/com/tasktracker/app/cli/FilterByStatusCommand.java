package com.tasktracker.app.cli;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import java.util.List;

public class FilterByStatusCommand implements TaskCommand {

  private TaskService service;
  private String status;

  /**
   * Creates the command using the service to get the filter task by status.
   *
   * @param service TaskService
   * @param status String, the status can be: todo, DOING, DONE
   */
  public FilterByStatusCommand(TaskService service, String status) {
    this.service = service;
    this.status = status;
  }

  @Override
  public void execute() {
    List<Task> list = service.filterByStatusTask(status);
    if (list.isEmpty()) {
      System.out.println("Empty list");
    } else {
      // TODO: overwrite toString in task
      list.forEach(System.out::println);
    }
  }
}
