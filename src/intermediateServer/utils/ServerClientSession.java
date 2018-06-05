package intermediateServer.utils;

import generalClasses.ClientInfo;
import generalClasses.Message;
import intermediateServer.IntrServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerClientSession extends Thread {
    private final Socket socket;
    private String nickName;
    private Message message;

    public ServerClientSession(final Socket socket) {
        this.socket = socket;
        this.start();
    }

    @Override
    public void run() {
        try {
            final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            this.message = (Message) inputStream.readObject();
            this.nickName = this.message.getNickname();
            final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            IntrServer.getUserList().addUser(this.nickName, socket, outputStream, inputStream);
            this.message.setOnlineUsers(IntrServer.getUserList().getUsersName());

            this.broadcast(IntrServer.getUserList().getClientsList(), this.message);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(ArrayList<ClientInfo> clientsList, Message message) {
        try {
            for (ClientInfo client : clientsList) {
                client.getThisObjectOutputStream().writeObject(message);
            }
        } catch (SocketException e) {
            System.out.println("in broadcast: " + this.nickName + " disconnected!");
            IntrServer.getUserList().deleteUser(nickName);
            //this.broadcast(IntrServer.getUserList().getClientsList(), new Message( "The user " + nickName + " has been disconnected", IntrServer.getUserList().getUsersName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
