package com.tasktracker.app.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tasktracker.app.domain.Task;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

@DisplayName("Integration test for the database")
public class TaskDaoTest {

  static PostgreSQLContainer postgres =
      new PostgreSQLContainer("postgres:16")
          .withCopyFileToContainer(
              MountableFile.forClasspathResource("init-db.sql"), "/docker-entrypoint-initdb.d/");

  private TaskRepository repo;

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @BeforeEach
  void setUp() throws Exception {
    Connection conn =
        DriverManager.getConnection(
            postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    conn.createStatement().execute("DELETE FROM tasks");
    conn.createStatement()
        .execute(
            "INSERT INTO tasks (id, title, type, description, priority, status, date, due_date)"
                + " VALUES (1, 'Clean the house', 'LIVE', 'clean the house', 'HIGH', 'TODO',"
                + " '2026-03-12', '2026-03-13'), (2, 'Finish task tracker', 'PROGRAMMING', NULL,"
                + " 'LOW', 'TODO', '2026-03-12', '2026-03-15')");
    repo = new TaskDao(conn);
  }

  @Test
  @DisplayName("Test for getting all the tasks")
  void shouldGetAllTasks() {
    List<Task> list = repo.getAllTask();
    assertAll(
        () -> assertEquals(2, list.size()),
        () -> assertEquals("Clean the house", list.get(0).getTitle()),
        () -> assertEquals("Finish task tracker", list.get(1).getTitle()));
  }

  @Test
  @DisplayName("Test for save task")
  void saveTaskTest() {
    Task task = new Task.Builder(3, "Test").build();
    boolean result = repo.save(task);
    assertTrue(result);
  }

  @Test
  @DisplayName("Test for filter by the type (PROGRAMMING)")
  void filterByTypeProgramming() {
    List<Task> list = repo.filterByType("PROGRAMMING");
    assertAll(
        () -> assertEquals(1, list.size()),
        () -> assertEquals("Finish task tracker", list.get(0).getTitle()));
  }

  @Test
  @DisplayName("Test for filter by the priority (HIGH)")
  void filterByPriorityHigh() {
    List<Task> list = repo.filterByPriority("HIGH");
    assertAll(
        () -> assertEquals(1, list.size()),
        () -> assertEquals("Clean the house", list.get(0).getTitle()));
  }

  @Test
  @DisplayName("Test for filter by the status (TODO)")
  void filterByStatusTodo() {
    List<Task> list = repo.filterByStatus("TODO");
    assertAll(
        () -> assertEquals(2, list.size()),
        () -> assertEquals("Clean the house", list.get(0).getTitle()),
        () -> assertEquals("Finish task tracker", list.get(1).getTitle()));
  }

  @Test
  @DisplayName("Search a task with the id")
  void searchTaskById() {
    Task task = repo.searchById(1).get();
    assertEquals("Clean the house", task.getTitle());
  }

  @Test
  @DisplayName("Update the status to complete test")
  void updateStatusToCompleteTest() {
    Task task = repo.searchById(1).get();
    boolean result = repo.completeTask(task);
    assertTrue(result);
  }

  @Test
  @DisplayName("Order by due date")
  void orderByDueDate() {
    List<Task> list = repo.orderByDueDate();
    assertAll(
        () -> assertEquals("Clean the house", list.get(0).getTitle()),
        () -> assertEquals("Finish task tracker", list.get(1).getTitle()));
  }

  @Test
  @DisplayName("Order by priority")
  void orderByPriority() {
    List<Task> list = repo.orderByPriority();
    assertAll(
        () -> assertEquals("Clean the house", list.get(0).getTitle()),
        () -> assertEquals("Finish task tracker", list.get(1).getTitle()));
  }

  @Test
  @DisplayName("Get all task complete")
  void getAllTaskComplete() {
    Task task = repo.searchById(1).get();
    boolean result = repo.completeTask(task);
    assertTrue(result);

    List<Task> list = repo.getAllTaskComplete();
    assertAll(
        () -> assertEquals(1, list.size()),
        () -> assertEquals("Clean the house", list.get(0).getTitle()));
  }

  @Test
  @DisplayName("Undone task")
  void undoneTask() {
    repo.completeTask(repo.searchById(1).get());

    Task task = repo.searchById(1).get();
    boolean result = repo.undoneTask(task);
    assertTrue(result);
  }

  @Test
  @DisplayName("Delete task")
  void deleteTask() {
    Task task = repo.searchById(1).get();
    boolean result = repo.deleteTask(task);
    assertTrue(result);
  }
}
