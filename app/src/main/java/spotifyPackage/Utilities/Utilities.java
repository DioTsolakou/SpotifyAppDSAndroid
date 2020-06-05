package spotifyPackage.Utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Utilities {
    public static File streamingPath;
    public static File downloadPath;
    public static File path; //might not be needed, or might need some changes where it is used. Renamed it to use the other 2 paths
    public static ArrayList<File> chunks = new ArrayList<>();

    public static void joinChunks(String song) {
        String dir = streamingPath.getPath();
        File[] listOfFiles = new File(dir).listFiles();
        chunks.clear();

        int size = 0;
        for (File f : listOfFiles) {
            if (f.getName().equals(song + "_final.mp3")) return;
            if (f.getName().contains(song)) {
                chunks.add(f);
                size += f.length();
            }
        }

        Collections.sort(chunks, new ChunkComparator());
        FileInputStream fis;
        int offset = 0;
        byte[] bytes = new byte[size];
        for (File f : chunks) {
            try {
                fis = new FileInputStream(f);
                fis.read(bytes, offset, (int) f.length());
                offset += f.length();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String firstFile = streamingPath.getPath() + song + "_0.mp3";
        String finalFile = streamingPath.getPath() + song + "_final.mp3";
        try (OutputStream fos = new FileOutputStream(finalFile)) {
            fos.write(bytes);
            try {
                ID3v2 id3v2TagFirst = (new Mp3File(firstFile)).getId3v2Tag();
                ID3v2 id3v2TagFinal = (new Mp3File(finalFile)).getId3v2Tag();
                id3v2TagFinal.setArtist(id3v2TagFirst.getArtist());
                id3v2TagFinal.setAlbum(id3v2TagFirst.getAlbum());
                id3v2TagFinal.setYear(id3v2TagFirst.getYear());
                id3v2TagFinal.setTitle(id3v2TagFirst.getTitle());
            } catch (Exception e) {
                //e.printStackTrace();
            }
            //fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //deletes chunks after being used
        boolean b;
        for (File f : listOfFiles) {
            if (!f.getName().endsWith("_final.mp3")) {
                 b = f.delete();
                 if (b) System.out.println("file deleted!");
            }
        }
    }

    public static void playSong(MediaPlayer mp, String songName, boolean downloaded) {
        //joinChunks(songName);
        try {
            File path = downloaded ? downloadPath: streamingPath;
            mp.setDataSource(path.getPath() + songName + "_final.mp3");
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String toTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString;
        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        // return timer string
        return finalTimerString;
    }

    public static void createStorageDir(Context context, String name) {
        boolean success = true;

        if (name.equals("downloads")) {
            downloadPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + name);

            if (!downloadPath.exists()) {
                success = downloadPath.mkdirs();
            }
        }
        if (name.equals("streaming")) {
            streamingPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + name);

            if (!streamingPath.exists()) {
                success = streamingPath.mkdirs();
            }
        }

        if (!success) {
            Toast.makeText(context, "Unable to create " +name+ " folder!", Toast.LENGTH_SHORT).show();
        }
    }

    public static String moveFromStreamingToDownload(String songName) {
        File[] listOfFiles = streamingPath.listFiles();

        for (File f : listOfFiles) {
            if (f.getName().equals(songName + "_final.mp3")) {
                if (f.renameTo(new File(downloadPath + "\\" + songName + "_final.mp3"))) {
                    f.delete();
                    return "Download successful!";
                }
                else return "Download failed!";
            }
        }
        return "Download failed!";
    }

    public static String[] findDownloads() {
        ArrayList<String> songlist = new ArrayList<>();

        File[] listOfFiles = downloadPath.listFiles();
        if (listOfFiles.length == 0) {
            String[] temp = new String[1];
            temp[0] = "No songs in your library";
            return temp;
        }
        for (File f : listOfFiles) {
            String filename = f.getName().substring(0, f.getName().indexOf("_"));
            songlist.add(filename.replace("@", "-"));
        }

        String[] songarray = new String[songlist.size()];
        songlist.toArray(songarray);

        return songarray;
    }

        /*
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

     */

}