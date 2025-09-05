package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

import data.TaskList;
import data.exceptions.BarryException;
import tasks.Task;

/**
 * Handles persistence of the {@link TaskList} to and from disk.
 * <p>
 * A {@code Storage} instance points to a single file path and can:
 * </p>
 * <ul>
 *   <li><b>load</b> tasks by reading each line and turning it into a {@link Task}
 *       via {@link Decode#decode(String)}, and</li>
 *   <li><b>save</b> tasks by writing each task’s string form through
 *       {@link Encode#encode(String)} in a pipe-delimited format.</li>
 * </ul>
 * <p>
 * If the backing file does not exist on load, this class attempts to create
 * the parent directories and an empty file at the configured path.
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
     * Each line is decoded with {@link Decode#decode(String)}. If the file is missing,
     * this method attempts to create the necessary parent directories and an empty file.
     * </p>
     *
     * @return a {@link TaskList} populated with tasks decoded from the file
     * @throws BarryException if the file cannot be created/read or if a line is malformed
     */
    public TaskList load() throws BarryException {
        TaskList re = new TaskList();
        File f = filePath.toFile();
        try {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                String command = s.nextLine();
                Task t = Decode.decode(command);
                re.addTask(t);
            }
        } catch (FileNotFoundException e) {
            try {
                boolean mkdirs = f.getParentFile().mkdirs();
                boolean create = f.createNewFile();
                if (!mkdirs || !create) {
                    // Should not go to this line
                    throw BarryException.invalidSourceFilePath();
                }
            } catch (IOException ee) {
                // Should not go to this line
                throw BarryException.invalidSourceFilePath();
            }
        }
        return re;
    }

    /**
     * Saves the provided {@link TaskList} to the configured file, overwriting any existing content.
     * <p>
     * Each task is converted to the storage line format using {@link Encode#encode(String)}.
     * Errors are caught and printed to standard output.
     * </p>
     *
     * @param taskList the tasks to be persisted
     */
    public void save(TaskList taskList) {
        StringBuilder s = new StringBuilder();
        for (String t : taskList.listTasks()) {
            s.append(Encode.encode(t));
        }
        try {
            File f = filePath.toFile();
            FileWriter fw = new FileWriter(f);
            fw.write(s.toString());
            fw.close();
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }
}
