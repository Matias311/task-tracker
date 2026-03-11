package com.tasktracker.app.repository;

import com.tasktracker.app.domain.Task;
import java.util.List;
import java.util.Optional;

/** TaskRepository. */
public interface TaskRepository {

  /**
   * Save a task in memory.
   *
   * @param task Task
   * @return true if its save, false if doesnt
   */
  boolean save(Task task);

  /**
   * Get all the Task in memory, if you call it and is empty, return a empty list.
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
   * @return true if the task is update to complete or false if the task doesnt change
   */
  boolean completeTask(Task task);

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
   * @return true if its undone the task, false if it doesn't
   */
  boolean undoneTask(Task task);

  /**
   * Search a task by id.
   *
   * @param id int
   * @return Optional of Task
   */
  Optional<Task> searchById(int id);

  /**
   * Delete a task in memory.
   *
   * @param task Task
   * @return boolean, true if delete the task, false if doesnt alter the task memory
   */
  boolean deleteTask(Task task);
}
