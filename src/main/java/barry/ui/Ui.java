package barry.ui;

import java.util.ArrayList;
import java.util.HashMap;

import barry.data.common.CommandType;

/**
 * Console-based user interface for rendering messages and task lists.
 * <p>
 * {@code Ui} prints framed output to standard out with a consistent style:
 * each message block is wrapped between lines of underscores and each line
 * is indented with a tab. Multi-line strings are re-indented so that line breaks
 * remain aligned.
 * </p>
 */
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

    /**
     * Prints the given lines inside a framed block with uniform indentation.
     * <p>
     * For each string in {@code strings}, all newline characters are replaced with
     * {@code "\n\t"} to maintain indentation across lines. A separator line of 50
     * underscores is printed before and after the block.
     * </p>
     *
     * @param strings the lines to print, in order
     */
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

    /**
     * Prints the startup banner and greeting message.
     */
    public void printGreetings() {
        ArrayList<String> s = new ArrayList<>();
        s.add(name);
        s.add(greeting);
        this.print(s);
    }

    /**
     * Prints a generic loading error message for data file access issues.
     */
    public void showLoadingError() {
        ArrayList<String> s = new ArrayList<>();
        s.add("Cannot access data file");
        this.print(s);
    }

    /**
     * Prints a confirmation message after adding a task, including the updated count.
     *
     * @param task the string representation of the added task
     * @param n    the total number of tasks after the addition
     */
    public void printAddTask(String task, int n) {
        ArrayList<String> s = new ArrayList<>();
        s.add("Got it. I've added this task:");
        s.add("\t" + task);
        s.add("Now you have " + n + (n > 1 ? "tasks " : " task ") + "in the list.");
        this.print(s);
    }

    /**
     * Prints all tasks in the list, numbered starting from 1.
     *
     * @param taskList the list of task strings to display
     */
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

    /**
     * Prints a confirmation message after marking or unmarking a task.
     *
     * @param task   the string representation of the affected task
     * @param marked {@code true} if the task is now marked as done; {@code false} if unmarked
     */
    public void printMarkTask(String task, boolean marked) {
        String comment = marked
                ? "Nice! I've marked this task as done:"
                : "Ok! I've marked this task as not done yet:";

        ArrayList<String> s = new ArrayList<>();
        s.add(comment);
        s.add("\t" + task);
        this.print(s);
    }

    /**
     * Prints a confirmation message after deleting a task, including the updated count.
     *
     * @param task the string representation of the removed task
     * @param n    the total number of remaining tasks
     */
    public void printDeleteTask(String task, int n) {
        ArrayList<String> s = new ArrayList<>();
        s.add("Noted. I've removed this task:");
        s.add("\t" + task);
        s.add("Now you have " + n + (n > 1 ? "tasks " : " task ") + "in the list.");
        this.print(s);
    }

    /**
     * Prints the goodbye message.
     */
    public void printGoodbye() {
        ArrayList<String> s = new ArrayList<>();
        s.add("Bye. Hope to see you again soon!");
        this.print(s);
    }

    /**
     * Prints a concise help message listing all supported command tags.
     * <p>
     * Uses {@link CommandType#allCommands()} to generate the content.
     * </p>
     */
    public void printHelp() {
        ArrayList<String> s = new ArrayList<>();
        s.add("The command must start with one of these below:");
        s.add(CommandType.allCommands());
        this.print(s);
    }

    /**
     * Prints a detailed help message showing command formulas and examples.
     * <p>
     * Uses {@link CommandType#allCommandsDetailed()} to generate the content.
     * </p>
     */
    public void printDetailedHelp() {
        ArrayList<String> s = new ArrayList<>();
        s.add("The command must have the formula as one of these below:");
        s.add(CommandType.allCommandsDetailed());
        this.print(s);
    }

    /**
     * Prints matching tasks for a search query, or an empty-result message if none match.
     *
     * @param taskList the list of matching task strings
     */
    public void printMatchingTasks(ArrayList<String> taskList) {
        ArrayList<String> s = new ArrayList<>();
        if (taskList.isEmpty()) {
            s.add("Oops! There isn't any task match your search");
            this.print(s);
            return;
        }
        s.add("Here are the matching tasks in your list:");
        int i = 1;
        for (String item : taskList) {
            s.add("\t" + i + "." + item);
            i++;
        }
        this.print(s);
    }

    public void printAliases(HashMap<String, String> aliases) {
        ArrayList<String> s = new ArrayList<>();
        for (String key: aliases.keySet()) {
            s.add(key + " = " + aliases.get(key));
        }
        this.print(s);
    }

}
