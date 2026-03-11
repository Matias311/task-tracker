package com.tasktracker.app.domain;

import com.tasktracker.app.utils.VerifyData;
import java.time.LocalDate;

/** This class represent a task model, the task have two obligatory values, the id and title. */
public class Task {

  private final int id;
  private final String title;
  private final String type;
  private final String description;
  private final String priority;
  private final String status;
  private final LocalDate date;
  private final LocalDate dueDate;

  private Task(Builder build) {
    VerifyData.verifyInt(build.id, "The id must be > 0");
    this.id = build.id;

    VerifyData.verifyString(build.title, "The title must have a value");
    this.title = build.title;

    if (build.type != null && !build.type.isEmpty()) {
      VerifyData.verifyEnum(build.type, TaskType.class, "The type is not valid");
    }
    this.type = build.type;

    this.description = build.description;

    if (build.priority != null && !build.priority.isEmpty()) {
      VerifyData.verifyEnum(build.priority, TaskPriority.class, "The priority is not valid");
    }
    this.priority = build.priority;

    if (build.status != null && !build.status.isEmpty()) {
      VerifyData.verifyEnum(build.status, TaskStatus.class, "The Status is not valid");
    }
    this.status = build.status == null ? TaskStatus.TODO.toString() : build.status;

    this.date = build.date == null ? LocalDate.now() : build.date;

    if (build.dueDate == null) {
      this.dueDate = this.date.plusDays(1);
    } else if (this.date.isAfter(build.dueDate)) {
      throw new IllegalArgumentException("The due date must be after the date");
    } else {
      this.dueDate = build.dueDate;
    }
  }

  /** Create a Task using a Builder. */
  public static class Builder {
    private final int id;
    private final String title;
    private String type;
    private String description;
    private String priority;
    private String status;
    private LocalDate date;
    private LocalDate dueDate;

    /**
     * Create a task using only the id and title.
     *
     * @param id int if the value is < 0 throw IllegalArgumentException
     * @param title String if the value is null or does not have a value throw
     *     IllegalArgumentException
     */
    public Builder(int id, String title) {
      this.id = id;
      this.title = title;
    }

    /**
     * Add the type to the task.
     *
     * @param type String
     * @return Builder
     */
    public Builder type(String type) {
      this.type = type;
      return this;
    }

    /**
     * Add the description to the task.
     *
     * @param description String
     * @return Builder
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Add the priority to the Task.
     *
     * @param priority String
     * @return Build
     */
    public Builder priority(String priority) {
      this.priority = priority;
      return this;
    }

    /**
     * Add the status to the task.
     *
     * @param status String
     * @return Builder
     */
    public Builder status(String status) {
      this.status = status;
      return this;
    }

    /**
     * Add the date to the task.
     *
     * @param date LocalDate
     * @return Builder
     */
    public Builder date(LocalDate date) {
      this.date = date;
      return this;
    }

    /**
     * Add the due date to task.
     *
     * @param dueDate LocalDate
     * @return Builder
     */
    public Builder dueDate(LocalDate dueDate) {
      this.dueDate = dueDate;
      return this;
    }

    /**
     * Creates a copy of a task and trasformit into a builder.
     *
     * @param task Task
     * @return Builder
     */
    public static Builder from(Task task) {
      return new Builder(task.getId(), task.getTitle())
          .type(task.getType())
          .description(task.getDescription())
          .status(task.getStatus())
          .priority(task.getPriority())
          .date(task.getDate())
          .dueDate(task.getDueDate());
    }

    /**
     * Create the task.
     *
     * @return Task
     */
    public Task build() {
      return new Task(this);
    }
  }

  /**
   * Update the status creating a new task and returning it.
   *
   * @param s String (status to change), if the status is invalud throw a IllegalArgumentException
   * @return Task
   */
  public Task updateStatus(String s) {
    VerifyData.verifyEnum(s, TaskStatus.class, "Invalid status");
    return Builder.from(this).status(s).build();
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public String getPriority() {
    return priority;
  }

  public String getStatus() {
    return status;
  }

  public LocalDate getDate() {
    return date;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  @Override
  public String toString() {
    return "Task [id="
        + id
        + ", title="
        + title
        + ", type="
        + type
        + ", description="
        + description
        + ", priority="
        + priority
        + ", status="
        + status
        + ", date="
        + date
        + ", dueDate="
        + dueDate
        + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Task task = (Task) o;
    return id == task.id;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(id);
  }
}
