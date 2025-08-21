public enum Command {
    TODO("todo",
            "todo {description}",
            "todo Do the laundry"),
    DEADLINE("deadline",
            "deadline {description} /by {date}",
            "deadline CS2103T /by Friday 16:00"),
    EVENT("event",
            "event {description} /from {time} /to {time}",
            "event Orbital Splashdown /from 16:00 to 20:00 on Wednesday"),
    MARK("mark", "mark {id}", "mark 2"),
    UNMARK("unmark", "unmark {id}", "unmark 3"),
    LIST("list", "list", "list"),
    DELETE("delete", "delete {id}", "delete 2"),
    BYE("bye", "bye", "bye"),
    HELP("help", "help", "help"),
    DETAILED_HELP("help --details", "help --details", "help --details");


    private final String type;
    private final String formula;
    private final String example;

    Command(String type, String formula, String example) {
        this.type = type;
        this.formula = formula;
        this.example = example;
    }

    public static Command parseCommand(String s) throws BarryException {
        for (Command c : Command.values()) {
            if (c.getType().equals(s)) {
                return c;
            }
        }
        throw BarryException.commandException();
    }

    public static String allCommands() {
        StringBuilder s = new StringBuilder("");
        for (Command c : Command.values()) {
            s.append("\t\t");
            s.append(c.getType());
            s.append("\n");
        }
        return s.toString();
    }

    public static String allCommandsDetailed() {
        StringBuilder s = new StringBuilder("");
        for (Command c : Command.values()) {
            s.append("\t\t");
            s.append(c.getFormula());
            s.append("\n");
            s.append("\t\t\te.g.: ");
            s.append(c.getExample());
            s.append("\n\n");
        }
        return s.toString();
    }

    public String getType() {
        return type;
    }

    public String getFormula() {
        return formula;
    }

    public String getExample() {
        return example;
    }
}