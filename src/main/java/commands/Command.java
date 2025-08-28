package commands;
import data.TaskList;
import storage.Storage;
import ui.Ui;

abstract public class Command {
	abstract void execute(TaskList t, Ui ui, Storage storage);
}
