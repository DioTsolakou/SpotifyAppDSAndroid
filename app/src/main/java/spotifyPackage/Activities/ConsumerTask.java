package spotifyPackage.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import spotifyPackage.Consumer;
import spotifyPackage.Request;
import spotifyPackage.Utilities.Utilities;

public class ConsumerTask extends AsyncTask<Object, Void, Integer> {
    Context context;
    String artist;
    String title;
    String path;

    public ConsumerTask(Context c) {
        context = c;
    }

    @Override
    protected Integer doInBackground(Object... args) {
        artist = (String)args[0];
        title = (String)args[1];
        path = (String)args[2];

        String ip = "10.0.2.2";
        int port = 9999;
        Consumer c = new Consumer(artist + "," + title, path);

        try {
            Socket connection = new Socket(ip, port);
            ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
            output.writeObject(new Request("songPull", artist + "," + title));
            output.flush();

            while (true) {
                Request message = (Request) input.readObject();
                if (message.getHeader().equals("newConnection")) {
                    output.writeObject(new Request("newConnectionAck", ""));
                    output.close();
                    input.close();
                    connection.close();
                    String[] tempArray = ((String) message.getData()).split(",", 2);
                    ip = tempArray[0];
                    port = Integer.parseInt(tempArray[1]);
                    connection = new Socket(ip, port);
                    output = new ObjectOutputStream(connection.getOutputStream());
                    output.flush();
                    input = new ObjectInputStream(connection.getInputStream());
                    output.writeObject(new Request("songPull", artist + "," + title));
                    output.flush();
                } else if (message.getHeader().contains("musicData")) {
                    output.writeObject(new Request("songPull", artist + "," + title));
                    output.flush();
                    c.saveChunks(message);
                    if (message.getHeader().contains("0")) break;
                } else if (message.getHeader().equals("artistUnavailable")) {
                    return -1;
                } else if (message.getHeader().equals("error")) {
                    return -1;
                }
            }
            Utilities.joinChunks(artist + "@" + title);
            output.close();
            input.close();
            connection.close();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onProgressUpdate(Void... progress) {}

    @Override
    protected void onPostExecute(Integer result) {
        System.out.println("reached onpostexecute");
        if (result == 0) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Artist_Name", artist);
            intent.putExtra("Song_Name", title);
            intent.putExtra("Path", path);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Error: Song not available!", Toast.LENGTH_SHORT).show();
        }
    }
}
