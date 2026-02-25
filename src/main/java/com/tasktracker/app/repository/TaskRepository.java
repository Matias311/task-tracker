package com.tasktracker.app.repository;

import com.tasktracker.app.domain.Task;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** TaskRepository. */
public interface TaskRepository {

  /**
   * Create a task.
   *
   * @param id int
   * @param title String
   * @param type String can be null
   * @param description String can be null
   * @param priority String can be null
   * @param status String can be null
   * @param date LocalDate can be null
   * @param dueDate LocalDate can be null
   */
  void createTask(
      int id,
      String title,
      String type,
      String description,
      String priority,
      String status,
      LocalDate date,
      LocalDate dueDate);

  /**
   * Get all the Task in memory.
   *
   * @return returns a unmodifiable list
   */
  List<Task> getAllTask();

  /**
   * Filter by the type (ex:programming).
   *
   * @param type String
   * @return List of Task
   */
  List<Task> filterByType(String type);

  /**
   * Filter by the priority (ex: important).
   *
   * @param type String
   * @return List of Task
   */
  List<Task> filterByPriority(String type);

  /**
   * Filter by the status (ex: done -> get all the task done).
   *
   * @param type String
   * @return List of task
   */
  List<Task> filterByStatus(String type);

  /**
   * Change the status of a task (ex: todo -> done).
   *
   * @param task Task
   */
  void completeTask(Task task);

  /**
   * Order the task by the due date (desc).
   *
   * @return List of Tasks
   */
  List<Task> orderByDueDate();

  /**
   * Order by the priority (desc).
   *
   * @return List of Tasks.
   */
  List<Task> orderByPriority();

  /**
   * Get all the task complete.
   *
   * @return List of task
   */
  List<Task> getAllTaskComplete();

  /**
   * Undone a task (change the status to todo ).
   *
   * @param task Task
   */
  void undoneTask(Task task);

  /**
   * Search a task by id.
   *
   * @param id int
   * @return Optional of Task
   */
  Optional<Task> searchById(int id);
}
