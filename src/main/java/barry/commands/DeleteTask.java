package barry.commands;

import barry.data.TaskList;
import barry.data.exceptions.BarryException;
import barry.storage.Storage;
import barry.tasks.Task;
import barry.ui.Gui;
import barry.ui.Ui;

/**
 * Represents a command that deletes a {@link Task} from the {@link TaskList}.
 * <p>
 * The {@code DeleteCommand} identifies a task by its one-based index
 * in the displayed task list. When executed, it removes the task,
 * saves the updated list to storage, and displays a confirmation message
 * to the user via the {@link Ui}.
 * </p>
 */
public class DeleteTask extends Command {

    /**
     * The one-based index of the task to be deleted.
     */
    private final int id;

    /**
     * Creates a {@code DeleteCommand} to delete the task at the specified index.
     *
     * @param id the one-based index of the task in the task list
     */
    public DeleteTask(int id) {
        super(false);
        this.id = id;
    }

    /**
     * Executes the command by removing the task at the specified index
     * from the task list, saving the updated list to storage, and
     * showing feedback to the user.
     *
     * @param taskList the task list from which the task will be deleted
     * @param ui       the user interface used to show the delete confirmation
     * @param storage  the storage handler used to persist the updated task list
     * @throws BarryException if the index is invalid (≤ 0 or greater than the task list size)
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws BarryException {
        if (id <= 0 || id > taskList.size()) {
            throw BarryException.taskNotFound(taskList.size());
        }
        try {
            String task = taskList.deleteTask(id - 1);
            storage.save(taskList);
            ui.printDeleteTask(task, taskList.size());
        } catch (IndexOutOfBoundsException e) {
            // Should not go into this line
            throw BarryException.taskNotFound(taskList.size());
        }
    }

    /**
     * Semantics are identical to {@link #execute(TaskList, Ui, Storage)}
     *
     * @param taskList the task list from which the task will be deleted
     * @param gui      the GUI facade (analogous to {@link Ui} but returning strings)
     * @param storage  the storage handler used to persist the updated task list
     * @return the confirmation message including the added task and updated count
     * @throws BarryException if the index is invalid (≤ 0 or greater than the task list size)
     */
    @Override
    public String execute(TaskList taskList, Gui gui, Storage storage) throws BarryException {
        if (id <= 0 || id > taskList.size()) {
            throw BarryException.taskNotFound(taskList.size());
        }
        try {
            String task = taskList.deleteTask(id - 1);
            storage.save(taskList);
            return gui.printDeleteTask(task, taskList.size());
        } catch (IndexOutOfBoundsException e) {
            // Should not go into this line
            throw BarryException.taskNotFound(taskList.size());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DeleteTask) {
            int oId = ((DeleteTask) o).id;
            return oId == id;
        }
        return false;
    }
}
