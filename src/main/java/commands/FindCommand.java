package commands;

import java.util.ArrayList;

import data.TaskList;
import storage.Storage;
import ui.Ui;

/**
 * Represents a command that finds tasks in the {@link TaskList} that match a search pattern.
 * <p>
 * The {@code FindCommand} searches the current tasks for entries matching the provided
 * {@code pattern} and displays them to the user through the {@link Ui}.
 * It does not modify the task list or storage, and it does not cause the program to exit.
 * </p>
 */
public class FindCommand extends Command {
    private final String pattern;


    /**
     * Creates a {@code FindCommand} with the specified search pattern.
     * <p>
     * The {@code isExit} flag is always set to {@code false} for this command,
     * as finding tasks does not terminate the program.
     * </p>
     *
     * @param pattern the search term or pattern used to filter tasks
     */
    public FindCommand(String pattern) {
        super(false);
        this.pattern = pattern;
    }

    /**
     * Executes the find command by searching the task list for tasks that match the stored
     * {@code pattern} and displaying the results through the user interface.
     *
     * @param taskList the task list to search for matching tasks
     * @param ui       the user interface used to display the matching tasks
     * @param storage  the storage handler (not used in this command,
     *                 but required by the method signature)
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        ArrayList<String> tasks = taskList.findMatchingTasks(pattern);
        ui.printMatchingTasks(tasks);
    }
}
