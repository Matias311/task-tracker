package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.VerifyData;
import java.util.List;

public class FilterByTypeCommand implements TaskCommand {

  private TaskService service;
  private String type;

  /**
   * Creates the command using the service to get the filter task by type/category.
   *
   * @param service TaskService
   * @param type String, PROGRAMMING, LIVE, UNIVERSITY
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
