package com.tasktracker.app.cli.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    command.execute();
    history.add(command);
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
