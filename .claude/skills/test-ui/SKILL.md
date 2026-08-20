---
name: test-ui
description: Run the project's UI test plan against the compiled program, comparing each command's output to what test/ui-test-plan.md expects. Use after any change to Eve.java, Task.java, or other source files, and whenever the user asks to test, verify, or check the chatbot's behavior.
---

# Test UI

Run every test case recorded in `test/ui-test-plan.md` against the actual
program: compile the current source, feed each case's input to the program's
stdin, and compare its full console output against that case's expected
output.

## Run the tests

From the repository root:

```bash
python3 .claude/skills/test-ui/scripts/run_ui_tests.py
```

The script:
1. Compiles every `.java` file under `src/main/java` (no external
   dependencies -- standard `javac`/`java` only).
2. Parses `test/ui-test-plan.md` into an ordered list of test cases, each
   with an aim, an input (stdin lines), and an expected output.
3. Runs each test case in order, printing a session record (the input lines
   and the actual output) so the console transcript is visible.
4. Stops immediately at the first failing test case, printing both the
   expected and actual output for that case, and exits with a non-zero
   status. It does not run the remaining test cases.
5. If every test case passes, prints a one-line summary and exits 0.

Report the printed session record and final result back to the user; do not
summarize away the actual/expected output on failure.

## Maintain the test plan

`test/ui-test-plan.md` is the single source of truth for these tests. Each
test case follows this exact structure so the script can parse it:

````markdown
## Test N: <short name>

**Aim:** <what this case verifies>

**Input:**
```text
<one command per line, must end with "bye" so the program exits>
```

**Expected output:**
```text
<the program's exact expected stdout for this input>
```
````

When a code change adds, removes, or changes the behavior of a command,
update or add test cases here first (verify the new expected output by
running the program manually), then run this skill to confirm.
