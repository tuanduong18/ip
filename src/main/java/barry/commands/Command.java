package barry.commands;

import barry.data.TaskList;
import barry.data.exceptions.BarryException;
import barry.storage.Storage;
import barry.ui.Gui;
import barry.ui.Ui;

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

    /**
     * Executes the command for a GUI flow and returns the user-facing message.
     * <p>
     * Semantics are identical to {@link #execute(TaskList, Ui, Storage)}: implementations
     * must perform the same side effects on the {@link TaskList} and interact with
     * {@link Storage} in the same way. The only difference is output handling—no console
     * printing should occur here. Instead, produce the message using the analogous
     * {@link Gui} methods (which mirror {@link Ui} but return strings) and return the
     * resulting text for the caller to render.
     * Callers may consult {@link #isExit} to decide whether to terminate afterward.
     * </p>
     *
     * @param taskList the task list the command operates on
     * @param gui      the GUI facade (same API as {@link Ui} but methods return strings)
     * @param storage  the storage handler used to load and save task data
     * @return the message to display in the GUI (may be multi-line)
     * @throws BarryException if a command-specific error occurs during execution
     * @see #execute(TaskList, Ui, Storage)
     */
    public abstract String execute(TaskList taskList, Gui gui, Storage storage) throws BarryException;
}
