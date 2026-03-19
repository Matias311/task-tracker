package com.tasktracker.app.service;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.domain.TaskPriority;
import com.tasktracker.app.domain.TaskStatus;
import com.tasktracker.app.domain.TaskType;
import com.tasktracker.app.exception.NotFoundException;
import com.tasktracker.app.exception.PersistenceException;
import com.tasktracker.app.repository.TaskRepository;
import com.tasktracker.app.repository.observer.AudditLogger;
import com.tasktracker.app.repository.observer.Observer;
import com.tasktracker.app.utils.TransactionalInterface;
import com.tasktracker.app.utils.VerifyData;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service responsible for managing {@link Task} instances.
 *
 * <p>This service provides operations to create, retrieve, update, filter, order and delete tasks.
 * All persistence operations are delegated to a {@link TaskRepository} implementation.
 *
 * <p>An {@link Observer} is used to audit task-related operations such as creation, completion and
 * deletion.
 *
 * <p>This class is stateless and acts as the application layer coordinating validation, repository
 * access and audit logging.
 */
public final class TaskService {

  private final TaskRepository repo;
  private final Observer observer;
  private final Connection conn;

  /**
   * Constructor, you must pass the repository and Observer.
   *
   * @param repo TaskRepository
   * @param observer Observer
   */
  public TaskService(TaskRepository repo, Observer observer) {
    this.repo = repo;
    this.observer = observer;
    this.conn = null;
  }

  /**
   * Constructor, creates a service using the TaskRepository, Observer, Connection.
   *
   * @param repo TaskRepository
   * @param observer Observer
   * @param conn Connection
   */
  public TaskService(TaskRepository repo, Observer observer, Connection conn) {
    this.repo = repo;
    this.observer = observer;
    this.conn = conn;
  }

  /**
   * Creates and stores a new task.
   *
   * <p>The task data is validated before being persisted. If the task is successfully saved, the
   * corresponding audit event is sent to the configured {@link Observer}.
   *
   * @param task to save, before is save we verify the values
   * @throws IllegalArgumentException if the id is not positive or the title is invalid
   */
  public void saveTask(Task task) {

    VerifyData.verifyInt(task.getId(), "ID must be > 0");
    VerifyData.verifyString(task.getTitle(), "Title must have a value");

    useTransactionOperation(
        () -> {
          repo.save(task);
          observer.update(task, "SAVE");
        });
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
   * @param type String is the type of the task
   * @return List of task with that type or empty task
   * @throws IllegalArgumentException if you pass a incorrect type
   */
  public List<Task> filterByTypeTask(String type) {
    VerifyData.verifyEnum(type, TaskType.class, "The status task is invalid");
    return repo.filterByType(type);
  }

  /**
   * Filter the task list by the priority, the priority are: HIGH, MEDIUM, LOW.
   *
   * @param priority String is the priority of the task,
   * @return List of task with that priority or empty task
   * @throws IllegalArgumentException if its invalid the priority
   */
  public List<Task> filterByPriorityTask(String priority) {
    VerifyData.verifyEnum(priority, TaskPriority.class, "The priority task is invalid");
    return repo.filterByPriority(priority);
  }

  /**
   * Filter the task list by the status, the status are: todo, doign, done.
   *
   * @param status String is the status of the task
   * @return List of task with that status or empty task
   * @throws IllegalArgumentException if its invalid the status
   */
  public List<Task> filterByStatusTask(String status) {
    VerifyData.verifyEnum(status, TaskStatus.class, "The status task is invalid");
    return repo.filterByStatus(status);
  }

  /**
   * Update the status of a task when its todo or doing. Also use the observer to save the action
   *
   * @param task Task to update
   * @throws IllegalArgumentException when you pass a null task
   * @throws IllegalStateException when the task is DONE
   * @see AudditLogger
   */
  public void completeTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Invalid task");
    }

    if (task.getStatus().equals("DONE")) {
      throw new IllegalStateException("The task is already in DONE status");
    }

    useTransactionOperation(
        () -> {
          repo.completeTask(task);
          observer.update(task, "COMPLETE TASK");
        });
  }

  /**
   * Order the task list by due date. to order the task uses
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
   * Retrieves a task by its identifier.
   *
   * @param id identifier of the task, must be greater than zero
   * @return the task associated with the given id
   * @throws IllegalArgumentException if the id is invalid
   * @throws NotFoundException if no task exists with the given id
   */
  public Task searchTaskById(int id) {
    VerifyData.verifyInt(id, "Invalid id");
    return repo.searchById(id)
        .orElseThrow(() -> new NotFoundException("Task with id: " + id + " not found"));
  }

  /**
   * Get all the task that are complete. if the list in memory is empty just return a empty list.
   *
   * @return List of task
   */
  public List<Task> getAllTaskThatAreComplete() {
    return repo.getAllTaskComplete();
  }

  /**
   * Undone a task, the task can not be in todo status and it cant be null.
   *
   * @param task Task to update the status to todo
   * @throws IllegalArgumentException if the task is null
   * @throws IllegalStateException if the task status is todo already
   */
  public void undoneTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Invalid task");
    }

    if (task.getStatus().equals("TODO")) {
      throw new IllegalStateException("The task is already in TODO status");
    }
    useTransactionOperation(
        () -> {
          repo.undoneTask(task);
          observer.update(task, "UNDONE");
        });
  }

  /**
   * Delete a task, if its doesnt in memory return false, if its delete return true.
   *
   * @param task Task to delete
   * @throws IllegalArgumentException if the task is null
   */
  public void deleteTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Invalid task");
    }
    useTransactionOperation(
        () -> {
          repo.deleteTask(task);
          observer.update(task, "DELETE");
        });
  }

  private void useTransactionOperation(TransactionalInterface operation) {
    if (conn == null) {
      try {
        operation.execute();
      } catch (Exception e) {
        throw new PersistenceException("Error doing the transaction: " + e);
      }
      return;
    }

    try {
      conn.setAutoCommit(false);
      operation.execute();
      conn.commit();
    } catch (Exception e) {
      try {
        conn.rollback();
      } catch (SQLException rollbackException) {
        e.addSuppressed(e);
      }
      throw new PersistenceException("Error using transaction: ", e);
    } finally {
      try {
        conn.setAutoCommit(true);
      } catch (SQLException setAutoCommitException) {
        throw new PersistenceException("Could not restore auto commit: ", setAutoCommitException);
      }
    }
  }
}
