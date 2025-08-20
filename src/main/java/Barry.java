import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Barry {
    private static ArrayList<Task> tasksList = new ArrayList<>();
    public static void main(String[] args) {
        greetings();
        startSession();
        endSession();
    }

    /**
     * Prints the greeting banner and a short introduction for the chatbot.
     */
    public static void greetings() {
        String name = "Barry";
        System.out.println("\t" + "_".repeat(50));
        System.out.println("\t" + "Hello I'm " + name);
        System.out.println("\t" + "What can I do for you?");
        System.out.println("\t" + "_".repeat(50));
    }

    /**
     * Runs the main loop to read and process user commands.
     */
    public static void startSession() {
        Scanner scan = new Scanner(System.in);
        while(true) {
            String temp = scan.nextLine();
            if (temp.equals("bye")) {
                System.out.println("\t" + "_".repeat(50));
                break;
            } else if (temp.equals("list")) {
                printList();
            } else if (Pattern.matches("(mark|unmark) (-|)[0-9]+", temp)) {
                markTask(temp);
            } else if (Pattern.matches("(todo|deadline|event) .*", temp)){
                addTask(temp);
            } else {
                System.out.println("\t" + "_".repeat(50));
                System.out.println("\t" + "Invalid command");
                System.out.println("\t" + "_".repeat(50));
            }

        }
    }

    /**
     * Adds a new task to the list.
     *
     * @param content the command string describing the task
     */
    public static void addTask(String content) {
        System.out.println("\t" + "_".repeat(50));
        if (Pattern.matches("todo .*", content)) {
            String name = content.substring(5);
            tasksList.add(new Todo(name));
            int n = tasksList.size();
        } else if (Pattern.matches("deadline .* /by .*", content)) {
            String[] ss = content.substring(9).split(" /by ", 2);
            tasksList.add(new Deadline(ss[0], ss[1]));
        } else if ((Pattern.matches("event .* /from .* /to .*", content))) {
            String[] s1 = content.substring(6).split(" /from ", 2);
            String[] s2 = s1[1].split(" /to ", 2);
            tasksList.add(new Event(s1[0], s2[0], s2[1]));
        } else {
            System.out.println("\tInvalid command");
            System.out.println("\t" + "_".repeat(50));
            return;
        }
        int n = tasksList.size();
        System.out.println("\t" + "Got it. I've added this task: ");
        System.out.println("\t\t" + tasksList.get(n - 1));
        System.out.println("\tNow you have " + n + (n > 1 ? " tasks " : " task ") + "in the list.");
        System.out.println("\t" + "_".repeat(50));
    }

    /**
     * Marks the task as done or undone.
     *
     * @param command the user input containing the action and task index
     */
    public static void markTask(String command) {
        String[] s = command.split(" ");
        String mark = s[0];
        int id = Integer.parseInt(s[1]);
        if (id > tasksList.size() || id <= 0) {
            System.out.println("\t" + "_".repeat(50));
            System.out.println("\t" + "Invalid task");
            System.out.println("\t" + "_".repeat(50));
        } else {
            System.out.println("\t" + "_".repeat(50));
            tasksList.get(id - 1).setStatus(mark.equals("mark"));
            if(mark.equals("mark")) {
                System.out.println("\t" + "Nice! I've marked this task as done:");
            } else {
                System.out.println("\t" + "Ok! I've marked this task as not done yet:");
            }
            System.out.println("\t\t" + tasksList.get(id - 1));
            System.out.println("\t" + "_".repeat(50));
        }
    }

    /**
     * Prints all tasks currently stored in tasksList, with their status and index.
     */
    public static void printList() {
        System.out.println("\t" + "_".repeat(50));
        System.out.println("\tHere are the tasks in your list:");
        int i = 1;
        for (Task item : tasksList) {
            System.out.println("\t" + i + "." + item.toString());
            i++;
        }
        System.out.println("\t" + "_".repeat(50));
    }

    /**
     * Prints the goodbye message.
     */
    public static void endSession() {
        System.out.println("\t" + "Bye. Hope to see you again soon!");
        System.out.println("\t" + "_".repeat(50));
    }
}
