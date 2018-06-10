package intermediateServer.utils;

import generalClasses.LongMessage;
import intermediateServer.Server;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class PoolRequestHandler extends Thread {

    private ArrayBlockingQueue<String> poolRequest;

    public PoolRequestHandler(ArrayBlockingQueue<String> requestList) {
        poolRequest = requestList;
    }

    @Override
    public void run() {
        System.out.println("requestHandler was started");
        while (true) {
            if (poolRequest.size() > 0) {
                String request = poolRequest.poll();
                System.out.println(request + " in handler");
                String[] requestBody = request.split(" ");
                if ("fire".equals(requestBody[0])) {
                    try {
                        fireMethod(requestBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if ("request".equals(requestBody[0])) {
                    try {
                        requestMethod(requestBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if ("y".equals(requestBody[0])) {
                    try {
                        responseMethod(requestBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if ("n".equals(requestBody[0])) {
                    try {
                        notResponseMethod(requestBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void notResponseMethod(String[] requestBody) throws IOException {
        Server.logger.info("Response is failure");
        String responseTo = requestBody[1];
        Server.getUserList().getUsers().get(responseTo).getThisObjectOutputStream().writeObject(new LongMessage("n"));
    }

    private void requestMethod(String[] requestBody) throws IOException, ClassNotFoundException {
        String requestTo = requestBody[1];
        String requestFrom = requestBody[2];
        String request = String.format("Do you want to play with %s", requestFrom);
        Server.getUserList().getUsers().get(requestTo).getThisObjectOutputStream().writeObject(new LongMessage(request));
        Server.logger.info("Request sent to the player " + requestTo + " from " + requestFrom);
    }

    private void responseMethod(String[] requestBody) throws IOException {
        String responseTo = requestBody[1];
        String responseFrom = requestBody[2];
        Server.logger.info("Received a response from " + responseFrom + " for " + responseTo);
        Server.logger.info("Response is consent");
        Server.getUserList().getUsers().get(responseFrom).setBusy(true);
        Server.getUserList().getUsers().get(responseTo).setBusy(true);
        Server.getUserList().getUsers().get(responseTo).getThisObjectOutputStream().writeObject(new LongMessage("y " + responseFrom));
    }

    private void fireMethod(String[] requestBody) throws IOException {
        int i = Integer.valueOf(requestBody[1]);
        int j = Integer.valueOf(requestBody[2]);
        String fireToName = requestBody[3];
        String fireFromName = requestBody[4];
        String resultForShooter;
        String resultForUnderFire;
        if (Server.getUserList().getUsers().get(fireToName).getBoard().getIndexCell(i, j).isWithShip()) {
            Server.logger.info("Fire from " + fireFromName + " to " + fireToName + " is successful");
            resultForShooter = String.format("yourResult %s %s true", i, j);
            resultForUnderFire = String.format("enemyResult %s %s true", i, j);
            Server.getUserList().getUsers().get(fireToName).getThisObjectOutputStream().writeObject(new LongMessage(resultForUnderFire));
            Server.getUserList().getUsers().get(fireFromName).getThisObjectOutputStream().writeObject(new LongMessage(resultForShooter));
            Server.logger.info("Result of fire sent to " + fireFromName + " and " + fireToName);
        } else {
            Server.logger.info("Fire from " + fireFromName + " to " + fireToName + " is a failure");
            resultForShooter = String.format("yourResult %s %s false", i, j);
            resultForUnderFire = String.format("enemyResult %s %s false", i, j);
            Server.getUserList().getUsers().get(fireToName).getThisObjectOutputStream().writeObject(new LongMessage(resultForUnderFire));
            Server.getUserList().getUsers().get(fireFromName).getThisObjectOutputStream().writeObject(new LongMessage(resultForShooter));
            Server.logger.info("Result of fire sent to " + fireFromName + " and " + fireToName);
        }
    }
}
