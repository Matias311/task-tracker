package com.tasktracker.app.cli.commands;

import com.tasktracker.app.service.TaskService;
import com.tasktracker.app.utils.VerifyData;
import java.util.List;

/** This command represent the action of saving a task. */
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
    if (service == null || data == null || data.isEmpty()) {
      throw new IllegalArgumentException("The service or data must have a value");
    }
    this.service = service;
    this.data = data;
    VerifyData.verifyInt(id, "The id must be > 0");
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
