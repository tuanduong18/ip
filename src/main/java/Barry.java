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

    }

    public static void endSession() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("_".repeat(50));
    }
}
