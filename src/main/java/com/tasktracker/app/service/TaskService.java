package com.tasktracker.app.service;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.domain.TaskPriority;
import com.tasktracker.app.domain.TaskStatus;
import com.tasktracker.app.domain.TaskType;
import com.tasktracker.app.repository.TaskRepository;
import com.tasktracker.app.utils.VerifyData;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TaskService {

  private TaskRepository repo;

  /**
   * Constructor, you must pass the repository.
   *
   * @param repo TaskRepository
   */
  public TaskService(TaskRepository repo) {
    this.repo = repo;
  }

  /**
   * Saves the task in memory.
   *
   * @param id int must be > 0 or throw IllegalArgumentException
   * @param title String must have a value or throw IllegalArgumentException
   * @param type String
   * @param description String
   * @param priority String
   * @param status String
   * @param date String
   * @param dueDate String
   */
  public void saveTask(
      int id,
      String title,
      String type,
      String description,
      String priority,
      String status,
      String date,
      String dueDate) {

    VerifyData.verifyInt(id, "ID must be > 0");
    VerifyData.verifyString(title, "Title must have a value");

    type = VerifyData.verifyStringForCli(type);
    description = VerifyData.verifyStringForCli(description);
    priority = VerifyData.verifyStringForCli(priority);
    status = VerifyData.verifyStringForCli(status);

    // regex to veryfy structure date yyyy-MM-dd
    LocalDate taskdate = null;
    if (date.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
      taskdate = LocalDate.parse(date);
    }

    LocalDate taskDueDate = null;
    if (dueDate.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
      taskDueDate = LocalDate.parse(dueDate);
    }

    Task task =
        new Task.Builder(id, title)
            .type(type)
            .description(description)
            .priority(priority)
            .status(status)
            .date(taskdate)
            .dueDate(taskDueDate)
            .build();
    repo.save(task);
  }

  /**
   * Get all the task.
   *
   * @return List of task , if in memory dont have any, return a empty list
   */
  public List<Task> getAllTask() {
    return repo.getAllTask();
  }

  /**
   * Filter the task list by type of task, the types are: PROGRAMMING, LIVE, UNIVERSITY.
   *
   * @param type String is the type of the task, if is invalid throw IllegalArgumentException
   * @return List of task with that type or empty task
   */
  public List<Task> filterByTypeTask(String type) {
    VerifyData.verifyEnum(type, TaskType.class, "The status task is invalid");
    return repo.filterByType(type);
  }

  /**
   * Filter the task list by the priority, the priority are: HIGH, MEDIUM, LOW.
   *
   * @param priority String is the priority of the task, if is invalid throw
   *     IllegalArgumentException
   * @return List of task with that priority or empty task
   */
  public List<Task> filterByPriorityTask(String priority) {
    VerifyData.verifyEnum(priority, TaskPriority.class, "The priority task is invalid");
    return repo.filterByPriority(priority);
  }

  /**
   * Filter the task list by the status, the status are: todo, doign, done.
   *
   * @param status String is the status of the task, if is invalid throw IllegalArgumentException
   * @return List of task with that status or empty task
   */
  public List<Task> filterByStatusTask(String status) {
    VerifyData.verifyEnum(status, TaskStatus.class, "The status task is invalid");
    return repo.filterByStatus(status);
  }

  /**
   * Complete a task.
   *
   * @param task Task, if you pass a invalid task (null one) throw IllegalArgumentException, if the
   *     task is already in DONE status throw IllegalStateException
   * @return Task
   */
  public Task completeTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Invalid task");
    }

    if (task.getStatus().equals("DONE")) {
      throw new IllegalStateException("The task is already in TODO status");
    }

    return repo.completeTask(task);
  }

  /**
   * Order the task list by due date.
   *
   * @return List of task if its empty return empty list
   */
  public List<Task> orderTaskByDueDate() {
    return repo.orderByDueDate();
  }

  /**
   * Order the task list by priority, if its empty the task list return a empty list.
   *
   * @return List of task
   */
  public List<Task> orderTaskByPriority() {
    return repo.orderByPriority();
  }

  /**
   * Search task by id, if doesnt find it return a empty Optional.
   *
   * @param id int if the id is < 0 throw IllegalArgumentException
   * @return Optional of Task
   */
  public Optional<Task> searchTaskById(int id) {
    VerifyData.verifyInt(id, "Invalid id");
    return repo.searchById(id);
  }

  /**
   * Get all the task that are complete.
   *
   * @return List of task, if the task list is empty return a empty list
   */
  public List<Task> getAllTaskThatAreComplete() {
    return repo.getAllTaskComplete();
  }

  /**
   * Undone a task, if the task is null throw IllegalArgumentException, if the Task is complete
   * throw IllegalStateException.
   *
   * @param task Task
   * @return Task
   */
  public Task undoneTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Invalid task");
    }

    if (task.getStatus().equals("TODO")) {
      throw new IllegalStateException("The task is already in TODO status");
    }
    return repo.undoneTask(task);
  }
}
