package parser;

import commands.*;
import data.exceptions.BarryException;
import tasks.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import static commands.CommandType.*;

public class Parser {
	String DATE_FORMAT = "dd/MM/yyyy HH:mm";


	String TODO_REGX = "todo [^|]*";
	String DEADLINE_REGX = "deadline [^|]* /by [^|]*";
	String EVENT_REGX = "event [^|]* /from [^|]* /to [^|]*";
	String MARK_REGX = "(mark|unmark) [0-9]+";
	String LIST_REGEX = "(list)";
	String DELETE_REGEX = "(delete) [0-9]+";
	String HELP_REGEX = "(help)";
	String DETAILED_HELP_REGEX = "(help --details)";

	public Command parseCommand(String fullCommand) throws BarryException {
		String tag = fullCommand.split(" ")[0];
		switch (CommandType.parseCommand(tag)) {
		case TODO:
		case DEADLINE:
		case EVENT:
			return addTask(fullCommand);

		case MARK:
		case UNMARK:
			return markTask(fullCommand);

		case DELETE:
			return deleteTask(fullCommand);

		case LIST:
			return listTask(fullCommand);

		case BYE:
			if(fullCommand.equals("bye")) {
				return new ExitCommand();
			} else {
				throw BarryException.commandException();
			}

		case HELP:
		case DETAILED_HELP:
			return help(fullCommand);

		default:
			throw BarryException.commandException();
		}
	}

	public Command addTask(String command) throws BarryException {
		Task temp;
		if (Pattern.matches(TODO_REGX, command)) {
			String name = command.substring(5);
			if (name.trim().isEmpty()) {
				throw BarryException.missingNameException(TODO);
			}
			temp = new Todo(name);
		} else if (Pattern.matches(DEADLINE_REGX, command)) {
			String[] ss = command.substring(9).split(" /by ", 2);
			if (ss[0].trim().isEmpty()) {
				throw BarryException.missingNameException(DEADLINE);
			} else if (ss[1].trim().isEmpty()) {
				throw BarryException.missingTimestamp(DEADLINE, "due date");
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
			LocalDateTime due;
			try {
				due = LocalDateTime.parse(ss[1].trim(), formatter);
			} catch (DateTimeParseException e) {
				throw BarryException.invalidTimestamp(DEADLINE, "due date", DATE_FORMAT);
			}
			temp = new Deadline(ss[0], due);
		} else if ((Pattern.matches(EVENT_REGX, command))) {
			String[] s1 = command.substring(6).split(" /from ", 2);
			String[] s2 = s1[1].split(" /to ", 2);
			if (s1[0].trim().isEmpty()) {
				throw BarryException.missingNameException(EVENT);
			} else if (s2[0].trim().isEmpty()) {
				throw BarryException.missingTimestamp(EVENT, "starting time");
			} else if (s2[1].trim().isEmpty()) {
				throw BarryException.missingTimestamp(EVENT, "ending time");
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
			LocalDateTime start, end;
			try {
				start = LocalDateTime.parse(s2[0].trim(), formatter);
			} catch (DateTimeParseException e) {
				throw BarryException.invalidTimestamp(EVENT, "start time", DATE_FORMAT);
			}
			try {
				end = LocalDateTime.parse(s2[1].trim(), formatter);
			} catch (DateTimeParseException e) {
				throw BarryException.invalidTimestamp(EVENT, "end time", DATE_FORMAT);
			}
			temp = new Event(s1[0], start, end);
		} else {
			throw BarryException.commandException(new CommandType[]{TODO, DEADLINE, EVENT});
		}
		return new AddCommand(temp);
	}

	public Command markTask(String command) throws BarryException {
		if (!(Pattern.matches(MARK_REGX, command))) {
			throw BarryException.commandException(new CommandType[]{MARK, UNMARK});
		}
		String[] ss = command.split(" ");
		int id;
		try {
			id = Integer.parseInt(ss[1]);
		} catch (NumberFormatException e) {
			// Should not go to this line
			throw BarryException.commandException();
		}

		return new MarkCommand(id, ss[0].equals("mark"));
	}

	public Command listTask(String command) throws BarryException {
		if (!(Pattern.matches(LIST_REGEX, command))) {
			// Should not go to this line
			throw BarryException.commandException(new CommandType[]{LIST});
		}
		return new ListCommand();
	}

	public Command deleteTask(String command) throws BarryException {
		if (!(Pattern.matches(DELETE_REGEX, command))) {
			// Should not go to this line
			throw BarryException.commandException(new CommandType[]{DELETE});
		}
		String[] ss = command.split(" ");
		int id;
		try {
			id = Integer.parseInt(ss[1]);
		} catch (NumberFormatException e) {
			// Should not go to this line
			throw BarryException.commandException();
		}

		return new DeleteCommand(id);
	}

	public Command help(String command) throws BarryException {
		if (!(Pattern.matches(HELP_REGEX, command)) && !(Pattern.matches(DETAILED_HELP_REGEX, command)))  {
			throw BarryException.commandException(new CommandType[]{CommandType.HELP, CommandType.DETAILED_HELP});
		}

		return new HelpCommand(command.equals("help --details"));
	}
}
