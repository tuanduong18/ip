import commands.Command;
import commands.CommandType;
import data.TaskList;
import data.exceptions.BarryException;
import parser.Parser;
import storage.Storage;
import tasks.Deadline;
import tasks.Event;
import tasks.Todo;
import ui.Ui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


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
		this.taskList = new TaskList();
		this.parser = new Parser();
		this.storage = new Storage(path);
	}

	public void run() {
		ui.printGreetings();
		fetchData();
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


	public void fetchData() {
		Path path = Paths.get("..","data", "Barry.txt");
		File f = path.toFile();
		try {
			Scanner s = new Scanner(f);
			while (s.hasNextLine()) {
				String temp = s.nextLine();
				String[] cmd = temp.split(" \\| ", 5);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
				try {
					String type = cmd[0];
					String marked = cmd[1];
					String name = cmd[2];
					switch (type) {
					case "T":
						taskList.addTask(new Todo(name));
						break;
					case "D":
						String by = cmd[3];
						LocalDateTime due = LocalDateTime.parse(by, formatter);
						taskList.addTask(new Deadline(name, due));
						break;
					case "E":
						LocalDateTime start = LocalDateTime.parse(cmd[3], formatter);
						LocalDateTime end = LocalDateTime.parse(cmd[4], formatter);
						taskList.addTask(new Event(name, start, end));
						break;
					}
					taskList.markTask(taskList.size() - 1, marked.equals("1"));

				} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
					continue;
				}
			}
		} catch (FileNotFoundException e) {
			try{
				f.getParentFile().mkdirs(); // make sure "data" folder exists
				f.createNewFile();

			} catch (IOException ee) {
				ee.printStackTrace();
			}

		}

	}

	public void saveData() {
		StringBuilder s = new StringBuilder();
		for (String t : taskList.listTasks()) {
			char type = t.charAt(1);
			boolean marked = t.charAt(4) == 'X';
			String command = t.substring(7);
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("h:mm a d MMM, yyyy");
			DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			switch (type) {
			case 'T':
				s.append(type);
				s.append(" | ");
				s.append(marked ? "1" : "0");
				s.append(" | ");
				s.append(command);
				s.append(System.lineSeparator());
				break;
			case 'D':
				String[] details = command.split(" \\(by: " , 2);
				s.append(type);
				s.append(" | ");
				s.append(marked ? "1" : "0");
				s.append(" | ");
				s.append(details[0]);
				s.append(" | ");
				String due = details[1].substring(0, details[1].length() - 1);
				LocalDateTime dueParsed = LocalDateTime.parse(due, formatter1);
				due = dueParsed.format(formatter2);
				s.append(due);
				s.append(System.lineSeparator());
				break;
			case 'E':
				String name = command.split(" \\(from: ", 2)[0];
				String start = command.split(" \\(from: ", 2)[1].split(" to: ", 2)[0];
				String end = command.split(" \\(from: ", 2)[1].split(" to: ", 2)[1];
				end = end.substring(0, end.length() - 1);
				start = LocalDateTime.parse(start, formatter1).format(formatter2);
				end = LocalDateTime.parse(end, formatter1).format(formatter2);
				s.append(type);
				s.append(" | ");
				s.append(marked ? "1" : "0");
				s.append(" | ");
				s.append(name);
				s.append(" | ");
				s.append(start);
				s.append(" | ");
				s.append(end);
				s.append(System.lineSeparator());
				break;
			}
		}
		try {
			Path path = Paths.get("..","data", "Barry.txt");
			File f = path.toFile();
			FileWriter fw = new FileWriter(f);
			fw.write(s.toString());
			fw.close();
		} catch (IOException e) {
			System.out.println("Something went wrong: " + e.getMessage());
		}
	}

}
