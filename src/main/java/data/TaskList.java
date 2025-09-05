package data;

import java.util.ArrayList;

import tasks.Task;

public class TaskList {
	private final ArrayList<Task> taskList;

	public TaskList() {
		this.taskList = new ArrayList<>();
	}

	public TaskList(ArrayList<Task> taskList) {
		this.taskList = taskList;
	}

	public void addTask(Task t) {
		this.taskList.add(t);
	}

	public String deleteTask(int index) {
		Task t = taskList.get(index);
		this.taskList.remove(index);
		return t.toString();

	}

	public String markTask(int index, boolean marked) {
		this.taskList.get(index).setStatus(marked);
		return taskList.get(index).toString();
	}

	public int size() {
		return this.taskList.size();
	}

	public ArrayList<String> listTasks() {
		ArrayList<String> s = new ArrayList<>();
		for (Task t : taskList) {
			s.add(t.toString());
		}
		return s;
	}

	public ArrayList<String> findMatchingTasks(String pattern) {
		ArrayList<String> s = new ArrayList<>();
		for (Task t : taskList) {
			if(t.getDescription().toLowerCase().contains(pattern.toLowerCase())) {
				s.add(t.toString());
			}
		}
		return s;
	}

}
