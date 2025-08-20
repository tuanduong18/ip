public class BarryException extends  Exception {
    BarryException(String message) {
        super(message);
    }

    public static BarryException commandException() {
        return new BarryException("Invalid command, valid commands should have the pattern as one of these below:\n"
                +"\t\t* todo {name}\n"
                + "\t\t\te.g. todo CS2103T homework\n\n"
                + "\t\t* deadline {name} /by {date}\n"
                + "\t\t\te.g. deadline CS2103T hw /by 16:00 every Friday\n\n"
                + "\t\t* event {name} /from {time} /to {time}\n"
                + "\t\t\te.g. event Orbital Splashdown /from 17:30 /to 20:00 on Wednesday\n\n"
                + "\t\t* list\n\n"
                + "\t\t* mark {id}\n"
                + "\t\t\te.g. mark 3\n\n"
                + "\t\t* unmark {id}\n"
                + "\t\t\te.g. unmark 2\n\n"
                +"\t\t* bye");
    }

    public static BarryException missingNameException(String name) {
        return new BarryException("The description of a " + name + " cannot be empty.");
    }

    public static BarryException missingTimestamp(String name, String type, String attr) {
        return new BarryException("Missing the " + attr + " of the " + type + ": " + name);
    }

    public static BarryException taskNotFound(int total) {
        return new BarryException("Invalid index, task's index should be an int between 0 and " + (total + 1));
    }

}