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
  public void save(Task task) {
    taskList.add(task);
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
  public Task completeTask(Task task) {
    return task.updateStatus("DONE");
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

  // TODO: arreglar bug, esta buscando por largo pero esta mal deberia de buscar en toda la lista y
  // retornar la task con el valor correcto
  @Override
  public Optional<Task> searchById(int id) {
    return Optional.of(taskList.get(id));
  }

  @Override
  public List<Task> getAllTaskComplete() {
    return taskList.stream()
        .filter(t -> t.getStatus() != null)
        .filter(t -> t.getStatus().equals("DONE"))
        .toList();
  }

  @Override
  public Task undoneTask(Task task) {
    return task.updateStatus("TODO");
  }
}
