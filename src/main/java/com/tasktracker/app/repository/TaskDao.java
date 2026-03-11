package com.tasktracker.app.repository;

import com.tasktracker.app.Exception.PersistenceException;
import com.tasktracker.app.domain.Task;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
  public boolean save(Task task) {
    int result;
    try (PreparedStatement prep =
        conn.prepareStatement(
            "INSERT INTO tasks (id, title, type, description, priority, status, date, due_date)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
      prep.setInt(1, task.getId());
      prep.setString(2, task.getTitle());
      prep.setString(3, task.getType());
      prep.setString(4, task.getDescription());
      prep.setString(5, task.getPriority());
      prep.setString(6, task.getStatus());
      prep.setDate(7, Date.valueOf(task.getDate()));
      prep.setDate(8, Date.valueOf(task.getDueDate()));
      result = prep.executeUpdate();
    } catch (Exception e) {
      throw new PersistenceException("Error: could not save the task\n", e);
    }
    return result == 1 ? true : false;
  }

  @Override
  public List<Task> getAllTask() {
    List<Task> list = new ArrayList<>();
    try (Statement stm = conn.createStatement();
        ResultSet result = stm.executeQuery("SELECT * FROM tasks;")) {
      list = parseTask(result);
    } catch (Exception e) {
      throw new PersistenceException("Error: could not obtain the tasks\n", e);
    }
    return list;
  }

  @Override
  public List<Task> filterByType(String type) {
    List<Task> list = new ArrayList<>();
    try (PreparedStatement prstm = conn.prepareStatement("SELECT * FROM tasks WHERE type = ?;")) {
      prstm.setString(1, type);
      list = parseTask(prstm.executeQuery());
    } catch (Exception e) {
      throw new PersistenceException("Error: can not filter by type\n", e);
    }
    return list;
  }

  @Override
  public List<Task> filterByPriority(String type) {
    List<Task> list = new ArrayList<>();
    try (PreparedStatement prstm =
        conn.prepareStatement("SELECT * FROM tasks WHERE priority = ?;")) {
      prstm.setString(1, type);
      list = parseTask(prstm.executeQuery());
    } catch (Exception e) {
      throw new PersistenceException("Error: can not filter by priority\n", e);
    }
    return list;
  }

  @Override
  public List<Task> filterByStatus(String type) {
    List<Task> list = new ArrayList<>();
    try (PreparedStatement prstm = conn.prepareStatement("SELECT * FROM tasks WHERE status = ?;")) {
      prstm.setString(1, type);
      list = parseTask(prstm.executeQuery());
    } catch (Exception e) {
      throw new PersistenceException("Error: can not filter by status\n", e);
    }
    return list;
  }

  @Override
  public boolean completeTask(Task task) {
    int result;
    try (PreparedStatement prstm =
        conn.prepareStatement("UPDATE tasks SET status = 'DONE' WHERE id = ?")) {
      prstm.setInt(1, task.getId());
      result = prstm.executeUpdate();
    } catch (Exception e) {
      throw new PersistenceException("Error: can not update the task status to DONE\n", e);
    }

    return result == 1 ? true : false;
  }

  @Override
  public List<Task> orderByDueDate() {
    List<Task> list = new ArrayList<>();
    try (Statement stm = conn.createStatement();
        ResultSet result = stm.executeQuery("SELECT * FROM tasks ORDER BY due_date;")) {
      list = parseTask(result);
    } catch (Exception e) {
      throw new PersistenceException("Error: can not order the tasks by due date\n", e);
    }
    return list;
  }

  @Override
  public List<Task> orderByPriority() {
    List<Task> list = new ArrayList<>();
    try (Statement stm = conn.createStatement();
        ResultSet result = stm.executeQuery("SELECT * FROM tasks ORDER BY priority;")) {
      list = parseTask(result);
    } catch (Exception e) {
      throw new PersistenceException("Error: can not order the tasks by priority\n", e);
    }
    return list;
  }

  @Override
  public List<Task> getAllTaskComplete() {
    List<Task> list = new ArrayList<>();
    try (Statement stm = conn.createStatement();
        ResultSet result = stm.executeQuery("SELECT * FROM tasks WHERE status = 'DONE';")) {
      list = parseTask(result);
    } catch (Exception e) {
      throw new PersistenceException("Error: can not get all the tasks\n", e);
    }
    return list;
  }

  @Override
  public boolean undoneTask(Task task) {
    int result;
    try (PreparedStatement prstm =
        conn.prepareStatement("UPDATE tasks SET status = 'TODO' WHERE id = ?;")) {
      prstm.setInt(1, task.getId());
      result = prstm.executeUpdate();
    } catch (Exception e) {
      throw new PersistenceException("Error: can not update the status to TODO of the task\n", e);
    }
    return result == 1 ? true : false;
  }

  @Override
  public Optional<Task> searchById(int id) {
    List<Task> list = new ArrayList<>();
    try (PreparedStatement prstm = conn.prepareStatement("SELECT * FROM tasks WHERE id = ?;")) {
      prstm.setInt(1, id);
      list = parseTask(prstm.executeQuery());
    } catch (Exception e) {
      throw new PersistenceException("Error: can not search task by the id\n", e);
    }
    return Optional.of(list.get(0));
  }

  @Override
  public boolean deleteTask(Task task) {
    int result;
    try (PreparedStatement prstm = conn.prepareStatement("DELETE FROM tasks WHERE id = ?;")) {
      prstm.setInt(1, task.getId());
      result = prstm.executeUpdate();
    } catch (Exception e) {
      throw new PersistenceException("Error: can not delete the task\n", e);
    }
    return result == 1 ? true : false;
  }

  /**
   * This transform the data of the result to a List of tasks.
   *
   * @param ResultSet is the result of a query that requiere more than one task
   * @return List of task that are saved in the database
   */
  private List<Task> parseTask(ResultSet result) throws SQLException {
    List<Task> list = new ArrayList<>();
    while (result.next()) {
      list.add(
          new Task.Builder(result.getInt("id"), result.getString("title"))
              .type(result.getString("type"))
              .description(result.getString("description"))
              .priority(result.getString("priority"))
              .status(result.getString("status"))
              .date(result.getDate("date").toLocalDate())
              .dueDate(result.getDate("due_date").toLocalDate())
              .build());
    }
    return list;
  }
}
