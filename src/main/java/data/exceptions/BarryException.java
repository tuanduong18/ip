package data.exceptions;

import commands.CommandType;

public class BarryException extends  Exception {
    public BarryException(String message) {
        super(message);
    }

    public static BarryException commandException() {
        return new BarryException("Invalid command, Type 'help' or 'help --details' for more information");
    }

    public static BarryException commandException(CommandType[] cmds) {
        StringBuilder s = new StringBuilder("Invalid command. Perhaps, you are mentioning one of these below:\n");
        for (CommandType cmd: cmds) {
            s.append("\t");
            s.append(cmd.getFormula());
            s.append("\n");
        }
        s.append("Type 'help' or 'help --details' for more information about the valid commands");
        return new BarryException(s.toString());
    }

    public static BarryException missingNameException(CommandType c) {
        return new BarryException("The description of a " + c.getType() + " cannot be empty.");
    }

    public static BarryException missingTimestamp(CommandType c, String attr) {
        return new BarryException("Missing the " + attr + " of the " + c.getType());
    }

    public static BarryException taskNotFound(int total) {
        return new BarryException("Invalid index, task's index should be an int between 0 and " + (total + 1));
    }

	public static BarryException invalidTimestamp(CommandType c, String s, String regx) {
		return new BarryException("Invalid time format of the " + c.getType() + "'s " + s + ". It should be " + regx);
	}

	public static BarryException invalidSourceFilePath() {
		return new BarryException("Invalid source file path");
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BarryException) {
			return o.toString().equals(this.toString());
		}
		return false;
	}
}