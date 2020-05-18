package spotifyPackage.Utilities;

import android.media.MediaPlayer;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;


public class Utilities
{
    public static ArrayList<File> chunks = new ArrayList<>();

    public static ArrayList<String> findArtistsAll() {
        ArrayList<String> artists = new ArrayList<>();
        artists.addAll(findArtists("spotifyPackage\\Publisher\\A-K"));
        artists.addAll(findArtists("spotifyPackage\\Publisher\\L-Z"));
        return artists;
    }

    private static ArrayList<String> findArtists(String dir) {
        ArrayList<String> list = new ArrayList<>();
        File[] listOfFiles = (new File(dir).listFiles());
        for (File f: listOfFiles) {
            try {
                Mp3File mp3file = new Mp3File(dir + "\\" + f.getName());
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    String artist = id3v2Tag.getArtist();
                    if (artist != null && !list.contains(artist)) {
                        list.add(artist);
                    }
                }
            }
            catch(Exception e) {e.printStackTrace();}
        }
        return list;
    }

    public static ArrayList<String> findArtistSongs(String artistName) {
        ArrayList<String> songs = new ArrayList<>();
        String dir, artist, title;
        if (Character.toUpperCase(artistName.charAt(0)) <= 'K')
            dir = "spotifyPackage\\Publisher\\A-K";
        else dir = "spotifyPackage\\Publisher\\L-Z";

        File[] listOfFiles = (new File(dir).listFiles());
        for (File f : listOfFiles) {
            try {
                Mp3File mp3file = new Mp3File(dir + "\\" + f.getName());
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    artist = id3v2Tag.getArtist();
                    title = id3v2Tag.getTitle();
                    if (artist.equals(artistName) && title != null && !songs.contains(title)) {
                        songs.add(title);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return songs;
    }

    private static void joinChunks(String songName)
    {
        String dir = "spotifyPackage\\Consumer\\";
        File[] listOfFiles = new File(dir).listFiles();
        chunks.clear();

        int size = 0;
        for (File f : listOfFiles) {
            if (f.getName().contains(songName))
            {
                chunks.add(f);
                size += f.length();
            }
        }

        Collections.sort(chunks, new ChunkComparator());
        FileInputStream fis;
        int offset = 0;
        byte[] bytes = new byte[size];
        for (File f: chunks){
            try {
                fis = new FileInputStream(f);
                fis.read(bytes, offset, (int)f.length());
                offset += f.length();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try (OutputStream fos = new FileOutputStream("spotifyPackage\\Consumer\\" + songName + "_final.mp3")) {
            fos.write(bytes);
            fos.close();
            System.out.println("Music fragment received and stored");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playSong(String songName)
    {
        joinChunks(songName);
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource("spotifyPackage\\Consumer\\" + songName + "_final.mp3");
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}