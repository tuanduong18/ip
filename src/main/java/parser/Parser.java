package parser;

import commands.CommandType;
import data.TaskList;
import data.exceptions.BarryException;
import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.Todo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Parser {
	String DATE_FORMAT = "dd/MM/yyyy HH:mm";

	String TODO_REGX = "todo [^|]*";
	String DEADLINE_REGX = "deadline [^|]* /by [^|]*";
	String EVENT_REGX = "event [^|]* /from [^|]* /to [^|]*";

	public ArrayList<String> parseTask(String content, TaskList allTask) throws BarryException {
		Task temp;
		if (Pattern.matches(TODO_REGX, content)) {
			String name = content.substring(5);
			if (name.trim().isEmpty()) {
				throw BarryException.missingNameException(CommandType.TODO);
			}
			temp = new Todo(name);
		} else if (Pattern.matches(DEADLINE_REGX, content)) {
			String[] ss = content.substring(9).split(" /by ", 2);
			if (ss[0].trim().isEmpty()) {
				throw BarryException.missingNameException(CommandType.DEADLINE);
			} else if (ss[1].trim().isEmpty()) {
				throw BarryException.missingTimestamp(CommandType.DEADLINE, "due date");
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
			LocalDateTime due;
			try {
				due = LocalDateTime.parse(ss[1].trim(), formatter);
			} catch (DateTimeParseException e) {
				throw BarryException.invalidTimestamp(CommandType.DEADLINE, "due date", DATE_FORMAT);
			}
			temp = new Deadline(ss[0], due);
		} else if ((Pattern.matches(EVENT_REGX, content))) {
			String[] s1 = content.substring(6).split(" /from ", 2);
			String[] s2 = s1[1].split(" /to ", 2);
			if (s1[0].trim().isEmpty()) {
				throw BarryException.missingNameException(CommandType.EVENT);
			} else if (s2[0].trim().isEmpty()) {
				throw BarryException.missingTimestamp(CommandType.EVENT, "starting time");
			} else if (s2[1].trim().isEmpty()) {
				throw BarryException.missingTimestamp(CommandType.EVENT, "ending time");
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
			LocalDateTime start, end;
			try {
				start = LocalDateTime.parse(s2[0].trim(), formatter);
			} catch (DateTimeParseException e) {
				throw BarryException.invalidTimestamp(CommandType.EVENT, "start time", DATE_FORMAT);
			}
			try {
				end = LocalDateTime.parse(s2[1].trim(), formatter);
			} catch (DateTimeParseException e) {
				throw BarryException.invalidTimestamp(CommandType.EVENT, "end time", DATE_FORMAT);
			}
			temp = new Event(s1[0], start, end);
		} else {
			throw BarryException.commandException(new CommandType[]{CommandType.TODO, CommandType.DEADLINE, CommandType.EVENT});
		}
		allTask.addTask(temp);
		int n = allTask.size();
		ArrayList<String> s = new ArrayList<>();
		s.add("Got it. I've added this task:");
		s.add("\t" + temp);
		s.add("Now you have " + n + (n > 1 ? " tasks " : " task ") + "in the list.");
		return s;
	}
}
