package commands;

import data.TaskList;
import data.exceptions.BarryException;
import storage.Storage;
import tasks.Task;
import ui.Ui;

public class MarkCommand extends Command {
	private final int id;
	private final boolean marked;

	public MarkCommand(int id, boolean marked) {
		super(false);
		this.id = id;
		this.marked = marked;
	}

	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) throws BarryException {
		if(id <= 0 || id > taskList.size()) {
			throw BarryException.taskNotFound(taskList.size());
		}
		String task = taskList.markTask(id - 1, marked);
		ui.printMarkTask(task, marked);
	}
}
