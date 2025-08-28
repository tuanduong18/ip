import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class Barry {
    private final ArrayList<Task> taskList = new ArrayList<>();

	private final Ui ui;

	public Barry() {
		this.ui = new Ui();

	}

	public void run() {
		ui.printGreetings();
		fetchData();
		startSession();
		endSession();
	}

	public static void main(String[] args) {
		new Barry().run();
	}


	public void fetchData() {
		Path path = Paths.get("..","data", "Barry.txt");
		File f = path.toFile();
		try {
			Scanner s = new Scanner(f);
			while (s.hasNextLine()) {
				String temp = s.nextLine();
				String[] cmd = temp.split(" \\| ", 5);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
				try {
					String type = cmd[0];
					String marked = cmd[1];
					String name = cmd[2];
					switch (type) {
					case "T":
						taskList.add(new Todo(name));
						break;
					case "D":
						String by = cmd[3];
						LocalDateTime due = LocalDateTime.parse(by, formatter);
						taskList.add(new Deadline(name, due));
						break;
					case "E":
						LocalDateTime start = LocalDateTime.parse(cmd[3], formatter);
						LocalDateTime end = LocalDateTime.parse(cmd[4], formatter);
						taskList.add(new Event(name, start, end));
						break;
					}
					taskList.get(taskList.toArray().length - 1).setStatus(marked.equals("1"));

				} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
					continue;
				}
			}
		} catch (FileNotFoundException e) {
			try{
				f.getParentFile().mkdirs(); // make sure "data" folder exists
				f.createNewFile();

			} catch (IOException ee) {
				ee.printStackTrace();
			}

		}

	}

	public void saveData() {
		StringBuilder s = new StringBuilder();
		for (Task task : taskList) {
			String t = task.toString();
			char type = t.charAt(1);
			boolean marked = t.charAt(4) == 'X';
			String command = t.substring(7);
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("h:mm a d MMM, yyyy");
			DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			switch (type) {
			case 'T':
				s.append(type);
				s.append(" | ");
				s.append(marked ? "1" : "0");
				s.append(" | ");
				s.append(command);
				s.append(System.lineSeparator());
				break;
			case 'D':
				String[] details = command.split(" \\(by: " , 2);
				s.append(type);
				s.append(" | ");
				s.append(marked ? "1" : "0");
				s.append(" | ");
				s.append(details[0]);
				s.append(" | ");
				String due = details[1].substring(0, details[1].length() - 1);
				LocalDateTime dueParsed = LocalDateTime.parse(due, formatter1);
				due = dueParsed.format(formatter2);
				s.append(due);
				s.append(System.lineSeparator());
				break;
			case 'E':
				String name = command.split(" \\(from: ", 2)[0];
				String start = command.split(" \\(from: ", 2)[1].split(" to: ", 2)[0];
				String end = command.split(" \\(from: ", 2)[1].split(" to: ", 2)[1];
				end = end.substring(0, end.length() - 1);
				start = LocalDateTime.parse(start, formatter1).format(formatter2);
				end = LocalDateTime.parse(end, formatter1).format(formatter2);
				s.append(type);
				s.append(" | ");
				s.append(marked ? "1" : "0");
				s.append(" | ");
				s.append(name);
				s.append(" | ");
				s.append(start);
				s.append(" | ");
				s.append(end);
				s.append(System.lineSeparator());
				break;
			}
		}
		try {
			Path path = Paths.get("..","data", "Barry.txt");
			File f = path.toFile();
			FileWriter fw = new FileWriter(f);
			fw.write(s.toString());
			fw.close();
		} catch (IOException e) {
			System.out.println("Something went wrong: " + e.getMessage());
		}
	}
	
    /**
     * Runs the main loop to read and process user commands.
     */
    public void startSession() {
        Scanner scan = new Scanner(System.in);
        while(true) {
            try {
                String temp = scan.nextLine();
                String tag = temp.split(" ")[0];
                switch (CommandType.parseCommand(tag)) {
                case TODO:
                case DEADLINE:
                case EVENT:
                    addTask(temp);
                    break;

                case MARK:
                case UNMARK:
                    markTask(temp);
                    break;

                case DELETE:
                    deleteTask(temp);
                    break;

                case LIST:
                    printList(temp);
                    break;

                case BYE:
                    if(temp.equals("bye")) {
                        return;
                    } else {
                        throw BarryException.commandException();
                    }

                case HELP:
                case DETAILED_HELP:
                    printHelp(temp);
                    break;

                default:
                    throw BarryException.commandException();
                }
				saveData();
            } catch (BarryException e) {
				ArrayList<String> s = new ArrayList<>();
				s.add("OOPS!!!");
				s.add(e.getMessage());
                ui.print(s);
            }
        }
    }

    /**
     * Adds a new task to the list.
     *
     * @param content the command string describing the task
     */
    public void addTask(String content) throws BarryException {
        if (Pattern.matches("todo [^|]*", content)) {
            String name = content.substring(5);
            if (name.trim().isEmpty()) {
              throw BarryException.missingNameException(CommandType.TODO);
            }
            taskList.add(new Todo(name));
        } else if (Pattern.matches("deadline [^|]* /by [^|]*", content)) {
            String[] ss = content.substring(9).split(" /by ", 2);
            if (ss[0].trim().isEmpty()) {
                throw BarryException.missingNameException(CommandType.DEADLINE);
            } else if (ss[1].trim().isEmpty()) {
                throw BarryException.missingTimestamp(CommandType.DEADLINE, "due date");
            }
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	        LocalDateTime due;
	        try {
		        due = LocalDateTime.parse(ss[1].trim(), formatter);
	        } catch (DateTimeParseException e) {
		        throw BarryException.invalidTimestamp(CommandType.DEADLINE, "due date", "dd/MM/yyyy HH:mm");
	        }
	        taskList.add(new Deadline(ss[0], due));
        } else if ((Pattern.matches("event [^|]* /from [^|]* /to [^|]*", content))) {
            String[] s1 = content.substring(6).split(" /from ", 2);
            String[] s2 = s1[1].split(" /to ", 2);
            if (s1[0].trim().isEmpty()) {
                throw BarryException.missingNameException(CommandType.EVENT);
            } else if (s2[0].trim().isEmpty()) {
                throw BarryException.missingTimestamp(CommandType.EVENT, "starting time");
            } else if (s2[1].trim().isEmpty()) {
                throw BarryException.missingTimestamp(CommandType.EVENT, "ending time");
            }
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	        LocalDateTime start, end;
	        try {
		        start = LocalDateTime.parse(s2[0].trim(), formatter);
	        } catch (DateTimeParseException e) {
		        throw BarryException.invalidTimestamp(CommandType.EVENT, "start time", "dd/MM/yyyy HH:mm");
	        }
			try {
		        end = LocalDateTime.parse(s2[1].trim(), formatter);
	        } catch (DateTimeParseException e) {
		        throw BarryException.invalidTimestamp(CommandType.EVENT, "end time", "dd/MM/yyyy HH:mm");
	        }
            taskList.add(new Event(s1[0], start, end));
        } else {
            throw BarryException.commandException(new CommandType[]{CommandType.TODO, CommandType.DEADLINE, CommandType.EVENT});
        }
	    int n = taskList.size();
		ArrayList<String> s = new ArrayList<>();
		s.add("Got it. I've added this task:");
		s.add("\t" + taskList.get(n - 1));
		s.add("Now you have \" + n + (n > 1 ? \" tasks \" : \" task \") + \"in the list.");
        ui.print(s);
    }

    /**
     * Deletes a task from the list.
     *
     * @param command the command string describing the task
     */
    public void deleteTask(String command) throws BarryException {
        if (!(Pattern.matches("delete [0-9]+", command))) {
            throw BarryException.commandException(new CommandType[]{CommandType.DELETE});
        }
        int total = taskList.size();
        String[] ss = command.split(" ");
        int id = Integer.parseInt(ss[1]);
        if (id > taskList.size() || id <= 0) {
            throw BarryException.taskNotFound(total);
        }
	    String deletedTask = taskList.get(id - 1).toString();
		ArrayList<String> s = new ArrayList<>();
		s.add("Noted. I've removed this task:");
		s.add("\t" + deletedTask);
		s.add("Now you have " + (total - 1) + (total - 1 > 1 ? " tasks " : " task ") + "in the list.");
        taskList.remove(id - 1);
		ui.print(s);
    }

    /**
     * Marks the task as done or undone.
     *
     * @param command the user input containing the action and task index
     */
    public void markTask(String command) throws BarryException {
        if (!(Pattern.matches("(mark|unmark) [0-9]+", command))) {
            throw BarryException.commandException(new CommandType[]{CommandType.MARK, CommandType.UNMARK});
        }
        int total = taskList.size();
        String[] ss = command.split(" ");
        String mark = ss[0];
        int id = Integer.parseInt(ss[1]);
        if (id > taskList.size() || id <= 0) {
            throw BarryException.taskNotFound(total);
        } else {

			String comment = mark.equals("mark")
					?  "Nice! I've marked this task as done:"
					: "Ok! I've marked this task as not done yet:";

	        ArrayList<String> s = new ArrayList<>();
	        s.add(comment);
	        s.add("\t" + taskList.get(id - 1));
            ui.print(s);
        }
    }

    /**
     * Prints all tasks currently stored in taskList, with their status and index.
     */
    public void printList(String command) throws BarryException {
        if (!command.equals("list")) {
            throw BarryException.commandException(new CommandType[]{CommandType.LIST});
        }
		ArrayList<String> s = new ArrayList<>();
        s.add("Here are the tasks in your list:");
        int i = 1;
        for (Task item : taskList) {
            s.add("\t" + i + "." + item.toString());
            i++;
        }
        ui.print(s);
    }

    public void printHelp(String ss) throws BarryException{
        if (!ss.equals("help") && !ss.equals("help --details"))  {
            throw BarryException.commandException(new CommandType[]{CommandType.HELP, CommandType.DETAILED_HELP});
        }
	    ArrayList<String> s = new ArrayList<>();
        if (ss.equals("help")) {
			s.add("The command must start with one of these below:");
			s.add(CommandType.allCommands());
        } else {
	        s.add("The command must have the formula as one of these below:");
	        s.add(CommandType.allCommandsDetailed());
        }
		ui.print(s);
    }

    /**
     * Prints the goodbye message.
     */
    public void endSession() {
	    ArrayList<String> s = new ArrayList<>();
		s.add("Bye. Hope to see you again soon!");
        ui.print(s);
    }
}
