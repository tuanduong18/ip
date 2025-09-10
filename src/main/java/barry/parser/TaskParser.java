package barry.parser;

import static barry.data.common.CommandType.DEADLINE;
import static barry.data.common.CommandType.EVENT;
import static barry.data.common.CommandType.TODO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import barry.data.common.CommandType;
import barry.data.common.TaskRegex;
import barry.data.exceptions.BarryException;
import barry.tasks.Deadline;
import barry.tasks.Event;
import barry.tasks.Task;
import barry.tasks.Todo;

/**
 * Parses raw task-creation commands into concrete {@link Task} instances.
 * <p>
 * {@code TaskParser} delegates the syntactic recognition to {@link TaskRegex},
 * extracts the captured components (e.g., description and timestamps), validates
 * required fields, and constructs the appropriate {@link Todo}, {@link Deadline},
 * or {@link Event}.
 * </p>
 *
 * <h3>Supported forms</h3>
 * <ul>
 *   <li><b>TODO</b> — {@code todo {description}}</li>
 *   <li><b>DEADLINE</b> — {@code deadline {description} /by {dd/MM/yyyy HH:mm}}</li>
 *   <li><b>EVENT</b> — {@code event {description} /from {dd/MM/yyyy HH:mm} /to {dd/MM/yyyy HH:mm}}</li>
 * </ul>
 *
 * <h3>Validation &amp; errors</h3>
 * <ul>
 *   <li>Empty descriptions or missing timestamps result in a {@link BarryException} with a
 *       user-friendly message</li>
 *   <li>Timestamps must follow {@link #DATE_FORMAT} exactly; otherwise a {@link BarryException}
 *       describing the expected format is thrown.</li>
 *   <li>If the input does not match any supported task pattern, a generic invalid-command
 *       {@link BarryException} is thrown, optionally hinting valid task commands.</li>
 * </ul>
 *
 * <h3>Examples</h3>
 * <pre>{@code
 * Task t1 = TaskParser.parseTask("todo Read CS2103T notes");
 * Task t2 = TaskParser.parseTask("deadline iP /by 30/08/2025 16:00");
 * Task t3 = TaskParser.parseTask("event Splashdown /from 27/08/2025 18:00 /to 27/08/2025 21:00");
 * }</pre>
 */
public class TaskParser {
    /**
     * The expected timestamp pattern for {@code /by}, {@code /from}, and {@code /to} fields.
     * Example: {@code 30/08/2025 16:00}.
     */
    static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

    /**
     * Parses a full task command line into a concrete {@link Task}.
     * <p>
     * Steps:
     * <ol>
     *   <li>Determine the task type using {@link TaskRegex#parseTask(String)}.</li>
     *   <li>Extract the captured components in order (description, then any timestamps).</li>
     *   <li>Validate required components and timestamp formats via {@link DateTimeFormatter}.</li>
     *   <li>Instantiate and return the corresponding {@link Task} subtype.</li>
     * </ol>
     * </p>
     *
     * @param command the raw task command (e.g., {@code "deadline iP /by 30/08/2025 16:00"})
     * @return a {@link Todo}, {@link Deadline}, or {@link Event} constructed from the input
     * @throws BarryException if the input does not match any supported pattern,
     *                        required fields are missing, or timestamps are malformed
     */
    public static Task parseTask(String command) throws BarryException {
        TaskRegex t = TaskRegex.parseTask(command); // This line can throw Barry.Barry Exception
        ArrayList<String> params = t.extractComponents(command); // Extract parameters
        String description = params.get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        switch (t) {
        case TODO:
            assert params.size() == 1 : "todo has 1 component: description";
            if (description.trim().isEmpty()) {
                throw BarryException.missingTaskDescription(TODO);
            }
            return new Todo(description);
        case DEADLINE:
            assert params.size() == 2 : "deadline has 2 components: desc, /by";
            if (description.trim().isEmpty()) {
                throw BarryException.missingTaskDescription(DEADLINE);
            } else if (params.get(1).trim().isEmpty()) {
                throw BarryException.missingTimestamp(DEADLINE, "due date");
            }
            LocalDateTime due;
            try {
                due = LocalDateTime.parse(params.get(1).trim(), formatter);
            } catch (DateTimeParseException e) {
                throw BarryException.invalidTimestamp(DEADLINE, "due date", DATE_FORMAT);
            }
            return new Deadline(description, due);
        case EVENT:
            assert params.size() == 3 : "event has 3 components: desc, /from, /to";
            if (description.trim().isEmpty()) {
                throw BarryException.missingTaskDescription(EVENT);
            } else if (params.get(1).trim().isEmpty()) {
                throw BarryException.missingTimestamp(EVENT, "starting time");
            } else if (params.get(2).trim().isEmpty()) {
                throw BarryException.missingTimestamp(EVENT, "ending time");
            }
            LocalDateTime start;
            LocalDateTime end;
            try {
                start = LocalDateTime.parse(params.get(1), formatter);
            } catch (DateTimeParseException e) {
                throw BarryException.invalidTimestamp(EVENT, "start time", DATE_FORMAT);
            }
            try {
                end = LocalDateTime.parse(params.get(2), formatter);
            } catch (DateTimeParseException e) {
                throw BarryException.invalidTimestamp(EVENT, "end time", DATE_FORMAT);
            }
            return new Event(description, start, end);
        default:
            throw BarryException.commandException(new CommandType[]{TODO, DEADLINE, EVENT});
        }
    }
}
