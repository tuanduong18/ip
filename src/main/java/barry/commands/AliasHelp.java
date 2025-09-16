package barry.commands;

import barry.alias.AliasStorage;
import barry.data.TaskList;
import barry.storage.Storage;
import barry.ui.Gui;
import barry.ui.Ui;

public class AliasHelp extends Command{
    private final AliasStorage aliasStorage = new AliasStorage();

    public AliasHelp() {
        super(false);
        aliasStorage.load();
    }

    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        ui.printAliases(aliasStorage.view());
    }

    /**
     * Semantics are identical to {@link #execute(TaskList, Ui, Storage)}.
     *
     * @param taskList the task list containing tasks to be listed
     * @param gui      the GUI facade (analogous to {@link Ui} but returning strings)
     * @param storage  the storage handler (not used in this command, required by signature)
     * @return the formatted list of tasks for display in the GUI
     */
    @Override
    public String execute(TaskList taskList, Gui gui, Storage storage) {
        return gui.printAliases(aliasStorage.view());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AliasHelp;
    }
}
