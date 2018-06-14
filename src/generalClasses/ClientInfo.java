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
    private int wins = 0;
    private int losses = 0;

    public ClientInfo(Socket socket , ObjectOutputStream outputStream , ObjectInputStream inputStream, Board board) {
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.board = board;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
    }

    public Board getBoard() {

        return board;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public Boolean getBusy() {
        return isBusy;
    }

    public ClientInfo(Socket socket){
        this.socket = socket;
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
