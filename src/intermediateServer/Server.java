package intermediateServer;

import generalClasses.LongMessage;
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

    PoolRequestHandler poolRequestHandler;

    private final static Logger logger = Logger.getLogger(Server.class);
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
        poolRequestHandler = new PoolRequestHandler(requestList);
        poolRequestHandler.start();
        logger.info("New Server create");
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
                logger.info("New client connected to server");
                ServerClientSession clientSession = new ServerClientSession(clientSocket);
                logger.info("New client session is create");
                System.out.println("requestListSize " + requestList.size());
                if (requestList.size() > 0) {
                    String request = requestList.poll();
                    String[] requestBody = request.split(" ");
                    if ("fire".equals(requestBody[0])) {
                        fireMethod(requestBody);
                        logger.info("Fire method started");
                    } else if ("request".equals(requestBody[0])) {
                        requestMethod(requestBody);
                        logger.info("Request method started");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Exception", e);
            e.printStackTrace();
        }
    }

    private void requestMethod(String[] requestBody) throws IOException, ClassNotFoundException {
        String requestTo = requestBody[1];
        String requestFrom = requestBody[2];
        String request = String.format("Do you want to play with %s?", requestFrom);
        usersList.getUsers().get(requestTo).getThisObjectOutputStream().writeObject(new LongMessage(request));
        logger.info("Request sent to the player " + requestTo + " from " + requestFrom);
        LongMessage response = (LongMessage) usersList.getUsers().get(requestTo).getThisObjectInputStream().readObject();
        logger.info("Received a response from " + requestFrom + " from " + requestTo);
        if ("y".equals(response.getReport())) {
            logger.info("Response is consent");
            usersList.getUsers().get(requestFrom).setBusy(true);
            usersList.getUsers().get(requestTo).setBusy(true);
            usersList.getUsers().get(requestFrom).getThisObjectOutputStream().writeObject(new LongMessage("y " + requestTo));
        } else {
            logger.info("Response is failure");
            usersList.getUsers().get(requestFrom).getThisObjectOutputStream().writeObject(new LongMessage("n"));
        }
    }

    private void fireMethod(String[] requestBody) throws IOException {
        int i = Integer.valueOf(requestBody[1]);
        int j = Integer.valueOf(requestBody[2]);
        String fireToName = requestBody[3];
        String fireFromName = requestBody[4];
        String resultForShooter;
        String resultForUnderFire;
        if (usersList.getUsers().get(fireToName).getBoard().getIndexCell(i, j).isWithShip()) {
            logger.info("Fire from " + fireFromName + " to " + fireToName + " is successful");
            resultForShooter = String.format("yourResult %s %s true", i, j);
            resultForUnderFire = String.format("enemyResult %s %s true", i, j);
            usersList.getUsers().get(fireToName).getThisObjectOutputStream().writeObject(new LongMessage(resultForUnderFire));
            usersList.getUsers().get(fireFromName).getThisObjectOutputStream().writeObject(new LongMessage(resultForShooter));
            logger.info("Result of fire sent to " + fireFromName + " and " + fireToName);
        } else {
            logger.info("Fire from " + fireFromName + " to " + fireToName + " is a failure");
            resultForShooter = String.format("yourResult %s %s false", i, j);
            resultForUnderFire = String.format("enemyResult %s %s false", i, j);
            usersList.getUsers().get(fireToName).getThisObjectOutputStream().writeObject(new LongMessage(resultForUnderFire));
            usersList.getUsers().get(fireFromName).getThisObjectOutputStream().writeObject(new LongMessage(resultForShooter));
            logger.info("Result of fire sent to " + fireFromName + " and " + fireToName);
        }
    }
}