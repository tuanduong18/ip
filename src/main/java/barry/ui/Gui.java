package barry.ui;

import java.util.ArrayList;
import java.util.HashMap;

import barry.data.common.CommandType;

/**
 * Builds user-facing messages for a graphical interface.
 * <p>
 * {@code Gui} mirrors the API of {@link Ui} but <em>returns</em> formatted text
 * instead of printing to the console. Each method constructs a multi-line response
 * string that callers can render in a GUI component.
 * </p>
 */
public class Gui {
    private static final String greeting = "Hello from Barry, what can I do for you?";

    /**
     * Joins the given lines into a single response string.
     * <p>
     * Each element is appended in order with a trailing line break, and existing
     * line breaks inside an element are preserved. The final response is trimmed
     * of trailing whitespace.
     * </p>
     *
     * @param strings the lines to include in the response
     * @return the concatenated, newline-separated response string
     */
    public String print(ArrayList<String> strings) {
        StringBuilder response = new StringBuilder();
        for (String s : strings) {
            response.append(s);
            response.append("\n");
        }
        return response.toString().trim();
    }

    /**
     * Returns the startup greeting message.
     *
     * @return the greeting text
     */
    public String printGreetings() {
        ArrayList<String> s = new ArrayList<>();
        s.add(greeting);
        return this.print(s);
    }

    /**
     * Returns a generic loading error message for data file access issues.
     *
     * @return the loading error text
     */
    public String showLoadingError() {
        ArrayList<String> s = new ArrayList<>();
        s.add("Cannot access data file");
        return this.print(s);
    }

    /**
     * Returns a confirmation message after adding a task, including the updated count.
     *
     * @param task the string representation of the added task
     * @param n    the total number of tasks after the addition
     * @return the add-confirmation text
     */
    public String printAddTask(String task, int n) {
        ArrayList<String> s = new ArrayList<>();
        s.add("Got it. I've added this task:");
        s.add("\t" + task);
        s.add("Now you have " + n + (n > 1 ? " tasks " : " task ") + "in the list.");
        return this.print(s);
    }

    /**
     * Returns a formatted listing of all tasks, numbered from 1.
     *
     * @param taskList the list of task strings to display
     * @return the formatted task list text
     */
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

    /**
     * Returns a confirmation message after marking or unmarking a task.
     *
     * @param task   the string representation of the affected task
     * @param marked {@code true} if the task is now marked as done; {@code false} if unmarked
     * @return the mark/unmark confirmation text
     */
    public String printMarkTask(String task, boolean marked) {
        String comment = marked
                ? "Nice! I've marked this task as done:"
                : "Ok! I've marked this task as not done yet:";

        ArrayList<String> s = new ArrayList<>();
        s.add(comment);
        s.add("\t" + task);
        return this.print(s);
    }

    /**
     * Returns a confirmation message after deleting a task, including the updated count.
     *
     * @param task the string representation of the removed task
     * @param n    the total number of remaining tasks
     * @return the delete-confirmation text
     */
    public String printDeleteTask(String task, int n) {
        ArrayList<String> s = new ArrayList<>();
        s.add("Noted. I've removed this task:");
        s.add("\t" + task);
        s.add("Now you have " + n + (n > 1 ? " tasks " : " task ") + "in the list.");
        return this.print(s);
    }

    /**
     * Returns the farewell message.
     *
     * @return the goodbye text
     */
    public String printGoodbye() {
        ArrayList<String> s = new ArrayList<>();
        s.add("Bye. Hope to see you again soon!");
        return this.print(s);
    }

    /**
     * Returns a concise help message listing all supported command tags.
     * <p>
     * Uses {@link CommandType#allCommands()} to generate the content.
     * </p>
     *
     * @return the concise help text
     */
    public String printHelp() {
        ArrayList<String> s = new ArrayList<>();
        s.add("The command must start with one of these below:");
        s.add(CommandType.allCommands());
        return this.print(s);
    }

    /**
     * Returns a detailed help message showing command formulas and examples.
     * <p>
     * Uses {@link CommandType#allCommandsDetailed()} to generate the content.
     * </p>
     *
     * @return the detailed help text
     */
    public String printDetailedHelp() {
        ArrayList<String> s = new ArrayList<>();
        s.add("The command must have the formula as one of these below:");
        s.add(CommandType.allCommandsDetailed());
        return this.print(s);
    }

    /**
     * Returns a formatted listing of tasks that match a search query,
     * or an empty-result message if none match.
     *
     * @param taskList the list of matching task strings
     * @return the matching-tasks text, or a not-found message when the list is empty
     */
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

    /**
     * Returns a formatted list of user-defined aliases.
     * <p>
     * Each alias is rendered on its own line in the form {@code <name> = <template>}.
     * If {@code aliases} is empty, the returned string is empty.
     * </p>
     * <p><strong>Ordering:</strong>
     * The output order follows the iteration order of the provided {@link HashMap},
     * which is unspecified and may vary across runs. If you require deterministic
     * ordering (e.g., for testing or UX consistency), copy and sort the keys before
     * formatting (e.g., case-insensitively) and then build the lines in that order.
     * </p>
     *
     * @param aliases a mapping from alias name to its expansion template
     * @return a newline-separated string where each line is {@code name = template}
     * @implNote This method does not sort the aliases. To produce a stable order,
     *           consider sorting the keys first and then constructing the lines.
     */
    public String printAliases(HashMap<String, String> aliases) {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>(aliases.keySet());
        keys.sort(String.CASE_INSENSITIVE_ORDER); // or null for natural
        lines.add("Here are your aliases, "
                + "{1} means the additional argument you need to type and "
                + "${sun} means upcoming Sunday:");
        for (String key : keys) {
            lines.add("\t" + key + " = " + aliases.get(key));
        }
        return this.print(lines);
    }
}
