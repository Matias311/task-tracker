package com.tasktracker.app.repository.observer;

import com.tasktracker.app.Exception.PersistenceException;
import com.tasktracker.app.domain.Event;
import com.tasktracker.app.domain.Task;
import com.tasktracker.app.service.EventService;

/**
 * Observer implementation responsible for auditing task-related actions.
 *
 * <p>This class receives notifications whenever an operation is executed on a {@link Task}. Each
 * event is logged to the console and stored internally as an {@link Event} for auditing purposes.
 *
 * <p>The logger records basic information about the action performed, including the action type,
 * task identifier and task title.
 *
 * <p>This class is typically used by services that want to track task operations such as creation,
 * completion or deletion.
 */
public class AudditLoggerInDB implements Observer {

  private EventService service;

  /**
   * Constructor, you must pass EventService.
   *
   * @param service EventService
   */
  public AudditLoggerInDB(EventService service) {
    this.service = service;
  }

  @Override
  public void update(Task task, String action) {
    String o =
        String.format(
            "AUDIT: %s EXECUTE ON THE TASK:\nTASK ID: %d\nTASK TITLE: %s",
            action, task.getId(), task.getTitle());
    System.out.println(o);
    try {
      service.saveEvent(new Event(action, task.getId(), task.getTitle()));
    } catch (PersistenceException ex) {
      System.out.println(ex);
    }
  }
}
