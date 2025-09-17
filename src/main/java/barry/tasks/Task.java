package barry.tasks;

/**
 * Represents a task with a description and a completion isDone.
 * <p>
 * A {@code Task} has a description and a boolean isDone
 * indicating whether it is marked as completed. The isDone is
 * {@code false} (not completed) by default.
 * </p>
 */
public class Task {

    /**
     * The description of the task.
     */
    private final String description;

    /**
     * The completion status of the task.
     * {@code true} if the task is completed, {@code false} otherwise.
     */
    private boolean isDone = false;

    /**
     * Creates a {@code Task} with the given description.
     * By default, the task is uncompleted.
     *
     * @param description the description of the task
     */
    public Task(String description) {
        this.description = description;
    }

    /**
     * Updates the completion isDone of the task.
     *
     * @param isDone {@code true} to mark the task as completed,
     *               {@code false} to mark it as uncompleted
     */
    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * @return the string representation of the task's description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns a string representation of the task,
     * showing its completion isDone and description.
     * <ul>
     *     <li>{@code "[X] taskName"} if the task is completed</li>
     *     <li>{@code "[ ] taskName"} if the task is not completed</li>
     * </ul>
     *
     * @return the string representation of the task
     */
    @Override
    public String toString() {
        return this.isDone
                ? "[X] " + this.description
                : "[ ] " + this.description;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Task) {
            return o.toString().equals(this.toString());
        }
        return false;
    }
}
