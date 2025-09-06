package barry.data.common;

import barry.data.exceptions.BarryException;

/**
 * Represents the set of supported command types and their usage metadata.
 * <p>
 * Each enum constant encapsulates:
 * <ul>
 *   <li><b>type</b> – the command keyword (first token, e.g., {@code "todo"})</li>
 *   <li><b>formula</b> – the canonical usage with placeholders
 *   (e.g., {@code "deadline {description} /by {datetime}"})</li>
 *   <li><b>example</b> – a concrete example demonstrating valid syntax</li>
 * </ul>
 * This enum is used for lightweight validation, help text generation, and mapping a tag
 * to a known command family.
 * </p>
 */
public enum CommandType {
    TODO("todo",
            "todo {description}",
            "todo Do the laundry"),
    DEADLINE("deadline",
            "deadline {description} /by {datetime}",
            "deadline CS2103T /by 30/08/2025 16:00"),
    EVENT("event",
            "event {description} /from {datetime} /to {datetime}",
            "event Orbital Splashdown /from 27/08/2025 18:00 /to 27/08/2025 21:00"),
    MARK("mark", "mark {id}", "mark 2"),
    UNMARK("unmark", "unmark {id}", "unmark 3"),
    LIST("list", "list", "list"),
    DELETE("delete", "delete {id}", "delete 2"),
    FIND("find", "find {description}", "find book"),
    BYE("bye", "bye", "bye"),
    HELP("help", "help", "help"),
    DETAILED_HELP("help --details", "help --details", "help --details");


    private final String type;
    private final String formula;
    private final String example;

    /**
     * Creates a {@code CommandType} with its display {@code type}, usage {@code formula},
     * and illustrative {@code example}.
     *
     * @param type    the command keyword (e.g., {@code "todo"})
     * @param formula the canonical usage with placeholders
     * @param example an example demonstrating valid syntax
     */
    CommandType(String type, String formula, String example) {
        this.type = type;
        this.formula = formula;
        this.example = example;
    }

    /**
     * Parses a command tag and returns the corresponding {@code CommandType}.
     * <p>
     * This method expects the first whitespace-delimited token (e.g., from user input)
     * and matches it exactly against the {@code type} of each enum constant.
     * </p>
     *
     * @param s the command tag to parse (e.g., {@code "todo"}, {@code "list"})
     * @return the matching {@code CommandType}
     * @throws BarryException if the tag does not correspond to any known command
     */
    public static CommandType parseCommand(String s) throws BarryException {
        for (CommandType c : CommandType.values()) {
            if (c.getType().equals(s)) {
                return c;
            }
        }
        throw BarryException.commandException();
    }

    /**
     * Builds a simple, human-readable listing of all supported command types.
     * <p>
     * The returned string contains one command per line, each prefixed with a tab.
     * </p>
     *
     * @return a formatted string enumerating all command tags
     */
    public static String allCommands() {
        StringBuilder s = new StringBuilder("");
        for (CommandType c : CommandType.values()) {
            s.append("\t");
            s.append(c.getType());
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * Builds a detailed usage guide for all commands, including their formulas and examples.
     * <p>
     * The returned string lists each command’s formula on one line (prefixed with a tab),
     * followed by an indented example line.
     * </p>
     *
     * @return a formatted string with each command’s formula and example
     */
    public static String allCommandsDetailed() {
        StringBuilder s = new StringBuilder("");
        for (CommandType c : CommandType.values()) {
            s.append("\t");
            s.append(c.getFormula());
            s.append("\n");
            s.append("\t\te.g.: ");
            s.append(c.getExample());
            s.append("\n\n");
        }
        return s.toString();
    }

    public String getType() {
        return type;
    }

    public String getFormula() {
        return formula;
    }

    public String getExample() {
        return example;
    }
}
