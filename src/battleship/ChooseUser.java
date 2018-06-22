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
        System.out.println("Choose number of your opponent. r - refresh list");
        int index;
        EnterNameCountdown countdown = new EnterNameCountdown();
        StringReader stringReader = new StringReader();
        stringReader.start();
        countdown.start();
        try {
            countdown.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String request;
        System.out.println(ClientModel.getTempString());
        if ("r".equals(ClientModel.getTempString())) {
            return;
        } else {
            System.out.println(ClientModel.getTempString());
            index = Integer.valueOf(ClientModel.getTempString());
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
