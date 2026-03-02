package com.tasktracker.app.cli;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

@DisplayName("Test for the menu of the aplication")
public class MenuTest {

  private Menu menu;

  @ParameterizedTest
  @NullSource
  @DisplayName("Creates a menu with null inputStream")
  void createMenuNullInputStream(InputStream in) {
    Exception ex = assertThrows(IllegalArgumentException.class, () -> Menu.of(in, System.out));
    assertEquals("InputStream and PrintStream must have a value", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Creates a menu with null printStream")
  void createMenuNullPrintStream(PrintStream out) {
    Exception ex = assertThrows(IllegalArgumentException.class, () -> Menu.of(System.in, out));
    assertEquals("InputStream and PrintStream must have a value", ex.getMessage());
  }

  @Test
  @DisplayName("Test for selection user, verify if shows the menu")
  void verifyMenuSelectionUser() {
    ByteArrayInputStream in = new ByteArrayInputStream("12\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Menu.of(in, new PrintStream(out)).start();
    String result = out.toString();
    assertAll(
        () -> assertTrue(result.contains("Welcome to the task manager!")),
        () -> assertTrue(result.contains("1. Save a task")),
        () -> assertTrue(result.contains("2. Show all tasks")),
        () -> assertTrue(result.contains("3. Filter tasks by category")),
        () -> assertTrue(result.contains("4. Filter tasks by priority")),
        () -> assertTrue(result.contains("5. Filter tasks by status")),
        () -> assertTrue(result.contains("6. Complete a task (require the task id)")),
        () -> assertTrue(result.contains("7. Order task by due date")),
        () -> assertTrue(result.contains("8. Order task by priority")),
        () -> assertTrue(result.contains("9. Search task using the id")),
        () -> assertTrue(result.contains("10. Show all the complete task")),
        () -> assertTrue(result.contains("11. Undone a task (requiere the task id)")),
        () -> assertTrue(result.contains("12. Exit!")),
        () -> assertTrue(result.contains("Adios!")));
  }

  @Test
  @DisplayName("Test for selection user, we pass a string")
  void selectionUserBadInput() {
    ByteArrayInputStream in = new ByteArrayInputStream("SISIS\n12\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Menu.of(in, new PrintStream(out)).start();
    String result = out.toString();
    assertTrue(result.contains("Invalid option, must be a number. Write a new option:"));
  }

  @Test
  @DisplayName("Test for start, pass invalid option")
  void startMethodInvalidOption() {
    ByteArrayInputStream in = new ByteArrayInputStream("23\n12\n".getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Menu.of(in, new PrintStream(out)).start();
    String result = out.toString();
    assertTrue(result.contains("Invalid option. Try again"));
  }
}
