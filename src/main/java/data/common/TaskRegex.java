package data.common;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.exceptions.BarryException;

/**
 * Defines full-line regular expressions for task-creation commands and utilities
 * to parse and extract their components.
 * <p>
 * Supported forms are:
 * <ul>
 *   <li><b>TODO</b>: {@code todo {description}}</li>
 *   <li><b>DEADLINE</b>: {@code deadline {description} /by {datetime}}</li>
 *   <li><b>EVENT</b>: {@code event {description} /from {datetime} /to {datetime}}</li>
 * </ul>
 * Each enum constant stores a compiled {@link Pattern} that must match the entire
 * input (via {@link Matcher#matches()}), ensuring exact—not partial—matches.
 * Capturing groups correspond to the placeholders in order. If a group matches an
 * empty string, the extracted component will be {@code ""} (after {@link String#trim()}).
 * </p>
 */
public enum TaskRegex {
    TODO(Pattern.compile("todo ([^|]*)")),
    DEADLINE(Pattern.compile("deadline ([^|]*) /by ([^|]*)")),
    EVENT(Pattern.compile("event ([^|]*) /from ([^|]*) /to ([^|]*)"));

    private final Pattern pattern;

    /**
     * Creates a {@code TaskRegex} bound to a compiled, full-line pattern.
     *
     * @param pattern the compiled regex for this task syntax
     */
    TaskRegex(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Determines which task pattern (TODO, DEADLINE, EVENT) matches the given input.
     * <p>
     * Iterates over all enum constants and returns the first whose pattern fully matches
     * the {@code command}. If none match, a {@link BarryException} is thrown.
     * </p>
     *
     * @param command the raw task command line (e.g., {@code "deadline CS2103T /by 30/08/2025 16:00"})
     * @return the {@code TaskRegex} variant that matches the input
     * @throws BarryException if the input does not conform to any supported task pattern
     */
    public static TaskRegex parseTask(String command) throws BarryException {
        for (TaskRegex t : TaskRegex.values()) {
            if (t.pattern.matcher(command).matches()) {
                return t;
            }
        }
        throw BarryException.commandException();
    }

    /**
     * Extracts the captured components from the input according to this pattern.
     * <p>
     * On a full match, returns a list containing, in order:
     * <ol>
     *   <li>{@code description} (for all types)</li>
     *   <li>{@code /by datetime} (for DEADLINE)</li>
     *   <li>{@code /from datetime}, {@code /to datetime} (for EVENT)</li>
     * </ol>
     * Whitespace around each captured group is trimmed. If a group matches nothing,
     * the corresponding element is the empty string. If the input does not match,
     * an empty list is returned.
     * </p>
     *
     * @param command the raw task command line to extract from
     * @return an {@link ArrayList} of trimmed capturing-group values in positional order
     */
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
