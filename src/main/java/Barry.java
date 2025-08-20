import java.util.ArrayList;
import java.util.Scanner;

public class Barry {
    private static ArrayList<String> todosList = new ArrayList<>();
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
                continue;
            }
            System.out.println("    " + "_".repeat(50));
            todosList.add(temp);
            System.out.println("    " + "added: " + temp);
            System.out.println("    " + "_".repeat(50));
        }
    }

    public static void printList() {
        System.out.println("    " + "_".repeat(50));
        int i = 1;
        for (String item : todosList) {
            System.out.println("    " + i + ". " + item);
            i++;
        }
        System.out.println("    " + "_".repeat(50));
    }

    public static void endSession() {
        System.out.println("    " + "Bye. Hope to see you again soon!");
        System.out.println("    " + "_".repeat(50));
    }
}
