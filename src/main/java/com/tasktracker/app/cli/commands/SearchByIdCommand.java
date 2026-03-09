package com.tasktracker.app.cli.commands;

import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.VerifyData;

/** This command represent the action of searching a task. */
public class SearchByIdCommand implements TaskCommand {

  private TaskService service;
  private int id;

  /**
   * Creates a SearchByIdCommand using the service and id. Verify the data
   *
   * @param service TaskService, if its null throw IllegalArgumentException
   * @param id int, if the id <= 0 throw IllegalArgumentException
   */
  public SearchByIdCommand(TaskService service, int id) {
    if (service == null) {
      throw new IllegalArgumentException("Service must have a value");
    }
    this.service = service;

    VerifyData.verifyInt(id, "Id must be > 0");
    this.id = id;
  }

  @Override
  public void execute() {
    System.out.println(service.searchTaskById(id).toString());
  }
}
