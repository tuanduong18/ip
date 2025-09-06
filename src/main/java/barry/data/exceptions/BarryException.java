package barry.data.exceptions;

import barry.data.common.CommandType;

/**
 * Application-specific exception type for Barry.Barry.
 * <p>
 * {@code BarryException} centralizes construction of user-facing error messages
 * for invalid commands, missing arguments, malformed timestamps, out-of-range
 * indices, and I/O-related issues. Prefer using the provided static factory
 * methods to ensure consistent wording across the application.
 * </p>
 */
public class BarryException extends Exception {
    /**
     * Creates a {@code BarryException} with the given message.
     *
     * @param message the human-readable error message
     */
    public BarryException(String message) {
        super(message);
    }

    /**
     * Returns a {@code BarryException} indicating that the input command is invalid.
     * <p>
     * The message suggests using {@code help} or {@code help --details} to discover
     * the list of supported commands.
     * </p>
     *
     * @return an exception describing an invalid command
     */
    public static BarryException commandException() {
        return new BarryException("Invalid command, Type 'help' or 'help --details' for more information");
    }

    /**
     * Returns a {@code BarryException} for an invalid command that closely matches
     * one or more known command types.
     * <p>
     * The message includes suggested formulas for the provided command types to guide
     * the user toward the correct syntax.
     * </p>
     *
     * @param cmds candidate command types that may match the user's intent
     * @return an exception describing an invalid command with suggestions
     * @see CommandType
     */
    public static BarryException commandException(CommandType[] cmds) {
        StringBuilder s = new StringBuilder("Invalid command. Perhaps, you are mentioning one of these below:\n");
        for (CommandType cmd : cmds) {
            s.append("\t");
            s.append(cmd.getFormula());
            s.append("\n");
        }
        s.append("Type 'help' or 'help --details' for more information about the valid commands");
        return new BarryException(s.toString());
    }

    /**
     * Returns a {@code BarryException} indicating a missing task description.
     *
     * @param c the command type for which a non-empty description is required
     * @return an exception describing a missing task description
     * @see CommandType
     */
    public static BarryException missingTaskDescription(CommandType c) {
        return new BarryException("The description of a " + c.getType() + " cannot be empty.");
    }

    /**
     * Returns a {@code BarryException} indicating that a required timestamp-like
     * attribute is missing (e.g., {@code /by}, {@code /from}, {@code /to}).
     *
     * @param c    the command type requiring the attribute
     * @param attr the human-readable name of the missing attribute
     * @return an exception describing the missing attribute
     * @see CommandType
     */
    public static BarryException missingTimestamp(CommandType c, String attr) {
        return new BarryException("Missing the " + attr + " of the " + c.getType());
    }

    /**
     * Returns a {@code BarryException} indicating that the provided task index
     * is out of the valid range.
     *
     * @param total the total number of tasks currently available
     * @return an exception describing the valid index range
     */
    public static BarryException taskNotFound(int total) {
        return new BarryException("Invalid index, task's index should be an int between 0 and " + (total + 1));
    }

    /**
     * Returns a {@code BarryException} indicating that a timestamp-like argument
     * does not conform to the expected format.
     *
     * @param c    the command type whose argument is invalid
     * @param s    the argument name (e.g., {@code "/by"}, {@code "/from"}, {@code "/to"})
     * @param regx the expected format description or regex
     * @return an exception describing the invalid time format
     * @see CommandType
     */
    public static BarryException invalidTimestamp(CommandType c, String s, String regx) {
        return new BarryException("Invalid time format of the " + c.getType() + "'s " + s + ". It should be " + regx);
    }

    /**
     * Returns a {@code BarryException} indicating that the provided source file path
     * is invalid or not usable.
     *
     * @return an exception describing an invalid source file path
     */
    public static BarryException invalidSourceFilePath() {
        return new BarryException("Invalid source file path");
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BarryException) {
            return o.toString().equals(this.toString());
        }
        return false;
    }
}
