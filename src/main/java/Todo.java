public class Todo {
    private final String name;
    private boolean status = false;

    public Todo(String name) {
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
}
