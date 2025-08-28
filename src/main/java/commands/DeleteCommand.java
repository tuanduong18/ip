package commands;

import data.TaskList;
import data.exceptions.BarryException;
import storage.Storage;
import ui.Ui;

public class DeleteCommand extends Command {
	private final int id;

	public DeleteCommand(int id) {
		super(false);
		this.id = id;
	}

	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) throws BarryException {
		if(id <= 0 || id > taskList.size()) {
			throw BarryException.taskNotFound(taskList.size());
		}

		String task = taskList.deleteTask(id - 1);
		ui.printDeleteTask(task, taskList.size());
	}
}
