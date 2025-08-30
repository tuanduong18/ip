package commands;

import data.TaskList;
import data.exceptions.BarryException;
import storage.Storage;
import ui.Ui;

/**
 * Represents a command that marks or unmarks a {@link tasks.Task} in the {@link TaskList}.
 * <p>
 * The {@code MarkCommand} identifies a task by its one-based index
 * in the displayed task list. When executed, it updates the marked
 * status of the specified task, saves the change to storage, and
 * displays a confirmation message to the user via the {@link Ui}.
 * </p>
 */
public class MarkCommand extends Command {

	/**
	 * The one-based index of the task to be marked or unmarked.
	 */
	private final int id;

	/**
	 * Indicates whether the task should be marked or unmarked.
	 * {@code true} means mark the task, {@code false} means unmark the task.
	 */
	private final boolean marked;

	/**
	 * Creates a {@code MarkCommand}.
	 *
	 * @param id the one-based index of the task in the task list
	 * @param marked {@code true} if the task should be marked as done,
	 *               {@code false} if the task should be unmarked
	 */
	public MarkCommand(int id, boolean marked) {
		super(false);
		this.id = id;
		this.marked = marked;
	}

	/**
	 * Executes the mark command by marking or unmarking the specified task,
	 * saving the updated list to storage, and showing feedback to the user.
	 *
	 * @param taskList the task list containing the task to be marked/unmarked
	 * @param ui the user interface used to show the mark/unmark confirmation
	 * @param storage the storage handler used to persist the updated task list
	 * @throws BarryException if the index is invalid (≤ 0 or greater than the task list size)
	 */
	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) throws BarryException {
		if(id <= 0 || id > taskList.size()) {
			throw BarryException.taskNotFound(taskList.size());
		}
		String task = taskList.markTask(id - 1, marked);
		ui.printMarkTask(task, marked);
		storage.save(taskList);
	}

	
}
