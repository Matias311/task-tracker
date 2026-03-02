package com.tasktracker.app.cli;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import java.util.Optional;

public class CompleteTaskCommand implements TaskCommand {

  private TaskService service;
  private int id;

  public CompleteTaskCommand(TaskService service, int id) {
    this.service = service;
    this.id = id;
  }

  @Override
  public void execute() {
    Optional<Task> task = service.searchTaskById(id);
    task.ifPresentOrElse(
        t -> service.completeTask(t), () -> System.out.println("Didn't find the task"));
  }
}
