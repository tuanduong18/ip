package tasks;

/**
 * Represents a to-do task without any specific date or time attached.
 * <p>
 * A {@code Todo} is the simplest type of {@link Task}, consisting only of
 * a description and a completion status. Its string representation
 * is prefixed with {@code [T]} to indicate that it is a to-do task.
 * </p>
 */
public class Todo extends Task {

	/**
	 * Creates a {@code Todo} with the specified description.
	 *
	 * @param name the description of the to-do task
	 */
	public Todo(String name) {
        super(name);
    }

	/**
	 * Returns a string representation of the to-do task, showing its type,
	 * completion status, and description.
	 * <p>
	 * The format is:
	 * <pre>
	 * [T][ ] Task description
	 * </pre>
	 * </p>
	 *
	 * @return the formatted string representation of the to-do task
	 */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}