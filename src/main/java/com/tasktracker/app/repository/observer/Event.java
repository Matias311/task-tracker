package com.tasktracker.app.repository.observer;

public class Event {

  private String action;
  private int idEntity;
  private String titleEntity;

  public String getAction() {
    return action;
  }

  public int getIdEntity() {
    return idEntity;
  }

  public String getTitleEntity() {
    return titleEntity;
  }

  public Event(String action, int idEntity, String titleEntity) {
    this.action = action;
    this.idEntity = idEntity;
    this.titleEntity = titleEntity;
  }
}
