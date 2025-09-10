package barry.parser;

import static barry.data.common.CommandType.DEADLINE;
import static barry.data.common.CommandType.EVENT;
import static barry.data.common.CommandType.TODO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import barry.data.common.Formats;
import barry.data.common.TaskRegex;
import barry.data.exceptions.BarryException;
import barry.tasks.Deadline;
import barry.tasks.Event;
import barry.tasks.Task;
import barry.tasks.Todo;

/**
 * Parses raw task-creation commands into concrete {@link Task} instances.
 * <p>
 * The {@code TaskParser} delegates syntax recognition to {@link TaskRegex},
 * validates required fields, enforces date formats, and constructs the
 * appropriate {@link Todo}, {@link Deadline}, or {@link Event}.
 * Command timestamps must use the {@code dd/MM/yyyy HH:mm} pattern
 * (see {@link Formats#CMD_DATETIME}).
 * </p>
 *
 * <h3>Supported forms</h3>
 * <ul>
 *   <li>{@code todo &lt;description&gt;}</li>
 *   <li>{@code deadline &lt;description&gt; /by &lt;dd/MM/yyyy HH:mm&gt;}</li>
 *   <li>{@code event &lt;description&gt; /from &lt;dd/MM/yyyy HH:mm&gt; /to &lt;dd/MM/yyyy HH:mm&gt;}</li>
 * </ul>
 *
 * <p>
 * If required components are missing or timestamps are malformed, a
 * {@link BarryException} with a user-friendly message is thrown.
 * </p>
 */
public class TaskParser {

    /**
     * Formatter for command timestamps (e.g., {@code 30/08/2025 16:00}).
     */
    private static final DateTimeFormatter CMD_FMT = DateTimeFormatter.ofPattern(Formats.CMD_DATETIME);

    /**
     * Parses a full task command line and produces a concrete {@link Task}.
     * <p>
     * Steps:
     * <ol>
     *   <li>Identify the task pattern via {@link TaskRegex#parseTask(String)}.</li>
     *   <li>Extract components (description and timestamps) using
     *       {@link TaskRegex#extractComponents(String)}.</li>
     *   <li>Dispatch to a type-specific parser that validates inputs and builds the task.</li>
     * </ol>
     * </p>
     *
     * @param command the raw task command (e.g., {@code "deadline iP /by 30/08/2025 16:00"})
     * @return a {@link Todo}, {@link Deadline}, or {@link Event} created from the input
     * @throws BarryException if the command does not match a supported pattern,
     *                        a required field is missing, or a timestamp is invalid
     */
    public static Task parseTask(String command) throws BarryException {
        TaskRegex t = TaskRegex.parseTask(command); // may throw BarryException
        ArrayList<String> p = t.extractComponents(command);

        return switch (t) {
        case TODO -> parseTodo(p);
        case DEADLINE -> parseDeadline(p);
        case EVENT -> parseEvent(p);
        };
    }

    /**
     * Builds a {@link Todo} from extracted components.
     *
     * @param p the extracted components; {@code p.get(0)} is the description
     * @return a new {@link Todo}
     * @throws BarryException if the description is empty
     */
    private static Task parseTodo(ArrayList<String> p) throws BarryException {
        String desc = p.get(0).trim();
        if (desc.isEmpty()) {
            throw BarryException.missingTaskDescription(TODO);
        }
        return new Todo(desc);
    }

    /**
     * Builds a {@link Deadline} from extracted components.
     *
     * @param p the extracted components; {@code p.get(0)} is description, {@code p.get(1)} is {@code /by}
     * @return a new {@link Deadline}
     * @throws BarryException if the description is empty, due date is missing, or the date is invalid
     */
    private static Task parseDeadline(ArrayList<String> p) throws BarryException {
        String desc = p.get(0).trim();
        String by = p.get(1).trim();

        if (desc.isEmpty()) {
            throw BarryException.missingTaskDescription(DEADLINE);
        }
        if (by.isEmpty()) {
            throw BarryException.missingTimestamp(DEADLINE, "due date");
        }

        LocalDateTime due = parseDateStrict(by, DEADLINE, "due date");
        return new Deadline(desc, due);
    }

    /**
     * Builds an {@link Event} from extracted components.
     *
     * @param p the extracted components; {@code p.get(0)} is description,
     *          {@code p.get(1)} is {@code /from}, {@code p.get(2)} is {@code /to}
     * @return a new {@link Event}
     * @throws BarryException if the description is empty, a timestamp is missing, or a date is invalid
     */
    private static Task parseEvent(ArrayList<String> p) throws BarryException {
        String desc = p.get(0).trim();
        String from = p.get(1).trim();
        String to = p.get(2).trim();

        if (desc.isEmpty()) {
            throw BarryException.missingTaskDescription(EVENT);
        }
        if (from.isEmpty()) {
            throw BarryException.missingTimestamp(EVENT, "starting time");
        }
        if (to.isEmpty()) {
            throw BarryException.missingTimestamp(EVENT, "ending time");
        }

        LocalDateTime start = parseDateStrict(from, EVENT, "start time");
        LocalDateTime end = parseDateStrict(to, EVENT, "end time");
        return new Event(desc, start, end);
    }

    /**
     * Parses a timestamp using the strict command format and maps parse failures
     * to a {@link BarryException} with a helpful message.
     *
     * @param raw   the raw timestamp text (e.g., {@code 30/08/2025 16:00})
     * @param type  the command type used for error messaging context
     * @param label a label describing the timestamp’s role (e.g., {@code "due date"})
     * @return the parsed {@link LocalDateTime}
     * @throws BarryException if the timestamp is not in {@link Formats#CMD_DATETIME} format
     */
    private static LocalDateTime parseDateStrict(String raw, barry.data.common.CommandType type, String label)
            throws BarryException {
        try {
            return LocalDateTime.parse(raw, CMD_FMT);
        } catch (DateTimeParseException e) {
            throw BarryException.invalidTimestamp(type, label, Formats.CMD_DATETIME);
        }
    }

}
