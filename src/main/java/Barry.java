import commands.Command;
import data.TaskList;
import data.exceptions.BarryException;
import parser.Parser;
import storage.Storage;
import ui.Ui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


public class Barry {

	// Print messages from Barry
	private final Ui ui;

	// Store all tasks in the session
	private final TaskList taskList;

	// Parse the command
	private final Parser parser;

	// Interact with local storage
	private final Storage storage;

	public Barry(Path path) {
		this.ui = new Ui();
		this.parser = new Parser();
		this.storage = new Storage(path);
		TaskList stored = new TaskList();
		try {
			stored = storage.load();
		} catch (BarryException e) {
			ui.showLoadingError();
		} finally {
			taskList = stored;
		}
	}

	public void run() {
		ui.printGreetings();
		Scanner scan = new Scanner(System.in);
		boolean isExit = false;
		while(!isExit) {
			try {
				String temp = scan.nextLine();
				Command c = parser.parseCommand(temp);
				c.execute(taskList, ui, storage);
				isExit = c.isExit;
			} catch (BarryException e) {
				ArrayList<String> s = new ArrayList<>();
				s.add("OOPS!!! " + e.getMessage());
				ui.print(s);
			}
		}
		ui.printGoodbye();
	}

	public static void main(String[] args) {
		new Barry(Paths.get("..","data", "Barry.txt")).run();
	}
}
