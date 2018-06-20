package battleship;

import generalClasses.LongMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class ChooseUser extends Thread {
    private String[] users;
    private Controller controller;
    private ObjectOutputStream outputStream;

    ChooseUser(String[] users, Controller controller, ObjectOutputStream outputStream) {
        this.controller = controller;
        this.users = users;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        System.out.println("Choose number of your opponent. For refresh opponents list enter \"r\" ");
        int index;
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        System.out.println(s);
        String request;
        if ("r".equals(s)) {
            return;
        } else {
            index = Integer.valueOf(s);
            System.out.println(users[index]);
            String[] result = users[index].split(" ");
            if (!Boolean.valueOf(result[1])) {
                request = "request " + result[0] + " " + controller.getMainView().getYouLabel().getText();
            } else {
                return;
            }
        }

        if (!"".equals(request)) {
            try {
                outputStream.writeObject(new LongMessage(request));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
