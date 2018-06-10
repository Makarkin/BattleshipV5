package intermediateServer;

import generalClasses.LongMessage;
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

    public synchronized static BlockingQueue<String> getRequestList() {
        return requestList;
    }

    public synchronized static UsersList getUserList() {
        return usersList;
    }

    Server(int port) {
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
                /*latch.await();*/
                System.out.println("requestListSize " + requestList.size());
                if (requestList.size() > 0) {
                    String request = requestList.poll();
                    String[] requestBody = request.split(" ");
                    if ("fire".equals(requestBody[0])) {
                        fireMethod(requestBody);
                    } else if ("request".equals(requestBody[0])) {
                        requestMethod(requestBody);
                    }
                }
            }
        } catch (IOException /*| InterruptedException */| ClassNotFoundException e) {
            e.printStackTrace();
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        }*/
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
            usersList.getUsers().get(requestFrom).getThisObjectOutputStream().writeObject(new LongMessage("y " + requestTo));
        } else {
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
            resultForShooter = String.format("yourResult %s %s true", i, j);
            resultForUnderFire = String.format("enemyResult %s %s true", i, j);
            usersList.getUsers().get(fireToName).getThisObjectOutputStream().writeObject(new LongMessage(resultForUnderFire));
            usersList.getUsers().get(fireFromName).getThisObjectOutputStream().writeObject(new LongMessage(resultForShooter));
        } else {
            resultForShooter = String.format("yourResult %s %s false", i, j);
            resultForUnderFire = String.format("enemyResult %s %s false", i, j);
            usersList.getUsers().get(fireToName).getThisObjectOutputStream().writeObject(new LongMessage(resultForUnderFire));
            usersList.getUsers().get(fireFromName).getThisObjectOutputStream().writeObject(new LongMessage(resultForShooter));
        }
    }
}
