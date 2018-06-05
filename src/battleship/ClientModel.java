package battleship;

import generalClasses.ClientInfo;
import generalClasses.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class ClientModel extends Thread {

    private Socket socket;
    private String nickname;
    private ServerSocket playerAsServer;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public ClientModel(String inetAddressIntrServer, int portIntrServer, String nickname) throws IOException {
        InetAddress address = InetAddress.getByName(inetAddressIntrServer);
        this.socket = new Socket(address, portIntrServer);
        this.nickname = nickname;
    }

    @Override
    public void run() {
        int i = 0;
        Controller.yourTurn = true;
        boolean flag = true;
        try {
            Message message = new Message(this.nickname);
            outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            outputStream.writeObject(message);

            inputStream = new ObjectInputStream(this.socket.getInputStream());
            while (flag) {
                Message mes = (Message) inputStream.readObject();
                String[] users = (String[]) mes.getUsers();
                showUsers(users);
                if (users != null && users.length > 1) {
                    flag = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showUsers(String[] users) {
        for (String user : users) {
            System.out.println(user);
        }
    }

    public void runToPlayer () {

    }
}
