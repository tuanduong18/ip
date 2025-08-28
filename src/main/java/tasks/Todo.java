package tasks;

/**
 * A tasks.Todo is a tasks.Task with description only.
 */
public class Todo extends Task {
    public Todo(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}