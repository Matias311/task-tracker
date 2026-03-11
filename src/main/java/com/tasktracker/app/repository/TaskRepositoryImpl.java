package com.tasktracker.app.repository;

import com.tasktracker.app.domain.Task;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** TaskRepositoryImpl. */
public class TaskRepositoryImpl implements TaskRepository {

  private List<Task> taskList = new ArrayList<>();

  @Override
  public boolean save(Task task) {
    return taskList.add(task);
  }

  @Override
  public List<Task> getAllTask() {
    return Collections.unmodifiableList(taskList);
  }

  @Override
  public List<Task> filterByType(String type) {
    return taskList.stream()
        .filter(t -> t.getType() != null)
        .filter(t -> t.getType().equals(type))
        .toList();
  }

  @Override
  public List<Task> filterByPriority(String type) {
    return taskList.stream()
        .filter(t -> t.getPriority() != null)
        .filter(t -> t.getPriority().equals(type))
        .toList();
  }

  @Override
  public List<Task> filterByStatus(String type) {
    return taskList.stream()
        .filter(t -> t.getStatus() != null)
        .filter(t -> t.getStatus().equals(type))
        .toList();
  }

  @Override
  public boolean completeTask(Task task) {
    if (!deleteTask(task)) {
      return false;
    }
    Task newTask = task.updateStatus("DONE");
    return taskList.add(newTask);
  }

  @Override
  public List<Task> orderByDueDate() {
    return taskList.stream()
        .filter(t -> t.getStatus() != null)
        .filter(t -> !t.getStatus().equals("DONE"))
        .sorted(Comparator.comparing(Task::getDueDate).thenComparing(Task::getTitle))
        .toList();
  }

  @Override
  public List<Task> orderByPriority() {
    return taskList.stream()
        .filter(t -> t.getPriority() != null)
        .filter(t -> t.getStatus() != null)
        .filter(t -> !t.getStatus().equals("DONE"))
        .sorted(Comparator.comparing(Task::getPriority).thenComparing(Task::getTitle))
        .toList();
  }

  @Override
  public Optional<Task> searchById(int id) {
    return taskList.stream().filter(t -> t.getId() == id).findFirst();
  }

  @Override
  public List<Task> getAllTaskComplete() {
    return taskList.stream()
        .filter(t -> t.getStatus() != null)
        .filter(t -> t.getStatus().equals("DONE"))
        .toList();
  }

  @Override
  public boolean undoneTask(Task task) {
    if (!deleteTask(task)) {
      return false;
    }
    Task newTask = task.updateStatus("TODO");
    return taskList.add(newTask);
  }

  @Override
  public boolean deleteTask(Task task) {
    return taskList.remove(task);
  }
}
