package spotifyPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Node {

    protected Socket connection;
    protected ObjectOutputStream output;
    protected ObjectInputStream input;

    protected String ip;
    protected int port;

    public Node(){
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Node(int port){
        this();
        this.port = port;
    }

    protected void processConnection() throws IOException{}

    protected void getStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
    }

    protected void sendData(Request message) {
        try {
            output.writeObject(message);
            output.flush();
        }
        catch (IOException ioException) {
            System.out.println("\nError writing object");
            closeConnection();
        }
    }

    protected void closeConnection() {
        System.out.println("Closing connection");
        try {
            output.close();
            input.close();
            connection.close();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
