package tasks;

/**
 * A tasks.Task has description (name) and a status, initially is false (undone).
 */
public class Task {
    private final String name;
    private boolean status = false;

    public Task(String name) {
        this.name = name;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status
                ? "[X] " + this.name
                : "[ ] " + this.name;
    }

	@Override
	public boolean equals(Object o) {
		if (o instanceof Task) {
			return o.toString().equals(this.toString());
		}
		return false;
	}
}
