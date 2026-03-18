package com.tasktracker.app.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.repository.EventRepositoryImpl;
import com.tasktracker.app.repository.TaskRepositoryImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

@Tag("unit")
@DisplayName("Unit tests for TaskService (in-memory, no database)")
public class TaskServiceTest {

  private TaskService service;
  private EventRepositoryImpl eventRepo;

  @BeforeEach
  void setUp() {
    eventRepo = new EventRepositoryImpl();
    // Constructor sin Connection - para tests in-memory
    this.service = new TaskService(new TaskRepositoryImpl(), eventRepo);
  }

  @Test
  @DisplayName("Create a task with the id and title, the rest is empty value")
  void saveTaskWithTitleId() {
    service.saveTask(new Task.Builder(1, "Test").build());
    assertEquals("Test", service.getAllTask().get(0).getTitle());
  }

  @Test
  @DisplayName("Get all the saved task")
  void getAllTask() {
    service.saveTask(new Task.Builder(1, "Test").build());
    service.saveTask(
        new Task.Builder(2, "Test").type("PROGRAMMING").description("Test task").build());
    List<Task> list = service.getAllTask();
    assertAll(
        () -> assertEquals(2, list.size()),
        () -> assertEquals("Test", list.get(0).getTitle()),
        () -> assertEquals("PROGRAMMING", list.get(1).getType()));
  }

  @Test
  @DisplayName("Filter task by type")
  void filterByType() {
    service.saveTask(new Task.Builder(1, "Test").build());
    service.saveTask(
        new Task.Builder(2, "Test2").type("PROGRAMMING").description("Test task").build());
    List<Task> list = service.filterByTypeTask("PROGRAMMING");
    assertEquals("Test2", list.get(0).getTitle());
  }

  @Test
  @DisplayName("Filter task by type with invalid type")
  void filterWithInvalidType() {
    service.saveTask(new Task.Builder(1, "Test").build());
    service.saveTask(
        new Task.Builder(2, "Test2").type("PROGRAMMING").description("Test task").build());
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> service.filterByTypeTask("SISI"));
    assertEquals("The status task is invalid", ex.getMessage());
  }

  @Test
  @DisplayName("Filter task by priority")
  void filterByPriority() {
    service.saveTask(new Task.Builder(1, "Test").build());
    service.saveTask(
        new Task.Builder(2, "Test2")
            .type("PROGRAMMING")
            .description("Test task")
            .priority("HIGH")
            .build());
    List<Task> list = service.filterByPriorityTask("HIGH");
    assertEquals("Test2", list.get(0).getTitle());
  }

  @Test
  @DisplayName("Filter task by type with invalid priority")
  void filterWithInvalidPriority() {
    service.saveTask(new Task.Builder(1, "Test").build());
    service.saveTask(
        new Task.Builder(2, "Test2")
            .type("PROGRAMMING")
            .description("Test task")
            .priority("HIGH")
            .build());
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> service.filterByPriorityTask("SISI"));
    assertEquals("The priority task is invalid", ex.getMessage());
  }

  @Test
  @DisplayName("Filter task by status")
  void filterByStatus() {
    service.saveTask(new Task.Builder(1, "Test").build());
    service.saveTask(
        new Task.Builder(2, "Test2")
            .type("PROGRAMMING")
            .description("Test task")
            .priority("HIGH")
            .status("DONE")
            .build());

    List<Task> list = service.filterByStatusTask("DONE");
    assertEquals("Test2", list.get(0).getTitle());
  }

  @Test
  @DisplayName("Filter task by type with invalid status")
  void filterWithInvalidStatus() {
    service.saveTask(new Task.Builder(1, "Test").build());
    service.saveTask(
        new Task.Builder(2, "Test2")
            .type("PROGRAMMING")
            .description("Test task")
            .priority("HIGH")
            .status("DONE")
            .build());

    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> service.filterByStatusTask("SISI"));
    assertEquals("The status task is invalid", ex.getMessage());
  }

  @Test
  @DisplayName("Complete task")
  void completeTask() {
    Task task = new Task.Builder(1, "Test").build();
    service.saveTask(task);
    service.completeTask(task);

    Task newtask = service.searchTaskById(1);
    assertEquals("DONE", newtask.getStatus());
  }

  @Test
  @DisplayName("Complete task verify in memory the update")
  void completeTaskInMemory() {
    service.saveTask(new Task.Builder(1, "Test").build());

    List<Task> list = service.getAllTask();
    service.completeTask(list.get(0));
    assertEquals("DONE", list.get(0).getStatus());
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
    assertEquals("The task is already in DONE status", ex.getMessage());
  }

  @Test
  @DisplayName("Search by id")
  void searchTaskById() {
    service.saveTask(
        new Task.Builder(42341, "Test2")
            .type("PROGRAMMING")
            .description("Test task")
            .priority("HIGH")
            .status("DONE")
            .build());
    assertEquals(42341, service.searchTaskById(42341).getId());
  }

  @Test
  @DisplayName("Search by invalid id")
  void searchByInvalidId() {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> service.searchTaskById(-42314));
    assertEquals("Invalid id", ex.getMessage());
  }

  @Test
  @DisplayName("Undone task")
  void undoneTask() {
    service.saveTask(
        new Task.Builder(42341, "Test2")
            .type("PROGRAMMING")
            .description("Test task")
            .priority("HIGH")
            .status("DONE")
            .build());
    Task task = service.searchTaskById(42341);
    service.undoneTask(task);
    assertEquals("TODO", service.searchTaskById(42341).getStatus());
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Undone task with invalid task (null)")
  void undoneTaskWithNullTask(Task task) {
    Exception ex = assertThrows(IllegalArgumentException.class, () -> service.undoneTask(task));
    assertEquals("Invalid task", ex.getMessage());
  }

  @Test
  @DisplayName("Undone task with invalid status task (TODO)")
  void undoneTaskWithInvalidStatusTask() {
    Task task = new Task.Builder(1, "test").status("TODO").build();
    Exception ex = assertThrows(IllegalStateException.class, () -> service.undoneTask(task));
    assertEquals("The task is already in TODO status", ex.getMessage());
  }

   @Test
   @DisplayName("Delete task")
   void deleteTask() {
     service.saveTask(new Task.Builder(1, "Test").build());
     Task task = service.searchTaskById(1);
     boolean deleted = service.deleteTask(task);
     assertTrue(deleted);
     assertFalse(service.getAllTask().stream().map(Task::getTitle).toList().contains("Test"));
   }

  @Test
  @DisplayName("Delete task with null task")
  void deleteTaskWithNull() {
    Exception ex = assertThrows(IllegalArgumentException.class, () -> service.deleteTask(null));
    assertEquals("Invalid task", ex.getMessage());
  }
}
