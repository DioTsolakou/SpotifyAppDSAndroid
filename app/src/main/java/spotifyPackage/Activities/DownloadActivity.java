package spotifyPackage.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import spotifyPackage.R;
import spotifyPackage.Utilities.Utilities;

import static spotifyPackage.Utilities.Utilities.downloadPath;
import static spotifyPackage.Utilities.Utilities.findDownloads;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView songList;
    private String[] songs;
    private Button backToSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        backToSearchButton = findViewById(R.id.back_to_search_button);
        backToSearchButton.setOnClickListener(this);

        songList = findViewById(R.id.songListView);
        songs = findDownloads();

        final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        songList.setAdapter(myAdapter);

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String song = myAdapter.getItem(position);
                if (song == null) return;
                if (song.equals("No songs in your library"))
                {
                    return;
                }
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

    @Override
    public void onClick(View v) {
        if (v == backToSearchButton)
        {
            Intent intent = new Intent(DownloadActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
