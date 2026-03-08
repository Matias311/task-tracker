package com.tasktracker.app.repository.observer;

import com.tasktracker.app.domain.Task;

public interface Observer {

  void update(Task task, String action);
}
