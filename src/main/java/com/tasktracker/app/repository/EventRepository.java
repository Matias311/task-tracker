package com.tasktracker.app.repository;

import com.tasktracker.app.domain.Event;

/** This interface have the method declare to manage Event model. */
public interface EventRepository {

  /**
   * Save a event.
   *
   * @return true if its saved, false if its not
   */
  boolean saveEvent(Event event);
}
