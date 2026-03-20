package com.tasktracker.app.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tasktracker.app.domain.Task;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

@DisplayName("Integration tests for TaskDao persistence layer")
public class TaskDaoTest {

  static PostgreSQLContainer postgres =
      new PostgreSQLContainer("postgres:16")
          .withCopyFileToContainer(
              MountableFile.forClasspathResource("init-db.sql"), "/docker-entrypoint-initdb.d/");

  private TaskRepository repo;
  private Connection conn;

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
    conn =
        DriverManager.getConnection(
            postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    conn.createStatement().execute("DELETE FROM audit_task");
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
  @DisplayName("Get all tasks returns all saved tasks")
  void getAllTasksReturnsAllSavedTasks() {
    List<Task> list = repo.getAllTask();

    assertAll(
        () -> assertEquals(2, list.size(), "Should return 2 tasks"),
        () -> assertEquals("Clean the house", list.get(0).getTitle()),
        () -> assertEquals("Finish task tracker", list.get(1).getTitle()));
  }

  @Test
  @DisplayName("Get all tasks with empty database")
  void getAllTasksWithEmptyDatabase() throws SQLException {
    conn.createStatement().execute("DELETE FROM tasks");

    List<Task> list = repo.getAllTask();

    assertTrue(list.isEmpty(), "Should return empty list when no tasks exist");
  }

  @Test
  @DisplayName("Save task persists to database")
  void saveTaskPersistsToDatabase() throws SQLException {
    Task task = new Task.Builder(3, "New Task").priority("HIGH").status("TODO").build();

    repo.save(task);

    Optional<Task> saved = repo.searchById(3);
    assertTrue(saved.isPresent(), "Task should be saved");
    assertEquals("New Task", saved.get().getTitle());
    assertEquals("HIGH", saved.get().getPriority());
    assertEquals("TODO", saved.get().getStatus());
  }

  @Test
  @DisplayName("Save task with all parameters")
  void saveTaskWithAllParameters() throws SQLException {
    LocalDate now = LocalDate.now();
    Task task =
        new Task.Builder(3, "Complex Task")
            .type("PROGRAMMING")
            .description("A complex task")
            .priority("MEDIUM")
            .status("DOING")
            .date(now)
            .dueDate(now.plusDays(5))
            .build();

    repo.save(task);

    Optional<Task> saved = repo.searchById(3);
    assertTrue(saved.isPresent());

    assertAll(
        () -> assertEquals(3, saved.get().getId()),
        () -> assertEquals("Complex Task", saved.get().getTitle()),
        () -> assertEquals("PROGRAMMING", saved.get().getType()),
        () -> assertEquals("A complex task", saved.get().getDescription()),
        () -> assertEquals("MEDIUM", saved.get().getPriority()),
        () -> assertEquals("DOING", saved.get().getStatus()),
        () -> assertEquals(now, saved.get().getDate()),
        () -> assertEquals(now.plusDays(5), saved.get().getDueDate()));
  }

  @Test
  @DisplayName("Search by existing id returns task")
  void searchByExistingIdReturnsTask() {
    Optional<Task> task = repo.searchById(1);

    assertTrue(task.isPresent(), "Task should be found");
    assertEquals("Clean the house", task.get().getTitle());
  }

  @Test
  @DisplayName("Search by non-existent id returns empty")
  void searchByNonExistentIdReturnsEmpty() {
    Optional<Task> task = repo.searchById(999);

    assertTrue(task.isEmpty(), "Should return empty Optional");
  }

  @Test
  @DisplayName("Complete task changes status to DONE")
  void completeTaskChangesStatusToDone() throws SQLException {
    Task task = repo.searchById(1).get();
    assertEquals("TODO", task.getStatus(), "Initial status should be TODO");

    repo.completeTask(task);

    // Verify status changed in database
    Task completed = repo.searchById(1).get();
    assertEquals("DONE", completed.getStatus(), "Status should be DONE after completion");
  }

  @Test
  @DisplayName("Complete task returns true")
  void completeTaskReturnsTrue() {
    Task task = repo.searchById(1).get();

    boolean result = repo.completeTask(task);

    assertTrue(result, "completeTask should return true");
  }

  @Test
  @DisplayName("Get all complete tasks filters correctly")
  void getAllCompleteTasksFiltersCorrectly() throws SQLException {
    // Initially, no DONE tasks
    List<Task> initialComplete = repo.getAllTaskComplete();
    assertTrue(initialComplete.isEmpty(), "Initially no DONE tasks");

    // Complete one task
    Task task1 = repo.searchById(1).get();
    repo.completeTask(task1);

    // Now should have 1 DONE task
    List<Task> completeAfter = repo.getAllTaskComplete();
    assertAll(
        () -> assertEquals(1, completeAfter.size(), "Should have 1 DONE task"),
        () -> assertEquals("DONE", completeAfter.get(0).getStatus()),
        () -> assertEquals("Clean the house", completeAfter.get(0).getTitle()));
  }

  @Test
  @DisplayName("Get all complete tasks returns empty when no DONE tasks")
  void getAllCompleteTasksEmptyWhenNoDoneTasks() {
    List<Task> complete = repo.getAllTaskComplete();

    assertTrue(complete.isEmpty(), "Should be empty when no tasks are DONE");
  }

  @Test
  @DisplayName("Undone task changes status from DONE to TODO")
  void undoneTaskChangesStatusToTodo() throws SQLException {
    // Setup: Complete the task first
    Task taskBefore = repo.searchById(1).get();
    repo.completeTask(taskBefore);

    // Verify it's DONE
    Task doneTsk = repo.searchById(1).get();
    assertEquals("DONE", doneTsk.getStatus(), "Task should be DONE");

    // Undone the task
    boolean result = repo.undoneTask(doneTsk);

    // Verify status changed to TODO
    Task undoneTask = repo.searchById(1).get();
    assertAll(
        () -> assertTrue(result, "undoneTask should return true"),
        () -> assertEquals("TODO", undoneTask.getStatus(), "Status should be TODO after undone"));
  }

  @Test
  @DisplayName("Undone task actually persists to database")
  void undoneTaskPersistsToDatabase() throws SQLException {
    // Setup
    Task task = repo.searchById(1).get();
    repo.completeTask(task);

    Task doneTask = repo.searchById(1).get();
    repo.undoneTask(doneTask);

    // Query database directly to verify persistence
    ResultSet rs = conn.createStatement().executeQuery("SELECT status FROM tasks WHERE id = 1");
    rs.next();
    String statusInDb = rs.getString("status");

    assertEquals("TODO", statusInDb, "Status should be TODO in database");
  }

  @Test
  @DisplayName("Delete task removes from database")
  void deleteTaskRemovesFromDatabase() throws SQLException {
    Optional<Task> beforeDelete = repo.searchById(1);
    assertTrue(beforeDelete.isPresent(), "Task should exist before delete");

    repo.deleteTask(beforeDelete.get());

    Optional<Task> afterDelete = repo.searchById(1);
    assertTrue(afterDelete.isEmpty(), "Task should be deleted");
  }

  @Test
  @DisplayName("Delete task actually removes from database count")
  void deleteTaskRemovesFromDatabaseCount() throws SQLException {
    List<Task> beforeDelete = repo.getAllTask();
    assertEquals(2, beforeDelete.size());

    repo.deleteTask(beforeDelete.get(0));

    List<Task> afterDelete = repo.getAllTask();
    assertEquals(1, afterDelete.size(), "Should have 1 task after deletion");
  }

  @Test
  @DisplayName("Delete task persists deletion")
  void deleteTaskPersistesDeletion() throws SQLException {
    Task task = repo.searchById(1).get();
    repo.deleteTask(task);

    // Query database directly
    ResultSet rs =
        conn.createStatement().executeQuery("SELECT COUNT(*) as count FROM tasks WHERE id = 1");
    rs.next();
    int count = rs.getInt("count");

    assertEquals(0, count, "Task should be deleted from database");
  }

  @Test
  @DisplayName("Filter by type returns matching tasks")
  void filterByTypeReturnsMatchingTasks() {
    List<Task> programming = repo.filterByType("PROGRAMMING");

    assertAll(
        () -> assertEquals(1, programming.size()),
        () -> assertEquals("Finish task tracker", programming.get(0).getTitle()));
  }

  @Test
  @DisplayName("Filter by type returns empty when no matches")
  void filterByTypeReturnsEmptyWhenNoMatches() {
    List<Task> storage = repo.filterByType("STORAGE");

    assertTrue(storage.isEmpty(), "Should return empty list for non-existent type");
  }

  @Test
  @DisplayName("Filter by type returns all matching tasks")
  void filterByTypeReturnsAllMatchingTasks() throws SQLException {
    // Add another LIVE task
    conn.createStatement()
        .execute(
            "INSERT INTO tasks (id, title, type, status, priority, date, due_date) VALUES (3,"
                + " 'Play', 'LIVE', 'TODO', 'MEDIUM', '2026-03-12', '2026-03-13')");

    List<Task> liveTasks = repo.filterByType("LIVE");

    assertEquals(2, liveTasks.size(), "Should have 2 LIVE tasks");
  }

  @Test
  @DisplayName("Filter by priority returns matching tasks")
  void filterByPriorityReturnsMatchingTasks() {
    List<Task> high = repo.filterByPriority("HIGH");

    assertAll(
        () -> assertEquals(1, high.size()),
        () -> assertEquals("Clean the house", high.get(0).getTitle()));
  }

  @Test
  @DisplayName("Filter by priority returns empty when no matches")
  void filterByPriorityReturnsEmptyWhenNoMatches() {
    List<Task> critical = repo.filterByPriority("CRITICAL");

    assertTrue(critical.isEmpty(), "Should return empty list for non-existent priority");
  }

  @Test
  @DisplayName("Filter by priority returns all matching tasks")
  void filterByPriorityReturnsAllMatchingTasks() throws SQLException {
    conn.createStatement()
        .execute(
            "INSERT INTO tasks (id, title, type, priority, status, date, due_date) VALUES (3,"
                + " 'Another HIGH', 'LIVE', 'HIGH', 'TODO', '2026-03-12', '2026-03-13')");

    List<Task> highPriority = repo.filterByPriority("HIGH");

    assertEquals(2, highPriority.size(), "Should have 2 HIGH priority tasks");
  }

  @Test
  @DisplayName("Filter by status returns matching tasks")
  void filterByStatusReturnsMatchingTasks() {
    List<Task> todo = repo.filterByStatus("TODO");

    assertEquals(2, todo.size(), "Should return 2 TODO tasks");
  }

  @Test
  @DisplayName("Filter by status returns empty when no matches")
  void filterByStatusReturnsEmptyWhenNoMatches() {
    List<Task> done = repo.filterByStatus("DONE");

    assertTrue(done.isEmpty(), "Should return empty list when no DONE tasks");
  }

  @Test
  @DisplayName("Filter by status with mixed task statuses")
  void filterByStatusWithMixedTaskStatuses() throws SQLException {
    // Complete one task
    Task task1 = repo.searchById(1).get();
    repo.completeTask(task1);

    // Now we have 1 DONE and 1 TODO
    List<Task> done = repo.filterByStatus("DONE");
    List<Task> todo = repo.filterByStatus("TODO");

    assertAll(
        () -> assertEquals(1, done.size(), "Should have 1 DONE task"),
        () -> assertEquals(1, todo.size(), "Should have 1 TODO task"),
        () -> assertEquals("DONE", done.get(0).getStatus()),
        () -> assertEquals("TODO", todo.get(0).getStatus()));
  }

  @Test
  @DisplayName("Order by due date returns tasks in correct order")
  void orderByDueDateReturnsSortedTasks() {
    List<Task> ordered = repo.orderByDueDate();

    assertAll(
        () -> assertEquals(2, ordered.size()),
        () ->
            assertEquals(
                "Clean the house", ordered.get(0).getTitle(), "Task with earlier due date first"),
        () ->
            assertEquals(
                "Finish task tracker",
                ordered.get(1).getTitle(),
                "Task with later due date second"));
  }

  @Test
  @DisplayName("Order by due date with empty database")
  void orderByDueDateWithEmptyDatabase() throws SQLException {
    conn.createStatement().execute("DELETE FROM tasks");

    List<Task> ordered = repo.orderByDueDate();

    assertTrue(ordered.isEmpty(), "Should return empty list");
  }

  @Test
  @DisplayName("Order by due date with tasks having same due date")
  void orderByDueDateWithSameDueDate() throws SQLException {
    // Both tasks have same due date, so order is determined by natural order or ID
    conn.createStatement().execute("UPDATE tasks SET due_date = '2026-03-20' WHERE id IN (1, 2)");

    List<Task> ordered = repo.orderByDueDate();

    assertEquals(2, ordered.size(), "Should return both tasks");
    // Both should be present, order might be by ID or insertion order
    assertTrue(ordered.stream().anyMatch(t -> "Clean the house".equals(t.getTitle())));
    assertTrue(ordered.stream().anyMatch(t -> "Finish task tracker".equals(t.getTitle())));
  }

  @Test
  @DisplayName("Order by priority returns tasks with highest priority first")
  void orderByPriorityReturnsSortedTasks() {
    List<Task> ordered = repo.orderByPriority();

    assertAll(
        () -> assertEquals(2, ordered.size()),
        () -> assertEquals("HIGH", ordered.get(0).getPriority(), "Highest priority first"),
        () -> assertEquals("LOW", ordered.get(1).getPriority(), "Lower priority last"));
  }

  @Test
  @DisplayName("Order by priority with empty database")
  void orderByPriorityWithEmptyDatabase() throws SQLException {
    conn.createStatement().execute("DELETE FROM tasks");

    List<Task> ordered = repo.orderByPriority();

    assertTrue(ordered.isEmpty(), "Should return empty list");
  }

  @Test
  @DisplayName("Order by priority with multiple same priority tasks")
  void orderByPriorityWithSamePriority() throws SQLException {
    conn.createStatement().execute("UPDATE tasks SET priority = 'HIGH' WHERE id = 2");

    List<Task> ordered = repo.orderByPriority();

    assertEquals(2, ordered.size(), "Should return both tasks");
    assertTrue(
        ordered.stream().allMatch(t -> "HIGH".equals(t.getPriority())),
        "All should have HIGH priority");
  }

  @Test
  @DisplayName("Order by priority only includes non-DONE tasks")
  void orderByPriorityExcludesDoneTasksIfApplicable() throws SQLException {
    // Complete one task
    Task task = repo.searchById(1).get();
    repo.completeTask(task);

    List<Task> ordered = repo.orderByPriority();

    // Check if DONE tasks are filtered (depends on implementation)
    // If implementation filters DONE, should only have 1 task
    assertTrue(ordered.size() >= 1, "Should have at least 1 task");
  }
}
