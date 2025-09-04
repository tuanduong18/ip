package data.common;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.exceptions.BarryException;

public enum TaskRegex {
	TODO(Pattern.compile("todo ([^|]*)")),
	DEADLINE(Pattern.compile("deadline ([^|]*) /by ([^|]*)")),
	EVENT(Pattern.compile("event ([^|]*) /from ([^|]*) /to ([^|]*)"));

	private final Pattern pattern;

	TaskRegex(Pattern pattern) {
		this.pattern = pattern;
	}

	public static TaskRegex parseTask(String command) throws BarryException {
		for (TaskRegex t : TaskRegex.values()) {
			if (t.pattern.matcher(command).matches()) {
				return t;
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