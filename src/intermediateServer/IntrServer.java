package intermediateServer;

import intermediateServer.utils.ServerClientSession;
import intermediateServer.utils.UsersList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class IntrServer extends Thread {

    private int port;

    private static UsersList usersList = new UsersList();


    public IntrServer(int port) {
        this.port = port;
    }

    public synchronized static UsersList getUserList() {
        return usersList;
    }

    @Override
    public void run() {
        boolean flag = true;
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            while (flag) {
                System.out.println("Waiting connection on port:" + this.port);
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected to server");
                ServerClientSession clientSession = new ServerClientSession(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
