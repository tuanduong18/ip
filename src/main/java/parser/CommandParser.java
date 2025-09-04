package parser;

import java.util.ArrayList;

import commands.AddCommand;
import commands.Command;
import data.common.CommandRegex;
import commands.DeleteCommand;
import commands.ExitCommand;
import commands.FindCommand;
import commands.HelpCommand;
import commands.ListCommand;
import commands.MarkCommand;
import data.exceptions.BarryException;
import tasks.Task;

public class CommandParser {

	public Command parseCommand(String fullCommand) throws BarryException {
		CommandRegex c = CommandRegex.parseCommand(fullCommand); // Can throw BarryException
		ArrayList<String> params = c.extractComponents(fullCommand);
		return switch (c) {
			case TODO, DEADLINE, EVENT -> addTask(fullCommand);
			case MARK, UNMARK -> markTask(params.get(0), params.get(1));
			case DELETE -> deleteTask(params.get(0));
			case LIST -> listTask();
			case FIND -> findTask(params.get(0));
			case BYE -> new ExitCommand();
			case HELP, DETAILED_HELP -> help(fullCommand);
			default -> throw BarryException.commandException();
		};
	}

	public Command addTask(String command) throws BarryException {
		Task temp = TaskParser.parseTask(command);
		return new AddCommand(temp);
	}

	public Command markTask(String type, String position) throws BarryException {
		try {
			int id = Integer.parseInt(position);
			return new MarkCommand(id, type.equals("mark"));
		} catch (NumberFormatException e) {
			// Should not go to this line
			throw BarryException.commandException();
		}
	}

	public Command listTask() {
		return new ListCommand();
	}

	public Command deleteTask(String position) throws BarryException {
		try {
			int id = Integer.parseInt(position);
			return new DeleteCommand(id);
		} catch (NumberFormatException e) {
			// Should not go to this line
			throw BarryException.commandException();
		}
	}

	public Command help(String command) throws BarryException {
		return new HelpCommand(command.equals("help --details"));
	}

	public Command findTask(String pattern) {

		return new FindCommand(pattern);
	}
}
