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
    private static final ArrayList<Task> taskList = new ArrayList<>();
    public static void main(String[] args) {
        greetings();
		fetchData();
        startSession();
        endSession();
    }

	public static void fetchData() {
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

	public static void saveData() {
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
     * Prints the greeting banner and a short introduction for the chatbot.
     */
    public static void greetings() {
        String name = """
                \t$$$$$$$\\
                \t$$  __$$\\
                \t$$ |  $$ | $$$$$$\\   $$$$$$\\   $$$$$$\\  $$\\   $$\\
                \t$$$$$$$\\ | \\____$$\\ $$  __$$\\ $$  __$$\\ $$ |  $$ |
                \t$$  __$$\\  $$$$$$$ |$$ |  \\__|$$ |  \\__|$$ |  $$ |
                \t$$ |  $$ |$$  __$$ |$$ |      $$ |      $$ |  $$ |
                \t$$$$$$$  |\\$$$$$$$ |$$ |      $$ |      \\$$$$$$$ |
                \t\\_______/  \\_______|\\__|      \\__|       \\____$$ |
                \t                                        $$\\   $$ |
                \t                                        \\$$$$$$  |
                \t                                         \\______/""";
        System.out.println("\t" + "_".repeat(50));
        System.out.println("\t" + "Hello from\n\n" + name);
        System.out.println("\t" + "What can I do for you?");
        System.out.println("\t" + "_".repeat(50));
    }

    /**
     * Runs the main loop to read and process user commands.
     */
    public static void startSession() {
        Scanner scan = new Scanner(System.in);
        while(true) {
            try {
                String temp = scan.nextLine();
                String tag = temp.split(" ")[0];
                switch (Command.parseCommand(tag)) {
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
                        System.out.println("\t" + "_".repeat(50));
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
                System.out.println("\t" + "_".repeat(50));
                System.out.println("\t" + "OOPS!!! " + e.getMessage());
                System.out.println("\t" + "_".repeat(50));
            }
        }
    }

    /**
     * Adds a new task to the list.
     *
     * @param content the command string describing the task
     */
    public static void addTask(String content) throws BarryException {
        if (Pattern.matches("todo [^|]*", content)) {
            String name = content.substring(5);
            if (name.trim().isEmpty()) {
              throw BarryException.missingNameException(Command.TODO);
            }
            taskList.add(new Todo(name));
        } else if (Pattern.matches("deadline [^|]* /by [^|]*", content)) {
            String[] ss = content.substring(9).split(" /by ", 2);
            if (ss[0].trim().isEmpty()) {
                throw BarryException.missingNameException(Command.DEADLINE);
            } else if (ss[1].trim().isEmpty()) {
                throw BarryException.missingTimestamp(Command.DEADLINE, "due date");
            }
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	        LocalDateTime due;
	        try {
		        due = LocalDateTime.parse(ss[1].trim(), formatter);
	        } catch (DateTimeParseException e) {
		        throw BarryException.invalidTimestamp(Command.DEADLINE, "due date", "dd/MM/yyyy HH:mm");
	        }
	        taskList.add(new Deadline(ss[0], due));
        } else if ((Pattern.matches("event [^|]* /from [^|]* /to [^|]*", content))) {
            String[] s1 = content.substring(6).split(" /from ", 2);
            String[] s2 = s1[1].split(" /to ", 2);
            if (s1[0].trim().isEmpty()) {
                throw BarryException.missingNameException(Command.EVENT);
            } else if (s2[0].trim().isEmpty()) {
                throw BarryException.missingTimestamp(Command.EVENT, "starting time");
            } else if (s2[1].trim().isEmpty()) {
                throw BarryException.missingTimestamp(Command.EVENT, "ending time");
            }
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	        LocalDateTime start, end;
	        try {
		        start = LocalDateTime.parse(s2[0].trim(), formatter);
	        } catch (DateTimeParseException e) {
		        throw BarryException.invalidTimestamp(Command.EVENT, "start time", "dd/MM/yyyy HH:mm");
	        }
			try {
		        end = LocalDateTime.parse(s2[1].trim(), formatter);
	        } catch (DateTimeParseException e) {
		        throw BarryException.invalidTimestamp(Command.EVENT, "end time", "dd/MM/yyyy HH:mm");
	        }
            taskList.add(new Event(s1[0], start, end));
        } else {
            throw BarryException.commandException(new Command[]{Command.TODO, Command.DEADLINE, Command.EVENT});
        }
        System.out.println("\t" + "_".repeat(50));
        int n = taskList.size();
        System.out.println("\t" + "Got it. I've added this task:");
        System.out.println("\t\t" + taskList.get(n - 1));
        System.out.println("\tNow you have " + n + (n > 1 ? " tasks " : " task ") + "in the list.");
        System.out.println("\t" + "_".repeat(50));
    }

    /**
     * Deletes a task from the list.
     *
     * @param command the command string describing the task
     */
    public static void deleteTask(String command) throws BarryException {
        if (!(Pattern.matches("delete [0-9]+", command))) {
            throw BarryException.commandException(new Command[]{Command.DELETE});
        }
        int total = taskList.size();
        String[] s = command.split(" ");
        int id = Integer.parseInt(s[1]);
        if (id > taskList.size() || id <= 0) {
            throw BarryException.taskNotFound(total);
        }
        String deletedTask = taskList.get(id - 1).toString();
        System.out.println("\t" + "_".repeat(50));
        System.out.println("\t" + "Noted. I've removed this task:");
        taskList.remove(id - 1);
        System.out.println("\t\t" + deletedTask);
        System.out.println("\tNow you have " + (total - 1) + (total - 1 > 1 ? " tasks " : " task ") + "in the list.");
        System.out.println("\t" + "_".repeat(50));

    }

    /**
     * Marks the task as done or undone.
     *
     * @param command the user input containing the action and task index
     */
    public static void markTask(String command) throws BarryException {
        if (!(Pattern.matches("(mark|unmark) [0-9]+", command))) {
            throw BarryException.commandException(new Command[]{Command.MARK, Command.UNMARK});
        }
        int total = taskList.size();
        String[] s = command.split(" ");
        String mark = s[0];
        int id = Integer.parseInt(s[1]);
        if (id > taskList.size() || id <= 0) {
            throw BarryException.taskNotFound(total);
        } else {
            System.out.println("\t" + "_".repeat(50));
            taskList.get(id - 1).setStatus(mark.equals("mark"));
            if (mark.equals("mark")) {
                System.out.println("\t" + "Nice! I've marked this task as done:");
            } else {
                System.out.println("\t" + "Ok! I've marked this task as not done yet:");
            }
            System.out.println("\t\t" + taskList.get(id - 1));
            System.out.println("\t" + "_".repeat(50));
        }
    }

    /**
     * Prints all tasks currently stored in taskList, with their status and index.
     */
    public static void printList(String command) throws BarryException {
        if (!command.equals("list")) {
            throw BarryException.commandException(new Command[]{Command.LIST});
        }
        System.out.println("\t" + "_".repeat(50));
        System.out.println("\tHere are the tasks in your list:");
        int i = 1;
        for (Task item : taskList) {
            System.out.println("\t" + i + "." + item.toString());
            i++;
        }
        System.out.println("\t" + "_".repeat(50));
    }

    public static void printHelp(String s) throws BarryException{
        if (!s.equals("help") && !s.equals("help --details"))  {
            throw BarryException.commandException(new Command[]{Command.HELP, Command.DETAILED_HELP});
        }

        System.out.println("\t" + "_".repeat(50));
        if (s.equals("help")) {
            System.out.println("\tThe command must start with one of these below:");
            System.out.print(Command.allCommands());
        } else {
            System.out.println("\tThe command must have the formula as one of these below:");
            System.out.print(Command.allCommandsDetailed());
        }
        System.out.println("\t" + "_".repeat(50));
    }

    /**
     * Prints the goodbye message.
     */
    public static void endSession() {
        System.out.println("\t" + "Bye. Hope to see you again soon!");
        System.out.println("\t" + "_".repeat(50));
    }
}
