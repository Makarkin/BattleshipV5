package battleship;

import generalClasses.LongMessage;
import javafx.application.Platform;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientModel extends Thread {

    private Controller controller;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private String opponentName = null;
    private String yourName;
    private Timer timer;
    private int timeInterval;
    private Scanner scanner;

    ClientModel(Controller controller) throws IOException {
        InetAddress address = InetAddress.getByName(controller.getIntrServerAddress());
        this.socket = new Socket(address, controller.getPort());
        this.controller = controller;
        this.yourName = controller.getMainView().getYouLabel().getText();
        this.timeInterval = 120;
        scanner = new Scanner(System.in);
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
            while (true) {
                LongMessage message = (LongMessage) inputStream.readObject();
                users = message.getOnlineUsers();
                if (users != null && users.length > 1) {
                    Platform.runLater(() -> controller.getMainView().getGameMessage().setText("Search for players"));
                    showUser(users);
                    ChooseUser chooseUser = new ChooseUser(users, controller, outputStream);
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
                        Platform.runLater(() -> controller.getMainView().getGameMessage().setText("Game started. Enemy turn"));
                        timer = new Timer();
                        Countdown countdown = new Countdown();
                        timer.scheduleAtFixedRate(countdown, 1000, 1000);
                    } else if ("n".equals(element)) {
                        return;
                    } else if ("enemyResult".equals(element)) {
                        printWriter.printf("Enemy fire from %s accepted. %s", opponentName, acceptFire(response));
                        printWriter.println();
                    } else if ("yourResult".equals(element)) {
                        printWriter.printf("Your fire on %s. %s", opponentName, acceptResultOfYourFire(response));
                        printWriter.println();
                    } else if ("Do".equals(element)) {
                        acceptOrRejectPlayer(response, scanner);
                        printWriter.println("Consideration of the request from the player");
                        printWriter.println();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void acceptOrRejectPlayer(String[] response, Scanner scanner) throws IOException {
        System.out.printf("Do you want to play with %s y/n", response[response.length - 1]);
        System.out.println();
        String s = "y"/*scanner.nextLine()*/;
        if ("y".equals(s)) {
            controller.setYourTurn(true);
            opponentName = response[response.length - 1];
            Platform.runLater(() -> controller.getMainView().getEnemyLabel().setText(opponentName));
            Platform.runLater(() -> controller.getMainView().getGameMessage().setText("Game started. Your turn"));
            timer = new Timer();
            Countdown countdown = new Countdown();
            timer.scheduleAtFixedRate(countdown, 1000, 1000);
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

    class Countdown extends TimerTask {

        @Override
        public void run() {
            if (timeInterval == 0) {
                timer.cancel();
                try {
                    controller.loseTimeMethod();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            timeInterval--;
            String s = String.valueOf(timeInterval/6);
            Platform.runLater(() -> controller.getMainView().getHelp().setText(s + " minutes left"));
        }
    }
}
