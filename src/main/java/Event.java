/** A task that starts at a specific date/time and ends at a specific date/time. */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an event task.
     *
     * @param description what the task is.
     * @param from when it starts, kept as free-form text (not parsed as a date/time).
     * @param to when it ends, kept as free-form text (not parsed as a date/time).
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns this task's display text, prefixed with "[E]" and suffixed with its start/end. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
