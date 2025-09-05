package commands;

import data.TaskList;
import data.exceptions.BarryException;
import storage.Storage;
import ui.Ui;

/**
 * Represents an abstract command that can be executed by the chatbot.
 * <p>
 * Each {@code Command} encapsulates a user instruction and defines
 * how it should affect the {@link TaskList}, {@link Ui}, and {@link Storage}.
 * Subclasses of {@code Command} implement the {@link #execute(TaskList, Ui, Storage)}
 * method to provide specific behavior (e.g., adding a task, listing tasks, exiting the program).
 * </p>
 */
public abstract class Command {

    /**
     * Indicates whether this command will cause the program to exit after execution.
     */
    public final boolean isExit;

    /**
     * Constructs a {@code Command}.
     *
     * @param isExit {@code true} if this command should terminate the application
     *               after execution, {@code false} otherwise.
     */
    public Command(boolean isExit) {
        this.isExit = isExit;
    }

    /**
     * Executes the command with the given task list, user interface, and storage.
     *
     * @param taskList the task list that the command operates on
     * @param ui       the user interface used to display messages to the user
     * @param storage  the storage handler used to load and save task data
     * @throws BarryException if an error specific to command execution occurs
     */
    public abstract void execute(TaskList taskList, Ui ui, Storage storage) throws BarryException;
}
