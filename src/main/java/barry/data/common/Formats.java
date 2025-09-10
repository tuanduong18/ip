package barry.data.common;

/**
 * Shared date/time format constants to keep parsing/formatting consistent.
 */
public final class Formats {
    /** Command input format, e.g., 30/08/2025 16:00 */
    public static final String CMD_DATETIME = "dd/MM/yyyy HH:mm";
    /** UI appearance format, e.g., 4:00 PM 30 Aug, 2025 */
    public static final String UI_DATETIME = "h:mm a d MMM, yyyy";

    private Formats() {}
}
