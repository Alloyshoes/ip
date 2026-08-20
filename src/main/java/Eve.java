import java.util.Scanner;

public class Eve {
    public static void main(String[] args) {
        String banner = " _____  __   __  _____ \n"
                + "|  ___| \\ \\ / / |  ___|\n"
                + "| |__    \\ V /  | |__  \n"
                + "|  __|    \\ /   |  __| \n"
                + "|_____|    V    |_____|";
        String line = "____________________________________________________________";

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
            else {
                System.out.println(line);
                System.out.println(input);
                System.out.println(line);
            }
        }
        scanner.close();
    }
}
