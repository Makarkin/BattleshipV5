package intermediateServer;

import generalClasses.LongMessage;
import intermediateServer.utils.PoolRequestHandler;
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
    private static ArrayBlockingQueue<String> requestList = new ArrayBlockingQueue<>(10);

    private PoolRequestHandler requestHandler = null;

    public synchronized static BlockingQueue<String> getRequestList() {
        return requestList;
    }

    public synchronized static UsersList getUserList() {
        return usersList;
    }

    Server(int port) {
        this.port = port;
        requestHandler = new PoolRequestHandler(requestList);
        System.out.println("requestPool was created");
        requestHandler.start();
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
