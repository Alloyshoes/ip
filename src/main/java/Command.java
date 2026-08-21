public enum Command {
    TODO("todo <description>", "Add a to-do task."),
    DEADLINE("deadline <description> /by <date/time>", "Add a task with a deadline."),
    EVENT("event <description> /from <start> /to <end>", "Add an event."),
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

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public static Command fromWord(String word) throws EveException {
        try {
            return Command.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EveException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }
}
