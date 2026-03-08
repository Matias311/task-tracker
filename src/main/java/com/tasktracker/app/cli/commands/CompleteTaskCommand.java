package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.VerifyData;

public class CompleteTaskCommand implements TaskCommand {

  private TaskService service;
  private int id;

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
