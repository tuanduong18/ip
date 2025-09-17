package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import barry.commands.AddTask;
import barry.commands.Command;
import barry.commands.DeleteTask;
import barry.commands.Exit;
import barry.commands.FindTask;
import barry.commands.HelpCommand;
import barry.commands.ListTasks;
import barry.commands.MarkTask;
import barry.data.exceptions.BarryException;
import barry.parser.CommandParser;
import barry.tasks.Deadline;
import barry.tasks.Event;
import barry.tasks.Todo;

public class CommandParserTest {

    private final CommandParser parser = new CommandParser();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // -------- happy paths --------

    @Test
    void todo_parse_ok() throws BarryException {
        String input = "todo read book";
        Command expected = new AddTask(new Todo("read book"));
        assertEquals(expected, parser.parseCommand(input));
    }

    @Test
    void deadline_parse_ok() throws BarryException {
        String input = "deadline CS2103T /by 29/08/2025 16:00";
        LocalDateTime due = LocalDateTime.parse("29/08/2025 16:00", fmt);
        Command expected = new AddTask(new Deadline("CS2103T", due));
        assertEquals(expected, parser.parseCommand(input));
    }

    @Test
    void event_parse_ok() throws BarryException {
        String input = "event Splashdown /from 27/08/2025 18:00 /to 27/08/2025 21:00";
        LocalDateTime start = LocalDateTime.parse("27/08/2025 18:00", fmt);
        LocalDateTime end = LocalDateTime.parse("27/08/2025 21:00", fmt);
        Command expected = new AddTask(new Event("Splashdown", start, end));
        assertEquals(expected, parser.parseCommand(input));
    }

    @Test
    void mark_ok() throws BarryException {
        assertEquals(new MarkTask(2, true), parser.parseCommand("mark 2"));
    }

    @Test
    void unmark_ok() throws BarryException {
        assertEquals(new MarkTask(3, false), parser.parseCommand("unmark 3"));
    }

    @Test
    void delete_ok() throws BarryException {
        assertEquals(new DeleteTask(5), parser.parseCommand("delete 5"));
    }

    @Test
    void list_ok() throws BarryException {
        assertEquals(new ListTasks(), parser.parseCommand("list"));
    }

    @Test
    void help_ok() throws BarryException {
        assertEquals(new HelpCommand(false), parser.parseCommand("help"));
    }

    @Test
    void help_details_ok() throws BarryException {
        assertEquals(new HelpCommand(true), parser.parseCommand("help --details"));
    }

    @Test
    void find_ok() throws BarryException {
        assertInstanceOf(FindTask.class, parser.parseCommand("find book"));
    }

    @Test
    void bye_ok() throws BarryException {
        assertInstanceOf(Exit.class, parser.parseCommand("bye"));
    }

    // -------- error cases (assert on message to avoid static-factory drift) --------

    @Test
    void todo_missing_desc() {
        BarryException ex = assertThrows(BarryException.class, (
        ) -> parser.parseCommand("todo    "));
        assertEquals("The description of a todo cannot be empty.", ex.getMessage());
    }

    @Test
    void deadline_missing_due() {
        BarryException ex = assertThrows(BarryException.class, (
                ) -> parser.parseCommand("deadline Project /by   "));
        assertEquals("Missing the due date of the deadline", ex.getMessage());
    }

    @Test
    void deadline_bad_format() {
        BarryException ex = assertThrows(BarryException.class, (
                ) -> parser.parseCommand("deadline iP /by 29-08-2025 16:00"));
        assertEquals("Invalid time format of the deadline's due date. It should be dd/MM/yyyy HH:mm",
                ex.getMessage());
    }

    @Test
    void event_missing_desc() {
        BarryException ex = assertThrows(BarryException.class, (
                ) -> parser.parseCommand("event  /from 27/08/2025 18:00 /to 27/08/2025 19:00"));
        assertEquals("The description of a event cannot be empty.", ex.getMessage());
    }

    @Test
    void event_missing_start() {
        BarryException ex = assertThrows(BarryException.class, (
                ) -> parser.parseCommand("event Meeting /from   /to 27/08/2025 19:00"));
        assertEquals("Missing the starting time of the event", ex.getMessage());
    }

    @Test
    void event_missing_end() {
        BarryException ex = assertThrows(BarryException.class, (
                ) -> parser.parseCommand("event Meeting /from 27/08/2025 18:00 /to   "));
        assertEquals("Missing the ending time of the event", ex.getMessage());
    }

    @Test
    void event_bad_format() {
        BarryException ex = assertThrows(BarryException.class, (
                ) -> parser.parseCommand("event Meeting /from 27-08-2025 18:00 /to 27/08/2025 19:00"));
        assertEquals("Invalid time format of the event's start time. It should be dd/MM/yyyy HH:mm",
                ex.getMessage());
    }

    @Test
    void unknown_cmd_throws() {
        BarryException ex = assertThrows(BarryException.class, (
                ) -> parser.parseCommand("blah"));
        assertEquals("Invalid command, Type 'help' or 'help --details' for more information", ex.getMessage());
    }
}
