import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Deals with making sense of a user command: splits a line of input into a
 * command word and its arguments, and turns them into the matching
 * {@link Command} to execute -- or an {@link EveException} explaining what
 * was wrong with them.
 */
public class Parser {
    private Parser() {
        // Not meant to be instantiated: every method is static.
    }

    /**
     * Parses one line of user input into the {@link Command} it requests.
     *
     * @param fullCommand the full line of input, e.g. "todo read book".
     * @throws EveException if the command word is unknown or its arguments are invalid.
     */
    public static Command parse(String fullCommand) throws EveException {
        String word = getCommandWord(fullCommand);
        String arguments = getArguments(fullCommand);
        CommandWord commandWord = CommandWord.fromWord(word);
        switch (commandWord) {
            case BYE:
                return new ExitCommand();
            case LIST:
                return new ListCommand();
            case MARK:
                return new MarkCommand(parseTaskNumber(arguments));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(arguments));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(arguments));
            case TODO:
                return new AddCommand(parseTodo(arguments));
            case DEADLINE:
                return new AddCommand(parseDeadline(arguments));
            case EVENT:
                return new AddCommand(parseEvent(arguments));
            case ON:
                return new OnCommand(parseOnDate(arguments));
            default:
                // Unreachable: CommandWord.fromWord only ever returns one of the cases above.
                throw new EveException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    private static String getCommandWord(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? input : input.substring(0, spaceIndex);
    }

    private static String getArguments(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? "" : input.substring(spaceIndex + 1).trim();
    }

    /**
     * Parses the format of a task number typed by the user (missing or
     * non-numeric). Whether the number is in range depends on the current
     * list size, so that's checked later by {@link TaskList#toIndex}.
     */
    private static int parseTaskNumber(String text) throws EveException {
        if (text.isEmpty()) {
            throw new EveException("OOPS!!! Please tell me which task number, e.g. mark 2.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new EveException("OOPS!!! '" + text + "' is not a valid task number.");
        }
    }

    /**
     * Parses the arguments of a {@code todo} command.
     *
     * @param arguments the text after "todo".
     * @throws EveException if the description is empty.
     */
    private static ToDo parseTodo(String arguments) throws EveException {
        if (arguments.isEmpty()) {
            throw new EveException("OOPS!!! The description of a todo cannot be empty.");
        }
        return new ToDo(arguments);
    }

    /**
     * Parses the arguments of a {@code deadline} command, e.g.
     * {@code "return book /by 2019-12-02"}.
     *
     * @param arguments the text after "deadline".
     * @throws EveException if the description or '/by' date is missing, empty, or malformed.
     */
    private static Deadline parseDeadline(String arguments) throws EveException {
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
        return new Deadline(description, by);
    }

    /**
     * Parses the arguments of an {@code event} command, e.g.
     * {@code "project meeting /from 2019-10-04 /to 2019-10-11"}.
     *
     * @param arguments the text after "event".
     * @throws EveException if the description or either date is missing, empty, or malformed.
     */
    private static Event parseEvent(String arguments) throws EveException {
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
        return new Event(description, from, to);
    }

    /**
     * Parses the arguments of an {@code on} command, e.g. {@code "2019-12-02"}.
     *
     * @param arguments the text after "on".
     * @throws EveException if the date is missing or malformed.
     */
    private static LocalDate parseOnDate(String arguments) throws EveException {
        if (arguments.isEmpty()) {
            throw new EveException("OOPS!!! Please tell me which date, e.g. on 2019-12-02.");
        }
        try {
            return LocalDate.parse(arguments);
        } catch (DateTimeParseException e) {
            throw new EveException("OOPS!!! Please give the date as yyyy-mm-dd, "
                    + "e.g. on 2019-12-02.");
        }
    }
}
