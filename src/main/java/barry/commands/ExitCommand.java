package barry.commands;

import barry.data.TaskList;
import barry.storage.Storage;
import barry.ui.Gui;
import barry.ui.Ui;

/**
 * Represents a command that exits the application.
 * <p>
 * The {@code ExitCommand} signals the application to terminate.
 * When executed, it saves the current {@link TaskList} to persistent storage
 * before the program exits. This command is always marked as an exit command.
 * </p>
 */
public class ExitCommand extends Command {

    /**
     * Creates an {@code ExitCommand}.
     * <p>
     * The {@code isExit} flag is always set to {@code true} for this command,
     * indicating that executing it will terminate the application.
     * </p>
     */
    public ExitCommand() {
        super(true);
    }

    /**
     * Executes the exit command by saving the current task list to storage.
     * After this method is run, the application is expected to terminate.
     *
     * @param taskList the task list to be saved before exit
     * @param ui       the user interface (not used in this command, but required by signature)
     * @param storage  the storage handler used to persist the task list
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        storage.save(taskList);
    }

    /**
     * Semantics are identical to {@link #execute(TaskList, Ui, Storage)}.
     *
     * @param taskList the task list to be saved before exit
     * @param gui      the GUI facade (analogous to {@link Ui} but returning strings)
     * @param storage  the storage handler used to persist the task list
     * @return the farewell message to display in the GUI
     */
    @Override
    public String execute(TaskList taskList, Gui gui, Storage storage) {
        storage.save(taskList);
        return gui.printGoodbye();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ExitCommand;
    }
}
