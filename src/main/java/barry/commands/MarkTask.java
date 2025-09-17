package barry.commands;

import barry.data.TaskList;
import barry.data.exceptions.BarryException;
import barry.storage.Storage;
import barry.tasks.Task;
import barry.ui.Gui;
import barry.ui.Ui;

/**
 * Represents a command that marks or unmarks a {@link Task} in the {@link TaskList}.
 * <p>
 * The {@code MarkCommand} identifies a task by its one-based index
 * in the displayed task list. When executed, it updates the marked
 * status of the specified task, saves the change to storage, and
 * displays a confirmation message to the user via the {@link Ui}.
 * </p>
 */
public class MarkTask extends Command {

    /**
     * The one-based index of the task to be marked or unmarked.
     */
    private final int id;

    /**
     * Indicates whether the task should be marked or unmarked.
     * {@code true} means mark the task, {@code false} means unmark the task.
     */
    private final boolean isMarked;

    /**
     * Creates a {@code MarkCommand}.
     *
     * @param id       the one-based index of the task in the task list
     * @param isMarked {@code true} if the task should be marked as done,
     *                 {@code false} if the task should be unmarked
     */
    public MarkTask(int id, boolean isMarked) {
        super(false);
        this.id = id;
        this.isMarked = isMarked;
    }

    /**
     * Executes the mark command by marking or unmarking the specified task,
     * saving the updated list to storage, and showing feedback to the user.
     *
     * @param taskList the task list containing the task to be marked/unmarked
     * @param ui       the user interface used to show the mark/unmark confirmation
     * @param storage  the storage handler used to persist the updated task list
     * @throws BarryException if the index is invalid (≤ 0 or greater than the task list size)
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws BarryException {
        if (id <= 0 || id > taskList.size()) {
            throw BarryException.taskNotFound(taskList.size());
        }
        String task = taskList.markTask(id - 1, isMarked);
        storage.save(taskList);
        ui.printMarkTask(task, isMarked);
    }

    /**
     * Semantics are identical to {@link #execute(TaskList, Ui, Storage)}.
     *
     * @param taskList the task list containing the task to be marked/unmarked
     * @param gui      the GUI facade (analogous to {@link Ui} but returning strings)
     * @param storage  the storage handler used to persist the updated task list
     * @return the confirmation message indicating the task and its updated marked status
     * @throws BarryException if the index is invalid (≤ 0 or greater than the task list size)
     */
    @Override
    public String execute(TaskList taskList, Gui gui, Storage storage) throws BarryException {
        if (id <= 0 || id > taskList.size()) {
            throw BarryException.taskNotFound(taskList.size());
        }
        String task = taskList.markTask(id - 1, isMarked);
        storage.save(taskList);
        return gui.printMarkTask(task, isMarked);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MarkTask) {
            MarkTask n = (MarkTask) o;
            return n.isMarked == this.isMarked && n.id == this.id;
        }
        return false;
    }

}
