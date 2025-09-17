# Barry User Guide

Barry is a **desktop app for managing your tasks, optimized for use via a Command
Line Interface** (CLI) while still having the benefits of a Graphical User Interface
(GUI). If you can type fast, Barry can get your tasks management done faster than
traditional GUI apps.

- [Quick Start](#quick-start)
- [Features](#features)
  - [Viewing help: ```help```](#viewing-help-help)
  - [Adding task: ```todo``` ```deadline``` ```event```](#adding-task-todo-deadline-event)
  - [Marking a task as done or undone: ```mark``` ```unmark```](#marking-a-task-as-done-or-undone-mark-unmark)
  - [Listing all tasks: ```list```](#listing-all-tasks-list)
  - [Finding tasks have similar descriptions: ```find```](#finding-tasks-have-similar-descriptions-find)
  - [Deleting a task: ```delete```](#deleting-a-task-delete)
  - [Listing all alias: ```alias```](#listing-all-alias-alias)
  - [Exiting the program: ```bye```](#exiting-the-program-bye)
- [FAQ](#faq)
- [Command summary](#command-summary)

## Quick start

1. Ensure you have Java 17 or above installed in your Computer.<br>
**Mac users**: Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).
2. **Download or build** the app JAR [here](https://github.com/tuanduong18/ip/releases).
3. **Run** Barry (works for both CLI and GUI):
   ```bash
   java -jar barry.jar
   ```
4. Start typing commands (press **Enter** to send).

**Date/Time**
- **Input format (commands):** `dd/MM/yyyy HH:mm` (e.g., `30/08/2025 16:00`)
- **Display format (UI):** `h:mm a d MMM, yyyy` (e.g., `4:00 PM 30 Aug, 2025`)

**Storage**
- Tasks are persisted in a text file (e.g., `data/Barry.txt`). If it’s missing, Barry starts with an empty list.

**Indices**
- Lists show **1-based** indices (first task is `1`).

---

## Features

### Viewing help: `help`

Display supported commands (concise) or full details with examples.

**Usage**
```text
help
help --details
```

**Example**
```text
help
```
**Expected output (abridged)**
```
The command must start with one of these below:
    todo
    deadline
    event
    mark
    unmark
    list
    delete
    find
    help
    help --details
    bye
    alias
```

**Example**
```text
help --details
```
**Expected output (abridged)**
```
The command must have the formula as one of these below:
    todo {description}
        e.g.: todo Read book

    deadline {description} /by {datetime}
        e.g.: deadline iP /by 30/08/2025 16:00

    event {description} /from {datetime} /to {datetime}
        e.g.: event Meeting /from 27/08/2025 18:00 /to 27/08/2025 21:00
```

### Adding task ```todo``` ```deadline``` ```event```

Create **todo**, **deadline**, or **event** tasks.

**Usage**
```text
todo {description}
deadline {description} /by {dd/MM/yyyy HH:mm}
event {description} /from {dd/MM/yyyy HH:mm} /to {dd/MM/yyyy HH:mm}
```

**Examples**
```text
todo Read book
deadline iP /by 30/08/2025 16:00
event Project meeting /from 27/08/2025 18:00 /to 27/08/2025 21:00
```

**Expected output**
```
Got it. I've added this task:
    [T][ ] Read book
Now you have 1 task in the list.
```
```
Got it. I've added this task:
    [D][ ] iP (by: 4:00 PM 30 Aug, 2025)
Now you have 2 tasks in the list.
```
```
Got it. I've added this task:
    [E][ ] Project meeting (from: 6:00 PM 27 Aug, 2025 to: 9:00 PM 27 Aug, 2025)
Now you have 3 tasks in the list.
```

**Notes**
- Barry validates required fields and the **input** datetime format.

### Marking a task as done or undone: ```mark``` ```unmark```

Set a task’s completion state by its **1-based** index.

**Usage**
```text
mark {id}
unmark {id}
```

**Examples**
```text
mark 2
unmark 2
```

**Expected output**
```
Nice! I've marked this task as done:
    [D][X] iP (by: 4:00 PM 30 Aug, 2025)
```
```
Ok! I've marked this task as not done yet:
    [D][ ] iP (by: 4:00 PM 30 Aug, 2025)
```

**Notes**
- You’ll get an error if the index is ≤ 0 or larger than the list size.

### Listing all tasks: ```list```

Show all tasks with 1-based indices.

**Usage**
```text
list
```

**Example output**
```
Here are the tasks in your list:
    1.[T][ ] Read book
    2.[D][ ] iP (by: 4:00 PM 30 Aug, 2025)
    3.[E][ ] Project meeting (from: 6:00 PM 27 Aug, 2025 to: 9:00 PM 27 Aug, 2025)
```

### Finding tasks have similar descriptions: ```find```

Search tasks whose **descriptions** contain a given substring (case-insensitive).

**Usage**
```text
find {query}
```

**Examples**
```text
find book
find meeting
```

**Expected output**
```
Here are the matching tasks in your list:
    1.[T][ ] Read book
```
If nothing matches:
```
Oops! There isn't any task match your search
```

### Deleting a task: ```delete```

Remove a task by its 1-based index.

**Usage**
```text
delete {id}
```

**Example**
```text
delete 1
```

**Expected output**
```
Noted. I've removed this task:
    [T][ ] Read book
Now you have 2 tasks in the list.
```

**Notes**
- You’ll get an error if the index is invalid.

### Listing all alias: ```alias```

Print all user-defined aliases (if enabled). Aliases come from `~/.barryrc` using lines like:
```text
alias <name>='<template>'
```
- `{1}` is replaced with everything after the alias name (one parameter max).
- `${sun}` expands to upcoming **Sunday 23:59** (same week) with the command datetime format.

**Usage**
```text
alias
```

**Example `~/.barryrc`**
```text
alias ls='list'
alias rm='delete {1}'
alias dl_sun='deadline {1} /by ${sun}'
```

**Expected output**
```
dl_sun = deadline {1} /by ${sun}
ls = list
rm = delete {1}
```

### Exiting the program: ```bye```

Quit the application gracefully.

**Usage**
```text
bye
```

**Expected behavior**
- CLI exits immediately.
- GUI exits shortly after a farewell message.

```
Bye. Hope to see you again soon!
```

---

## FAQ

**Why do I get “Invalid time format …”?**  
Use the **input** format `dd/MM/yyyy HH:mm` in commands.

**Why is the index invalid?**  
Indices are **1-based** and must be within the `list` range.

**Why didn’t my tasks load?**  
If the storage file is missing/unreadable, Barry starts empty and shows a loading error. Check the path and permissions.

**Do aliases support multiple parameters?**  
No. Aliases support **one** `{1}` parameter and expand once. Macro `${sun}` is available by default.

---

## Command summary

| Command      | Format                                                  | Example                                                       |
|--------------|---------------------------------------------------------|---------------------------------------------------------------|
| Help         | `help`                                                  | `help`                                                        |
| Help+        | `help --details`                                        | `help --details`                                              |
| Todo         | `todo {description}`                                    | `todo Read book`                                              |
| Deadline     | `deadline {description} /by {dd/MM/yyyy HH:mm}`         | `deadline iP /by 30/08/2025 16:00`                            |
| Event        | `event {description} /from {dt} /to {dt}`               | `event Meeting /from 27/08/2025 18:00 /to 27/08/2025 21:00`   |
| List         | `list`                                                  | `list`                                                        |
| Mark         | `mark {id}`                                             | `mark 2`                                                      |
| Unmark       | `unmark {id}`                                           | `unmark 2`                                                    |
| Find         | `find {query}`                                          | `find book`                                                   |
| Delete       | `delete {id}`                                           | `delete 1`                                                    |
| Aliases      | `alias`                                                 | `alias`                                                       |
| Exit         | `bye`                                                   | `bye`                                                         |

---

Happy tasking with **Barry**!
