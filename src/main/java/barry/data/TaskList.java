package barry.data;

import java.util.ArrayList;
import java.util.stream.Collectors;

import barry.tasks.Task;

/**
 * Represents a mutable, in-memory list of {@link Task} objects and provides
 * utilities to add, remove, update, and query tasks.
 * <p>
 * This class stores tasks in insertion order and exposes convenience methods
 * that return a string view of tasks via each task’s {@link Task#toString()}.
 * It is not thread-safe.
 * </p>
 */
public class TaskList {
    private final ArrayList<Task> taskList;

    public TaskList() {
        this.taskList = new ArrayList<>();
    }

    /**
     * Creates a {@code TaskList} backed by the provided list.
     * <p>
     * Note: the provided list is used directly (no defensive copy).
     * Mutations through this {@code TaskList} will affect the given list.
     * </p>
     *
     * @param taskList the underlying list of tasks to use
     */
    public TaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param t the task to add
     */
    public void addTask(Task t) {
        this.taskList.add(t);
    }

    /**
     * Removes and returns the string representation of the task at the given index.
     *
     * @param index zero-based index of the task to remove
     * @return the removed task’s {@link Task#toString() toString()} value
     * @throws IndexOutOfBoundsException if {@code index} is out of range
     */
    public String deleteTask(int index) {
        Task t = taskList.get(index);
        this.taskList.remove(index);
        return t.toString();

    }

    /**
     * Sets the completion status of the task at the given index and returns its string representation.
     *
     * @param index  zero-based index of the task to update
     * @param marked {@code true} to mark as done; {@code false} to unmark
     * @return the updated task’s {@link Task#toString() toString()} value
     * @throws IndexOutOfBoundsException if {@code index} is out of range
     */
    public String markTask(int index, boolean marked) {
        this.taskList.get(index).setStatus(marked);
        return taskList.get(index).toString();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return this.taskList.size();
    }

    /**
     * Returns a snapshot of all tasks as strings, in list order.
     *
     * @return an {@link ArrayList} of {@code toString()} values for each task
     */
    public ArrayList<String> listTasks() {
        return taskList.stream()
                .map(Task::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Finds tasks whose descriptions contain the given pattern (case-insensitive)
     * and returns their string representations.
     *
     * @param pattern the substring to search for within each task’s description
     * @return an {@link ArrayList} of matching tasks’ {@code toString()} values
     */
    public ArrayList<String> findMatchingTasks(String pattern) {
        return taskList.stream()
                .filter(t -> t.getDescription().toLowerCase().contains(pattern.toLowerCase()))
                .map(Task::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
