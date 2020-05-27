package spotifyPackage.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import spotifyPackage.R;

public class DownloadActivity extends AppCompatActivity {

    private ListView songList;
    private String artistName = getIntent().getStringExtra("Artist_Name");
    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String songName = (String) songList.getItemAtPosition(position);
            //Toast.makeText(ArtistActivity.this, songName + " was selected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
            intent.putExtra("Song_Name", songName);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        songList = findViewById(R.id.songListView);
        //Toast.makeText(ArtistActivity.this, Utilities.findArtistSongs(artistName).get(1), Toast.LENGTH_SHORT).show();
        //ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Utilities.findArtistSongs(artistName));
        //songList.setAdapter(myAdapter);
        songList.setOnItemClickListener(listClick);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
}
