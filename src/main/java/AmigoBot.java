import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class AmigoBot {

    /**
     * Tries to parse a date string into a LocalDate.
     * Accepts formats like yyyy-mm-dd, dd/mm/yyyy, dd-mm-yyyy, yyyy/mm/dd, etc.
     * Returns null if the string is not a recognizable date (e.g. "tomorrow").
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

    public static void main(String[] args) {
        String line = "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*";

        String banner = "    _          _           ____        _   \n"
                + "   / \\   _ __ (_) __ _  ___| __ )  ___ | |_ \n"
                + "  / _ \\ | '_ \\| |/ _` |/ _ \\  _ \\ / _ \\| __|\n"
                + " / ___ \\| | | | | (_| | (_) | |_) | (_) | |_ \n"
                + "/_/   \\_\\_| |_|_|\\__, |\\___/|____/ \\___/ \\__|\n"
                + "                 |___/                        \n";

	String capybara = 
		    "⠀⠀⢀⣀⠤⠿⢤⢖⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n"
		  + "⡔⢩⠂⠀⠒⠗⠈⠀⠉⠢⠄⣀⠠⠤⠄⠒⢖⡒⢒⠂⠤⢄⠀⠀⠀⠀\n"
		  + "⠇⠤⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠀⠀⠈⠀⠈⠈⡨⢀⠡⡪⠢⡀⠀\n"
		  + "⠈⠒⠀⠤⠤⣄⡆⡂⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠢⠀⢕⠱⠀\n"
		  + "⠀⠀⠀⠀⠀⠈⢳⣐⡐⠐⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠀⠁⠇\n"
		  + "⠀⠀⠀⠀⠀⠀⠀⠑⢤⢁⠀⠆⠀⠀⠀⠀⠀⢀⢰⠀⠀⠀⡀⢄⡜⠀\n"
		  + "⠀⠀⠀⠀⠀⠀⠀⠀⠘⡦⠄⡷⠢⠤⠤⠤⠤⢬⢈⡇⢠⣈⣰⠎⠀⠀\n"
		  + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⣃⢸⡇⠀⠀⠀⠀⠀⠈⢪⢀⣺⡅⢈⠆⠀⠀\n"
		  + "⠀⠀⠀⠀⠀⠀⠀⠶⡿⠤⠚⠁⠀⠀⠀⢀⣠⡤⢺⣥⠟⢡⠃⠀⠀⠀\n"
		  + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠀⠀⠀⠀⠀⠀⠀⠀";

        System.out.println(line);
        System.out.println(banner);
        System.out.println(capybara);
        System.out.println("Hola amigo! I'm AmigoBot.");
        System.out.println("What can I do for you, compadre?");
        System.out.println(line);

        Storage storage = new Storage(java.nio.file.Paths.get("data", "amigobot.txt").toString());
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (IOException e) {
            System.out.println("Ay caramba! Could not load saved tasks: " + e.getMessage());
            tasks = new ArrayList<>();
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            System.out.println(line);
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
                System.out.println("Adios amigo! Hope to see you again soon!");
                break;
            case LIST:
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + "." + tasks.get(i));
                }
                break;
            case DELETE: {
                if (arguments.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! Please provide a task number. Example: delete 1");
                }
                int index;
                try {
                    index = Integer.parseInt(arguments) - 1;
                } catch (NumberFormatException e) {
                    throw new AmigoBotException("Ay caramba! That's not a valid number, compadre.");
                }
                if (index < 0 || index >= tasks.size()) {
                    throw new AmigoBotException("Ay caramba! Task number " + (index + 1) + " does not exist. You have " + tasks.size() + " tasks.");
                }
                Task removed = tasks.remove(index);
                storage.save(tasks);
                System.out.println("Noted. I've removed this task:");
                System.out.println("  " + removed);
                System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                break;
            }
            case MARK: {
                if (arguments.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! Please provide a task number. Example: mark 1");
                }
                int index;
                try {
                    index = Integer.parseInt(arguments) - 1;
                } catch (NumberFormatException e) {
                    throw new AmigoBotException("Ay caramba! That's not a valid number, compadre.");
                }
                if (index < 0 || index >= tasks.size()) {
                    throw new AmigoBotException("Ay caramba! Task number " + (index + 1) + " does not exist. You have " + tasks.size() + " tasks.");
                }
                tasks.get(index).markAsDone();
                storage.save(tasks);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks.get(index));
                break;
            }
            case UNMARK: {
                if (arguments.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! Please provide a task number. Example: unmark 1");
                }
                int index;
                try {
                    index = Integer.parseInt(arguments) - 1;
                } catch (NumberFormatException e) {
                    throw new AmigoBotException("Ay caramba! That's not a valid number, compadre.");
                }
                if (index < 0 || index >= tasks.size()) {
                    throw new AmigoBotException("Ay caramba! Task number " + (index + 1) + " does not exist. You have " + tasks.size() + " tasks.");
                }
                tasks.get(index).markAsNotDone();
                storage.save(tasks);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks.get(index));
                break;
            }
            case TODO: {
                if (arguments.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! The description of a todo cannot be empty.");
                }
                tasks.add(new Todo(arguments));
                storage.save(tasks);
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks.get(tasks.size() - 1));
                System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                break;
            }
            case DEADLINE: {
                if (arguments.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! The description of a deadline cannot be empty.");
                }
                if (!arguments.contains(" /by ")) {
                    throw new AmigoBotException("Ay caramba! A deadline needs a /by date. Example: deadline return book /by Sunday");
                }
                String[] parts = arguments.split(" /by ", 2);
                String desc = parts[0].trim();
                String byStr = parts[1].trim();
                if (byStr.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! The /by date of a deadline cannot be empty.");
                }
                LocalDate byDate = tryParseDate(byStr);
                if (byDate != null) {
                    tasks.add(new Deadline(desc, byDate));
                } else {
                    tasks.add(new Deadline(desc, byStr));
                }
                storage.save(tasks);
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks.get(tasks.size() - 1));
                System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                break;
            }
            case EVENT: {
                if (arguments.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! The description of an event cannot be empty.");
                }
                if (!arguments.contains(" /from ")) {
                    throw new AmigoBotException("Ay caramba! An event needs a /from time. Example: event meeting /from Mon 2pm /to 4pm");
                }
                String[] parts = arguments.split(" /from ", 2);
                String desc = parts[0].trim();
                if (!parts[1].contains(" /to ")) {
                    throw new AmigoBotException("Ay caramba! An event needs a /to time. Example: event meeting /from Mon 2pm /to 4pm");
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
                tasks.add(new Event(desc, fromDate, fromDate == null ? from : null,
                        toDate, toDate == null ? to : null));
                storage.save(tasks);
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks.get(tasks.size() - 1));
                System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                break;
            }
            case ON: {
                if (arguments.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! Please provide a date. Example: on 2025-08-20");
                }
                LocalDate target = tryParseDate(arguments);
                if (target == null) {
                    throw new AmigoBotException("Ay caramba! Invalid date format. Please use yyyy-MM-dd or dd/mm/yyyy.");
                }
                System.out.println("Here are the tasks on " + target.format(
                        java.time.format.DateTimeFormatter.ofPattern("MMM d yyyy")) + ":");
                int count = 0;
                for (int i = 0; i < tasks.size(); i++) {
                    Task task = tasks.get(i);
                    boolean match = false;
                    if (task instanceof Deadline) {
                        Deadline d = (Deadline) task;
                        match = d.byDate != null && d.byDate.equals(target);
                    } else if (task instanceof Event) {
                        Event e = (Event) task;
                        if (e.fromDate != null && e.toDate != null) {
                            match = !target.isBefore(e.fromDate) && !target.isAfter(e.toDate);
                        } else if (e.fromDate != null) {
                            match = e.fromDate.equals(target);
                        } else if (e.toDate != null) {
                            match = e.toDate.equals(target);
                        }
                    }
                    if (match) {
                        count++;
                        System.out.println(count + "." + task);
                    }
                }
                if (count == 0) {
                    System.out.println("No tasks found on that date, compadre!");
                }
                break;
            }
            case UNKNOWN:
            default:
                throw new AmigoBotException("Ay caramba! I don't know what that means, compadre :-(");
            }
            } catch (AmigoBotException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Ay caramba! Could not save tasks: " + e.getMessage());
            }
            System.out.println(line);
            if (input.equalsIgnoreCase("bye")) {
                break;
            }
        }
        scanner.close();
    }
}
