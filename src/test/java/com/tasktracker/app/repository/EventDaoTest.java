package com.tasktracker.app.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tasktracker.app.domain.Event;
import com.tasktracker.app.exception.PersistenceException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

@DisplayName("Integration tests for EventDao persistence layer")
public class EventDaoTest {

  static PostgreSQLContainer postgres =
      new PostgreSQLContainer("postgres:16")
          .withCopyFileToContainer(
              MountableFile.forClasspathResource("init-db.sql"), "/docker-entrypoint-initdb.d/");

  private EventRepository repo;
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

    // Clean up before each test
    conn.createStatement().execute("DELETE FROM audit_task");
    conn.createStatement().execute("DELETE FROM tasks");

    // Insert test data
    conn.createStatement()
        .execute(
            "INSERT INTO tasks (id, title, type, description, priority, status, date, due_date)"
                + " VALUES (1, 'Clean the house', 'LIVE', 'clean the house', 'HIGH', 'TODO',"
                + " '2026-03-12', '2026-03-13'),(2, 'Finish task tracker', 'PROGRAMMING', NULL,"
                + " 'LOW', 'TODO', '2026-03-12', '2026-03-15')");

    repo = new EventDao(conn);
  }

  @AfterEach
  void tearDown() throws Exception {
    conn.close();
  }

  @Test
  @DisplayName("Save a single event successfully")
  void saveEventSuccessfully() {
    Event event = new Event("SAVE", 1, "Clean the house");

    boolean result = repo.saveEvent(event);

    assertTrue(result, "Event should be saved successfully");
  }

  @Test
  @DisplayName("Verify saved event exists in database with correct fields")
  void savedEventExistsInDatabaseWithCorrectData() throws SQLException {
    Event event = new Event("SAVE", 1, "Clean the house");
    repo.saveEvent(event);

    // Query the database to verify the event was saved
    ResultSet rs =
        conn.createStatement()
            .executeQuery("SELECT * FROM audit_task WHERE id_task = 1 AND action = 'SAVE'");

    assertTrue(rs.next(), "Event should exist in database");

    assertAll(
        () -> assertEquals(1, rs.getInt("id_task"), "Task ID should be 1"),
        () -> assertEquals("SAVE", rs.getString("action"), "Action should be SAVE"),
        () ->
            assertEquals("Clean the house", rs.getString("task_title"), "Task title should match"),
        () ->
            assertNotNull(rs.getTimestamp("execution_date"), "Execution date should not be null"));
  }

  @Test
  @DisplayName("Save multiple events for the same task")
  void saveMultipleEventsForSameTask() throws SQLException {
    Event event1 = new Event("SAVE", 1, "Clean the house");
    Event event2 = new Event("COMPLETE", 1, "Clean the house");
    Event event3 = new Event("DELETE", 1, "Clean the house");

    repo.saveEvent(event1);
    repo.saveEvent(event2);
    repo.saveEvent(event3);

    ResultSet rs =
        conn.createStatement()
            .executeQuery("SELECT COUNT(*) as count FROM audit_task WHERE id_task = 1");
    rs.next();

    assertEquals(3, rs.getInt("count"), "Should have 3 events for task 1");
  }

  @Test
  @DisplayName("Save events for different tasks")
  void saveEventsForDifferentTasks() throws SQLException {
    Event event1 = new Event("SAVE", 1, "Clean the house");
    Event event2 = new Event("SAVE", 2, "Finish task tracker");

    repo.saveEvent(event1);
    repo.saveEvent(event2);

    ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) as count FROM audit_task");
    rs.next();

    assertEquals(2, rs.getInt("count"), "Should have 2 total events");
  }

  @Test
  @DisplayName("Event execution date is set automatically")
  void eventExecutionDateIsSetAutomatically() throws SQLException {
    LocalDateTime before = LocalDateTime.now();
    Event event = new Event("SAVE", 1, "Clean the house");
    LocalDateTime after = LocalDateTime.now();

    repo.saveEvent(event);

    ResultSet rs =
        conn.createStatement()
            .executeQuery("SELECT execution_date FROM audit_task WHERE id_task = 1");
    rs.next();
    LocalDateTime savedDate = rs.getTimestamp("execution_date").toLocalDateTime();

    assertTrue(
        (savedDate.isAfter(before) || savedDate.isEqual(before))
            && (savedDate.isBefore(after) || savedDate.isEqual(after)),
        "Saved date should be between before and after timestamps");
  }

  @Test
  @DisplayName("Save event with different action types")
  void saveEventWithDifferentActionTypes() throws SQLException {
    String[] actions = {"SAVE", "COMPLETE", "DELETE", "UNDONE", "UPDATE"};

    for (String action : actions) {
      Event event = new Event(action, 1, "Clean the house");
      boolean result = repo.saveEvent(event);
      assertTrue(result, "Should save event with action: " + action);
    }

    ResultSet rs =
        conn.createStatement()
            .executeQuery("SELECT COUNT(*) as count FROM audit_task WHERE id_task = 1");
    rs.next();
    assertEquals(5, rs.getInt("count"), "Should have 5 events for different actions");
  }

  @Test
  @DisplayName("Save event with task ID that exists in tasks table")
  void saveEventWithExistingTaskId() throws SQLException {
    Event event = new Event("SAVE", 2, "Finish task tracker");

    boolean result = repo.saveEvent(event);

    assertTrue(result, "Should save event with existing task ID");

    ResultSet rs =
        conn.createStatement().executeQuery("SELECT * FROM audit_task WHERE id_task = 2");
    assertTrue(rs.next(), "Event should be in database");
  }

  @Test
  @DisplayName("Save event with long title")
  void saveEventWithLongTitle() throws SQLException {
    String longTitle = "A".repeat(500); // Very long title
    Event event = new Event("SAVE", 1, longTitle);

    boolean result = repo.saveEvent(event);

    assertTrue(result, "Should save event with long title");

    ResultSet rs =
        conn.createStatement().executeQuery("SELECT task_title FROM audit_task WHERE id_task = 1");
    rs.next();
    assertEquals(longTitle, rs.getString("task_title"), "Long title should be saved correctly");
  }

  @Test
  @DisplayName("Save event with special characters in title")
  void saveEventWithSpecialCharactersInTitle() throws SQLException {
    String specialTitle = "Task with @#$%^&*() and 'quotes' and \"double quotes\"";
    Event event = new Event("SAVE", 1, specialTitle);

    boolean result = repo.saveEvent(event);

    assertTrue(result, "Should save event with special characters");

    ResultSet rs =
        conn.createStatement().executeQuery("SELECT task_title FROM audit_task WHERE id_task = 1");
    rs.next();
    assertEquals(
        specialTitle, rs.getString("task_title"), "Special characters should be preserved");
  }

  @Test
  @DisplayName("Save null event throws PersistenceException")
  void saveNullEventThrowsException() {
    assertThrows(
        PersistenceException.class,
        () -> repo.saveEvent(null),
        "Should throw PersistenceException when event is null");
  }

  @Test
  @DisplayName("Event with null action throws exception during creation")
  void eventWithNullActionThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Event(null, 1, "Clean the house"),
        "Should throw IllegalArgumentException for null action");
  }

  @Test
  @DisplayName("Event with null title throws exception during creation")
  void eventWithNullTitleThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Event("SAVE", 1, null),
        "Should throw IllegalArgumentException for null title");
  }

  @Test
  @DisplayName("Event with invalid task ID throws exception during creation")
  void eventWithInvalidTaskIdThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Event("SAVE", -1, "Clean the house"),
        "Should throw IllegalArgumentException for negative task ID");
  }

  @Test
  @DisplayName("Event with zero task ID throws exception during creation")
  void eventWithZeroTaskIdThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Event("SAVE", 0, "Clean the house"),
        "Should throw IllegalArgumentException for zero task ID");
  }

  @Test
  @DisplayName("Multiple saves for same event action on same task")
  void multipleSavesForSameActionOnSameTask() throws SQLException {
    Event event1 = new Event("COMPLETE", 1, "Clean the house");
    Event event2 = new Event("COMPLETE", 1, "Clean the house");

    repo.saveEvent(event1);
    repo.saveEvent(event2);

    ResultSet rs =
        conn.createStatement()
            .executeQuery(
                "SELECT COUNT(*) as count FROM audit_task WHERE id_task = 1 AND action ="
                    + " 'COMPLETE'");
    rs.next();

    assertEquals(2, rs.getInt("count"), "Should have 2 COMPLETE events (duplicates allowed)");
  }

  @Test
  @DisplayName("Verify database constraints: event sequence increases")
  void eventIdSequenceIncreases() throws SQLException {
    Event event1 = new Event("SAVE", 1, "Clean the house");
    Event event2 = new Event("COMPLETE", 1, "Clean the house");

    repo.saveEvent(event1);
    repo.saveEvent(event2);

    ResultSet rs = conn.createStatement().executeQuery("SELECT id FROM audit_task ORDER BY id ASC");

    assertTrue(rs.next(), "Should have first event");
    int firstId = rs.getInt("id");

    assertTrue(rs.next(), "Should have second event");
    int secondId = rs.getInt("id");

    assertTrue(secondId > firstId, "Event IDs should be in increasing order");
  }

  @Test
  @DisplayName("Save event returns true on successful insert")
  void saveEventReturnsTrueOnSuccess() {
    Event event = new Event("SAVE", 1, "Clean the house");
    boolean result = repo.saveEvent(event);

    assertTrue(result, "saveEvent should return true on successful insert");
  }

  @Test
  @DisplayName(
      "Save event with non-existent task ID throws PersistenceException due to FK constraint")
  void saveEventWithNonExistentTaskIdThrowsException() {
    Event event = new Event("SAVE", 999, "Non-existent task");

    assertThrows(
        PersistenceException.class,
        () -> repo.saveEvent(event),
        "Should throw PersistenceException when task ID violates FK constraint");
  }
}
