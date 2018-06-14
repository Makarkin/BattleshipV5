package intermediateServer;

import intermediateServer.utils.PoolRequestHandler;
import intermediateServer.utils.ServerClientSession;
import intermediateServer.utils.UsersList;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Server extends Thread {

    private int port;

    public final static Logger logger = Logger.getLogger(Server.class);
    private static UsersList usersList = new UsersList();
    private static ArrayBlockingQueue<String> requestList = new ArrayBlockingQueue<>(10);

    public synchronized static BlockingQueue<String> getRequestList() {
        return requestList;
    }

    public synchronized static UsersList getUserList() {
        return usersList;
    }

    Server(int port) {
        this.port = port;
        PoolRequestHandler poolRequestHandler = new PoolRequestHandler(requestList);
        poolRequestHandler.start();
        logger.info("New Server create");
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            while (true) {
                System.out.println("Waiting connection on port:" + this.port);
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected to server");
                logger.info("New client connected to server");
                ServerClientSession clientSession = new ServerClientSession(clientSocket);
                logger.info("New client session is create");
            }
        } catch (IOException e) {
            logger.error("Exception", e);
            e.printStackTrace();
        }
    }
}