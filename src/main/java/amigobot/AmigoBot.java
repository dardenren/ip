package amigobot;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import amigobot.command.Command;
import amigobot.task.Deadline;
import amigobot.task.Event;
import amigobot.task.Task;
import amigobot.task.TaskList;
import amigobot.task.Todo;

/**
 * Main class for the AmigoBot chatbot application.
 * Processes user commands and manages the task list.
 */
public class AmigoBot {

    private final Storage storage;
    private TaskList tasks;

    /**
     * Constructs an AmigoBot that loads tasks from the default data file.
     */
    public AmigoBot() {
        this.storage = new Storage(java.nio.file.Paths.get("data", "amigobot.txt").toString());
        try {
            this.tasks = new TaskList(storage.load());
        } catch (IOException e) {
            this.tasks = new TaskList();
        }
    }

    /**
     * Processes the given user input and returns the response string.
     *
     * @param input the raw user input.
     * @return the response to display.
     */
    public String getResponse(String input) {
        try {
            String[] words = input.split(" ", 2);
            String commandWord = words[0].toUpperCase();
            String arguments = words.length > 1 ? words[1].trim() : "";

            Command command;
            try {
                command = Command.valueOf(commandWord);
            } catch (IllegalArgumentException e) {
                command = Command.UNKNOWN;
            }

            return executeCommand(command, arguments);
        } catch (AmigoBotException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "Ay caramba! Could not save tasks: " + e.getMessage();
        }
    }

    /**
     * Executes the given command with the provided arguments and returns the response.
     */
    private String executeCommand(Command command, String arguments)
            throws AmigoBotException, IOException {
        switch (command) {
            case BYE:
                return "Adios amigo! Hope to see you again soon!";
            case LIST:
                return formatTaskList();
            case DELETE:
                return handleDelete(arguments);
            case MARK:
                return handleMark(arguments);
            case UNMARK:
                return handleUnmark(arguments);
            case TODO:
                return handleTodo(arguments);
            case DEADLINE:
                return handleDeadline(arguments);
            case EVENT:
                return handleEvent(arguments);
            case ON:
                return handleOn(arguments);
            case FIND:
                return handleFind(arguments);
            case UNKNOWN:
            default:
                throw new AmigoBotException("Ay caramba! I don't know what that means, compadre :-(");
        }
    }

    private String formatTaskList() {
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append("\n").append(i + 1).append(".").append(tasks.getTask(i));
        }
        return sb.toString();
    }

    private String handleDelete(String arguments) throws AmigoBotException, IOException {
        int index = parseTaskIndex(arguments, "delete 1");
        Task removed = tasks.deleteTask(index);
        storage.save(tasks);
        return "Noted. I've removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String handleMark(String arguments) throws AmigoBotException, IOException {
        int index = parseTaskIndex(arguments, "mark 1");
        tasks.getTask(index).markAsDone();
        storage.save(tasks);
        return "Nice! I've marked this task as done:\n  " + tasks.getTask(index);
    }

    private String handleUnmark(String arguments) throws AmigoBotException, IOException {
        int index = parseTaskIndex(arguments, "unmark 1");
        tasks.getTask(index).markAsNotDone();
        storage.save(tasks);
        return "OK, I've marked this task as not done yet:\n  " + tasks.getTask(index);
    }

    private String handleTodo(String arguments) throws AmigoBotException, IOException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The description of a todo cannot be empty.");
        }
        Task task = new Todo(arguments);
        tasks.addTask(task);
        storage.save(tasks);
        return formatTaskAdded(task);
    }

    private String handleDeadline(String arguments) throws AmigoBotException, IOException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The description of a deadline cannot be empty.");
        }
        if (!arguments.contains(" /by ")) {
            throw new AmigoBotException(
                    "Ay caramba! A deadline needs a /by date. Example: deadline return book /by Sunday");
        }
        String[] parts = arguments.split(" /by ", 2);
        String desc = parts[0].trim();
        String byStr = parts[1].trim();
        if (byStr.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The /by date of a deadline cannot be empty.");
        }
        LocalDate byDate = tryParseDate(byStr);
        Task task;
        if (byDate != null) {
            task = new Deadline(desc, byDate);
        } else {
            task = new Deadline(desc, byStr);
        }
        tasks.addTask(task);
        storage.save(tasks);
        return formatTaskAdded(task);
    }

    private String handleEvent(String arguments) throws AmigoBotException, IOException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The description of an event cannot be empty.");
        }
        if (!arguments.contains(" /from ")) {
            throw new AmigoBotException(
                    "Ay caramba! An event needs a /from time. Example: event meeting /from Mon 2pm /to 4pm");
        }
        String[] parts = arguments.split(" /from ", 2);
        String desc = parts[0].trim();
        if (!parts[1].contains(" /to ")) {
            throw new AmigoBotException(
                    "Ay caramba! An event needs a /to time. Example: event meeting /from Mon 2pm /to 4pm");
        }
        String[] timeParts = parts[1].split(" /to ", 2);
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (from.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The /from time of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The /to time of an event cannot be empty.");
        }
        LocalDate fromDate = tryParseDate(from);
        LocalDate toDate = tryParseDate(to);
        Task task = new Event(desc, fromDate, fromDate == null ? from : null,
                toDate, toDate == null ? to : null);
        tasks.addTask(task);
        storage.save(tasks);
        return formatTaskAdded(task);
    }

    private String handleOn(String arguments) throws AmigoBotException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException("Ay caramba! Please provide a date. Example: on 2025-08-20");
        }
        LocalDate target = tryParseDate(arguments);
        if (target == null) {
            throw new AmigoBotException(
                    "Ay caramba! Invalid date format. Please use yyyy-MM-dd or dd/mm/yyyy.");
        }
        ArrayList<Task> matching = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.getTask(i);
            if (isOnDate(task, target)) {
                matching.add(task);
            }
        }
        String dateDisplay = target.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
        StringBuilder sb = new StringBuilder("Here are the tasks on " + dateDisplay + ":");
        if (matching.isEmpty()) {
            sb.append("\nNo tasks found on that date, compadre!");
        } else {
            for (int i = 0; i < matching.size(); i++) {
                sb.append("\n").append(i + 1).append(".").append(matching.get(i));
            }
        }
        return sb.toString();
    }

    private String handleFind(String arguments) throws AmigoBotException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException(
                    "Ay caramba! Please provide a keyword to search for. Example: find book");
        }
        ArrayList<Task> found = tasks.findTasks(arguments);
        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:");
        if (found.isEmpty()) {
            sb.append("\nNo matching tasks found, compadre!");
        } else {
            for (int i = 0; i < found.size(); i++) {
                sb.append("\n").append(i + 1).append(".").append(found.get(i));
            }
        }
        return sb.toString();
    }

    /**
     * Parses and validates a 1-based task index from the arguments string.
     */
    private int parseTaskIndex(String arguments, String example) throws AmigoBotException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException(
                    "Ay caramba! Please provide a task number. Example: " + example);
        }
        int index;
        try {
            index = Integer.parseInt(arguments) - 1;
        } catch (NumberFormatException e) {
            throw new AmigoBotException("Ay caramba! That's not a valid number, compadre.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new AmigoBotException("Ay caramba! Task number " + (index + 1)
                    + " does not exist. You have " + tasks.size() + " tasks.");
        }
        return index;
    }

    private String formatTaskAdded(Task task) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private boolean isOnDate(Task task, LocalDate target) {
        if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            return d.getByDate() != null && d.getByDate().equals(target);
        } else if (task instanceof Event) {
            Event e = (Event) task;
            if (e.getFromDate() != null && e.getToDate() != null) {
                return !target.isBefore(e.getFromDate()) && !target.isAfter(e.getToDate());
            } else if (e.getFromDate() != null) {
                return e.getFromDate().equals(target);
            } else if (e.getToDate() != null) {
                return e.getToDate().equals(target);
            }
        }
        return false;
    }

    /**
     * Tries to parse a date string into a LocalDate.
     * Normalizes separators (/, ., -) and detects whether the format is
     * year-first (yyyy-mm-dd) or day-first (dd-mm-yyyy).
     *
     * @param input the date string to parse.
     * @return the parsed LocalDate, or null if the string is not a recognizable date.
     */
    private static LocalDate tryParseDate(String input) {
        String normalized = input.replace("/", "-").replace(".", "-");
        String[] parts = normalized.split("-");
        if (parts.length != 3) {
            return null;
        }
        try {
            if (parts[0].length() == 4) {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                return LocalDate.of(year, month, day);
            } else if (parts[2].length() == 4) {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);
                return LocalDate.of(year, month, day);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Entry point for the text-based CLI mode.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        AmigoBot bot = new AmigoBot();

        while (true) {
            String input = ui.readCommand();
            ui.showLine();
            System.out.println(bot.getResponse(input));
            ui.showLine();
            if (input.equalsIgnoreCase("bye")) {
                break;
            }
        }
        ui.close();
    }
}
