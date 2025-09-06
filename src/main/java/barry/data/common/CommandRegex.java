package barry.data.common;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import barry.data.exceptions.BarryException;

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
    DEADLINE("deadline", Pattern.compile("deadline (.*) /by (.*)")),
    EVENT("event", Pattern.compile("event (.*) /from (.*) /to (.*)")),
    MARK("mark", Pattern.compile("mark ([0-9]+)")),
    UNMARK("unmark", Pattern.compile("unmark ([0-9]+)")),
    LIST("list", Pattern.compile("list")),
    DELETE("delete", Pattern.compile("delete ([0-9]+)")),
    FIND("find", Pattern.compile("find (.*)")),
    HELP("help", Pattern.compile("help( --details|)")),
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
     * Resolves the given input line to a {@link CommandRegex} by tag and full-pattern match.
     * <p>
     * Steps:
     * <ol>
     *   <li>Extract the first whitespace-delimited token of {@code command} as the tag.</li>
     *   <li>Iterate over enum constants; for those whose {@code tag} equals the extracted tag,
     *       return the first whose pattern <em>fully</em> matches the entire input
     *       (via {@link java.util.regex.Matcher#matches()}).</li>
     *   <li>If the tag is recognized but none of its patterns match the full input,
     *       throw a {@link BarryException} that suggests the corresponding {@link CommandType}.</li>
     *   <li>If no enum constant shares the tag (i.e., the tag is unknown), throw a generic
     *       invalid-command {@link BarryException}.</li>
     * </ol>
     * </p>
     *
     * @param command the complete user input line to parse
     * @return the matching {@code CommandRegex} enum constant
     * @throws BarryException if the tag is unknown, or if the tag is known but the input does not
     *                        satisfy any expected pattern for that tag
     */
    public static CommandRegex parseCommand(String command) throws BarryException {
        String commandTag = command.split(" ")[0];
        for (CommandRegex t : CommandRegex.values()) {
            if (commandTag.equals(t.tag)) {
                if (t.pattern.matcher(command).matches()) {
                    return t;
                } else {
                    CommandType c = CommandType.parseCommand(commandTag);
                    throw BarryException.commandException(new CommandType[]{c});
                }
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
     *     are the trimmed contents of each capturing group (if any)
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
