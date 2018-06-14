package intermediateServer.utils;

import battleship.utils.Board;
import generalClasses.ClientInfo;
import generalClasses.LongMessage;
import intermediateServer.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerClientSession extends Thread {
    private final Socket socket;
    private String nickName;

    public ServerClientSession(final Socket socket) {
        this.socket = socket;
        this.start();
    }

    @Override
    public void run() {
        try {
            final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            LongMessage longMessage = (LongMessage) inputStream.readObject();
            this.nickName = longMessage.getNickname();
            Board board = longMessage.getBoard();

            final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            Server.getUserList().addUser(nickName, socket, outputStream, inputStream, board);
            longMessage.setOnlineUsers(Server.getUserList().getUsersName());
            this.broadcast(Server.getUserList().getClientsList(), longMessage);

            Boolean flag = true;
            while (true) {
                Object object = inputStream.readObject();
                System.out.println(object);
                longMessage = (LongMessage) object;
                String request = longMessage.getReport();
                System.out.println(request + " in client session");
                if (!"".equals(request)) {
                    Server.getRequestList().add(request);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(ArrayList<ClientInfo> clientsList, LongMessage longMessage) {
        try {
            for (ClientInfo client : clientsList) {
                client.getThisObjectOutputStream().writeObject(longMessage);
            }
        } catch (SocketException e) {
            System.out.println("in broadcast: " + this.nickName + " disconnected!");
           /* Server.getUserList().deleteUser(nickName);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
