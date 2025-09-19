package barry.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Collectors;

import barry.data.TaskList;
import barry.data.exceptions.BarryException;
import barry.tasks.Task;

/**
 * Handles persistence of the {@link TaskList} to and from disk.
 * <p>
 * A {@code Storage} instance points to a single file path and can:
 * </p>
 * <ul>
 *   <li><b>load</b> tasks by reading each line, decoding it into a {@link Task}
 *       via {@link Decode#decode(String)}, and collecting them into a {@link TaskList}; and</li>
 *   <li><b>save</b> tasks by converting each task’s string representation through
 *       {@link Encode#encode(String)} and writing the result to the file.</li>
 * </ul>
 * <p>
 * If the backing file does not exist when loading, this class attempts to create
 * the parent directories (if any) and an empty file at the configured path.
 * </p>
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a {@code Storage} handler for the given file path.
     *
     * @param filePath the path of the file used to persist tasks
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the configured file into a new {@link TaskList}.
     * <p>
     * Ensures the data file exists (creating directories and an empty file if necessary),
     * then reads it line by line, decoding each record via {@link Decode#decode(String)}.
     * </p>
     *
     * @return a {@link TaskList} populated with tasks decoded from the file
     * @throws BarryException if the file cannot be created/read or if a line is malformed
     */
    public TaskList load() throws BarryException {
        File f = filePath.toFile();
        ensureDataFileExists(f);
        return readAllTasks(f);
    }

    /**
     * Saves the provided {@link TaskList} to the configured file, overwriting any existing content.
     * <p>
     * Each task is first converted to the storage line format using {@link Encode#encode(String)},
     * and the combined output is written atomically using a try-with-resources {@link FileWriter}.
     * Errors are reported to standard output.
     * </p>
     *
     * @param taskList the tasks to be persisted
     */
    public void save(TaskList taskList) {
        String payload = taskList.listTasks().stream()
                .map(Encode::encode)
                .collect(Collectors.joining());
        try {
            File f = filePath.toFile();
            FileWriter fw = new FileWriter(f);
            fw.write(payload);
            fw.close();
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    // ---- helpers ----

    /**
     * Ensures that the given file exists, creating parent directories and the file if missing.
     *
     * @param f the target data file
     * @throws BarryException if the file or its parent directories cannot be created
     */
    private static void ensureDataFileExists(File f) throws BarryException {
        try {
            // If the target already exists as a file, we’re done.
            if (f.isFile()) {
                return;
            }

            // Ensure parent directory exists (idempotent).
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) {
                // mkdirs() returns false if it already exists or creation failed.
                // Consider it success if, after the call, the directory exists.
                if (!parent.mkdirs() && !parent.exists()) {
                    throw BarryException.invalidSourceFilePath();
                }
            }

            // Create the file if missing. If it already exists, that's OK.
            if (!f.exists() && !f.createNewFile()) {
                // If createNewFile returned false AND the file still doesn't exist, it's an error.
                throw BarryException.invalidSourceFilePath();
            }
        } catch (IOException e) {
            throw BarryException.invalidSourceFilePath();
        }
    }

    /**
     * Reads and decodes all tasks from the given file.
     *
     * @param f the data file to read
     * @return a {@link TaskList} containing all decoded tasks
     * @throws BarryException if the file cannot be found (unexpected after creation) or a line is invalid
     */
    private static TaskList readAllTasks(File f) throws BarryException {
        TaskList list = new TaskList();
        try (Scanner s = new Scanner(f)) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                Task t = Decode.decode(line);
                list.addTask(t);
            }
        } catch (FileNotFoundException e) {
            // Should not happen after ensureDataFileExists
            throw BarryException.invalidSourceFilePath();
        }
        return list;
    }
}
