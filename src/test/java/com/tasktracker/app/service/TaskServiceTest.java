package com.tasktracker.app.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tasktracker.app.domain.Task;
import com.tasktracker.app.exception.NotFoundException;
import com.tasktracker.app.repository.TaskRepositoryImpl;
import com.tasktracker.app.repository.observer.AudditLogger;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

@DisplayName("Test for class task service")
public class TaskServiceTest {

  private TaskService service;

  @BeforeEach
  void setUp() {
    this.service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());
  }

  @Test
  @DisplayName("Create a task with the id and title, the rest is empty value")
  void saveTaskWithValidIdAndTitle() {
    service.saveTask(new Task.Builder(1, "Test").build());
    assertEquals("Test", service.getAllTask().get(0).getTitle());
  }

  @Test
  @DisplayName("Get all the saved task")
  void getAllTasksReturnsAllSavedTasks() {
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
  @DisplayName("Get all the saved task (should return a empty list)")
  void getAllTaskReturnsEmptyList() {
    List<Task> list = service.getAllTask();
    assertTrue(list.isEmpty());
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
  @DisplayName("Filter task by type should return a emtpy list")
  void filterByTypeReturnEmptyList() {
    List<Task> list = service.filterByTypeTask("PROGRAMMING");
    assertTrue(list.isEmpty());
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
  @DisplayName("Filter task by priority should return a emtpy list")
  void filterByPriorityReturnEmptyList() {
    List<Task> list = service.filterByPriorityTask("HIGH");
    assertTrue(list.isEmpty());
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
  @DisplayName("Filter task by status should return a emtpy list")
  void filterByStatusReturnEmptyList() {
    List<Task> list = service.filterByStatusTask("TODO");
    assertTrue(list.isEmpty());
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
  void completeTaskChangesStatusToDone() {
    Task task = new Task.Builder(1, "Test").build();
    service.saveTask(task);
    service.completeTask(task);

    Task newtask = service.searchTaskById(1);
    assertEquals("DONE", newtask.getStatus());
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
    service.deleteTask(task);

    assertFalse(service.getAllTask().stream().anyMatch(t -> t.getId() == 1));
  }

  @Test
  @DisplayName("Delete task with null task")
  void deleteTaskWithNull() {
    Exception ex = assertThrows(IllegalArgumentException.class, () -> service.deleteTask(null));
    assertEquals("Invalid task", ex.getMessage());
  }

  @Test
  @DisplayName("Cache is updated when task is completed")
  void cacheUpdatedAfterComplete() {
    Task task = new Task.Builder(1, "Test").build();
    service.saveTask(task);

    Task cached = service.searchTaskById(1);
    assertEquals("TODO", cached.getStatus());

    service.completeTask(cached);

    Task updatedCached = service.searchTaskById(1);
    assertEquals("DONE", updatedCached.getStatus());
  }

  @Test
  @DisplayName("Cache is updated when task is todo")
  void cacheUpdatedAfterTodo() {
    Task task = new Task.Builder(1, "Test").status("DONE").build();
    service.saveTask(task);

    Task cached = service.searchTaskById(1);
    assertEquals("DONE", cached.getStatus());

    service.undoneTask(cached);

    Task updatedCached = service.searchTaskById(1);
    assertEquals("TODO", updatedCached.getStatus());
  }

  @Test
  @DisplayName("Complete task throws NotFoundException if task not in cache")
  void completeTaskThrowsNotFoundExceptionIfTaskNotInCache() {
    Task task = new Task.Builder(1, "Test").build();
    service.saveTask(task);
    service.deleteTask(task);

    assertThrows(NotFoundException.class, () -> service.completeTask(task));
  }

  @Test
  @DisplayName("Order the task by due date (only if the task are not DONE)")
  void orderTaskByDueDateOnlyTodoStatus() {
    Task task = new Task.Builder(1, "Test").build();
    Task task2 = new Task.Builder(2, "Test").dueDate(LocalDate.of(2026, 03, 21)).build();
    service.saveTask(task);
    service.saveTask(task2);
    List<Task> list = service.orderTaskByDueDate();
    assertAll(
        () -> assertEquals(2, list.size()),
        () -> assertEquals(1, list.get(0).getId()),
        () -> assertEquals(2, list.get(1).getId()));
  }

  @Test
  @DisplayName(
      "Order the task by due date (only if the task are not DONE). Should return empty list")
  void orderTaskByDueDateOnlyTodoStatusReturnEmptyList() {
    List<Task> list = service.orderTaskByDueDate();
    assertTrue(list.isEmpty());
  }

  @Test
  @DisplayName("Order the task by priority (only if the task are not DONE)")
  void orderTaskByPriorityTodoStatus() {
    Task task = new Task.Builder(1, "Test").priority("LOW").build();
    Task task2 = new Task.Builder(2, "Test").priority("HIGH").build();
    service.saveTask(task);
    service.saveTask(task2);
    List<Task> list = service.orderTaskByPriority();
    assertAll(
        () -> assertEquals(2, list.size()),
        () -> assertEquals(2, list.get(0).getId()),
        () -> assertEquals(1, list.get(1).getId()));
  }

  @Test
  @DisplayName(
      "Order the task by priority (only if the task are not DONE). Should return empty list")
  void orderTaskByPriorityOnlyTodoStatusReturnEmptyList() {
    List<Task> list = service.orderTaskByPriority();
    assertTrue(list.isEmpty());
  }
}
