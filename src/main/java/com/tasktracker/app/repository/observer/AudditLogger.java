package com.tasktracker.app.repository.observer;

import com.tasktracker.app.domain.Task;
import java.util.ArrayList;
import java.util.List;

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
public class AudditLogger implements Observer {

  private List<Event> list = new ArrayList<>();

  @Override
  public void update(Task task, String action) {
    String o =
        String.format(
            "AUDIT: %s EXECUTE ON THE TASK:\nTASK ID: %d\nTASK TITLE: %s",
            action, task.getId(), task.getTitle());
    System.out.println(o);
    list.add(new Event(action, task.getId(), task.getTitle()));
  }
}
