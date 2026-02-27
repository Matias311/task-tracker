package com.tasktracker.app.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.repository.TaskRepositoryImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test for class task service")
public class TaskServiceTest {

  private TaskService service;

  @BeforeEach
  void setUp() {
    this.service = new TaskService(new TaskRepositoryImpl());
  }

  @Test
  @DisplayName("Create a task with the id and title, the rest is empty value")
  void saveTaskWithTitleId() {
    service.saveTask(1, "Test", "", "", "", "", "", "");
    assertEquals("Test", service.getAllTask().get(0).getTitle());
  }

  @Test
  @DisplayName("Get all the saved task")
  void getAllTask() {
    service.saveTask(1, "Test", "", "", "", "", "", "");
    service.saveTask(2, "Test", "PROGRAMMING", "Test task", "", "", "", "");
    List<Task> list = service.getAllTask();
    assertAll(
        () -> assertEquals(2, list.size()),
        () -> assertEquals("Test", list.get(0).getTitle()),
        () -> assertEquals("PROGRAMMING", list.get(1).getType()));
  }

  @Test
  @DisplayName("Filter task by type")
  void filterByType() {
    service.saveTask(1, "Test", "", "", "", "", "", "");
    service.saveTask(2, "Test2", "PROGRAMMING", "Test task", "", "", "", "");
    List<Task> list = service.filterByTypeTask("PROGRAMMING");
    assertEquals("Test2", list.get(0).getTitle());
  }

  @Test
  @DisplayName("Filter task by type with invalid type")
  void filterWithInvalidType() {
    service.saveTask(1, "Test", "", "", "", "", "", "");
    service.saveTask(2, "Test2", "PROGRAMMING", "Test task", "", "", "", "");
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> service.filterByTypeTask("SISI"));
    assertEquals("The status task is invalid", ex.getMessage());
  }

  @Test
  @DisplayName("Filter task by priority")
  void filterByPriority() {
    service.saveTask(1, "Test", "", "", "", "", "", "");
    service.saveTask(2, "Test2", "PROGRAMMING", "Test task", "HIGH", "", "", "");
    List<Task> list = service.filterByPriorityTask("HIGH");
    assertEquals("Test2", list.get(0).getTitle());
  }

  @Test
  @DisplayName("Filter task by type with invalid priority")
  void filterWithInvalidPriority() {
    service.saveTask(1, "Test", "", "", "", "", "", "");
    service.saveTask(2, "Test2", "PROGRAMMING", "Test task", "HIGH", "", "", "");
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> service.filterByPriorityTask("SISI"));
    assertEquals("The priority task is invalid", ex.getMessage());
  }

  @Test
  @DisplayName("Filter task by status")
  void filterByStatus() {
    service.saveTask(1, "Test", "", "", "", "", "", "");
    service.saveTask(2, "Test2", "PROGRAMMING", "Test task", "HIGH", "DONE", "", "");
    List<Task> list = service.filterByStatusTask("DONE");
    assertEquals("Test2", list.get(0).getTitle());
  }

  @Test
  @DisplayName("Filter task by type with invalid status")
  void filterWithInvalidStatus() {
    service.saveTask(1, "Test", "", "", "", "", "", "");
    service.saveTask(2, "Test2", "PROGRAMMING", "Test task", "HIGH", "DONE", "", "");
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> service.filterByStatusTask("SISI"));
    assertEquals("The status task is invalid", ex.getMessage());
  }

  @Test
  @DisplayName("Complete task")
  void completeTask() {
    Task task = service.completeTask(new Task.Builder(1, "Test").build());
    assertEquals("DONE", task.getStatus());
  }

  @Test
  @DisplayName("Complete task with null task")
  void completeNullTask() {
    Exception ex = assertThrows(IllegalArgumentException.class, () -> service.completeTask(null));
    assertEquals("Invalid task", ex.getMessage());
  }

  @Test
  @DisplayName("Complete done task")
  void completeDoneTask() {
    Exception ex =
        assertThrows(
            IllegalStateException.class,
            () -> service.completeTask(new Task.Builder(1, "Test").status("DONE").build()));
    assertEquals("The task is already in TODO status", ex.getMessage());
  }

  // TODO: terminar
  @Test
  @DisplayName("Search by id")
  void searchTaskById() {
    service.saveTask(2, "Test2", "PROGRAMMING", "Test task", "HIGH", "DONE", "", "");
  }

  @Test
  @DisplayName("Search by invalid id")
  void searchByInvalidId() {}
}
