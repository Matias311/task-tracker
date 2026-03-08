package com.tasktracker.app.repository.observer;

import com.tasktracker.app.domain.Task;
import java.util.ArrayList;
import java.util.List;

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
