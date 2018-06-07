package intermediateServer;

import intermediateServer.utils.ServerClientSession;
import intermediateServer.utils.UsersList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Server extends Thread {

    private int port;

    private static UsersList usersList = new UsersList();
    private static BlockingQueue<String> requestList = new ArrayBlockingQueue<>(10);
//написать обработчик запросов
    public synchronized static BlockingQueue<String> getRequestList() {
        return requestList;
    }

    public synchronized static UsersList getUserList() {
        return usersList;
    }

    public Server(int port) {
        this.port = port;
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
                for (int i = 0; i < requestList.size(); i++) {
                    //написать обработчик запросов по ключевым словам
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
