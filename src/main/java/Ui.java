import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

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
        for (Command command : Command.values()) {
            usageWidth = Math.max(usageWidth, command.getUsage().length());
        }

        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println();
        System.out.println("Hello! I'm Eve.");
        System.out.println("What can I do for you?");
        System.out.println();
        System.out.println("Here's what I can do:");
        for (Command command : Command.values()) {
            System.out.printf("  %-" + usageWidth + "s  %s%n", command.getUsage(), command.getDescription());
        }
        System.out.println(LINE);
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
     * @param tasks the task array (only the first {@code taskCount} slots are used).
     * @param taskCount how many of the array's slots are in use.
     */
    public void showTaskList(Task[] tasks, int taskCount) {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
        System.out.println(LINE);
    }

    /**
     * Prints the tasks that occur on a given date, or a "no tasks" message
     * if none do.
     *
     * @param date the date that was queried.
     * @param matches the tasks that occur on {@code date}, in list order.
     */
    public void showTasksOnDate(LocalDate date, List<Task> matches) {
        System.out.println(LINE);
        if (matches.isEmpty()) {
            System.out.println("You have no tasks on " + date.format(Task.DISPLAY_FORMAT) + ".");
        } else {
            System.out.println("Here are the tasks on " + date.format(Task.DISPLAY_FORMAT) + ":");
            for (int i = 0; i < matches.size(); i++) {
                System.out.println((i + 1) + "." + matches.get(i));
            }
        }
        System.out.println(LINE);
    }

    /** Prints confirmation that a task was marked as done. */
    public void showTaskMarked(Task task) {
        System.out.println(LINE);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
        System.out.println(LINE);
    }

    /** Prints confirmation that a task was marked as not done. */
    public void showTaskUnmarked(Task task) {
        System.out.println(LINE);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
        System.out.println(LINE);
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task the task that was added.
     * @param taskCount how many tasks are in the list after adding it.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Prints confirmation that a task was removed.
     *
     * @param task the task that was removed.
     * @param taskCount how many tasks remain in the list after removing it.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /** Prints an error message, e.g. from a caught {@link EveException}. */
    public void showError(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
