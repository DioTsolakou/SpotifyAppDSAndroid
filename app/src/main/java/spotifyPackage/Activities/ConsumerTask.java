package spotifyPackage.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

import spotifyPackage.Consumer;

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
        return (new Consumer(artist + "," + title, path)).run();
    }

    @Override
    protected void onProgressUpdate(Void... progress) {}

    @Override
    protected void onPostExecute(Integer result) {
        if (result == 0) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("Artist_Name", artist);
            intent.putExtra("Song_Name", title);
            intent.putExtra("Path", path);
            context.startActivity(intent);
            MainActivity hello = new MainActivity();
        } else {
            Toast.makeText(context, "Error: Song not available!", Toast.LENGTH_SHORT).show();
        }
    }
}
