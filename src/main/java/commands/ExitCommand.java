package commands;

import data.TaskList;
import storage.Storage;
import ui.Ui;

public class ExitCommand extends Command{
	public ExitCommand() {
		super(true);
	}

	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) {
		storage.save(taskList);
	}
}
