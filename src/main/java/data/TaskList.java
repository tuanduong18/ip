package data;

import java.util.ArrayList;

import tasks.Task;

public class TaskList {
	private final ArrayList<Task> allTask;

	public TaskList() {
		this.allTask = new ArrayList<>();
	}

	public TaskList(ArrayList<Task> allTask) {
		this.allTask = allTask;
	}

	public void addTask(Task t) {
		this.allTask.add(t);
	}

	public String deleteTask(int index) {
		Task t = allTask.get(index);
		this.allTask.remove(index);
		return t.toString();

	}

	public String markTask(int index, boolean marked) {
		this.allTask.get(index).setStatus(marked);
		return allTask.get(index).toString();
	}

	public int size() {
		return this.allTask.size();
	}

	public ArrayList<String> listTasks() {
		ArrayList<String> s = new ArrayList<String>();
		for (Task t : allTask) {
			s.add(t.toString());
		}
		return s;
	}
}
