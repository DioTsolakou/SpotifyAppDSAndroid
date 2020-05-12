package spotifyPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;

public class ServerNode extends Node {

    protected ServerSocket server;
    protected int counter;

    protected ServerNode(){
        super();
    }

    protected ServerNode(int port){
        super(port);
        counter = 0;
    }

    protected void hostServer() {
        try {
            System.out.println("Creating new server socket at port " + port);
            server = new ServerSocket(port);
            while (true) {
                System.out.println("Waiting for new connection at " + ip + "::" + port + " ...");
                connection = server.accept();
                if (connection.isConnected()) {
                    try{
                        handleServerConnection();
                    }
                    catch(IOException e){}
                    closeConnection();
                } else System.out.println("Connection denied from " + connection.getInetAddress().toString());
            }
        } catch (IOException ioE) {
            ioE.printStackTrace();
        }
    }

    protected void handleServerConnection() throws IOException{}
}
