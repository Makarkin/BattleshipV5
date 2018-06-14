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

    void addUser(String nickname, Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream, Board board) {
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

    Map<String, ClientInfo> getUsers() {
        return this.onlineUsers;
    }

    void incrementClientWinsByName(String nickname) {
        int tempWins = onlineUsers.get(nickname).getWins() + 1;
        onlineUsers.get(nickname).setWins(tempWins);
    }

    void incrementClientLossesByName(String nickname) {
        int tempLosses = onlineUsers.get(nickname).getLosses() + 1;
        onlineUsers.get(nickname).setLosses(tempLosses);
    }

    String[] getUsersName() {
        ArrayList<String> arrayList = new ArrayList<>();
        String result;
        boolean busy;
        int wins ;
        int losses;
        Set<String> set = onlineUsers.keySet();
        for (String s : set) {
            busy = onlineUsers.get(s).getBusy();
            wins = onlineUsers.get(s).getWins();
            losses = onlineUsers.get(s).getLosses();
            result = String.format("%s %s %s %s", s, busy, wins, losses);
            arrayList.add(result);
        }

        String[] resultArray = new String[arrayList.size()];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = arrayList.get(i);
        }

        return resultArray;
    }

    ArrayList<ClientInfo> getClientsList() {
        ArrayList<ClientInfo> clientsList = new ArrayList<ClientInfo>(this.onlineUsers.entrySet().size());
        String s = "";
        for (Map.Entry<String, ClientInfo> m : this.onlineUsers.entrySet()) {
            clientsList.add(m.getValue());
            s = s + m.getKey();
        }

        return clientsList;
    }
}

