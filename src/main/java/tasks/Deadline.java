package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A tasks.Deadline is a tasks.Task with description and due date.
 */
public class Deadline extends Task {
    private final LocalDateTime dueAt;

    public Deadline(String description, LocalDateTime dueAt) {
        super(description);
        this.dueAt = dueAt;
    }

    @Override
    public String toString() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a d MMM, yyyy");
	    String formatted = dueAt.format(formatter);
		return "[D]" + super.toString() + " (by: " + formatted +")";
    }
}