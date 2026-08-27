/** A task that needs to be done before a specific date/time. */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates a deadline task.
     *
     * @param description what the task is.
     * @param by when it is due, kept as free-form text (not parsed as a date/time).
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /** Returns this task's display text, prefixed with "[D]" and suffixed with its due date/time. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }

    @Override
    public String toSaveFormat() {
        return "D | " + super.toSaveFormat() + " | " + by;
    }
}
