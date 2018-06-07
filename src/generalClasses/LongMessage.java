package generalClasses;

import battleship.utils.Board;

import java.io.Serializable;

public class LongMessage implements Serializable {
    private String nickname;
    private String[] onlineUsers;
    private Board board;
    private String response;

    public String getResponse() {
        return response;
    }

    public String[] getOnlineUsers() {
        return onlineUsers;
    }

    public String getNickname() {
        return nickname;
    }

    public Board getBoard() {
        return board;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LongMessage(String response) {
        this.response = response;
    }

    public LongMessage(String nickname, Board board) {
        this.nickname = nickname;
        this.board = board;
    }

    public void setOnlineUsers(String[] onlineUsers) {
        this.onlineUsers = onlineUsers;
    }
}
