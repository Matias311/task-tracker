package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.VerifyData;
import java.util.List;

/** This filter the task by type represent the action of filter by type the task. */
public class FilterByTypeCommand implements TaskCommand {

  private TaskService service;
  private String type;

  /**
   * Creates the command using the service to get the filter task by type/category. And checks the
   * service value and the priority
   *
   * @param service TaskService, if its null throw IllegalArgumentException
   * @param type String, PROGRAMMING, LIVE, UNIVERSITY, if its null or empty throw
   *     IllegalArgumentException
   */
  public FilterByTypeCommand(TaskService service, String type) {
    if (service == null) {
      throw new IllegalArgumentException("Service must have a value");
    }
    VerifyData.verifyString(type, "Type must have a value");
    this.service = service;
    this.type = type;
  }

  @Override
  public void execute() {
    List<Task> list = service.filterByTypeTask(type);
    if (list.isEmpty()) {
      System.out.println("Empty list");
    } else {
      list.forEach(System.out::println);
    }
  }
}
