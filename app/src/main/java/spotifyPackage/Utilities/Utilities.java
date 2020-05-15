package spotifyPackage.Utilities;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.util.ArrayList;

public class Utilities
{
    public static ArrayList<String> findAllArtists() {
        ArrayList<String> artists = new ArrayList<>();
        artists.addAll(findArtists("spotifyPackage\\Publisher\\A-K"));
        artists.addAll(findArtists("spotifyPackage\\Publisher\\L-Z"));
        return artists;
    }

    static ArrayList<String> findArtists(String dir) {
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

    ArrayList<String> findArtistSongs(String artistName) {
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
}
