package com.tasktracker.app.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tasktracker.app.domain.Task;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test for the repository implementation")
public class TaskRepositoryImplTest {
  private TaskRepositoryImpl repo;

  @BeforeEach
  void setUp() {
    this.repo = new TaskRepositoryImpl();
  }

  @Test
  @DisplayName("Saving a task with all the parameters")
  void savingTaskWithAllParameters() {
    Task task =
        new Task.Builder(1, "Hola")
            .type("PROGRAMMING")
            .date(LocalDate.now())
            .description("Test")
            .status("TODO")
            .dueDate(LocalDate.now().plusDays(1))
            .build();
    repo.save(task);
    assertEquals("Hola", repo.getAllTask().get(0).getTitle());
  }

  @Test
  @DisplayName("Get all the task from memory (3 task)")
  void getAllTask() {
    Task task1 = new Task.Builder(1, "1").build();
    Task task2 = new Task.Builder(2, "2").build();
    Task task3 = new Task.Builder(3, "3").build();
    repo.save(task1);
    repo.save(task2);
    repo.save(task3);
    List<Task> list = repo.getAllTask();
    assertAll(
        () -> assertEquals(3, list.size()),
        () -> assertEquals("1", list.get(0).getTitle()),
        () -> assertEquals("2", list.get(1).getTitle()),
        () -> assertEquals("3", list.get(2).getTitle()));
  }

  @Test
  @DisplayName("Get all the task with empty list")
  void getAllTaskEmptyList() {
    List<Task> list = repo.getAllTask();
    assertEquals(0, list.size());
  }

  @Test
  @DisplayName("Filter task by type")
  void filterTaskByType() {
    Task task1 = new Task.Builder(1, "1").type("LIVE").build();
    Task task2 = new Task.Builder(2, "2").type("PROGRAMMING").build();
    Task task3 = new Task.Builder(3, "3").build();
    Task task4 = new Task.Builder(4, "4").type("LIVE").build();
    repo.save(task1);
    repo.save(task2);
    repo.save(task3);
    repo.save(task4);
    List<Task> list = repo.filterByType("LIVE");
    assertAll(
        () -> assertEquals(2, list.size()),
        () -> assertEquals("1", list.get(0).getTitle()),
        () -> assertEquals("4", list.get(1).getTitle()));
  }

  @Test
  @DisplayName("Filter task by type with empty list")
  void filterTaskByTypeEmptyList() {
    List<Task> list = repo.filterByType("LIVE");
    assertEquals(0, list.size());
  }

  @Test
  @DisplayName("Filter task by priority (only high priority tasks)")
  void filterHighPriorityTask() {
    Task task1 = new Task.Builder(1, "1").type("LIVE").priority("HIGH").build();
    Task task2 = new Task.Builder(2, "2").type("PROGRAMMING").priority("HIGH").build();
    Task task3 = new Task.Builder(3, "3").type("UNIVERSITY").priority("LOW").build();
    Task task4 = new Task.Builder(4, "4").type("LIVE").build();
    repo.save(task1);
    repo.save(task2);
    repo.save(task3);
    repo.save(task4);
    List<Task> list = repo.filterByPriority("HIGH");
    assertAll(
        () -> assertEquals(2, list.size()),
        () -> assertEquals("1", list.get(0).getTitle()),
        () -> assertEquals("2", list.get(1).getTitle()));
  }

  @Test
  @DisplayName("Filter task by priority with empty list")
  void filterWithEmptyList() {
    List<Task> list = repo.filterByPriority("HIGH");
    assertEquals(0, list.size());
  }

  @Test
  @DisplayName("Filter by status DONE")
  void filterByStatusTask() {
    Task task1 = new Task.Builder(1, "1").status("DONE").build();
    Task task2 = new Task.Builder(2, "2").type("PROGRAMMING").priority("HIGH").build();
    Task task3 = new Task.Builder(3, "3").status("TODO").priority("LOW").build();
    Task task4 = new Task.Builder(4, "4").status("DONE").type("LIVE").build();
    repo.save(task1);
    repo.save(task2);
    repo.save(task3);
    repo.save(task4);
    List<Task> list = repo.filterByStatus("DONE");
    assertAll(
        () -> assertEquals(2, list.size()),
        () -> assertEquals("1", list.get(0).getTitle()),
        () -> assertEquals("4", list.get(1).getTitle()));
  }

  @Test
  @DisplayName("Filter by status empty list")
  void emptyListFilterByStatus() {
    List<Task> list = repo.filterByStatus("DONE");
    assertEquals(0, list.size());
  }

  @Test
  @DisplayName("Complete a task")
  void completeTask() {
    Task task = new Task.Builder(1, "SI").status("TODO").build();
    task = task.updateStatus("DONE");
    assertEquals("DONE", task.getStatus());
  }

  @Test
  @DisplayName("Order by due date")
  void orderByDueDate() {
    Task task1 = new Task.Builder(1, "1").dueDate(LocalDate.now().plusDays(3)).build();
    Task task2 = new Task.Builder(2, "2").dueDate(LocalDate.now().plusDays(2)).build();
    Task task3 = new Task.Builder(3, "3").dueDate(LocalDate.now().plusDays(3)).build();
    Task task4 = new Task.Builder(4, "4").build(); // ocupa localdate.now y la due date ocupa plus 1
    repo.save(task1);
    repo.save(task2);
    repo.save(task3);
    repo.save(task4);
    List<Task> list = repo.orderByDueDate();
    assertAll(
        () -> assertEquals("4", list.get(0).getTitle()),
        () -> assertEquals("2", list.get(1).getTitle()),
        () -> assertEquals("1", list.get(2).getTitle()),
        () -> assertEquals("3", list.get(3).getTitle()));
  }

  @Test
  @DisplayName("Order by due date with empty list")
  void orderByDueDateEmptyList() {
    List<Task> list = repo.orderByDueDate();
    assertEquals(0, list.size());
  }

  @Test
  @DisplayName("Order by priority")
  void orderTaskByPriority() {
    Task task1 = new Task.Builder(1, "1").type("LIVE").priority("HIGH").build();
    Task task2 = new Task.Builder(2, "2").type("PROGRAMMING").priority("HIGH").build();
    Task task3 = new Task.Builder(3, "3").type("UNIVERSITY").priority("LOW").build();
    Task task4 = new Task.Builder(4, "4").type("LIVE").build();
    repo.save(task1);
    repo.save(task2);
    repo.save(task3);
    repo.save(task4);
    List<Task> list = repo.orderByPriority();
    assertAll(
        () -> assertEquals("1", list.get(0).getTitle()),
        () -> assertEquals("2", list.get(1).getTitle()),
        () -> assertEquals("3", list.get(2).getTitle()),
        () -> assertEquals(3, list.size()));
  }

  @Test
  @DisplayName("Order by priority empty")
  void orderByPriorityEmptyTaskList() {
    List<Task> list = repo.orderByPriority();
    assertEquals(0, list.size());
  }

  @Test
  @DisplayName("Search by id")
  void searchTaskById() {
    Task task1 = new Task.Builder(1, "1").type("LIVE").priority("HIGH").build();
    repo.save(task1);
    assertEquals(Optional.of(task1), repo.searchById(0));
  }

  @Test
  @DisplayName("Get all the complete tasks")
  void getAllCompleteTasks() {
    Task task1 = new Task.Builder(1, "1").type("LIVE").priority("HIGH").status("DONE").build();
    Task task2 =
        new Task.Builder(2, "2").type("PROGRAMMING").priority("HIGH").status("DONE").build();
    Task task3 = new Task.Builder(3, "3").type("UNIVERSITY").priority("LOW").status("TODO").build();
    Task task4 = new Task.Builder(4, "4").type("LIVE").build();
    repo.save(task1);
    repo.save(task2);
    repo.save(task3);
    repo.save(task4);
    List<Task> list = repo.getAllTaskComplete();
    assertAll(
        () -> assertEquals("1", list.get(0).getTitle()),
        () -> assertEquals("2", list.get(1).getTitle()),
        () -> assertEquals(2, list.size()));
  }

  @Test
  @DisplayName("Get all complete tasks with empty list")
  void allCompleteTaskWithEmptyList() {
    List<Task> list = repo.getAllTaskComplete();
    assertEquals(0, list.size());
  }

  @Test
  @DisplayName("Undone task")
  void undoneTask() {
    Task task =
        new Task.Builder(2, "2").type("PROGRAMMING").priority("HIGH").status("DONE").build();
    repo.save(task);
    task = repo.undoneTask(task);
    assertEquals("TODO", task.getStatus());
  }
}
