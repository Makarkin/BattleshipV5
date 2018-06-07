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

    private Socket socket;
    private String nickname;
    private Board board;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public ClientModel(String inetAddressIntrServer, int portIntrServer, String nickname, Board board) throws IOException {
        InetAddress address = InetAddress.getByName(inetAddressIntrServer);
        this.socket = new Socket(address, portIntrServer);
        this.nickname = nickname;
        this.board = board;
    }

    @Override
    public void run() {
        Controller.yourTurn = true;
        boolean flag = true;
        try {
            Message message = new Message(this.nickname, this.board);
            outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            outputStream.writeObject(message);

            inputStream = new ObjectInputStream(this.socket.getInputStream());
            while (flag) {
                Message mes = (Message) inputStream.readObject();
                String[] users = (String[]) mes.getOnlineUsers();
                showUsers(users);
                sendRequestToPlayer(users);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
        System.out.println(date.toString());
        for (String user : users) {
            System.out.println(i + " " + user);
        }
    }

    public void runToPlayer () {

    }
}
