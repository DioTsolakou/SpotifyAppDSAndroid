package spotifyPackage.Utilities;

import android.media.MediaPlayer;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

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

    public static void joinChunks(String songName)
    {
        String dir = "spotifyPackage\\Consumer\\";
        File[] listOfFiles = new File(dir).listFiles();
        int chunkNumber;
        chunks.clear();

        for (File f : listOfFiles) {
            if (f.getName().contains(songName))
            {
                chunks.add(f);
            }
        }

        Collections.sort(chunks, new Comparator<String>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().substring(songName.length() - 1, o1.getName().lastIndexOf('.').compareTo(o2.getName().substring(songName.length() - 1, o2.getName().lastIndexOf('.'))));
            }
        });
    }

    public static int getChunkNumber(File file)
    {
        int chunkNumber = Integer.parseInt(file.getName().substring(songName.length() - 1, file.getName().lastIndexOf('.')));
        return chunkNumber;
    }

    public void playChunks(String songName)
    {
        MediaPlayer mediaPlayer = new MediaPlayer();

        int chunkCounter = findChunks(songName);

        for (int i = 0; i < chunkCounter; i++)
        {
            try {
                mediaPlayer.setDataSource("spotifyPackage\\Consumer\\" + songName + i + ".mp3");
                mediaPlayer.start();
                wait(mediaPlayer.getDuration());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}