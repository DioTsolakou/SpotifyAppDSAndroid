package spotifyPackage.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import spotifyPackage.R;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
}
