package spotifyPackage.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import spotifyPackage.R;
import spotifyPackage.Utilities.Utilities;

import static spotifyPackage.Utilities.Utilities.downloadPath;
import static spotifyPackage.Utilities.Utilities.findDownloads;

public class DownloadActivity extends AppCompatActivity {

    private ListView songList;
    private String[] songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        songList = findViewById(R.id.songListView);
        songs = findDownloads();

        final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String song = myAdapter.getItem(position);
                if (song == null) return;
                String artist = song.substring(0, song.indexOf("-"));
                String title = song.substring(song.indexOf("-")+1);
                //Toast.makeText(DownloadActivity.this, songName + " was selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DownloadActivity.this, PlayerActivity.class);
                intent.putExtra("Artist_Name", artist);
                intent.putExtra("Song_Name", title);
                intent.putExtra("Path", downloadPath.getPath());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
