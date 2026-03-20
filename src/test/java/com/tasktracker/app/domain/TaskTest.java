package com.tasktracker.app.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

@DisplayName("Test for the Task class")
public class TaskTest {

  @Test
  @DisplayName("Creates a task with all the parameters")
  void createTaskWithAllParameters() {
    Task task =
        new Task.Builder(1, "Test task")
            .priority("HIGH")
            .status("TODO")
            .description("Test task")
            .build();
    assertAll(
        () -> assertEquals("Test task", task.getTitle()),
        () -> assertEquals("HIGH", task.getPriority()),
        () -> assertEquals("TODO", task.getStatus()));
  }

  @Test
  @DisplayName(
      "Test for creating a task with the id and name, verify the id, name and see if the"
          + " description is null")
  void creatingTaskWithIdNameNullDescription() {
    Task task = new Task.Builder(1, "Test task").build();
    assertEquals("Test task", task.getTitle());
    assertNull(task.getDescription());
  }

  @Test
  @DisplayName("Creating a task with a id < 0")
  void creatingTaskLessThanZeroId() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new Task.Builder(-1, "Test").build());
    assertEquals("The id must be > 0", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Creating a task with null title")
  void creatingTaskNullTitle(String title) {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> new Task.Builder(1, title).build());
    assertEquals("The title must have a value", ex.getMessage());
  }

  @Test
  @DisplayName("Due date before date created")
  void taskDueDateBeforeDateCreated() {
    LocalDate date = LocalDate.of(2026, 02, 24);
    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Task.Builder(1, "Test").date(date.plusDays(1)).dueDate(date).build());
    assertEquals("The due date must be after the date", ex.getMessage());
  }

  @Test
  @DisplayName(
      "create task with a type, should throw a IllegalArgumentException 'cause its not part of the"
          + " TaskType enum")
  void createTaskWithTypeInvalid() {
    Exception ex =
        assertThrows(
            IllegalArgumentException.class, () -> new Task.Builder(1, "Test").type("SISI").build());
    assertEquals("The type is not valid", ex.getMessage());
  }

  @Test
  @DisplayName("create task with priority invalid should throw a IllegalArgumentException")
  void createTaskPriorityInvalid() {
    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Task.Builder(1, "Test").priority("SISI").build());
    assertEquals("The priority is not valid", ex.getMessage());
  }

  @Test
  @DisplayName("create task with invalid status, should throw a IllegalArgumentException")
  void createTaskInvalidStatus() {
    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Task.Builder(1, "Test").status("SISI").build());
    assertEquals("The Status is not valid", ex.getMessage());
  }

  @Test
  @DisplayName("Test to update the status of a task")
  void updateStatusTask() {
    Task oldTask = new Task.Builder(1, "Test").build();
    assertEquals("Test", oldTask.getTitle());
    assertEquals("TODO", oldTask.getStatus());
    Task newTask = oldTask.updateStatus("DOING");
    assertEquals("DOING", newTask.getStatus());
  }

  @Test
  @DisplayName("Test invalid status updating the task")
  void invalidStatusUpdatingTask() {
    Task oldTask = new Task.Builder(1, "Test").build();
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> oldTask.updateStatus("YEYEY"));
    assertEquals("Invalid status", ex.getMessage());
  }
}
