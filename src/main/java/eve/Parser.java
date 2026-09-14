package eve;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import eve.command.AddCommand;
import eve.command.Command;
import eve.command.CommandWord;
import eve.command.DeleteCommand;
import eve.command.ExitCommand;
import eve.command.FindCommand;
import eve.command.ListCommand;
import eve.command.MarkCommand;
import eve.command.OnCommand;
import eve.command.ScheduleCommand;
import eve.command.UnmarkCommand;
import eve.task.Deadline;
import eve.task.Event;
import eve.task.ToDo;

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
            case FIND:
                return new FindCommand(parseFindKeyword(arguments));
            case SCHEDULE:
                return new ScheduleCommand(parseOptionalScheduleDate(arguments));
            default:
                // Unreachable: CommandWord.fromWord only ever returns one of the cases above.
                // If a new CommandWord constant is ever added without a case here, this
                // assertion should fail loudly during development instead of silently
                // falling through to a misleading "unknown command" error at runtime.
                assert false : "Unhandled CommandWord: " + commandWord;
                throw new EveException("Oops, I don't recognize that command! No worries -- give it another shot?");
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
            throw new EveException("Oops, I need a task number for that! Try something like mark 2.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new EveException("Oops, '" + text + "' isn't a valid task number! Numbers only, please.");
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
            throw new EveException("Oops, your to-do needs a description! What are we adding?");
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
            throw new EveException("Oops, a deadline needs a description and a '/by' date! "
                    + "Try: deadline return book /by 2019-12-02.");
        }
        String description = arguments.substring(0, byIndex).trim();
        String byText = arguments.substring(byIndex + " /by ".length()).trim();
        if (description.isEmpty()) {
            throw new EveException("Oops, your deadline needs a description too!");
        }
        if (byText.isEmpty()) {
            throw new EveException("Oops, don't forget the '/by' date!");
        }
        LocalDate by = parseDate("'/by' date", byText, "2019-12-02");
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
            throw new EveException("Oops, an event needs a description, a '/from' date, and a '/to' date! "
                    + "Try: event project meeting /from 2019-10-04 /to 2019-10-11.");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String fromText = arguments.substring(fromIndex + " /from ".length(), toIndex).trim();
        String toText = arguments.substring(toIndex + " /to ".length()).trim();
        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new EveException("Oops, fill in the event's description, '/from' date, "
                    + "and '/to' date -- all of them!");
        }
        LocalDate from = parseDate("'/from' date", fromText, "2019-10-04");
        LocalDate to = parseDate("'/to' date", toText, "2019-10-11");
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
            throw new EveException("Oops, which date? Try: on 2019-12-02.");
        }
        return parseDate("date", arguments, "on 2019-12-02");
    }

    /**
     * Parses text as an ISO date, or throws a field-specific error naming
     * what was expected. Shared by every command that takes a date, so the
     * "please use yyyy-mm-dd" message stays worded consistently.
     *
     * @param fieldDescription names the field in the error message, e.g. {@code "'/by' date"}.
     * @param dateText the text to parse.
     * @param example a valid example shown in the error message, e.g. {@code "2019-12-02"}.
     * @throws EveException if the text isn't a valid yyyy-mm-dd date.
     */
    private static LocalDate parseDate(String fieldDescription, String dateText, String example)
            throws EveException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new EveException("Oops, give the " + fieldDescription
                    + " as yyyy-mm-dd! Like " + example + ".");
        }
    }

    /**
     * Parses the arguments of a {@code schedule} command: an optional date
     * to filter to, e.g. {@code "2019-12-02"}, or empty for the whole
     * schedule.
     *
     * @param arguments the text after "schedule".
     * @throws EveException if a date was given but is malformed.
     */
    private static Optional<LocalDate> parseOptionalScheduleDate(String arguments) throws EveException {
        if (arguments.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(parseDate("date", arguments, "schedule 2019-12-02"));
    }

    /**
     * Parses the arguments of a {@code find} command.
     *
     * @param arguments the text after "find".
     * @throws EveException if the keyword is empty.
     */
    private static String parseFindKeyword(String arguments) throws EveException {
        if (arguments.isEmpty()) {
            throw new EveException("Oops, what should I search for? Try: find book.");
        }
        return arguments;
    }
}
