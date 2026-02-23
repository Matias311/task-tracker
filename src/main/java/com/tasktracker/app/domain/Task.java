package com.tasktracker.app.domain;

import java.time.LocalDate;
import java.util.List;

// TODO: Change the status to a enum
// TODO: change the type to a enum

/** Task. */
public class Task {

  private int id;
  private String title;
  private List<String> type;
  private String description;
  private String priority;
  private String status;
  private LocalDate date;
  private LocalDate dueDate;

  private Task(
      int id,
      String title,
      List<String> type,
      String description,
      String priority,
      String status,
      LocalDate date,
      LocalDate dueDate) {
    this.id = id;
    this.title = title;
    this.type = type;
    this.description = description;
    this.priority = priority;
    this.status = status;
    this.date = date;
    this.dueDate = dueDate;
  }

  /**
   * Create a Task using a factory method.
   *
   * @param id int if the value is < 0 throw IllegalArgumentException
   * @param title String if the value is null or does not have a value throw
   *     IllegalArgumentException
   * @param type List of String if the type is null or the size is equals to 0 throw
   *     IllegalArgumentException
   * @param description String if the value is null or does not have a value throw
   *     IllegalArgumentException
   * @param priority String if the value is null or does not have a value throw
   *     IllegalArgumentException
   * @param status String if the value is null or does not have a value throw
   *     IllegalArgumentException
   * @param date String (optional, if you dont pass a date creates a date using LocalDate.now())
   * @param dueDate String (optional, if you dont pass a date creates a due date using the date plus
   *     one day)
   * @return Task
   */
  public static Task of(
      int id,
      String title,
      List<String> type,
      String description,
      String priority,
      String status,
      LocalDate date,
      LocalDate dueDate) {
    if (id < 0) {
      throw new IllegalArgumentException("ID must have a value < 0");
    }

    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Title must have a value");
    }

    if (type == null || type.size() == 0) {
      throw new IllegalArgumentException("The list type must have value and not be null");
    }

    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Description must have a value");
    }

    if (priority == null || priority.isBlank()) {
      throw new IllegalArgumentException("Description must have a value");
    }

    if (status == null || status.isBlank()) {
      throw new IllegalArgumentException("Description must have a value");
    }

    return new Task(
        id,
        title,
        type,
        description,
        priority,
        status,
        date != null ? date : LocalDate.now(),
        dueDate != null ? dueDate : date.plusDays(1));
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public List<String> getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public String getPriority() {
    return priority;
  }

  public LocalDate getDate() {
    return date;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public String getStatus() {
    return status;
  }
}
