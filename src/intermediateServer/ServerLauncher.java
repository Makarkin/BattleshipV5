package intermediateServer;

import java.util.Scanner;

public class ServerLauncher {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your port");
        int port = 6667;//Integer.valueOf(scanner.nextLine());
        Server server = new Server(port);
        server.start();
    }
}
