import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Barry {
    private static final ArrayList<Task> taskList = new ArrayList<>();
    public static void main(String[] args) {
        greetings();
        startSession();
        endSession();
    }

    /**
     * Prints the greeting banner and a short introduction for the chatbot.
     */
    public static void greetings() {
        String name = """
                \t$$$$$$$\\
                \t$$  __$$\\
                \t$$ |  $$ | $$$$$$\\   $$$$$$\\   $$$$$$\\  $$\\   $$\\
                \t$$$$$$$\\ | \\____$$\\ $$  __$$\\ $$  __$$\\ $$ |  $$ |
                \t$$  __$$\\  $$$$$$$ |$$ |  \\__|$$ |  \\__|$$ |  $$ |
                \t$$ |  $$ |$$  __$$ |$$ |      $$ |      $$ |  $$ |
                \t$$$$$$$  |\\$$$$$$$ |$$ |      $$ |      \\$$$$$$$ |
                \t\\_______/  \\_______|\\__|      \\__|       \\____$$ |
                \t                                        $$\\   $$ |
                \t                                        \\$$$$$$  |
                \t                                         \\______/""";
        System.out.println("\t" + "_".repeat(50));
        System.out.println("\t" + "Hello from\n\n" + name);
        System.out.println("\t" + "What can I do for you?");
        System.out.println("\t" + "_".repeat(50));
    }

    /**
     * Runs the main loop to read and process user commands.
     */
    public static void startSession() {
        Scanner scan = new Scanner(System.in);
        while(true) {
            try {
                String temp = scan.nextLine();
                if (temp.equals("bye")) {
                    System.out.println("\t" + "_".repeat(50));
                    break;
                } else if (temp.equals("list")) {
                    printList();
                } else if (Pattern.matches("(mark|unmark) (-|)[0-9]+", temp)) {
                    markTask(temp);
                } else if (Pattern.matches("(todo|deadline|event) .*", temp)) {
                    addTask(temp);
                } else if (Pattern.matches("delete (-|)[0-9]+", temp)) {
                    deleteTask(temp);
                } else {
                    throw BarryException.commandException();
                }
            } catch (BarryException e) {
                System.out.println("\t" + "_".repeat(50));
                System.out.println("\t" + "OOPS!!! " + e.getMessage());
                System.out.println("\t" + "_".repeat(50));
            }
        }
    }

    /**
     * Adds a new task to the list.
     *
     * @param content the command string describing the task
     */
    public static void addTask(String content) throws BarryException {
        if (Pattern.matches("todo .*", content)) {
            String name = content.substring(5);
            if (name.trim().isEmpty()) {
              throw BarryException.missingNameException("todo");
            }
            taskList.add(new Todo(name));
        } else if (Pattern.matches("deadline .* /by .*", content)) {
            String[] ss = content.substring(9).split(" /by ", 2);
            if (ss[0].trim().isEmpty()) {
                throw BarryException.missingNameException("deadline");
            } else if (ss[1].trim().isEmpty()) {
                throw BarryException.missingTimestamp(ss[0], "deadline", "due date");
            }
            taskList.add(new Deadline(ss[0], ss[1]));
        } else if ((Pattern.matches("event .* /from .* /to .*", content))) {
            String[] s1 = content.substring(6).split(" /from ", 2);
            String[] s2 = s1[1].split(" /to ", 2);
            if (s1[0].trim().isEmpty()) {
                throw BarryException.missingNameException("event");
            } else if (s2[0].trim().isEmpty()) {
                throw BarryException.missingTimestamp(s1[0], "event", "starting time");
            } else if (s2[1].trim().isEmpty()) {
                throw BarryException.missingTimestamp(s1[0], "event", "ending time");
            }
            taskList.add(new Event(s1[0], s2[0], s2[1]));
        } else {
            throw BarryException.commandException();
        }
        System.out.println("\t" + "_".repeat(50));
        int n = taskList.size();
        System.out.println("\t" + "Got it. I've added this task:");
        System.out.println("\t\t" + taskList.get(n - 1));
        System.out.println("\tNow you have " + n + (n > 1 ? " tasks " : " task ") + "in the list.");
        System.out.println("\t" + "_".repeat(50));
    }

    /**
     * Deletes a task from the list.
     *
     * @param command the command string describing the task
     */
    public static void deleteTask(String command) throws BarryException {
        int total = taskList.size();
        String[] s = command.split(" ");
        int id = Integer.parseInt(s[1]);
        if (id > taskList.size() || id <= 0) {
            throw BarryException.taskNotFound(total);
        }
        String deletedTask = taskList.get(id - 1).toString();
        System.out.println("\t" + "_".repeat(50));
        System.out.println("\t" + "Noted. I've removed this task:");
        taskList.remove(id - 1);
        System.out.println("\t\t" + deletedTask);
        System.out.println("\tNow you have " + (total - 1) + (total - 1 > 1 ? " tasks " : " task ") + "in the list.");
        System.out.println("\t" + "_".repeat(50));

    }

    /**
     * Marks the task as done or undone.
     *
     * @param command the user input containing the action and task index
     */
    public static void markTask(String command) throws BarryException {
        int total = taskList.size();
        String[] s = command.split(" ");
        String mark = s[0];
        int id = Integer.parseInt(s[1]);
        if (id > taskList.size() || id <= 0) {
            throw BarryException.taskNotFound(total);
        } else {
            System.out.println("\t" + "_".repeat(50));
            taskList.get(id - 1).setStatus(mark.equals("mark"));
            if (mark.equals("mark")) {
                System.out.println("\t" + "Nice! I've marked this task as done:");
            } else {
                System.out.println("\t" + "Ok! I've marked this task as not done yet:");
            }
            System.out.println("\t\t" + taskList.get(id - 1));
            System.out.println("\t" + "_".repeat(50));
        }
    }

    /**
     * Prints all tasks currently stored in taskList, with their status and index.
     */
    public static void printList() {
        System.out.println("\t" + "_".repeat(50));
        System.out.println("\tHere are the tasks in your list:");
        int i = 1;
        for (Task item : taskList) {
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
