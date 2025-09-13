package barry.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import barry.data.common.Formats;

/**
 * Encodes a task’s <em>display string</em> (as produced by {@code Task.toString()})
 * into the canonical, pipe-delimited storage format used by the app.
 * <p>
 * This class is the logical inverse of {@link Decode}:
 * it parses the UI-facing representation to extract the task type, completion status,
 * description, and (if any) timestamps, and then emits a single storage line
 * terminated with {@link System#lineSeparator()}.
 * </p>
 *
 * <h3>Expected input (examples of {@code Task.toString()})</h3>
 * <ul>
 *   <li>Todo: {@code [T][X] Read book}</li>
 *   <li>Deadline: {@code [D][ ] iP (by: 4:00 PM 30 Aug, 2025)}</li>
 *   <li>Event: {@code [E][X] Splashdown (from: 6:00 PM 27 Aug, 2025 to: 9:00 PM 27 Aug, 2025)}</li>
 * </ul>
 *
 * <h3>Output storage format</h3>
 * <ul>
 *   <li>Todo: {@code T | 1 | Read book}</li>
 *   <li>Deadline: {@code D | 0 | iP | 30/08/2025 16:00}</li>
 *   <li>Event: {@code E | 1 | Splashdown | 27/08/2025 18:00 | 27/08/2025 21:00}</li>
 * </ul>
 *
 * <h3>Date/time handling</h3>
 * <ul>
 *   <li>Appearance (input) format: {@link Formats#UI_DATETIME} (e.g., {@code 4:00 PM 30 Aug, 2025})</li>
 *   <li>Stored (output) format: {@link Formats#CMD_DATETIME} (e.g., {@code 30/08/2025 16:00})</li>
 * </ul>
 *
 * <p><b>Note:</b> Methods in this class assume the input strictly follows
 * the current {@code Task.toString()} layout. They are not defensive and may throw
 * runtime exceptions if the structure deviates.</p>
 *
 * @see Decode
 */
public class Encode {

    private static final int TYPE_POS = 1; // '[T]'
    private static final int MARK_POS = 4; // '[X]' or '[ ]'
    private static final int CONTENT_START = 7;
    private static final String SEP = " | ";

    private static final char TODO = 'T';
    private static final char DEADLINE = 'D';
    private static final char EVENT = 'E';

    private static final DateTimeFormatter UI_FMT =
            DateTimeFormatter.ofPattern(Formats.UI_DATETIME);
    private static final DateTimeFormatter STORED_FMT =
            DateTimeFormatter.ofPattern(Formats.CMD_DATETIME);
    /**
     * Converts a task’s display string into its persisted line format.
     * <p>
     * Dispatches to a type-specific encoder based on the bracketed type marker
     * in the display string (e.g., {@code [T]}, {@code [D]}, {@code [E]}).
     * </p>
     *
     * @param task the UI-facing {@code Task.toString()} output to encode
     * @return a pipe-delimited storage line, ending with {@link System#lineSeparator()}
     */
    public static String encode(String task) {
        char type = task.charAt(TYPE_POS);

        switch (type) {
        case TODO:
            return encodeTodo(task);
        case DEADLINE:
            return encodeDeadline(task);
        case EVENT:
            return encodeEvent(task);
        default:
            //Should not reach this line
            return "";
        }
    }

    /**
     * Encodes a {@code Todo} display string of the form {@code [T][X] <description>}.
     *
     * @param task the {@code toString()} output of a Todo
     * @return {@code T | <0|1> | <description>} plus a trailing line separator
     */
    private static String encodeTodo(String task) {
        StringBuilder s = new StringBuilder();
        char type = task.charAt(TYPE_POS);
        boolean marked = task.charAt(MARK_POS) == 'X';
        String content = task.substring(CONTENT_START);
        s.append(type);
        s.append(SEP);
        s.append(marked ? "1" : "0");
        s.append(SEP);
        s.append(content);
        s.append(System.lineSeparator());
        return s.toString();
    }

    /**
     * Encodes a {@code Deadline} display string of the form
     * {@code [D][ ] <description> (by: <h:mm a d MMM, yyyy>)}.
     *
     * @param task the {@code toString()} output of a Deadline
     * @return {@code D | <0|1> | <description> | <dd/MM/yyyy HH:mm>} plus a trailing line separator
     */
    private static String encodeDeadline(String task) {
        StringBuilder s = new StringBuilder();
        char type = task.charAt(TYPE_POS);
        boolean marked = task.charAt(MARK_POS) == 'X';
        String content = task.substring(CONTENT_START);
        String[] details = content.split(" \\(by: ", 2);
        s.append(type);
        s.append(SEP);
        s.append(marked ? "1" : "0");
        s.append(SEP);
        s.append(details[0]);
        s.append(SEP);
        String due = details[1].substring(0, details[1].length() - 1);
        LocalDateTime dueParsed = LocalDateTime.parse(due, UI_FMT);
        due = dueParsed.format(STORED_FMT);
        s.append(due);
        s.append(System.lineSeparator());
        return s.toString();
    }

    /**
     * Encodes an {@code Event} display string of the form
     * {@code [E][X] <description> (from: <h:mm a d MMM, yyyy> to: <h:mm a d MMM, yyyy>)}.
     *
     * @param task the {@code toString()} output of an Event
     * @return {@code E | <0|1> | <description> | <start> | <end>} where dates are in
     *         {@link Formats#CMD_DATETIME}, plus a trailing line separator
     */
    private static String encodeEvent(String task) {
        StringBuilder s = new StringBuilder();
        char type = task.charAt(TYPE_POS);
        boolean marked = task.charAt(MARK_POS) == 'X';
        String content = task.substring(CONTENT_START);
        String name = content.split(" \\(from: ", 2)[0];
        String start = content.split(" \\(from: ", 2)[1].split(" to: ", 2)[0];
        String end = content.split(" \\(from: ", 2)[1].split(" to: ", 2)[1];
        end = end.substring(0, end.length() - 1);
        start = LocalDateTime.parse(start, UI_FMT).format(STORED_FMT);
        end = LocalDateTime.parse(end, UI_FMT).format(STORED_FMT);
        s.append(type);
        s.append(SEP);
        s.append(marked ? "1" : "0");
        s.append(SEP);
        s.append(name);
        s.append(SEP);
        s.append(start);
        s.append(SEP);
        s.append(end);
        s.append(System.lineSeparator());
        return s.toString();
    }
}
