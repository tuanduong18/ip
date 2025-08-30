package commands;

import data.TaskList;
import data.exceptions.BarryException;
import storage.Storage;
import ui.Ui;

abstract public class Command {
	public final boolean isExit;
	public abstract void execute(TaskList taskList, Ui ui, Storage storage) throws BarryException;

	public Command(boolean isExit) {
		this.isExit = isExit;
	}
}
