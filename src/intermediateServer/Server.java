package intermediateServer;

import generalClasses.LongMessage;
import intermediateServer.utils.ServerClientSession;
import intermediateServer.utils.UsersList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

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
                CountDownLatch latch = new CountDownLatch(1);
                ServerClientSession clientSession = new ServerClientSession(clientSocket, latch);
                latch.await();

                for (String request : requestList) {
                    String[] requestBody = request.split(" ");
                    if ("fire".equals(requestBody[0])) {
                        fireMethod(requestBody);
                    } else if ("request".equals(requestBody[0])) {
                        requestMethod(requestBody);
                    }
                }
            }
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void requestMethod(String[] requestBody) throws IOException, ClassNotFoundException {
        String requestTo = requestBody[1];
        String requestFrom = requestBody[2];
        String request = String.format("Do you want to play with %s?", requestFrom);
        usersList.getUsers().get(requestTo).getThisObjectOutputStream().writeObject(new LongMessage(request));
        LongMessage response = (LongMessage) usersList.getUsers().get(requestTo).getThisObjectInputStream().readObject();
        if ("y".equals(response.getReport())) {
          usersList.getUsers().get(requestFrom).setBusy(true);
          usersList.getUsers().get(requestTo).setBusy(true);

        }
    }

    private void fireMethod(String[] requestBody) throws IOException {
        int i = Integer.valueOf(requestBody[1]);
        int j = Integer.valueOf(requestBody[2]);
        String fireToName = requestBody[3];
        String fireFromName = requestBody[4];
        String resultForShooter = new String();
        String resultForUnderFire = new String();
        if (usersList.getUsers().get(fireToName).getBoard().getIndexCell(i, j).isWithShip()) {
            resultForShooter = "yourResult true";
            resultForUnderFire = String.format("enemyResult %s %s true", i, j);
            usersList.getUsers().get(fireToName).getThisObjectOutputStream().writeObject(new LongMessage(resultForUnderFire));
            usersList.getUsers().get(fireFromName).getThisObjectOutputStream().writeObject(new LongMessage(resultForShooter));
        } else {
            resultForShooter = "yourResult false";
            resultForUnderFire = String.format("enemyResult %s %s false", i, j);
            usersList.getUsers().get(fireToName).getThisObjectOutputStream().writeObject(new LongMessage(resultForUnderFire));
            usersList.getUsers().get(fireFromName).getThisObjectOutputStream().writeObject(new LongMessage(resultForShooter));
        }
    }
}
