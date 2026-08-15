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

        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            System.out.println(line);
            if (input.equals("bye")) {
                System.out.println("Adios amigo! Hope to see you again soon!");
            } else if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String status = isDone[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + status + "] " + tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5)) - 1;
                isDone[index] = true;
                String status = isDone[index] ? "X" : " ";
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  [" + status + "] " + tasks[index]);
            } else {
                tasks[taskCount] = input;
                isDone[taskCount] = false;
                taskCount++;
                System.out.println("added: " + input);
            }
            System.out.println(line);
            if (input.equals("bye")) {
                break;
            }
        }
        scanner.close();
    }
}
