package barry.tasks;

/**
 * Represents a task with a description and a completion status.
 * <p>
 * A {@code Task} has a description and a boolean status
 * indicating whether it is marked as completed. The status is
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
    private boolean status = false;

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
     * Updates the completion status of the task.
     *
     * @param status {@code true} to mark the task as completed,
     *               {@code false} to mark it as uncompleted
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * @return the string representation of the task's description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns a string representation of the task,
     * showing its completion status and description.
     * <ul>
     *     <li>{@code "[X] taskName"} if the task is completed</li>
     *     <li>{@code "[ ] taskName"} if the task is not completed</li>
     * </ul>
     *
     * @return the string representation of the task
     */
    @Override
    public String toString() {
        return this.status
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
