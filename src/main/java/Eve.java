import java.util.Scanner;

public class Eve {
    public static void main(String[] args) {
        String banner = " _____  __   __  _____ \n"
                + "|  ___| \\ \\ / / |  ___|\n"
                + "| |__    \\ V /  | |__  \n"
                + "|  __|    \\ /   |  __| \n"
                + "|_____|    V    |_____|";
        String line = "____________________________________________________________";

        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm Eve.");
        System.out.println("What can I do for you?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }
            else if (input.equals("list")) {
                System.out.println(line);
                for (int i = 0; i < taskCount; i++) {
                    String statusIcon = isDone[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + statusIcon + "] " + tasks[i]);
                }
                System.out.println(line);
            }
            else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5)) - 1;
                isDone[index] = true;
                System.out.println(line);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  [X] " + tasks[index]);
                System.out.println(line);
            }
            else if (input.startsWith("Unmark ")) {
                int index = Integer.parseInt(input.substring(7)) - 1;
                isDone[index] = false;
                System.out.println(line);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  [ ] " + tasks[index]);
                System.out.println(line);
            }
            else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println(line);
                System.out.println("added: " + input);
                System.out.println(line);
            }
        }
        scanner.close();
    }
}
