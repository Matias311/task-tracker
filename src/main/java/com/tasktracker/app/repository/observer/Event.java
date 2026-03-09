package com.tasktracker.app.repository.observer;

/**
 * Represents an audit event generated when an action is performed on an entity.
 *
 * <p>An event stores basic information about the executed action, including the action type, the
 * identifier of the affected entity and its title. These events are typically created by observers
 * responsible for auditing operations performed on tasks.
 *
 * <p>This class is commonly used by audit loggers to keep a record of actions executed in the
 * system.
 */
public class Event {

  /** The action performed on the entity (e.g. SAVE, DELETE, COMPLETE). */
  private String action;

  /** Identifier of the entity involved in the event. */
  private int idEntity;

  /** Title or name of the entity involved in the event. */
  private String titleEntity;

  /**
   * Creates a new audit event.
   *
   * @param action the action performed
   * @param idEntity identifier of the entity
   * @param titleEntity title or name of the entity
   */
  public Event(String action, int idEntity, String titleEntity) {
    this.action = action;
    this.idEntity = idEntity;
    this.titleEntity = titleEntity;
  }

  /**
   * Returns the action associated with this event.
   *
   * @return the action performed
   */
  public String getAction() {
    return action;
  }

  /**
   * Returns the identifier of the entity involved in the event.
   *
   * @return entity identifier
   */
  public int getIdEntity() {
    return idEntity;
  }

  /**
   * Returns the title or name of the entity involved in the event.
   *
   * @return entity title
   */
  public String getTitleEntity() {
    return titleEntity;
  }
}
