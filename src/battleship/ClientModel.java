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
                users = mes.getOnlineUsers();//если юзеров нет - то это ответ на запрос.
                if (users.length > 0) {
                    showUser(users);
                    String request = new String();
                    chooseUser(users, request);
                    if (request != null) {
                        outputStream.writeObject(new LongMessage(request));
                    }
                } else {
                    opponentName = mes.getResponse();
                    controller.getMainView().getEnemyLabel().setText(opponentName);
                    //решение под ответ на реквест
                }
            }

            outputStream.reset();
            inputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void chooseUser(String[] users, String request) {
        System.out.println("Choose number of your opponent. For refresh opponents list enter \"r\" ");
        Scanner scanner = new Scanner(System.in);
        int index;
        String s = scanner.nextLine().toLowerCase();
        if ("r".equals(s)) {
            return;
        } else {
            index = Integer.valueOf(s);
            String[] result = users[index].split(" ");
            if (!Boolean.valueOf(result[1])) {
                 request = "request " + result[0];
            } else {
                return;
            }
        }
    }

    void transferFire(String fireCoordinates) throws IOException {
        fireCoordinates = fireCoordinates + " " + opponentName;
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        outputStream.writeObject(fireCoordinates);
        outputStream.flush();
        outputStream.reset();
    }

    Boolean acceptResultOfYourFire() throws IOException, ClassNotFoundException {
        inputStream = new ObjectInputStream(this.socket.getInputStream());
        Boolean result = (Boolean) inputStream.readObject();
        inputStream.reset();
        return result;
    }

    private void showUser(String[] users) {
        Date date = new Date();
        int i = 0;
        String[] info;
        System.out.println(date.toString());
        for (String user : users) {
            info = user.split(" ");
            if (!controller.getMainView().getYouLabel().getText().equals(info[0])) {
                System.out.println("Number of player: " + i + "; Name: " + info[0] + "; Is busy: " + info[1]);
            }
        }
    }

    void transferVictoryMessage() {
    }

    int[] acceptFire() throws IOException, ClassNotFoundException {
        inputStream = new ObjectInputStream(this.socket.getInputStream());
        int[] result = (int[]) inputStream.readObject();
        inputStream.reset();
        return result;
    }

    void transferResultOfEnemyFire(String result) throws IOException {
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        outputStream.writeObject(result);
        outputStream.flush();
        outputStream.reset();
    }
}
