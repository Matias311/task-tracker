package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.VerifyData;

/** This delte task command represent the action of deleting a task. */
public class DeleteCommand implements TaskCommand {
  private TaskService service;
  private int id;

  /**
   * Constructor of the delete task command, checks the values.
   *
   * @param service TaskService if its null throw IllegalArgumentException
   * @param id int if the id is <= 0 throw IllegalArgumentException
   */
  public DeleteCommand(TaskService service, int id) {
    if (service == null) {
      throw new IllegalArgumentException("Service must have a value");
    }
    this.service = service;

    VerifyData.verifyInt(id, "Id must be > 0");
    this.id = id;
  }

  @Override
  public void execute() {
    Task task = service.searchTaskById(id);
    service.deleteTask(task);
    System.out.println("Task with the id: " + id + " its deleted");
  }
}
