import java.util.Scanner;

/**
 * Eve is a command-line task-tracking chatbot. It reads commands from
 * standard input in a loop, tracking to-do/deadline/event tasks in memory
 * (not persisted to disk), until the user types "bye".
 */
public class Eve {
    /**
     * Runs the chatbot: prints the greeting, then repeatedly reads and
     * handles one command per line until "bye" is entered.
     *
     * @param args unused.
     */
    public static void main(String[] args) {
        String banner = " _____  __   __  _____ \n"
                + "|  ___| \\ \\ / / |  ___|\n"
                + "| |__    \\ V /  | |__  \n"
                + "|  __|    \\ /   |  __| \n"
                + "|_____|    V    |_____|";
        String line = "____________________________________________________________";

        Task[] tasks = new Task[100];
        int taskCount = 0;

        int usageWidth = 0;
        for (Command command : Command.values()) {
            usageWidth = Math.max(usageWidth, command.getUsage().length());
        }

        System.out.println(line);
        System.out.println(banner);
        System.out.println();
        System.out.println("Hello! I'm Eve.");
        System.out.println("What can I do for you?");
        System.out.println();
        System.out.println("Here's what I can do:");
        for (Command command : Command.values()) {
            System.out.printf("  %-" + usageWidth + "s  %s%n", command.getUsage(), command.getDescription());
        }
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        outerLoop:
        while (true) {
            String input = scanner.nextLine();
            int spaceIndex = input.indexOf(' ');
            String commandWord = spaceIndex == -1 ? input : input.substring(0, spaceIndex);
            String arguments = spaceIndex == -1 ? "" : input.substring(spaceIndex + 1).trim();

            try {
                Command command = Command.fromWord(commandWord);
                switch (command) {
                    case BYE:
                        System.out.println("Bye. Hope to see you again soon!");
                        System.out.println(line);
                        break outerLoop;
                    case LIST:
                        System.out.println(line);
                        System.out.println("Here are the tasks in your list:");
                        for (int i = 0; i < taskCount; i++) {
                            System.out.println((i + 1) + "." + tasks[i]);
                        }
                        System.out.println(line);
                        break;
                    case MARK: {
                        int index = parseTaskNumber(arguments, taskCount) - 1;
                        tasks[index].markAsDone();
                        System.out.println(line);
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println("  " + tasks[index]);
                        System.out.println(line);
                        break;
                    }
                    case UNMARK: {
                        int index = parseTaskNumber(arguments, taskCount) - 1;
                        tasks[index].markAsNotDone();
                        System.out.println(line);
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  " + tasks[index]);
                        System.out.println(line);
                        break;
                    }
                    case TODO:
                        if (arguments.isEmpty()) {
                            throw new EveException("OOPS!!! The description of a todo cannot be empty.");
                        }
                        tasks[taskCount] = new ToDo(arguments);
                        taskCount++;
                        System.out.println(line);
                        System.out.println("Got it. I've added this task:");
                        System.out.println("  " + tasks[taskCount - 1]);
                        System.out.println("Now you have " + taskCount + " tasks in the list.");
                        System.out.println(line);
                        break;
                    case DEADLINE: {
                        int byIndex = arguments.indexOf(" /by ");
                        if (byIndex == -1) {
                            throw new EveException("OOPS!!! A deadline needs a description and a "
                                    + "'/by' date, e.g. deadline return book /by Sunday.");
                        }
                        String description = arguments.substring(0, byIndex).trim();
                        String by = arguments.substring(byIndex + " /by ".length()).trim();
                        if (description.isEmpty()) {
                            throw new EveException("OOPS!!! The description of a deadline cannot be empty.");
                        }
                        if (by.isEmpty()) {
                            throw new EveException("OOPS!!! The '/by' date of a deadline cannot be empty.");
                        }
                        tasks[taskCount] = new Deadline(description, by);
                        taskCount++;
                        System.out.println(line);
                        System.out.println("Got it. I've added this task:");
                        System.out.println("  " + tasks[taskCount - 1]);
                        System.out.println("Now you have " + taskCount + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                    case EVENT: {
                        int fromIndex = arguments.indexOf(" /from ");
                        int toIndex = arguments.indexOf(" /to ");
                        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
                            throw new EveException("OOPS!!! An event needs a description, a '/from' "
                                    + "time, and a '/to' time, e.g. event project meeting /from Mon "
                                    + "2pm /to 4pm.");
                        }
                        String description = arguments.substring(0, fromIndex).trim();
                        String from = arguments.substring(fromIndex + " /from ".length(), toIndex).trim();
                        String to = arguments.substring(toIndex + " /to ".length()).trim();
                        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                            throw new EveException("OOPS!!! An event's description, '/from' time, "
                                    + "and '/to' time must all be filled in.");
                        }
                        tasks[taskCount] = new Event(description, from, to);
                        taskCount++;
                        System.out.println(line);
                        System.out.println("Got it. I've added this task:");
                        System.out.println("  " + tasks[taskCount - 1]);
                        System.out.println("Now you have " + taskCount + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                    case DELETE: {
                        int index = parseTaskNumber(arguments, taskCount) - 1;
                        Task removed = tasks[index];
                        for (int i = index; i < taskCount - 1; i++) {
                            tasks[i] = tasks[i + 1];
                        }
                        tasks[taskCount - 1] = null;
                        taskCount--;
                        System.out.println(line);
                        System.out.println("Noted. I've removed this task:");
                        System.out.println("  " + removed);
                        System.out.println("Now you have " + taskCount + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                }
            } catch (EveException e) {
                System.out.println(line);
                System.out.println(e.getMessage());
                System.out.println(line);
            }
        }
        scanner.close();
    }

    /**
     * Parses and validates a 1-based task number typed by the user.
     *
     * @param text the argument text after the command word, e.g. "2".
     * @param taskCount how many tasks currently exist, for range checking.
     * @return the parsed task number (1-based).
     * @throws EveException if the text is missing, not a number, or out of range.
     */
    private static int parseTaskNumber(String text, int taskCount) throws EveException {
        if (text.isEmpty()) {
            throw new EveException("OOPS!!! Please tell me which task number, e.g. mark 2.");
        }
        int number;
        try {
            number = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new EveException("OOPS!!! '" + text + "' is not a valid task number.");
        }
        if (number < 1 || number > taskCount) {
            throw new EveException("OOPS!!! There is no task number " + number + " in your list.");
        }
        return number;
    }
}
