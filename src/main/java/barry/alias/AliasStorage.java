package barry.alias;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;


/**
 * Loads and stores user-defined command aliases from a shell-like RC file.
 * <p>
 * Each alias is defined on a single line using the syntax:
 * </p>
 * <pre>{@code
 * alias <name>='<template>'
 * }</pre>
 * <p>
 * Double quotes are also accepted. Example entries:
 * </p>
 * <ul>
 *   <li>{@code alias ls='list'}</li>
 *   <li>{@code alias rm='delete {1}'}</li>
 *   <li>{@code alias dl_sun='deadline {1} /by ${sun}'}</li>
 * </ul>
 * <p>
 * Templates may contain at most one parameter placeholder {@code {1}} which is replaced by
 * the remainder of the user input after the alias token (using {@code split(" ", 2)} in
 * {@link AliasExpander}). Lines beginning with {@code #} and blank lines are ignored.
 * </p>
 */
public final class AliasStorage {

    private final HashMap<String, String> templates = new HashMap<String, String>();
    private Path rcPath = Paths.get(System.getProperty("user.home"), "Barry data", ".barryrc");

    public AliasStorage() {}
    /**
     * Creates an {@code AliasStorage} that reads from the given RC file path.
     *
     * @param rcPath path to the RC file (e.g., {@code ~/.barryrc})
     */
    public AliasStorage(Path rcPath) {
        this.rcPath = rcPath;
    }

    /**
     * Loads aliases from the RC file into memory.
     * <p>
     * Existing aliases in memory are cleared first. If the file does not exist, no error is thrown.
     * </p>
     */
    public void load() {
        // Start fresh
        templates.clear();

        // 1) Best-effort: ensure parent directory exists
        try {
            Path parent = rcPath.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (FileAlreadyExistsException e) {
            // A non-directory exists where the directory should be — proceed without failing.
        } catch (AccessDeniedException e) {
            // No permission to create directories — proceed; reading may still work.
        } catch (java.io.IOException e) {
            // Other I/O problems — proceed; reading may still work.
        } catch (SecurityException e) {
            // Security manager disallows file ops — proceed; reading may still work.
        }

        // 2) If RC file is missing, seed from bundled default and try to save (both best-effort)
        try {
            if (Files.notExists(rcPath)) {
                try {
                    this.loadDefault(); // fill 'templates' from classpath default (if present)
                } catch (SecurityException se) {
                    // Classloader restricted — continue with empty templates
                }
                try {
                    this.save(); // create the file with whatever we have (possibly empty)
                } catch (SecurityException ignore) {
                    // Can't write file — okay, continue with in-memory templates only
                }
            }
        } catch (SecurityException ignore) {
            // Cannot even check existence — proceed to read attempt below (will fail safely)
        }

        // 3) Read ~/.barryrc (UTF-8). Skip malformed lines, never throw.
        try {
            Scanner sc = new Scanner(new FileReader(rcPath.toFile()));
            while (sc.hasNextLine()) {
                final String line = sc.nextLine();
                // Your parseLine(...) is already defensive
                parseLine(line);
            }
            // If underlying stream had IO errors, Scanner records it; ignoring by design.
            if (sc.ioException() != null) {
                // Optionally log sc.ioException()
            }
        } catch (FileNotFoundException e) {
            // File missing/unreadable — leave templates as-is.
        } catch (SecurityException e) {
            // No permission to read — leave templates as-is.
        }
    }


    /**
     * Load the aliases from resources/config/default.barryrc
     */
    private void loadDefault() {
        try {
            InputStream in = getClass()
                    .getClassLoader()
                    .getResourceAsStream("config/default.barryrc");
            if (in == null) {
                return; // No bundled default available
            }
            try (Scanner sc = new Scanner(in, UTF_8)) {
                while (sc.hasNextLine()) {
                    parseLine(sc.nextLine());
                }
                // If an IO error occurred during scanning, it will be available via sc.ioException()
            }
        } catch (SecurityException e) {
            // Classloader/resource access restricted — ignore and return
        }
    }

    /**
     * Writes the current aliases to the RC file, overwriting any existing content.
     * <p>
     * Lines are emitted in the canonical form {@code alias name='template'}.
     * This method is optional for basic usage, but useful if you later add in-app alias editing.
     * </p>
     */
    public void save() {
        try {
            if (rcPath.getParent() != null) {
                Files.createDirectories(rcPath.getParent());
            }
            FileWriter writer = new FileWriter(rcPath.toFile(), /* append = */ false);
            writer.write("# Barry command aliases");
            writer.write(System.lineSeparator());
            for (HashMap.Entry<String, String> e : templates.entrySet()) {
                writer.write("alias ");
                writer.write(e.getKey());
                writer.write("='");
                writer.write(e.getValue());
                writer.write("'");
                writer.write(System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            return;
        }
    }

    /**
     * Returns a copy of the current alias map keyed by lower-cased alias names.
     *
     * @return a defensive copy of aliases in memory
     */
    public HashMap<String, String> view() {
        return new HashMap<String, String>(templates);
    }

    /**
     * Gets the template for the given alias name.
     *
     * @param name the alias name (case-insensitive)
     * @return the template string if present, or {@code null} otherwise
     */
    public String get(String name) {
        if (name == null) {
            return null;
        }
        return templates.get(name.toLowerCase());
    }

    /**
     * Adds or replaces an alias in memory (does not persist to disk automatically).
     *
     * @param name     the alias name (case-insensitive)
     * @param template the template to store
     */
    public void put(String name, String template) {
        if (name == null || template == null) {
            return;
        }
        templates.put(name.toLowerCase(), template);
    }

    /**
     * Removes an alias from memory if present (does not persist to disk automatically).
     *
     * @param name the alias name (case-insensitive)
     */
    public void remove(String name) {
        if (name == null) {
            return;
        }
        templates.remove(name.toLowerCase());
    }

    /**
     * Parses one RC line in the form {@code alias name='template'}; ignores comments/blank lines.
     */
    private void parseLine(String line) {
        String s = line.trim();
        if (s.isEmpty() || s.startsWith("#")) {
            return;
        }
        if (!s.startsWith("alias ")) {
            return;
        }

        // Strip "alias " then expect NAME='TEMPLATE'
        String body = s.substring(6).trim();
        int eq = body.indexOf('=');
        if (eq < 1) {
            return; // need a non-empty name before '='
        }

        String name = body.substring(0, eq).trim().toLowerCase();
        String rhs = body.substring(eq + 1).trim();
        if (rhs.length() < 2) {
            return;
        }

        char first = rhs.charAt(0);
        char last = rhs.charAt(rhs.length() - 1);
        if ((first == '\'' && last == '\'') || (first == '"' && last == '"')) {
            String template = rhs.substring(1, rhs.length() - 1);
            if (!name.isEmpty() && !template.isEmpty()) {
                templates.put(name, template);
            }
        }
    }
}
