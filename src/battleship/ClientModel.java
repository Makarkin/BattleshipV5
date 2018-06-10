package battleship;

import generalClasses.LongMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class ClientModel extends Thread {

    private Controller controller;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String opponentName;

    ClientModel(Controller controller) throws IOException {
        InetAddress address = InetAddress.getByName(controller.getIntrServerAddress());
        this.socket = new Socket(address, controller.getPort());
        this.controller = controller;
    }

    @Override
    public void run() {
        Controller.yourTurn = true;
        boolean flag = true;
        try {
            String[] users;
            LongMessage longMessage = new LongMessage(controller.getMainView().getYouLabel().getText(), controller.getYourBoard());
            outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            outputStream.writeObject(longMessage);

            inputStream = new ObjectInputStream(this.socket.getInputStream());
            while (flag) {
                LongMessage mes = (LongMessage) inputStream.readObject();
                users = mes.getOnlineUsers();
                ChooseUser chooseUser = new ChooseUser(users, controller, outputStream);
                if (users != null) {
                    showUser(users);
                    chooseUser.start();
                } else {
                    String[] response = mes.getReport().split(" ");
                    String element = response[0];
                    if ("y".equals(element)) {
                        opponentName = response[1];
                        controller.getMainView().getEnemyLabel().setText(opponentName);
                    } else if ("n".equals(element)) {
                        return;
                    } else if ("enemyResult".equals(element)) {
                        acceptFire(response);
                    } else if ("yourResult".equals(element)) {
                        acceptResultOfYourFire(response);
                    } else if ("Do".equals(element)) {
                        acceptOrRejectPlayer(response);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void acceptOrRejectPlayer(String[] response) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("Do you want to play with %s y/n", response[response.length - 1]);
        System.out.println();
        String s = "y"/*scanner.nextLine()*/;
        if ("y".equals(s)) {
            opponentName = response[response.length - 1];
            outputStream.writeObject(new LongMessage("y"));
        } else {
            outputStream.writeObject(new LongMessage("n"));
            return;
        }
    }

    private void showUser(String[] users) {
        Date date = new Date();

        String[] info;
        System.out.println(date.toString());
        for (int i = 0; i < users.length; i++) {
            info = users[i].split(" ");
            if (!controller.getMainView().getYouLabel().getText().equals(info[0])) {
                System.out.println("Number of player: " + i + "; Name: " + info[0] + "; Is busy: " + info[1]);
            }
        }
    }

    void transferFire(String fireCoordinates) throws IOException {
        fireCoordinates += String.format(" %s %s", opponentName, controller.getMainView().getYouLabel().getText());
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        outputStream.writeObject(fireCoordinates);
        outputStream.flush();
    }

    private void acceptResultOfYourFire(String[] response) {
        String[] tempArray = new String[2];
        tempArray[0] = response[1];
        tempArray[1] = response[2];
        if (Boolean.valueOf(response[response.length - 1])) {
            controller.acceptResult(tempArray);
        }
    }

    private void acceptFire(String[] enemyFire) {
        String[] tempArray = new String[2];
        tempArray[0] = enemyFire[1];
        tempArray[1] = enemyFire[2];
        if (Boolean.valueOf(enemyFire[enemyFire.length - 1])) {
            controller.getShot(tempArray);
        } else {
            controller.getShotPast(tempArray);
        }
    }

    void transferLoseMessage() {
    }

    void transferVictoryMessage() {
    }
}
