package commands;

import data.TaskList;
import data.exceptions.BarryException;
import storage.Storage;
import ui.Ui;

/**
 * Represents a command that deletes a {@link tasks.Task} from the {@link TaskList}.
 * <p>
 * The {@code DeleteCommand} identifies a task by its one-based index
 * in the displayed task list. When executed, it removes the task,
 * saves the updated list to storage, and displays a confirmation message
 * to the user via the {@link Ui}.
 * </p>
 */
public class DeleteCommand extends Command {

	/**
	 * The one-based index of the task to be deleted.
	 */
	private final int id;

	/**
	 * Creates a {@code DeleteCommand} to delete the task at the specified index.
	 *
	 * @param id the one-based index of the task in the task list
	 */
	public DeleteCommand(int id) {
		super(false);
		this.id = id;
	}

	/**
	 * Executes the command by removing the task at the specified index
	 * from the task list, saving the updated list to storage, and
	 * showing feedback to the user.
	 *
	 * @param taskList the task list from which the task will be deleted
	 * @param ui the user interface used to show the delete confirmation
	 * @param storage the storage handler used to persist the updated task list
	 * @throws BarryException if the index is invalid (≤ 0 or greater than the task list size)
	 */
	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) throws BarryException {
		if(id <= 0 || id > taskList.size()) {
			throw BarryException.taskNotFound(taskList.size());
		}

		String task = taskList.deleteTask(id - 1);
		ui.printDeleteTask(task, taskList.size());
		storage.save(taskList);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DeleteCommand) {
			int oId = ((DeleteCommand) o).id;
			return oId == id;
		}
		return false;
	}
}
