import java.util.Scanner;

public class Eve {
    public static void main(String[] args) {
        String banner = " _____  __   __  _____ \n"
                + "|  ___| \\ \\ / / |  ___|\n"
                + "| |__    \\ V /  | |__  \n"
                + "|  __|    \\ /   |  __| \n"
                + "|_____|    V    |_____|";
        String line = "____________________________________________________________";

        Task[] tasks = new Task[100];
        int taskCount = 0;

        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm Eve.");
        System.out.println("What can I do for you?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            try {
                if (input.equals("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(line);
                    break;
                }
                else if (input.equals("list")) {
                    System.out.println(line);
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                    System.out.println(line);
                }
                else if (input.equals("mark") || input.startsWith("mark ")) {
                    int index = parseTaskNumber(input.substring(4).trim(), taskCount) - 1;
                    tasks[index].markAsDone();
                    System.out.println(line);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks[index]);
                    System.out.println(line);
                }
                else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    int index = parseTaskNumber(input.substring(6).trim(), taskCount) - 1;
                    tasks[index].markAsNotDone();
                    System.out.println(line);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks[index]);
                    System.out.println(line);
                }
                else if (input.equals("todo") || input.startsWith("todo ")) {
                    String description = input.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new EveException("OOPS!!! The description of a todo cannot be empty.");
                    }
                    tasks[taskCount] = new ToDo(description);
                    taskCount++;
                    System.out.println(line);
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks[taskCount - 1]);
                    System.out.println("Now you have " + taskCount + " tasks in the list.");
                    System.out.println(line);
                }
                else if (input.equals("deadline") || input.startsWith("deadline ")) {
                    String rest = input.substring(8);
                    int byIndex = rest.indexOf(" /by ");
                    if (byIndex == -1) {
                        throw new EveException("OOPS!!! A deadline needs a description and a "
                                + "'/by' date, e.g. deadline return book /by Sunday.");
                    }
                    String description = rest.substring(0, byIndex).trim();
                    String by = rest.substring(byIndex + " /by ".length()).trim();
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
                }
                else if (input.equals("event") || input.startsWith("event ")) {
                    String rest = input.substring(5);
                    int fromIndex = rest.indexOf(" /from ");
                    int toIndex = rest.indexOf(" /to ");
                    if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
                        throw new EveException("OOPS!!! An event needs a description, a '/from' "
                                + "time, and a '/to' time, e.g. event project meeting /from Mon "
                                + "2pm /to 4pm.");
                    }
                    String description = rest.substring(0, fromIndex).trim();
                    String from = rest.substring(fromIndex + " /from ".length(), toIndex).trim();
                    String to = rest.substring(toIndex + " /to ".length()).trim();
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
                }
                else {
                    throw new EveException("OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
            } catch (EveException e) {
                System.out.println(line);
                System.out.println(e.getMessage());
                System.out.println(line);
            }
        }
        scanner.close();
    }

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
