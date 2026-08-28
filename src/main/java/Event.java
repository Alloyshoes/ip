import java.time.LocalDate;

/** A task that starts on a specific date and ends on a specific date. */
public class Event extends Task {
    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates an event task.
     *
     * @param description what the task is.
     * @param from the date it starts.
     * @param to the date it ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns this task's display text, prefixed with "[E]" and suffixed with its start/end dates. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from + " | " + to;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }
}
