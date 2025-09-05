package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event task with a start and end time.
 * <p>
 * An {@code Event} is a type of {@link Task} that has a duration,
 * defined by a {@link LocalDateTime} start and end. Its string
 * representation includes the start and end times in a readable format.
 * </p>
 */
public class Event extends Task {

    /**
     * The starting date and time of the event.
     */
    private final LocalDateTime start;

    /**
     * The ending date and time of the event.
     */
    private final LocalDateTime end;

    /**
     * Creates an {@code Event} with the specified description,
     * start date/time, and end date/time.
     *
     * @param description the description of the event
     * @param start       the start date and time of the event
     * @param end         the end date and time of the event
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns a string representation of the event, showing its type,
     * completion status, description, and time range.
     * <p>
     * The format is:
     * <pre>
     * [E][ ] Event description (from: 6:00 PM 27 Aug, 2025 to: 9:00 PM 27 Aug, 2025)
     * </pre>
     * </p>
     *
     * @return the formatted string representation of the event
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a d MMM, yyyy");
        String from = start.format(formatter);
        String to = end.format(formatter);
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
