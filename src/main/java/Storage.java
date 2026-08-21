import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Handles saving tasks to a file on disk.
 * Uses a pipe-delimited format: TYPE | DONE | DESCRIPTION | ...extra fields.
 */
public class Storage {

    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Saves all tasks to the file, creating the parent directory if needed.
     * Each task is written in pipe-delimited format, e.g.:
     *   T | 1 | read book
     *   D | 0 | return book | June 6th
     *   E | 0 | project meeting | Mon 2pm | 4pm
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        Files.createDirectories(filePath.getParent());
        FileWriter writer = new FileWriter(filePath.toFile());
        for (Task task : tasks) {
            writer.write(toFileFormat(task) + System.lineSeparator());
        }
        writer.close();
    }

    /**
     * Converts a Task into its pipe-delimited file format string.
     */
    private String toFileFormat(Task task) {
        int done = task.isDone ? 1 : 0;
        if (task instanceof Event) {
            Event e = (Event) task;
            return "E | " + done + " | " + e.description + " | " + e.from + " | " + e.to;
        } else if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            return "D | " + done + " | " + d.description + " | " + d.by;
        } else {
            return "T | " + done + " | " + task.description;
        }
    }
}
