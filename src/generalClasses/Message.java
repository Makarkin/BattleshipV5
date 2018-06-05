package generalClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Message implements Serializable {
    private String nickname;
    private String[] users;

    public String[] getUsers() {
        return users;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Message(String nickname) {
        this.nickname = nickname;
    }

    public Message(String nickname, String[] users) {
        this.nickname = nickname;
        this.users = users;
    }

    public void setOnlineUsers(String[] onlineUsers) {
        this.users = onlineUsers;
    }
}
