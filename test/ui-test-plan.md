# UI Test Plan

Test cases for the `test-ui` skill (`.claude/skills/test-ui/`). Each case is
run by feeding **Input** to the program's stdin, one command per line, and
comparing the program's full stdout against **Expected output** exactly.

Every case's input must end with `bye` so the program exits cleanly instead
of hitting end-of-input while still waiting for a command.

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
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 2: Add tasks and list them

**Aim:** Plain text input is stored as a new task and confirmed with
`added: ...`; `list` shows every stored task, numbered from 1, each with a
not-done `[ ]` status icon.

**Input:**
```text
read book
return book
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
____________________________________________________________
____________________________________________________________
added: read book
____________________________________________________________
____________________________________________________________
added: return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[ ] read book
2.[ ] return book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 3: Mark a task as done

**Aim:** `mark <n>` marks the n-th task (1-based) as done, confirms it, and
the change is reflected the next time `list` is run.

**Input:**
```text
read book
return book
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
____________________________________________________________
____________________________________________________________
added: read book
____________________________________________________________
____________________________________________________________
added: return book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[ ] read book
2.[X] return book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 4: Unmark a task

**Aim:** `unmark <n>` reverses a task's done status back to not-done and
confirms it.

**Input:**
```text
read book
return book
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
____________________________________________________________
____________________________________________________________
added: read book
____________________________________________________________
____________________________________________________________
added: return book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [ ] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[X] read book
2.[ ] return book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
