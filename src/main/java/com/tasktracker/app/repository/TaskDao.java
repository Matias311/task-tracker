package com.tasktracker.app.repository;

import com.tasktracker.app.domain.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

/**
 * This class are use to interact with the {@link Task} model to perform operations save, delte,
 * search, etc.
 */
public class TaskDao implements TaskRepository {
  private final Connection conn;

  /**
   * Create a task dao using a Connection.
   *
   * @param conn Connection, if its null throw IllegalArgumentException
   */
  public TaskDao(Connection conn) {
    if (conn == null) {
      throw new IllegalArgumentException("The connection can't be null");
    }
    this.conn = conn;
  }

  @Override
  public void save(Task task) {
    try (PreparedStatement prep =
        conn.prepareStatement("INSERT INTO task VALUES = ?, ?, ?, ?,?,?,?")) {

    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  @Override
  public List<Task> getAllTask() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAllTask'");
  }

  @Override
  public List<Task> filterByType(String type) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'filterByType'");
  }

  @Override
  public List<Task> filterByPriority(String type) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'filterByPriority'");
  }

  @Override
  public List<Task> filterByStatus(String type) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'filterByStatus'");
  }

  @Override
  public Task completeTask(Task task) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'completeTask'");
  }

  @Override
  public List<Task> orderByDueDate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'orderByDueDate'");
  }

  @Override
  public List<Task> orderByPriority() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'orderByPriority'");
  }

  @Override
  public List<Task> getAllTaskComplete() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAllTaskComplete'");
  }

  @Override
  public Task undoneTask(Task task) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'undoneTask'");
  }

  @Override
  public Optional<Task> searchById(int id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'searchById'");
  }

  @Override
  public boolean deleteTask(Task task) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteTask'");
  }
}
