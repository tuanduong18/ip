package data.common;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.exceptions.BarryException;

/**
 * Defines the set of command tags and their full-line regular expressions
 * for parsing user input.
 * <p>
 * Each enum constant pairs a command {@code tag} (the first token in the input)
 * with a compiled {@link Pattern} that is used to validate and extract arguments
 * from the complete command line via {@link Matcher#matches() matches()}.
 * Some commands share the same tag but have different patterns (e.g.,
 * {@code HELP} and {@code DETAILED_HELP} both use {@code "help"}), allowing
 * multiple syntaxes under a single verb.
 * </p>
 */
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

    /**
     * Creates a {@code CommandRegex} with the given {@code tag} and compiled {@code pattern}.
     *
     * @param tag     the leading token used to identify the command (e.g., {@code "todo"})
     * @param pattern the compiled regular expression that must match the entire input line
     */
    CommandRegex(String tag, Pattern pattern) {
        this.tag = tag;
        this.pattern = pattern;
    }

    /**
     * Parses a raw input line and returns the matching {@code CommandRegex}.
     * <p>
     * The first whitespace-delimited token of {@code command} is treated as the command tag.
     * The method then selects enum entries with the same tag and returns the first whose
     * pattern fully matches the input (using {@link Matcher#matches()}).
     * If no such entry exists, a {@link BarryException} is thrown. When the tag maps to a
     * known {@code CommandType} but the remainder does not satisfy any expected pattern,
     * the thrown exception may include that type to aid error messaging.
     * </p>
     *
     * @param command the complete user input line to parse
     * @return the matching {@code CommandRegex} enum constant
     * @throws BarryException if the input does not match any known command pattern
     */
    public static CommandRegex parseCommand(String command) throws BarryException {
        String commandTag = command.split(" ")[0];
        for (CommandRegex t : CommandRegex.values()) {
            if (commandTag.equals(t.tag)) {
                if (t.pattern.matcher(command).matches()) {
                    return t;
                }
            } else {
                CommandType c = CommandType.parseCommand(commandTag);
                throw BarryException.commandException(new CommandType[]{c});
            }
        }
        throw BarryException.commandException();
    }

    /**
     * Extracts structured components from {@code command} according to this entry's pattern.
     * <p>
     * The returned list always includes the command {@code tag} as the first element.
     * If the input fully matches the pattern, subsequent elements correspond to each
     * capturing group in order (from group 1 to {@link Matcher#groupCount()}), with
     * leading and trailing whitespace trimmed. If a capturing group matches an empty
     * string, the corresponding element will be {@code ""}. If the input does not
     * match the pattern, only the {@code tag} is returned.
     * </p>
     *
     * @param command the complete user input line to extract from
     * @return an {@link ArrayList} where index 0 is the command tag and subsequent indices
     * are the trimmed contents of each capturing group (if any)
     */
    public ArrayList<String> extractComponents(String command) {
        ArrayList<String> components = new ArrayList<>();
        Matcher matcher = pattern.matcher(command);
        components.add(this.tag);
        if (matcher.matches()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                components.add(matcher.group(i).trim());
            }
        }
        return components;
    }
}
