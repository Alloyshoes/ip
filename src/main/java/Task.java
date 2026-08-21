/**
 * A task with a description and a done/not-done status.
 * Base class for the specific task types ({@link ToDo}, {@link Deadline}, {@link Event}).
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description, initially not done.
     *
     * @param description what the task is.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon shown in task listings.
     *
     * @return "X" if this task is done, or " " (a space) otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's display text: its status icon followed by its description.
     * Subclasses extend this with their own type icon and any extra details.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
