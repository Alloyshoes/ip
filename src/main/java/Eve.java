import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Eve is a command-line task-tracking chatbot. It reads commands from
 * standard input in a loop, tracking to-do/deadline/event tasks, until the
 * user types "bye". Tasks are loaded from disk at startup and saved back
 * automatically whenever the list changes (see {@link Storage}). All user
 * input/output goes through {@link Ui}.
 */
public class Eve {
    /**
     * Runs the chatbot: prints the greeting, then repeatedly reads and
     * handles one command per line until "bye" is entered.
     *
     * @param args unused.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();

        Task[] tasks = new Task[100];
        int taskCount = 0;
        for (Task task : Storage.load()) {
            tasks[taskCount] = task;
            taskCount++;
        }

        ui.showWelcome();

        outerLoop:
        while (true) {
            String input = ui.readCommand();
            int spaceIndex = input.indexOf(' ');
            String commandWord = spaceIndex == -1 ? input : input.substring(0, spaceIndex);
            String arguments = spaceIndex == -1 ? "" : input.substring(spaceIndex + 1).trim();

            try {
                Command command = Command.fromWord(commandWord);
                switch (command) {
                    case BYE:
                        ui.showGoodbye();
                        break outerLoop;
                    case LIST:
                        ui.showTaskList(tasks, taskCount);
                        break;
                    case MARK: {
                        int index = parseTaskNumber(arguments, taskCount) - 1;
                        tasks[index].markAsDone();
                        Storage.save(tasks, taskCount);
                        ui.showTaskMarked(tasks[index]);
                        break;
                    }
                    case UNMARK: {
                        int index = parseTaskNumber(arguments, taskCount) - 1;
                        tasks[index].markAsNotDone();
                        Storage.save(tasks, taskCount);
                        ui.showTaskUnmarked(tasks[index]);
                        break;
                    }
                    case TODO:
                        if (arguments.isEmpty()) {
                            throw new EveException("OOPS!!! The description of a todo cannot be empty.");
                        }
                        tasks[taskCount] = new ToDo(arguments);
                        taskCount++;
                        Storage.save(tasks, taskCount);
                        ui.showTaskAdded(tasks[taskCount - 1], taskCount);
                        break;
                    case DEADLINE: {
                        int byIndex = arguments.indexOf(" /by ");
                        if (byIndex == -1) {
                            throw new EveException("OOPS!!! A deadline needs a description and a "
                                    + "'/by' date, e.g. deadline return book /by 2019-12-02.");
                        }
                        String description = arguments.substring(0, byIndex).trim();
                        String byText = arguments.substring(byIndex + " /by ".length()).trim();
                        if (description.isEmpty()) {
                            throw new EveException("OOPS!!! The description of a deadline cannot be empty.");
                        }
                        if (byText.isEmpty()) {
                            throw new EveException("OOPS!!! The '/by' date of a deadline cannot be empty.");
                        }
                        LocalDate by;
                        try {
                            by = LocalDate.parse(byText);
                        } catch (DateTimeParseException e) {
                            throw new EveException("OOPS!!! Please give the '/by' date as "
                                    + "yyyy-mm-dd, e.g. 2019-12-02.");
                        }
                        tasks[taskCount] = new Deadline(description, by);
                        taskCount++;
                        Storage.save(tasks, taskCount);
                        ui.showTaskAdded(tasks[taskCount - 1], taskCount);
                        break;
                    }
                    case EVENT: {
                        int fromIndex = arguments.indexOf(" /from ");
                        int toIndex = arguments.indexOf(" /to ");
                        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
                            throw new EveException("OOPS!!! An event needs a description, a '/from' "
                                    + "date, and a '/to' date, e.g. event project meeting /from "
                                    + "2019-10-04 /to 2019-10-11.");
                        }
                        String description = arguments.substring(0, fromIndex).trim();
                        String fromText = arguments.substring(fromIndex + " /from ".length(), toIndex).trim();
                        String toText = arguments.substring(toIndex + " /to ".length()).trim();
                        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
                            throw new EveException("OOPS!!! An event's description, '/from' date, "
                                    + "and '/to' date must all be filled in.");
                        }
                        LocalDate from;
                        try {
                            from = LocalDate.parse(fromText);
                        } catch (DateTimeParseException e) {
                            throw new EveException("OOPS!!! Please give the '/from' date as "
                                    + "yyyy-mm-dd, e.g. 2019-10-04.");
                        }
                        LocalDate to;
                        try {
                            to = LocalDate.parse(toText);
                        } catch (DateTimeParseException e) {
                            throw new EveException("OOPS!!! Please give the '/to' date as "
                                    + "yyyy-mm-dd, e.g. 2019-10-11.");
                        }
                        tasks[taskCount] = new Event(description, from, to);
                        taskCount++;
                        Storage.save(tasks, taskCount);
                        ui.showTaskAdded(tasks[taskCount - 1], taskCount);
                        break;
                    }
                    case ON: {
                        if (arguments.isEmpty()) {
                            throw new EveException("OOPS!!! Please tell me which date, e.g. on 2019-12-02.");
                        }
                        LocalDate date;
                        try {
                            date = LocalDate.parse(arguments);
                        } catch (DateTimeParseException e) {
                            throw new EveException("OOPS!!! Please give the date as yyyy-mm-dd, "
                                    + "e.g. on 2019-12-02.");
                        }
                        List<Task> matches = new ArrayList<>();
                        for (int i = 0; i < taskCount; i++) {
                            if (tasks[i].occursOn(date)) {
                                matches.add(tasks[i]);
                            }
                        }
                        ui.showTasksOnDate(date, matches);
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
                        Storage.save(tasks, taskCount);
                        ui.showTaskDeleted(removed, taskCount);
                        break;
                    }
                }
            } catch (EveException e) {
                ui.showError(e.getMessage());
            }
        }
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
