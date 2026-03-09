package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import java.util.List;

/** This command represent the action of getting all the task that are complete. */
public class GetAllTaskCompleteCommand implements TaskCommand {

  private TaskService service;

  /**
   * Constructor to create a GetAllTaskCompleteCommand, verify the service.
   *
   * @param service TaskService if its null throw IllegalArgumentException
   */
  public GetAllTaskCompleteCommand(TaskService service) {
    if (service == null) {
      throw new IllegalArgumentException("The service must have a value");
    }
    this.service = service;
  }

  @Override
  public void execute() {
    List<Task> list = service.getAllTaskThatAreComplete();
    if (list.isEmpty()) {
      System.out.println("Empty List");
    } else {
      list.forEach(System.out::println);
    }
  }
}
