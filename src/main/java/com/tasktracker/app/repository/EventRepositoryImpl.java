package com.tasktracker.app.repository;

import com.tasktracker.app.domain.Event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory implementation of {@link EventRepository} for testing purposes.
 *
 * <p>This repository stores events in a {@link List} without any persistence layer. It's intended
 * for unit tests where transactional behavior with the database is not needed.
 */
public class EventRepositoryImpl implements EventRepository {

  private final List<Event> events = new ArrayList<>();

  @Override
  public boolean saveEvent(Event event) {
    return events.add(event);
  }

  /**
   * Returns an unmodifiable view of all stored events.
   *
   * @return list of events
   */
  public List<Event> getAll() {
    return Collections.unmodifiableList(events);
  }

  /**
   * Clears all events (useful for test cleanup).
   */
  public void clear() {
    events.clear();
  }
}
