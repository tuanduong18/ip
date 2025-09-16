package barry.parser;

import java.util.ArrayList;

import barry.alias.AliasExpander;
import barry.alias.AliasStorage;
import barry.commands.AddCommand;
import barry.commands.Command;
import barry.commands.DeleteCommand;
import barry.commands.ExitCommand;
import barry.commands.FindCommand;
import barry.commands.HelpCommand;
import barry.commands.ListCommand;
import barry.commands.MarkCommand;
import barry.data.common.CommandRegex;
import barry.data.exceptions.BarryException;
import barry.tasks.Task;

/**
 * Parses raw user input into executable {@link Command} objects.
 * <p>
 * {@code CommandParser} acts as a thin coordinator:
 * it first identifies the command shape via {@link CommandRegex#parseCommand(String)},
 * then extracts any parameters and constructs the appropriate concrete command
 * (e.g., {@link AddCommand}, {@link MarkCommand}, {@link DeleteCommand}, etc.).
 * This class is stateless and thread-unsafe by default.
 * </p>
 */
public class CommandParser {
    private final AliasStorage aliasStorage = new AliasStorage();
    private final AliasExpander aliasExpander = new AliasExpander(aliasStorage);

    public CommandParser() {
        aliasStorage.load();
    }

    /**
     * Parses a full command line and returns the corresponding {@link Command}.
     * <p>
     * The method:
     * <ol>
     *   <li>Resolves the command pattern with {@link CommandRegex#parseCommand(String)}.</li>
     *   <li>Extracts parameters using {@link CommandRegex#extractComponents(String)}.</li>
     *   <li>Dispatches to helper methods that instantiate the concrete command.</li>
     * </ol>
     * Examples:
     * <ul>
     *   <li>{@code "todo Read book"} → {@link AddCommand}</li>
     *   <li>{@code "mark 2"} → {@link MarkCommand}</li>
     *   <li>{@code "find book"} → {@link FindCommand}</li>
     *   <li>{@code "help --details"} → {@link HelpCommand}</li>
     * </ul>
     * </p>
     *
     * @param fullCommand the raw, full user input line
     * @return a concrete {@link Command} ready for execution
     * @throws BarryException if the input does not match any known command
     *                        or required arguments are missing/invalid
     */
    public Command parseCommand(String fullCommand) throws BarryException {
        String expanded = aliasExpander.expand(fullCommand);
        CommandRegex c = CommandRegex.parseCommand(expanded); // Can throw BarryException
        ArrayList<String> params = c.extractComponents(expanded);
        switch (c) {
        case TODO, DEADLINE, EVENT:
            return addTask(expanded);
        case MARK, UNMARK:
            assert params.size() >= 2 : "mark/unmark requires an index";
            return markTask(params.get(0), params.get(1));
        case DELETE:
            assert params.size() == 2 : "delete requires exactly one index";
            return deleteTask(params.get(1));
        case LIST:
            return listTask();
        case FIND:
            assert params.size() == 2 : "find requires a pattern";
            return findTask(params.get(1));
        case BYE:
            return new ExitCommand();
        case HELP:
            return help(expanded);
        default:
            throw BarryException.commandException();
        }
    }

    /**
     * Builds an {@link AddCommand} by delegating to {@link TaskParser} to parse
     * the task payload from the raw input.
     *
     * @param command the full add-type command (e.g., {@code "todo ..."}, {@code "deadline ..."})
     * @return a new {@link AddCommand} wrapping the parsed {@link Task}
     * @throws BarryException if the task payload is invalid or missing fields
     */
    public Command addTask(String command) throws BarryException {
        Task temp = TaskParser.parseTask(command);
        return new AddCommand(temp);
    }

    /**
     * Builds a {@link MarkCommand} for {@code mark} or {@code unmark}.
     * <p>
     * The {@code type} should be either {@code "mark"} or {@code "unmark"} and the position
     * is the 1-based task index as a string. Number format errors are not expected here
     * because the input should have been validated by {@link CommandRegex}.
     * </p>
     *
     * @param type     the verb indicating mark/unmark
     * @param position the task index as a string
     * @return a new {@link MarkCommand} with the parsed index and action
     * @throws BarryException if the index cannot be parsed (unexpected) or is invalid
     */
    public Command markTask(String type, String position) throws BarryException {
        try {
            int id = Integer.parseInt(position);
            return new MarkCommand(id, type.equals("mark"));
        } catch (NumberFormatException e) {
            // Should not go to this line
            throw BarryException.commandException();
        }
    }

    /**
     * Creates a {@link ListCommand} that lists all tasks.
     *
     * @return a new {@link ListCommand}
     */
    public Command listTask() {
        return new ListCommand();
    }

    /**
     * Builds a {@link DeleteCommand} for the given position.
     * <p>
     * Number format errors are not expected here because the input should have been
     * validated by {@link CommandRegex}.
     * </p>
     *
     * @param position the task index to delete, as a string
     * @return a new {@link DeleteCommand} for the parsed index
     * @throws BarryException if the index cannot be parsed (unexpected) or is invalid
     */
    public Command deleteTask(String position) throws BarryException {
        try {
            int id = Integer.parseInt(position);
            return new DeleteCommand(id);
        } catch (NumberFormatException e) {
            // Should not go to this line
            throw BarryException.commandException();
        }
    }

    /**
     * Builds a {@link HelpCommand} based on the raw input string.
     * <p>
     * If the command equals {@code "help --details"}, the detailed flag is set; otherwise
     * a concise help is returned.
     * </p>
     *
     * @param command either {@code "help"} or {@code "help --details"}
     * @return a new {@link HelpCommand} with the appropriate verbosity flag
     */
    public Command help(String command) {
        return new HelpCommand(command.equals("help --details"));
    }

    /**
     * Builds a {@link FindCommand} that searches for tasks whose descriptions
     * contain the given pattern (case-insensitive).
     *
     * @param pattern the substring to search for
     * @return a new {@link FindCommand} configured with the pattern
     */
    public Command findTask(String pattern) {

        return new FindCommand(pattern);
    }
}
