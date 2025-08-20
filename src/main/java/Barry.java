import java.util.Scanner;

public class Barry {
    public static void main(String[] args) {
        greetings();
        startSession();
        endSession();
    }

    public static void greetings() {
        String name = "Barry";
        System.out.println("_".repeat(50));
        System.out.println("Hello I'm " + name);
        System.out.println("What can I do for you?");
        System.out.println("_".repeat(50));
    }

    public static void startSession() {
        Scanner scan = new Scanner(System.in);
        while(true) {
            String temp = scan.nextLine();
            if(temp.equals("bye")) {
                System.out.println("_".repeat(50));
                break;
            }
            System.out.println("_".repeat(50));
            System.out.println(temp);
            System.out.println("_".repeat(50));
        }
    }

    public static void endSession() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("_".repeat(50));
    }
}
