package com.tasktracker.app.repository;

import com.tasktracker.app.domain.Task;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** TaskRepositoryImpl. */
public class TaskRepositoryImpl implements TaskRepository {

  private List<Task> taskList = new ArrayList<>();

  @Override
  public void createTask(
      int id,
      String title,
      List<String> type,
      String description,
      String priority,
      List<String> status,
      LocalDate date,
      LocalDate dueDate) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createTask'");
  }

  @Override
  public List<Task> getAllTask() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAllTask'");
  }

  @Override
  public List<Task> filterByType(String type) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'filterByType'");
  }

  @Override
  public List<Task> filterByPriority(String type) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'filterByPriority'");
  }

  @Override
  public List<Task> filterByStatus(String type) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'filterByStatus'");
  }

  @Override
  public void completeTask(Task task) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'completeTask'");
  }

  @Override
  public List<Task> orderByDueDate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'orderByDueDate'");
  }

  @Override
  public List<Task> orderByPriority() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'orderByPriority'");
  }

  @Override
  public Optional<Task> searchById(int id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'searchById'");
  }
}
