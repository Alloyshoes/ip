import java.time.LocalDate;
import java.util.List;

/**
 * Eve is a command-line task-tracking chatbot. It reads commands from
 * standard input in a loop, tracking to-do/deadline/event tasks, until the
 * user types "bye". Tasks are loaded from disk at startup and saved back
 * automatically whenever the list changes (see {@link Storage}). All user
 * input/output goes through {@link Ui}, and all command interpretation
 * goes through {@link Parser}.
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
        Storage storage = new Storage("data/eve.txt");
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (EveException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }

        ui.showWelcome();

        outerLoop:
        while (true) {
            String input = ui.readCommand();
            String commandWord = Parser.getCommandWord(input);
            String arguments = Parser.getArguments(input);

            try {
                Command command = Command.fromWord(commandWord);
                switch (command) {
                    case BYE:
                        ui.showGoodbye();
                        break outerLoop;
                    case LIST:
                        ui.showTaskList(tasks.asList());
                        break;
                    case MARK: {
                        int index = Parser.parseTaskNumber(arguments, tasks.size()) - 1;
                        tasks.get(index).markAsDone();
                        storage.save(tasks.asList());
                        ui.showTaskMarked(tasks.get(index));
                        break;
                    }
                    case UNMARK: {
                        int index = Parser.parseTaskNumber(arguments, tasks.size()) - 1;
                        tasks.get(index).markAsNotDone();
                        storage.save(tasks.asList());
                        ui.showTaskUnmarked(tasks.get(index));
                        break;
                    }
                    case TODO: {
                        Task task = Parser.parseTodo(arguments);
                        tasks.add(task);
                        storage.save(tasks.asList());
                        ui.showTaskAdded(task, tasks.size());
                        break;
                    }
                    case DEADLINE: {
                        Task task = Parser.parseDeadline(arguments);
                        tasks.add(task);
                        storage.save(tasks.asList());
                        ui.showTaskAdded(task, tasks.size());
                        break;
                    }
                    case EVENT: {
                        Task task = Parser.parseEvent(arguments);
                        tasks.add(task);
                        storage.save(tasks.asList());
                        ui.showTaskAdded(task, tasks.size());
                        break;
                    }
                    case ON: {
                        LocalDate date = Parser.parseOnDate(arguments);
                        List<Task> matches = tasks.occurringOn(date);
                        ui.showTasksOnDate(date, matches);
                        break;
                    }
                    case DELETE: {
                        int index = Parser.parseTaskNumber(arguments, tasks.size()) - 1;
                        Task removed = tasks.delete(index);
                        storage.save(tasks.asList());
                        ui.showTaskDeleted(removed, tasks.size());
                        break;
                    }
                }
            } catch (EveException e) {
                ui.showError(e.getMessage());
            }
        }
    }
}
