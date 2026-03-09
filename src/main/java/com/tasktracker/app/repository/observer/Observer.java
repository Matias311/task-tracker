package com.tasktracker.app.repository.observer;

import com.tasktracker.app.domain.Task;

/**
 * Defines the contract for objects that want to observe task-related events.
 *
 * <p>This interface follows the Observer pattern. Implementations are notified whenever an action
 * is performed on a {@link Task}. Typical implementations include components responsible for
 * auditing, logging, or triggering additional behavior after task operations.
 *
 * <p>The observer is usually invoked by a service or repository that manages tasks and wants to
 * notify interested components when changes occur.
 */
public interface Observer {

  /**
   * Notifies the observer that an action has been performed on a task.
   *
   * @param task the task involved in the event
   * @param action the action performed on the task (for example: SAVE, DELETE, COMPLETE)
   */
  void update(Task task, String action);
}
