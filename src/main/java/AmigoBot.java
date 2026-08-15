import java.util.Scanner;

public class AmigoBot {
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

        Task[] tasks = new Task[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            System.out.println(line);
            try {
            if (input.equals("bye")) {
                System.out.println("Adios amigo! Hope to see you again soon!");
            } else if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (input.equals("mark") || input.equals("unmark")) {
                throw new AmigoBotException("Ay caramba! Please provide a task number. Example: mark 1");
            } else if (input.startsWith("mark ")) {
                int index;
                try {
                    index = Integer.parseInt(input.substring(5)) - 1;
                } catch (NumberFormatException e) {
                    throw new AmigoBotException("Ay caramba! That's not a valid number, compadre.");
                }
                if (index < 0 || index >= taskCount) {
                    throw new AmigoBotException("Ay caramba! Task number " + (index + 1) + " does not exist. You have " + taskCount + " tasks.");
                }
                tasks[index].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[index]);
            } else if (input.startsWith("unmark ")) {
                int index;
                try {
                    index = Integer.parseInt(input.substring(7)) - 1;
                } catch (NumberFormatException e) {
                    throw new AmigoBotException("Ay caramba! That's not a valid number, compadre.");
                }
                if (index < 0 || index >= taskCount) {
                    throw new AmigoBotException("Ay caramba! Task number " + (index + 1) + " does not exist. You have " + taskCount + " tasks.");
                }
                tasks[index].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[index]);
            } else if (input.equals("todo") || input.startsWith("todo ")) {
                String desc = input.substring(4).trim();
                if (desc.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! The description of a todo cannot be empty.");
                }
                tasks[taskCount] = new Todo(desc);
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                String deadlineArgs = input.substring(8).trim();
                if (deadlineArgs.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! The description of a deadline cannot be empty.");
                }
                if (!deadlineArgs.contains(" /by ")) {
                    throw new AmigoBotException("Ay caramba! A deadline needs a /by date. Example: deadline return book /by Sunday");
                }
                String[] parts = deadlineArgs.split(" /by ", 2);
                String desc = parts[0].trim();
                String by = parts[1].trim();
                if (by.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! The /by date of a deadline cannot be empty.");
                }
                tasks[taskCount] = new Deadline(desc, by);
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else if (input.equals("event") || input.startsWith("event ")) {
                String eventArgs = input.substring(5).trim();
                if (eventArgs.isEmpty()) {
                    throw new AmigoBotException("Ay caramba! The description of an event cannot be empty.");
                }
                if (!eventArgs.contains(" /from ")) {
                    throw new AmigoBotException("Ay caramba! An event needs a /from time. Example: event meeting /from Mon 2pm /to 4pm");
                }
                String[] parts = eventArgs.split(" /from ", 2);
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
                tasks[taskCount] = new Event(desc, from, to);
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else {
                throw new AmigoBotException("Ay caramba! I don't know what that means, compadre :-(");
            }
            } catch (AmigoBotException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(line);
            if (input.equals("bye")) {
                break;
            }
        }
        scanner.close();
    }
}
