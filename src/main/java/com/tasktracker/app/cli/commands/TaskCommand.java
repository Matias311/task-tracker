package com.tasktracker.app.cli.commands;

/**
 * Represents a command that can be executed in the task tracking application.
 *
 * <p>This interface follows the Command pattern. Each implementation encapsulates a specific
 * operation that can be executed by the CLI, such as creating, updating, searching or deleting
 * tasks.
 *
 * <p>Commands typically delegate the actual business logic to services while keeping the CLI layer
 * simple and decoupled from the application logic.
 */
public interface TaskCommand {

  /**
   * Executes the command.
   *
   * <p>The implementation defines the specific behavior of the command, usually invoking methods
   * from the service layer.
   */
  void execute();
}
