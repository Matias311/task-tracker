package com.tasktracker.app.cli;

import com.tasktracker.app.service.TaskService;

public class UndoneTask implements TaskCommand {

  private TaskService service;
  private int id;

  public UndoneTask(TaskService service, int id) {
    this.service = service;
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
