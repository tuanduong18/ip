package barry.alias;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import barry.data.common.Formats;
import barry.data.exceptions.BarryException;

/**
 * Expands a leading alias token in a user command into its canonical form.
 * <p>
 * This expander supports aliases that have <b>at most one parameter</b> and a simple
 * date macro for convenience:
 * </p>
 * <ul>
 *   <li><b>Single parameter</b>: if the template contains {@code {1}}, it will be replaced
 *       by the remainder of the input after the alias token (captured using
 *       {@code split(" ", 2)}).</li>
 *   <li><b>Sunday macro</b>: the token {@code ${sun}} is replaced with the upcoming
 *       Sunday's date at {@code 23:59}, formatted using {@link Formats#CMD_DATETIME}.</li>
 * </ul>
 *
 * <h3>Examples</h3>
 * <pre>{@code
 * # ~/.barryrc
 * alias ls='list'
 * alias rm='delete {1}'
 * alias dl_sun='deadline {1} /by ${sun}'
 *
 * Input:  "ls"
 * Output: "list"
 *
 * Input:  "rm 2"
 * Output: "delete 2"
 *
 * Input:  "dl_sun read book"
 * Output: "deadline read book /by 21/09/2025 23:59"
 * }</pre>
 */
public final class AliasExpander {

    private static final String PLACEHOLDER = "{1}";
    private static final String MACRO_SUN = "${sun}";
    private static final DateTimeFormatter OUT_FMT =
            DateTimeFormatter.ofPattern(Formats.CMD_DATETIME);

    private final AliasStorage storage;

    /**
     * Creates an {@code AliasExpander} bound to the given storage.
     *
     * @param storage the alias storage from which templates are retrieved
     */
    public AliasExpander(AliasStorage storage) {
        this.storage = storage;
    }

    /**
     * Expands the input once if its first token is an alias; otherwise returns the input unchanged.
     * <p>
     * The first whitespace-delimited token is treated as the alias name. If a template is found,
     * the remainder of the input (if any) is treated as the sole parameter and substituted into
     * {@code {1}}. Finally, known macros (e.g., {@code ${sun}}) are resolved.
     * </p>
     *
     * @param input the raw user command
     * @return the expanded command if an alias was matched; otherwise the original input
     * @throws BarryException if the template contains {@code {1}} but no parameter was provided
     */
    public String expand(String input) throws BarryException {
        if (input == null || input.isBlank()) {
            return input;
        }

        // Split into alias + (optional) remainder using split(" ", 2)
        String[] parts = input.trim().split(" ", 2);
        String tag = parts[0].toLowerCase();
        String template = storage.get(tag);

        if (template == null) {
            // Not an alias; leave line unchanged
            return input;
        }

        boolean needsParam = template.contains(PLACEHOLDER);
        String param = (parts.length > 1) ? parts[1].trim() : "";

        if (needsParam && param.isEmpty()) {
            throw new BarryException("Alias '" + tag + "' requires one parameter.");
        }

        // Substitute parameter (if any)
        String expanded = needsParam ? template.replace(PLACEHOLDER, param) : template;

        // Resolve macros (currently supports ${sun})
        expanded = applyMacros(expanded);

        return expanded;
    }

    // ---- macro helpers ----

    /** Replaces known macro tokens in-place (currently only ${sun}). */
    private static String applyMacros(String s) {
        if (s.contains(MACRO_SUN)) {
            String sun = formatUpcomingSunday2359();
            s = s.replace(MACRO_SUN, sun);
        }
        return s;
    }

    /** Formats the upcoming Sunday (today if Sunday) at 23:59 using Formats.CMD_DATETIME. */
    private static String formatUpcomingSunday2359() {
        LocalDate today = LocalDate.now(); // or use ZoneId if you need a fixed TZ
        int dow = today.getDayOfWeek().getValue(); // 1=Mon ... 7=Sun
        int daysUntilSunday = 7 - dow; // 0 if today is Sunday
        LocalDate sunday = today.plusDays(daysUntilSunday);
        LocalDateTime dt = LocalDateTime.of(sunday, LocalTime.of(23, 59));
        return dt.format(OUT_FMT);
    }
}
