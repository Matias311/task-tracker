package com.tasktracker.app.service;

import com.tasktracker.app.domain.Event;
import com.tasktracker.app.repository.EventRepository;
import com.tasktracker.app.utils.VerifyData;

/**
 * Service responsible for managing {@link Event} instances.
 *
 * <p>This service provides operation to save All persistence operation are delegate to a {@link
 * EventRespository} implementation
 */
public final class EventService {

  private EventRepository repo;

  /**
   * Constructor, you must pass the repository.
   *
   * @param repo EventRespository
   */
  public EventService(EventRepository repo) {
    this.repo = repo;
  }

  /**
   * Save the event, if the identity or action or title entity are null or empty throw
   * IllegalArgumentException.
   *
   * @param event Event , must have all the values
   */
  public void saveEvent(Event event) {
    VerifyData.verifyInt(event.getIdEntity(), "The event task id must be > 0");
    VerifyData.verifyString(event.getAction(), "The action event must have a value");
    VerifyData.verifyString(
        event.getTitleEntity(), "The title task in the event must have a value");
    repo.saveEvent(event);
  }
}
