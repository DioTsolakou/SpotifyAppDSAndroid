package spotifyPackage.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
    Socket connection;
    ObjectOutputStream output;
    ObjectInputStream input;
    Request message;
    String ip;
    int port;

    public ConsumerTask(Context c) {
        context = c;
        ip = "10.0.2.2";
        port = 9999;
    }

    public ConsumerTask(Context c, String ip, int port) {
        context = c;
        this.ip = ip;
        this.port = port;
    }


    @Override
    protected Integer doInBackground(Object... args) {
        artist = (String) args[0];
        title = (String) args[1];
        path = (String) args[2] + File.separator;

        Consumer c = new Consumer(artist + "," + title, path);

        try {
            connection = new Socket(ip, port);

            getStreams();
            sendData(new Request("songPull", artist + "," + title));

            while (true) {
                synchronized (input) {
                    message = (Request) input.readObject();
                }
                if (message.getHeader().equals("newConnection")) {
                    sendData(new Request("newConnectionAck", ""));
                    closeConnection();

                    String[] tempArray = ((String) message.getData()).split(",", 2);
                    ip = tempArray[0];
                    if (ip.equals("127.0.0.1"))
                        ip = "10.0.2.2";
                    port = Integer.parseInt(tempArray[1]);
                    if (connection.isClosed()) {
                        //connection = new Socket(ip, port);
                        ConsumerTask ct = new ConsumerTask(context, ip, port);
                        ct.execute(artist, title, path);
                        System.out.println("After AsyncTask execute");
                        return 1;
                    }
                    getStreams();
                    sendData(new Request("songPull", artist + "," + title));
                } else if (message.getHeader().contains("musicData")) {
                    sendData(new Request("musicDataAck", ""));
                    c.saveChunks(message);
                    if (message.getHeader().contains("0")) break;
                } else if (message.getHeader().equals("artistUnavailable")) {
                    return -1;
                } else if (message.getHeader().equals("error")) {
                    return -1;
                }
            }
            Utilities.joinChunks(artist + "@" + title);
            closeConnection();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected void onProgressUpdate(Void... progress) {
    }

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
        } else if (result == -1) {
            Toast.makeText(context, "Error: Song not available!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void getStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
    }

    protected void sendData(Request message) {
        try {
            output.writeObject(message);
            output.flush();
        }
        catch (IOException ioException) {
            System.out.println("\nError writing object");
            closeConnection();
        }
    }

    protected void closeConnection() {
        System.out.println("Closing connection");
        try {
            output.close();
            input.close();
            connection.close();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
