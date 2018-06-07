package battleship;

import battleship.utils.Board;
import generalClasses.Message;

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
            Message message = new Message(controller.getMainView().getYouLabel().getText(), controller.getYourBoard());
            outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            outputStream.writeObject(message);

            inputStream = new ObjectInputStream(this.socket.getInputStream());

                Message mes = (Message) inputStream.readObject();
                String[] users = mes.getOnlineUsers();
                showUsers(users);
                sendRequestToPlayer(users);

                outputStream.reset();
                inputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void transferFire(String fireCoordinates) throws IOException {
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

    private void sendRequestToPlayer(String[] users) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose number of opponent");
        int opponentIndex = Integer.parseInt(scanner.nextLine());
        String[] opponentInfo = users[opponentIndex].split(" ");
    }

    private void showUsers(String[] users) {
        Date date = new Date();
        int i = 0;
        String[] info = new String[2];
        System.out.println(date.toString());
        for (String user : users) {
            info = user.split(" ");
            System.out.println("Number of player: " + i + "; Name: " + info[0] +"; Is busy: " + info[1]);
        }
    }

    public void transferVictoryMessage() {
    }

    public int[] acceptFire() throws IOException, ClassNotFoundException {
        inputStream = new ObjectInputStream(this.socket.getInputStream());
        int[] result = (int[]) inputStream.readObject();
        inputStream.reset();
        return result;
    }

    public void transferResultOfEnemyFire(String result) throws IOException {
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        outputStream.writeObject(result);
        outputStream.flush();
        outputStream.reset();
    }
}
