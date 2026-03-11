package com.tasktracker.app.repository;

import com.tasktracker.app.domain.Event;
import com.tasktracker.app.exception.PersistenceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/** Class that have the method to manage Event in Memory. */
public final class EventDao implements EventRepository {

  private Connection conn;

  /**
   * Creates a event dao.
   *
   * @param conn Connection to the db
   */
  public EventDao(Connection conn) {
    this.conn = conn;
  }

  @Override
  public boolean saveEvent(Event event) {
    int result;
    try (PreparedStatement prep =
        conn.prepareStatement(
            "INSERT INTO audit_task (id_task, action, task_title, execution_date)"
                + " VALUES (?,?,?,?)")) {
      prep.setInt(1, event.getIdEntity());
      prep.setString(2, event.getAction());
      prep.setString(3, event.getTitleEntity());
      prep.setTimestamp(4, Timestamp.valueOf(event.getExecutionDate()));
      result = prep.executeUpdate();
    } catch (Exception e) {
      throw new PersistenceException("Error: could not save the Audit\n", e);
    }
    return result == 1;
  }
}
