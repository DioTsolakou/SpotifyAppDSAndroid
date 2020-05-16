package spotifyPackage.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import spotifyPackage.R;
import spotifyPackage.Utilities.Utilities;

public class ArtistActivity extends AppCompatActivity {

    private ListView songList;
    private String songName;
    private String artistName = getIntent().getStringExtra("Artist_Name");
    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            songName = (String) songList.getItemAtPosition(position);
            Toast.makeText(ArtistActivity.this, songName + " was selected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        songList = findViewById(R.id.songListView);

        Toast.makeText(ArtistActivity.this, Utilities.findArtistSongs(artistName).get(1), Toast.LENGTH_SHORT).show();

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        songList.setAdapter(myAdapter);
        songList.setOnItemClickListener(listClick);

        Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
        intent.putExtra("Song_Name", songName);
        startActivity(intent);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
}
