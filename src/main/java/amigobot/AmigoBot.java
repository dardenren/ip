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
 * Handles the command loop that reads user input, parses commands,
 * and delegates to the appropriate task operations.
 */
public class AmigoBot {

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
     * Entry point for the AmigoBot application.
     * Initializes the UI, storage, and task list, then runs the command loop.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Storage storage = new Storage(java.nio.file.Paths.get("data", "amigobot.txt").toString());
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }

        while (true) {
            String input = ui.readCommand();
            ui.showLine();
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

                switch (command) {
                    case BYE:
                        ui.showGoodbye();
                        break;
                    case LIST:
                        ui.showTaskList(tasks);
                        break;
                    case DELETE: {
                        if (arguments.isEmpty()) {
                            throw new AmigoBotException(
                                    "Ay caramba! Please provide a task number. Example: delete 1");
                        }
                        int index;
                        try {
                            index = Integer.parseInt(arguments) - 1;
                        } catch (NumberFormatException e) {
                            throw new AmigoBotException(
                                    "Ay caramba! That's not a valid number, compadre.");
                        }
                        if (index < 0 || index >= tasks.size()) {
                            throw new AmigoBotException("Ay caramba! Task number "
                                    + (index + 1) + " does not exist. You have "
                                    + tasks.size() + " tasks.");
                        }
                        Task removed = tasks.deleteTask(index);
                        storage.save(tasks);
                        ui.showTaskDeleted(removed, tasks.size());
                        break;
                    }
                    case MARK: {
                        if (arguments.isEmpty()) {
                            throw new AmigoBotException(
                                    "Ay caramba! Please provide a task number. Example: mark 1");
                        }
                        int index;
                        try {
                            index = Integer.parseInt(arguments) - 1;
                        } catch (NumberFormatException e) {
                            throw new AmigoBotException(
                                    "Ay caramba! That's not a valid number, compadre.");
                        }
                        if (index < 0 || index >= tasks.size()) {
                            throw new AmigoBotException("Ay caramba! Task number "
                                    + (index + 1) + " does not exist. You have "
                                    + tasks.size() + " tasks.");
                        }
                        tasks.getTask(index).markAsDone();
                        storage.save(tasks);
                        ui.showTaskMarked(tasks.getTask(index));
                        break;
                    }
                    case UNMARK: {
                        if (arguments.isEmpty()) {
                            throw new AmigoBotException(
                                    "Ay caramba! Please provide a task number. Example: unmark 1");
                        }
                        int index;
                        try {
                            index = Integer.parseInt(arguments) - 1;
                        } catch (NumberFormatException e) {
                            throw new AmigoBotException(
                                    "Ay caramba! That's not a valid number, compadre.");
                        }
                        if (index < 0 || index >= tasks.size()) {
                            throw new AmigoBotException("Ay caramba! Task number "
                                    + (index + 1) + " does not exist. You have "
                                    + tasks.size() + " tasks.");
                        }
                        tasks.getTask(index).markAsNotDone();
                        storage.save(tasks);
                        ui.showTaskUnmarked(tasks.getTask(index));
                        break;
                    }
                    case TODO: {
                        if (arguments.isEmpty()) {
                            throw new AmigoBotException(
                                    "Ay caramba! The description of a todo cannot be empty.");
                        }
                        Task task = new Todo(arguments);
                        tasks.addTask(task);
                        storage.save(tasks);
                        ui.showTaskAdded(task, tasks.size());
                        break;
                    }
                    case DEADLINE: {
                        if (arguments.isEmpty()) {
                            throw new AmigoBotException(
                                    "Ay caramba! The description of a deadline cannot be empty.");
                        }
                        if (!arguments.contains(" /by ")) {
                            throw new AmigoBotException(
                                    "Ay caramba! A deadline needs a /by date."
                                    + " Example: deadline return book /by Sunday");
                        }
                        String[] parts = arguments.split(" /by ", 2);
                        String desc = parts[0].trim();
                        String byStr = parts[1].trim();
                        if (byStr.isEmpty()) {
                            throw new AmigoBotException(
                                    "Ay caramba! The /by date of a deadline cannot be empty.");
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
                        ui.showTaskAdded(task, tasks.size());
                        break;
                    }
                    case EVENT: {
                        if (arguments.isEmpty()) {
                            throw new AmigoBotException(
                                    "Ay caramba! The description of an event cannot be empty.");
                        }
                        if (!arguments.contains(" /from ")) {
                            throw new AmigoBotException(
                                    "Ay caramba! An event needs a /from time."
                                    + " Example: event meeting /from Mon 2pm /to 4pm");
                        }
                        String[] parts = arguments.split(" /from ", 2);
                        String desc = parts[0].trim();
                        if (!parts[1].contains(" /to ")) {
                            throw new AmigoBotException(
                                    "Ay caramba! An event needs a /to time."
                                    + " Example: event meeting /from Mon 2pm /to 4pm");
                        }
                        String[] timeParts = parts[1].split(" /to ", 2);
                        String from = timeParts[0].trim();
                        String to = timeParts[1].trim();
                        if (from.isEmpty()) {
                            throw new AmigoBotException(
                                    "Ay caramba! The /from time of an event cannot be empty.");
                        }
                        if (to.isEmpty()) {
                            throw new AmigoBotException(
                                    "Ay caramba! The /to time of an event cannot be empty.");
                        }
                        LocalDate fromDate = tryParseDate(from);
                        LocalDate toDate = tryParseDate(to);
                        Task task = new Event(desc, fromDate,
                                fromDate == null ? from : null,
                                toDate, toDate == null ? to : null);
                        tasks.addTask(task);
                        storage.save(tasks);
                        ui.showTaskAdded(task, tasks.size());
                        break;
                    }
                    case ON: {
                        if (arguments.isEmpty()) {
                            throw new AmigoBotException(
                                    "Ay caramba! Please provide a date. Example: on 2025-08-20");
                        }
                        LocalDate target = tryParseDate(arguments);
                        if (target == null) {
                            throw new AmigoBotException(
                                    "Ay caramba! Invalid date format."
                                    + " Please use yyyy-MM-dd or dd/mm/yyyy.");
                        }
                        ArrayList<Task> matching = new ArrayList<>();
                        for (int i = 0; i < tasks.size(); i++) {
                            Task task = tasks.getTask(i);
                            boolean isMatch = false;
                            if (task instanceof Deadline) {
                                Deadline d = (Deadline) task;
                                isMatch = d.getByDate() != null
                                        && d.getByDate().equals(target);
                            } else if (task instanceof Event) {
                                Event e = (Event) task;
                                if (e.getFromDate() != null && e.getToDate() != null) {
                                    isMatch = !target.isBefore(e.getFromDate())
                                            && !target.isAfter(e.getToDate());
                                } else if (e.getFromDate() != null) {
                                    isMatch = e.getFromDate().equals(target);
                                } else if (e.getToDate() != null) {
                                    isMatch = e.getToDate().equals(target);
                                }
                            }
                            if (isMatch) {
                                matching.add(task);
                            }
                        }
                        ui.showTasksOnDate(
                                target.format(DateTimeFormatter.ofPattern("MMM d yyyy")),
                                matching);
                        break;
                    }
                    case UNKNOWN:
                    default:
                        throw new AmigoBotException(
                                "Ay caramba! I don't know what that means, compadre :-(");
                }
            } catch (AmigoBotException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showSavingError(e.getMessage());
            }
            ui.showLine();
            if (input.equalsIgnoreCase("bye")) {
                break;
            }
        }
        ui.close();
    }
}
