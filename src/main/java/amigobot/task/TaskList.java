package amigobot.task;

import java.util.ArrayList;

/**
 * Wraps an ArrayList of tasks and provides operations to add, delete, and access tasks.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index (0-based).
     */
    public Task deleteTask(int index) {
        return tasks.remove(index);
    }

    public Task getTask(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose description contains the given keyword (case-insensitive).
     */
    public ArrayList<Task> findTasks(String keyword) {
        ArrayList<Task> matches = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowerKeyword)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Returns the underlying list for serialization by Storage.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
