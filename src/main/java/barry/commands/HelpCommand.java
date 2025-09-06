package barry.commands;

import barry.data.TaskList;
import barry.storage.Storage;
import barry.ui.Gui;
import barry.ui.Ui;

/**
 * Represents a command that displays usage information to the user.
 * <p>
 * The {@code HelpCommand} can either show a concise help message
 * or a detailed help message, depending on the {@code detailed} flag.
 * This command does not modify the {@link TaskList} or {@link Storage}.
 * </p>
 */
public class HelpCommand extends Command {

    /**
     * Determines whether the detailed help message should be shown.
     * If {@code true}, a detailed help message is displayed;
     * otherwise, a concise help message is displayed.
     */
    private final boolean isDetailed;

    /**
     * Creates a {@code HelpCommand}.
     *
     * @param detailed {@code true} to show the detailed help message,
     *                 {@code false} to show the concise help message
     */
    public HelpCommand(boolean detailed) {
        super(false);
        this.isDetailed = detailed;
    }

    /**
     * Executes the help command by displaying the appropriate help message
     * through the {@link Ui}. This command does not affect the task list or storage.
     *
     * @param taskList the task list (not used in this command, but required by signature)
     * @param ui       the user interface used to display the help message
     * @param storage  the storage handler (not used in this command, but required by signature)
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        if (isDetailed) {
            ui.printHelp();
        } else {
            ui.printDetailedHelp();
        }
    }

    @Override
    public String execute(TaskList taskList, Gui gui, Storage storage) {
        if (isDetailed) {
            return gui.printHelp();
        } else {
            return gui.printDetailedHelp();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof HelpCommand) {
            boolean oDetails = ((HelpCommand) o).isDetailed;
            return oDetails == isDetailed;
        }
        return false;
    }
}
