package eve;

/**
 * Eve is a command-line task-tracking chatbot. It reads commands from
 * standard input in a loop, tracking to-do/deadline/event tasks, until the
 * user types "bye". Tasks are loaded from disk at startup and saved back
 * automatically whenever the list changes (see {@link Storage}). Each line
 * of input is turned into a {@link Command} by {@link Parser}, which is
 * then executed against the current {@link TaskList}; all user
 * input/output goes through {@link Ui}.
 */
public class Eve {
    /**
     * Runs the chatbot: prints the greeting, then repeatedly reads,
     * parses, and executes one command per line until "bye" is entered.
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

        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (EveException e) {
                ui.showError(e.getMessage());
            }
        }
    }
}
