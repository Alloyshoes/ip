# UI Test Plan

Test cases for the `test-ui` skill (`.claude/skills/test-ui/`). Each case is
run by feeding **Input** to the program's stdin, one command per line, and
comparing the program's full stdout against **Expected output** exactly.

Every case's input must end with `bye` so the program exits cleanly instead
of hitting end-of-input while still waiting for a command.

The `data/` folder the program persists its task list under (see
`Storage.java`) is reset before each test case's first run, so every case
still starts from an empty task list regardless of what an earlier case
saved. A case may optionally include a second input/expected-output pair
(run as a separate process invocation, without resetting `data/` in
between) to verify behavior across two runs, and/or a "Data file before
run" block to seed the data file with specific content before the first
run -- see `.claude/skills/test-ui/SKILL.md` for the exact format.

When behavior changes (a new command, a changed message, a new class),
update or add test cases here so this file always reflects the program's
actual current behavior.

## Test 1: Greet and exit

**Aim:** The program prints the banner and greeting, then exits cleanly
when the very first command is `bye`.

**Input:**
```text
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 2: Add tasks and list them

**Aim:** `todo <description>` adds a new task and confirms it with
`Got it. I've added this task: ...`; `list` shows every stored task,
numbered from 1, each with a not-done `[ ]` status icon.

**Input:**
```text
todo read book
todo return book
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 3: Mark a task as done

**Aim:** `mark <n>` marks the n-th task (1-based) as done, confirms it, and
the change is reflected the next time `list` is run.

**Input:**
```text
todo read book
todo return book
mark 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][X] return book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 4: Unmark a task

**Aim:** `unmark <n>` reverses a task's done status back to not-done and
confirms it.

**Input:**
```text
todo read book
todo return book
mark 1
mark 2
unmark 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] return book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[T][ ] return book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 5: Add a ToDo

**Aim:** `todo <description>` adds a `ToDo`, confirmed with the `[T]` type
icon, a not-done `[ ]` status icon, and the running task count.

**Input:**
```text
todo borrow book
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 6: Add a Deadline

**Aim:** `deadline <description> /by <by>` adds a `Deadline`, confirmed with
the `[D]` type icon and a `(by: ...)` suffix; the date/time is stored as a
plain string, unparsed.

**Input:**
```text
deadline return book /by Sunday
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 7: Add an Event

**Aim:** `event <description> /from <from> /to <to>` adds an `Event`,
confirmed with the `[E]` type icon and a `(from: ... to: ...)` suffix.

**Input:**
```text
event project meeting /from Mon 2pm /to 4pm
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 8: Mixed task types with mark

**Aim:** ToDo, Deadline, and Event tasks can be added, marked done, and
listed together, each keeping its own type icon and detail suffix (matches
the Level-4 requirement's example transcript).

**Input:**
```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
mark 1
mark 4
todo borrow book
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 9: Empty description and unknown command

**Aim:** `todo` with no description, and any input that doesn't match a
known command (e.g. `blah`), each produce a specific `OOPS!!!` error
instead of crashing or silently doing something wrong. Matches the
Level-5 requirement's own example transcript.

**Input:**
```text
todo
blah
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 10: Malformed deadline/event and bad task numbers

**Aim:** A `deadline` missing `/by`, an `event` missing `/to`, `mark`/`unmark`
with an out-of-range or non-numeric task number all produce specific
error messages, and the program keeps running afterward instead of
crashing.

**Input:**
```text
deadline return book
event project meeting /from Mon 2pm
mark 5
unmark abc
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a description and a '/by' date, e.g. deadline return book /by Sunday.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a description, a '/from' time, and a '/to' time, e.g. event project meeting /from Mon 2pm /to 4pm.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 5 in your list.
____________________________________________________________
____________________________________________________________
OOPS!!! 'abc' is not a valid task number.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 11: Delete a task

**Aim:** `delete <n>` removes the n-th task, confirms it with the removed
task's own display text and the updated count, and the remaining tasks
shift down and renumber correctly in `list`. Matches the Level-6
requirement's example transcript.

**Input:**
```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
mark 2
delete 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][X] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 12: Delete errors reuse task-number validation

**Aim:** `delete` with a missing, non-numeric, or out-of-range task number
produces the same specific errors as `mark`/`unmark` (they share the
`parseTaskNumber` helper), and the task list is left untouched.

**Input:**
```text
todo x
delete
delete abc
delete 0
delete 99
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] x
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! Please tell me which task number, e.g. mark 2.
____________________________________________________________
____________________________________________________________
OOPS!!! 'abc' is not a valid task number.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 0 in your list.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 99 in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] x
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 13: Tasks persist across separate runs

**Aim:** Tasks added and marked in one run of the program are saved to
disk, and a completely separate run of the program (started fresh, no
in-memory state carried over) loads them back via `list`.

**Input:**
```text
todo read book
deadline return book /by June 6th
mark 1
bye
```

**Expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

**Second input:**
```text
list
bye
```

**Second expected output:**
```text
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: June 6th)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 14: Corrupted data file lines are skipped, not crashed on

**Aim:** A pre-existing data file containing a line that doesn't parse
at all, and a line with an invalid status field, are each skipped with a
warning printed to the console; well-formed lines in the same file still
load correctly (stretch goal from the Level 7 requirement).

**Data file before run:**
```text
T | 1 | read book
NOT A VALID LINE
T | X | bad status
D | 0 | return book | June 6th
```

**Input:**
```text
list
bye
```

**Expected output:**
```text
Warning: skipping corrupted line in data file: NOT A VALID LINE
Warning: skipping corrupted line in data file: T | X | bad status
____________________________________________________________
 _____  __   __  _____ 
|  ___| \ \ / / |  ___|
| |__    \ V /  | |__  
|  __|    \ /   |  __| 
|_____|    V    |_____|

Hello! I'm Eve.
What can I do for you?

Here's what I can do:
  todo <description>                           Add a to-do task.
  deadline <description> /by <date/time>       Add a task with a deadline.
  event <description> /from <start> /to <end>  Add an event.
  list                                         Show all tasks.
  mark <task number>                           Mark a task as done.
  unmark <task number>                         Mark a task as not done.
  delete <task number>                         Remove a task.
  bye                                          Exit the program.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: June 6th)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
