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
    private Board board;
    private LongMessage longMessage;
    private String request;
    private Boolean flag = true;

    public ServerClientSession(final Socket socket) {
        this.socket = socket;
        this.start();
    }

    @Override
    public void run() {
        try {
            final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            this.longMessage = (LongMessage) inputStream.readObject();
            this.nickName = this.longMessage.getNickname();
            this.board = this.longMessage.getBoard();

            final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            Server.getUserList().addUser(nickName, socket, outputStream, inputStream, board);
            this.longMessage.setOnlineUsers(Server.getUserList().getUsersName());
            this.broadcast(Server.getUserList().getClientsList(), this.longMessage);

            /*latch.countDown();*/

            while (flag) {
                request = ((LongMessage) inputStream.readObject()).getReport();
                if (request != "") {
                    Server.getRequestList().add(this.request);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
            Server.getUserList().deleteUser(nickName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
