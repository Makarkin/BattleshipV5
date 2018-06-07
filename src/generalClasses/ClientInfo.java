package generalClasses;

import battleship.utils.Board;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ClientInfo implements Serializable {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Board board;
    private Boolean isBusy = false;

    public Boolean getBusy() {
        return isBusy;
    }

    public ClientInfo(Socket socket){
        this.socket = socket;
    }

    public ClientInfo(Socket socket , ObjectOutputStream outputStream , ObjectInputStream inputStream, Board board) {
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.board = board;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public ObjectOutputStream getThisObjectOutputStream() {
        return this.outputStream;
    }

    public ObjectInputStream getThisObjectInputStream() {
        return this.inputStream;
    }

    public void setThisObjectOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setThisObjectInputStream(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }
}
