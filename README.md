# Barry — Developer Guide

This guide helps contributors understand Barry’s architecture, build process, coding conventions, and extension points. It complements the User Guide and focuses on developer-facing details.

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Build & Run](#build--run)
- [JavaFX GUI](#javafx-gui)
- [Commands & Parsing](#commands--parsing)
- [Storage Format](#storage-format)
- [Aliases](#aliases)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [Code Quality & Conventions](#code-quality--conventions)
- [Extending Barry](#extending-barry)
- [Packaging & Distribution](#packaging--distribution)
- [Troubleshooting](#troubleshooting)

---

## Overview

Barry is a task manager with both **CLI** and **GUI** front ends backed by the same core. The main flow is:

```
User input  →  Parser  →  Command  →  TaskList/Storage  →  Ui/Gui  →  Output
```

- **Parser** converts raw text into a concrete `Command`.
- **Command** mutates `TaskList` (add/mark/delete), persists via `Storage`, and emits UI-facing messages via `Ui` (console) or `Gui` (string-returning).
- **Storage** serializes/deserializes tasks to a text file.
- **GUI (JavaFX)** wraps the same core with FXML/CSS and chat-style message bubbles.

---

## Project Structure

```
src/
├─ main/
│  ├─ java/
│  │  ├─ barry/
│  │  │  ├─ commands/        # Command hierarchy; each implements execute(Ui)/execute(Gui)
│  │  │  ├─ data/            # TaskList; common regex/enums; exceptions
│  │  │  ├─ parser/          # CommandParser, TaskParser
│  │  │  ├─ storage/         # Encode/Decode/Storage (persistence)
│  │  │  ├─ ui/              # Ui (console), Gui (strings for GUI)
│  │  │  ├─ javafx/          # MainWindow, DialogBox, Launcher (JavaFX)
│  │  │  ├─ alias/           # AliasStorage, AliasExpander
│  │  │  └─ config/          # BarryRcBootstrap (copy default .barryrc once)
│  └─ resources/
│     ├─ images/             # avatars, background
│     ├─ view/               # FXML files (MainWindow.fxml, DialogBox.fxml)
│     ├─ css/                # main.css, dialog-box.css
│     └─ config/
│        └─ default.barryrc  # bundled default aliases
└─ test/
   └─ java/…                 # JUnit 5 tests
```

> All files under `src/main/resources` are packaged into the JAR and available via the classpath.

---

## Build & Run

Barry uses **Gradle Wrapper**. You do **not** need a system Gradle.

- macOS/Linux/WSL:
  ```bash
  chmod +x gradlew           # once
  ./gradlew -v               # verify wrapper
  ./gradlew clean test
  ./gradlew shadowJar
  java -jar build/libs/barry.jar
  ```

- Windows (PowerShell):
  ```powershell
  .\gradlew -v
  .\gradlew clean test
  .\gradlew shadowJar
  java -jar build/libs/barry.jar
  ```

**Notes**

- The JAR name is configured (Shadow): `build/libs/barry.jar`.
- Ensure a JDK (Java 17+) is installed: `java -version`.
- Commit wrapper files: `gradlew`, `gradlew.bat`, `gradle/wrapper/*`.

---

## JavaFX GUI

- **Controllers**: `barry.javafx.MainWindow`, `barry.javafx.DialogBox` load `MainWindow.fxml` and `DialogBox.fxml` respectively via `FXMLLoader`.
- **Styling**: CSS classes like `user-label`, `reply-label`, `error-label` differentiate user, Barry, and error bubbles.
- **Layout tips**:
    - Bind `scrollPane.vvalueProperty()` to `dialogContainer.heightProperty()` to auto-scroll.
    - Use `setFitToWidth(true)` and `setFillWidth(true)` for responsive wrapping.
    - Set the window title in your launcher: `primaryStage.setTitle("Barry")`.

**Launcher skeleton**

```java
@Override
public void start(Stage stage) throws Exception {
    FXMLLoader fxml = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
    Scene scene = new Scene(fxml.load());
    stage.setTitle("Barry");
    stage.setScene(scene);
    stage.show();
}
```

---

## Commands & Parsing

- **Command hierarchy**: abstract `Command` defines two methods:
    - `execute(TaskList, Ui, Storage)` — prints via console `Ui`.
    - `execute(TaskList, Gui, Storage)` — returns a string for the GUI.
    - Each command sets `isExit` (`ExitCommand` sets it to `true`).

- **Parsing**:
    - `CommandRegex` identifies the command and validates syntax.
    - `TaskRegex` extracts fields for `todo`, `deadline`, `event`.
    - `TaskParser` enforces validation and strict datetime parsing (`dd/MM/yyyy HH:mm`).

**Adding a new command**
1. Add an entry to `CommandType` and `CommandRegex`.
2. Implement a `Command` subclass with both `execute` overloads.
3. Wire it in `CommandParser:parseCommand`.
4. Update `Ui`/`Gui` methods if custom output is needed.
5. Update `help` and tests.

---

## Storage Format

Tasks are stored as **pipe-delimited** lines:

- **Todo**: `T | <0|1> | <description>`
- **Deadline**: `D | <0|1> | <description> | <dd/MM/yyyy HH:mm>`
- **Event**: `E | <0|1> | <description> | <dd/MM/yyyy HH:mm> | <dd/MM/yyyy HH:mm>`

- `Encode` converts `Task.toString()` (UI format like `4:00 PM 30 Aug, 2025`) into stored format.
- `Decode` parses stored lines back into `Task` objects.

**Date/Time**

- **Input/Storage**: `dd/MM/yyyy HH:mm` (strict)
- **Display (UI)**: `h:mm a d MMM, yyyy`

---

## Aliases

Barry supports one-parameter pre-defined aliases loaded from `~/.barryrc`, e.g.

```
alias ls='list'
alias rm='delete {1}'
alias dl_sun='deadline {1} /by ${sun}'
```

- `{1}` is replaced by everything after the alias name.
- Macro `${sun}` expands to upcoming **Sunday 23:59** (same week) in command datetime format.
- **Strict syntax**: `alias name='template'` (single or double quotes).

**Loading logic**

- `AliasStorage.load()` copies the bundled default from `config/default.barryrc` (inside the JAR) to the user path 
if the file is missing.
- `AliasStorage.load()` then reads `~/.barryrc` (using `Scanner`) and calls `parseLine(String)` per line.
- `AliasExpander.expand(input)` then expand the shortened version into full command and pass to CommandParser.

**Editing/Saving**

- `AliasStorage.save()` overwrites the RC file with current aliases (one per line). The JAR resource is **read-only**; 
only the user file is writable.

---

## Error Handling

- Centralized in `BarryException` with helpers:
    - `commandException()`, `missingTaskDescription(CommandType)`, `missingTimestamp(...)`, `invalidTimestamp(...)`, `taskNotFound(int)` etc.
- The CLI prepends `OOPS!!!` to error messages. The GUI shows an **error bubble** with a distinct CSS style.
- Prefer throwing `BarryException` in parser/command layers and keep UI focused on rendering messages.

---

## Testing

- **Framework**: JUnit 5.
- **Run**: `./gradlew test`
- **Guidelines**:
    - Prefer **deterministic** tests for date parsing (use fixed strings with `dd/MM/yyyy HH:mm`).
    - Test both **happy paths** and **failure** cases (e.g., `missingTaskDescription`, `invalidTimestamp`).
    - Leverage command `equals()` overrides for direct comparisons in parser tests.
    - For GUI, isolate logic and test **string outputs** via `Gui` instead of JavaFX nodes.

**Example: parser test snippet**
```java
@Test
void parse_addCommand_todo() throws BarryException {
    CommandParser parser = new CommandParser();
    Command expected = new AddCommand(new Todo("read book"));
    Command actual = parser.parseCommand("todo read book");
    assertEquals(expected, actual);
}
```

---

## Code Quality & Conventions

- **Readability** over cleverness (KISS, SLAP, guard clauses).
- **Methods**: keep under ~60 LoC; avoid deep nesting.
- **Avoid** magic numbers/strings; prefer named constants.
- **Comments**: focus on **what/why**, not how; prefer Javadoc headers.
- **Naming**:
    - Java fields/methods: `lowerCamelCase`
    - Constants: `UPPER_SNAKE_CASE` with ≤ 3 tokens (e.g., `CMD_DATETIME`, `UI_DATETIME`)
    - Keep aliases/regex names descriptive and consistent.
- **Formatting**: follow project style (brace placement, spacing).

---

## Extending Barry

**Add a new task type** (outline)
1. Extend `Task`, implement `toString()` in UI format.
2. Add a `TaskRegex` pattern and update `TaskParser` with strict validation.
3. Update `Encode/Decode` to persist/restore the new type.
4. Update `Ui/Gui` if special rendering is needed.
5. Add tests and help messages.

**Add a new command** (outline)
1. Add to `CommandType`/`CommandRegex`.
2. Create a `Command` subclass.
3. Wire in `CommandParser`.
4. Add help coverage and tests.

---

## Packaging & Distribution

- Build a fat JAR:
  ```bash
  ./gradlew shadowJar
  ls build/libs/barry.jar
  ```
- Ensure resources (FXML, images, CSS, `config/default.barryrc`) are under `src/main/resources` so they are packaged.
- Commit Gradle wrapper files so users/CI can run builds without a system Gradle.

**JavaFX note**: The build includes platform-classified JavaFX artifacts (`win`, `mac`, `linux`) to run on each OS. If you customize this, ensure the correct classifiers are included for your target platform(s).

---

## Troubleshooting

- **Wrapper error: “Cannot find class org.gradle.wrapper.GradleWrapperMain”**  
  Wrapper JAR missing. Regenerate with a system Gradle once (`gradle wrapper --gradle-version 8.7`), commit wrapper files, or re-clone.

- **No `gradle` command**  
  Use the wrapper: `./gradlew …` (macOS/Linux/WSL) or `.\gradlew …` (Windows).

- **JavaFX resources not found**  
  Check resource paths like `"/view/MainWindow.fxml"` exist under `src/main/resources`. Use `getResource("/view/MainWindow.fxml")`.

- **Storage file errors**  
  `Storage` will create the file and parent directory when missing. If it fails, Barry prints a loading error and starts with an empty list.
