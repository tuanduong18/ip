package parser;

import static data.common.CommandType.DEADLINE;
import static data.common.CommandType.EVENT;
import static data.common.CommandType.TODO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import data.common.CommandType;
import data.common.TaskRegex;
import data.exceptions.BarryException;
import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.Todo;

public class TaskParser {
	static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

	public static Task parseTask(String command) throws BarryException {
		TaskRegex t = TaskRegex.parseTask(command); // This line can throw Barry Exception
		ArrayList<String> params = t.extractComponents(command); // Extract parameters
		String description = params.get(0);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

		switch (t) {
		case TODO:
			if (description.trim().isEmpty()) {
				throw BarryException.missingNameException(TODO);
			}
			return new Todo(description);
		case DEADLINE:
			if (description.trim().isEmpty()) {
				throw BarryException.missingNameException(DEADLINE);
			} else if (params.get(1).trim().isEmpty()) {
				throw BarryException.missingTimestamp(DEADLINE, "due date");
			}
			LocalDateTime due;
			try {
				due = LocalDateTime.parse(params.get(1).trim(), formatter);
			} catch (DateTimeParseException e) {
				throw BarryException.invalidTimestamp(DEADLINE, "due date", DATE_FORMAT);
			}
			return new Deadline(description, due);
		case EVENT:
			if (description.trim().isEmpty()) {
				throw BarryException.missingNameException(EVENT);
			} else if (params.get(1).trim().isEmpty()) {
				throw BarryException.missingTimestamp(EVENT, "starting time");
			} else if (params.get(2).trim().isEmpty()) {
				throw BarryException.missingTimestamp(EVENT, "ending time");
			}
			LocalDateTime start, end;
			try {
				start = LocalDateTime.parse(params.get(1), formatter);
			} catch (DateTimeParseException e) {
				throw BarryException.invalidTimestamp(EVENT, "start time", DATE_FORMAT);
			}
			try {
				end = LocalDateTime.parse(params.get(2), formatter);
			} catch (DateTimeParseException e) {
				throw BarryException.invalidTimestamp(EVENT, "end time", DATE_FORMAT);
			}
			return new Event(description, start, end);
		default:
			throw BarryException.commandException(new CommandType[]{TODO, DEADLINE, EVENT});
		}
	}
}
