package intermediateServer.utils;

import battleship.utils.Board;
import generalClasses.ClientInfo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UsersList {

    private Map<String, ClientInfo> onlineUsers = new HashMap<String, ClientInfo>();

    public void addUser(String nickname, Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream, Board board) {
        System.out.println(nickname + " connected");

        if (!this.onlineUsers.containsKey(nickname)) {
            this.onlineUsers.put(nickname, new ClientInfo(socket, outputStream, inputStream, board));
        } /*else {
            int i = 1;
            while (this.onlineUsers.containsKey(nickname)) {
                nickname = nickname + i;
                i++;
            }
            this.onlineUsers.put(nickname, new ClientInfo(socket, outputStream, inputStream, board));
        }*/
    }

    public void deleteUser(String nickname) {
        this.onlineUsers.remove(nickname);
    }

    public Map<String, ClientInfo> getUsers() {
        return this.onlineUsers;
    }

    public String[] getUsersName() {
        ArrayList<String> arrayList = new ArrayList<>();
        String result = new String();
        Set<String> set = onlineUsers.keySet();
        for (String s : set) {
            result = s + " " + onlineUsers.get(s).getBusy();
            arrayList.add(result);
        }

        String[] resultArray = new String[arrayList.size()];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = arrayList.get(i);
        }

        return resultArray;
    }

    public ArrayList<ClientInfo> getClientsList() {
        ArrayList<ClientInfo> clientsList = new ArrayList<ClientInfo>(this.onlineUsers.entrySet().size());
        String s = "";
        for (Map.Entry<String, ClientInfo> m : this.onlineUsers.entrySet()) {
            clientsList.add(m.getValue());
            s = s + m.getKey();
        }

        return clientsList;
    }
}

