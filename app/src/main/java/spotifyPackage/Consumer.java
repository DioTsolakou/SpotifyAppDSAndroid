package spotifyPackage;

import com.mpatric.mp3agic.*;
import java.io.*;
import java.net.Socket;

public class Consumer extends Node{

    private String brokersDir = "spotifyPackage\\Brokers\\brokers.txt";
    private Request message;
    private String connectionStatus;
    private boolean hasChanged = true;
    String songName;

    private String currentSongArtist;
    private String currentSongGenre;
    private String currentSongAlbum;
    private String currentSongTitle;

    public Consumer(String songName) {
        this.ip = selectFirstBrokerIp(brokersDir);
        this.port = selectFirstBrokerPort(brokersDir);
        this.songName = songName;
        runClient();
    }

    private void runClient() {
        try {
            do {
                if (hasChanged) {
                    connectToServer();
                    getStreams();
                    requestSong(songName);
                    hasChanged = false;
                }
                processConnection();
            } while (!connectionStatus.equals("exit"));
        }
        catch (EOFException eofException) {
            //eofException.printStackTrace();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        finally {
            closeConnection();
        }
    }

    private void connectToServer() throws IOException {
        System.out.println("Attempting connection to " + ip + "::" + port);
        connection = new Socket(ip, port);
        if (connection.isConnected()) {
            System.out.println("Connected to " + ip + "::" + port);
        }
        else {
            System.out.println("Can't connect to " + ip + "::" + port);
        }
    }

    protected void processConnection() throws IOException {
        try {
            message = (Request) input.readObject();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (message.getHeader().equals("newConnection")) {
            sendData(new Request("newConnectionAck", ""));
            //closeConnection();
            String[] tempArray = ((String) message.getData()).split(",", 2);
            this.ip = tempArray[0];
            this.port = Integer.parseInt(tempArray[1]);
            hasChanged = true;
        }
        else if (message.getHeader().equals("error")) {
            System.out.println("Unknown error");
            connectionStatus = "exit";
        }
        else if (message.getHeader().equals("musicData 1")) {
            sendData(new Request("musicDataAck", ""));
            saveChunks(message);
            System.out.println("Music fragment received and stored");
        }
        else if (message.getHeader().equals("artistUnavailable")) {
            System.out.println("Sorry we can't find this song, try another one");
            connectionStatus = "exit";
        }
        else if (message.getHeader().equals("musicData 0")) {
            sendData(new Request("musicDataAck", ""));
            saveChunks(message);
            System.out.println("Music fragment received and stored");
            connectionStatus = "exit";
        }
        connectionStatus = "continue";
    }

    private void requestSong(String songName) {
        sendData(new Request("songPull", songName));
    }

    int counter = 0;
    private void saveChunks(Request chunk) {

        storeMetaData((MusicFile) chunk.getData());
        counter++;
        File f = new File("spotifyPackage\\Consumer\\" +currentSongTitle + counter + ".mp3");

        try (OutputStream fos = new FileOutputStream(f)) {
            fos.write( ((MusicFile)chunk.getData()).getMusic() );
            fos.close();
            System.out.println("Music fragment received and stored");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        setMetaData(f);
    }

    private void storeMetaData(MusicFile song){
        currentSongTitle = song.getTitle();
        currentSongArtist = song.getArtistName();
        currentSongAlbum = song.getAlbumInfo();
        currentSongGenre = song.getGenre();
    }

    private void setMetaData(File f){
        try {
            ID3v2 id3v2Tag = (new Mp3File(f)).getId3v2Tag();
            id3v2Tag.setArtist(currentSongArtist);
            id3v2Tag.setAlbum(currentSongAlbum);
            id3v2Tag.setYear(currentSongGenre);
            id3v2Tag.setTitle(currentSongTitle);
        }
        catch(Exception e){
            //e.printStackTrace();
        }
    }

    private String selectFirstBrokerIp(String file) {
        String[] brokerArray = new String[2];
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            line = br.readLine();
            brokerArray = line.split(" ", 2);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return brokerArray[0];
    }

    private int selectFirstBrokerPort(String file) {
        String[] brokerArray = new String[2];
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            line = br.readLine();
            brokerArray = line.split(" ", 2);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(brokerArray[1]);
    }
}