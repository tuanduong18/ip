package barry.commands;

import barry.data.TaskList;
import barry.storage.Storage;
import barry.tasks.Task;
import barry.ui.Gui;
import barry.ui.Ui;

/**
 * Represents a command that adds a {@link Task} to the {@link TaskList}.
 * <p>
 * The {@code AddCommand} encapsulates the {@link Task} to be added and, when executed,
 * updates the task list, saves the change to storage, and displays a confirmation
 * message to the user via the {@link Ui}.
 * </p>
 */
public class AddCommand extends Command {

    /**
     * The task to be added to the task list.
     */
    private final Task task;

    /**
     * Creates an {@code AddCommand} with the specified task.
     *
     * @param t the task to be added to the task list
     */
    public AddCommand(Task t) {
        super(false);
        this.task = t;
    }

    /**
     * Executes the command by adding the task to the task list,
     * saving the updated list to storage, and showing feedback to the user.
     *
     * @param taskList the task list to which the task will be added
     * @param ui       the user interface used to show the add confirmation
     * @param storage  the storage handler used to persist the task list
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        taskList.addTask(task);
        storage.save(taskList);
        ui.printAddTask(task.toString(), taskList.size());
    }

    @Override
    public String execute(TaskList taskList, Gui gui, Storage storage) {
        taskList.addTask(task);
        storage.save(taskList);
        return gui.printAddTask(task.toString(), taskList.size());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AddCommand) {
            Task t = ((AddCommand) o).task;
            return t.equals(this.task);
        }
        return false;
    }
}
