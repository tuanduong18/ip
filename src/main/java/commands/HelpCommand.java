package commands;

import data.TaskList;
import data.exceptions.BarryException;
import storage.Storage;
import ui.Ui;

public class HelpCommand extends Command {
	private final boolean detailed;

	public HelpCommand(boolean detailed) {
		super(false);
		this.detailed = detailed;
	}

	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) throws BarryException {
		if (detailed) {
			ui.printHelp();
		} else {
			ui.printDetailedHelp();
		}
	}
}
