import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A Deadline is a Task with description and due date.
 */
public class Deadline extends Task {
    private final LocalDateTime dueAt;

    public Deadline(String name, LocalDateTime dueAt) {
        super(name);
        this.dueAt = dueAt;
    }

    @Override
    public String toString() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	    String formatted = dueAt.format(formatter);
		return "[D]" + super.toString() + " (by: " + formatted +")";
    }
}