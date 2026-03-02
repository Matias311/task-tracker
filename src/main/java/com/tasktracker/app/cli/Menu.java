package com.tasktracker.app.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Menu {
  private final Scanner in;
  private final PrintStream out;

  private Menu(InputStream in, PrintStream out) {
    this.in = new Scanner(in);
    this.out = out;
  }

  /**
   * Creates a menu.
   *
   * @param in InputStream if its null throw IllegalArgumentException
   * @param out PrintStream if its null throw IllegalArgumentException
   */
  public static Menu of(InputStream in, PrintStream out) {
    if (in == null || out == null) {
      throw new IllegalArgumentException("InputStream and PrintStream must have a value");
    }
    return new Menu(in, out);
  }

  /** Shows the main menu to manage your task. */
  public void start() {
    out.println("Welcome to the task manager!");
    exit: // label to break the while loop
    while (true) {
      switch (selectionUser()) {
        case 1:
          // TODO: add the command to save a task
          break;
        case 2:
          // TODO: add the command to show all task
          break;
        case 3:
          // TODO: add the command to filter by category
          break;
        case 4:
          // TODO: add the command to filter by priority
          break;
        case 5:
          // TODO: add the command to filter by status
          break;
        case 6:
          // TODO: add the command to complete task
          break;
        case 7:
          // TODO: add the command to order task by due date
          break;
        case 8:
          // TODO: add the command to order task by priority
          break;
        case 9:
          // TODO: add the command to search task
          break;
        case 10:
          // TODO: add the command to show all the complete task
          break;
        case 11:
          // TODO: add the command to undone a task
          break;
        case 12:
          out.println("Adios!");
          break exit;
        default:
          out.println("Invalid option. Try again");
          break;
      }
    }
  }

  /**
   * Creates the options and return the selection of the user.
   *
   * @return int, the selection of the user
   */
  private int selectionUser() {
    out.println("1. Save a task");
    out.println("2. Show all tasks");
    out.println("3. Filter tasks by category");
    out.println("4. Filter tasks by priority");
    out.println("5. Filter tasks by status");
    out.println("6. Complete a task (require the task id)");
    out.println("7. Order task by due date");
    out.println("8. Order task by priority");
    out.println("9. Search task using the id");
    out.println("10. Show all the complete task");
    out.println("11. Undone a task (requiere the task id)");
    out.println("12. Exit!");

    while (!in.hasNextInt()) {
      out.println("Invalid option, must be a number. Write a new option:");
      in.next();
    }

    return in.nextInt();
  }
}
