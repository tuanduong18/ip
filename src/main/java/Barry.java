import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Barry {
    private static ArrayList<Task> tasksList = new ArrayList<>();
    public static void main(String[] args) {
        greetings();
        startSession();
        endSession();
    }

    public static void greetings() {
        String name = "Barry";
        System.out.println("    " + "_".repeat(50));
        System.out.println("    " + "Hello I'm " + name);
        System.out.println("    " + "What can I do for you?");
        System.out.println("    " + "_".repeat(50));
    }

    public static void startSession() {
        Scanner scan = new Scanner(System.in);
        while(true) {
            String temp = scan.nextLine();
            if (temp.equals("bye")) {
                System.out.println("    " + "_".repeat(50));
                break;
            } else if (temp.equals("list")) {
                printList();
            } else if (Pattern.matches("(mark|unmark) (-|)[0-9]+", temp)) {
                markTask(temp);
            } else {
                addTask(temp);
            }

        }
    }

    public static void addTask(String name) {
        System.out.println("    " + "_".repeat(50));
        tasksList.add(new Task(name));
        System.out.println("    " + "added: " + name);
        System.out.println("    " + "_".repeat(50));
    }

    public static void markTask(String command) {
        String[] s = command.split(" ");
        String mark = s[0];
        int id = Integer.parseInt(s[1]);
        if (id > tasksList.size() || id < 0) {
            System.out.println("    " + "_".repeat(50));
            System.out.println("    " + "Invalid todo");
            System.out.println("    " + "_".repeat(50));
        } else {
            System.out.println("    " + "_".repeat(50));
            tasksList.get(id - 1).setStatus(mark.equals("mark"));
            if(mark.equals("mark")) {
                System.out.println("    " + "Nice! I've marked this task as done:");
            } else {
                System.out.println("    " + "Ok! I've marked this task as not done yet:");
            }
            System.out.println("        " + tasksList.get(id - 1));
            System.out.println("    " + "_".repeat(50));
        }
    }

    public static void printList() {
        System.out.println("    " + "_".repeat(50));
        int i = 1;
        for (Task item : tasksList) {
            System.out.println("    " + i + "." + item.toString());
            i++;
        }
        System.out.println("    " + "_".repeat(50));
    }

    public static void endSession() {
        System.out.println("    " + "Bye. Hope to see you again soon!");
        System.out.println("    " + "_".repeat(50));
    }
}
