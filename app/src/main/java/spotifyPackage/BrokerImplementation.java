package spotifyPackage;

import java.io.*;
import java.util.*;

public class BrokerImplementation extends ServerNode {


    protected ArrayList<String> registeredBrokers;
    protected ArrayList<String> registeredPublishers;

    public BrokerImplementation() {
        super();
        registeredPublishers = new ArrayList<String>();
    }

    public BrokerImplementation(String path){
        this();
        registerBrokers(path);
        hostServer();
    }

    protected void handleServerConnection() throws IOException {
        System.out.println("BrokerImpl Connection accepted from " + connection.getInetAddress().toString());
        getStreams();
        Request check;
        loop: while (true) {
            try {
                check = (Request) input.readObject();

                if (check.getHeader().equals("publisherHello")) {
                    synchronized (registeredPublishers) {
                        registeredPublishers.add(((String)check.getData()).replaceAll(",", " "));
                        Collections.sort(registeredPublishers);
                    }
                    break loop;
                }
                else if (check.getHeader().equals("songPull")) {
                    counter++;
                    Thread t = new Thread(new BrokerRunnable(port, counter, registeredBrokers, registeredPublishers));
                    t.start();
                    int temp = this.port + counter;
                    sendData(new Request("newConnection", this.ip + "," + temp));
                }
                else if (check.getHeader().equals("newConnectionAck")) {
                    break loop;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerBrokers(String file) {
        registeredBrokers = new ArrayList<String>();
        MD5hash hash = new MD5hash();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String temp;
            String[] tempArr;
            while ((line = br.readLine()) != null) {
                if (line.contains(ip))
                    this.port = Integer.parseInt(line.substring(line.indexOf(" ")).trim());
                temp = hash.getMd5(line.replaceAll(" ", "")) + " " + line;
                registeredBrokers.add(temp);
            }
            Collections.sort(registeredBrokers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}