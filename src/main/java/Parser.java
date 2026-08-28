import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Deals with making sense of a user command: splitting a line of input into
 * a command word and its arguments, and turning each command's arguments
 * into validated, ready-to-use values (a constructed {@link Task}, a
 * {@link LocalDate}, a task number) or an {@link EveException} explaining
 * what was wrong with them.
 */
public class Parser {
    private Parser() {
        // Not meant to be instantiated: every method is static.
    }

    /**
     * Returns the first word of a line of input, e.g. {@code "todo"} from
     * {@code "todo read book"}.
     */
    public static String getCommandWord(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? input : input.substring(0, spaceIndex);
    }

    /**
     * Returns everything after the first word of a line of input, trimmed,
     * or an empty string if there's only one word.
     */
    public static String getArguments(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? "" : input.substring(spaceIndex + 1).trim();
    }

    /**
     * Parses and validates a 1-based task number typed by the user.
     *
     * @param text the argument text after the command word, e.g. "2".
     * @param taskCount how many tasks currently exist, for range checking.
     * @return the parsed task number (1-based).
     * @throws EveException if the text is missing, not a number, or out of range.
     */
    public static int parseTaskNumber(String text, int taskCount) throws EveException {
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

    /**
     * Parses the arguments of a {@code todo} command.
     *
     * @param arguments the text after "todo".
     * @throws EveException if the description is empty.
     */
    public static ToDo parseTodo(String arguments) throws EveException {
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
    public static Deadline parseDeadline(String arguments) throws EveException {
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
    public static Event parseEvent(String arguments) throws EveException {
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
    public static LocalDate parseOnDate(String arguments) throws EveException {
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
