package barry.ui;

import java.util.ArrayList;

import barry.data.common.CommandType;

public class Gui {
    private static final String greeting = "Hello from Barry, what can I do for you?";

    public String print(ArrayList<String> strings) {
        StringBuilder response = new StringBuilder();
        for (String s : strings) {
            // Replace all '\n' with '\n\t'
            String formatted = s.replace("\n", "\n");
            response.append(formatted);
            response.append("\n");
        }
        return response.toString().trim();
    }

    public String printGreetings() {
        ArrayList<String> s = new ArrayList<>();
        s.add(greeting);
        return this.print(s);
    }

    public String showLoadingError() {
        ArrayList<String> s = new ArrayList<>();
        s.add("Cannot access data file");
        return this.print(s);
    }

    public String printAddTask(String task, int n) {
        ArrayList<String> s = new ArrayList<>();
        s.add("Got it. I've added this task:");
        s.add("\t" + task);
        s.add("Now you have " + n + (n > 1 ? "tasks " : " task ") + "in the list.");
        return this.print(s);
    }

    public String printListTask(ArrayList<String> taskList) {
        ArrayList<String> s = new ArrayList<>();
        s.add("Here are the tasks in your list:");
        int i = 1;
        for (String item : taskList) {
            s.add("\t" + i + "." + item);
            i++;
        }
        return this.print(s);
    }

    public String printMarkTask(String task, boolean marked) {
        String comment = marked
                ? "Nice! I've marked this task as done:"
                : "Ok! I've marked this task as not done yet:";

        ArrayList<String> s = new ArrayList<>();
        s.add(comment);
        s.add("\t" + task);
        return this.print(s);
    }

    public String printDeleteTask(String task, int n) {
        ArrayList<String> s = new ArrayList<>();
        s.add("Noted. I've removed this task:");
        s.add("\t" + task);
        s.add("Now you have " + n + (n > 1 ? "tasks " : " task ") + "in the list.");
        return this.print(s);
    }

    public String printGoodbye() {
        ArrayList<String> s = new ArrayList<>();
        s.add("Bye. Hope to see you again soon!");
        return this.print(s);
    }

    public String printHelp() {
        ArrayList<String> s = new ArrayList<>();
        s.add("The command must start with one of these below:");
        s.add(CommandType.allCommands());
        return this.print(s);
    }

    public String printDetailedHelp() {
        ArrayList<String> s = new ArrayList<>();
        s.add("The command must have the formula as one of these below:");
        s.add(CommandType.allCommandsDetailed());
        return this.print(s);
    }

    public String printMatchingTasks(ArrayList<String> taskList) {
        ArrayList<String> s = new ArrayList<>();
        if (taskList.isEmpty()) {
            s.add("Oops! There isn't any task match your search");
            return this.print(s);
        }
        s.add("Here are the matching tasks in your list:");
        int i = 1;
        for (String item : taskList) {
            s.add("\t" + i + "." + item);
            i++;
        }
        return this.print(s);
    }
}
