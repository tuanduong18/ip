package commands;

import java.util.ArrayList;

import data.TaskList;
import storage.Storage;
import ui.Ui;

public class FindCommand extends Command{
	private final String pattern;

	public FindCommand(String pattern) {
		super(false);
		this.pattern = pattern;
	}

	@Override
	public void execute(TaskList taskList, Ui ui, Storage storage) {
		ArrayList<String> tasks = taskList.findMatchingTasks(pattern);
		ui.printMatchingTasks(tasks);
	}
}
