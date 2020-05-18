package spotifyPackage.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import spotifyPackage.R;
import spotifyPackage.Utilities.Utilities;

public class PlayerActivity extends AppCompatActivity {

    private String songName = getIntent().getStringExtra("Song_Name");
    private ImageView songImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Utilities utilities = new Utilities();
        utilities.playChunks(songName);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
}
