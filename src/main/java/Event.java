import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An Event is a Task with description, starting time and ending time.
 */
class Event extends Task{
    private final LocalDateTime start;
    private final LocalDateTime end;

    public Event(String name, LocalDateTime start, LocalDateTime end) {
        super(name);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a d MMM, yyyy");
	    String from = start.format(formatter);
		String to = end.format(formatter);
		return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}