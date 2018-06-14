package battleship;

import generalClasses.LongMessage;
import javafx.application.Platform;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class ClientModel extends Thread {

    private Controller controller;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private String opponentName;
    private String yourName;

    ClientModel(Controller controller) throws IOException {
        InetAddress address = InetAddress.getByName(controller.getIntrServerAddress());
        this.socket = new Socket(address, controller.getPort());
        this.controller = controller;
        this.yourName = controller.getMainView().getYouLabel().getText();
    }

    @Override
    public void run() {
        String fileLogName = String.format("%sBattleLog.txt", yourName);
        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(fileLogName, true))) {
            String[] users;
            LongMessage longMessage = new LongMessage(yourName, controller.getYourBoard());
            outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            outputStream.writeObject(longMessage);
            ObjectInputStream inputStream = new ObjectInputStream(this.socket.getInputStream());
            printWriter.println("Battle started");
            while (true) {
                LongMessage message = (LongMessage) inputStream.readObject();
                users = message.getOnlineUsers();
                ChooseUser chooseUser = new ChooseUser(users, controller, outputStream);
                if (users != null) {
                    showUser(users);
                    chooseUser.start();
                    printWriter.println("Selecting an opponent");
                } else {
                    String[] response = message.getReport().split(" ");
                    String element = response[0];
                    if ("y".equals(element)) {
                        opponentName = response[1];
                        printWriter.printf("Opponent selected. It is %s", opponentName);
                        printWriter.println();
                        Platform.runLater(() -> controller.getMainView().getEnemyLabel().setText(opponentName));
                    } else if ("n".equals(element)) {
                        return;
                    } else if ("enemyResult".equals(element)) {
                        printWriter.printf("Enemy fire from %s accepted. %s", opponentName, acceptFire(response));
                        printWriter.println();
                    } else if ("yourResult".equals(element)) {
                        printWriter.printf("Your fire on %s. %s", opponentName, acceptResultOfYourFire(response));
                        printWriter.println();
                    } else if ("Do".equals(element)) {
                        acceptOrRejectPlayer(response);
                        printWriter.println("Consideration of the request from the player");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void acceptOrRejectPlayer(String[] response) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("Do you want to play with %s y/n", response[response.length - 1]);
        System.out.println();
        String s = "y"/*scanner.nextLine()*/;
        if ("y".equals(s)) {
            controller.setYourTurn(true);
            opponentName = response[response.length - 1];
            Platform.runLater(() -> controller.getMainView().getEnemyLabel().setText(opponentName));
            outputStream.writeObject(new LongMessage("y " + opponentName + " " + yourName));
        } else {
            outputStream.writeObject(new LongMessage("n " + response[response.length - 1] + " " + yourName));
        }
    }

    private void showUser(String[] users) {
        Date date = new Date();
        String[] info;
        System.out.println(date.toString());
        for (int i = 0; i < users.length; i++) {
            info = users[i].split(" ");
            if (!yourName.equals(info[0])) {
                System.out.printf("Number of player: %s; Name: %s;  Busy: %s; Wins: %s; Losses: %s", i, info[0], info[1], info[2], info[3]);
                System.out.println();
            }
        }
    }

    void transferFire(String fireCoordinates) throws IOException {
        fireCoordinates += String.format(" %s %s", opponentName, yourName);
        outputStream.writeObject(new LongMessage(fireCoordinates));
    }

    private String acceptResultOfYourFire(String[] response) throws IOException {
        String[] tempArray = new String[2];
        tempArray[0] = response[1];
        tempArray[1] = response[2];
        if (Boolean.valueOf(response[response.length - 1])) {
            controller.acceptResult(tempArray);
            return "You hit the enemy";
        } else {
            controller.acceptFalseResult(tempArray);
            return "You did not hit the enemy";
        }
    }

    private String acceptFire(String[] enemyFire) throws IOException {
        String[] tempArray = new String[2];
        tempArray[0] = enemyFire[1];
        tempArray[1] = enemyFire[2];
        if (Boolean.valueOf(enemyFire[enemyFire.length - 1])) {
            controller.getShot(tempArray);
            return "The enemy hit you";
        } else {
            controller.getShotPast(tempArray);
            return "The enemy did not hit you";
        }
    }

    void transferLoseMessage() throws IOException {
        outputStream.writeObject(new LongMessage("lose " + yourName));
    }

    void transferVictoryMessage() throws IOException {
        outputStream.writeObject(new LongMessage("victory " + yourName));
    }
}
