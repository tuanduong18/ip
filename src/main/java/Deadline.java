/**
 * A Deadline is a Task with description and due date.
 */
public class Deadline extends Task {
    private final String dueAt;

    public Deadline(String name, String dueAt) {
        super(name);
        this.dueAt = dueAt;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueAt +")";
    }
}