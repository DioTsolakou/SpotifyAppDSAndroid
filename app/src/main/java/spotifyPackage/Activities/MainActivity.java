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

public class MainActivity extends AppCompatActivity {

    private Button searchButton;
    private EditText songEditTxt;
    private EditText artistEditTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songEditTxt = findViewById(R.id.songEditText);
        artistEditTxt = findViewById(R.id.artistEditText);

        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == searchButton) {
                    String songName = songEditTxt.getText().toString();
                    String artistName = artistEditTxt.getText().toString();
                    if (songName.length() > 0 && artistName.length() > 0) {
                        Consumer c = new Consumer(artistName + "," + songName);
                        if (c.run() == 0) {
                            Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
                            intent.putExtra("Artist_Name", artistName);
                            intent.putExtra("Song_Name", songName);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Error: Song not available!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }


}