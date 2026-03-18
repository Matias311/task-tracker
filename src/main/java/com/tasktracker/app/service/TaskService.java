package com.tasktracker.app.service;

import com.tasktracker.app.domain.Event;
import com.tasktracker.app.domain.Task;
import com.tasktracker.app.domain.TaskPriority;
import com.tasktracker.app.domain.TaskStatus;
import com.tasktracker.app.domain.TaskType;
import com.tasktracker.app.exception.NotFoundException;
import com.tasktracker.app.exception.PersistenceException;
import com.tasktracker.app.repository.EventRepository;
import com.tasktracker.app.repository.TaskRepository;
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
 * <p>Mutating operations (save, complete, undone, delete) are executed within a JDBC transaction
 * that includes both the task operation and its corresponding audit event. If either operation
 * fails, the entire transaction is rolled back.
 *
 * <p>This class is stateless and acts as the application layer coordinating validation, repository
 * access and audit logging.
 */
public final class TaskService {

  private final TaskRepository taskRepo;
  private final EventRepository eventRepo;
  private final Connection conn;

  /**
   * Constructor for database-backed usage with transactional support.
   *
   * <p>Both repositories must share the same underlying JDBC connection so that the service can
   * coordinate transactions across task and audit operations.
   *
   * @param taskRepo TaskRepository for task persistence
   * @param eventRepo EventRepository for audit persistence
   * @param conn the shared JDBC connection used to coordinate transactions
   */
  public TaskService(TaskRepository taskRepo, EventRepository eventRepo, Connection conn) {
    this.taskRepo = taskRepo;
    this.eventRepo = eventRepo;
    this.conn = conn;
  }

  /**
   * Constructor for in-memory usage (no transactional support).
   *
   * <p>When no JDBC connection is provided, mutating operations execute without transaction
   * coordination. This is intended for testing with in-memory repositories.
   *
   * @param taskRepo TaskRepository for task persistence
   * @param eventRepo EventRepository for audit persistence
   */
  public TaskService(TaskRepository taskRepo, EventRepository eventRepo) {
    this(taskRepo, eventRepo, null);
  }

  /**
   * Creates and stores a new task within a transaction.
   *
   * <p>The task data is validated before being persisted. Both the task and its audit event are
   * saved atomically: if either fails, the entire operation is rolled back.
   *
   * @param task the task to save
   * @throws IllegalArgumentException if the id is not positive or the title is invalid
   * @throws PersistenceException if the transaction fails
   */
  public void saveTask(Task task) {
    VerifyData.verifyInt(task.getId(), "ID must be > 0");
    VerifyData.verifyString(task.getTitle(), "Title must have a value");

    executeInTransaction(() -> {
      taskRepo.save(task);
      eventRepo.saveEvent(new Event("SAVE", task.getId(), task.getTitle()));
    });
  }

  /**
   * Get all the task.
   *
   * @return List of task, if in memory dont have any, return a empty list
   */
  public List<Task> getAllTask() {
    return taskRepo.getAllTask();
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
    return taskRepo.filterByType(type);
  }

  /**
   * Filter the task list by the priority, the priority are: HIGH, MEDIUM, LOW.
   *
   * @param priority String is the priority of the task
   * @return List of task with that priority or empty task
   * @throws IllegalArgumentException if its invalid the priority
   */
  public List<Task> filterByPriorityTask(String priority) {
    VerifyData.verifyEnum(priority, TaskPriority.class, "The priority task is invalid");
    return taskRepo.filterByPriority(priority);
  }

  /**
   * Filter the task list by the status, the status are: todo, doing, done.
   *
   * @param status String is the status of the task
   * @return List of task with that status or empty task
   * @throws IllegalArgumentException if its invalid the status
   */
  public List<Task> filterByStatusTask(String status) {
    VerifyData.verifyEnum(status, TaskStatus.class, "The status task is invalid");
    return taskRepo.filterByStatus(status);
  }

  /**
   * Update the status of a task to DONE within a transaction.
   *
   * <p>Both the status update and the audit event are saved atomically.
   *
   * @param task Task to update
   * @throws IllegalArgumentException when you pass a null task
   * @throws IllegalStateException when the task is DONE
   * @throws PersistenceException if the transaction fails
   */
  public void completeTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Invalid task");
    }

    if (task.getStatus().equals("DONE")) {
      throw new IllegalStateException("The task is already in DONE status");
    }

    executeInTransaction(() -> {
      taskRepo.completeTask(task);
      eventRepo.saveEvent(new Event("COMPLETE TASK", task.getId(), task.getTitle()));
    });
  }

  /**
   * Order the task list by due date.
   *
   * @return List of task if its empty return empty list
   */
  public List<Task> orderTaskByDueDate() {
    return taskRepo.orderByDueDate();
  }

  /**
   * Order the task list by priority, if its empty the task list return a empty list.
   *
   * @return List of task
   */
  public List<Task> orderTaskByPriority() {
    return taskRepo.orderByPriority();
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
    return taskRepo.searchById(id)
        .orElseThrow(() -> new NotFoundException("Task with id: " + id + " not found"));
  }

  /**
   * Get all the task that are complete.
   *
   * @return List of task
   */
  public List<Task> getAllTaskThatAreComplete() {
    return taskRepo.getAllTaskComplete();
  }

  /**
   * Undone a task within a transaction.
   *
   * <p>Both the status update and the audit event are saved atomically.
   *
   * @param task Task to update the status to todo
   * @throws IllegalArgumentException if the task is null
   * @throws IllegalStateException if the task status is todo already
   * @throws PersistenceException if the transaction fails
   */
  public void undoneTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Invalid task");
    }

    if (task.getStatus().equals("TODO")) {
      throw new IllegalStateException("The task is already in TODO status");
    }

    executeInTransaction(() -> {
      taskRepo.undoneTask(task);
      eventRepo.saveEvent(new Event("UNDONE", task.getId(), task.getTitle()));
    });
  }

  /**
   * Delete a task within a transaction.
   *
   * <p>Both the deletion and the audit event are saved atomically.
   *
   * @param task Task to delete
   * @return true if deleted
   * @throws IllegalArgumentException if the task is null
   * @throws PersistenceException if the transaction fails
   */
  public boolean deleteTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Invalid task");
    }

     executeInTransaction(() -> {
       eventRepo.saveEvent(new Event("DELETE", task.getId(), task.getTitle()));
       taskRepo.deleteTask(task);
     });
    return true;
  }

  /**
   * Executes the given operation within a JDBC transaction.
   *
   * <p>Sets auto-commit to false, executes the operation, and commits. If any exception occurs
   * during the operation, the transaction is rolled back and a {@link PersistenceException} is
   * thrown.
   *
   * <p>If no connection is available (null), executes the operation without transactional support
   * (for in-memory testing).
   *
   * @param operation the operation to execute within the transaction
   * @throws PersistenceException if the operation or transaction management fails
   */
  private void executeInTransaction(TransactionalInterface operation) {
    if (conn == null) {
      // In-memory mode: no transactional support
      try {
        operation.execute();
      } catch (Exception e) {
        throw new PersistenceException("Operation failed\n", e);
      }
      return;
    }

    // Database mode: with transactional support
    try {
      conn.setAutoCommit(false);
      operation.execute();
      conn.commit();
    } catch (Exception e) {
      try {
        conn.rollback();
      } catch (SQLException rollbackException) {
        e.addSuppressed(rollbackException);
      }
      throw new PersistenceException("Transaction failed, rollback executed\n", e);
    } finally {
      try {
        conn.setAutoCommit(true);
      } catch (SQLException autoCommitEx) {
        throw new PersistenceException("Could not restore auto-commit\n", autoCommitEx);
      }
    }
  }
}
