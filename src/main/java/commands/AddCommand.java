package commands;

import data.TaskList;
import storage.Storage;
import tasks.Task;
import ui.Ui;

public class AddCommand extends Command {
	private final Task t;

	public AddCommand(Task t) {
		super(false);
		this.t = t;
	}

	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) {
		taskList.addTask(t);
		ui.printAddTask(t.toString(), taskList.size());
	}
}
