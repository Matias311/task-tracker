package com.tasktracker.app.cli.commands;

import com.tasktracker.app.exception.PersistenceException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** This class represent the history, can execute the command and save it in order of execution. */
public class CommandHistory {

  private final List<TaskCommand> history = new ArrayList<>();

  /**
   * Execute and push the command in the history, if the command is null throw
   * IllegalArgumentException.
   *
   * @param command TaskCommand
   */
  public void execute(TaskCommand command) {
    if (command == null) {
      throw new IllegalArgumentException("Invalid command");
    }
    try {
      command.execute();
      history.add(command);
    } catch (PersistenceException ex) {
      PrintStream ps = new PrintStream(System.out);
      ps.println(ex);
    }
  }

  /**
   * Get a copy of the history.
   *
   * @return List of command task
   */
  public List<TaskCommand> getHistory() {
    return Collections.unmodifiableList(history);
  }
}
