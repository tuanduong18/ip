package storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import data.exceptions.BarryException;
import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.Todo;

public class Decode {
	public static Task decode(String content) throws BarryException {
		Task t;
		String[] cmd = content.split(" \\| ", 5);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		try {
			String type = cmd[0];
			String marked = cmd[1];
			String name = cmd[2];
			switch (type) {
			case "T":
				t = new Todo(name);
				t.setStatus(marked.equals("1"));
				break;
			case "D":
				String by = cmd[3];
				LocalDateTime due = LocalDateTime.parse(by, formatter);
				t = new Deadline(name, due);
				t.setStatus(marked.equals("1"));
				break;
			case "E":
				LocalDateTime start = LocalDateTime.parse(cmd[3], formatter);
				LocalDateTime end = LocalDateTime.parse(cmd[4], formatter);
				t = new Event(name, start, end);
				t.setStatus(marked.equals("1"));
				break;
			default:
				throw new BarryException("Invalid data source");
			}
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			// Should not go to this line
			throw BarryException.invalidSourceFilePath();
		}
		return t;
	}
}
