package spotifyPackage;

import java.io.*;
import java.net.Socket;

public class PublisherImplementation extends ServerNode {

    private char lastLetter;
    protected String dir;

    public PublisherImplementation() {
    }

    public PublisherImplementation(int port, String dir) {
        super(port);
        this.dir = dir;
        this.lastLetter = this.dir.charAt(this.dir.length() - 1);

        try {
            BufferedReader br = new BufferedReader(new FileReader("spotifyPackage\\Brokers\\brokers.txt"));
            String line;
            String[] tempArr;

            while ((line = br.readLine()) != null) {
                tempArr = line.split(" ", 2);
                Socket helloSocket = new Socket(tempArr[0], Integer.parseInt(tempArr[1]));
                ObjectOutputStream helloOut = new ObjectOutputStream(helloSocket.getOutputStream());
                helloOut.writeObject(new Request("publisherHello", lastLetter + "," + ip + "," + port));
                helloOut.close();
                helloSocket.close();
            }
            this.hostServer();
        }
        catch (IOException ioE) {
            ioE.printStackTrace();
        }
    }

    protected void handleServerConnection() throws IOException {
        System.out.println("Connection accepted from " + connection.getInetAddress().toString());
        counter++;
        int temp = this.port + counter;
        Thread t = new Thread(new PublisherRunnable(temp, this.dir));
        t.start();
        getStreams();
        sendData(new Request("newConnection", this.ip + "," + temp));
        Request check;
        try {
            do {
                check = (Request) input.readObject();
            } while (!check.getHeader().equals("newConnectionAck"));
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}