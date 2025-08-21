public class BarryException extends  Exception {
    BarryException(String message) {
        super(message);
    }

    public static BarryException commandException() {
        return new BarryException("Invalid command, Type 'help' or 'help --details' for more information");
    }

    public static BarryException commandException(Command[] cmds) {
        StringBuilder s = new StringBuilder("Invalid command. Perhaps, you are mentioning one of these below:\n");
        for (Command cmd: cmds) {
            s.append("\t\t");
            s.append(cmd.getFormula());
            s.append("\n");
        }
        s.append("\tType 'help' or 'help --details' for more information about the valid commands");
        return new BarryException(s.toString());
    }

    public static BarryException missingNameException(Command c) {
        return new BarryException("The description of a " + c.getType() + " cannot be empty.");
    }

    public static BarryException missingTimestamp(Command c, String attr) {
        return new BarryException("Missing the " + attr + " of the " + c.getType());
    }

    public static BarryException taskNotFound(int total) {
        return new BarryException("Invalid index, task's index should be an int between 0 and " + (total + 1));
    }
}