package storage;

import data.TaskList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Storage {
	private final Path filePath;
	public Storage(Path filePath) {
		this.filePath = filePath;
	}

	public TaskList loadTasks() {
		TaskList re = new TaskList();
		File f = filePath.toFile();
		try {
			Scanner s = new Scanner(f);
			while (s.hasNext()) {
				String temp = s.nextLine();
				String[] cmd = temp.split(" \\| ", 5);

			}
		} catch (FileNotFoundException e) {
			try{
				f.getParentFile().mkdirs();
				f.createNewFile();

			} catch (IOException ee) {
				ee.printStackTrace();
			}
		}
		return re;
	}
}
