package intermediateServer;

import java.util.Scanner;

public class IntrServerLauncher {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your port");
        int port = 6667;//Integer.valueOf(scanner.nextLine());
        IntrServer server = new IntrServer(port);
        server.start();
    }
}
