package com.tasktracker.app.cli.commands;

import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.VerifyData;

public class UndoneTaskCommand implements TaskCommand {

  private TaskService service;
  private int id;

  public UndoneTaskCommand(TaskService service, int id) {
    if (service == null) {
      throw new IllegalArgumentException("Service must have a value");
    }
    this.service = service;

    VerifyData.verifyInt(id, "Id must be > 0");
    this.id = id;
  }

  @Override
  public void execute() {
    service
        .searchTaskById(id)
        .ifPresentOrElse(
            t -> service.undoneTask(t), () -> System.out.println("Didn't find the task"));
  }
}
