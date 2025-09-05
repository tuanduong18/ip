package tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a d MMM, yyyy");

    @Test
    void create_deadline() {
        LocalDateTime due = LocalDateTime.now();
        Deadline actual = new Deadline("CS2103T", due);
        String expected = String.format("[D][ ] CS2103T (by: %s)", formatter.format(due));
        assertEquals(actual.toString(), expected);
    }

    @Test
    void mark_deadline() {
        LocalDateTime due = LocalDateTime.now();
        Deadline actual = new Deadline("CS2103T", due);
        actual.setStatus(true);
        String expected = String.format("[D][X] CS2103T (by: %s)", formatter.format(due));
        assertEquals(actual.toString(), expected);
    }
}
