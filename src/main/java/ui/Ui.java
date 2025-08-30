package ui;

import commands.CommandType;

import java.util.ArrayList;

public class Ui {
	private static final String name = """
                $$$$$$$\\
                $$  __$$\\
                $$ |  $$ | $$$$$$\\   $$$$$$\\   $$$$$$\\  $$\\   $$\\
                $$$$$$$\\ | \\____$$\\ $$  __$$\\ $$  __$$\\ $$ |  $$ |
                $$  __$$\\  $$$$$$$ |$$ |  \\__|$$ |  \\__|$$ |  $$ |
                $$ |  $$ |$$  __$$ |$$ |      $$ |      $$ |  $$ |
                $$$$$$$  |\\$$$$$$$ |$$ |      $$ |      \\$$$$$$$ |
                \\_______/  \\_______|\\__|      \\__|       \\____$$ |
                                                        $$\\   $$ |
                                                        \\$$$$$$  |
                                                         \\______/""";

	private static final String greeting = "Hello from Barry, what can I do for you?";

	public void print(ArrayList<String> strings) {
		System.out.println("\t" + "_".repeat(50));
		for (String s : strings) {
			// Replace all '\n' with '\n\t'
			String formatted = s.replace("\n", "\n\t");

			// Format the first line
			formatted = "\t" + formatted;
			System.out.println(formatted);

		}
		System.out.println("\t" + "_".repeat(50));
	}

	public void printGreetings() {
		ArrayList<String> s = new ArrayList<>();
		s.add(name);
		s.add(greeting);
		this.print(s);
	}

	public void showLoadingError() {
		ArrayList<String> s = new ArrayList<>();
		s.add("Cannot access data file");
		this.print(s);
	}

	public void printAddTask(String task, int n) {
		ArrayList<String> s = new ArrayList<>();
		s.add("Got it. I've added this task:");
		s.add("\t" + task);
		s.add("Now you have " + n + (n > 1 ? " tasks " : " task ") + "in the list.");
		this.print(s);
	}

	public void printListTask(ArrayList<String> taskList) {
		ArrayList<String> s = new ArrayList<>();
		s.add("Here are the tasks in your list:");
		int i = 1;
		for (String item : taskList) {
			s.add("\t" + i + "." + item);
			i++;
		}
		this.print(s);
	}

	public void printMarkTask(String task, boolean marked) {
		String comment = marked
				?  "Nice! I've marked this task as done:"
				: "Ok! I've marked this task as not done yet:";

		ArrayList<String> s = new ArrayList<>();
		s.add(comment);
		s.add("\t" + task);
		this.print(s);
	}

	public void printDeleteTask(String task, int n) {
		ArrayList<String> s = new ArrayList<>();
		s.add("Noted. I've removed this task:");
		s.add("\t" + task);
		s.add("Now you have " + n + (n > 1 ? " tasks " : " task ") + "in the list.");
		this.print(s);
	}

	public void printGoodbye() {
		ArrayList<String> s = new ArrayList<>();
		s.add("Bye. Hope to see you again soon!");
		this.print(s);
	}

	public void printHelp() {
		ArrayList<String> s = new ArrayList<>();
		s.add("The command must start with one of these below:");
		s.add(CommandType.allCommands());
		this.print(s);
	}

	public void printDetailedHelp() {
		ArrayList<String> s = new ArrayList<>();
		s.add("The command must have the formula as one of these below:");
		s.add(CommandType.allCommandsDetailed());
		this.print(s);
	}

	public void printMatchingTasks(ArrayList<String> taskList) {
		ArrayList<String> s = new ArrayList<>();
		s.add("Here are the matching tasks in your list:");
		int i = 1;
		for (String item : taskList) {
			s.add("\t" + i + "." + item);
			i++;
		}
		this.print(s);
	}
}

