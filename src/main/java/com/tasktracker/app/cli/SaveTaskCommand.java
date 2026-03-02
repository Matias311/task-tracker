package com.tasktracker.app.cli;

import com.tasktracker.app.service.TaskService;
import java.util.List;

public class SaveTaskCommand implements TaskCommand {

  private TaskService service;
  private List<String> data;
  private int id;

  /**
   * Creates a Command to save a task.
   *
   * @param service TaskService
   * @param id int, id of the task
   * @param data the data is a List of string, contain: title, type, description, priority, status,
   *     date, dueDate in that exact order
   */
  public SaveTaskCommand(TaskService service, int id, List<String> data) {
    this.service = service;
    this.data = data;
    this.id = id;
  }

  @Override
  public void execute() {
    service.saveTask(
        id,
        data.get(0),
        data.get(1),
        data.get(2),
        data.get(3),
        data.get(4),
        data.get(5),
        data.get(6));
  }
}
