import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes the task list to a fixed location on disk so tasks
 * persist between runs of the program.
 */
public class Storage {
    private static final Path FILE_PATH = Paths.get("data", "eve.txt");

    /**
     * Loads tasks from disk. If the data file or its folder doesn't exist
     * yet (e.g. first run), returns an empty list instead of failing.
     * Lines that don't match the expected format are skipped with a
     * warning rather than crashing the program.
     *
     * @return the tasks read from disk, in file order.
     */
    public static List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(FILE_PATH)) {
                Task task = parseLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read saved tasks (" + e.getMessage()
                    + "). Starting with an empty list.");
        }
        return tasks;
    }

    private static Task parseLine(String line) {
        try {
            String[] parts = line.split(" \\| ");
            if (!parts[1].equals("0") && !parts[1].equals("1")) {
                System.out.println("Warning: skipping corrupted line in data file: " + line);
                return null;
            }
            boolean isDone = parts[1].equals("1");
            String description = parts[2];
            Task task;
            switch (parts[0]) {
                case "T":
                    task = new ToDo(description);
                    break;
                case "D":
                    task = new Deadline(description, LocalDate.parse(parts[3]));
                    break;
                case "E":
                    task = new Event(description, LocalDate.parse(parts[3]), LocalDate.parse(parts[4]));
                    break;
                default:
                    System.out.println("Warning: skipping corrupted line in data file: " + line);
                    return null;
            }
            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (RuntimeException e) {
            System.out.println("Warning: skipping corrupted line in data file: " + line);
            return null;
        }
    }

    /**
     * Saves the given tasks to disk, creating the data folder if needed.
     *
     * @param tasks the tasks currently in the list.
     */
    public static void save(List<Task> tasks) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toSaveFormat());
            }
            Files.write(FILE_PATH, lines);
        } catch (IOException e) {
            System.out.println("Warning: could not save tasks (" + e.getMessage() + ").");
        }
    }
}
