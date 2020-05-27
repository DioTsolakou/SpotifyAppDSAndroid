package spotifyPackage;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import spotifyPackage.Utilities.Utilities;

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
    private String path;

    public Consumer(String songName, String path) {
        this.ip = selectFirstBrokerIp(brokersDir);
        this.port = selectFirstBrokerPort(brokersDir);
        this.songName = songName;
        this.path = path;

    }

    public int run() {
        try {
            connectToServer();
            getStreams();
            requestSong(songName);
            return process();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeConnection();
        }
        return -1;
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

    private int process() throws IOException {
        while (true) {
            try {
                message = (Request) input.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (message.getHeader().equals("newConnection")) {
                sendData(new Request("newConnectionAck", ""));
                //closeConnection();
                String[] tempArray = ((String) message.getData()).split(",", 2);
                this.ip = tempArray[0];
                this.port = Integer.parseInt(tempArray[1]);
                connectToServer();
                getStreams();
                requestSong(songName);
            } else if (message.getHeader().contains("musicData")) {
                sendData(new Request("musicDataAck", ""));
                saveChunks(message);
                if (message.getHeader().contains("0")) break;
            } else if (message.getHeader().equals("artistUnavailable")) {
                return -1;
            }
            else if (message.getHeader().equals("error")) {
                return -1;
            }
        }
        Utilities.joinChunks(currentSongTitle);
        return 0;
    }

    private void requestSong(String songName) {
        sendData(new Request("songPull", songName));
    }

    int counter = 0;

    private void saveChunks(Request chunk) {
        storeMetaData((MusicFile) chunk.getData());
        counter++;
        File f = new File(this.path + currentSongTitle + "_" + counter + ".mp3");

        try (OutputStream fos = new FileOutputStream(f)) {
            fos.write( ((MusicFile)chunk.getData()).getMusic() );
            fos.close();
            System.out.println("Music fragment received and stored");
        }
        catch (Exception e) {
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