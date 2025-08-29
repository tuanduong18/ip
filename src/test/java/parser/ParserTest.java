package parser;

import commands.*;
import data.exceptions.BarryException;
import org.junit.jupiter.api.Test;
import tasks.Deadline;
import tasks.Todo;
import commands.CommandType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static commands.CommandType.*;

public class ParserTest {
	Parser parser = new Parser();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Test
	void parse_addCommand_todo() {
		// given
		String input = "todo read book";
		Command expected = new AddCommand(new Todo("read book"));

		try {
			Command actual = parser.parseCommand(input);
			assertEquals(expected, actual);
		} catch (BarryException e) {
			fail("Wrong Exception thrown: " + e.getMessage());
		}
	}

	@Test
	void parse_nearlyCorrect_addCommand_todo_throwsException() {
		// given
		String input = "todo    ";
		BarryException expected = BarryException.missingNameException(TODO);

		try {
			Command cmd = parser.parseCommand(input);
			fail("No exception thrown!!!");
		} catch (BarryException actual) {
			assertEquals(expected, actual);
		}
	}

	@Test
	void parse_addCommand_deadline() {
		// given
		String input = "deadline CS2103T /by 29/08/2025 16:00";

		try {
			LocalDateTime due = LocalDateTime.parse("29/08/2025 16:00", formatter);
			Command expected = new AddCommand(new Deadline("CS2103T", due));

			try {
				Command actual = parser.parseCommand(input);
				assertEquals(expected, actual);
			} catch (BarryException e) {
				fail("Wrong Exception thrown: " + e.getMessage());
			}
		} catch (DateTimeParseException e) {
			fail("Wrong Exception thrown: " + e.getMessage());
		}
	}

	@Test
	void parse_wrongCommand_throwsException() {
		// given
		String input = "blah";
		BarryException expected = BarryException.commandException();

		try {
			Command cmd = parser.parseCommand(input);
			fail("No exception thrown!!!");
		} catch (BarryException actual) {
			assertEquals(expected,actual);
		}
	}
}
