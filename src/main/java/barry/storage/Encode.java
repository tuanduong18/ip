package barry.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Encodes a task's display string (as produced by {@code Task.toString()})
 * into the canonical, pipe-delimited storage format.
 * <p>
 * This is the logical inverse of {@link Decode#decode(String)}:
 * it reads the UI-facing representation, extracts type, status, description,
 * and any timestamps, and emits a single storage line terminated with
 * {@link System#lineSeparator()}.
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
 *   <li>Appearance (input) format: {@code h:mm a d MMM, yyyy} (e.g., {@code 4:00 PM 30 Aug, 2025})</li>
 *   <li>Stored (output) format: {@code dd/MM/yyyy HH:mm} (e.g., {@code 30/08/2025 16:00})</li>
 * </ul>
 * <p>
 * <b>Note:</b> This method assumes the input string strictly follows the current
 * {@code Task.toString()} format. It does not perform defensive validation and may
 * throw runtime exceptions if the structure deviates.
 * </p>
 */
public class Encode {
    /**
     * Converts a task's display string into its persisted line format.
     *
     * @param task the UI-facing {@code Task.toString()} output to encode
     * @return a pipe-delimited storage line, ending with {@link System#lineSeparator()}
     */
    public static String encode(String task) {
        StringBuilder s = new StringBuilder();
        char type = task.charAt(1);
        assert type == 'T' || type == 'D' || type == 'E' : "unknown task type";
        boolean marked = task.charAt(4) == 'X';
        String command = task.substring(7);
        DateTimeFormatter appearanceFormatter = DateTimeFormatter.ofPattern("h:mm a d MMM, yyyy");
        DateTimeFormatter storedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        switch (type) {
        case 'T':
            s.append(type);
            s.append(" | ");
            s.append(marked ? "1" : "0");
            s.append(" | ");
            s.append(command);
            s.append(System.lineSeparator());
            break;
        case 'D':
            String[] details = command.split(" \\(by: ", 2);
            s.append(type);
            s.append(" | ");
            s.append(marked ? "1" : "0");
            s.append(" | ");
            s.append(details[0]);
            s.append(" | ");
            String due = details[1].substring(0, details[1].length() - 1);
            LocalDateTime dueParsed = LocalDateTime.parse(due, appearanceFormatter);
            due = dueParsed.format(storedFormatter);
            s.append(due);
            s.append(System.lineSeparator());
            break;
        case 'E':
            String name = command.split(" \\(from: ", 2)[0];
            String start = command.split(" \\(from: ", 2)[1].split(" to: ", 2)[0];
            String end = command.split(" \\(from: ", 2)[1].split(" to: ", 2)[1];
            end = end.substring(0, end.length() - 1);
            start = LocalDateTime.parse(start, appearanceFormatter).format(storedFormatter);
            end = LocalDateTime.parse(end, appearanceFormatter).format(storedFormatter);
            s.append(type);
            s.append(" | ");
            s.append(marked ? "1" : "0");
            s.append(" | ");
            s.append(name);
            s.append(" | ");
            s.append(start);
            s.append(" | ");
            s.append(end);
            s.append(System.lineSeparator());
            break;
        default:
            break;
        }
        return s.toString();
    }
}
