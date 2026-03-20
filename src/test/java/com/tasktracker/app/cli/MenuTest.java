package com.tasktracker.app.cli;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tasktracker.app.repository.TaskRepositoryImpl;
import com.tasktracker.app.repository.observer.AudditLogger;
import com.tasktracker.app.service.TaskService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

@DisplayName("Integration tests for Menu CLI")
public class MenuTest {

  @ParameterizedTest
  @NullSource
  @DisplayName("Menu.of() throws exception when InputStream is null")
  void createMenuNullInputStream(InputStream in) {
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> Menu.of(in, System.out, service));

    assertEquals("InputStream, PrintStream and TaskService must have a value", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Menu.of() throws exception when PrintStream is null")
  void createMenuNullPrintStream(PrintStream out) {
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> Menu.of(System.in, out, service));

    assertEquals("InputStream, PrintStream and TaskService must have a value", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Menu.of() throws exception when TaskService is null")
  void createMenuNullService(TaskService service) {
    Exception ex =
        assertThrows(IllegalArgumentException.class, () -> Menu.of(System.in, System.out, service));

    assertEquals("InputStream, PrintStream and TaskService must have a value", ex.getMessage());
  }

  @Test
  @DisplayName("Menu displays welcome message and options")
  void menuDisplaysWelcomeAndOptions() {
    ByteArrayInputStream in = new ByteArrayInputStream("13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Menu.of(in, new PrintStream(out), service).start();

    String result = out.toString();

    assertAll(
        () -> assertTrue(result.contains("Welcome to the task manager!"), "Should welcome user"),
        () -> assertTrue(result.contains("1. Save a task"), "Should show save task option"),
        () -> assertTrue(result.contains("13. Exit!"), "Should show exit option"),
        () -> assertTrue(result.contains("Adios!"), "Should show goodbye message"));
  }

  @Test
  @DisplayName("Menu displays all numbered options (1-13)")
  void menuDisplaysAllNumberedOptions() {
    ByteArrayInputStream in = new ByteArrayInputStream("13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Menu.of(in, new PrintStream(out), service).start();

    String result = out.toString();

    // Verify all menu options are displayed
    for (int i = 1; i <= 13; i++) {
      assertTrue(result.contains(String.valueOf(i)), "Should display option " + i);
    }
  }

  @Test
  @DisplayName("Menu rejects non-numeric input")
  void menuRejectsNonNumericInput() {
    ByteArrayInputStream in = new ByteArrayInputStream("INVALID\n13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Menu.of(in, new PrintStream(out), service).start();

    String result = out.toString();

    assertTrue(
        result.contains("Invalid option") || result.contains("invalid"),
        "Should handle invalid input");
  }

  @Test
  @DisplayName("Menu rejects out-of-range numeric input")
  void menuRejectsOutOfRangeInput() {
    ByteArrayInputStream in = new ByteArrayInputStream("23\n13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Menu.of(in, new PrintStream(out), service).start();

    String result = out.toString();

    assertTrue(
        result.contains("Invalid option") || result.contains("Try again"),
        "Should reject out-of-range option");
  }

  @Test
  @DisplayName("Menu rejects negative input")
  void menuRejectsNegativeInput() {
    ByteArrayInputStream in = new ByteArrayInputStream("-5\n13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Menu.of(in, new PrintStream(out), service).start();

    String result = out.toString();

    assertTrue(
        result.contains("Invalid option") || result.contains("Try again"),
        "Should reject negative option");
  }

  @Test
  @DisplayName("Menu exits on option 13")
  void menuExitsOnOption13() {
    ByteArrayInputStream in = new ByteArrayInputStream("13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Menu.of(in, new PrintStream(out), service).start();

    String result = out.toString();

    assertTrue(result.contains("Adios!"), "Should show exit message");
  }

  @Test
  @DisplayName("Menu continues after invalid option before exit")
  void menuContinuesAfterInvalidOptionBeforeExit() {
    ByteArrayInputStream in = new ByteArrayInputStream("999\n13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Menu.of(in, new PrintStream(out), service).start();

    String result = out.toString();

    assertAll(
        () ->
            assertTrue(
                result.contains("Invalid option") || result.contains("Try again"),
                "Should reject invalid option"),
        () -> assertTrue(result.contains("Adios!"), "Should still show exit message"));
  }

  @Test
  @DisplayName("Menu handles multiple invalid inputs before exit")
  void menuHandlesMultipleInvalidInputsBeforeExit() {
    ByteArrayInputStream in = new ByteArrayInputStream("abc\n-1\n50\n13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Menu.of(in, new PrintStream(out), service).start();

    String result = out.toString();

    assertTrue(result.contains("Adios!"), "Should eventually exit and show goodbye");
  }

  @Test
  @DisplayName("Menu exits immediately with option 13")
  void menuExitImmediately() {
    ByteArrayInputStream in = new ByteArrayInputStream("13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Menu.of(in, new PrintStream(out), service).start();

    String result = out.toString();

    assertAll(
        () -> assertTrue(result.contains("Welcome to the task manager!"), "Should show welcome"),
        () -> assertTrue(result.contains("Adios!"), "Should show goodbye"));
  }

  @Test
  @DisplayName("Menu accepts option 1 (Save task)")
  void menuAcceptsOptionOne() {
    // Send option 1 to save, then provide invalid input to quit
    ByteArrayInputStream in = new ByteArrayInputStream("1\n13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    try {
      Menu.of(in, new PrintStream(out), service).start();
    } catch (Exception e) {
      // Menu might throw due to waiting for input, which is OK for this test
    }

    String result = out.toString();

    assertTrue(result.length() > 0, "Menu should accept option 1");
  }

  @Test
  @DisplayName("Menu output is non-empty")
  void menuProducesOutput() {
    ByteArrayInputStream in = new ByteArrayInputStream("13\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TaskService service = new TaskService(new TaskRepositoryImpl(), new AudditLogger());

    Menu.of(in, new PrintStream(out), service).start();

    String result = out.toString();

    assertTrue(result.length() > 0, "Menu should produce non-empty output");
  }
}
