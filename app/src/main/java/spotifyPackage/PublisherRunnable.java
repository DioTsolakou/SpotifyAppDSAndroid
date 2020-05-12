package spotifyPackage;

import com.mpatric.mp3agic.*;
import java.io.*;
import java.net.ServerSocket;

public class PublisherRunnable extends PublisherImplementation implements Runnable {

    PublisherRunnable(int port, String dir) {
        this.port = port;
        this.dir = dir;
    }

    @Override
    public void run() {
        System.out.println(dir);
        try {
            server = new ServerSocket(port, 1000);
            System.out.println("Waiting for new connection at " + ip + "::" + port + " ...");
            connection = server.accept();
            System.out.println("Runnable handling connection from " + connection.getInetAddress().toString() + "::" + server.getLocalPort());
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
        try{
            Request r = (Request) input.readObject();
            if(r.getHeader().equals("songPull")){
                String inputString = (r.getData().toString());
                File f = findSong(inputString.substring(inputString.indexOf(',')+1));
                System.out.println(dir + "\\" + f.getName());
                Mp3File mp3file = new Mp3File(dir + "\\" + f.getName());
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    sendChunks(id3v2Tag.getTitle(), id3v2Tag.getArtist(), id3v2Tag.getAlbum(), id3v2Tag.getGenreDescription(), fileToByteArray(f));
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private File findSong(String song) {
        File[] listOfFiles = (new File(dir)).listFiles();
        for (File f: listOfFiles) {
            if (f.getName().contains(song)) return f;
        }
        return null;
    }

    private byte[] fileToByteArray(File f) {
        byte[] bytesArray = new byte[(int) f.length()];

        try {
            FileInputStream fis = new FileInputStream(f);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
            return bytesArray;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendChunks (String title, String artist, String album, String genre, byte[] fileBytes){
        byte[] outputBytes;
        char c = '1';

        System.out.println("Sending chunks ...");
        for (int offset = 0; offset < fileBytes.length; offset += 512000) {
            int len = (offset + 512000 < fileBytes.length ? 512000 : fileBytes.length - offset);
            outputBytes = new byte[len];
            System.arraycopy(fileBytes, offset, outputBytes, 0, len);
            if (offset + 512000 >= fileBytes.length) c = '0';
            MusicFile mf =  new MusicFile(title, artist, album, genre, outputBytes);
            sendData (new Request("musicData " + c, mf ));
        }
    }
}