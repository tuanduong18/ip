package data.common;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.exceptions.BarryException;

public enum CommandRegex {
	TODO("todo", Pattern.compile("todo (.*)")),
	DEADLINE("deadline", Pattern.compile("deadline (.*)")),
	EVENT("event", Pattern.compile("event (.*)")),
	MARK("mark", Pattern.compile("mark ([0-9]+)")),
	UNMARK("unmark", Pattern.compile("unmark ([0-9]+)")),
	LIST("list", Pattern.compile("list")),
	DELETE("delete", Pattern.compile("delete ([0-9]+)")),
	FIND("find", Pattern.compile("find (.*)")),
	HELP("help", Pattern.compile("help")),
	DETAILED_HELP("help", Pattern.compile("help --details")),
	BYE("bye", Pattern.compile("bye"));

	private final String tag;
	private final Pattern pattern;

	CommandRegex(String tag, Pattern pattern) {
		this.tag = tag;
		this.pattern = pattern;
	}

	public static CommandRegex parseCommand(String command) throws BarryException {
		String commandTag = command.split(" ")[0];
		for (CommandRegex t : CommandRegex.values()) {
			if (commandTag.equals(t.tag)) {
				if(t.pattern.matcher(command).matches()) {
					return t;
				}
			}
		}
		throw BarryException.commandException();
	}

	public ArrayList<String> extractComponents(String command) {
		ArrayList<String> components = new ArrayList<>();
		Matcher matcher = pattern.matcher(command);

		if (matcher.matches()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				components.add(matcher.group(i).trim());
			}
		}
		return components;
	}
}
