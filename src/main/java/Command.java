/**
 * The chatbot's recognized command words, each with its usage syntax and a
 * short description shown in the startup greeting.
 */
public enum Command {
    TODO("todo <description>", "Add a to-do task."),
    DEADLINE("deadline <description> /by <yyyy-mm-dd>", "Add a task with a deadline."),
    EVENT("event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>", "Add an event."),
    ON("on <yyyy-mm-dd>", "Show tasks occurring on a date."),
    LIST("list", "Show all tasks."),
    MARK("mark <task number>", "Mark a task as done."),
    UNMARK("unmark <task number>", "Mark a task as not done."),
    DELETE("delete <task number>", "Remove a task."),
    BYE("bye", "Exit the program.");

    private final String usage;
    private final String description;

    Command(String usage, String description) {
        this.usage = usage;
        this.description = description;
    }

    /** Returns this command's usage syntax, e.g. {@code "mark <task number>"}. */
    public String getUsage() {
        return usage;
    }

    /** Returns a short, human-readable description of what this command does. */
    public String getDescription() {
        return description;
    }

    /**
     * Looks up the command matching the given word (case-insensitive).
     *
     * @param word the first word of a line of user input.
     * @return the matching command.
     * @throws EveException if the word doesn't match any known command.
     */
    public static Command fromWord(String word) throws EveException {
        try {
            return Command.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EveException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }
}
