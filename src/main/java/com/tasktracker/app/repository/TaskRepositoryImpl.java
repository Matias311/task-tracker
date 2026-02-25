package com.tasktracker.app.repository;

import com.tasktracker.app.domain.Task;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// TODO: test what happend when a task in the filter/order doesnt have value

/** TaskRepositoryImpl. */
public class TaskRepositoryImpl implements TaskRepository {

  private List<Task> taskList = new ArrayList<>();

  @Override
  public void createTask(
      int id,
      String title,
      String type,
      String description,
      String priority,
      String status,
      LocalDate date,
      LocalDate dueDate) {
    taskList.add(
        new Task.Builder(id, title)
            .type(type)
            .description(description)
            .priority(priority)
            .status(status)
            .date(date)
            .dueDate(dueDate)
            .build());
  }

  @Override
  public List<Task> getAllTask() {
    return Collections.unmodifiableList(taskList);
  }

  @Override
  public List<Task> filterByType(String type) {
    return taskList.stream().filter(t -> t.getType().equals(type)).toList();
  }

  @Override
  public List<Task> filterByPriority(String type) {
    return taskList.stream().filter(t -> t.getPriority().equals(type)).toList();
  }

  @Override
  public List<Task> filterByStatus(String type) {
    return taskList.stream().filter(t -> t.getStatus().equals(type)).toList();
  }

  @Override
  public void completeTask(Task task) {
    task.updateStatus("DONE");
  }

  @Override
  public List<Task> orderByDueDate() {
    return taskList.stream()
        .filter(t -> !t.getStatus().equals("DONE"))
        .sorted(Comparator.comparing(Task::getDueDate).thenComparing(Task::getTitle))
        .toList();
  }

  @Override
  public List<Task> orderByPriority() {
    return taskList.stream()
        .filter(t -> !t.getStatus().equals("DONE"))
        .sorted(Comparator.comparing(Task::getPriority).thenComparing(Task::getTitle))
        .toList();
  }

  @Override
  public Optional<Task> searchById(int id) {
    return Optional.of(taskList.get(id));
  }

  @Override
  public List<Task> getAllTaskComplete() {
    return taskList.stream().filter(t -> t.getStatus().equals("DONE")).toList();
  }

  @Override
  public void undoneTask(Task task) {
    task.updateStatus("TODO");
  }
}
