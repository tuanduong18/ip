package barry.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    /**
     * Decodes a pipe-delimited record into a {@link Task} and applies its completion status.
     * <p>
     * The method splits the input into at most five fields (to preserve the trailing time field for events),
     * constructs the appropriate task subtype, and sets the done/undone status based on the second field.
     * </p>
     *
     * @param content the persisted record line (e.g., {@code "D | 1 | iP | 30/08/2025 16:00"})
     * @return a {@link Todo}, {@link Deadline}, or {@link Event} representing the decoded record
     * @throws BarryException if the line is malformed, contains an unknown type, or has an invalid date
     */
    public static Task decode(String content) throws BarryException {
        Task t;
        String[] cmd = content.split(" \\| ", 5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            String type = cmd[0];
            String marked = cmd[1];
            assert "0".equals(marked) || "1".equals(marked) : "status must be 0 or 1";
            String name = cmd[2];
            switch (type) {
            case "T":
                t = new Todo(name);
                t.setStatus(marked.equals("1"));
                break;
            case "D":
                String by = cmd[3];
                LocalDateTime due = LocalDateTime.parse(by, formatter);
                t = new Deadline(name, due);
                t.setStatus(marked.equals("1"));
                break;
            case "E":
                LocalDateTime start = LocalDateTime.parse(cmd[3], formatter);
                LocalDateTime end = LocalDateTime.parse(cmd[4], formatter);
                t = new Event(name, start, end);
                t.setStatus(marked.equals("1"));
                break;
            default:
                assert false : "unreachable: unknown record type " + type;
                throw new BarryException("Invalid data source");
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            // Should not go to this line
            throw BarryException.invalidSourceFilePath();
        }
        return t;
    }
}
