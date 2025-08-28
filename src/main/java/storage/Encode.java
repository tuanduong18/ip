package storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Encode {
	public static String encode(String task) {
		StringBuilder s = new StringBuilder();
		char type = task.charAt(1);
		boolean marked = task.charAt(4) == 'X';
		String command = task.substring(7);
		DateTimeFormatter appearanceFormatter = DateTimeFormatter.ofPattern("h:mm a d MMM, yyyy");
		DateTimeFormatter storedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
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
			String[] details = command.split(" \\(by: ", 2);
			s.append(type);
			s.append(" | ");
			s.append(marked ? "1" : "0");
			s.append(" | ");
			s.append(details[0]);
			s.append(" | ");
			String due = details[1].substring(0, details[1].length() - 1);
			LocalDateTime dueParsed = LocalDateTime.parse(due, appearanceFormatter);
			due = dueParsed.format(storedFormatter);
			s.append(due);
			s.append(System.lineSeparator());
			break;
		case 'E':
			String name = command.split(" \\(from: ", 2)[0];
			String start = command.split(" \\(from: ", 2)[1].split(" to: ", 2)[0];
			String end = command.split(" \\(from: ", 2)[1].split(" to: ", 2)[1];
			end = end.substring(0, end.length() - 1);
			start = LocalDateTime.parse(start, appearanceFormatter).format(storedFormatter);
			end = LocalDateTime.parse(end, appearanceFormatter).format(storedFormatter);
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
		return s.toString();
	}
}
