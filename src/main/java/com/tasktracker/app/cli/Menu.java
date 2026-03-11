package com.tasktracker.app.cli;

import com.tasktracker.app.cli.commands.CommandHistory;
import com.tasktracker.app.cli.commands.CompleteTaskCommand;
import com.tasktracker.app.cli.commands.DeleteCommand;
import com.tasktracker.app.cli.commands.FilterByPriorityCommand;
import com.tasktracker.app.cli.commands.FilterByStatusCommand;
import com.tasktracker.app.cli.commands.FilterByTypeCommand;
import com.tasktracker.app.cli.commands.GetAllTaskCompleteCommand;
import com.tasktracker.app.cli.commands.OrderByDueDateCommand;
import com.tasktracker.app.cli.commands.OrderByPriorityCommand;
import com.tasktracker.app.cli.commands.SaveTaskCommand;
import com.tasktracker.app.cli.commands.SearchByIdCommand;
import com.tasktracker.app.cli.commands.ShowAllTaskCommand;
import com.tasktracker.app.cli.commands.UndoneTaskCommand;
import com.tasktracker.app.service.TaskService;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Scanner;

/** This class creates the menu, have the diferent options that a user can use. */
public class Menu {
  private final Scanner in;
  private final PrintStream out;
  private final TaskService service;
  private final CommandHistory control = new CommandHistory();

  private Menu(InputStream in, PrintStream out, TaskService service) {
    this.in = new Scanner(in);
    this.out = out;
    this.service = service;
  }

  /**
   * Creates a menu.
   *
   * @param in InputStream if its null throw IllegalArgumentException
   * @param out PrintStream if its null throw IllegalArgumentException
   */
  public static Menu of(InputStream in, PrintStream out, TaskService service) {
    if (in == null || out == null || service == null) {
      throw new IllegalArgumentException(
          "InputStream, PrintStream and TaskService must have a value");
    }
    return new Menu(in, out, service);
  }

  /** Shows the main menu to manage your task. */
  public void start() {
    out.println("Welcome to the task manager!");
    exit: // label to break the while loop
    while (true) {
      switch (selectionUser()) {
        case 1:
          out.println("Save task\nThe id and title must have value, else is optional");

          int id = inId("Task id: ");
          String title = inString("Task title: ", true);
          String type =
              verifyStringForCli(
                  inString("Task type:\n- PROGRAMMING\n- UNIVERSITY\n- LIVE ", false));
          String description = verifyStringForCli(inString("Task description: ", false));
          String priority =
              verifyStringForCli(inString("Task priority:\n- HIGH\n- MEDIUM\n- LOW", false));
          String status =
              verifyStringForCli(inString("Task status:\n- TODO\n- DOING\n- DONE", false));

          LocalDate date =
              parseDate(inString("Task date (Must follow this pattern YYYY-MM-DD", false));
          LocalDate dueDate =
              parseDate(
                  inString(
                      "Task due date (Must follow this pattern YYYY-MM-DD and must be after task"
                          + " date)",
                      false));

          control.execute(
              new SaveTaskCommand(
                  service, id, title, type, description, priority, status, date, dueDate));
          break;
        case 2:
          control.execute(new ShowAllTaskCommand(service));
          break;
        case 3:
          control.execute(
              new FilterByTypeCommand(
                  service,
                  inString("Write the category:\n- PROGRAMMING\n- UNIVERSITY\n- LIVE: ", true)));
          break;
        case 4:
          control.execute(
              new FilterByPriorityCommand(
                  service, inString("Write the priority:\n- HIGH\n- MEDIUM\n- LOW: ", true)));
          break;
        case 5:
          control.execute(
              new FilterByStatusCommand(
                  service, inString("Write the status:\n- TODO\n- DOING\n- DONE: ", true)));
          break;
        case 6:
          control.execute(new CompleteTaskCommand(service, inId("Task id: ")));
          break;
        case 7:
          control.execute(new OrderByDueDateCommand(service));
          break;
        case 8:
          control.execute(new OrderByPriorityCommand(service));
          break;
        case 9:
          control.execute(new SearchByIdCommand(service, inId("Task id: ")));
          break;
        case 10:
          control.execute(new GetAllTaskCompleteCommand(service));
          break;
        case 11:
          control.execute(new UndoneTaskCommand(service, inId("Task id: ")));
          break;
        case 12:
          control.execute(new DeleteCommand(service, inId("Task id to delete: ")));
          break;
        case 13:
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
    out.println("12. delete a task (requiere the task id)");
    out.println("13. Exit!");

    while (!in.hasNextInt()) {
      out.println("Invalid option, must be a number. Write a new option:");
      in.next();
    }

    return in.nextInt();
  }

  /**
   * Output a message and get the input value of the user.
   *
   * @param m is the message to the user
   * @param f is the flag to check if the input is a valid string
   * @return string is the input of the user
   */
  private String inString(String m, boolean f) {
    String userIn;
    out.println(m);

    if (f) {
      while (in.nextLine() == null) {
        out.println("Invalid input");
        in.nextLine();
      }
    }

    userIn = in.nextLine();
    return userIn;
  }

  /**
   * Show the m param to get a int value, if the value is not a int ask again for a int.
   *
   * @param m String that is output in the console
   * @return int the input user parse to int
   */
  private int inId(String m) {
    out.println(m);
    while (!in.hasNextInt()) {
      out.println("Invalid id");
      in.next();
    }
    int id = in.nextInt();
    return id;
  }

  /**
   * Verify if the data have value or is null, if is null or empty return null if not return the
   * data.
   *
   * @param data String
   * @return String can be the data or a null
   */
  private String verifyStringForCli(String data) {
    return data == null || data.isBlank() ? null : data;
  }

  /**
   * Checks if the String if a date following {@code YYYY-MM-DD}.
   *
   * @param date is the string date
   */
  private LocalDate parseDate(String date) {
    if (date.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
      return LocalDate.parse(date);
    } else {
      return null;
    }
  }
}
