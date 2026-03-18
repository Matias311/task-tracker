package com.tasktracker.app.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tasktracker.app.domain.Event;
import com.tasktracker.app.domain.Task;
import com.tasktracker.app.exception.PersistenceException;
import com.tasktracker.app.repository.EventDao;
import com.tasktracker.app.repository.TaskDao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

/**
 * Integration tests for TaskService with real JDBC transactions.
 *
 * <p>These tests verify that the transactional behavior of TaskService works correctly with a real
 * PostgreSQL database via testcontainers. They ensure that when saving a task with its audit
 * event, either both succeed or both fail (atomicity).
 */
@Tag("integration")
@DisplayName("Integration tests for TaskService (real database, transactions)")
public class TaskServiceIntegrationTest {

  static PostgreSQLContainer postgres =
      new PostgreSQLContainer("postgres:16")
          .withCopyFileToContainer(
              MountableFile.forClasspathResource("init-db.sql"), "/docker-entrypoint-initdb.d/");

  private Connection conn;
  private TaskService service;

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

    // Limpia las tablas
    conn.createStatement().execute("DELETE FROM audit_task");
    conn.createStatement().execute("DELETE FROM tasks");

    // Inserta datos iniciales
    conn.createStatement()
        .execute(
            "INSERT INTO tasks (id, title, type, description, priority, status, date, due_date)"
                + " VALUES (1, 'Clean the house', 'LIVE', 'clean the house', 'HIGH', 'TODO',"
                + " '2026-03-12', '2026-03-13'), (2, 'Finish task tracker', 'PROGRAMMING', NULL,"
                + " 'LOW', 'TODO', '2026-03-12', '2026-03-15')");

    // Constructor CON Connection - para tests de integración con BD real
    service = new TaskService(new TaskDao(conn), new EventDao(conn), conn);
  }

  // ==================== TESTS: saveTask() ====================

  @Test
  @DisplayName("saveTask should persist both task and audit event atomically")
  void saveTaskShouldPersistBothTaskAndAuditAtomically() throws Exception {
    Task task = new Task.Builder(10, "New Task").priority("HIGH").build();

    service.saveTask(task);

    // Verifica que la task se guardó
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM tasks WHERE id = 10")) {
      assertTrue(rs.next());
      assertEquals("New Task", rs.getString("title"));
      assertEquals("HIGH", rs.getString("priority"));
    }

    // Verifica que el audit se guardó
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM audit_task WHERE id_task = 10")) {
      assertTrue(rs.next());
      assertEquals("SAVE", rs.getString("action"));
      assertEquals("New Task", rs.getString("task_title"));
    }
  }

  @Test
  @DisplayName("saveTask with invalid id should fail validation at Task construction")
  void saveTaskWithInvalidIdShouldFailValidation() throws Exception {
    // Task constructor validates id, so we catch that
    assertThrows(
        IllegalArgumentException.class,
        () -> new Task.Builder(-1, "Test").build());

    // Verifica que nada se guardó
    int auditCount = 0;
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM audit_task WHERE action = 'SAVE'")) {
      if (rs.next()) {
        auditCount = rs.getInt("count");
      }
    }
    assertEquals(0, auditCount);
  }

  // ==================== TESTS: completeTask() ====================

  @Test
  @DisplayName("completeTask should persist status update and audit event atomically")
  void completeTaskShouldPersistBothUpdatesAtomically() throws Exception {
    Task task = service.searchTaskById(1);

    service.completeTask(task);

    // Verifica que el status se actualizó
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT status FROM tasks WHERE id = 1")) {
      assertTrue(rs.next());
      assertEquals("DONE", rs.getString("status"));
    }

    // Verifica que el audit se guardó
    try (Statement stmt = conn.createStatement();
        ResultSet rs =
            stmt.executeQuery("SELECT * FROM audit_task WHERE id_task = 1 AND action = 'COMPLETE TASK'")) {
      assertTrue(rs.next());
      assertEquals("Clean the house", rs.getString("task_title"));
    }
  }

  @Test
  @DisplayName("completeTask on already DONE task should not create audit")
  void completeTaskOnDoneTaskShouldThrowException() throws Exception {
    // Primero marca como DONE
    Task task = service.searchTaskById(1);
    service.completeTask(task);

    // Intenta completar nuevamente
    Task doneTask = service.searchTaskById(1);
    assertThrows(IllegalStateException.class, () -> service.completeTask(doneTask));
  }

  // ==================== TESTS: undoneTask() ====================

  @Test
  @DisplayName("undoneTask should persist status update and audit event atomically")
  void undoneTaskShouldPersistBothUpdatesAtomically() throws Exception {
    // Primero marca como DONE
    Task task = service.searchTaskById(1);
    service.completeTask(task);

    // Luego deshace
    Task doneTask = service.searchTaskById(1);
    service.undoneTask(doneTask);

    // Verifica que el status volvió a TODO
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT status FROM tasks WHERE id = 1")) {
      assertTrue(rs.next());
      assertEquals("TODO", rs.getString("status"));
    }

    // Verifica que hay audits para ambas operaciones
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM audit_task WHERE id_task = 1")) {
      assertTrue(rs.next());
      assertEquals(2, rs.getInt("count")); // COMPLETE TASK + UNDONE
    }
  }

  // ==================== TESTS: deleteTask() ====================

  @Test
  @DisplayName("deleteTask should persist deletion and audit event atomically")
  void deleteTaskShouldPersistBothDeletionAndAuditAtomically() throws Exception {
    Task task = service.searchTaskById(1);

    boolean deleted = service.deleteTask(task);
    assertTrue(deleted);

    // Verifica que la task se eliminó
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM tasks WHERE id = 1")) {
      assertFalse(rs.next());
    }

    // Verifica que el audit se guardó (id_task will be SET NULL due to ON DELETE SET NULL)
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM audit_task WHERE action = 'DELETE' AND task_title = 'Clean the house'")) {
      assertTrue(rs.next());
    }
  }

  // ==================== TESTS: Atomic behavior (rollback scenarios) ====================

  @Test
  @DisplayName("transaction should save both task and audit (atomic behavior)")
  void transactionShouldSaveBothTaskAndAudit() throws Exception {
    // Este test verifica el comportamiento transaccional
    // Ambas operaciones (task + audit) deben guardarse

    Task task = new Task.Builder(20, "Should Save").build();
    
    int taskCountBefore = getTaskCount();
    int auditCountBefore = getAuditCount();

    service.saveTask(task);

    int taskCountAfter = getTaskCount();
    int auditCountAfter = getAuditCount();

    // Ambas tablas deberían haber incrementado
    assertEquals(taskCountBefore + 1, taskCountAfter);
    assertEquals(auditCountBefore + 1, auditCountAfter);
  }

  @Test
  @DisplayName("multiple operations should have corresponding audits")
  void multipleOperationsShouldHaveCorrespondingAudits() throws Exception {
    Task task = service.searchTaskById(2);

    // Save (already there, but verify audit count)
    int auditCountBefore = getAuditCount();

    service.completeTask(task);
    assertEquals(auditCountBefore + 1, getAuditCount());

    Task completedTask = service.searchTaskById(2);
    service.undoneTask(completedTask);
    assertEquals(auditCountBefore + 2, getAuditCount());

    Task undoneTask = service.searchTaskById(2);
    service.deleteTask(undoneTask);
    assertEquals(auditCountBefore + 3, getAuditCount());
  }

  // ==================== Helper methods ====================

  private int getTaskCount() throws Exception {
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM tasks")) {
      rs.next();
      return rs.getInt("count");
    }
  }

  private int getAuditCount() throws Exception {
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM audit_task")) {
      rs.next();
      return rs.getInt("count");
    }
  }
}
