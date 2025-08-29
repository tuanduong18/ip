package commands;

import data.TaskList;
import storage.Storage;
import tasks.Task;
import ui.Ui;

public class AddCommand extends Command {
	private final Task task;

	public AddCommand(Task t) {
		super(false);
		this.task = t;
	}

	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) {
		taskList.addTask(task);
		ui.printAddTask(task.toString(), taskList.size());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AddCommand) {
			Task t = ((AddCommand) o).task;
			return t.equals(this.task);
		}
		return false;
	}
}
