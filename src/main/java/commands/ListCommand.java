package commands;

import data.TaskList;
import storage.Storage;
import ui.Ui;

/**
 * Represents a command that lists all tasks in the {@link TaskList}.
 * <p>
 * The {@code ListCommand} displays the current tasks to the user
 * through the {@link Ui}. It does not modify the task list or storage,
 * and it does not cause the program to exit.
 * </p>
 */
public class ListCommand extends Command{

	/**
	 * Creates a {@code ListCommand}.
	 * <p>
	 * The {@code isExit} flag is always set to {@code false}
	 * for this command, as listing tasks does not terminate the program.
	 * </p>
	 */
	public ListCommand() {
		super(false);
	}

	/**
	 * Executes the list command by displaying all tasks in the task list
	 * through the user interface.
	 *
	 * @param taskList the task list containing tasks to be listed
	 * @param ui the user interface used to display the tasks
	 * @param storage the storage handler (not used in this command,
	 *                but required by the method signature)
	 */
	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) {
		ui.printListTask(taskList.listTasks());
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ListCommand;
	}
}
