package barry.commands;

import barry.alias.AliasStorage;
import barry.data.TaskList;
import barry.storage.Storage;
import barry.ui.Gui;
import barry.ui.Ui;

/**
 * Represents a command that displays user-defined command aliases.
 * <p>
 * The {@code AliasHelp} command reads aliases from {@link AliasStorage} and renders
 * them to the user, either by printing to the console via {@link Ui} or by returning
 * a formatted string for GUI rendering via {@link Gui}. It does not modify the
 * {@link TaskList} or {@link Storage}, and it does not cause the program to exit.
 * </p>
 */
public class AliasHelp extends Command {

    /**
     * Alias storage used to retrieve the current set of user-defined aliases.
     */
    private final AliasStorage aliasStorage = new AliasStorage();

    /**
     * Creates an {@code AliasHelp} command.
     * <p>
     * The {@code isExit} flag is set to {@code false} as this command does not
     * terminate the application. The constructor attempts to load aliases from
     * the default RC file (e.g., {@code ~/.barryrc}).
     * </p>
     */
    public AliasHelp() {
        super(false);
        aliasStorage.load();
    }

    /**
     * Executes the command by printing the current aliases to the console.
     * <p>
     * The aliases are obtained from {@link #aliasStorage} and passed to
     * {@link Ui#printAliases(java.util.HashMap)} for normalized rendering.
     * The task list and storage are not used.
     * </p>
     *
     * @param taskList the task list (not used by this command)
     * @param ui       the console UI used to print the aliases
     * @param storage  the storage handler (not used by this command)
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        ui.printAliases(aliasStorage.view());
    }

    /**
     * Semantics are identical to {@link #execute(TaskList, Ui, Storage)}, but returns
     * a formatted string suitable for GUI display instead of printing to the console.
     *
     * @param taskList the task list (not used by this command)
     * @param gui      the GUI facade (analogous to {@link Ui} but returning strings)
     * @param storage  the storage handler (not used by this command)
     * @return the formatted list of aliases for display in the GUI
     */
    @Override
    public String execute(TaskList taskList, Gui gui, Storage storage) {
        return gui.printAliases(aliasStorage.view());
    }

    /**
     * Returns {@code true} if {@code o} is also an {@code AliasHelp} command.
     * <p>
     * This command is stateless; therefore, all instances are considered equal.
     * </p>
     *
     * @param o the object to compare against
     * @return {@code true} if {@code o} is an {@code AliasHelp}; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof AliasHelp;
    }
}
