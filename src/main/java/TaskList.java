import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the current list of tasks and the operations that act on it
 * (adding, deleting, looking one up by position, or finding the ones that
 * occur on a given date). Callers never see the underlying storage, so how
 * the list is represented internally can change without affecting them.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list pre-populated with the given tasks, e.g. ones just
     * loaded from disk.
     *
     * @param tasks the initial tasks, in order.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given position.
     *
     * @param index the 0-based position of the task to remove.
     * @return the task that was removed.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index the 0-based position of the task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns how many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Validates a 1-based task number typed by the user against the
     * current list size, and converts it to a 0-based index.
     *
     * @param taskNumber the 1-based task number to validate.
     * @return the corresponding 0-based index.
     * @throws EveException if the number is out of range.
     */
    public int toIndex(int taskNumber) throws EveException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EveException("OOPS!!! There is no task number " + taskNumber + " in your list.");
        }
        return taskNumber - 1;
    }

    /**
     * Returns every task that occurs on the given date (see
     * {@link Task#occursOn}), in list order.
     *
     * @param date the date to check against.
     */
    public List<Task> occurringOn(LocalDate date) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /** Returns a read-only view of every task in the list, in order. */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
