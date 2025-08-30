package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline task with a due date and time.
 * <p>
 * A {@code Deadline} is a type of {@link Task} that must be
 * completed before a specific date and time. Its string representation
 * includes the deadline in a human-readable format.
 * </p>
 */
public class Deadline extends Task {

	/**
	 * The due date and time of the deadline.
	 */
	private final LocalDateTime dueAt;

	/**
	 * Creates a {@code Deadline} with the specified description
	 * and due date/time.
	 *
	 * @param name  the description of the deadline task
	 * @param dueAt the due date and time of the task
	 */
    public Deadline(String name, LocalDateTime dueAt) {
        super(name);
        this.dueAt = dueAt;
    }

	/**
	 * Returns a string representation of the deadline task,
	 * showing its type, completion status, description,
	 * and due date/time.
	 * <p>
	 * The format is:
	 * <pre>
	 * [D][ ] Task description (by: 4:00 PM 30 Aug, 2025)
	 * </pre>
	 * </p>
	 *
	 * @return the formatted string representation of the deadline task
	 */
    @Override
    public String toString() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a d MMM, yyyy");
	    String formatted = dueAt.format(formatter);
		return "[D]" + super.toString() + " (by: " + formatted +")";
    }
}