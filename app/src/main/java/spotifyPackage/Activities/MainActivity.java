package spotifyPackage.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import spotifyPackage.Consumer;
import spotifyPackage.R;
import spotifyPackage.Utilities.Utilities;

import static spotifyPackage.Utilities.Utilities.path;
import static spotifyPackage.Utilities.Utilities.streamingPath;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText artistEditTxt;
    private EditText songEditTxt;
    private Button searchButton;
    private Button libaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songEditTxt = findViewById(R.id.songEditText);
        artistEditTxt = findViewById(R.id.artistEditText);

        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        libaryButton = findViewById(R.id.libraryButton);
        libaryButton.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onClick(View v) {
        if (v == searchButton) {
            String artist = artistEditTxt.getText().toString();
            String title = songEditTxt.getText().toString();
            if (title.length() > 0 && artist.length() > 0) {
                Utilities.createStreamingDir(MainActivity.this);
                Consumer c = new Consumer(artist + "," + title, streamingPath.getPath());
                if (c.run() == 0) {
                    Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
                    intent.putExtra("Artist_Name", artist);
                    intent.putExtra("Song_Name", title);
                    intent.putExtra("Path", streamingPath.getPath());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Error: Song not available!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (v == libaryButton) {
            Intent intent = new Intent(this, DownloadActivity.class);
            startActivity(intent);
        }
    }
}