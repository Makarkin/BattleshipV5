package generalClasses;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ClientInfo {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ClientInfo(Socket socket){
        this.socket = socket;
    }

    public ClientInfo(Socket socket , ObjectOutputStream outputStream , ObjectInputStream inputStream) {
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
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
