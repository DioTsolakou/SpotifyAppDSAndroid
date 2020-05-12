package spotifyPackage;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.util.*;
import java.net.Socket;

public class BrokerRunnable extends BrokerImplementation implements Runnable {

    public BrokerRunnable(int port, int counter, ArrayList<String> brokers, ArrayList<String> publishers) {
        super();
        this.port = port;
        this.counter = counter;

        this.registeredBrokers = brokers;
        this.registeredPublishers = publishers;
    }

    @Override
    public void run() {
        try {
            int temp = this.port + this.counter;
            server = new ServerSocket(temp);
            System.out.println("BrokerRunn " + this.counter + " Waiting for new connection at " + ip + "::" + temp + " ...");
            connection = server.accept();
            System.out.println("BrokerRunn " + this.counter + " handling connection from " + connection.getInetAddress().toString() + "::" + server.getLocalPort());
            getStreams();
            processConnection();
        }
        catch (IOException ioException) {
            sendData(new Request("error", "\nSomething went wrong\n"));
        }
        finally {
            closeConnection();
        }
    }

    protected void processConnection() throws IOException {
        Request message;
        try {
            message = (Request) input.readObject();
            if (message.getHeader().equals("songPull")) {
                handleConsumer(message);
            }
        } catch (ClassNotFoundException e){}
    }

    private void handleConsumer(Request r) {
        String[] consReqDataSplit = ((String) r.getData()).split(",", 2);
        for (int i = 0; i < consReqDataSplit.length; i++) consReqDataSplit[i] = consReqDataSplit[i].trim();
        if (!hashCheck(ip + port, consReqDataSplit[0])) {
            boolean found = false;
            synchronized (registeredBrokers) {
                for (String rb : registeredBrokers) {
                    if (rb.contains(ip)) continue;
                    String[] rbSplit = rb.split(" ", 3);
                    for (int i = 0; i < rbSplit.length; i++) rbSplit[i] = rbSplit[i].trim();
                    if (hashCheck(rbSplit[1] + rbSplit[2], consReqDataSplit[0])) {
                        sendData(new Request("newConnection", rbSplit[1] + "," + rbSplit[2]));
                        found = true;
                        break;
                    }
                }
                if(!found) sendData(new Request("artistUnavailable", ""));
            }
        }
        else
        {
            boolean found = false;
            synchronized(registeredPublishers)
            {
                for (String rp : registeredPublishers)
                {
                    String[] rpSplit = rp.split(" ", 3);
                    if (Character.toUpperCase(consReqDataSplit[0].charAt(0)) <= Character.toUpperCase(rpSplit[0].charAt(0)))
                    {
                        found = true;
                        pull(consReqDataSplit[0], consReqDataSplit[1], rpSplit[1], rpSplit[2]);
                    }
                }
            }
            if(!found)
            {
                sendData(new Request("artistUnavailable", r.getData()));
            }
        }
    }

    private boolean hashCheck(String s1, String s2) {
        MD5hash hasher = new MD5hash();
        BigInteger hash1 = hasher.getMd5(s1);
        BigInteger hash2 = hasher.getMd5(s2);
        String lastRB;

        synchronized(registeredBrokers)
        {
            lastRB = registeredBrokers.get(registeredBrokers.size()-1);
        }
        String lastRBhash = lastRB.substring(0, lastRB.indexOf(" "));

        return hash1.compareTo(hash2.mod(new BigInteger(lastRBhash))) < 0;
    }

    private void pull(String artistName, String song, String ip, String port)
    {
        Socket pullConnection = null;
        ObjectOutputStream pullOutput = null;
        ObjectInputStream pullInput = null;

        try
        {
            pullConnection = new Socket(ip, Integer.parseInt(port));
            pullOutput = new ObjectOutputStream(pullConnection.getOutputStream());
            pullOutput.flush();
            pullInput = new ObjectInputStream(pullConnection.getInputStream());
            pullOutput.writeObject(new Request("songPull", artistName + "," + song));
            pullOutput.flush();

            Request check = null;
            while (true)
            {
                check = (Request) pullInput.readObject();
                if(check.getHeader().startsWith("musicData"))   //make sure it's the correct type of request
                {
                    sendData(check);
                    if(check.getHeader().endsWith("0")) break;  //leave if no more fragments
                    check = (Request) input.readObject();   //wait for acknowledgement from consumer or a new songPull request
                    if(!(check.getHeader().equals("musicDataAck"))) break;  //if new songPull request stop sending current song
                }
                if (check.getHeader().equals("newConnection"))
                {
                    pullOutput.writeObject(new Request("newConnectionAck", ""));
                    pullOutput.flush();
                    String[] tempArray = ((String) check.getData()).split(",", 2);
                    pull(artistName, song, tempArray[0], tempArray[1]);
                }
            }
            if(check.getHeader().equals("songPull"))
            {
                try{
                    pullOutput.close();
                    pullInput.close();
                    pullConnection.close();
                }
                catch(IOException ioException){
                    ioException.printStackTrace();
                }
                handleConsumer(check);
            }
        }
        catch (Exception E)
        {
            sendData(new Request("error", "\nSomething went wrong\n"));
            try{
                pullOutput.close();
                pullInput.close();
                pullConnection.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }
}