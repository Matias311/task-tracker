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

@DisplayName("Class test for the Taskcommand")
public class CommandTest {

  private TaskService service;

  @BeforeEach
  void setUp() {
    this.service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());
  }

  @Test
  @DisplayName("Saving a task using a command")
  void savingTaskCommand() {
    TaskCommand command =
        new SaveTaskCommand(
            service,
            1,
            "Test",
            null,
            null,
            null,
            null,
            LocalDate.now(),
            LocalDate.now().plusDays(1));
    command.execute();
    assertEquals("Test", service.getAllTask().get(0).getTitle());
  }

  @Test
  @DisplayName("Complete a task using completeTaskCommand")
  void completeTaskUsingCommnad() {
    TaskCommand command =
        new SaveTaskCommand(
            service,
            1,
            "Test",
            null,
            null,
            null,
            null,
            LocalDate.now(),
            LocalDate.now().plusDays(1));
    command.execute();

    new CompleteTaskCommand(service, 1).execute();
    assertEquals("DONE", service.getAllTask().get(0).getStatus());
  }

  @Test
  @DisplayName("Try to complete a task with invalid id")
  void tryCompleteTaskWithInvalidId() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new CompleteTaskCommand(service, -1));
    assertEquals("Id must be > 0", ex.getMessage());
  }

  @Test
  @DisplayName("Undone a task using UndoneTaskCommand")
  void undoneTaskUsingCommand() {

    TaskCommand command =
        new SaveTaskCommand(
            service,
            1,
            "Test",
            null,
            null,
            null,
            "DONE",
            LocalDate.now(),
            LocalDate.now().plusDays(1));
    command.execute();

    new UndoneTaskCommand(service, 1).execute();

    assertEquals("TODO", service.getAllTask().get(0).getStatus());
  }

  @Test
  @DisplayName("Delete task using DeleteCommand")
  void deleteTaskCommand() {
    TaskCommand command =
        new SaveTaskCommand(
            service,
            1,
            "Test",
            null,
            null,
            null,
            "DONE",
            LocalDate.now(),
            LocalDate.now().plusDays(1));
    command.execute();

    new DeleteCommand(service, 1).execute();
    assertEquals(0, service.getAllTask().size());
  }
}
