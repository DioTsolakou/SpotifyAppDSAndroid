package spotifyPackage;

import android.util.Log;

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
    //private String connectionStatus;
    //private boolean hasChanged = true;
    private String artistSong;

    private String artist;
    private String genre;
    private String album;
    private String title;
    private String path;

    private int counter = 0;

    public Consumer(String artistSong, String path) {
        String[] brokerDetails = selectFirstBrokerDetails(brokersDir);
        //this.ip = brokerDetails[0];
        //this.port = Integer.parseInt(brokerDetails[1]);this.ip = brokerDetails[0];
        this.ip = "127.0.0.1";
        this.port = 9999;
        this.artistSong = artistSong;
        this.path = path;
    }

    public int run() {
        try {
            connectToServer();
            getStreams();
            requestSong(artistSong);
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
                closeConnection();
                String[] tempArray = ((String) message.getData()).split(",", 2);
                this.ip = tempArray[0];
                this.port = Integer.parseInt(tempArray[1]);
                connectToServer();
                getStreams();
                requestSong(artistSong);
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
        Utilities.joinChunks(artist + "@" + title);
        return 0;
    }

    private void requestSong(String songName) {
        sendData(new Request("songPull", songName));
    }

    private void saveChunks(Request chunk) {
        if (counter == 0) storeMetaData((MusicFile) chunk.getData());
        counter++;

        File f = new File(this.path + artist + "@" + title + "_" + counter + ".mp3");
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

    private void storeMetaData(MusicFile song) {
        title = song.getTitle();
        artist = song.getArtistName();
        album = song.getAlbumInfo();
        genre = song.getGenre();
    }

    private void setMetaData(File f) {
        try {
            ID3v2 id3v2Tag = (new Mp3File(f)).getId3v2Tag();
            id3v2Tag.setArtist(artist);
            id3v2Tag.setAlbum(album);
            id3v2Tag.setYear(genre);
            id3v2Tag.setTitle(title);
        }
        catch(Exception e){
            //e.printStackTrace();
        }
    }

    private String[] selectFirstBrokerDetails(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            line = br.readLine();
            Log.d("debugline", line);
            String[] details = line.split(" ", 2);
            Log.d("debugip", details[0]);
            Log.d("debugport", details[1]);
            return details;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new String[2];
    }
    /*
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
    */
}