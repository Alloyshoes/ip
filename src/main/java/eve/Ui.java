package eve;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import eve.command.CommandWord;
import eve.task.Task;

/**
 * Deals with all interactions with the user: reading command lines from
 * standard input, and printing every message the chatbot shows (the
 * greeting, task confirmations, error messages, etc.). Keeping this in one
 * class means the exact wording/formatting of the chatbot's output lives in
 * a single place, separate from the logic that decides what to say.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _____  __   __  _____ \n"
            + "|  ___| \\ \\ / / |  ___|\n"
            + "| |__    \\ V /  | |__  \n"
            + "|  __|    \\ /   |  __| \n"
            + "|_____|    V    |_____|";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the banner, greeting, and a list of every available command. */
    public void showWelcome() {
        int usageWidth = 0;
        for (CommandWord commandWord : CommandWord.values()) {
            usageWidth = Math.max(usageWidth, commandWord.getUsage().length());
        }

        List<String> lines = new ArrayList<>();
        lines.add(BANNER);
        lines.add("");
        lines.add("Hello! I'm Eve.");
        lines.add("What can I do for you?");
        lines.add("");
        lines.add("Here's what I can do:");
        for (CommandWord commandWord : CommandWord.values()) {
            lines.add(String.format("  %-" + usageWidth + "s  %s",
                    commandWord.getUsage(), commandWord.getDescription()));
        }
        showLines(lines);
    }

    /**
     * Reads one line of user input.
     *
     * @return the full line the user typed, unmodified.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the farewell message shown when the user exits with "bye". */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /**
     * Prints every task in the list, numbered from 1.
     *
     * @param tasks the tasks to print, in order.
     */
    public void showTaskList(List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        lines.add("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            lines.add((i + 1) + "." + tasks.get(i));
        }
        showLines(lines);
    }

    /**
     * Prints the tasks whose description matched a search keyword, or a
     * "no matches" message if none did.
     *
     * @param matches the matching tasks, in list order.
     */
    public void showMatchingTasks(List<Task> matches) {
        List<String> lines = new ArrayList<>();
        if (matches.isEmpty()) {
            lines.add("No matching tasks found in your list.");
        } else {
            lines.add("Here are the matching tasks in your list:");
            for (int i = 0; i < matches.size(); i++) {
                lines.add((i + 1) + "." + matches.get(i));
            }
        }
        showLines(lines);
    }

    /**
     * Prints the tasks that occur on a given date, or a "no tasks" message
     * if none do.
     *
     * @param date the date that was queried.
     * @param matches the tasks that occur on {@code date}, in list order.
     */
    public void showTasksOnDate(LocalDate date, List<Task> matches) {
        List<String> lines = new ArrayList<>();
        if (matches.isEmpty()) {
            lines.add("You have no tasks on " + date.format(Task.DISPLAY_FORMAT) + ".");
        } else {
            lines.add("Here are the tasks on " + date.format(Task.DISPLAY_FORMAT) + ":");
            for (int i = 0; i < matches.size(); i++) {
                lines.add((i + 1) + "." + matches.get(i));
            }
        }
        showLines(lines);
    }

    /** Prints confirmation that a task was marked as done. */
    public void showTaskMarked(Task task) {
        showLines("Nice! I've marked this task as done:", "  " + task);
    }

    /** Prints confirmation that a task was marked as not done. */
    public void showTaskUnmarked(Task task) {
        showLines("OK, I've marked this task as not done yet:", "  " + task);
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task the task that was added.
     * @param taskCount how many tasks are in the list after adding it.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showLines("Got it. I've added this task:", "  " + task, "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints confirmation that a task was removed.
     *
     * @param task the task that was removed.
     * @param taskCount how many tasks remain in the list after removing it.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showLines("Noted. I've removed this task:", "  " + task, "Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints an error message, e.g. from a caught {@link EveException}. */
    public void showError(String message) {
        showLines(message);
    }

    /**
     * Prints a warning that the saved task list could not be loaded, and
     * that the chatbot is starting with an empty list instead.
     *
     * @param message detail of what went wrong, e.g. an I/O error message.
     */
    public void showLoadingError(String message) {
        showLines(message + " Starting with an empty list.");
    }

    /**
     * Prints one or more message lines wrapped between two divider lines --
     * the shape shared by every show method above that has a fixed, known
     * number of lines to print (as opposed to one line per task, which
     * varies at runtime; see the {@link #showLines(List)} overload for that
     * case).
     *
     * @param lines the message lines to print, in order.
     */
    private void showLines(String... lines) {
        System.out.println(LINE);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(LINE);
    }

    /**
     * Prints message lines built up at runtime (e.g. one per task) wrapped
     * between two divider lines, by delegating to {@link #showLines(String...)}.
     *
     * @param lines the message lines to print, in order.
     */
    private void showLines(List<String> lines) {
        showLines(lines.toArray(new String[0]));
    }
}
