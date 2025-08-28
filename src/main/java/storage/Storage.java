package storage;

import data.TaskList;
import data.exceptions.BarryException;
import tasks.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Storage {
	private final Path filePath;
	public Storage(Path filePath) {
		this.filePath = filePath;
	}

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
			try{
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
