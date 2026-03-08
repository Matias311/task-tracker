package com.tasktracker.app.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tasktracker.app.repository.TaskRepositoryImpl;
import com.tasktracker.app.repository.observer.AudditLogger;
import com.tasktracker.app.service.TaskService;
import java.util.ArrayList;
import java.util.List;
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
    List<String> data = new ArrayList<>(List.of("Test", "", "", "", "", "", ""));
    TaskCommand command = new SaveTaskCommand(service, 1, data);
    command.execute();
    assertEquals("Test", service.getAllTask().get(0).getTitle());
  }

  @Test
  @DisplayName("Try to save a task with null data")
  void saveWithNullData() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new SaveTaskCommand(service, 1, null));
    assertEquals("The service or data must have a value", ex.getMessage());
  }

  @Test
  @DisplayName("Try to save with negative id")
  void saveWithNegativeId() {
    List<String> data = new ArrayList<>(List.of("Test", "", "", "", "", "", ""));
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new SaveTaskCommand(service, -1, data));
    assertEquals("The id must be > 0", ex.getMessage());
  }

  @Test
  @DisplayName("Complete a task using completeTaskCommand")
  void completeTaskUsingCommnad() {
    List<String> data = new ArrayList<>(List.of("Test", "", "", "", "", "", ""));
    TaskCommand command = new SaveTaskCommand(service, 1, data);
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
    List<String> data = new ArrayList<>(List.of("Test", "", "", "", "DONE", "", ""));
    TaskCommand command = new SaveTaskCommand(service, 1, data);
    command.execute();
    new UndoneTaskCommand(service, 1).execute();
    ;
    assertEquals("TODO", service.getAllTask().get(0).getStatus());
  }

  @Test
  @DisplayName("Delete task using DeleteCommand")
  void deleteTaskCommand() {
    List<String> data = new ArrayList<>(List.of("Test", "", "", "", "DONE", "", ""));
    TaskCommand command = new SaveTaskCommand(service, 1, data);
    command.execute();

    new DeleteCommand(service, 1).execute();
    assertEquals(0, service.getAllTask().size());
  }
}
