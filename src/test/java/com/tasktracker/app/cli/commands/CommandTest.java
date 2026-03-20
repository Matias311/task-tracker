package com.tasktracker.app.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tasktracker.app.repository.TaskRepositoryImpl;
import com.tasktracker.app.repository.observer.AudditLogger;
import com.tasktracker.app.service.TaskService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Class test for the TaskCommand implementations")
public class CommandTest {

  private TaskService service;

  @BeforeEach
  void setUp() {
    this.service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());
  }

  /**
   * Helper method to create and execute a SaveTaskCommand. Reduces code duplication and improves
   * test readability.
   *
   * @param id the task ID
   * @param title the task title
   * @param status the initial status (null for TODO)
   */
  private void saveTaskWithStatus(int id, String title, String status) {
    TaskCommand command =
        new SaveTaskCommand(
            service,
            id,
            title,
            null, // type
            null, // description
            null, // priority
            status,
            LocalDate.now(),
            LocalDate.now().plusDays(1));
    command.execute();
  }

  /** Convenience method to save a TODO task (default status). */
  private void saveTodoTask(int id, String title) {
    saveTaskWithStatus(id, title, null);
  }

  /** Convenience method to save a DONE task. */
  private void saveDoneTask(int id, String title) {
    saveTaskWithStatus(id, title, "DONE");
  }

  @Test
  @DisplayName("Save a task using SaveTaskCommand")
  void savingTaskCommand() {
    saveTodoTask(1, "Test");

    assertEquals("Test", service.getAllTask().get(0).getTitle());
  }

  @Test
  @DisplayName("Save a task with specific status using SaveTaskCommand")
  void savingTaskWithStatusCommand() {
    saveDoneTask(2, "Completed task");

    assertEquals("Completed task", service.getAllTask().get(0).getTitle());
    assertEquals("DONE", service.getAllTask().get(0).getStatus());
  }

  @Test
  @DisplayName("Save multiple tasks")
  void saveMultipleTasks() {
    saveTodoTask(1, "Task 1");
    saveTodoTask(2, "Task 2");
    saveTodoTask(3, "Task 3");

    assertEquals(3, service.getAllTask().size());
  }

  @Test
  @DisplayName("Complete a task using CompleteTaskCommand")
  void completeTaskUsingCommand() {
    saveTodoTask(1, "Test");

    new CompleteTaskCommand(service, 1).execute();

    assertEquals("DONE", service.getAllTask().get(0).getStatus());
  }

  @Test
  @DisplayName("Complete task with invalid id throws exception")
  void completeTaskWithInvalidIdThrowsException() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new CompleteTaskCommand(service, -1));
    assertEquals("Id must be > 0", ex.getMessage());
  }

  @Test
  @DisplayName("Complete task with zero id throws exception")
  void completeTaskWithZeroIdThrowsException() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new CompleteTaskCommand(service, 0));
    assertEquals("Id must be > 0", ex.getMessage());
  }

  @Test
  @DisplayName("Undone a task using UndoneTaskCommand")
  void undoneTaskUsingCommand() {
    saveDoneTask(1, "Test");

    new UndoneTaskCommand(service, 1).execute();

    assertEquals("TODO", service.getAllTask().get(0).getStatus());
  }

  @Test
  @DisplayName("Undone task with invalid id throws exception")
  void undoneTaskWithInvalidIdThrowsException() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new UndoneTaskCommand(service, -1));
    assertEquals("Id must be > 0", ex.getMessage());
  }

  @Test
  @DisplayName("Undone task with zero id throws exception")
  void undoneTaskWithZeroIdThrowsException() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new UndoneTaskCommand(service, 0));
    assertEquals("Id must be > 0", ex.getMessage());
  }

  @Test
  @DisplayName("Delete task using DeleteCommand")
  void deleteTaskCommand() {
    saveDoneTask(1, "Test");

    new DeleteCommand(service, 1).execute();

    assertEquals(0, service.getAllTask().size());
  }

  @Test
  @DisplayName("Delete task with invalid id throws exception")
  void deleteTaskWithInvalidIdThrowsException() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new DeleteCommand(service, -1));
    assertEquals("Id must be > 0", ex.getMessage());
  }

  @Test
  @DisplayName("Delete task with zero id throws exception")
  void deleteTaskWithZeroIdThrowsException() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new DeleteCommand(service, 0));
    assertEquals("Id must be > 0", ex.getMessage());
  }

  @Test
  @DisplayName("Delete task removes it from service")
  void deleteTaskRemovesFromService() {
    saveTodoTask(1, "Task to delete");
    saveTodoTask(2, "Task to keep");

    assertEquals(2, service.getAllTask().size());

    new DeleteCommand(service, 1).execute();

    assertEquals(1, service.getAllTask().size());
    assertEquals("Task to keep", service.getAllTask().get(0).getTitle());
  }
}
