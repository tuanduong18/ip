package barry;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import barry.commands.Command;
import barry.data.TaskList;
import barry.data.exceptions.BarryException;
import barry.parser.CommandParser;
import barry.storage.Storage;
import barry.ui.Ui;

/**
 * Entry point and top-level coordinator for the Barry.Barry CLI task manager.
 * <p>
 * {@code Barry.Barry} wires together the user interface ({@link Ui}), command parsing
 * ({@link CommandParser}), in-memory task state ({@link TaskList}), and
 * persistence ({@link Storage}). On startup, it attempts to load tasks from the
 * provided storage path; if loading fails, a concise error is shown and an empty
 * task list is used.
 * </p>
 */
public class Barry {
    private static final Path path = Paths.get("..", "data", "Barry.txt");

    // Print messages from Barry.Barry
    private final Ui ui;

    // Store all tasks in the session
    private final TaskList taskList;

    // Parse the command
    private final CommandParser parser;

    // Interact with local storage
    private final Storage storage;

    public Barry() {
        this(Barry.path);
    }
    /**
     * Creates a new {@code Barry.Barry} application bound to the given storage path.
     * <p>
     * The constructor initializes the UI, parser, and storage handler, then tries
     * to load tasks from disk via {@link Storage#load()}. If loading fails with a
     * {@link BarryException}, a loading error is printed and the task list starts empty.
     * </p>
     *
     * @param path the file path used to persist and load tasks
     */
    public Barry(Path path) {
        this.ui = new Ui();
        this.parser = new CommandParser();
        this.storage = new Storage(path);
        TaskList stored = new TaskList();
        try {
            stored = storage.load();
        } catch (BarryException e) {
            ui.showLoadingError();
        } finally {
            taskList = stored;
        }
    }

    /**
     * Runs the main REPL loop for the Barry.Barry application.
     * <p>
     * After printing a greeting, this method reads user input line-by-line from
     * standard input, parses it into a {@link Command}, executes the command,
     * and continues until a command sets the {@code isExit} flag. Any
     * {@link BarryException} thrown during parsing or execution is caught and
     * displayed through the {@link Ui}. A goodbye message is printed on exit.
     * </p>
     */
    public void run() {
        ui.printGreetings();
        Scanner scan = new Scanner(System.in);
        boolean isExit = false;
        while (!isExit) {
            try {
                String temp = scan.nextLine();
                Command c = parser.parseCommand(temp);
                c.execute(taskList, ui, storage);
                isExit = c.isExit;
            } catch (BarryException e) {
                ArrayList<String> s = new ArrayList<>();
                s.add("OOPS!!! " + e.getMessage());
                ui.print(s);
            }
        }
        ui.printGoodbye();
    }

    public String getResponse(String input) {
        return "";
    }

    /**
     * Program entry point.
     * <p>
     * Launches Barry.Barry with the default storage path {@code ../data/Barry.Barry.txt}
     * (relative to the working directory).
     * </p>
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        new Barry(Paths.get("..", "data", "Barry.txt")).run();
    }
}
