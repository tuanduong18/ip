package tasks;

/**
 * A tasks.Task has description (name) and a status, initially is false (undone).
 */
public class Task {
    private final String description;
    private boolean status = false;

    public Task(String description) {
        this.description = description;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

	public String getDescription() {
		return this.description;
	}

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
