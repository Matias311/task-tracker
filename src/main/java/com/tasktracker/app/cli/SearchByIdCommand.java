package com.tasktracker.app.cli;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;

public class SearchByIdCommand implements TaskCommand {

  private TaskService service;
  private int id;

  public SearchByIdCommand(TaskService service, int id) {
    this.service = service;
    this.id = id;
  }

  @Override
  public void execute() {
    service
        .searchTaskById(id)
        .ifPresentOrElse(Task::toString, () -> System.out.println("Didn't find the task"));
  }
}
