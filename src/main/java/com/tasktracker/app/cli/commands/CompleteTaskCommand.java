package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.VerifyData;

/** This complete task command represent the action of compleeting a task. */
public class CompleteTaskCommand implements TaskCommand {

  private TaskService service;
  private int id;

  /**
   * Constructor of the complete task command, checks the values.
   *
   * @param service TaskService if its null throw IllegalArgumentException
   * @param id int if the id is <= 0 throw IllegalArgumentException
   */
  public CompleteTaskCommand(TaskService service, int id) {
    if (service == null) {
      throw new IllegalArgumentException("Service must have a value");
    }
    VerifyData.verifyInt(id, "Id must be > 0");
    this.service = service;
    this.id = id;
  }

  @Override
  public void execute() {
    Task task = service.searchTaskById(id);
    service.completeTask(task);
    System.out.println("Now the status in the task with the id: " + id + " is DONE");
  }
}
