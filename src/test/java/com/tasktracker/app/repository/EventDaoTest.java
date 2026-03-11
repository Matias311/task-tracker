package com.tasktracker.app.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tasktracker.app.domain.Event;
import java.sql.Connection;
import java.sql.DriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

@DisplayName("Test for the event percistence")
public class EventDaoTest {

  static PostgreSQLContainer postgres =
      new PostgreSQLContainer("postgres:16")
          .withCopyFileToContainer(
              MountableFile.forClasspathResource("init-db.sql"), "/docker-entrypoint-initdb.d/");

  private EventRepository repo;

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
    repo = new EventDao(conn);
  }

  @Test
  @DisplayName("Save a event test")
  void saveEvent() {
    boolean result = repo.saveEvent(new Event("SAVE", 1, "Clean the house"));
    assertTrue(result);
  }
}
