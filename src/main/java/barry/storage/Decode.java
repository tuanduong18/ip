package barry.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import barry.data.common.Formats;
import barry.data.exceptions.BarryException;
import barry.tasks.Deadline;
import barry.tasks.Event;
import barry.tasks.Task;
import barry.tasks.Todo;

/**
 * Decodes a single persisted task record into a concrete {@link Task}.
 * <p>
 * The input line is expected to be pipe-delimited with spaces on both sides, using one of:
 * </p>
 * <ul>
 *   <li><b>Todo</b>: {@code T | <0|1> | <description>}</li>
 *   <li><b>Deadline</b>: {@code D | <0|1> | <description> | <dd/MM/yyyy HH:mm>}</li>
 *   <li><b>Event</b>: {@code E | <0|1> | <description> | <dd/MM/yyyy HH:mm> | <dd/MM/yyyy HH:mm>}</li>
 * </ul>
 * <p>
 * A value of {@code "1"} marks the task as done; {@code "0"} marks it as not done.
 * Timestamps are parsed with the pattern {@code dd/MM/yyyy HH:mm}.
 * </p>
 *
 * <h3>Examples</h3>
 * <pre>{@code
 * T | 1 | Read book
 * D | 0 | iP | 30/08/2025 16:00
 * E | 1 | Splashdown | 27/08/2025 18:00 | 27/08/2025 21:00
 * }</pre>
 */
public class Decode {
    private static final String SEP = " \\| ";

    private static final char TODO = 'T';
    private static final char DEADLINE = 'D';
    private static final char EVENT = 'E';

    private static final DateTimeFormatter STORED_FMT =
            DateTimeFormatter.ofPattern(Formats.CMD_DATETIME);
    /**
     * Parses a persisted record line into a {@link Task} and applies its completion status.
     * <p>
     * The first character determines the task subtype ({@code T}, {@code D}, or {@code E}).
     * The line is then split into fields, date values (if any) are parsed using
     * {@link Formats#CMD_DATETIME}, and the corresponding {@link Task} implementation is created.
     * </p>
     *
     * @param content the persisted record line (e.g., {@code "D | 1 | iP | 30/08/2025 16:00"})
     * @return a {@link Todo}, {@link Deadline}, or {@link Event} representing the decoded record
     * @throws BarryException if the type code is unknown, the record structure is malformed,
     *                        or a timestamp cannot be parsed
     */
    public static Task decode(String content) throws BarryException {
        if (content == null || content.isBlank()) {
            throw BarryException.invalidSourceFilePath();
        }
        char type = content.charAt(0);
        try {
            switch (type) {
            case TODO:
                return decodeTodo(content);
            case DEADLINE:
                return decodeDeadline(content);
            case EVENT:
                return decodeEvent(content);
            default:
                assert false : "unreachable: unknown record type " + type;
                throw new BarryException("Invalid data source");
            }
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            // Should not go to this line
            throw BarryException.invalidSourceFilePath();
        }
    }

    /**
     * Decodes a {@code Todo} record of the form {@code T | <0|1> | <description>}.
     *
     * @param content the persisted record line
     * @return a populated {@link Todo} with isDone applied
     */
    private static Task decodeTodo(String content) {
        String[] cmd = content.split(SEP, 3);
        String marked = cmd[1];
        String name = cmd[2];
        Task t = new Todo(name);
        t.setIsDone(marked.equals("1"));
        return t;
    }

    /**
     * Decodes a {@code Deadline} record of the form
     * {@code D | <0|1> | <description> | <dd/MM/yyyy HH:mm>}.
     *
     * @param content the persisted record line
     * @return a populated {@link Deadline} with isDone applied
     * @throws java.time.format.DateTimeParseException if the timestamp is not in the expected format
     */
    private static Task decodeDeadline(String content) {
        String[] cmd = content.split(SEP, 4);
        String marked = cmd[1];
        String name = cmd[2];
        String by = cmd[3];
        LocalDateTime due = LocalDateTime.parse(by, STORED_FMT);
        Task t = new Deadline(name, due);
        t.setIsDone(marked.equals("1"));
        return t;
    }

    /**
     * Decodes an {@code Event} record of the form
     * {@code E | <0|1> | <description> | <start> | <end>} where timestamps use
     * {@link Formats#CMD_DATETIME}.
     *
     * @param content the persisted record line
     * @return a populated {@link Event} with isDone applied
     * @throws java.time.format.DateTimeParseException if either timestamp is not in the expected format
     */
    private static Task decodeEvent(String content) {
        String[] cmd = content.split(SEP, 5);
        String marked = cmd[1];
        String name = cmd[2];
        LocalDateTime start = LocalDateTime.parse(cmd[3], STORED_FMT);
        LocalDateTime end = LocalDateTime.parse(cmd[4], STORED_FMT);
        Task t = new Event(name, start, end);
        t.setIsDone(marked.equals("1"));
        return t;
    }
}
