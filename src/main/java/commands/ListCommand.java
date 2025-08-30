package commands;

import data.TaskList;
import storage.Storage;
import ui.Ui;

public class ListCommand extends Command{
	public ListCommand() {
		super(false);
	}

	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) {
		ui.printListTask(taskList.listTasks());
	}
}
