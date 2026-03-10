package com.tasktracker.app.cli.commands;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.TaskService;
import java.time.LocalDate;

/** This command represent the action of saving a task. */
public class SaveTaskCommand implements TaskCommand {

  private TaskService service;
  private int id;
  private String title;
  private String type;
  private String description;
  private String priority;
  private String status;
  private LocalDate date;
  private LocalDate dueDate;

  /**
   * Creates a save task command, save the data to the task.
   *
   * @param service TaskService, if its null throo IllegalArgumentException
   * @param id int, the id of a task
   * @param title String, the name/title to the task
   * @param type type of the task
   * @param description optional description
   * @param priority priority level of the task
   * @param status initial status of the task
   * @param date LocalDate when creates the task
   * @param dueDate LocalDate, represent the due date of a task
   */
  public SaveTaskCommand(
      TaskService service,
      int id,
      String title,
      String type,
      String description,
      String priority,
      String status,
      LocalDate date,
      LocalDate dueDate) {
    if (service == null) {
      throw new IllegalArgumentException("The service or data must have a value");
    }
    this.service = service;
    this.id = id;
    this.title = title;
    this.type = type;
    this.description = description;
    this.priority = priority;
    this.status = status;
    this.date = date;
    this.dueDate = dueDate;
  }

  @Override
  public void execute() {
    service.saveTask(
        new Task.Builder(id, title)
            .type(type)
            .description(description)
            .priority(priority)
            .status(status)
            .date(date)
            .dueDate(dueDate)
            .build());
  }
}
